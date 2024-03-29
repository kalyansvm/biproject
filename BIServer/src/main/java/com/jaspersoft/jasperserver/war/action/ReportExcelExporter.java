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

import jxl.write.biff.RowsExceededException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.XlsExportParametersBean;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportExcelExporter.java 11875 2008-01-21 09:32:20Z lucian $
 */
public class ReportExcelExporter extends AbstractReportExporter{

	private static final String DIALOG_NAME = "excelExportParams";
	
	private XlsExportParametersBean exportParameters;
	
	/**
	 * @return Returns the exportParameters.
	 */
	public XlsExportParametersBean getExportParameters() {
		return exportParameters;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		return context.getFlowScope().get(ReportExcelExporter.DIALOG_NAME)== null? exportParameters : (ExportParameters)context.getFlowScope().get(ReportExcelExporter.DIALOG_NAME);
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(XlsExportParametersBean exportParameters) {
		this.exportParameters = exportParameters;
	}

	public void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException,JSException {
		JExcelApiExporter exporter = new JExcelApiExporter();
		exporter.setParameters(baseParameters);
		XlsExportParametersBean exportParams = (XlsExportParametersBean)getExportParameters(context);
		
		if (exportParams.isOverrideReportHints()) {
			exporter.setParameter(JRExporterParameter.PARAMETERS_OVERRIDE_REPORT_HINTS, Boolean.TRUE);
		}
		
		if (exportParams.getOnePagePerSheet() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, exportParams.getOnePagePerSheet());
		if (exportParams.getDetectCellType() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, exportParams.getDetectCellType());
		if (exportParams.getRemoveEmptySpaceBetweenRows() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, exportParams.getRemoveEmptySpaceBetweenRows());
		if (exportParams.getRemoveEmptySpaceBetweenColumns() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, exportParams.getRemoveEmptySpaceBetweenColumns());
		if (exportParams.getWhitePageBackground() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, exportParams.getWhitePageBackground());
		if (exportParams.getIgnoreGraphics() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, exportParams.getIgnoreGraphics());
		if (exportParams.getCollapseRowSpan() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, exportParams.getCollapseRowSpan());
		if (exportParams.getIgnoreCellBorder() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, exportParams.getIgnoreCellBorder());
		if (exportParams.getFontSizeFixEnabled() != null)
			exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, exportParams.getFontSizeFixEnabled());
		if (exportParams.getMaximumRowsPerSheet() != null)
			exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, exportParams.getMaximumRowsPerSheet());
		if(exportParams.getXlsFormatPatternsMap() != null && !exportParams.getXlsFormatPatternsMap().isEmpty())
			exporter.setParameter(JRXlsExporterParameter.FORMAT_PATTERNS_MAP, exportParams.getXlsFormatPatternsMap());
		exporter.setParameter(JExcelApiExporterParameter.CREATE_CUSTOM_PALETTE, Boolean.TRUE);
		try{
			exporter.exportReport();
		}catch(JRException e){
			if(e.getCause() instanceof RowsExceededException)
				throw new JSException("jsexception.too.many.data.rows");
			else
				throw e;
		}
	}

	protected String getContentType(RequestContext context) {
		return "application/xls";
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
		response.setHeader("Content-Disposition", "inline; filename=\"" + getFilename(context, "xls") + "\"");
	}
}
