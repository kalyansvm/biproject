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

import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.common.service.ImplementationObjectFactory;
import com.jaspersoft.jasperserver.api.common.service.ObjectFactory;

/**
 * @author swood
 *
 */
public class ImplementationClassObjectFactoryImpl implements ImplementationObjectFactory {
	
	private Map implementationClassMappings;
	
	private ObjectFactory objectFactory;

	public Map getImplementationClassMappings() {
		return implementationClassMappings;
	}

	public void setImplementationClassMappings(Map implementationClassMappings) {
		this.implementationClassMappings = implementationClassMappings;
	}

	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
	
	public void setObjectFactory(ObjectFactory factoryImpl) {
		this.objectFactory = factoryImpl;
	}
	
	public Class getImplementationClass(Class _class) {
		return getObjectFactory().getImplementationClass(getImplementationClassMappings(), _class);
	}
	
	public Class getImplementationClass(String id) {
		return getObjectFactory().getImplementationClass(getImplementationClassMappings(), id);
	}
	
	public String getImplementationClassName(Class _class) {
		return getObjectFactory().getImplementationClassName(getImplementationClassMappings(), _class);
	}
	
	public String getImplementationClassName(String id) {
		return getObjectFactory().getImplementationClassName(getImplementationClassMappings(), id);
	}
	
	public Class getInterface(Class _class) {
		return getObjectFactory().getInterface(getImplementationClassMappings(), _class);
	}
	
	public String getInterfaceName(Class _class) {
		return getObjectFactory().getInterfaceName(getImplementationClassMappings(), _class);
	}
	
	public String getIdForClass(Class _class) {
		return getObjectFactory().getIdForClass(getImplementationClassMappings(), _class);
	}

	public Object newObject(Class _class) {
		return getObjectFactory().newObject(getImplementationClassMappings(), _class);
	}

	public Object newObject(String id) {
		return getObjectFactory().newObject(getImplementationClassMappings(), id);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.service.ImplementationObjectFactory#getKeys()
	 */
	public List getKeys() {
		return getObjectFactory().getKeys(getImplementationClassMappings());
	}
}
