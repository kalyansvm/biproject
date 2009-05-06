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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;

/**
 * @author tkavanagh
 * @version $Id: ListOfValuesItemBean.java 8408 2007-05-29 23:29:12Z melih $
 */

/*
 * This bean class represents ListOfValuesItem. 
 * 
 * ListOfValuesItem does not inherit from Resource, therefore this bean does not
 * extend ResourceBean.
 * 
 */
public class ListOfValuesItemBean {

	private String label;
	private Object value;
	
	public ListOfValuesItemBean() {
	}
	
	public ListOfValuesItemBean(ListOfValuesItem item) {
		this.label = item.getLabel();
		this.value = item.getValue();
	}
	
	public void copyTo(ListOfValuesItem item) {
		item.setLabel(getLabel());
		item.setValue(getValue());
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
}
