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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.EntityEntry;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.EventSource;
import org.hibernate.event.def.DefaultDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateCompositeDeleteListener.java 11806 2008-01-17 17:56:56Z lucian $
 */
public class HibernateCompositeDeleteListener extends DefaultDeleteEventListener {
	
	private List listeners;

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}
	
	protected void cascadeBeforeDelete(EventSource session, EntityPersister persister, Object entity, EntityEntry entityEntry, Set transientEntities) throws HibernateException {
		super.cascadeBeforeDelete(session, persister, entity, entityEntry, transientEntities);
		
		fireBeforeListeners(new DeleteEvent(entity, session));
	}

	protected void fireBeforeListeners(DeleteEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				Object listener = it.next();
				if (listener instanceof HibernateBeforeDeleteListener) {
					fireListener((HibernateBeforeDeleteListener) listener, event);
				}
			}
		}
	}

	protected void fireListener(HibernateBeforeDeleteListener listener, DeleteEvent event) {
		listener.beforeDelete(event);
	}

}
