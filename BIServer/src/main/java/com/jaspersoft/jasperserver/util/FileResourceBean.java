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
 * @version $Id: FileResourceBean.java 8408 2007-05-29 23:29:12Z melih $
 */

/**
 * A FileResource typically holds data (unless it in a "link" (isReference = true)).
 * For these bean objects, they do not hold their own data. Instead,
 * the data is written to disk. The stringURI field inherited from ResourceBean
 * is uses to locate the data once it is time to import the bean objects 
 * back into JasperServer and recreate copies of the original java objects.
 * 
 */
public class FileResourceBean extends ResourceBean {

	/*
	 * The following come from the FileResource interface 
	 */
	private String fileType;
	private boolean hasData;
	private boolean isReference;
	private String referenceUri;
	
	/*
	 * The following is added to simplify export-import processing
	 */
	private ResourceBean linkTarget;	// if this bean points to a target resource, the
										// target is stored here


	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public boolean getHasData() {
		return hasData;
	}
	
	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}
	
	public boolean getIsReference() {
		return isReference;
	}
	
	public void setIsReference(boolean isReference) {
		this.isReference = isReference;
	}
	
	public String getReferenceUri() {
		return referenceUri;
	}
	
	public void setReferenceUri(String referenceUri) {
		this.referenceUri = referenceUri;
	}
	
	public ResourceBean getLinkTarget() {
		return linkTarget;
	}

	public void setLinkTarget(ResourceBean linkTarget) {
		this.linkTarget = linkTarget;
	}
}
