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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: MondrianXmlaDefinitionBean.java 8408 2007-05-29 23:29:12Z melih $
 */
public class MondrianXmlaDefinitionBean extends ResourceBean {

	private String catalog;
	private ResourceReferenceBean mondrianConnection;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		MondrianXMLADefinition def = (MondrianXMLADefinition) res;
		setCatalog(def.getCatalog());
		setMondrianConnection(exportHandler.handleReference(def.getMondrianConnection()));
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		MondrianXMLADefinition def = (MondrianXMLADefinition) res;
		def.setCatalog(getCatalog());
		def.setMondrianConnection(importHandler.handleReference(getMondrianConnection()));
	}

	public String getCatalog() {
		return catalog;
	}
	
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	public ResourceReferenceBean getMondrianConnection() {
		return mondrianConnection;
	}
	
	public void setMondrianConnection(ResourceReferenceBean mondrianConnection) {
		this.mondrianConnection = mondrianConnection;
	}
}