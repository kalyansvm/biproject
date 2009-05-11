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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: QueryBean.java 8408 2007-05-29 23:29:12Z melih $
 */

public class QueryBean extends ResourceBean {

	private String language;
	private String queryString;
	private ResourceReferenceBean dataSource;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		Query query = (Query) res;
		setLanguage(query.getLanguage());
		setQueryString(query.getSql());
		setDataSource(referenceHandler.handleReference(query.getDataSource()));
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		Query query = (Query) res;
		query.setLanguage(getLanguage());
		query.setSql(getQueryString());
		query.setDataSource(importHandler.handleReference(getDataSource()));
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String sql) {
		this.queryString = sql;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public ResourceReferenceBean getDataSource() {
		return dataSource;
	}

	public void setDataSource(ResourceReferenceBean dataSource) {
		this.dataSource = dataSource;
	}
	
}
