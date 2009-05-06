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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JndiJdbcDataSourceService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class JndiJdbcDataSourceService extends BaseJdbcDataSource {
	
	private static final Log log = LogFactory.getLog(JndiJdbcDataSourceService.class);

	private final String jndiName;
	
	public JndiJdbcDataSourceService(String jndiName) {
		this.jndiName = jndiName;
	}
	
	protected Connection createConnection() {
		try
		{
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
			return ds.getConnection();
		}
		catch (NamingException e)
		{
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
		catch (SQLException e)
		{
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

}
