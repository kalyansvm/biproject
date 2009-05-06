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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;

/**
 * @author tkavanagh
 * @version $Id: RoleBean.java 8408 2007-05-29 23:29:12Z melih $
 */
public class RoleBean {

	private String roleName;
	private boolean externallyDefined = false;

	public void copyFrom(Role role) {
		setRoleName(role.getRoleName());
		setExternallyDefined(role.isExternallyDefined());
	}

	public void copyTo(Role role) {
		role.setRoleName(getRoleName());
		role.setExternallyDefined(isExternallyDefined());
	}
	
	public boolean isExternallyDefined() {
		return externallyDefined;
	}
	
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
}
