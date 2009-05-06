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
package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate;

import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author swood
 * @version $Id: RepoRole.java 9116 2007-07-17 00:04:55Z sbirney $
 *
 * @hibernate.class table="Role"
 */
public class RepoRole implements Role, IdedObject {

	private long id;
	private String roleName;
	private boolean externallyDefined = false;
	private Set users = new HashSet();
        private List attributes = null;

	/**
	 * @return
	 * @hibernate.id type="long" column="id" generator-class="identity"
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @hibernate.property
	 * 		column="rolename" type="string" length="100" not-null="true" unique="true"
	 *
     * (non-Javadoc)
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

	/**
	 * @hibernate.property
	 * 		column="externallyDefined" type="boolean"
	 *
	 * @return Returns the externallyDefined.
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

	public void addUser(User aUser)
	{
		// doesn't need implementing
	}

	public void removeUser(User aUser)
	{
		// doesn't need implementing
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


	public void copyFromClient(Object obj, PersistentObjectResolver resolver) {
		Role r = (Role) obj;
		// r -> this
		setRoleName(r.getRoleName());
		setExternallyDefined(r.isExternallyDefined());
//		Set users = r.getUsers();
//		Set repoUsers = getUsers();
//		repoUsers.clear();
//		for (Iterator it = users.iterator(); it.hasNext();) {
//			RepoUser user = (RepoUser) resolver.getPersistentObject(it.next());
//			repoUsers.add(user);
//		}
	}

	public Object toClient(ResourceFactory clientMappingFactory) {

		Role r = (Role) clientMappingFactory.newObject(Role.class);
		// this -> r
		r.setRoleName(getRoleName());
		r.setExternallyDefined(isExternallyDefined());
		return r;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("roleId", getId())
			.append("roleName", getRoleName())
			.toString();
	}

    public boolean equals(Object other) {
        if ( !(other instanceof RepoRole) ) return false;
        RepoRole castOther = (RepoRole) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

	public Set getUsers() {
		return users;
	}

	public void setUsers(Set users) {
		this.users = users;
	}

}
