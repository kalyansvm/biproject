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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;

/**
 * To be used as part of an Acegi FilterChainProxy.
 * 
 * An authentication can exist that is not based on our internal security mechanism, such
 * as using an external LDAP service. This filter will assume that the external authentication
 * is what is wanted, and will:
 * 
 * <ul>
 *     <li>create a user in the metadata if it does not exist, adding any default internal roles</li>
 *     <li>synchronize the external roles with the user profile, adding and removing external roles</li>
 * </ul>
 *     
 * @author swood
 *
 */
public class MetadataAuthenticationProcessingFilter implements Filter, InitializingBean {

	private static Log log = LogFactory.getLog(MetadataAuthenticationProcessingFilter.class);

	protected ExternalUserService externalUserService;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(externalUserService);
    }

    /**
     * Does nothing - we reply on IoC lifecycle services instead.
     *
     * @param ignored not used
     *
     * @throws ServletException DOCUMENT ME!
     */
    public void init(FilterConfig ignored) throws ServletException {}

    /**
     * Does nothing - we reply on IoC lifecycle services instead.
     */
    public void destroy() {}

    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (log.isDebugEnabled()) {
        	if (auth == null) {
        		log.debug("No authentication token");
        	} else {
        		log.debug("Authentication token: '" + auth + "'");
        	}
        }
        // If we have authenticated, but not against an internal metadata store,
        // synch up with a possibly new user in our repository

        if (auth != null && auth.getPrincipal() != null &&
        		!(auth.getPrincipal() instanceof MetadataUserDetails)) {

            String principalName = null;
            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                principalName = userDetails.getUsername();

                getExternalUserService().maintainInternalUser(userDetails, auth.getAuthorities());
            } else if (auth.getPrincipal() instanceof String) {
                
                principalName = (String) auth.getPrincipal();
                getExternalUserService().maintainInternalUser(principalName, auth.getAuthorities());
            } else {
                throw new JSException("Cannot synchronize user details. Unknown principal class: " + 
                           auth.getPrincipal().getClass().getName());
            }

            getExternalUserService().makeUserLoggedIn(principalName);

            if (log.isDebugEnabled()) {
                log.debug(
                    "Populated SecurityContextHolder with JS authentication: '"
                    + SecurityContextHolder.getContext().getAuthentication()
                    + "'");
            }

            Authentication newAuth = SecurityContextHolder.getContext().getAuthentication();
            
            // The authentication can be null if there are no roles. This sets the anonymous user as the
            // logged-in user, if the anonymousUserFilter is in the chain
            
            if (newAuth != null && newAuth.getPrincipal() instanceof MetadataUserDetails) {
            	MetadataUserDetails newPrincipal = (MetadataUserDetails) newAuth.getPrincipal();

            	// Keep a hold of the original principal: it may be useful
            	// later
            	newPrincipal.setOriginalAuthentication(auth);
            }
        }
        
        chain.doFilter(request, response);

        if (log.isDebugEnabled()) {
            log.debug(
                "After chain, JI metadata token is: '"
                + SecurityContextHolder.getContext().getAuthentication()
                + "'");
        }
    }


	public ExternalUserService getExternalUserService() {
		return externalUserService;
	}

	public void setExternalUserService(ExternalUserService externalUserService) {
		this.externalUserService = externalUserService;
	}

}
