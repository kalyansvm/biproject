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

import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: XmlaConnectionDataSourceService.java 10276 2007-09-28 23:35:53Z lucian $
 */
public class XmlaConnectionDataSourceService implements ReportDataSourceService {

	private final XMLAConnection xmlaConnection;
	private final User contextUser;
	
	public XmlaConnectionDataSourceService(XMLAConnection xmlaConnection) {
		this(xmlaConnection, null);
	}
	
	public XmlaConnectionDataSourceService(XMLAConnection xmlaConnection,
			User contextUser) {
		this.xmlaConnection = xmlaConnection;
		this.contextUser = contextUser;
	}

	public void setReportParameterValues(Map parameterValues) {
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL, xmlaConnection.getURI());
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_CATALOG, xmlaConnection.getCatalog());
		parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_DATASOURCE, xmlaConnection.getDataSource());
		
		if (contextUser == null) {
			parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER, xmlaConnection.getUsername());
			parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD, xmlaConnection.getPassword());
		} else {
			parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER, contextUser.getUsername());
			parameterValues.put(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD, contextUser.getPassword());
		}
	}

	public void closeConnection() {
	}

}
