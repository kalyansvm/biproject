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

import java.io.Serializable;
import java.util.Date;

import com.jaspersoft.jasperserver.api.common.domain.AttributedObject;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: Resource.java 12757 2008-03-31 16:58:16Z lucian $
 */
public interface Resource extends AttributedObject, InternalURI, Serializable
{
	public static String URI_PROTOCOL = "repo";
	
	int VERSION_NEW = -1;
	
	public int getVersion();
	
	public void setVersion(int version);
	
	public String getURIString();

	public void setURIString(String uri);

	public String getParentFolder();

	public void setParentFolder(String uri);

	public void setParentFolder(Folder folder);

	/**
	 *
	 */
	public String getName();
	
	public void setName(String name);

	/**
	 *
	 */
	public String getLabel();
	
	public void setLabel(String label);

	/**
	 *
	 */
	public String getDescription();
	
	public void setDescription(String description);
	
	
	public String getResourceType();
	
	public boolean isSameType(Resource resource);
	
	public Date getCreationDate();
	
	public void setCreationDate(Date timestamp);

	public boolean isNew();
}
