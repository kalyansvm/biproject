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

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;

import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.CsvExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;


/**
 * @author sanda zaharia (szaharia@users.sourceforge.net)
 * @version $Id: ReportCsvExporter.java 10154 2007-09-19 16:55:09Z lucian $
 */
public class ReportCsvExporter extends AbstractReportExporter {
	
	private static final String DIALOG_NAME = "csvExportParams";

	private CsvExportParametersBean exportParameters;

	public void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException {
		JRCsvExporter exporter = new JRCsvExporter();
		exporter.setParameters(baseParameters);
		CsvExportParametersBean exportParams = (CsvExportParametersBean)getExportParameters(context);
		
		if (exportParams.isOverrideReportHints()) {
			exporter.setParameter(JRExporterParameter.PARAMETERS_OVERRIDE_REPORT_HINTS, Boolean.TRUE);
		}
		
		exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, exportParams.getFieldDelimiter());
		exporter.exportReport();
	}

	protected String getContentType(RequestContext context) {
		return "text/csv";
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
		response.setHeader("Content-Disposition", "inline; filename=\"" + getFilename(context, "csv") + "\"");
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public CsvExportParametersBean getExportParameters() {
		return exportParameters;
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(CsvExportParametersBean exportParameters) {
		this.exportParameters = exportParameters;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		//if request 
		return context.getFlowScope().get(ReportCsvExporter.DIALOG_NAME)== null? exportParameters : (ExportParameters)context.getFlowScope().get(ReportCsvExporter.DIALOG_NAME);
	}

}
