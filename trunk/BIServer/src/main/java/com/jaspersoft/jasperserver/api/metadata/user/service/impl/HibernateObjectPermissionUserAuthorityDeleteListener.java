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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectPermissionRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateObjectPermissionUserAuthorityDeleteListener.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HibernateObjectPermissionUserAuthorityDeleteListener implements
		HibernateDeleteListener, ApplicationContextAware {

	private String objectPermissionsServiceBeanName;

	private ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	protected ObjectPermissionServiceInternal getObjectPermissionService() {
		return (ObjectPermissionServiceInternal) context.getBean(
				getObjectPermissionsServiceBeanName(),
				ObjectPermissionServiceInternal.class);
	}
	
	public void onDelete(Object o) {
		if (o instanceof RepoUser) {
			deleteObjectPermissions((RepoUser) o);
		} else if (o instanceof RepoRole) {
			deleteObjectPermissions((RepoRole) o);
		}
	}
	
	protected void deleteObjectPermissions(IdedObject recipient) {
		ObjectPermissionRecipientIdentity recipientIdentity = new ObjectPermissionRecipientIdentity(recipient);
		getObjectPermissionService().deleteObjectPermissionsForRecipient(null, recipientIdentity);
	}

	public String getObjectPermissionsServiceBeanName() {
		return objectPermissionsServiceBeanName;
	}

	public void setObjectPermissionsServiceBeanName(
			String objectPermissionsServiceBeanName) {
		this.objectPermissionsServiceBeanName = objectPermissionsServiceBeanName;
	}

}
