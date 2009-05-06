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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SpringBeanServletContextPlublisher.java 10092 2007-09-17 12:17:16Z lucian $
 */
public class SpringBeanServletContextPlublisher implements ServletContextListener {

	private final static Log log = LogFactory.getLog(SpringBeanServletContextPlublisher.class); 
	
	private final static String ATTRIBUTE_BEAN_NAMES = "sessionPublishedBeans";

	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		String beanNamesAttr = servletContext.getInitParameter(ATTRIBUTE_BEAN_NAMES);
		String[] beanNames = beanNamesAttr.split("\\,");
		for (int i = 0; i < beanNames.length; i++) {
			String beanName = beanNames[i];
			Object bean = applicationContext.getBean(beanName);
			if (bean == null) {
				log.warn("Bean \"" + beanName + "\" not found");
			} else {
				servletContext.setAttribute(beanName, bean);
				
				if (log.isDebugEnabled()) {
					log.debug("Bean \"" + beanName + "\" published in the application context");
				}
			}
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// NOOP
		
	}

}
