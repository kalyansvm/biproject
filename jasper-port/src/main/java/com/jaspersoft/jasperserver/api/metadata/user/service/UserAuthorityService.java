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
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;

/**
 * @author swood
 * @version $Id: UserAuthorityService.java 12524 2008-03-17 21:22:25Z achan $
 */
public interface UserAuthorityService {
	public User getUser(ExecutionContext context, String username);
	public void putUser(ExecutionContext context, User aUser);
	public List getUsers(ExecutionContext context, FilterCriteria filterCriteria);
	public User newUser(ExecutionContext context);
	public void deleteUser(ExecutionContext context, String username);
	public boolean userExists(ExecutionContext context, String username);
	
	public boolean disableUser(ExecutionContext context, String username);
	public boolean enableUser(ExecutionContext context, String username);
	public void addRole(ExecutionContext context, User aUser, Role role);
	public void removeRole(ExecutionContext context, User aUser, Role role);
	public void removeAllRoles(ExecutionContext context, User aUser);
	
	public Role getRole(ExecutionContext context, String roleName);
	public void putRole(ExecutionContext context, Role aRole);
	public List getRoles(ExecutionContext context, FilterCriteria filterCriteria);
	public Role newRole(ExecutionContext context);
	public void deleteRole(ExecutionContext context, String roleName);
	public List getUsersNotInRole(ExecutionContext context, String roleName);
	public List getUsersInRole(ExecutionContext context, String roleName);
	public List getAssignedRoles(ExecutionContext context, String userName);
	public List getAvailableRoles(ExecutionContext context, String userName);
	public boolean roleExists(ExecutionContext context, String roleName);
	public boolean isPasswordExpired(ExecutionContext context, String username, int nDate);
	public void resetPasswordExpiration(ExecutionContext context, String username);
}
