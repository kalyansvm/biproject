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

package com.jaspersoft.jasperserver.export.modules.auth;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.auth.beans.RoleBean;
import com.jaspersoft.jasperserver.export.modules.auth.beans.UserBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AuthorityExporter.java 9458 2007-08-10 14:30:48Z lucian $
 */
public class AuthorityExporter extends BaseExporterModule {

	private String usersArgument;
	private String rolesArgument;
	private String includeRoleUsersArgument;
	private AuthorityModuleConfiguration configuration;

	private Map users;
	private Map roles;
	private boolean includeRoleUsers;

	public String getIncludeRoleUsersArgument() {
		return includeRoleUsersArgument;
	}

	public void setIncludeRoleUsersArgument(String includeRoleUserArgument) {
		this.includeRoleUsersArgument = includeRoleUserArgument;
	}

	public String getRolesArgument() {
		return rolesArgument;
	}

	public void setRolesArgument(String rolesArgument) {
		this.rolesArgument = rolesArgument;
	}

	public String getUsersArgument() {
		return usersArgument;
	}

	public void setUsersArgument(String usersArgument) {
		this.usersArgument = usersArgument;
	}

	public AuthorityModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AuthorityModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public void init(ExporterModuleContext moduleContext) {
		super.init(moduleContext);
		
		initUsers();
		initRoles();
	}

	protected void initUsers() {
		users = new LinkedHashMap();
		if (exportEverything || hasParameter(getUsersArgument())) {
			String[] userNames = getParameterValues(getUsersArgument());
			if (exportEverything || userNames == null) {
				List usersList = configuration.getAuthorityService().getUsers(executionContext, null);
				for (Iterator it = usersList.iterator(); it.hasNext();) {
					User user = (User) it.next();
					//fetch again the user so that its attributes are set
					addUser(user.getUsername());
				}
			} else {
				for (int i = 0; i < userNames.length; i++) {
					addUser(userNames[i]);
				}
			}
		}
	}

	protected void addUser(String userName) {
		User user = getUser(userName);
		users.put(userName, user);
	}

	protected User getUser(String userName) {
		User user = configuration.getAuthorityService().getUser(executionContext, userName);
		if (user == null) {
			throw new JSException("jsexception.no.such.user", new Object[] {user});
		}
		return user;
	}

	protected void initRoles() {
		includeRoleUsers = hasParameter(getIncludeRoleUsersArgument());

		roles = new LinkedHashMap();
		if (exportEverything || hasParameter(getRolesArgument())) {
			String[] roleNames = getParameterValues(getRolesArgument());
			if (exportEverything || roleNames == null) {
				List rolesList = configuration.getAuthorityService().getRoles(executionContext, null);
				for (Iterator it = rolesList.iterator(); it.hasNext();) {
					Role role = (Role) it.next();
					addRole(role);
				}
			} else {
				for (int i = 0; i < roleNames.length; i++) {
					addRole(roleNames[i]);
				}
			}
		}
	}
	
	protected void addRole(Role role) {
		if (roles.put(role.getRoleName(), role) == null) {
			addRoleUsers(role);
		}
	}

	protected void addRole(String name) {
		if (!roles.containsKey(name)) {
			Role role = getRole(name);
			roles.put(name, role);
			addRoleUsers(role);
		}		
	}

	protected Role getRole(String name) {
		Role role = configuration.getAuthorityService().getRole(executionContext, name);
		if (role == null) {
			throw new JSException("jsexception.no.such.role", new Object[] {role});
		}
		return role;
	}

	protected void addRoleUsers(Role role) {
		if (includeRoleUsers) {
			String roleName = role.getRoleName();
			List usersInRole = configuration.getAuthorityService().getUsersInRole(executionContext, roleName);
			if (usersInRole != null && !usersInRole.isEmpty()) {
				for (Iterator userIt = usersInRole.iterator(); userIt.hasNext();) {
					User user = (User) userIt.next();
					//fetch again the user so that its attributes are set
					addUser(user.getUsername());
				}
			}
		}		
	}

	protected boolean hasUsers() {
		return !users.isEmpty();
	}
	
	protected boolean hasRoles() {
		return !roles.isEmpty();
	}

	protected boolean isToProcess() {
		return hasRoles() || hasUsers();
	}
	
	public void process() {
		if (hasUsers()) {
			exportUsers();
		}
		
		if (hasRoles()) {
			exportRoles();
		}
	}

	protected void exportRoles() {
		mkdir(configuration.getRolesDirName());
		
		for (Iterator it = roles.values().iterator(); it.hasNext();) {
			Role role = (Role) it.next();
			exportRole(role);
		}
	}

	protected void exportRole(Role role) {
		commandOut.info("Exporting role " + role.getRoleName());

		RoleBean roleBean = new RoleBean();
		roleBean.copyFrom(role);
		
		serialize(roleBean, configuration.getRolesDirName(), getRoleFile(role), configuration.getSerializer());
		
		addIndexElement(role);
	}

	protected void addIndexElement(Role role) {
		Element roleElement = getIndexElement().addElement(configuration.getRoleIndexElementName());
		roleElement.setText(role.getRoleName());
	}

	protected void exportUsers() {
		mkdir(configuration.getUsersDirName());
		
		for (Iterator it = users.values().iterator(); it.hasNext();) {
			User user = (User) it.next();
			export(user);
		}
	}

	protected void export(User user) {
		commandOut.info("Exporting user " + user.getUsername());

		UserBean userBean = new UserBean();
		userBean.copyFrom(user);
		
		serialize(userBean, configuration.getUsersDirName(), getUserFile(user), configuration.getSerializer());
		
		addIndexElement(user);
	}

	protected void addIndexElement(User user) {
		Element userElement = getIndexElement().addElement(configuration.getUserIndexElementName());
		userElement.setText(user.getUsername());
	}

	protected String getUserFile(User user) {
		return user.getUsername() + ".xml";
	}

	protected String getRoleFile(Role role) {
		return role.getRoleName() + ".xml";
	}

}
