/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 * @author Lucian Chirita
 *
 */
public class HibernateCompositeInterceptor extends EmptyInterceptor {
	
	private List listeners;

	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		fireDeleteListeners(entity);
	}
	
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		fireSaveListeners(entity);
		return false;
	}

	protected void fireDeleteListeners(Object deleted) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				Object listener = it.next();
				if (listener instanceof HibernateDeleteListener) {
					fireListener((HibernateDeleteListener) listener, deleted);
				}
			}
		}
	}

	protected void fireSaveListeners(Object saved) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				Object listener = it.next();
				if (listener instanceof HibernateSaveListener) {
					fireListener((HibernateSaveListener) listener, saved);
				}
			}
		}
	}

	protected void fireListener(HibernateDeleteListener listener, Object deleted) {
		listener.onDelete(deleted);
	}

	protected void fireListener(HibernateSaveListener listener, Object saved) {
		listener.onSave(saved);
	}

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}

}
