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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepoJdbcDataSource.java 8408 2007-05-29 23:29:12Z melih $
 * 
 * @hibernate.joined-subclass table="JdbcDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoJdbcDataSource extends RepoDataSource implements RepoReportDataSource {
	
	private String driverClass;
	private String connectionUrl;
	private String username;
	private String password;
	private String timezone;

	public RepoJdbcDataSource() {
	}

	/**
	 * @hibernate.property column="driver" type="string" length="100" not-null="true"
	 */
	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @hibernate.property column="password" type="string" length="100"
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @hibernate.property column="connectionUrl" type="string" length="200"
	 */
	public String getConnectionUrl() {
		return connectionUrl;
	}

	public void setConnectionUrl(String url) {
		this.connectionUrl = url;
	}

	/**
	 * @hibernate.property column="username" type="string" length="100"
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTimezone()
	{
		return timezone;
	}

	public void setTimezone(String timezone)
	{
		this.timezone = timezone;
	}

	protected Class getClientItf() {
		return JdbcReportDataSource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		JdbcReportDataSource ds = (JdbcReportDataSource) clientRes;
		ds.setDriverClass(getDriverClass());
		ds.setConnectionUrl(getConnectionUrl());
		ds.setUsername(getUsername());
		ds.setTimezone(getTimezone());
		ds.setPassword(PasswordCipherer.getInstance().decodePassword(getPassword()));
		
	}

	protected void copyFrom(Resource clientRes,
			ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		JdbcReportDataSource ds = (JdbcReportDataSource) clientRes;
		setDriverClass(ds.getDriverClass());
		setConnectionUrl(ds.getConnectionUrl());
		setUsername(ds.getUsername());
		setTimezone(ds.getTimezone());
		setPassword(PasswordCipherer.getInstance().encodePassword(ds.getPassword()));
		
	}
}
