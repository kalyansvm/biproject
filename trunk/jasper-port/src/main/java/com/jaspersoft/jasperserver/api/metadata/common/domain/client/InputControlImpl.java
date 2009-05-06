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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: InputControlImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class InputControlImpl extends ResourceImpl implements InputControl
{
	
	/**
	 * 
	 */
	private byte type = TYPE_SINGLE_VALUE;
	private boolean isMandatory = false;
	private boolean isReadOnly = false;
	private boolean isVisible= true;
	private ResourceReference dataType = null;
	private ResourceReference listOfValues = null;
	private ResourceReference query = null;
	private List queryVisibleColumns = new ArrayList();
	private String queryValueColumn = null;
	private Object defaultValue = null;
	private List defaultValues = null;


	/**
	 * 
	 */
	public byte getType()
	{
		return type;
	}

	/**
	 * 
	 */
	public void setType(byte type)
	{
		this.type = type;
	}

	/**
	 * 
	 */
	public boolean isMandatory()
	{
		return isMandatory;
	}

	/**
	 * 
	 */
	public void setMandatory(boolean isMandatory)
	{
		this.isMandatory = isMandatory;
	}

	/**
	 * 
	 */
	public boolean isReadOnly()
	{
		return isReadOnly;
	}

	/**
	 * 
	 */
	public void setReadOnly(boolean isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}

	/**
	 *
	 */
	public boolean isVisible()
	{
		return isVisible;
	}

	/**
	 *
	 */
	public void setVisible(boolean visible)
	{
		isVisible = visible;
	}

	/**
	 * 
	 */
	public ResourceReference getDataType()
	{
		return dataType;
	}

	/**
	 * 
	 */
	public void setDataType(ResourceReference dataType)
	{
		this.dataType = dataType;
	}

	public void setDataType(DataType dataType) {
		setDataType(new ResourceReference(dataType));
	}

	public void setDataTypeReference(String referenceURI) {
		setDataType(new ResourceReference(referenceURI));
	}

	/**
	 * 
	 */
	public ResourceReference getListOfValues()
	{
		return listOfValues;
	}

	/**
	 * 
	 */
	public void setListOfValues(ResourceReference values)
	{
		this.listOfValues = values;
	}

	public void setListOfValues(ListOfValues listOfValues) {
		setListOfValues(new ResourceReference(listOfValues));		
	}

	public void setListOfValuesReference(String referenceURI) {
		setListOfValues(new ResourceReference(referenceURI));		
	}

	/**
	 * 
	 */
	public ResourceReference getQuery()
	{
		return query;
	}

	/**
	 * 
	 */
	public void setQuery(ResourceReference query)
	{
		this.query = query;
	}

	public void setQuery(Query query) {
		setQuery(new ResourceReference(query));
	}

	public void setQueryReference(String referenceURI) {
		setQuery(new ResourceReference(referenceURI));
	}

	/**
	 * 
	 */
	public String[] getQueryVisibleColumns()
	{
		return (String[]) queryVisibleColumns.toArray(new String[queryVisibleColumns.size()]);
	}

	/**
	 *
	 */
	public List getQueryVisibleColumnsAsList()
	{
		return queryVisibleColumns;
	}

	/**
	 * 
	 */
	public void addQueryVisibleColumn(String column)
	{
		queryVisibleColumns.add(column);
	}

	/**
	 * 
	 */
	public void removeQueryVisibleColumn(String column)
	{
		queryVisibleColumns.remove(column);
	}

	/**
	 * 
	 */
	public String getQueryValueColumn()
	{
		return queryValueColumn;
	}

	/**
	 * 
	 */
	public void setQueryValueColumn(String column)
	{
		this.queryValueColumn = column;
	}

	/**
	 * 
	 */
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * 
	 */
	public void setDefaultValue(Object value)
	{
		this.defaultValue = value;
	}

	/**
	 * 
	 */
	public List getDefaultValues()
	{
		return defaultValues;
	}

	/**
	 * 
	 */
	public void setDefaultValues(List values)
	{
		this.defaultValues = values;
	}

	protected Class getImplementingItf() {
		return InputControl.class;
	}

}
