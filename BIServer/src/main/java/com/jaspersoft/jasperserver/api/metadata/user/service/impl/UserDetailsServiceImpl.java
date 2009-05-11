/*
 * Copyright (C) 2005 - 2006 JasperSoft Corporation.  All rights reserved.
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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;

/**
 * @author bklawans from code written by swood
 * @version $Id: UserDetailsServiceImpl.java 9476 2007-08-10 21:21:13Z barry $
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	protected static final Log log = LogFactory.getLog(UserDetailsServiceImpl.class);

	private List defaultInternalRoles;
	private List defaultAdminRoles;
	private List adminUsers;

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		if (adminUsers.contains(username)) {
		    log.debug("User " + username + " is an admin");
		    return detailsFromList(defaultAdminRoles, username);
		}
		
		log.debug("User " + username + " is not an admin, getting default authorities");
		return detailsFromList(defaultInternalRoles, username);
	}

	public void setDefaultInternalRoles(List defaultInternalRoles) {
		this.defaultInternalRoles = defaultInternalRoles;
	}
	
    public void setDefaultAdminRoles(List defaultAdminRoles) {
        this.defaultAdminRoles = defaultAdminRoles;
    }
    
    public void setAdminUsers(List adminUsers) {
        this.adminUsers = adminUsers;
    }
    
    public List getDefaultInternalRoles() {
        return defaultInternalRoles;
    }
    
    public List getDefaultAdminRoles() {
        return defaultAdminRoles;
    }
    
    public List getAdminUsers() {
        return adminUsers;
    }
    
    private UserDetails detailsFromList(List roles, String username) {
		
		GrantedAuthorityImpl[] authorities = roles == null ? new GrantedAuthorityImpl[0] : new GrantedAuthorityImpl[roles.size()];
		
		if (roles == null) {
			return new UserDetailsImpl(authorities, username);
		}
		
		Iterator it = roles.iterator();
		int i = 0;
		while (it.hasNext()) {
			authorities[i++] = new GrantedAuthorityImpl((String) it.next());
		}
		
		return new UserDetailsImpl(authorities, username);
       
    }
    
    public class UserDetailsImpl implements UserDetails {
        private GrantedAuthorityImpl[] authorities;
        private String username;
        
        public UserDetailsImpl(GrantedAuthorityImpl[] authorities, String username) {
            this.authorities = authorities;
            this.username = username;
        }
        
        public GrantedAuthority[] getAuthorities() {
            return authorities;
        }
        
        public String getPassword() {
            return null;
        }
        
        public String getUsername() {
            return username;
        }
        
        public boolean isAccountNonExpired() {
            return true;
        }
        
        public boolean isAccountNonLocked() {
            return true;
        }
        
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        public boolean isEnabled() {
            return true;
        }
    }
}
