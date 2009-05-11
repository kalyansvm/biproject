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
package com.jaspersoft.jasperserver.war.control;


import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.jaspersoft.jasperserver.api.common.util.TimeZonesList;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl;
import com.jaspersoft.jasperserver.war.common.HeartbeatBean;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.LocalesList;


/**
 * @author aztec
 * @version $Id: JSCommonController.java 13018 2008-04-16 16:07:52Z tdanciu $
 */
public class JSCommonController extends JRBaseMultiActionController {

	protected HeartbeatBean heartbeat;

	private LocalesList locales;
	private TimeZonesList timezones;
	private String allowUserPasswordChange;
	private String passwordExpirationInDays;

	private static Log log = LogFactory.getLog(JSCommonController.class);

	/*
	 * Overridden method for handling the requests
	 * @args HttpServletRequest, HttpServletResponse
	 * @returns ModelAndView - Home Page
	 */
	public ModelAndView homePage(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		return new ModelAndView("home");
	}

	public ModelAndView login(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		Cookie[] cookies = req.getCookies();
		String locale = null;
		String preferredTz = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(JasperServerConstImpl.getUserLocaleSessionAttr()))
					locale = cookie.getValue();
				if (cookie.getName().equals(JasperServerConstImpl.getUserTimezoneSessionAttr()))
					preferredTz = cookie.getValue();
			}
		}
		
		Locale displayLocale = req.getLocale();
		String preferredLocale;
		if (locale == null || locale.length() == 0) {
			preferredLocale = displayLocale.toString();
		} else {
			preferredLocale = locale;
		}

		if (preferredTz == null) {
			preferredTz = timezones.getDefaultTimeZoneID();
		}
		
		req.setAttribute("preferredLocale", preferredLocale);
		req.setAttribute("userLocales", locales.getUserLocales(displayLocale));
		req.setAttribute("preferredTimezone", preferredTz);
		req.setAttribute("userTimezones", timezones.getTimeZones(displayLocale));
		try {
			if (Integer.parseInt(passwordExpirationInDays) > 0) {
			   allowUserPasswordChange = "true";
			}
		} catch (NumberFormatException e) {
			// if the value is NaN, then assume it's non postive.
            // not overwrite allowUserPasswordChange
		}
		req.setAttribute("allowUserPasswordChange", allowUserPasswordChange);
	    req.setAttribute("passwordExpirationInDays", passwordExpirationInDays);

		return new ModelAndView("login");
	}
	
	public ModelAndView login_welcome(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException {
	    Cookie[] cookies = req.getCookies();
	    String locale = null;
	    String preferredTz = null;
	    if (cookies != null) {
		   for (int i = 0; i < cookies.length; i++) {
			   Cookie cookie = cookies[i];
			   if (cookie.getName().equals(JasperServerConstImpl.getUserLocaleSessionAttr()))
				  locale = cookie.getValue();
			   if (cookie.getName().equals(JasperServerConstImpl.getUserTimezoneSessionAttr()))
				  preferredTz = cookie.getValue();
		   }
	    }
	
	    Locale displayLocale = req.getLocale();
	    String preferredLocale;
	    if (locale == null || locale.length() == 0) {
		   preferredLocale = displayLocale.toString();
	    } else {
		   preferredLocale = locale;
	    }

	    if (preferredTz == null) {
		   preferredTz = timezones.getDefaultTimeZoneID();
	    }
	
	    req.setAttribute("preferredLocale", preferredLocale);
	    req.setAttribute("userLocales", locales.getUserLocales(displayLocale));
	    req.setAttribute("preferredTimezone", preferredTz);
	    req.setAttribute("userTimezones", timezones.getTimeZones(displayLocale));
		try {
			if (Integer.parseInt(passwordExpirationInDays) > 0) {
			   allowUserPasswordChange = "true";
			}
		} catch (NumberFormatException e) {
			// if the value is NaN, then assume it's non postive.
            // not overwrite allowUserPasswordChange
		}
	    req.setAttribute("allowUserPasswordChange", allowUserPasswordChange);
	    req.setAttribute("passwordExpirationInDays", passwordExpirationInDays);

	    return new ModelAndView("login_welcome");
}

	public ModelAndView login_oem(HttpServletRequest req, HttpServletResponse res)
    throws ServletException {
    Cookie[] cookies = req.getCookies();
    String locale = null;
    String preferredTz = null;
    if (cookies != null) {
	   for (int i = 0; i < cookies.length; i++) {
		   Cookie cookie = cookies[i];
		   if (cookie.getName().equals(JasperServerConstImpl.getUserLocaleSessionAttr()))
			  locale = cookie.getValue();
		   if (cookie.getName().equals(JasperServerConstImpl.getUserTimezoneSessionAttr()))
			  preferredTz = cookie.getValue();
	   }
    }

    Locale displayLocale = req.getLocale();
    String preferredLocale;
    if (locale == null || locale.length() == 0) {
	   preferredLocale = displayLocale.toString();
    } else {
	   preferredLocale = locale;
    }

    if (preferredTz == null) {
	   preferredTz = timezones.getDefaultTimeZoneID();
    }

    req.setAttribute("preferredLocale", preferredLocale);
    req.setAttribute("userLocales", locales.getUserLocales(displayLocale));
    req.setAttribute("preferredTimezone", preferredTz);
    req.setAttribute("userTimezones", timezones.getTimeZones(displayLocale));
	try {
		if (Integer.parseInt(passwordExpirationInDays) > 0) {
		   allowUserPasswordChange = "true";
		}
	} catch (NumberFormatException e) {
		// if the value is NaN, then assume it's non postive.
        // not overwrite allowUserPasswordChange
	}
    req.setAttribute("allowUserPasswordChange", allowUserPasswordChange);
    req.setAttribute("passwordExpirationInDays", passwordExpirationInDays);

    return new ModelAndView("login_oem");
}
	
	
	public ModelAndView heartbeat(HttpServletRequest req, HttpServletResponse res) throws ServletException 
	{
		boolean isCallPermitted = false;
		String permit = req.getParameter("permit");
		if (permit != null)
		{
			isCallPermitted =  Boolean.valueOf(permit).booleanValue();
		}
		
		heartbeat.permitCall(isCallPermitted);
		
		return new ModelAndView("home");
	}
	

    public ModelAndView exitUser(HttpServletRequest req, HttpServletResponse res) {
	String redirectURL = "/logout.html" + "?" + "showPasswordChange="+req.getParameter("showPasswordChange");
	if (UserAuthorityServiceImpl.isUserSwitched()) {
	    redirectURL = "/j_acegi_exit_user";
	}
	return new ModelAndView("redirect:" + redirectURL);
    }

	public ModelAndView logout(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		// invalidate session
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		// we aren't using RememberMe but this is how we'd log out if we did
		// Cookie terminate = new Cookie(TokenBasedRememberMeServices.ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE_KEY, null);
		// terminate.setMaxAge(0);
		// res.addCookie(terminate);
		SecurityContextHolder.clearContext(); //invalidate authentication
		return new ModelAndView("redirect:/login.html" + "?" + "showPasswordChange="+req.getParameter("showPasswordChange"));
	}

	public ModelAndView loginError(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		log.warn("There was a login error");
		HttpSession session = req.getSession();
		return new ModelAndView("loginError");
	}

	/*
	 * @args req, res
	 * @returns ModelAndView - menutest.jsp
	 */
	public ModelAndView menuTest(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		return new ModelAndView("menutest");
	}


	public LocalesList getLocales()
	{
		return locales;
	}

	public void setLocales(LocalesList locales)
	{
		this.locales = locales;
	}

	public TimeZonesList getTimezones()
	{
		return timezones;
	}

	public void setTimezones(TimeZonesList timezones)
	{
		this.timezones = timezones;
	}

	public String getAllowUserPasswordChange()
	{
		return allowUserPasswordChange;
	}

	public void setAllowUserPasswordChange(String changePassword)
	{
		this.allowUserPasswordChange = changePassword;
	}

	public String getPasswordExpirationInDays() {
		return passwordExpirationInDays;
	}

	public void setPasswordExpirationInDays(String passwordExpirationInDays) {
		this.passwordExpirationInDays = passwordExpirationInDays;
	}
	
	public HeartbeatBean getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(HeartbeatBean heartbeat) {
		this.heartbeat = heartbeat;
	}
	
	
}
