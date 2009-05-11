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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author swood
 * @version $Id: HibernateDaoImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HibernateDaoImpl extends HibernateDaoSupport {
	
	private static final Log log = LogFactory.getLog(HibernateDaoImpl.class);
	
	private final ThreadLocal operationDate;
	
	public HibernateDaoImpl()
	{
		operationDate = new ThreadLocal();
	}

	protected static interface DaoCallback {
		Object execute();
	}

	protected final Object executeCallback(final DaoCallback callback) {
		try {
			Object ret = callback.execute();
			return ret;
		} catch (DataAccessException e) {
			log.error("Hibernate DataAccessException", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected final Object executeWriteCallback(final DaoCallback callback) {
		return executeWriteCallback(callback, true);
	}

	protected final Object executeWriteCallback(final DaoCallback callback, boolean flush) {
		startOperation();
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		int origFlushMode = hibernateTemplate.getFlushMode();
		try {
			hibernateTemplate.setFlushMode(HibernateAccessor.FLUSH_COMMIT);
			Object ret = callback.execute();
			if (flush) {
				hibernateTemplate.flush();
			}
			return ret;
		} catch (DataAccessException e) {
			log.error("Hibernate DataAccessException", e);
			throw new JSExceptionWrapper(e);
		}
		finally {
			hibernateTemplate.setFlushMode(origFlushMode);
			endOperation();
		}
	}

	protected void startOperation() {
		operationDate.set(new Date());
	}
	
	protected Date getOperationTimestamp() {
		return (Date) operationDate.get();
	}
	
	protected void endOperation() {
		operationDate.set(null);
	}
}
