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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.util.List;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: InputControl.java 8408 2007-05-29 23:29:12Z melih $
 */
public interface InputControl extends Resource
{

	/**
	 * 
	 */
	public static final byte TYPE_BOOLEAN = 1;
	public static final byte TYPE_SINGLE_VALUE = 2;
	public static final byte TYPE_SINGLE_SELECT_LIST_OF_VALUES = 3;
	public static final byte TYPE_SINGLE_SELECT_QUERY = 4;
	public static final byte TYPE_MULTI_VALUE = 5;
	public static final byte TYPE_MULTI_SELECT_LIST_OF_VALUES = 6;
	public static final byte TYPE_MULTI_SELECT_QUERY = 7;
	public static final byte TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO = 8;
	public static final byte TYPE_SINGLE_SELECT_QUERY_RADIO = 9;
	public static final byte TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX = 10;
	public static final byte TYPE_MULTI_SELECT_QUERY_CHECKBOX = 11;


	/**
	 * 
	 */
	public byte getType();

	/**
	 * 
	 */
	public void setType(byte type);

	/**
	 * 
	 */
	public boolean isMandatory();

	/**
	 * 
	 */
	public void setMandatory(boolean isMandatory);

	/**
	 * 
	 */
	public boolean isReadOnly();

	/**
	 * 
	 */
	public void setReadOnly(boolean isReadOnly);

	/**
	 *
	 */
	public boolean isVisible();

	/**
	 *
	 */
	public void setVisible(boolean isVisible);

	/**
	 * Returns the reference to the
	 * {@link DataType data type}
	 * of this input control.
	 * 
	 * @return a reference to the data type used by this input control
	 */
	public ResourceReference getDataType();

	/**
	 * 
	 */
	public void setDataType(ResourceReference dataTypeReference);
	
	public void setDataType(DataType dataType);
	
	public void setDataTypeReference(String referenceURI);

	/**
	 * Returns the reference to the
	 * {@link ListOfValues list of values}
	 * used by this input control.
	 * 
	 * @return a reference to the list of values used by this input control
	 */
	public ResourceReference getListOfValues();

	/**
	 * 
	 */
	public void setListOfValues(ResourceReference listOfValuesReference);
	
	public void setListOfValues(ListOfValues listOfValues);
	
	public void setListOfValuesReference(String referenceURI);

	/**
	 * Returns the reference to the
	 * {@link Query query}
	 * used by this input control.
	 * 
	 * @return a reference to the query used by this input control
	 */
	public ResourceReference getQuery();

	/**
	 * 
	 */
	public void setQuery(ResourceReference query);

	public void setQuery(Query query);

	public void setQueryReference(String referenceURI);

	/**
	 * 
	 */
	public String[] getQueryVisibleColumns();

	/**
	 * 
	 */
	public void addQueryVisibleColumn(String column);

	/**
	 * 
	 */
	public void removeQueryVisibleColumn(String column);

	/**
	 * 
	 */
	public String getQueryValueColumn();

	/**
	 * 
	 */
	public void setQueryValueColumn(String column);

	/**
	 * 
	 */
	public Object getDefaultValue();

	/**
	 * 
	 */
	public void setDefaultValue(Object value);

	/**
	 * 
	 */
	public List getDefaultValues();

	/**
	 * 
	 */
	public void setDefaultValues(List values);

	/**
	 *
	 */
	public List getQueryVisibleColumnsAsList();
}
