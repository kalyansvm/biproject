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

import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AuthorityModuleConfiguration.java 9458 2007-08-10 14:30:48Z lucian $
 */
public class AuthorityModuleConfiguration {
	
	private UserAuthorityService authorityService;
	private ProfileAttributeService attributeService;
	private String roleIndexElementName;
	private String userIndexElementName;
	private String usersDirName;
	private String rolesDirName;
	private ObjectSerializer serializer;
	
	public UserAuthorityService getAuthorityService() {
		return authorityService;
	}
	
	public void setAuthorityService(UserAuthorityService authorityService) {
		this.authorityService = authorityService;
	}
	
	public String getRolesDirName() {
		return rolesDirName;
	}
	
	public void setRolesDirName(String rolesDirName) {
		this.rolesDirName = rolesDirName;
	}
	
	public ObjectSerializer getSerializer() {
		return serializer;
	}
	
	public void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}
	
	public String getUsersDirName() {
		return usersDirName;
	}
	
	public void setUsersDirName(String usersDirName) {
		this.usersDirName = usersDirName;
	}

	public String getRoleIndexElementName() {
		return roleIndexElementName;
	}

	public void setRoleIndexElementName(String roleIndexElementName) {
		this.roleIndexElementName = roleIndexElementName;
	}

	public String getUserIndexElementName() {
		return userIndexElementName;
	}

	public void setUserIndexElementName(String userIndexElementName) {
		this.userIndexElementName = userIndexElementName;
	}

	public ProfileAttributeService getAttributeService() {
		return attributeService;
	}

	public void setAttributeService(ProfileAttributeService attributeService) {
		this.attributeService = attributeService;
	}

}
