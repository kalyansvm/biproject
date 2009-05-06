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

import java.util.Date;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ResourceImpl.java 12757 2008-03-31 16:58:16Z lucian $
 */
public abstract class ResourceImpl implements Resource
{
	private int version;
	private Date creationDate;
	private String name;
	private String label;
	private String description;
        private List attributes;
	private String folderUri;
	private transient String uri;

	private static String getParentFolderFromUri(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		int lastSeparator = s.lastIndexOf(Folder.SEPARATOR);
		
		if (lastSeparator <= 0) {
			return null;
		}
			
		return s.substring(0, lastSeparator);
	}

	private static String getNameFromUri(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		int lastSeparator = s.lastIndexOf(Folder.SEPARATOR);
		
		if (lastSeparator < 0 || lastSeparator == s.length() - 1) {
			return null;
		}
			
		return s.substring(lastSeparator + 1, s.length());
	}
	
	protected ResourceImpl() {
		version = VERSION_NEW;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		uri = null;
		this.name = name;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getURIString()
	{
		if (uri == null) {
			StringBuffer sb = new StringBuffer();
			if (getParentFolder() != null && !getParentFolder().equals(Folder.SEPARATOR))
				sb.append(getParentFolder());
			sb.append(Folder.SEPARATOR);
			if (!getName().equals(Folder.SEPARATOR))
				sb.append(getName());
			uri = sb.toString();
		}
		return uri;
	}

	public void setURIString(String uri)
	{
		this.uri = uri;
		this.name = getNameFromUri(uri);
		this.folderUri = getParentFolderFromUri(uri);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getPath()
	 */
	public String getPath() {
		return getURIString();
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getProtocol()
	 */
	public String getProtocol() {
		return Resource.URI_PROTOCOL;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getURI()
	 */
	public String getURI() {
		return getProtocol() + ":" + getPath();
	}
	
	public String getParentURI() {
		return getParentFolder() == null ? null : getProtocol() + ":" + getParentFolder();
	}
	
	public String getParentPath() {
		return getParentFolder() == null ? null : getParentFolder();
	}

	public String getParentFolder() {
		return folderUri;
	}

	public void setParentFolder(Folder folder) {
		this.uri = null;
		folderUri = (folder == null ? null : folder.getURIString());
	}

	public void setParentFolder(String folderURI) {
		this.uri = null;
		folderUri = folderURI;
	}

	public List getAttributes()
	{
       	        return attributes;
	}

        public void setAttributes(List attrs) 
        {
	    attributes = attrs;
        }


	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getResourceType() {
		return getImplementingItf().getName();
	}
	
	public boolean isSameType(Resource resource) {
		return getResourceType().equals(resource.getResourceType());
	}

	protected abstract Class getImplementingItf();

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isNew() {
		return version == VERSION_NEW;
	}
}
