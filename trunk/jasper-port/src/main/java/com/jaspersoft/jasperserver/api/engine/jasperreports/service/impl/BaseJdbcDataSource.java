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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRParameter;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRTimezoneJdbcQueryExecuterFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseJdbcDataSource.java 8408 2007-05-29 23:29:12Z melih $
 */
public abstract class BaseJdbcDataSource implements ReportDataSourceService {


	private static final Log log = LogFactory.getLog(BaseJdbcDataSource.class);
	
	private Connection conn;
	

	public void setReportParameterValues(Map parameterValues) {
		conn = createConnection();
		parameterValues.put(JRParameter.REPORT_CONNECTION, conn);
	}

	public void closeConnection() {
		if (conn != null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("Error closing connection.", e);
				throw new JSExceptionWrapper(e);
			}

			conn = null;
		}
	}
	
	
	protected abstract Connection createConnection();
}
