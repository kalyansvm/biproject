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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: XmlaConnectionBean.java 8408 2007-05-29 23:29:12Z melih $
 */


public class XmlaConnectionBean extends ResourceBean {

	private String uri;
	private String dataSource;
	private String catalog;
	private String username;
	private String password;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		XMLAConnection xmla = (XMLAConnection) res;
		setUri(xmla.getURI());
		setDataSource(xmla.getDataSource());
		setCatalog(xmla.getCatalog());
		setUsername(xmla.getUsername());
		setPassword(xmla.getPassword());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		XMLAConnection xmla = (XMLAConnection) res;
		xmla.setURI(getUri());
		xmla.setDataSource(getDataSource());
		xmla.setCatalog(getCatalog());
		xmla.setUsername(getUsername());
		xmla.setPassword(getPassword());
	}

	public String getCatalog() {
		return catalog;
	}
	
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	public String getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
