/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; and without the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt 
 * or write to:
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 */

package com.jaspersoft.jasperserver.api.metadata.view.domain;

/**
 * @author swood
 *
 */
public class FilterElementOr implements	FilterElement {

	private FilterElement leftHandSide;
	private FilterElement rightHandSide;
	/**
	 * 
	 */
	public FilterElementOr() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement#apply(com.jaspersoft.jasperserver.api.metadata.view.domain.Filter)
	 */
	public void apply(Filter filter) {
		filter.applyOr(getLeftHandSide(), getRightHandSide());

	}

	/**
	 * @return Returns the leftHandSide.
	 */
	public FilterElement getLeftHandSide() {
		return leftHandSide;
	}

	/**
	 * @param leftHandSide The leftHandSide to set.
	 */
	public void setLeftHandSide(FilterElement leftHandSide) {
		this.leftHandSide = leftHandSide;
	}

	/**
	 * @return Returns the rightHandSide.
	 */
	public FilterElement getRightHandSide() {
		return rightHandSide;
	}

	/**
	 * @param rightHandSide The rightHandSide to set.
	 */
	public void setRightHandSide(FilterElement rightHandSide) {
		this.rightHandSide = rightHandSide;
	}

}
