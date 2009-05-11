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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.service.impl.BeanForInterfaceImplementationFactoryImpl;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataSourceServiceFactoryImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class DataSourceServiceFactoryImpl extends
		BeanForInterfaceImplementationFactoryImpl implements
		DataSourceServiceFactory, InitializingBean {

	private static final Log log = LogFactory.getLog(DataSourceServiceFactoryImpl.class);
	
	private Map serviceDefinitionMap;
	
	private Set universalTypes;
	private Map languageTypes;
	
	public void afterPropertiesSet() {
		setBeanInterfaceMappings();
		collectLanguageTypes();
	}

	protected void setBeanInterfaceMappings() {
		Map beanItfMap = new HashMap();
		for (Iterator it = serviceDefinitionMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String itf = (String) entry.getKey();
			DataSourceServiceDefinition serviceDef = (DataSourceServiceDefinition) entry.getValue();
			beanItfMap.put(itf, serviceDef.getServiceBeanName());
		}
		setBeanForInterfaceMappings(beanItfMap);
	}

	protected void collectLanguageTypes() {
		languageTypes = new HashMap();
		universalTypes = new HashSet();
		
		for (Iterator it = serviceDefinitionMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String itf = (String) entry.getKey();
			DataSourceServiceDefinition serviceDef = (DataSourceServiceDefinition) entry.getValue();
			
			if (serviceDef.isAnyLanguage()) {
				universalTypes.add(resolveType(itf));
			}
			
			collectTypes(itf, serviceDef);
		}
	}

	protected void collectTypes(String itf, DataSourceServiceDefinition serviceDef) {
		Set languages = serviceDef.getSupportedQueryLanguages();
		if (languages != null) {
			for (Iterator langIt = languages.iterator(); langIt.hasNext();) {
				String language = (String) langIt.next();
				Set langTypes = (Set) languageTypes.get(language);
				if (langTypes == null) {
					langTypes = new HashSet();
					languageTypes.put(language, langTypes);
				}
				langTypes.add(resolveType(itf));
			}
		}
	}

	protected Class resolveType(String itf) {
		try {
			return Class.forName(itf, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	public Set getSupportingDataSourceTypes(String queryLanguage) {
		Set types = new HashSet();
		Set langTypes = (Set) languageTypes.get(queryLanguage);
		if (langTypes != null) {
			types.addAll(langTypes);
		}
		types.addAll(universalTypes);
		return types;
	}

	public Map getServiceDefinitionMap() {
		return serviceDefinitionMap;
	}

	public void setServiceDefinitionMap(Map serviceBeansMap) {
		this.serviceDefinitionMap = serviceBeansMap;
	}

}
