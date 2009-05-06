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
package com.jaspersoft.jasperserver.api.metadata.user.domain.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author swood
 *
 */
public class RoleImpl implements Role, Serializable {
	private String roleName;
	private Set userSet = new HashSet();
	private boolean externallyDefined = false;
        private List attributes = null;

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.Role#getRoleName()
	 */
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String newRoleName) {
//		if (newRoleName == null || newRoleName.trim().length() == 0) {
//			throw new RuntimeException("No role name");
//		}
		roleName = newRoleName;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.Role#getUsers()
	 */
	public Set getUsers() {
		return userSet;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.Role#setUsers(java.util.Set)
	 */
	public void setUsers(Set userSet) {
		this.userSet = userSet;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.Role#isExternallyDefined()
	 */
	public boolean isExternallyDefined() {
		return externallyDefined;
	}

	/**
	 * @param externallyDefined The externallyDefined to set.
	 */
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.domain.AttributedObject#getAttributes()
	 */
	public List getAttributes() {
	    return attributes;
	}

        public void setAttributes(List attrs) {
	    attributes = attrs;
        }


	public String toString() {
		return new ToStringBuilder(this)
			.append("roleName", getRoleName())
			.toString();
	}

	public boolean equals(Object other) {
		if ( !(other instanceof RoleImpl) ) return false;
		RoleImpl castOther = (RoleImpl) other;
		return new EqualsBuilder()
			.append(this.getRoleName(), castOther.getRoleName())
			.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(getRoleName())
			.toHashCode();
	}

	public void addUser(User aUser)
	{
		userSet.add(aUser);
	}

	public void removeUser(User aUser)
	{
		userSet.remove(aUser);
	}
}
