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
package com.jaspersoft.jasperserver.util;

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;

/**
 * @author tkavanagh
 * @version $Id: InputControlBean.java 8408 2007-05-29 23:29:12Z melih $
 */

public class InputControlBean extends ResourceBean {

	private byte type; 
	private boolean isMandatory;
	private boolean isReadOnly;
	private DataTypeBean dataType;
	private ListOfValuesBean listOfValues;
	private QueryBean query;
	private String[] queryVisibleColumns;
	private String queryValueColumn;
	private String defaultValue;			// orig type Object
	private String[] defaultValues;			// orig type List
	
	public DataTypeBean getDataType() {
		return dataType;
	}
	
	public void setDataType(DataTypeBean dataType) {
		this.dataType = dataType;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String[] getDefaultValues() {
		return defaultValues;
	}
	
	public void setDefaultValues(String[] defaultValues) {
		this.defaultValues = defaultValues;
	}
	
	public boolean getIsMandatory() {
		return isMandatory;
	}
	
	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public boolean getIsReadOnly() {
		return isReadOnly;
	}
	
	public void setIsReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	
	public ListOfValuesBean getListOfValues() {
		return listOfValues;
	}
	
	public void setListOfValues(ListOfValuesBean listOfValues) {
		this.listOfValues = listOfValues;
	}
	
	public QueryBean getQuery() {
		return query;
	}
	
	public void setQuery(QueryBean query) {
		this.query = query;
	}
	
	public String getQueryValueColumn() {
		return queryValueColumn;
	}
	
	public void setQueryValueColumn(String queryValueColumn) {
		this.queryValueColumn = queryValueColumn;
	}
	
	public String[] getQueryVisibleColumns() {
		return queryVisibleColumns;
	}
	
	public void setQueryVisibleColumns(String[] queryVisibleColumns) {
		this.queryVisibleColumns = queryVisibleColumns;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
}
