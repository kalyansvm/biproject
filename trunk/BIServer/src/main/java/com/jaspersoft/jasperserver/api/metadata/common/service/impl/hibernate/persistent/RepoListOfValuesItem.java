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



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoQuery.java 2343 2006-03-10 14:54:32Z lucian $
 * 
 * @hibernate.joined-subclass table="ListOfValuesItem"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoListOfValuesItem
{
	/**
	 *
	 */
	private long id;
	private String label;
	private Object value;

	/**
	 * @hibernate.id generator-class="identity"
	 */
	public long getId() 
	{
		return id;
	}

	/**
	 *
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * @hibernate.property
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 *
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @hibernate.property type="serializable"
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

}
