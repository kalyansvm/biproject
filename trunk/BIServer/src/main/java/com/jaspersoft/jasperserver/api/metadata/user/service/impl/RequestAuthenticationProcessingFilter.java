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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.acegisecurity.ui.AbstractProcessingFilter;
import org.acegisecurity.ui.savedrequest.SavedRequest;
import org.acegisecurity.util.PortResolver;
import org.acegisecurity.util.PortResolverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class RequestAuthenticationProcessingFilter extends AbstractProcessingFilter {
	
	private static final Log log = LogFactory.getLog(RequestAuthenticationProcessingFilter.class);
	
	public static final String REQUEST_AUTHENTICATION_ID = "REQUEST_AUTHENTICATION_ID"; 

	private PortResolver portResolver = new PortResolverImpl();
	
	public RequestAuthenticationProcessingFilter() {
		super();
	}

    public void afterPropertiesSet() throws Exception {
       Assert.hasLength(getDefaultTargetUrl(), "defaultTargetUrl must be specified");
        Assert.hasLength(getAuthenticationFailureUrl(),
            "authenticationFailureUrl must be specified");
        Assert.notNull(getAuthenticationManager(),
            "authenticationManager must be specified");
        Assert.notNull(getRememberMeServices());
    }

	/* (non-Javadoc)
	 * @see org.acegisecurity.ui.AbstractProcessingFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest)
	 */
	public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        Map requestParameters = obtainRequestParameters(request);
	    String targetUrl = (String) request.getSession().getAttribute(ACEGI_SAVED_REQUEST_KEY);

        if (log.isDebugEnabled()) {
    		
    	    targetUrl = (String) request.getSession().getAttribute(ACEGI_SAVED_REQUEST_KEY);
		    log.debug("Authenticating with values: '" + requestParameters + "'");
		    log.debug("from URL: " + targetUrl);
        }
        
        Authentication authRequest = new RequestAuthenticationToken(requestParameters);

        // This call to getSession().setAttribute needs to happen, otherwise you get into an
        // infinite loop. Maybe just a getAttribute will work?
        request.getSession().setAttribute(REQUEST_AUTHENTICATION_ID, targetUrl);

        return this.getAuthenticationManager().authenticate(authRequest);
	}

    protected boolean requiresAuthentication(HttpServletRequest request,
            HttpServletResponse response) {
    	return SecurityContextHolder.getContext().getAuthentication() == null;
    	//return obtainRequestParameters(request) != null && obtainRequestParameters(request).size() > 0;
    }


    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, Authentication authResult)
        throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success: " + authResult.toString());
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        if (logger.isDebugEnabled()) {
            logger.debug(
                "Updated SecurityContextHolder to contain the following Authentication: '"
                + authResult + "'");
        }

        String targetUrl = (new SavedRequest(request, portResolver)).getFullRequestUrl();

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to target URL from HTTP Session (or default): " + targetUrl);
        }

        onSuccessfulAuthentication(request, response, authResult);

        getRememberMeServices().loginSuccess(request, response, authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }

        response.sendRedirect(response.encodeRedirectURL(targetUrl));
    }

    
    protected Map obtainRequestParameters(HttpServletRequest request) {
		
		Map result = new HashMap();
		result.putAll(request.getParameterMap());
		
		Enumeration attrs = request.getAttributeNames();
		while (attrs.hasMoreElements()) {
			String attrName = (String) attrs.nextElement();
			result.put(attrName, request.getAttribute(attrName));
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.acegisecurity.ui.AbstractProcessingFilter#getDefaultFilterProcessesUrl()
	 */
	public String getDefaultFilterProcessesUrl() {
        return "/requestAuthentication";
	}

}
