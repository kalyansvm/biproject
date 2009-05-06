/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation.  All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from JasperSoft,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FilterElementCollection.java 8408 2007-05-29 23:29:12Z melih $
 */
public abstract class FilterElementCollection {
	private final List criteria;
	
	protected FilterElementCollection() {
		criteria = new ArrayList();
	}
	
	/**
	 * 
	 * @return List of FilterElement
	 */
	public List getFilterElements() {
		return criteria;
	}
	
	public void addFilterElement(FilterElement element) {
		criteria.add(element);
	}

	public void addNegatedFilterElement(FilterElement element) {
		NegatedFilterElement negated = new NegatedFilterElement();
		negated.setElement(element);
		criteria.add(negated);
	}
	
	public FilterElementConjunction addConjunction() {
		FilterElementConjunction conjunction = new FilterElementConjunction();
		addFilterElement(conjunction);
		return conjunction;
	}
	
	public FilterElementDisjunction addDisjunction() {
		FilterElementDisjunction disjunction = new FilterElementDisjunction();
		addFilterElement(disjunction);
		return disjunction;
	}
	
	public FilterElementOr addOr() {
		FilterElementOr or = new FilterElementOr();
		addFilterElement(or);
		return or;
	}
}
