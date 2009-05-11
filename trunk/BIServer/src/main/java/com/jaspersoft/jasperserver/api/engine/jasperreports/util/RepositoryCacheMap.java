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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryCacheMap.java 8408 2007-05-29 23:29:12Z melih $
 */
public class RepositoryCacheMap {

	private static final Log log = LogFactory.getLog(RepositoryCacheMap.class); 
	
	public static interface ObjectCache {
		boolean isValid(Object o);
		Object create(ExecutionContext context, FileResource res);
		void release(Object o);
	}

	protected static class VersionObject {
		public final Object o;
		public final String referenceURI;
		public final int version;

		public VersionObject(Object o, int version) {
			this.o = o;
			this.referenceURI = null;
			this.version = version;
		}

		public VersionObject(String referenceURI, int version) {
			this.o = null;
			this.referenceURI = referenceURI;
			this.version = version;
		}
		
		public boolean isReference() {
			return referenceURI != null;
		}
	}
	
	public static class CacheObject {
		private final Object o;
		private final boolean cached;
		
		public CacheObject(Object o, boolean cached) {
			this.o = o;
			this.cached = cached;
		}
		
		public Object getObject() {
			return o;
		}
		
		public boolean isCached() {
			return cached;
		}
	}

	private final RepositoryService repository;
	private final ObjectCache objectCache;
	private final Map map;
	
	public RepositoryCacheMap(RepositoryService repository, ObjectCache objectCache) {
		this.repository = repository;
		this.objectCache = objectCache;
		map = Collections.synchronizedMap(new HashMap());
	}

	public CacheObject cache(ExecutionContext context, FileResource res, boolean cacheFirst) {
		Object ret;
		
		VersionObject val = null;
		if (cacheFirst) {
			if (log.isDebugEnabled()) {
				log.debug("Searching cache for " + res.getURIString() + ", version " + res.getVersion());
			}
			
			val = (VersionObject) map.get(res.getURIString());

			if (log.isDebugEnabled()) {
				if (val == null) {
					log.debug("Cache doesn't contain " + res.getURIString());
				} else {
					log.debug("Found in cache " + res.getURIString() + " with version " + val.version);
				}
			}
		}
		
		boolean cached = true;
		if (val != null && val.version >= res.getVersion() && (val.isReference() || objectCache.isValid(val.o))) {
			ret = val.o;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Creating cached object for " + res.getURIString() + ", version " + res.getVersion());
			}
			
			if (res.isReference()) {
				FileResource ref = (FileResource) repository.getResource(context, res.getReferenceURI());
				CacheObject refCache = cache(context, ref, true);
				ret = refCache.getObject();
				if (cacheFirst) {
					VersionObject value = new VersionObject(res.getReferenceURI(), res.getVersion());
					put(res, value);
				}
			} else {
				ret = objectCache.create(context, res);
				if (cacheFirst) {
					VersionObject value = new VersionObject(ret, res.getVersion());
					put(res, value);
				} else {
					cached = false;
				}
			}
		}

		return new CacheObject(ret, cached);
	}

	public void remove(String resourceURI) {
		if (log.isDebugEnabled()) {
			log.debug("Removing from cache " + resourceURI);
		}
		
		map.remove(resourceURI);
	}
	
	protected void put(FileResource res, VersionObject value) {
		VersionObject old = (VersionObject) map.put(res.getURIString(), value);
		if (old != null && !old.isReference()) {
			objectCache.release(old.o);
		}
	}
	
	public void release() {
		for (Iterator it = map.values().iterator(); it.hasNext();) {
			VersionObject val = (VersionObject) it.next();
			if (!val.isReference()) {
				objectCache.release(val.o);
			}
		}
	}

	protected void finalize() throws Throwable {
		release();
	}
}
