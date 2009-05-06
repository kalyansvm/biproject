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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JdbcDataSourceService.java 12002 2008-02-04 13:20:22Z lucian $
 */
public class JdbcDataSourceService extends BaseJdbcDataSource {

	private static final Log log = LogFactory
			.getLog(JdbcDataSourceService.class);

	private final DataSource dataSource;
	private TimeZone timezone;

	public JdbcDataSourceService(DataSource dataSource, TimeZone timezone) {
		this.dataSource = dataSource;
		this.timezone = timezone;
	}

	protected Connection createConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			log.error("Error creating connection.", e);
			throw new JSException("jsexception.error.creating.connection", e);
		}
	}

	public void setReportParameterValues(Map parameterValues) {
		super.setReportParameterValues(parameterValues);
		//TODO implement as java.sql.Connection decoration?
		parameterValues.put(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE, timezone);
	}

	/**
	 * @return Returns the dataSource.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

}
