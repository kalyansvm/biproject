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
package com.jaspersoft.jasperserver.api.metadata.user.service;

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;

/**
 * @author swood
 *
 */
public interface ObjectPermissionService {
	public ObjectPermission newObjectPermission(ExecutionContext context);
	public ObjectPermission getObjectPermission(ExecutionContext context, ObjectPermission objPerm);
	public List getObjectPermissionsForRecipient(ExecutionContext context, Object recipient);
	public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject);
	public List getObjectPermissionsForObjectAndRecipient(ExecutionContext context, Object targetObject, Object recipient);
	public void putObjectPermission(ExecutionContext context, ObjectPermission objPerm);
	public void deleteObjectPermission(ExecutionContext context, ObjectPermission objPerm);
	
	public void deleteObjectPermissionForObject(ExecutionContext context, Object targetObject);
	
	public void deleteObjectPermissionsForRecipient(ExecutionContext context, Object recipient);
	
	/*
	public ObjectIdentity newObjectIdentity(ExecutionContext context);
	public List getObjectIdentitiesForObject(ExecutionContext context, Object targetObject);
	public ObjectIdentity getObjectIdentityForObject(ExecutionContext context, Object parentObject, Object targetObject);
	public void putObjectIdentity(ExecutionContext context, ObjectIdentity objIdent);
	*/
	
}
