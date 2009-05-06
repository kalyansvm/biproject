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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.auth.beans.RoleBean;
import com.jaspersoft.jasperserver.export.modules.auth.beans.UserBean;
import com.jaspersoft.jasperserver.export.modules.common.ProfileAttributeBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AuthorityImporter.java 9458 2007-08-10 14:30:48Z lucian $
 */
public class AuthorityImporter extends BaseImporterModule {
	
	private AuthorityModuleConfiguration configuration;
	
	protected static class ImportHandler implements AuthorityImportHandler {

		private final Map roles;
		
		protected ImportHandler(List roles) {
			this.roles = new HashMap();
			
			for (Iterator it = roles.iterator(); it.hasNext();) {
				Role role = (Role) it.next();
				this.roles.put(role.getRoleName(), role);
			}
		}
		
		public Role resolveRole(String rolename) {
			Role role = (Role) roles.get(rolename);
			return role;
		}
		
	}
	
	public void process() {
		importRoles();
		importUsers();
	}

	protected void importRoles() {
		String rolesDir = configuration.getRolesDirName();
		for (Iterator it = indexElement.elementIterator(configuration.getRoleIndexElementName()); it.hasNext(); ) {
			Element roleElement = (Element) it.next();
			String roleName = roleElement.getText();
			String file = getRoleFile(roleName);
			RoleBean roleBean = (RoleBean) deserialize(rolesDir, file, configuration.getSerializer());
			if (alreadyExists(roleBean)) {
				commandOut.warn("Role " + roleBean.getRoleName() + " already exists, skipping.");
			} else {
				Role role = createRole(roleBean);
				saveRole(role);
				
				commandOut.info("Created role " + role.getRoleName());
			}
		}
	}
	
	protected boolean alreadyExists(RoleBean roleBean) {
		return configuration.getAuthorityService().getRole(executionContext, roleBean.getRoleName()) != null;
	}

	protected Role createRole(RoleBean roleBean) {
		Role role = configuration.getAuthorityService().newRole(executionContext);
		roleBean.copyTo(role);
		return role;
	}

	protected void saveRole(Role role) {
		configuration.getAuthorityService().putRole(executionContext, role);
	}

	protected void importUsers() {
		String usersDir = configuration.getUsersDirName();
		for (Iterator it = indexElement.elementIterator(configuration.getUserIndexElementName()); it.hasNext(); ) {
			Element userElement = (Element) it.next();
			String username = userElement.getText();
			String file = getUserFile(username);
			UserBean userBean = (UserBean) deserialize(usersDir, file, configuration.getSerializer());
			if (alreadyExists(userBean)) {
				commandOut.warn("User " + userBean.getUsername() + " already exists, skipping.");
			} else {
				User user = createUser(userBean);
				saveUser(user);
				saveUserAttributes(user, userBean.getAttributes());
				
				commandOut.info("Created user " + userBean.getUsername());
			}
		}
	}

	protected boolean alreadyExists(UserBean userBean) {
		return configuration.getAuthorityService().getUser(executionContext, userBean.getUsername()) != null;
	}

	protected User createUser(UserBean userBean) {
		User user = configuration.getAuthorityService().newUser(executionContext);
		
		List allRoles = configuration.getAuthorityService().getRoles(executionContext, null);
		ImportHandler handler = new ImportHandler(allRoles);
		
		userBean.copyTo(user, handler);		
		return user;
	}
	
	protected void saveUser(User user) {
		configuration.getAuthorityService().putUser(executionContext, user);
	}
	
	protected void saveUserAttributes(User user, ProfileAttributeBean[] attributes) {
		if (attributes != null && attributes.length > 0) {
			for (int i = 0; i < attributes.length; i++) {
				ProfileAttributeBean profileAttributeBean = attributes[i];
				saveUserAttribute(user, profileAttributeBean);
			}
		}
	}

	protected void saveUserAttribute(User user,
			ProfileAttributeBean attributeBean) {
		ProfileAttributeService attributeService = configuration.getAttributeService();
		ProfileAttribute attribute = attributeService.newProfileAttribute(executionContext);
		attribute.setPrincipal(user);
		attributeBean.copyTo(attribute);
		attributeService.putProfileAttribute(executionContext, attribute);
	}

	protected String getUserFile(String username) {
		return username + ".xml";
	}

	protected String getRoleFile(String roleName) {
		return roleName + ".xml";
	}

	public AuthorityModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AuthorityModuleConfiguration configuration) {
		this.configuration = configuration;
	}

}
