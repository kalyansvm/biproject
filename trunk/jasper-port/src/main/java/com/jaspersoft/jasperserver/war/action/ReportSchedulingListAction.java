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

import java.util.List;

import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportSchedulingListAction.java 10888 2007-11-27 20:10:40Z achan $
 */
public class ReportSchedulingListAction extends MultiAction {
	
	private String reportUnitURIAttrName;
	private String jobListAttrName;
	private String selectedJobsParamName;
	private String attributeOwnerURI;
	
	private ReportSchedulingService schedulingService;
	
	public ReportSchedulingListAction() {
	}

	public ReportSchedulingService getSchedulingService() {
		return schedulingService;
	}

	public void setSchedulingService(ReportSchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}

	public String getJobListAttrName() {
		return jobListAttrName;
	}

	public void setJobListAttrName(String jobListAttrName) {
		this.jobListAttrName = jobListAttrName;
	}

	public String getReportUnitURIAttrName() {
		return reportUnitURIAttrName;
	}

	public void setReportUnitURIAttrName(String reportUnitURIAttrName) {
		this.reportUnitURIAttrName = reportUnitURIAttrName;
	}

	public String getSelectedJobsParamName() {
		return selectedJobsParamName;
	}

	public void setSelectedJobsParamName(String selectedJobsParamName) {
		this.selectedJobsParamName = selectedJobsParamName;
	}

	public Event listJobs(RequestContext context) {
		setRequestErrorMessage(context);
		
		List jobs = loadJobList(context);
		context.getRequestScope().put(getJobListAttrName(), jobs);
		
		context.getRequestScope().put(getAttributeOwnerURI(), getOwnerURI(context));
		
		return success();
	}

	protected String getOwnerURI(RequestContext context) {
		return context.getFlowScope().getRequiredString(getReportUnitURIAttrName());
	}

	protected List loadJobList(RequestContext context) {
		String reportUnitURI = context.getFlowScope().getString(getReportUnitURIAttrName());
		if (reportUnitURI == null) {
			reportUnitURI = (String)context.getRequestScope().get("reportUnitURI");
		}
		List jobs = schedulingService.getScheduledJobs(getExecutionContext(context), reportUnitURI);
		return jobs;
	}

	protected void setRequestErrorMessage(RequestContext context) {
		MutableAttributeMap flowScope = context.getFlowScope();
		if (flowScope.contains("errorMessage")) {
			String message = (String) flowScope.remove("errorMessage");
			Object args = flowScope.remove("errorArguments");
			
			MutableAttributeMap requestScope = context.getRequestScope();
			requestScope.put("errorMessage", message);
			requestScope.put("errorArguments", args);
		}
	}

	public Event deleteJobs(RequestContext context) {
		String[] selectedJobs = context.getRequestParameters().getArray(getSelectedJobsParamName());
		if (selectedJobs != null && selectedJobs.length > 0) {
			long[] jobIds = new long[selectedJobs.length];
			for (int i = 0; i < selectedJobs.length; i++) {
				jobIds[i] = Long.parseLong(selectedJobs[i]);
			}
			schedulingService.removeScheduledJobs(getExecutionContext(context), jobIds);
		}
		
		return success();
	}
	
	protected ExecutionContext getExecutionContext(RequestContext context) {
		return JasperServerUtil.getExecutionContext(context);
	}

	public String getAttributeOwnerURI() {
		return attributeOwnerURI;
	}

	public void setAttributeOwnerURI(String attributeOwnerURI) {
		this.attributeOwnerURI = attributeOwnerURI;
	}

}
