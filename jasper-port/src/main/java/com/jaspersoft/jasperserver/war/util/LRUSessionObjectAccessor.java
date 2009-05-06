/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package com.jaspersoft.jasperserver.war.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.WebUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LRUSessionObjectAccessor.java 8408 2007-05-29 23:29:12Z melih $
 */
public class LRUSessionObjectAccessor implements SessionObjectSerieAccessor {

	protected static final Log log = LogFactory.getLog(LRUSessionObjectAccessor.class);
	
	private String listSessionName;
	private int maxSize;

	protected static class ObjectSerie extends LinkedHashMap {
		
		private static final long serialVersionUID = 1L;
		
		private final int maxSize;

		public ObjectSerie(int maxSize) {
			super();
			this.maxSize = maxSize;
		}

		protected boolean removeEldestEntry(Entry entry) {
			boolean remove = size() > maxSize;
			
			if (remove && log.isDebugEnabled()) {
				log.debug("Automatically removing object with name " + entry.getKey());
			}
			
			return remove;
		}

	}
	
	public String putObject(HttpServletRequest request, Object object) {
		ObjectSerie objectSerie = getObjectSerie(request);
		String name = createName(object);
		
		if (log.isDebugEnabled()) {
			log.debug("Putting object " + object + " with name " + name);
		}

		synchronized (objectSerie) {
			objectSerie.put(name, object);
		}

		return name;
	}

	public Object getObject(HttpServletRequest request, String name) {
		ObjectSerie objectSerie = getObjectSerie(request);
		synchronized (objectSerie) {
			Object object = objectSerie.get(name);
			return object;
		}
	}

	public void removeObject(HttpServletRequest request, String name) {
		if (log.isDebugEnabled()) {
			log.debug("Removing object with name " + name);
		}
		
		ObjectSerie objectSerie = getObjectSerie(request);
		synchronized (objectSerie) {
			objectSerie.remove(name);
		}
	}
	
	protected ObjectSerie getObjectSerie(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object mutex = WebUtils.getSessionMutex(session);
		ObjectSerie serie;
		boolean created = false;
		synchronized (mutex) {
			serie = (ObjectSerie) session.getAttribute(getListSessionName());
			if (serie == null) {
				created = true;
				serie = new ObjectSerie(getMaxSize());
				session.setAttribute(getListSessionName(), serie);
			}
		}
		
		if (created && log.isDebugEnabled()) {
			log.debug("Created object serie " + serie + " for session " + session.getId());
		}
		
		return serie;
	}
	
	protected String createName(Object object) {
		return System.identityHashCode(object) + "_" + System.currentTimeMillis();
	}

	public String getListSessionName() {
		return listSessionName;
	}

	public void setListSessionName(String listSessionName) {
		this.listSessionName = listSessionName;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

}
