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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.beans.factory.InitializingBean;
import org.acegisecurity.Authentication;


import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

/**
 * @author achan
 *
 */
public class PasswordExpirationProcessingFilter implements Filter, InitializingBean {

	
    private UserAuthorityService userService;
    private String passwordExpirationInDays;

	
	public void doFilter(ServletRequest request, ServletResponse response,
	        FilterChain chain) throws IOException, ServletException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if (auth != null) {
		   // skip password expiration check if from trusted host
		   String fromTrustedHost = (String)request.getAttribute("fromTrustedHost");
		   if ("true".equals(fromTrustedHost)) {
			   request.removeAttribute("fromTrustedHost");
			   chain.doFilter(request, response);
			   return;
		   }
			
		   
		   // get expiration date
		   int nDays = 0;
		   try {
			   nDays = Integer.parseInt(passwordExpirationInDays);
		   } catch (NumberFormatException e) {}		
           if (nDays > 0) {
		      if (userService.isPasswordExpired(null, auth.getName(), nDays)) {
			     SecurityContextHolder.getContext().setAuthentication(null);
			     chain.doFilter(request, response);
			     return;
		      }
           }
		}
		request.removeAttribute("fromTrustedHost");
		chain.doFilter(request, response);
	}
	
    public void afterPropertiesSet() throws Exception {
    }
    
    public void destroy() {}
    
    public void init(FilterConfig arg0) throws ServletException {}

	public UserAuthorityService getUserService() {
		return userService;
	}

	public void setUserService(UserAuthorityService userService) {
		this.userService = userService;
	}

	public String getPasswordExpirationInDays() {
		return passwordExpirationInDays;
	}

	public void setPasswordExpirationInDays(String passwordExpirationInDays) {
		this.passwordExpirationInDays = passwordExpirationInDays;
	}
	
    
	
}
