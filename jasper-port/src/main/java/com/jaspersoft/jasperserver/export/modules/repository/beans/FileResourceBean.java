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

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

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

	public static final String DATA_PROVIDER_ID = "fileResourceData";
	
	/*
	 * The following come from the FileResource interface 
	 */
	private String fileType;
	private String dataFile;
	private String referenceUri;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		FileResource fileRes = (FileResource) res;
		setFileType(fileRes.getFileType());
		
		setDataFile(exportHandler.handleData(res, DATA_PROVIDER_ID));
		
		String reference = fileRes.getReferenceURI();
		setReferenceUri(reference);
		if (reference != null) {
			exportHandler.queueResource(reference);
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		FileResource fileRes = (FileResource) res;
		fileRes.setFileType(getFileType());

		if (dataFile != null) {
			fileRes.setData(importHandler.handleData(this, dataFile, DATA_PROVIDER_ID));
		}

		if (referenceUri != null) {
			fileRes.setReferenceURI(importHandler.handleResource(referenceUri));
		}
	}

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

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}
}
