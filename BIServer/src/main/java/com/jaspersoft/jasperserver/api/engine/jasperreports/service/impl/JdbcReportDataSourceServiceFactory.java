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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JdbcReportDataSourceServiceFactory.java 8408 2007-05-29 23:29:12Z melih $
 */
public class JdbcReportDataSourceServiceFactory implements ReportDataSourceServiceFactory {

	private static final Log log = LogFactory.getLog(JdbcReportDataSourceServiceFactory.class);

	protected static class PooledDataSourcesCache {
		protected static class DataSourceEntry {
			final Object key;
			final PooledDataSource ds;
			DataSourceEntry next, prev;
			long lastAccess;

			public DataSourceEntry(Object key, PooledDataSource ds) {
				this.key = key;
				this.ds = ds;
			}

			public void access(long now) {
				lastAccess = now;
			}
		}
		
		final Map cache;
		DataSourceEntry first, last;

		public PooledDataSourcesCache() {
			cache = new HashMap();
			first = last = null;
		}

		public PooledDataSource get(Object key, long now) {
			DataSourceEntry entry = (DataSourceEntry) cache.get(key);

			if (entry == null) {
				return null;
			}
			
			moveFirst(entry);
			entry.access(now);
			return entry.ds;
		}

		private void moveFirst(DataSourceEntry entry) {
			entry.next = first;
			entry.prev = null;
			if (first != null) {
				first.prev = entry;
			}
			first = entry;
			if (last == null) {
				last = entry;
			}
		}

		public void put(Object key, PooledDataSource ds, long now) {
			DataSourceEntry entry = new DataSourceEntry(key, ds);
			moveFirst(entry);
			entry.access(now);
			cache.put(key, entry);
		}
		
		public List removeExpired(long now, int timeout) {
			List expired = new ArrayList();
			DataSourceEntry entry = last;
			long expTime = now - timeout * 1000;
			while (entry != null && entry.lastAccess < expTime) {
				expired.add(entry.ds);				
				remove(entry);
				entry = entry.prev;
			}
			return expired;
		}

		protected void remove(DataSourceEntry entry) {
			cache.remove(entry.key);
			if (entry.prev != null) {
				entry.prev.next = entry.next;
			}
			if (entry.next != null) {
				entry.next.prev = entry.prev;
			}
			if (first == entry) {
				first = entry.next;
			}
			if (last == entry) {
				last = entry.prev;
			}
		}
	}

	private PooledJdbcDataSourceFactory pooledJdbcDataSourceFactory;
	private PooledDataSourcesCache poolDataSources;
	private int poolTimeout;
	
	public JdbcReportDataSourceServiceFactory () {
		poolDataSources = new PooledDataSourcesCache();
	}

	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof JdbcReportDataSource)) {
			throw new JSException("jsexception.invalid.jdbc.datasource", new Object[] {reportDataSource.getClass()});
		}
		JdbcReportDataSource jdbcDataSource = (JdbcReportDataSource) reportDataSource;
	
		DataSource dataSource = getPoolDataSource(jdbcDataSource.getDriverClass(), jdbcDataSource.getConnectionUrl(), jdbcDataSource.getUsername(), jdbcDataSource.getPassword());
		TimeZone timeZone = jdbcDataSource.getTimezone() != null ? TimeZone.getTimeZone(jdbcDataSource.getTimezone()) : null; 		
		return new JdbcDataSourceService(dataSource, timeZone);
	}

	protected DataSource getPoolDataSource(String driverClass, String url, String username, String password) {
		Object poolKey = createJdbcPoolKey(driverClass, url, username, password);
		PooledDataSource dataSource;
		List expired = null;
		long now = System.currentTimeMillis();
		synchronized (poolDataSources) {
			dataSource = poolDataSources.get(poolKey, now);
			if (dataSource == null) {
				if (log.isDebugEnabled()) {
					log.debug("Creating connection pool for driver=\"" + driverClass + "\", url=\"" +
							url + "\", username=\"" + username + "\".");
				}
				dataSource = pooledJdbcDataSourceFactory.createPooledDataSource(driverClass, url, username, password);
				poolDataSources.put(poolKey, dataSource, now);
			}

			if (getPoolTimeout() > 0) {
				expired = poolDataSources.removeExpired(now, getPoolTimeout());
			}
		}

		if (expired != null && !expired.isEmpty()) {
			for (Iterator it = expired.iterator(); it.hasNext();) {
				PooledDataSource ds = (PooledDataSource) it.next();
				try {
					ds.release();
				} catch (Exception e) {
					log.error("Error while releasing connection pool.", e);
					// ignore
				}
			}
		}

		return dataSource.getDataSource();
	}

	public PooledJdbcDataSourceFactory getPooledJdbcDataSourceFactory() {
		return pooledJdbcDataSourceFactory;
	}

	public void setPooledJdbcDataSourceFactory(
			PooledJdbcDataSourceFactory jdbcDataSourceFactory) {
		this.pooledJdbcDataSourceFactory = jdbcDataSourceFactory;
	}

	protected Object createJdbcPoolKey(String driverClass,
			String url, String username, String password) {
		return new JdbcPoolKey(driverClass, url, username, password);
	}
	
	protected static class JdbcPoolKey {
		private final String driverClass;
		private final String url;
		private final String username;
		private final String password;
		private final int hash;

		public JdbcPoolKey(String driverClass, String url, String username,
				String password) {
			this.driverClass = driverClass;
			this.url = url;
			this.username = username;
			this.password = password;

			int hashCode = 559;
			if (driverClass != null) {
				hashCode += driverClass.hashCode();
			}
			hashCode *= 43;
			if (url != null) {
				hashCode += url.hashCode();
			}
			hashCode *= 43;
			if (username != null) {
				hashCode += username.hashCode();
			}
			hashCode *= 43;
			if (password != null) {
				hashCode += password.hashCode();
			}
			
			hash = hashCode;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof JdbcPoolKey)) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			
			JdbcPoolKey key = (JdbcPoolKey) obj;
			return
				(driverClass == null ? key.driverClass == null : (key.driverClass != null && driverClass.equals(key.driverClass))) &&
				(url == null ? key.url == null : (key.url != null && url.equals(key.url))) &&
				(username == null ? key.username == null : (key.username != null && username.equals(key.username))) &&
				(password == null ? key.password == null : (key.password != null && password.equals(key.password)));
		}

		public int hashCode() {
			return hash;
		}

	}

	public int getPoolTimeout() {
		return poolTimeout;
	}

	public void setPoolTimeout(int poolTimeout) {
		this.poolTimeout = poolTimeout;
	}
}
