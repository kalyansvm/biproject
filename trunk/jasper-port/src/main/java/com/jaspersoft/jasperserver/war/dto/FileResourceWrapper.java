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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;

public class FileResourceWrapper extends BaseDTO implements Serializable {
	private FileResource fileResource;

	private Resource resource;

	private byte[] newData;

	private String newUri;

	private Map allTypes;

	private boolean suggested;

	private List allResources;
	
	private List existingResources;

	private String source;

	private String fileName;

	private Object parentFlowObject;

	private boolean located;
	
	private List allFolders;
	
	private String folder;

	public boolean isLocated() {
		return located;
	}

	public void setLocated(boolean located) {
		this.located = located;
	}

	public Object getParentFlowObject() {
		return parentFlowObject;
	}

	public void setParentFlowObject(Object parentFlowObject) {
		this.parentFlowObject = parentFlowObject;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List getAllResources() {
		return allResources;
	}

	public void setAllResources(List allResources) {
		this.allResources = allResources;
	}

	public boolean isSuggested() {
		return suggested;
	}

	public void setSuggested(boolean suggested) {
		this.suggested = suggested;
	}

	public Map getAllTypes() {
		if (allTypes == null) {
			allTypes = new LinkedHashMap();
			allTypes.put(FileResource.TYPE_IMAGE,
					JasperServerConst.TYPE_RSRC_IMAGE);
			allTypes.put(FileResource.TYPE_FONT,
					JasperServerConst.TYPE_RSRC_FONT);
			allTypes.put(FileResource.TYPE_JAR,
					JasperServerConst.TYPE_RSRC_CLASS_JAR);
			allTypes.put(FileResource.TYPE_JRXML,
					JasperServerConst.TYPE_RSRC_SUB_REPORT);
			allTypes.put(FileResource.TYPE_RESOURCE_BUNDLE,
					JasperServerConst.TYPE_RSRC_RESOURCE_BUNDLE);
			allTypes.put(FileResource.TYPE_STYLE_TEMPLATE,
					JasperServerConst.TYPE_RSRC_STYLE_TEMPLATE);
			allTypes.put(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA,
					JasperServerConst.TYPE_RSRC_OLAP_SCHEMA);
			allTypes.put(ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA,
						 JasperServerConst.TYPE_RSRC_ACCESS_GRANT_SCHEMA); // pro-only
		}
		return allTypes;
	}

	public void setAllTypes(Map allTypes) {
		this.allTypes = allTypes;
	}

	public FileResource getFileResource() {
		return fileResource;
	}

	public void setFileResource(FileResource fileResource) {
		this.fileResource = fileResource;
	}

	public void afterBind() {
		if (getSource() != null
				&& fileResource != null
				&& getSource().equals(
						JasperServerConst.FIELD_CHOICE_FILE_SYSTEM)) {
			fileResource.setReferenceURI(null);
		}
	}

	public static String getTypeForExtention(String extension) {
		String type = null;
		if (extension != null) {
			if (extension.equalsIgnoreCase(FileResource.TYPE_JRXML))
				type = FileResource.TYPE_JRXML;
			else if (extension.equalsIgnoreCase("ttf"))
				type = FileResource.TYPE_FONT;
			else if (extension.equalsIgnoreCase("xml"))
				type = ResourceDescriptor.TYPE_MONDRIAN_SCHEMA;
			else if (extension.equalsIgnoreCase("agxml"))
				type = ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA; // pro-only
			else if (extension.equalsIgnoreCase(FileResource.TYPE_JAR)) {
				type = FileResource.TYPE_JAR;
			} else if (extension.indexOf(FileResource.TYPE_RESOURCE_BUNDLE) != -1) {
				type = FileResource.TYPE_RESOURCE_BUNDLE;
			} else if (extension.equalsIgnoreCase(FileResource.TYPE_STYLE_TEMPLATE)) {
				type = FileResource.TYPE_STYLE_TEMPLATE;
			} else {
				String[] imageTypes = { "jpg", "jpeg", "gif", "bmp" };
				for (int i = 0; i < imageTypes.length; i++) {
					if (extension.equalsIgnoreCase(imageTypes[i])) {
						type = FileResource.TYPE_IMAGE;
					}
				}
			}
		}
		return type;
	}

	public byte[] getNewData() {
		return newData;
	}

	public void setNewData(byte[] newData) {
		if (newData != null && newData.length != 0) {
			fileResource.setData(newData);
		}
		this.newData = newData;
	}

	public String getNewUri() {
		return newUri;
	}

	public void setNewUri(String newUri) {
		if (newUri != null && newUri.trim().length() != 0)
			fileResource.setReferenceURI(newUri);
		this.newUri = newUri;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public List getExistingResources() {
		return existingResources;
	}

	public void setExistingResources(List existingResources) {
		this.existingResources = existingResources;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public List getAllFolders() {
		return allFolders;
	}

	public void setAllFolders(List allFolders) {
		this.allFolders = allFolders;
	}
}
