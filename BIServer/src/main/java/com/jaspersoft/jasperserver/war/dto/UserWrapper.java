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

import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class UserWrapper extends BaseDTO
{
	private User user;
	private String confirmedPassword;
	private List assignedRoles;
	private List availableRoles;
	private Object selectedAssignedRoles;
	private Object selectedAvailableRoles;


	public List getAssignedRoles()
	{
		return assignedRoles;
	}

	public void setAssignedRoles(List assignedRoles)
	{
		this.assignedRoles = assignedRoles;
	}

	public List getAvailableRoles()
	{
		return availableRoles;
	}

	public void setAvailableRoles(List availableRoles)
	{
		this.availableRoles = availableRoles;
	}

	public Object getSelectedAssignedRoles()
	{
		return selectedAssignedRoles;
	}

	public void setSelectedAssignedRoles(Object selectedAssignedRoles)
	{
		this.selectedAssignedRoles = selectedAssignedRoles;
	}

	public Object getSelectedAvailableRoles()
	{
		return selectedAvailableRoles;
	}

	public void setSelectedAvailableRoles(Object selectedAvailableRoles)
	{
		this.selectedAvailableRoles = selectedAvailableRoles;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
		
		setConfirmedPassword(this.user == null ? null : this.user.getPassword());
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}
}
