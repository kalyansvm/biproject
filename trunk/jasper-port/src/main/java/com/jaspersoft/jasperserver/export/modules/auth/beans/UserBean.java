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
package com.jaspersoft.jasperserver.export.modules.auth.beans;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.export.modules.auth.AuthorityImportHandler;
import com.jaspersoft.jasperserver.export.modules.common.ProfileAttributeBean;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author tkavanagh
 * @version $Id: UserBean.java 12774 2008-04-01 16:21:16Z lucian $
 */
public class UserBean {
	
	private static final CommandOut commandOut = CommandOut.getInstance();
	
	private String username;
	private String fullName;
	private String password;
	private String emailAddress;
	private boolean externallyDefined = false;
	private boolean enabled = false;
	private String[] roleNames;
	private ProfileAttributeBean[] attributes;
	private Date previousPasswordChangeTime;

	public void copyFrom(User user) {
		setUsername(user.getUsername());
		setFullName(user.getFullName());
		setPassword(user.getPassword());
		setEmailAddress(user.getEmailAddress());
		setExternallyDefined(user.isExternallyDefined());
		setEnabled(user.isEnabled());
		setPreviousPasswordChangeTime(user.getPreviousPasswordChangeTime());

		copyRolesFrom(user);
		copyAttributesFrom(user);
	}

	protected void copyRolesFrom(User user) {
		Set roles = user.getRoles();
		String[] names;
		if (roles != null && !roles.isEmpty()) {
			names = new String[roles.size()];
			int c = 0;
			for (Iterator iter = roles.iterator(); iter.hasNext(); ++c) {
				Role role = (Role) iter.next();
				names[c] = role.getRoleName();
			}
		} else {
			names = null;
		}
		setRoleNames(names);
	}

	public void copyAttributesFrom(User user) {
		List userAttributes = user.getAttributes();
		if (userAttributes == null || userAttributes.isEmpty()) {
			attributes = null;
		} else {
			attributes = new ProfileAttributeBean[userAttributes.size()];
			int idx = 0;
			for (Iterator it = userAttributes.iterator(); it
					.hasNext(); ++idx) {
				ProfileAttribute attr = (ProfileAttribute) it.next();
				attributes[idx] = new ProfileAttributeBean();
				attributes[idx].copyFrom(attr);
			}
		}
	}

	public void copyTo(User user, AuthorityImportHandler importHandler) {
		user.setUsername(getUsername());
		user.setFullName(getFullName());
		user.setPassword(getPassword());
		user.setEmailAddress(getEmailAddress());
		user.setExternallyDefined(isExternallyDefined());
		user.setEnabled(isEnabled());
		user.setPreviousPasswordChangeTime(getPreviousPasswordChangeTime());

		copyRolesTo(user, importHandler);
	}
	
	protected void copyRolesTo(User user, AuthorityImportHandler importHandler) {
		Set roles;
		if (roleNames == null) {
			roles = null;
		} else {
			roles = new HashSet();
			for (int i = 0; i < roleNames.length; i++) {
				String roleName = roleNames[i];
				Role role = importHandler.resolveRole(roleName);
				if (role == null) {
					commandOut.warn("Role " + roleName + " not found while copying user " + getUsername() + ", skipping.");
				} else {
					roles.add(role);
				}
			}
		}
		user.setRoles(roles);
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isExternallyDefined() {
		return externallyDefined;
	}
	
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String[] roleNames) {
		this.roleNames = roleNames;
	}

	public ProfileAttributeBean[] getAttributes() {
		return attributes;
	}

	public void setAttributes(ProfileAttributeBean[] attributes) {
		this.attributes = attributes;
	}

	public Date getPreviousPasswordChangeTime() {
		return previousPasswordChangeTime;
	}

	public void setPreviousPasswordChangeTime(Date previousPasswordChangeTime) {
		this.previousPasswordChangeTime = previousPasswordChangeTime;
	}

}
