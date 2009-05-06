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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.UserWrapper;
import com.jaspersoft.jasperserver.war.tags.PaginatorTag;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class UserAction extends FormAction
{
	protected final Log log = LogFactory.getLog(this.getClass());
	private static final String FORM_OBJECT_KEY = "user";

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

		binder.registerCustomEditor(String.class, "user.username", new StringTrimmerEditor(true));
	}

	public ConfigurationBean getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(ConfigurationBean configuration)
	{
		this.configuration = configuration;
	}

	public UserAction()
	{
		setFormObjectClass(UserWrapper.class); //custom form backing object class
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW); 		//this is a multi-page wizard!
//		setValidator(new CRUDUserValidator());

	}


	public Event userList(RequestContext context)
	{
		List users = userService.getUsers(null, null);

		context.getRequestScope().put("users", users);

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

	public Event removeUsers(RequestContext context)
	{
		if (context.getRequestParameters().contains("selectedUsers"))
		{
			String[] selectedUsers = context.getRequestParameters().getArray("selectedUsers");

			for (int i = 0; i < selectedUsers.length; i++)
				userService.deleteUser(null, selectedUsers[i]);
		}


		return success();
	}

	public Event editUser(RequestContext context) throws Exception
	{
		UserWrapper wrapper = (UserWrapper) getFormObject(context);
		String userName = wrapper.getUser().getUsername();

		Role defaultRole = new RoleImpl();
		defaultRole.setRoleName("ROLE_USER");

		if (userName != null) {
			wrapper.setAvailableRoles(userService.getAvailableRoles(null, userName));
			wrapper.setAssignedRoles(userService.getAssignedRoles(null, userName));
		}
		else {
			List availableRoles = userService.getRoles(null, null);
			availableRoles.remove(defaultRole);
			wrapper.setAvailableRoles(availableRoles);

			ArrayList assignedRoles = new ArrayList();
			assignedRoles.add(defaultRole);
			wrapper.setAssignedRoles(assignedRoles);
		}
		return success();
	}

	public Event addRoles(RequestContext context) throws Exception
	{
		UserWrapper wrapper = (UserWrapper) getFormObject(context);

		List availableRoles = wrapper.getAvailableRoles();
		List assignedRoles = wrapper.getAssignedRoles();

		String[] selectedAvailableRoles = getComboBoxValues(context, "selectedAvailableRoles");
		if (selectedAvailableRoles == null)
			return success();

		for (int i = 0; i < selectedAvailableRoles.length; i++) {
			for (int j = 0; j < availableRoles.size(); j++) {
				Role role = (Role) availableRoles.get(j);
				if (role.getRoleName() != null && role.getRoleName().equals(selectedAvailableRoles[i])) {
					availableRoles.remove(role);
					assignedRoles.add(role);
					break;
				}
			}
		}

		return success();
	}

	public Event removeRoles(RequestContext context) throws Exception
	{
		UserWrapper wrapper = (UserWrapper) getFormObject(context);

		List availableRoles = wrapper.getAvailableRoles();
		List assignedRoles = wrapper.getAssignedRoles();

		String[] selectedAssignedRoles = getComboBoxValues(context, "selectedAssignedRoles");
		if (selectedAssignedRoles == null)
			return success();

		for (int i = 0; i < selectedAssignedRoles.length; i++) {
			for (int j = 0; j < assignedRoles.size(); j++) {
				Role role = (Role) assignedRoles.get(j);
				if (role.getRoleName() != null && role.getRoleName().equals(selectedAssignedRoles[i])) {
					assignedRoles.remove(role);
					availableRoles.add(role);
					break;
				}
			}
		}

		return success();
	}

	public Event saveUser(RequestContext context) throws Exception
	{
		UserWrapper wrapper = (UserWrapper) getFormObject(context);
		User user = wrapper.getUser();

		Set existingRoles = user.getRoles();
		Map existingRolesMap = new HashMap();
		if (existingRoles != null) {
			for (Iterator it = existingRoles.iterator(); it.hasNext();) {
				Role role = (Role) it.next();
				existingRolesMap.put(role.getRoleName(), role);
			}
		}

		List assignedRoles = wrapper.getAssignedRoles();
		if (assignedRoles != null) {
			for (int i = 0; i < assignedRoles.size(); i++) {
				Role role = (Role) assignedRoles.get(i);
				if (existingRolesMap.remove(role.getRoleName()) == null) {
					user.addRole(role);
				}
			}
		}
		
		for (Iterator it = existingRolesMap.values().iterator(); it.hasNext();) {
			Role role = (Role) it.next();
			user.removeRole(role);
		}
		
		// reset password changing timestamp
		user.setPreviousPasswordChangeTime(new Date());

		userService.putUser(null, user);

		return success();
	}

	public Object createFormObject(RequestContext context)
	{
		String userName = context.getRequestParameters().get("userName");
		User user;
		UserWrapper wrapper = new UserWrapper();
		if (userName != null && userName.length() > 0) {
			user = userService.getUser(null, userName);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
		}
		else {
			user = userService.newUser(null);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
		}

		wrapper.setUser(user);

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
