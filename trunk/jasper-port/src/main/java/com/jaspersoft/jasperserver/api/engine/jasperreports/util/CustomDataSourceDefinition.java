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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.util.JRProperties;

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

/**
 * @author bob
 * This class is meant to be instantiated as a spring bean that registers a custom data source with the system.
 *
 */
public class CustomDataSourceDefinition implements Serializable {
	public static final String PARAM_NAME = "name";
	public static final String PARAM_LABEL = "label";
	public static final String PARAM_DEFAULT = "default";
	// this is a param that doesn't get edited, such as a bean; in that case, you need a default
	public static final String PARAM_HIDDEN = "hidden";
	
	private transient CustomReportDataSourceServiceFactory factory;
	private String name;
	private String serviceClassName;
	private CustomDataSourceValidator validator;
	private List propertyDefinitions;
	private Map queryExecuterMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This is always set to the CustomReportDataSourceServiceFactory which is instantiated with the other RDSSF's
	 * in applicationContext.xml.
	 * We then need to register ourselves with that factory so it knows about us.
	 *  
	 * @param factory
	 */
	public void setFactory(CustomReportDataSourceServiceFactory factory) {
		this.factory = factory;
		factory.addDefinition(this);
	}
	
	public CustomReportDataSourceServiceFactory getFactory() {
		return factory;
	}
	
	/**
	 * The name of the custom impl of ReportDataSourceService.
	 * @param serviceClassName
	 */
	public void setServiceClassName(String serviceClassName) {
		this.serviceClassName = serviceClassName;
	}
	
	public String getServiceClassName() {
		return serviceClassName;
	}
	
	/**
	 * (optional) the name of a validator that's used to check the params
	 * @param validatorClassName
	 */
	public void setValidator(CustomDataSourceValidator validator) {
		this.validator = validator;
	}
	
	public CustomDataSourceValidator getValidator() {
		return validator;
	}
	
	/**
	 * a list of parameter defs.
	 * Each param is a map of props. Here are current valid props:
	 *  name: name used to refer to the prop--in particular, used as the key in the persisted custom data source prop map
	 *  label (auto generated): name of a label for this parameter to get out of a message catalog
	 *  type:
	 *  mandatory: 
	 * @param paramDefs
	 */
	public void setPropertyDefinitions(List propertyDefinitions) {
		this.propertyDefinitions = new ArrayList();
		// auto generate labels
		Iterator pdi = propertyDefinitions.iterator();
		while (pdi.hasNext()) {
			Map pd = (Map) pdi.next();
			Map newPd = new HashMap(pd);
			// create label name
			newPd.put(PARAM_LABEL, getParameterLabelName((String) newPd.get(PARAM_NAME)));
			this.propertyDefinitions.add(newPd);
		}
	}
	
	public List getPropertyDefinitions() {
		return propertyDefinitions;
	}

	/**
	 * message name used as label
	 */
	public String getLabelName() {
		return name + ".name";
	}

	/**
	 * message names for params
	 * @param paramName
	 * @return
	 */
	public String getParameterLabelName(String paramName) {
		return name + ".properties." + paramName;
	}

	/**
	 * utility function for the jsp--return just the editable param defs
	 * @return
	 */
	public List getEditablePropertyDefinitions() {
		List list = new ArrayList(propertyDefinitions);
		Iterator pdi = list.iterator();
		while (pdi.hasNext()) {
			Map pd = (Map) pdi.next();
			// if hidden, delete from list
			String hidden = (String) pd.get(CustomDataSourceDefinition.PARAM_HIDDEN);
			if (Boolean.parseBoolean(hidden)) {
				pdi.remove();
			}
		}
		return list;
	}

	/**
	 * Map with query languages (used language attribute of JRXML queryString element)
	 * as keys, and JRQueryExecuterFactory class names as values
	 * @return
	 */
	public Map getQueryExecuterMap() {
		return queryExecuterMap;
	}

	/**
	 * register query executer factories with JR
	 * @param queryExecuterMap
	 */
	public void setQueryExecuterMap(Map queryExecuterMap) {
		this.queryExecuterMap = queryExecuterMap;
		if (queryExecuterMap == null) {
			return;
		}
		Iterator qei = queryExecuterMap.keySet().iterator();
		while (qei.hasNext()) {
			String lang = (String) qei.next();
			String qefClassName = (String) queryExecuterMap.get(lang);
			// set property that will allow jr to look up qe factory
			JRProperties.setProperty(JRProperties.QUERY_EXECUTER_FACTORY_PREFIX + lang, qefClassName);
		}
	}

	/**
	 * Initialize a CustomReportDataSource instance with the defaults specified
	 * @param cds
	 * @param b
	 */
	public void setDefaultValues(CustomReportDataSource cds, boolean includeHidden) {
		if (cds.getPropertyMap() == null) {
			cds.setPropertyMap(new HashMap());
		}
		Iterator pdi = propertyDefinitions.iterator();
		while (pdi.hasNext()) {
			Map pd = (Map) pdi.next();
			String name = (String) pd.get(CustomDataSourceDefinition.PARAM_NAME);
			Object def = pd.get(CustomDataSourceDefinition.PARAM_DEFAULT);
			String hidden = (String) pd.get(CustomDataSourceDefinition.PARAM_HIDDEN);
			if (Boolean.parseBoolean(hidden) && ! includeHidden) {
				continue;
			}
			String value = (String) cds.getPropertyMap().get(name);
			if (value == null) {
				if (def == null) {
					def = "";
				}
				cds.getPropertyMap().put(name, def);
			}
		}
	}
}
