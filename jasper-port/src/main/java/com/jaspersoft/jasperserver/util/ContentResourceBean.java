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
 * @version $Id: ContentResourceBean.java 8408 2007-05-29 23:29:12Z melih $
 */


public class ContentResourceBean extends ResourceBean {

	private String fileType;
	private String referenceUri;
	private ContentResourceBean[] resources;
	
	/*
	 * Additional fields.
	 */
	
	/*
	 * Data is not stored in the bean (it's written to disk). 
	 * 
	 * hasData: keeps track of the status regarding data.
	 * (For instance, a "link" (isReference = true) would not have data.)
	 */
	private boolean hasData;
	
	/*
	 * linkTarget: is used to store the target of a link and thus simplify
	 * export-import processing.
	 */
	private ResourceBean linkTarget;
	
	
	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getReferenceUri() {
		return referenceUri;
	}

	public void setReferenceUri(String referenceUri) {
		this.referenceUri = referenceUri;
	}

	public ContentResourceBean[] getResources() {
		return resources;
	}

	public void setResources(ContentResourceBean[] resources) {
		this.resources = resources;
	}

	public boolean getHasData() {
		return hasData;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}
	
	public boolean getIsReference() {
		return referenceUri != null && referenceUri.length() > 0;
	}
	
	public ResourceBean getLinkTarget() {
		return linkTarget;
	}

	public void setLinkTarget(ResourceBean linkTarget) {
		this.linkTarget = linkTarget;
	}
	
}
