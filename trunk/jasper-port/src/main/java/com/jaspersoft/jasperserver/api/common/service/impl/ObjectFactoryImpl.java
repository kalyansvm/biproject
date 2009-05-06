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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.service.ObjectFactory;

/**
 * @author swood
 *
 */
public class ObjectFactoryImpl extends BaseClassMappings implements ObjectFactory {
	private static final Log log = LogFactory.getLog(ObjectFactoryImpl.class);
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getImplementationClass(java.util.Map, java.lang.Class)
	 */

	public Object newObject(Map classMappings, Class _class) {
		Class implementationClass = getImplementationClass(classMappings, _class);
		if (implementationClass == null) {
			throw new JSException("jsexception.implementation.class.not.found", new Object[] {_class.getName()});
		}
		
		try {
			return implementationClass.newInstance();
		} catch (InstantiationException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		} catch (IllegalAccessException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	
	
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#newObject(java.util.Map, java.lang.Class)
	 */

	public Class getImplementationClass(Map classMappings, Class itfClass) {
		String implName = (String) getClassMapping(classMappings, itfClass);
		Class implClass = null;
		if (implName != null) {
			try {
				implClass = Class.forName(implName, true, Thread.currentThread().getContextClassLoader());
			} catch (ClassNotFoundException e) {
				throw new JSExceptionWrapper(e);
			}
		}
		return implClass;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getImplementationClassName(java.util.Map, java.lang.Class)
	 */
	public String getImplementationClassName(Map classMappings, Class itfClass) {
		Class _class = getImplementationClass(classMappings, itfClass);
		if (_class == null) {
			return null;
		} else {
			return _class.getName();
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getInterface(java.util.Map, java.lang.Class)
	 */
	public Class getInterface(Map classMappings, Class implClass) {
		if (classMappings == null) {
			return null;
		}
		
		//TODO cache
		try {
			Class interfaceClass = null;
			for (Iterator it = classMappings.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String itfName = (String) entry.getKey();
				String implName = (String) entry.getValue();
				Class itf = Class.forName(itfName, true, Thread.currentThread().getContextClassLoader());
				
				/*
				 * We have some classes coming from Hibernate that are proxies.
				 * These have a class name of <class name>$$ blah blah.
				 * Match the first part of the class name to handle these.
				 */
				if (implClass.getName().startsWith(implName, 0)) {
					// TODO maybe we should do this check too. It makes sense, but
					// does not work.
					
					//  && implClass.isAssignableFrom(itf)
					interfaceClass = itf;
					break;
				}
			}

			return interfaceClass;
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getInterfaceName(java.util.Map, java.lang.Class)
	 */
	public String getInterfaceName(Map classMappings, Class implClass) {
		Class _class = getInterface(classMappings, implClass);
		if (_class == null) {
			return null;
		} else {
			return _class.getName();
		}
	}




	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getImplementationClass(java.util.Map, java.lang.String)
	 */
	public Class getImplementationClass(Map classMappings, String id) {
		if (classMappings == null) {
			return null;
		}
		try {
			
			String implName = (String) classMappings.get(id);
			Class implClass = Class.forName(implName, true, Thread.currentThread().getContextClassLoader());

			return implClass;
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}




	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getImplementationClassName(java.util.Map, java.lang.String)
	 */
	public String getImplementationClassName(Map classMappings, String id) {
		return (String) classMappings.get(id);
	}




	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getIdForClass(java.util.Map, java.lang.Class)
	 */
	public String getIdForClass(Map classMappings, Class _class) {
		if (classMappings == null) {
			return null;
		}
		
		for (Iterator it = classMappings.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String id = (String) entry.getKey();
			String implName = (String) entry.getValue();
			
			/*
			 * We have some classes coming from Hibernate that are proxies.
			 * These have a class name of <class name>$$ blah blah.
			 * Match the first part of the class name to handle these.
			 */
			if (_class.getName().startsWith(implName, 0)) {
				// TODO maybe we should do this check too. It makes sense, but
				// does not work.
				
				//  && implClass.isAssignableFrom(itf)
				return id;
			}
		}

		return null;
	}




	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#newObject(java.util.Map, java.lang.String)
	 */
	public Object newObject(Map classMappings, String id) {
		Class implementationClass = getImplementationClass(classMappings, id);
		if (implementationClass == null) {
			throw new JSException("jsexception.implementation.class.not.found", new Object[] {id});
		}
		
		try {
			return implementationClass.newInstance();
		} catch (InstantiationException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		} catch (IllegalAccessException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}




	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ObjectFactory#getKeys(java.util.Map)
	 */
	public List getKeys(Map classMappings) {
		List l = new ArrayList(classMappings.keySet());
		Collections.sort(l);
		return l;
	}

}
