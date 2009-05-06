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
package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AcegiSecurityContextProvider.java 8408 2007-05-29 23:29:12Z melih $
 */
public class AcegiSecurityContextProvider implements SecurityContextProvider {
	
	private UserDetailsService userDetailsService;
	private UserAuthorityService userAuthorityService;

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public UserAuthorityService getUserAuthorityService() {
		return userAuthorityService;
	}

	public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
		this.userAuthorityService = userAuthorityService;
	}

	public String getContextUsername() {
		Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
		if (authenticationToken == null) {
			return null;
		}
		
		if (authenticationToken.getPrincipal() instanceof UserDetails) {
			UserDetails contextUserDetails = (UserDetails) authenticationToken.getPrincipal();
			return contextUserDetails.getUsername();
		} else if (authenticationToken.getPrincipal() instanceof String) {
			return (String) authenticationToken.getPrincipal();
		} else {
			return null;
		}
	}
	
	public User getContextUser() {
		String username = getContextUsername();
		if (username == null) {
			return null;
		}
		return getUserAuthorityService().getUser(null, username);//TODO context
	}

	public void setAuthenticatedUser(String username) {
		UserDetails userDetails = getUserDetailsService().loadUserByUsername(username);
		String quotedUsername = "\"" + username + "\"";
		if (userDetails == null) {
			throw new JSException("jsexception.user.not.found", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isAccountNonExpired()) {
			throw new JSException("jsexception.user.expired", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isAccountNonLocked()) {
			throw new JSException("jsexception.user.locked", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isCredentialsNonExpired()) {
			throw new JSException("jsexception.user.credentials.are.expired", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isEnabled()) {
			throw new JSException("jsexception.user.disabled", new Object[] {quotedUsername});
		}
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	public void revertAuthenticatedUser() {
		// TODO revert to previous principal
		SecurityContextHolder.getContext().setAuthentication(null);
	}

}
