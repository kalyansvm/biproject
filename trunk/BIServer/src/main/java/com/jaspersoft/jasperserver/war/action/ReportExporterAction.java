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
package com.jaspersoft.jasperserver.war.action;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;


/**
 * @author sanda zaharia (szaharia@users.sourceforge.net)
 * @version $Id: ReportExporterAction.java 10154 2007-09-19 16:55:09Z lucian $
 */
public class ReportExporterAction extends FormAction {
	
	private static final String REPORT_EXPORT = "export";
	private static final String REPORT_EXPORT_PARAMS = "exportParams";
	private static final String REPORT_EXPORT_OPTION = "exportOption";
	private static final String PARAMETER_DIALOG_NAME = "parameterDialogName";
	private static final String OUTPUT = "output";
	private static final String REPORT_OUTPUT = "reportOutput";
	private static final String EXPORTER_TYPE = "exporterType";
	
	private Map configuredExporters;
	private Object exportParameters;
//	private String exporterType;
	private ConfigurationBean configurationBean;

	public Event exportOptions(RequestContext context) throws Exception{
		MutableAttributeMap flowScope = context.getFlowScope();
		String type = getExporterType(context);
//		if(type!= null){
//			exporterType = type;
//		}
		flowScope.put(ReportExporterAction.EXPORTER_TYPE, type);
//		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(exporterType);
		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(type);
		if(type!= null && StringUtils.isNotEmpty(exporterConfiguration.getParameterDialogName())){
			exportParameters=exporterConfiguration.getExportParameters();
			String parameterDialogName = exporterConfiguration.getParameterDialogName();
			setFormObjectClass(exportParameters.getClass()); 
			setFormObjectName(parameterDialogName);
			setFormObjectScope(ScopeType.FLOW);
			if(configurationBean.isReportLevelConfigurable()){
				flowScope.put(ReportExporterAction.PARAMETER_DIALOG_NAME, parameterDialogName);
				ExportParameters formObject = (ExportParameters) getFormObject(context);
				formObject.setOverrideReportHints(true);
				context.getRequestScope().put(parameterDialogName, formObject);
				flowScope.put(ReportExporterAction.REPORT_EXPORT_OPTION, ReportExporterAction.REPORT_EXPORT_PARAMS);
				
			}else{
				flowScope.put(parameterDialogName, getFormObject(context));
				flowScope.put(ReportExporterAction.REPORT_EXPORT_OPTION, ReportExporterAction.REPORT_EXPORT);
			}
		}else{
			flowScope.put(ReportExporterAction.REPORT_EXPORT_OPTION, ReportExporterAction.REPORT_EXPORT);
		}
		return success();
	}
	
	public void export(RequestContext context) throws Exception{
		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(context.getFlowScope().get(ReportExporterAction.EXPORTER_TYPE));
		exporterConfiguration.getCurrentExporter().export(context);
	}

	/**
	 * @return Returns the configuredExporters.
	 */
	public Map getConfiguredExporters() {
		return configuredExporters;
	}

	/**
	 * @param configuredExporters The configuredExporters to set.
	 */
	public void setConfiguredExporters(Map configuredExporters) {
		this.configuredExporters = configuredExporters;
	}
	
	private String getExporterType(RequestContext context)
	{
		String type = context.getRequestParameters().get(ReportExporterAction.OUTPUT) == null ?
				(String)context.getFlowScope().get(ReportExporterAction.REPORT_OUTPUT) :
				(String)context.getRequestParameters().get(ReportExporterAction.OUTPUT);	
		return getCanonicValue(type);
//		return getCanonicValue(context.getRequestParameters().get(ReportExporterAction.OUTPUT));
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public Object getExportParameters() {
		return exportParameters;
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(Object exportParameters) {
		this.exportParameters = exportParameters;
	}
	/**
	 *
	 */
	public Object createFormObject(RequestContext context) throws Exception
	{
		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(context.getFlowScope().get(ReportExporterAction.EXPORTER_TYPE));
		if(null != exporterConfiguration){
			Object exportParams=exporterConfiguration.getExportParameters();
			if(null != exportParams){
				ExportParameters exp = (ExportParameters)exportParams.getClass().newInstance();
				exp.setPropertyValues(exportParams);
				return exp;
			}
		}
		return null;
	}
	
	private String getCanonicValue(String type){
		if("excel".equalsIgnoreCase(type)){
			return "xls";
		}
		return type;
	}

	/**
	 * @return Returns the configurationBean.
	 */
	public ConfigurationBean getConfigurationBean() {
		return configurationBean;
	}

	/**
	 * @param configurationBean The configurationBean to set.
	 */
	public void setConfigurationBean(ConfigurationBean configurationBean) {
		this.configurationBean = configurationBean;
	}
}
