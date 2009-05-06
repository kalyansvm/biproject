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
package com.jaspersoft.jasperserver.war;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.acegisecurity.context.SecurityContextHolder;

import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import javax.servlet.RequestDispatcher;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class UserPreferencesFilter implements Filter
{
	private static String USER_LOCALE_PARAM = "userLocale";
	private static String USER_TIMEZONE_PARAM = "userTimezone";
	private static String USER_NAME = "j_username";
	private static String USER_PASSWORD = "j_newpassword1";

	private int cookieAge;
	UserAuthorityService userService;
	
	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}


	public void init(FilterConfig config) throws ServletException
	{
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		String userLocale = request.getParameter(USER_LOCALE_PARAM);
		String userTimezone = request.getParameter(USER_TIMEZONE_PARAM);

		if (SecurityContextHolder.getContext().getAuthentication() == null) {		
			session.removeAttribute("js_uname");
			session.removeAttribute("js_upassword");			
		}
		
		if (session.getAttribute(JasperServerConstImpl.getUserLocaleSessionAttr()) == null
				&& userLocale != null && userLocale.length() > 0 ) {
			Locale locale = LocaleHelper.getInstance().getLocale(userLocale);
			session.setAttribute(JasperServerConstImpl.getUserLocaleSessionAttr(), locale);
			Cookie cookie = new Cookie(JasperServerConstImpl.getUserLocaleSessionAttr(), userLocale);
			cookie.setMaxAge(cookieAge);
			((HttpServletResponse)response).addCookie(cookie);
		}

		if (session.getAttribute(JasperServerConstImpl.getUserTimezoneSessionAttr()) == null
				&& userTimezone != null && userTimezone.length() > 0) {
			session.setAttribute(JasperServerConstImpl.getUserTimezoneSessionAttr(), userTimezone);
			Cookie cookie = new Cookie(JasperServerConstImpl.getUserTimezoneSessionAttr(), userTimezone);
			cookie.setMaxAge(cookieAge);
			((HttpServletResponse)response).addCookie(cookie);
		}
		

		String userName = request.getParameter(USER_NAME);
		String userNewPassword = request.getParameter(USER_PASSWORD);		
		String passwordExpiredDays = request.getParameter("passwordExpiredDays");

		String testFilter = (String)session.getAttribute("js_uname");

		if (testFilter == null) {		
			
           if (userName != null) {
        	  session.setAttribute("js_uname", userName);
           }
           if (userNewPassword != null) {
        	  session.setAttribute("js_upassword", userNewPassword);
           }
           if (passwordExpiredDays != null) {
        	  session.setAttribute("passwordExpiredDays", passwordExpiredDays);
           }
		} else {	
		   userName = (String)session.getAttribute("js_uname");
		   userNewPassword = (String)session.getAttribute("js_upassword");
		   if (userNewPassword != null) {
		      if (!("".equals(userNewPassword.trim()))) {
			  	 User user = userService.getUser(null, userName);
				 user.setPassword(userNewPassword);
				 // reset password timer
				 user.setPreviousPasswordChangeTime(new Date());
				 userService.putUser(null, user);			 
				 session.removeAttribute("js_uname");
				 session.removeAttribute("js_upassword");	
				 session.removeAttribute("passwordExpiredDays");
				 chain.doFilter(request, response);
				 return;
		      }
		   }
		   session.removeAttribute("js_uname");
		   session.removeAttribute("js_upassword");		
           // check if password expired, if so, log off user and go back to login page and show the password change UI	
		   String nDate = (String)session.getAttribute("passwordExpiredDays");		   
		   if (nDate != null) {
			  int totalDate = 0;
			  try {
				 totalDate = Integer.parseInt(nDate); 
			  } catch (NumberFormatException e) {
				 // do nothing, then 0
			  }
			  if (totalDate > 0) {
		         if (userService.isPasswordExpired(null, userName, totalDate)) {
			        // log user off and show password change UI
                   RequestDispatcher rd = request.getRequestDispatcher("/exituser.html?showPasswordChange=true");
                   rd.forward(request, response);
                   return;
		         }
			  }
		   }
		   session.removeAttribute("passwordExpiredDays");	
   
		}
		chain.doFilter(request, response);
	}

	public void destroy()
	{
	}

	public int getCookieAge()
	{
		return cookieAge;
	}

	public void setCookieAge(int cookieAge)
	{
		this.cookieAge = cookieAge;
	}
}
