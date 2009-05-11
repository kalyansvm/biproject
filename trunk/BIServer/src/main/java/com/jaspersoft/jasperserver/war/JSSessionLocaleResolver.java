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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JSSessionLocaleResolver implements LocaleResolver
{
	public Locale resolveLocale(HttpServletRequest request)
	{
		String sessionAttribute = JasperServerConstImpl.getUserLocaleSessionAttr();
		Locale locale = (Locale) request.getSession().getAttribute(sessionAttribute);
		if (locale == null)
			locale = request.getLocale();
		return locale;
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale)
	{
		String sessionAttribute = JasperServerConstImpl.getUserLocaleSessionAttr();
		request.getSession().setAttribute(sessionAttribute, locale);
	}
}
