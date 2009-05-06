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

import org.hibernate.HibernateException;
import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateCompositeSaveOrUpdateListener.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HibernateCompositeSaveOrUpdateListener extends DefaultSaveOrUpdateEventListener {
	
	private List listeners;

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}

	public void onSaveOrUpdate(final SaveOrUpdateEvent event) throws HibernateException {
		visitListeners(new ListenerVisitor() {
			public void visit(HibernateSaveOrUpdateListener listener) {
				listener.beforeSaveOrUpdate(event);
			}
		});
		
		super.onSaveOrUpdate(event);
		
		visitListeners(new ListenerVisitor() {
			public void visit(HibernateSaveOrUpdateListener listener) {
				listener.afterSaveOrUpdate(event);
			}
		});
	}

	protected static interface ListenerVisitor {
		void visit(HibernateSaveOrUpdateListener listener);
	}
	
	protected void visitListeners(ListenerVisitor visitor) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				HibernateSaveOrUpdateListener listener = (HibernateSaveOrUpdateListener) it.next();
				visitor.visit(listener);
			}
		}
	}

}
