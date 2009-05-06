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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: JdbcDataSourceBean.java 8408 2007-05-29 23:29:12Z melih $
 */
public class JdbcDataSourceBean extends ResourceBean {

	private String driverClass;
	private String url;
	private String username;
	private String password;
	private String timezone;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		JdbcReportDataSource ds = (JdbcReportDataSource) res;
		setDriverClass(ds.getDriverClass());
		setConnectionUrl(ds.getConnectionUrl());
		setConnectionUsername(ds.getUsername());
		setConnectionPassword(ds.getPassword());
		setTimezone(ds.getTimezone());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		JdbcReportDataSource ds = (JdbcReportDataSource) res;
		ds.setDriverClass(getDriverClass());
		ds.setConnectionUrl(getConnectionUrl());
		ds.setUsername(getConnectionUsername());
		ds.setPassword(getConnectionPassword());
		ds.setTimezone(getTimezone());
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getConnectionUrl() {
		return url;
	}

	public void setConnectionUrl(String url) {
		this.url = url;
	}
	
	public String getConnectionUsername() {
		return username;
	}

	public void setConnectionUsername(String username) {
		this.username = username;
	}
	
	public String getConnectionPassword() {
		return password;
	}

	public void setConnectionPassword(String password) {
		this.password = password;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
