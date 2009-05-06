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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author swood
 *
 */
public class UserImpl implements User, Serializable {
	
	private Set roleSet = new HashSet();
	private String username = null;
	private String fullName = null;
	private String password = null;
	private String emailAddress = null;
	private boolean externallyDefined = false;
	private boolean enabled = false;
	private Date previousPasswordChangeTime = null;
        private List attributes = null;

	/**
	 * @return Returns the username.
	 * 
	 * (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#getUsername()
	 */
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String newUsername) {
		if (newUsername == null || newUsername.trim().length() == 0) {
			throw new RuntimeException("No user name");
		}
		username = newUsername;
	}

	/**
	 * @return Returns the fullName.
	 * 
	 * (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#getFullName()
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName The fullName to set.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#getPassword()
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the emailAddress.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress The emailAddress to set.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * 
	 * @return Set
	 */
	public Set getRoles() {
		return roleSet;
	}
	
	public void setRoles(Set newRoleSet) {
		roleSet = newRoleSet;
	}

	public void addRole(final Role newRole) {
		/*
		Predicate findRolePredicate = new Predicate() {
			public boolean evaluate(Object o) {
				Role r = (Role) o;
				if (r == null || newRole == null || r.getRoleName() == null || newRole.getRoleName() == null) {
					return false;
				}
				return r.getRoleName().equalsIgnoreCase(newRole.getRoleName());
			}
		};
		Object found = CollectionUtils.find(getRoles(), findRolePredicate);
		*/
		if (newRole != null && !getRoles().contains(newRole)) {
			getRoles().add(newRole);
			// Not for DTO? newRole.getUsers().add(this);
		}
	}

	public void removeRole(final Role removedRole) {
		getRoles().remove(removedRole);
//		 Not for DTO? removedRole.getUsers().remove(this);
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

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#isExternallyDefined()
	 */
	public boolean isExternallyDefined() {
		return externallyDefined;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#setExternallyDefined(boolean)
	 */
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}

	/** 
     *  (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("username", getUsername())
			.toString();
	}

    public boolean equals(Object other) {
        if ( !(other instanceof UserImpl) ) return false;
        UserImpl castOther = (UserImpl) other;
        return new EqualsBuilder()
            .append(this.getUsername(), castOther.getUsername())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getUsername())
            .toHashCode();
    }
    
    
	public Date getPreviousPasswordChangeTime() {
		return previousPasswordChangeTime;
	}

	public void setPreviousPasswordChangeTime(Date previousPasswordChangeTime) {
		this.previousPasswordChangeTime = previousPasswordChangeTime;
	}
    

}
