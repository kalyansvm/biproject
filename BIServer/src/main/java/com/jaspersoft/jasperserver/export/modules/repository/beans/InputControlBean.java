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

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceReferenceBean;

/**
 * @author tkavanagh
 * @version $Id: InputControlBean.java 8408 2007-05-29 23:29:12Z melih $
 */

public class InputControlBean extends ResourceBean {

	private byte type; 
	private boolean mandatory;
	private boolean readOnly;
	private boolean visible = true;
	private ResourceReferenceBean dataType;
	private ResourceReferenceBean listOfValues;
	private ResourceReferenceBean query;
	private String[] queryVisibleColumns;
	private String queryValueColumn;
	private Object defaultValue;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler export) {
		InputControl ic = (InputControl) res;
		setType(ic.getType());
		setMandatory(ic.isMandatory());
		setReadOnly(ic.isReadOnly());
		setVisible(ic.isVisible());
		setDataType(export.handleReference(ic.getDataType()));
		setListOfValues(export.handleReference(ic.getListOfValues()));
		setQuery(export.handleReference(ic.getQuery()));
		setQueryVisibleColumns(ic.getQueryVisibleColumns());
		setQueryValueColumn(ic.getQueryValueColumn());
		setDefaultValue(ic.getDefaultValue());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		InputControl ic = (InputControl) res;
		ic.setType(getType());
		ic.setMandatory(isMandatory());
		ic.setReadOnly(isReadOnly());
		ic.setVisible(isVisible());
		ic.setDataType(importHandler.handleReference(getDataType()));
		ic.setListOfValues(importHandler.handleReference(getListOfValues()));
		ic.setQuery(importHandler.handleReference(getQuery()));
		copyQueryColsTo(ic);
		ic.setQueryValueColumn(getQueryValueColumn());
		ic.setDefaultValue(getDefaultValue());
	}

	protected void copyQueryColsTo(InputControl ic) {
		if (queryVisibleColumns != null) {
			for (int i = 0; i < queryVisibleColumns.length; i++) {
				String column = queryVisibleColumns[i];
				ic.addQueryVisibleColumn(column);
			}
		}
	}

	public ResourceReferenceBean getDataType() {
		return dataType;
	}
	
	public void setDataType(ResourceReferenceBean dataType) {
		this.dataType = dataType;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public ResourceReferenceBean getListOfValues() {
		return listOfValues;
	}
	
	public void setListOfValues(ResourceReferenceBean listOfValues) {
		this.listOfValues = listOfValues;
	}
	
	public ResourceReferenceBean getQuery() {
		return query;
	}
	
	public void setQuery(ResourceReferenceBean query) {
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

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible)	{
		this.visible = visible;
	}
}
