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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import net.sf.jasperreports.engine.util.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryCache;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryCacheableItem;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.CachedItem;
import com.jaspersoft.jasperserver.api.metadata.common.util.LocalLockManager;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockHandle;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockManager;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateRepositoryCache.java 13142 2008-04-23 15:47:20Z lucian $
 */
public class HibernateRepositoryCache extends HibernateDaoImpl implements RepositoryCache {
	private static final Log log = LogFactory.getLog(HibernateRepositoryCache.class);

	private RepositoryService repository;
	private LockManager lockManager = new LocalLockManager();

	public HibernateRepositoryCache() {
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public InputStream cache(ExecutionContext context, FileResource resource, RepositoryCacheableItem cacheableItem) {
		CachedItem cachedItem = getCachedItem(context, resource, cacheableItem);
		while(cachedItem.isItemReference()) {
			cachedItem = cachedItem.getReference();
		}
		return new ByteArrayInputStream(cachedItem.getDataBytes());
	}

	public InputStream cache(ExecutionContext context, String uri, RepositoryCacheableItem cacheableItem) {
		FileResource resource = (FileResource) repository.getResource(context, uri);
		return cache(context, resource, cacheableItem);
	}

	protected LockHandle lock(FileResource resource, RepositoryCacheableItem cacheableItem) {
		return lockManager.lock(cacheableItem.getCacheName(), resource.getURIString());
	}

	protected void unlock(LockHandle lock) {
		lockManager.unlock(lock);
	}

	protected Pair getLockKey(FileResource resource, RepositoryCacheableItem cacheableItem) {
		return new Pair(resource.getURIString(), cacheableItem.getCacheName());
	}

	protected CachedItem getCachedItem(ExecutionContext context, FileResource resource, RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Looking in repository cache \"" + cacheableItem.getCacheName() + "\" for resource \"" + resource.getURIString() +
					"\", version " + resource.getVersion() + "\", version date " + resource.getCreationDate());
		}

		LockHandle lock = lock(resource, cacheableItem);
		try {
			CachedItem cachedItem = getCachedItem(resource.getURIString(), cacheableItem);
			if (cachedItem == null
					|| cachedItem.getVersion() < resource.getVersion()
					|| cachedItem.getVersionDate() == null
					|| cachedItem.getVersionDate().before(resource.getCreationDate())) {
				if (resource.isReference()) {
					cachedItem = saveRefence(context, resource, cachedItem, cacheableItem);
				} else {
					cachedItem = saveData(context, resource, cachedItem, cacheableItem);
				}
			} else if (resource.isReference()) {
				//load the reference to force updates
				FileResource ref = (FileResource) repository.getResource(context, resource.getReferenceURI());
				CachedItem refItem = getCachedItem(context, ref, cacheableItem);
				if (!refItem.equals(cachedItem.getReference())) {
					updateReference(cachedItem, refItem);
				}
			}
			return cachedItem;
		} finally {
			unlock(lock);
		}
	}

	protected CachedItem getCachedItem(String uri, RepositoryCacheableItem cacheableItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CachedItem.class);
		criteria.add(Restrictions.naturalId().set("cacheName", cacheableItem.getCacheName()).set("uri", uri));
		List list = getHibernateTemplate().findByCriteria(criteria);
		CachedItem item;
		if (list.isEmpty()) {
			item = null;
		} else {
			item = (CachedItem) list.get(0);
		}
		return item;
	}

	protected CachedItem saveRefence(ExecutionContext context, FileResource resource, CachedItem item, RepositoryCacheableItem cacheableItem) {
		FileResource ref = (FileResource) repository.getResource(context, resource.getReferenceURI());
		CachedItem refItem = getCachedItem(context, ref, cacheableItem);

		CachedItem saveItem;
		if (item == null) {
			saveItem = new CachedItem();
		} else {
			saveItem = item;
		}
		saveItem.setCacheName(cacheableItem.getCacheName());
		saveItem.setData(null);
		saveItem.setReference(refItem);
		saveItem.setUri(resource.getURIString());
		saveItem.setVersion(resource.getVersion());
		saveItem.setVersionDate(resource.getCreationDate());

		if (item == null) {
			getHibernateTemplate().save(saveItem);
		} else {
			getHibernateTemplate().update(saveItem);
		}

		return saveItem;
	}

	protected CachedItem saveData(ExecutionContext context, FileResource resource, CachedItem item, RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Saving repository cache \"" + cacheableItem.getCacheName() + "\" for resource \"" + resource.getURIString() +
					"\", version " + resource.getVersion() + "\", version date " + resource.getCreationDate());
		}

		byte[] data = cacheableItem.getData(context, resource);

		CachedItem saveItem;
		if (item == null) {
			saveItem = new CachedItem();
		} else {
			saveItem = item;
		}
		saveItem.setCacheName(cacheableItem.getCacheName());
		saveItem.setDataBytes(data);
		saveItem.setReference(null);
		saveItem.setUri(resource.getURIString());
		saveItem.setVersion(resource.getVersion());
		saveItem.setVersionDate(resource.getCreationDate());

		if (item == null) {
			getHibernateTemplate().save(saveItem);
		} else {
			getHibernateTemplate().update(saveItem);
		}

		return saveItem;
	}

	protected void updateReference(CachedItem item, CachedItem refItem) {
		item.setReference(refItem);
		getHibernateTemplate().update(item);
	}

	public void clearCache(final String uri, final RepositoryCacheableItem cacheableItem) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				removeCached(uri, cacheableItem);
				return null;
			}
		}, false);
	}

	protected void removeCached(String uri, RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Clearing cache " + cacheableItem.getCacheName() + " for resource " + uri);
		}

		CachedItem cachedItem = getCachedItem(uri, cacheableItem);
		if (cachedItem != null) {
			getHibernateTemplate().delete(cachedItem);
		}
	}

	public void clearCache(final RepositoryCacheableItem cacheableItem) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				removeCached(cacheableItem);
				return null;
			}
		}, false);
	}

	protected void removeCached(RepositoryCacheableItem cacheableItem) {
		if (log.isDebugEnabled()) {
			log.debug("Clearing entire cache " + cacheableItem.getCacheName());
		}

		getHibernateTemplate().bulkUpdate(
				"delete CachedItem where cacheName = ?", 
				cacheableItem.getCacheName());
	}

	public LockManager getLockManager() {
		return lockManager;
	}

	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}
}
