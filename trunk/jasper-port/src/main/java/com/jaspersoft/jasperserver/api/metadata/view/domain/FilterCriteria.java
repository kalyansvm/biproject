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



/**
 * @author swood
 * @version $Id: FilterCriteria.java 8408 2007-05-29 23:29:12Z melih $
 */
public class FilterCriteria extends FilterElementCollection
{
	public static ParentFolderFilter createParentFolderFilter(String folderURI) {
		ParentFolderFilter filter = new ParentFolderFilter();
		filter.setFolderURI(folderURI);
		return filter;
	}	

	public static PropertyFilter createPropertyEqualsFilter(String property, Object value) {
		return createPropertyFilter(property, value, PropertyFilter.EQ);
	}

	public static PropertyFilter createPropertyLikeFilter(String propery, Object value) {
		return createPropertyFilter(propery, value, PropertyFilter.LIKE);
	}

	public static PropertyFilter createPropertyGreaterThanFilter(String propery, Object value) {
		return createPropertyFilter(propery, value, PropertyFilter.GT);
	}
	
	public static PropertyFilter createPropertyLessThanFilter(String propery, Object value) {
		return createPropertyFilter(propery, value, PropertyFilter.LT);
	}
	
	public static PropertyFilter createPropertyBetweenFilter(String propery, Object loValue, Object hiValue) {
		return createPropertyFilter(propery, loValue, hiValue, PropertyFilter.BETWEEN);
	}
	
	protected static PropertyFilter createPropertyFilter(String property, Object value, byte op) {
		PropertyFilter filter = new PropertyFilter();
		filter.setOp(op);
		filter.setProperty(property);
		filter.setValue(value);
		return filter;
	}

	protected static PropertyFilter createPropertyFilter(String property, Object loValue, Object hiValue, byte op) {
		PropertyFilter filter = new PropertyFilter();
		filter.setOp(op);
		filter.setProperty(property);
		filter.setLowValue(loValue);
		filter.setHighValue(hiValue);
		return filter;
	}
	
	public static ReferenceFilter createReferenceFilter(String property, Class referenceType, String referredURI) {
		ReferenceFilter filter = new ReferenceFilter();
		filter.setProperty(property);
		filter.setReferenceClass(referenceType);
		filter.setReferredURI(referredURI);
		return filter;
	}
	
	public static URIFilter createResourceURIFilter(String uri) {
		URIFilter filter = new URIFilter();
		filter.setURI(uri);
		return filter;
	}	
	
	private Class _class;

	public static FilterCriteria createFilter() {
		return new FilterCriteria();
	}

	public static FilterCriteria createFilter(Class _class) {
		return new FilterCriteria(_class);
	}
	
	public FilterCriteria () {
		this(null);
	}
	
	public FilterCriteria (Class _class) {
		super();
		this._class = _class;
	}

	public Class getFilterClass() {
		return _class;
	}

	public void setFilterClass(Class _class) {
		this._class = _class;
	}
}
