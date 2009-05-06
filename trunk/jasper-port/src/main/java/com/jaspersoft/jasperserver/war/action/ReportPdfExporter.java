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

import net.sf.jasperreports.engine.JRExporterParameter;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PdfExportParametersBean;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportPdfExporter.java 10154 2007-09-19 16:55:09Z lucian $
 */
public class ReportPdfExporter extends AbstractReportExporter {

	private EngineService engine;
	private PdfExportParametersBean exportParameters;
	
	public void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) {
		String locale = LocaleContextHolder.getLocale().getLanguage();
		Map localizedFontMap = exportParameters.getLocalizedFontMap().get(locale) != null ?
				(Map)exportParameters.getLocalizedFontMap().get(locale) :
					(Map)exportParameters.getLocalizedFontMap().get(locale.substring(0,2));
		if(localizedFontMap != null){
			baseParameters.put(JRExporterParameter.FONT_MAP, localizedFontMap);
		}
		
		if (exportParameters.isOverrideReportHints()) {
			baseParameters.put(JRExporterParameter.PARAMETERS_OVERRIDE_REPORT_HINTS, Boolean.TRUE);
		}
		
		engine.exportToPdf(executionContext, reportUnitURI, baseParameters);
	}

	protected String getContentType(RequestContext context) {
		return "application/pdf";
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
		response.setHeader("Content-Disposition", "inline; filename=\"" + getFilename(context, "pdf") + "\"");
	}

	public EngineService getEngine() {
		return engine;
	}

	public void setEngine(EngineService engine) {
		this.engine = engine;
	}
	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		return null;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public PdfExportParametersBean getExportParameters() {
		return exportParameters;
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(PdfExportParametersBean exportParameters) {
		this.exportParameters = exportParameters;
	}
	
}
