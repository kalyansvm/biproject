/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.jaspersoft.jasperserver.api.common.service.ServletContextInformation;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ServletContextWrapper.java 9931 2007-09-06 16:31:57Z tdanciu $
 */
public class ServletContextWrapper implements ServletContextAware, ServletContextInformation {

	private ServletContext servletContext;
	private String jspPathPrefix;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public boolean jspExists(String path) {
		String finalPath = getJspPathPrefix() + path;
		try {
			URL resource = servletContext.getResource(finalPath);
			return resource != null;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public String getJspPathPrefix() {
		return jspPathPrefix;
	}

	public void setJspPathPrefix(String pathPrefix) {
		this.jspPathPrefix = pathPrefix;
	}
	
}
