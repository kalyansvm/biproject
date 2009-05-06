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
package com.jaspersoft.jasperserver.war.dto;

import java.util.Set;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class RoleWrapper extends BaseDTO
{
	private Role role;
	private List usersNotInRole;
	private List usersInRole;
	private Object selectedUsersNotInRole;
	private Object selectedUsersInRole;


	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public List getUsersNotInRole()
	{
		return usersNotInRole;
	}

	public void setUsersNotInRole(List usersNotInRole)
	{
		this.usersNotInRole = usersNotInRole;
	}

	public List getUsersInRole()
	{
		return usersInRole;
	}

	public void setUsersInRole(List usersInRole)
	{
		this.usersInRole = usersInRole;
	}

	public Object getSelectedUsersInRole()
	{
		return selectedUsersInRole;
	}

	public void setSelectedUsersInRole(Object selectedUsersInRole)
	{
		this.selectedUsersInRole = selectedUsersInRole;
	}

	public Object getSelectedUsersNotInRole()
	{
		return selectedUsersNotInRole;
	}

	public void setSelectedUsersNotInRole(Object selectedUsersNotInRole)
	{
		this.selectedUsersNotInRole = selectedUsersNotInRole;
	}
}
