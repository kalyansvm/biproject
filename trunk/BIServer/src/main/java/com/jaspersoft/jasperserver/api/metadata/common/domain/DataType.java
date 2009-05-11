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


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: InputControl.java 2332 2006-03-09 02:23:53Z tony $
 */
public interface DataType extends Resource
{

	/**
	 * 
	 */
	public static final byte TYPE_TEXT = 1;
	public static final byte TYPE_NUMBER = 2;
	public static final byte TYPE_DATE = 3;
	public static final byte TYPE_DATE_TIME = 4;


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
	public Integer getMaxLength();

	/**
	 * 
	 */
	public void setMaxLength(Integer maxLength);

	/**
	 * 
	 */
	public Integer getDecimals();

	/**
	 * 
	 */
	public void setDecimals(Integer decimals);

	/**
	 * 
	 */
	public String getRegularExpr();

	/**
	 * 
	 */
	public void setRegularExpr(String regExp);

	/**
	 * 
	 */
	public Comparable getMinValue();

	/**
	 * 
	 */
	public void setMinValue(Comparable minValue);

	/**
	 * 
	 */
	public Comparable getMaxValue();

	/**
	 * 
	 */
	public void setMaxValue(Comparable maxValue);

	/**
	 * 
	 */
	public boolean isStrictMin();

	/**
	 * 
	 */
	public void setStrictMin(boolean isStrictMin);

	/**
	 * 
	 */
	public boolean isStrictMax();

	/**
	 * 
	 */
	public void setStrictMax(boolean isStrictMax);

}
