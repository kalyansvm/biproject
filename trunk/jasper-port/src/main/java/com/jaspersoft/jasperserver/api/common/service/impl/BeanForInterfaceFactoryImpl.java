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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory;

/**
 * @author swood
 *
 */
public class BeanForInterfaceFactoryImpl implements BeanForInterfaceFactory, ApplicationContextAware {
	private static final Log log = LogFactory.getLog(BeanForInterfaceFactoryImpl.class);
	
	private ApplicationContext ctx;
    
    public void setApplicationContext(ApplicationContext ctx) {
        this.ctx = ctx;
    }
	
	private final Comparator itfComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			Class itf1 = (Class) o1;
			Class itf2 = (Class) o2;
			
			if (itf1.equals(itf2)) {
				return 0;
			} else if (itf2.isAssignableFrom(itf1)) {
				return -1;
			} else if (itf1.isAssignableFrom(itf2)) {
				return 1;
			} else {
				return itf1.getName().compareTo(itf2.getName());
			}
		}
	};

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory#getBean(java.util.Map, java.lang.Class)
	 */
	public String getBeanName(Map interfaceToBeanMappings, Class itfClass) {
		if (interfaceToBeanMappings == null) {
			return null;
		}
		
		//TODO cache
		try {
			SortedSet interfaces = new TreeSet(itfComparator);

			for (Iterator it = interfaceToBeanMappings.keySet().iterator(); it.hasNext();) {
				String itfName = (String) it.next();
				Class itf = Class.forName(itfName, true, Thread.currentThread().getContextClassLoader());
				if (itf.isAssignableFrom(itfClass)) {
					interfaces.add(itf);
				}
			}

			if (!interfaces.isEmpty()) {
				Class itf = (Class) interfaces.iterator().next();
				return (String) interfaceToBeanMappings.get(itf.getName());
			}
			return null;
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.BeanForInterfaceFactory#getBeanName(java.util.Map, java.lang.Class)
	 */
	public Object getBean(Map classToBeanMappings, Class _class) {
		
		String beanName = getBeanName(classToBeanMappings, _class);
		
		/*
		 * TODO Leave this to the caller to handle?
		 */
		if (beanName == null) {
			throw new JSException("jsexception.bean.name.not.found.for.interface", new Object[] {_class.getName()});
		}
		
		Object bean = ctx.getBean(beanName);
		
		/*
		 * TODO Leave this to the caller to handle?
		 */
		if (bean == null) {
			throw new JSException("jsexception.bean.no.name", new Object[] {beanName});
		}
		
		return bean;
	}

}
