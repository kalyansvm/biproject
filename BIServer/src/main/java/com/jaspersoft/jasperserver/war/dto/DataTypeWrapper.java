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
package com.jaspersoft.jasperserver.war.dto;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

public class DataTypeWrapper extends BaseDTO
{

	private DataType dataType;
	private ResourceLookup[] allDataTypes;
	private String minValueText;
	private String maxValueText;
	
	public DataTypeWrapper(DataType dataType)
	{
		this.dataType = dataType;
	}


	public DataType getDataType()
	{
		return dataType;
	}

	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}

	public ResourceLookup[] getAllDataTypes()
	{
		return allDataTypes;
	}

	public void setAllDataTypes(ResourceLookup[] allDataTypes)
	{
		this.allDataTypes = allDataTypes;
	}


	/**
	 * @return Returns the minValueText.
	 */
	public String getMinValueText() {
		return minValueText;
	}


	/**
	 * @param minValueText The minValueText to set.
	 */
	public void setMinValueText(String minValueText) {
		this.minValueText = minValueText;
	}


	/**
	 * @return Returns the maxValueText.
	 */
	public String getMaxValueText() {
		return maxValueText;
	}


	/**
	 * @param maxValueText The maxValueText to set.
	 */
	public void setMaxValueText(String maxValueText) {
		this.maxValueText = maxValueText;
	}
}
