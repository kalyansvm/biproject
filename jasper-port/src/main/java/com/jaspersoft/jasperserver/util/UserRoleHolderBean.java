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
package com.jaspersoft.jasperserver.util;

/**
 * @author tkavanagh
 * @version $Id: UserRoleHolderBean.java 8408 2007-05-29 23:29:12Z melih $
 */

/*
 * This bean class is used as a holder for Users and Roles.
 * Users and Roles are separate from repository resource objects
 * (Users and Roles do not inherit from Resource). 
 * 
 * For convenience, the UserRoleHolder object will be stored as part
 * of the highest level object in the catalog export tree (xml). The
 * import operation will look for it there.
 * 
 */
public class UserRoleHolderBean {

	private UserBean[] users;
	private RoleBean[] roles;
	
	public RoleBean[] getRoles() {
		return roles;
	}
	
	public void setRoles(RoleBean[] roles) {
		this.roles = roles;
	}
	
	public UserBean[] getUsers() {
		return users;
	}
	
	public void setUsers(UserBean[] users) {
		this.users = users;
	}
}
