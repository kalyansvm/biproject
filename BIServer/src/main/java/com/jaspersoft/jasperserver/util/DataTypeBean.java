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

/**
 * @author tkavanagh
 * @version $Id: DataTypeBean.java 8408 2007-05-29 23:29:12Z melih $
 */

public class DataTypeBean extends ResourceBean {

	private byte type;
	private Integer maxLength;
	private Integer decimals;
	private String regularExpr;
	private String minValue;		// orig type Comparable
	private String maxValue;		// orig type Comparable
	private boolean isStrictMin;
	private boolean isStrictMax;
	
	public Integer getDecimals() {
		return decimals;
	}
	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}
	public boolean getIsStrictMax() {
		return isStrictMax;
	}
	public void setIsStrictMax(boolean isStrictMax) {
		this.isStrictMax = isStrictMax;
	}
	public boolean getIsStrictMin() {
		return isStrictMin;
	}
	public void setIsStrictMin(boolean isStrictMin) {
		this.isStrictMin = isStrictMin;
	}
	public Integer getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public String getRegularExpr() {
		return regularExpr;
	}
	public void setRegularExpr(String regularExpr) {
		this.regularExpr = regularExpr;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	
}
