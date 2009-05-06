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

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerSupport;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ObjectPermissionsRepositoryListener.java 11000 2007-12-03 17:37:40Z lucian $
 */
public class ObjectPermissionsRepositoryListener extends RepositoryEventListenerSupport implements RepositoryListener {

	private ObjectPermissionServiceInternal permissionsService;
	
	public void onResourceDelete(Class resourceItf, String resourceURI) {
		getPermissionsService().deleteObjectPermissionForRepositoryPath(null, resourceURI);
	}

	public void onFolderDelete(String folderURI) {
		getPermissionsService().deleteObjectPermissionForRepositoryPath(null, folderURI);
	}

	public ObjectPermissionServiceInternal getPermissionsService() {
		return permissionsService;
	}

	public void setPermissionsService(ObjectPermissionServiceInternal permissionsService) {
		this.permissionsService = permissionsService;
	}

	public void folderMoved(FolderMoveEvent folderMove) {
		getPermissionsService().updateObjectPermissionRepositoryPath(
				folderMove.getOldFolderURI(), folderMove.getNewFolderURI());
	}

	public void resourceMoved(ResourceMoveEvent resourceMove) {
		getPermissionsService().updateObjectPermissionRepositoryPath(
				resourceMove.getOldResourceURI(), resourceMove.getNewResourceURI());
	}

}
