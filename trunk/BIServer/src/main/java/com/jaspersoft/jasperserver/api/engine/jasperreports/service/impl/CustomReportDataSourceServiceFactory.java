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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author swood
 *
 */
public class CustomReportDataSourceServiceFactory implements ReportDataSourceServiceFactory, ApplicationContextAware {
	public static final String PROPERTY_MAP = "propertyMap";
	ApplicationContext ctx;
	private List customDataSourceDefs = new ArrayList();
	
	/**
	 * 
	 */
	public CustomReportDataSourceServiceFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ctx = arg0;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof CustomReportDataSource)) {
			throw new JSException("jsexception.invalid.custom.datasource", new Object[] {reportDataSource.getClass()});
		}
		CustomReportDataSource customDataSource = (CustomReportDataSource) reportDataSource;
		
		// get the service class name, look up the class, and create an instance
		String serviceClassName = customDataSource.getServiceClass();
		ReportDataSourceService service;
		try {
			Class serviceClass = Class.forName(serviceClassName);
			service = (ReportDataSourceService) serviceClass.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JSException ex = new JSException("jsexception.creating.custom.datasource", e);
			ex.setArgs(new Object[] { serviceClassName });
			throw ex;
		}
		try {
			// use spring for introspection help
			BeanWrapperImpl bw = new BeanWrapperImpl(service);
			// get definition
			CustomDataSourceDefinition def = getDefinitionByServiceClass(serviceClassName);
			// use "propertyMap" for passing params if you want that...
			Map propMap = new HashMap();
			// set params
			Iterator pdi = def.getPropertyDefinitions().iterator();
			while (pdi.hasNext()) {
				Map pd = (Map) pdi.next();
				String name = (String) pd.get(CustomDataSourceDefinition.PARAM_NAME);
				Object deflt = pd.get(CustomDataSourceDefinition.PARAM_DEFAULT);
				Object value = customDataSource.getPropertyMap().get(name);
				if (value == null && deflt != null) {
					value = deflt;
				}
				// set prop if it's writeable
				if (value != null) {
					if (bw.isWritableProperty(name)) {
						bw.setPropertyValue(name, value);
					}
					propMap.put(name, value);
				}
			}
			// pass all prop values as map if available
			if (bw.isWritableProperty(PROPERTY_MAP)) {
				bw.setPropertyValue(PROPERTY_MAP, propMap);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JSException ex = new JSException("jsexception.setting.custom.datasource.props", e);
			ex.setArgs(new Object[] { serviceClassName });
			throw ex;
		}
		
		// set params from the custom data source instance
		// tbd...
		// make the param mapper pluggable??
		// done
		return service;
	}

	/**
	 * add a definition to the list of definitions
	 */
	public void addDefinition(CustomDataSourceDefinition def) {
		customDataSourceDefs.add(def);
	}
	
	public List getDefinitions() {
		return customDataSourceDefs ;
	}

	/**
	 * @param serviceClass
	 * @return
	 */
	public CustomDataSourceDefinition getDefinitionByServiceClass(String serviceClass) {
		Iterator cdsi = getDefinitions().iterator();
		while (cdsi.hasNext()) {
			CustomDataSourceDefinition cds = (CustomDataSourceDefinition) cdsi.next();
			if (cds.getServiceClassName().equals(serviceClass)) {
				return cds;
			}
		}
		return null;
	}

	/**
	 * @param serviceClass
	 * @return
	 */
	public CustomDataSourceDefinition getDefinitionByName(String name) {
		Iterator cdsi = getDefinitions().iterator();
		while (cdsi.hasNext()) {
			CustomDataSourceDefinition cds = (CustomDataSourceDefinition) cdsi.next();
			if (cds.getName().equals(name)) {
				return cds;
			}
		}
		return null;
	}

}
