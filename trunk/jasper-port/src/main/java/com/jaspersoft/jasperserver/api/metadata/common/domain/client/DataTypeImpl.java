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

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: InputControlImpl.java 2332 2006-03-09 02:23:53Z tony $
 */
public class DataTypeImpl extends ResourceImpl implements DataType
{
	
	/**
	 * 
	 */
	private byte type = TYPE_TEXT;
	private Integer maxLength = null;
	private Integer decimals = null;
	private String regularExpr = null;
	private Comparable minValue = null;
	private Comparable maxValue = null;
	private boolean isStrictMin = false;
	private boolean isStrictMax = false;


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
	public Integer getMaxLength()
	{
		return maxLength;
	}

	/**
	 * 
	 */
	public void setMaxLength(Integer maxLength)
	{
		this.maxLength = maxLength;
	}

	/**
	 * 
	 */
	public Integer getDecimals()
	{
		return decimals;
	}

	/**
	 * 
	 */
	public void setDecimals(Integer decimals)
	{
		this.decimals = decimals;
	}

	/**
	 * 
	 */
	public String getRegularExpr()
	{
		return regularExpr;
	}

	/**
	 * 
	 */
	public void setRegularExpr(String regularExpr)
	{
		this.regularExpr = regularExpr;
	}

	/**
	 * 
	 */
	public Comparable getMinValue()
	{
		return minValue;
	}

	/**
	 * 
	 */
	public void setMinValue(Comparable min)
	{
		this.minValue = min;
	}

	/**
	 * 
	 */
	public Comparable getMaxValue()
	{
		return maxValue;
	}

	/**
	 * 
	 */
	public void setMaxValue(Comparable max)
	{
		this.maxValue = max;
	}

	/**
	 * 
	 */
	public boolean isStrictMin()
	{
		return isStrictMin;
	}

	/**
	 * 
	 */
	public void setStrictMin(boolean isStrictMin)
	{
		this.isStrictMin = isStrictMin;
	}

	/**
	 * 
	 */
	public boolean isStrictMax()
	{
		return isStrictMax;
	}

	/**
	 * 
	 */
	public void setStrictMax(boolean isStrictMax)
	{
		this.isStrictMax = isStrictMax;
	}

	protected Class getImplementingItf() {
		return DataType.class;
	}

}
