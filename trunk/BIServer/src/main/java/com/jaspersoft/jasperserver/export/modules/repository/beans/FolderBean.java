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

import java.util.Date;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

/**
 * @author tkavanagh
 * @version $Id: FolderBean.java 8408 2007-05-29 23:29:12Z melih $
 */

public class FolderBean {

	/*
	 * The following come from the Resource interface 
	 */
	private String name;
	private String label;
	private String description;
	private String parent;
	private Date creationDate;
	private String[] subFolders;
	private String[] resources;
	private RepositoryObjectPermissionBean[] permissions;
	
	public void copyFrom(Folder folder) {
		setName(folder.getName());
		setLabel(folder.getLabel());
		setDescription(folder.getDescription());
		setParent(folder.getParentFolder());
		setCreationDate(folder.getCreationDate());
	}
	
	public void copyTo(Folder folder) {
		folder.setName(getName());
		folder.setLabel(getLabel());
		folder.setDescription(getDescription());
		folder.setParentFolder(getParent());
	}
	
	/*
	 * getters and setters
	 */
	
	public String getName()	{
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String folderUri) {
		this.parent = folderUri;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String[] getResources() {
		return resources;
	}

	public void setResources(String[] resources) {
		this.resources = resources;
	}

	public String[] getSubFolders() {
		return subFolders;
	}

	public void setSubFolders(String[] subFolders) {
		this.subFolders = subFolders;
	}

	public RepositoryObjectPermissionBean[] getPermissions() {
		return permissions;
	}

	public void setPermissions(RepositoryObjectPermissionBean[] permissions) {
		this.permissions = permissions;
	}

}
