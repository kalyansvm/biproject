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

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.ObjectPoolFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DbcpDataSource.java 8408 2007-05-29 23:29:12Z melih $
 */
public class DbcpDataSource implements PooledDataSource {
	
	private final static Log log = LogFactory.getLog(DbcpDataSource.class);

	private final ObjectPool connectionPool;
	private final PoolingDataSource dataSource;
	
	public DbcpDataSource(ObjectPoolFactory objectPoolFactory, 
			String url, String username, String password) {
		connectionPool = createPool(objectPoolFactory);
		createPoolableConnectionFactory(url, username, password);

		dataSource = new PoolingDataSource(connectionPool);
	}

	protected ObjectPool createPool(ObjectPoolFactory objectPoolFactory) {
		ObjectPool objectPool = objectPoolFactory.createPool();
		return objectPool;
	}

	protected void createPoolableConnectionFactory(String url, String username, String password) {
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, username, password);
		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, true, false);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void release() {
		try {
			connectionPool.close();
		} catch (Exception e) {
			log.error("Error while closing DBCP connection pool", e);
			throw new JSExceptionWrapper(e);
		}
	}

}
