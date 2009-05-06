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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectPermissionRecipientIdentity;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ObjectPermissionServiceInternal.java 11000 2007-12-03 17:37:40Z lucian $
 */
public interface ObjectPermissionServiceInternal {
	
	void deleteObjectPermissionForRepositoryPath(ExecutionContext context, String path);
	
	void updateObjectPermissionRepositoryPath(String oldPath, String newPath);
	
	void deleteObjectPermissionsForRecipient(ExecutionContext context, ObjectPermissionRecipientIdentity recipientIdentity);

}
