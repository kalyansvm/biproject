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
package com.jaspersoft.jasperserver.export.modules.scheduling.beans;

import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.export.modules.common.ReportParameterValueBean;
import com.jaspersoft.jasperserver.export.modules.scheduling.SchedulingModuleConfiguration;

/**
 * @author tkavanagh
 * @version $Id: ReportJobSourceBean.java 12731 2008-03-28 13:37:04Z lucian $
 */
public class ReportJobSourceBean {

	private String reportUnitURI;
	private ReportParameterValueBean[] parameters;

	public void copyFrom(ReportJobSource src, 
			SchedulingModuleConfiguration configuration) {
		setReportUnitURI(src.getReportUnitURI());
		copyParametersFrom(src, configuration);
	}

	protected void copyParametersFrom(ReportJobSource src, 
			SchedulingModuleConfiguration configuration) {
		Map reportParameters = src.getParametersMap();
		ReportParameterValueBean[] params = configuration.getReportParametersTranslator().getBeanParameterValues(
				src.getReportUnitURI(), reportParameters);
		setParameters(params);
	}

	public void copyTo(ReportJobSource dest, String newReportUri, 
			SchedulingModuleConfiguration configuration) {
		dest.setReportUnitURI(newReportUri);
		copyParametersMap(dest, newReportUri, configuration);
	}

	protected void copyParametersMap(ReportJobSource dest, 
			String newReportUri, SchedulingModuleConfiguration configuration) {
		Map params = configuration.getReportParametersTranslator().getParameterValues(
				newReportUri, getParameters());
		dest.setParametersMap(params);
	}

	public String getReportUnitURI() {
		return reportUnitURI;
	}
	
	public void setReportUnitURI(String reportUnitURI) {
		this.reportUnitURI = reportUnitURI;
	}

	public ReportParameterValueBean[] getParameters() {
		return parameters;
	}
	
	public void setParameters(ReportParameterValueBean[] parameters) {
		this.parameters = parameters;
	}

}
