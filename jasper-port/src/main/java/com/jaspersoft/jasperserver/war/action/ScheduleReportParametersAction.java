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

import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ScheduleReportParametersAction.java 12074 2008-02-13 16:35:09Z lucian $
 */
public class ScheduleReportParametersAction extends ReportParametersAction {


    private String jobFormObjectName;
    private Class jobFormObjectClass;
    private ScopeType jobFormObjectScope;

	public Class getJobFormObjectClass() {
		return jobFormObjectClass;
	}

	public void setJobFormObjectClass(Class jobFormObjectClass) {
		this.jobFormObjectClass = jobFormObjectClass;
	}

	public String getJobFormObjectName() {
		return jobFormObjectName;
	}

	public void setJobFormObjectName(String jobFormObjectName) {
		this.jobFormObjectName = jobFormObjectName;
	}

	public ScopeType getJobFormObjectScope() {
		return jobFormObjectScope;
	}

	public void setJobFormObjectScope(ScopeType jobFormObjectScope) {
		this.jobFormObjectScope = jobFormObjectScope;
	}

	public Event checkForParameters(RequestContext context)
	{
		createWrappers(context);
		return success();
	}

	public Event setParameterValues(RequestContext context) {
		Map parameterValues = getParameterValuesForJob(context);
		if (parameterValues == null) {
			return error();
		}
		
		ReportJob reportJob = getReportJob(context);
		reportJob.getSource().setParametersMap(parameterValues);

		return success();
	}

	protected Map getParameterValuesForJob(RequestContext context) {
		return getParameterValues(context, false);
	}

	protected InputValueProvider initialValueProvider(RequestContext context) {
		InputValueProvider provider = baseJobValueProvider(context);
		
		ReportJob reportJob = getReportJob(context);
		Map paramValues = reportJob.getSource().getParametersMap();
		if (paramValues != null && !paramValues.isEmpty()) {
			provider = new MapValueProvider(paramValues, provider);
		}

		return provider;
	}

	protected InputValueProvider baseJobValueProvider(RequestContext context) {
		return defaultValuesProvider(context);
	}
	
	protected ReportJob getReportJob(RequestContext context) {
		AttributeMap scope = getJobFormObjectScope().getScope(context);
		return (ReportJob) scope.getRequired(getJobFormObjectName(), getJobFormObjectClass());
	}

}
