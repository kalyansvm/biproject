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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoFont.java 2343 2006-03-10 14:54:32Z lucian $
 * 
 * @hibernate.joined-subclass table="DataType"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoDataType extends RepoResource
{

	/**
	 *
	 */
	private byte type = DataType.TYPE_TEXT;
	private Integer maxLength = null;
	private Integer decimals = null;
	private String regularExpr = null;
	private Comparable minValue = null;
	private Comparable maxValue = null;
	private boolean isStrictMin = false;
	private boolean isStrictMax = false;


	/**
	 * @hibernate.property
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
	 * @hibernate.property
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
	 * @hibernate.property
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
	 * @hibernate.property
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
	 * @hibernate.property type="serializable"
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
	 * @hibernate.property type="serializable"
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
	 * @hibernate.property
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
	 * @hibernate.property
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

	/**
	 *
	 */
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) 
	{
		super.copyFrom(clientRes, referenceResolver);
		
		DataType dataType = (DataType) clientRes;
		
		setType(dataType.getType());
		setMaxLength(dataType.getMaxLength());
		setDecimals(dataType.getDecimals());
		setRegularExpr(dataType.getRegularExpr());
		setMinValue(dataType.getMinValue());
		setMaxValue(dataType.getMaxValue());
		setStrictMin(dataType.isStrictMin());
		setStrictMax(dataType.isStrictMax());
	}

	protected Class getClientItf() {
		return DataType.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory)
	{
		super.copyTo(clientRes, resourceFactory);

		DataType dataType = (DataType) clientRes;
		dataType.setType(getType());
		dataType.setMaxLength(getMaxLength());
		dataType.setDecimals(getDecimals());
		dataType.setRegularExpr(getRegularExpr());
		dataType.setMinValue(getMinValue());
		dataType.setMaxValue(getMaxValue());
		dataType.setStrictMin(isStrictMin());
		dataType.setStrictMax(isStrictMax());
	}

}
