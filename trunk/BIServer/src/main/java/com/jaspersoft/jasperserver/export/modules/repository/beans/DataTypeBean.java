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

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: DataTypeBean.java 8408 2007-05-29 23:29:12Z melih $
 */

public class DataTypeBean extends ResourceBean {

	private byte type;
	private Integer maxLength;
	private Integer decimals;
	private String regularExpr;
	private Object minValue;
	private Object maxValue;
	private boolean strictMin;
	private boolean strictMax;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		DataType dt = (DataType) res;
		setType(dt.getType());
		setMaxLength(dt.getMaxLength());
		setDecimals(dt.getDecimals());
		setRegularExpr(dt.getRegularExpr());
		setMinValue(dt.getMinValue());
		setMaxValue(dt.getMaxValue());
		setStrictMin(dt.isStrictMin());
		setStrictMax(dt.isStrictMax());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		DataType dt = (DataType) res;
		dt.setType(getType());
		dt.setMaxLength(getMaxLength());
		dt.setDecimals(getDecimals());
		dt.setRegularExpr(getRegularExpr());
		dt.setMinValue((Comparable) getMinValue());
		dt.setMaxValue((Comparable) getMaxValue());
		dt.setStrictMin(isStrictMin());
		dt.setStrictMax(isStrictMax());
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Object getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Object maxValue) {
		this.maxValue = maxValue;
	}

	public Object getMinValue() {
		return minValue;
	}

	public void setMinValue(Object minValue) {
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

	public boolean isStrictMax() {
		return strictMax;
	}

	public void setStrictMax(boolean strictMax) {
		this.strictMax = strictMax;
	}

	public boolean isStrictMin() {
		return strictMin;
	}

	public void setStrictMin(boolean strictMin) {
		this.strictMin = strictMin;
	}

}
