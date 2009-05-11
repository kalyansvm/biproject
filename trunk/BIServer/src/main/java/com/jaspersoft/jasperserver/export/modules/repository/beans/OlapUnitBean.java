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

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: OlapUnitBean.java 12568 2008-03-19 15:57:56Z lucian $
 */
public class OlapUnitBean extends ResourceBean {
	
	public static final String DATA_PROVIDER_VIEW_OPTIONS = "olapUnitViewOptions";
	
	private String mdxQuery;
	private ResourceReferenceBean olapClientConnection;
	private String olapViewOptionsDataFile;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		OlapUnit unit = (OlapUnit) res;
		setMdxQuery(unit.getMdxQuery());
		setOlapClientConnection(exportHandler.handleReference(unit.getOlapClientConnection()));
		setOlapViewOptionsDataFile(exportHandler.handleData(unit, DATA_PROVIDER_VIEW_OPTIONS));
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		OlapUnit unit = (OlapUnit) res;
		unit.setMdxQuery(getMdxQuery());
		unit.setOlapClientConnection(importHandler.handleReference(getOlapClientConnection()));
		unit.setOlapViewOptions(constructViewOptions(importHandler));
	}

	protected Object constructViewOptions(ResourceImportHandler importHandler) {
		Object options;
		if (olapViewOptionsDataFile == null) {
			options = null;
		} else {
			byte[] viewOptionsData = importHandler.handleData(this, 
					olapViewOptionsDataFile, DATA_PROVIDER_VIEW_OPTIONS);
    	    XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(viewOptionsData));
    	    options = decoder.readObject();
     	    decoder.close();
		}
		return options;
	}

	public String getMdxQuery() {
		return mdxQuery;
	}
	
	public void setMdxQuery(String mdxQuery) {
		this.mdxQuery = mdxQuery;
	}
	
	public ResourceReferenceBean getOlapClientConnection() {
		return olapClientConnection;
	}
	
	public void setOlapClientConnection(ResourceReferenceBean olapClientConnection) {
		this.olapClientConnection = olapClientConnection;
	}

	public String getOlapViewOptionsDataFile() {
		return olapViewOptionsDataFile;
	}

	public void setOlapViewOptionsDataFile(String olapViewOptionsDataFile) {
		this.olapViewOptionsDataFile = olapViewOptionsDataFile;
	}
}
