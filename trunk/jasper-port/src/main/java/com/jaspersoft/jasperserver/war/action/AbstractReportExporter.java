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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.war.common.FileBufferedOutputStream;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.util.HttpUtils;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AbstractReportExporter.java 9885 2007-09-03 16:39:13Z lucian $
 */
public abstract class AbstractReportExporter extends MultiAction {
	
	private static final Log log = LogFactory.getLog(AbstractReportExporter.class);

	private SessionObjectSerieAccessor jasperPrintAccessor;
	private String flowAttributeJasperPrintName;
	private String flowAttributeReportUnitURI;
	private boolean setResponseContentLength = true;
	private int memoryThreshold = FileBufferedOutputStream.DEFAULT_MEMORY_THRESHOLD;
	private int initialMemoryBufferSize = FileBufferedOutputStream.DEFAULT_INITIAL_MEMORY_BUFFER_SIZE;
	private HttpUtils httpUtils;
	
	public Event export(RequestContext context) throws IOException, JRException {
		ServletExternalContext servletContext = (ServletExternalContext) context.getExternalContext();
		HttpServletResponse response = servletContext.getResponse();

		AttributeMap flowAttrs = context.getFlowScope();
		String jasperPrintName = flowAttrs.getRequiredString(getFlowAttributeJasperPrintName());
		ReportUnitResult result = (ReportUnitResult) getJasperPrintAccessor().getObject(servletContext.getRequest(), jasperPrintName);
		JasperPrint jasperPrint = (result == null ? null : result.getJasperPrint());
		if (jasperPrint == null) {
			throw new JSException("jsexception.jasperprint.not.found", new Object[] {jasperPrintName});
		}

		String reportUnitURI = flowAttrs.getRequiredString(getFlowAttributeReportUnitURI());
		
		if (setResponseContentLength)
		{
			exportBuffered(context, response, jasperPrint, reportUnitURI);
		}
		else
		{
			exportToStream(context, response, jasperPrint, reportUnitURI);
		}
		
		return success();
	}

	protected void exportToStream(RequestContext context, HttpServletResponse response, JasperPrint jasperPrint, String reportUnitURI) throws IOException, JRException {
		Map parameters = new HashMap();
		parameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);

		OutputStream ouputStream = response.getOutputStream();
		parameters.put(JRExporterParameter.OUTPUT_STREAM, ouputStream);
		
		try
		{
			response.setContentType(getContentType(context));
			setAdditionalResponseHeaders(context, response);
			export(context, getExecutionContext(context), reportUnitURI, parameters);
		}
		finally
		{
			if (ouputStream != null)
			{
				try
				{
					ouputStream.close();
				}
				catch (IOException ex)
				{
					log.warn("Error closing output stream", ex);
				}
			}
		}
	}

	protected ExecutionContext getExecutionContext(RequestContext context) {
		return JasperServerUtil.getExecutionContext(context);
	}

	protected void exportBuffered(RequestContext context, HttpServletResponse response, JasperPrint jasperPrint, String reportUnitURI) throws IOException, JRException {
		Map parameters = new HashMap();
		parameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);

		FileBufferedOutputStream bufferedOutput = new FileBufferedOutputStream(getMemoryThreshold(), getInitialMemoryBufferSize());
		parameters.put(JRExporterParameter.OUTPUT_STREAM, bufferedOutput);

		try {
			export(context, getExecutionContext(context), reportUnitURI, parameters);
			bufferedOutput.close();
			
			response.setContentType(getContentType(context));
			setAdditionalResponseHeaders(context, response);
			response.setContentLength(bufferedOutput.size());
			ServletOutputStream ouputStream = response.getOutputStream();

			try {
				bufferedOutput.writeData(ouputStream);
				bufferedOutput.dispose();
				
				ouputStream.flush();
			} finally {
				if (ouputStream != null) {
					try {
						ouputStream.close();
					}
					catch (IOException ex) {
						log.warn("Error closing output stream", ex);
					}
				}
			}
		} finally {
			bufferedOutput.close();
			bufferedOutput.dispose();
		}
	}

	public String getFlowAttributeJasperPrintName() {
		return flowAttributeJasperPrintName;
	}

	public void setFlowAttributeJasperPrintName(String flowAttributeJasperPrintName) {
		this.flowAttributeJasperPrintName = flowAttributeJasperPrintName;
	}

	public String getFlowAttributeReportUnitURI() {
		return flowAttributeReportUnitURI;
	}

	public void setFlowAttributeReportUnitURI(
			String requestAttributeReportUnitURI) {
		this.flowAttributeReportUnitURI = requestAttributeReportUnitURI;
	}

	public boolean isSetResponseContentLength() {
		return setResponseContentLength;
	}

	public void setSetResponseContentLength(boolean setResponseContentLength) {
		this.setResponseContentLength = setResponseContentLength;
	}

	public int getInitialMemoryBufferSize() {
		return initialMemoryBufferSize;
	}

	public int getMemoryThreshold() {
		return memoryThreshold;
	}

	public void setMemoryThreshold(int memoryThreshold) {
		this.memoryThreshold = memoryThreshold;
	}

	public void setInitialMemoryBufferSize(int initialMemoryBufferSize) {
		this.initialMemoryBufferSize = initialMemoryBufferSize;
	}
	
	
	protected abstract String getContentType(RequestContext context);

	/**
	 * 
	 * @param context
	 * @return
	 */
	protected abstract ExportParameters getExportParameters(RequestContext context);
	
	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		// This is needed for IE under SSL
		response.setHeader("Pragma","");
		response.setHeader("Cache-Control","no-store");
	}
	
	protected String getReportName(RequestContext context) {
		AttributeMap flowAttrs = context.getFlowScope();
		String reportUnitURI = flowAttrs.getRequiredString(getFlowAttributeReportUnitURI());
		// Get the last part of the URI
		
		return reportUnitURI.substring(reportUnitURI.lastIndexOf("/") + 1);
	}
	
	protected abstract void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException;
	
	protected String getFilename(RequestContext context, String fileExtension) {
		ServletExternalContext servletContext = (ServletExternalContext) context.getExternalContext();
		String filename = getReportName(context) + "." + fileExtension;
		return httpUtils.encodeContentFilename(servletContext.getRequest(), filename);
	}

	public SessionObjectSerieAccessor getJasperPrintAccessor() {
		return jasperPrintAccessor;
	}

	public void setJasperPrintAccessor(
			SessionObjectSerieAccessor jasperPrintAccessor) {
		this.jasperPrintAccessor = jasperPrintAccessor;
	}

	public HttpUtils getHttpUtils() {
		return httpUtils;
	}

	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}
	
}
