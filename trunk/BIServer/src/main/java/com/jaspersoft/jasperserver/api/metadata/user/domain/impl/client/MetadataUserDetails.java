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
package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.acegisecurity.Authentication;

/**
 * @author swood
 *
 */
public class MetadataUserDetails implements UserDetails, User {

	private Set roleSet;
	private String username = null;
	private String password = null;
	private boolean enabled = false;
	private String fullName = null;
	private String emailAddress = null;
	private boolean externallyDefined = false;
	private Authentication originalAuthentication = null;
	private Date previousPasswordChangeTime = null;
        private List attributes = null;

	/**
	 * 
	 */
	public MetadataUserDetails(User u) {
		super();
		
		setUsername(u.getUsername());
		setPassword(u.getPassword());
		setFullName(u.getFullName());
		setEmailAddress(u.getEmailAddress());
		setExternallyDefined(u.isExternallyDefined());
		setEnabled(u.isEnabled());
		setRoles(u.getRoles());
		setAttributes(u.getAttributes());
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#getAuthorities()
	 */
	public GrantedAuthority[] getAuthorities() {
		Set currentRoles = getRoles();
		
		GrantedAuthority[] authorities = currentRoles == null ? new GrantedAuthority[0] : new GrantedAuthority[currentRoles.size()];
		
		if (currentRoles == null) {
			return authorities;
		}
		
		Iterator it = currentRoles.iterator();
		int i = 0;
		while (it.hasNext()) {
			Role aRole = (Role) it.next();
			authorities[i++] = new GrantedAuthorityImpl(aRole.getRoleName());
		}
		return authorities;
	}

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
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

	/**
	 * @return Returns the fullName.
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

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isAccountNonExpired()
	 */
	public boolean isAccountNonExpired() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isAccountNonLocked()
	 */
	public boolean isAccountNonLocked() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	public boolean isCredentialsNonExpired() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled The enabled to set.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set getRoles() {
		return roleSet;
	}
	
	public void setRoles(Set newRoleSet) {
		roleSet = newRoleSet;
	}
	
	/**
	 * @return Returns the originalAuthentication.
	 */
	public Authentication getOriginalAuthentication() {
		return originalAuthentication;
	}

	/**
	 * @param originalAuthentication The originalAuthentication to set.
	 */
	public void setOriginalAuthentication(Authentication auth) {
		this.originalAuthentication = auth;
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
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#addRole(com.jaspersoft.jasperserver.api.metadata.user.domain.Role)
	 */
	public void addRole(Role aRole) {
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#removeRole(com.jaspersoft.jasperserver.api.metadata.user.domain.Role)
	 */
	public void removeRole(Role aRole) {
	}
	
	public String toString() {
		return "MetadataUserDetails: " + getUsername(); 
	}
	
	public Date getPreviousPasswordChangeTime() {
		return previousPasswordChangeTime;
	}

	public void setPreviousPasswordChangeTime(Date previousPasswordChangeTime) {
		this.previousPasswordChangeTime = previousPasswordChangeTime;
	}
    

}
