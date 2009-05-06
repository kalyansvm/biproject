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

package com.jaspersoft.jasperserver.api.metadata.user.service;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: UserAuthorityEventListenerProcessor.java 8408 2007-05-29 23:29:12Z melih $
 */
public class UserAuthorityEventListenerProcessor implements BeanPostProcessor {

	private UserAuthorityEventListenerRegistry registry;
	private String listenerBeanName;

	public Object postProcessBeforeInitialization(Object bean, String name) {
		if (name != null && name.equals(getListenerBeanName())) {
			getRegistry().registerListener((UserAuthorityEventListener) bean);
		}
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String name) {
		return bean;
	}

	public String getListenerBeanName() {
		return listenerBeanName;
	}

	public void setListenerBeanName(String listenerBeanName) {
		this.listenerBeanName = listenerBeanName;
	}

	public UserAuthorityEventListenerRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(UserAuthorityEventListenerRegistry registry) {
		this.registry = registry;
	}

}
