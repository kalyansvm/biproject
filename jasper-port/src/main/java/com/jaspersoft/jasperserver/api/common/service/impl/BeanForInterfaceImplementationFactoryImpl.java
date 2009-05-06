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
package com.jaspersoft.jasperserver.api.common.service.impl;

import java.util.Map;

import com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory;
import com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceImplementationFactory;

/**
 * @author swood
 *
 */
public class BeanForInterfaceImplementationFactoryImpl implements BeanForInterfaceImplementationFactory {

	private BeanForInterfaceFactory factory = null;
	private Map beanForInterfaceMappings = null;
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceImplementationFactory#getBean(java.lang.Class)
	 */
	public Object getBean(Class itfClass) {
		return factory.getBean(getBeanForInterfaceMappings(), itfClass);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceImplementationFactory#getBeanName(java.lang.Class)
	 */
	public String getBeanName(Class itfClass) {
		return factory.getBeanName(getBeanForInterfaceMappings(), itfClass);
	}

	/**
	 * @return Returns the factory.
	 */
	public BeanForInterfaceFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory The factory to set.
	 */
	public void setFactory(BeanForInterfaceFactory factory) {
		this.factory = factory;
	}

	/**
	 * @return Returns the beanForInterfaceMappings.
	 */
	public Map getBeanForInterfaceMappings() {
		return beanForInterfaceMappings;
	}

	/**
	 * @param beanForInterfaceMappings The beanForInterfaceMappings to set.
	 */
	public void setBeanForInterfaceMappings(Map beanForInterfaceMappings) {
		this.beanForInterfaceMappings = beanForInterfaceMappings;
	}

}
