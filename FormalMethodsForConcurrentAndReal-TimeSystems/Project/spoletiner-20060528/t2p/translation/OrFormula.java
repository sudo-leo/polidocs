/*
 spoletiner - a TRIO to Promela translator
 Copyright (C) 2006 Domenico Bianculli (bianculli@gmail.com) & Paola Spoletini (spoleti@elet.polimi.it)

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 */
package t2p.translation;

import t2p.translation.TranslationUtilities.TemporalQuality;

/*******************************************************************************
 * A (sub)formula, not root, having OR as operator.
 * 
 * @author bianculli
 * 
 */
public class OrFormula extends TrioFormula {
	/**
	 * Constructs an OR formula given its children
	 * 
	 * @param tf1
	 *            the left child
	 * @param tf2
	 *            the right child
	 */
	public OrFormula(TrioFormula tf1, TrioFormula tf2) {
		super(tf1, tf2);
		this.temporalQuality = TranslationUtilities.computeTemporalQuality(tf1
				.getTemporalQuality(), tf2.getTemporalQuality());
		this.logicExpression = tf1.logicExpression + " || "
				+ tf2.logicExpression;
	}

	public String getExternalBodyCode() {
		StringBuilder s = new StringBuilder();
		TrioFormula left = this.children.get(0);
		TrioFormula right = this.children.get(1);
		// formula future || future
		if (left.getTemporalQuality() == TemporalQuality.future
				&& right.getTemporalQuality() == TemporalQuality.future) {
			s.append("\n\t\t\tif");
			s
					.append("\n\t\t\t\t::\n\t\t\t\t\n/* i=0; /* enable this line for interactive simulation */");
			s.append(left.getExternalBodyCode());
			s
					.append("\n\t\t\t\t::\n\t\t\t\t/* i=1; /* enable this line for interactive simulation */\n");
			s.append(right.getExternalBodyCode());
			s.append("\n\t\t\tfi;\n");

		} else
		// formula past || future
		if (right.getTemporalQuality() == TemporalQuality.future) {
			s.append(this.pastAndFutureCode(left, right));
		}
		// formula future || Past
		else if (left.getTemporalQuality() == TemporalQuality.future) {
			s.append(this.pastAndFutureCode(right, left));
		}
		// past /present
		else {
			s.append("\n\t\t\tif");
			s.append("\n\t\t\t\t::!(" + left.getLogicExpression() + " || "
					+ right.getLogicExpression() + ")-> s=0; goto gen;\n");
			s.append("\n\t\t\t\t:: (" + left.getLogicExpression() + " || "
					+ right.getLogicExpression() + ")-> skip;\n");
			s.append("\n\t\t\tfi;");
		}
		return s.toString();
	}

	private String pastAndFutureCode(TrioFormula past, TrioFormula futr) {
		StringBuilder s = new StringBuilder();
		s.append("\n\t\t\tif");
		s.append("\n\t\t\t\t::!(" + past.getLogicExpression() + ")->\n");
		s.append(futr.getExternalBodyCode());
		s.append("\n\t\t\t\t::(" + past.getLogicExpression() + ")->skip;\n");
		s.append("\n\t\t\tfi;");
		return s.toString();
	}

}
