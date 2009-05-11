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
package com.jaspersoft.jasperserver.war.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.RoleWrapper;
import com.jaspersoft.jasperserver.war.tags.PaginatorTag;

/**
 * FIXME: spring:bind doesn't work properly, should take a closer look at this
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class RoleAction extends FormAction
{
	protected final Log log = LogFactory.getLog(this.getClass());
	private static final String FORM_OBJECT_KEY = "role";

	UserAuthorityService userService;
	private ConfigurationBean configuration;

	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}


	protected void initBinder(RequestContext context, DataBinder binder) {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

		binder.registerCustomEditor(String.class, "role.roleName", new StringTrimmerEditor(true));
	}

	public ConfigurationBean getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(ConfigurationBean configuration)
	{
		this.configuration = configuration;
	}

	public RoleAction()
	{
		setFormObjectClass(RoleWrapper.class); //custom form backing object class
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW); 		//this is a multi-page wizard!
	}

	public Event roleList(RequestContext context)
	{
		List roles = userService.getRoles(null, null);

		context.getRequestScope().put("roles", roles);

		return success();
	}

	public Event goToPage(RequestContext context)
	{
		context.getFlowScope().put(
			PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER, 
			context.getRequestParameters().get(PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER)
			);

		return success();
	}

	public Event removeRoles(RequestContext context)
	{
		if (context.getRequestParameters().contains("selectedRoles"))
		{
			String[] selectedRoles = context.getRequestParameters().getArray("selectedRoles");

			for (int i = 0; i < selectedRoles.length; i++)
				userService.deleteRole(null, selectedRoles[i]);
		}


		return success();
	}

	public Event editRole(RequestContext context) throws Exception
	{

		RoleWrapper wrapper = (RoleWrapper) getFormObject(context);
		String roleName = wrapper.getRole().getRoleName();

		if (roleName != null) {
			wrapper.setUsersNotInRole(userService.getUsersNotInRole(null, roleName));
			wrapper.setUsersInRole(userService.getUsersInRole(null, roleName));
		}
		else {
			wrapper.setUsersNotInRole(userService.getUsers(null, null));
			wrapper.setUsersInRole(new ArrayList());
		}
		return success();
	}

	public Event saveRole(RequestContext context) throws Exception
	{
		RoleWrapper wrapper = (RoleWrapper) getFormObject(context);
		Role role = wrapper.getRole();

		List usersInRole = wrapper.getUsersInRole();
		if (usersInRole != null)
			for (int i = 0; i < usersInRole.size(); i++)
				role.addUser((User) usersInRole.get(i));

		userService.putRole(null, role);

		return success();
	}

	public Event addUser(RequestContext context) throws Exception
	{
		RoleWrapper wrapper = (RoleWrapper) getFormObject(context);

		List usersNotInRole = wrapper.getUsersNotInRole();
		List usersInRole = wrapper.getUsersInRole();

		String[] selectedUsersNotInRole = getComboBoxValues(context, "selectedUsersNotInRole");
		if (selectedUsersNotInRole == null)
			return success();

		for (int i = 0; i < selectedUsersNotInRole.length; i++) {
			for (int j = 0; j < usersNotInRole.size(); j++) {
				User user = (User) usersNotInRole.get(j);
				if (user.getUsername() != null && user.getUsername().equals(selectedUsersNotInRole[i])) {
					usersNotInRole.remove(user);
					usersInRole.add(user);
					break;
				}
			}
		}

		return success();
	}

	public Event removeUser(RequestContext context) throws Exception
	{
		RoleWrapper wrapper = (RoleWrapper) getFormObject(context);

		List usersNotInRole = wrapper.getUsersNotInRole();
		List usersInRole = wrapper.getUsersInRole();
		String[] selectedUsersInRole = getComboBoxValues(context, "selectedUsersInRole");
		if (selectedUsersInRole == null)
			return success();

		for (int i = 0; i < selectedUsersInRole.length; i++) {
			for (int j = 0; j < usersInRole.size(); j++) {
				User user = (User) usersInRole.get(j);
				if (user.getUsername() != null && user.getUsername().equals(selectedUsersInRole[i])) {
					usersInRole.remove(user);
					usersNotInRole.add(user);
					break;
				}
			}
		}
		return success();
	}

	public Object createFormObject(RequestContext context)
	{
		String roleName = context.getRequestParameters().get("roleName");
		Role role;
		RoleWrapper wrapper = new RoleWrapper();
		if (roleName != null && roleName.length() > 0) {
			role = userService.getRole(null, roleName);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
		}
		else {
			role = userService.newRole(null);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
		}

		wrapper.setRole(role);

		return wrapper;
	}

	private String[] getComboBoxValues(RequestContext context, String comboBoxName) {
		if (context.getRequestParameters().contains(comboBoxName))
		{
			return context.getRequestParameters().getArray(comboBoxName);
		}
		return null;
	}
}
