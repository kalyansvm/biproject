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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import java.util.Map;

import net.sf.jasperreports.olap.JRMondrianQueryExecuterFactory;

import mondrian.olap.Connection;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: MondrianConnectionDataSourceService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class MondrianConnectionDataSourceService implements ReportDataSourceService {

	private final Connection connection;
	
	public MondrianConnectionDataSourceService(Connection connection) {
		this.connection = connection;
	}
	
	public void setReportParameterValues(Map parameterValues) {
		parameterValues.put(JRMondrianQueryExecuterFactory.PARAMETER_MONDRIAN_CONNECTION, connection);
	}

	public void closeConnection() {
		connection.close();
	}

}
