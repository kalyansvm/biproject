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

import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author swood
 *
 */
public class JndiJdbcReportDataSourceServiceFactory implements ReportDataSourceServiceFactory {

	private static final Log log = LogFactory.getLog(JndiJdbcReportDataSourceServiceFactory.class);
	
	private Context ctx = null;
	
	public JndiJdbcReportDataSourceServiceFactory() {
		try {
			
			// Set the context here, as it is a heavyweight constructor
			
			ctx = new InitialContext();
		} catch (NamingException e) {
			log.error(e);
			throw new JSException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public ReportDataSourceService createService(ReportDataSource dataSource) {
		try {
			if (!(dataSource instanceof JndiJdbcReportDataSource)) {
				throw new JSException("jsexception.invalid.jndi.jdbc.datasource", new Object[] {dataSource.getClass()});
			}
			JndiJdbcReportDataSource jndiDataSource = (JndiJdbcReportDataSource) dataSource;
			
			String jndiName = jndiDataSource.getJndiName();
			
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
			TimeZone timeZone = jndiDataSource.getTimezone() != null ? TimeZone.getTimeZone(jndiDataSource.getTimezone()) : null;
			return new JdbcDataSourceService(ds, timeZone);
		} catch (NamingException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

}
