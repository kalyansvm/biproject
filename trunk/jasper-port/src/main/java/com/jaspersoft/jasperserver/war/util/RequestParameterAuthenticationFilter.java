/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.war.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.WebAuthenticationDetails;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita
 *
 */
public class RequestParameterAuthenticationFilter implements Filter {

    private static final Log log = LogFactory.getLog(RequestParameterAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;
    private String authenticationFailureUrl;
    private String[] excludeUrls;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    	HttpServletRequest httpRequest = (HttpServletRequest) request;
    	HttpServletResponse httpResponse = (HttpServletResponse) response; 
    	
        if (requiresAuthentication(httpRequest)) {
            String username = request.getParameter(
					AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_USERNAME_KEY);
			String password = request.getParameter(
					AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_PASSWORD_KEY);
			UsernamePasswordAuthenticationToken authRequest = 
				new UsernamePasswordAuthenticationToken(username, password);
			authRequest.setDetails(new WebAuthenticationDetails(httpRequest));

			Authentication authResult;
			try {
			    authResult = authenticationManager.authenticate(authRequest);
			} catch (AuthenticationException e) {
			    if (log.isDebugEnabled()) {
			        log.debug("User " + username + " failed to authenticate: " + e.toString());
			    }

			    SecurityContextHolder.getContext().setAuthentication(null);
			    httpResponse.sendRedirect(httpResponse.encodeRedirectURL(getFullFailureUrl(httpRequest)));
			    return;
			}

			if (log.isDebugEnabled()) {
			    log.debug("User " + username + " authenticated: " + authResult);
			}

			SecurityContextHolder.getContext().setAuthentication(authResult);
        }

        chain.doFilter(request, response);
    }
    
    protected boolean requiresAuthentication(HttpServletRequest request) {
    	boolean authenticate;
        String username = request.getParameter(AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_USERNAME_KEY);
        if (username == null) {
        	authenticate = false;
        } else {
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuth != null
            		&& existingAuth.isAuthenticated()
            		&& existingAuth.getName().equals(username)) {
            	authenticate = false;
            } else {
            	authenticate = !isUrlExcluded(request);
            }
        }
        return authenticate;
    }

	protected boolean isUrlExcluded(HttpServletRequest request) {
		if (excludeUrls == null || excludeUrls.length == 0) {
			return false;
		}
			
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');
        if (pathParamIndex > 0) {
            uri = uri.substring(0, pathParamIndex);
        }

        for (int i = 0; i < excludeUrls.length; i++) {
			String excludedUrl = excludeUrls[i];
			if (uri.endsWith(request.getContextPath() + excludedUrl)) {
				return true;
			}
		}
        return false;
	}

	protected String getFullFailureUrl(HttpServletRequest request) {
    	return request.getContextPath() + authenticationFailureUrl;
    }
    
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

	public String getAuthenticationFailureUrl() {
		return authenticationFailureUrl;
	}

	public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
		this.authenticationFailureUrl = authenticationFailureUrl;
	}

	public String[] getExcludeUrls() {
		return excludeUrls;
	}

	public void setExcludeUrls(String[] excludeUrls) {
		this.excludeUrls = excludeUrls;
	}
	
}
