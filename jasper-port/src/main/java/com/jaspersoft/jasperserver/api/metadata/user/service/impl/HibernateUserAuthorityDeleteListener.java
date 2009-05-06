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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityEventListener;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityEventListenerRegistry;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateUserAuthorityDeleteListener.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HibernateUserAuthorityDeleteListener implements HibernateDeleteListener, UserAuthorityEventListenerRegistry {
	
	private List listeners;

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}

	protected void ensureListeners() {
		if (listeners == null) {
			listeners = new ArrayList();
		}
	}

	public void registerListener(UserAuthorityEventListener listener) {
		ensureListeners();
		listeners.add(listener);
	}

	public void deregisterListener(UserAuthorityEventListener listener) {
		ensureListeners();
		listeners.remove(listener);
	}

	public void onDelete(Object o) {
		if (o instanceof RepoUser) {
			RepoUser user = (RepoUser) o;
			final String username = user.getUsername();
			fireListeners(new ListenerVisitor() {
				public void visit(UserAuthorityEventListener listener) {
					listener.onUserDelete(username);
				}
			});
		} else if (o instanceof RepoRole) {
			RepoRole role = (RepoRole) o;
			final String roleName = role.getRoleName();
			fireListeners(new ListenerVisitor() {
				public void visit(UserAuthorityEventListener listener) {
					listener.onRoleDelete(roleName);
				}
			});
		}
	}

	protected static interface ListenerVisitor {
		void visit(UserAuthorityEventListener listener);
	}
	
	protected void fireListeners(ListenerVisitor visitor) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				UserAuthorityEventListener listener = (UserAuthorityEventListener) it.next();
				visitor.visit(listener);
			}
		}
	}

}
