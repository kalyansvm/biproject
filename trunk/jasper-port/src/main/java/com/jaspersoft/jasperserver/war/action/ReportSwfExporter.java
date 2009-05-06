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
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.webflow.context.servlet.ServletExternalContext;

import net.sf.jasperreports.engine.JRException;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.core.collection.AttributeMap;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ReportRtfExporter.java 9885 2007-09-03 16:39:13Z lucian $
 */
public class ReportSwfExporter extends AbstractReportExporter 
{

	public Event export(RequestContext context) throws IOException, JRException 
	{
		ServletExternalContext servletContext = (ServletExternalContext) context.getExternalContext();
		HttpServletRequest request = servletContext.getRequest();
		HttpServletResponse response = servletContext.getResponse();
		
		AttributeMap flowAttrs = context.getFlowScope();
		String jasperPrintName = flowAttrs.getRequiredString(getFlowAttributeJasperPrintName());

		try
		{
			RequestDispatcher reqdis = request.getRequestDispatcher("WEB-INF/jsp/exporters/swfExport.jsp");
			request.setAttribute("jasperPrintName", jasperPrintName);
			reqdis.forward(request, response);
		}
		catch(Exception e)	
		{
			throw new JRException(e);
		}
		
		return success();
	}

	public void export(RequestContext context, ExecutionContext executionContext, String reportUnitURI, Map baseParameters) throws JRException
	{
		return;//FIXME refactor this to extract some other common exporter interface
	}

	protected String getContentType(RequestContext context) {
		return null;
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
	}
	
	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		return null;
	}
	
}
