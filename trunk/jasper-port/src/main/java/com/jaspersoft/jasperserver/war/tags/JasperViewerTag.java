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
package com.jaspersoft.jasperserver.war.tags;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.war.action.hyperlinks.HyperlinkProducerFactoryFlowFactory;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;


/**
 * renderJsp parameter allows override of default output format for HTML controls
 * 
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: JasperViewerTag.java 8867 2007-06-22 16:07:37Z lucian $
 */
public class JasperViewerTag extends RequestContextAwareTag
{
	protected static final Log log = LogFactory.getLog(JasperViewerTag.class);
	
	public static final String DEFAULT_RENDER_JSP = "/WEB-INF/jsp/DefaultJasperViewer.jsp";
	public static final String DEFAULT_JASPER_PRINT_ATTRIBUTE = "jasperPrint";
	public static final String DEFAULT_PAGE_INDEX_ATTRIBUTE = "pageIndex";
	public static final String DEFAULT_LINK_PRODUCER_FACTORY_ATTRIBUTE = "hyperlinkHandlerFactory";
	public static final String EMPTY_REPORT_ATTRIBUTE = "emptyReport";
	
	protected static final String JASPER_PRINT_ACCESSOR_BEAN_NAME = "jasperPrintAccessor";

	private String imageServlet;
	private String page;
	private String renderJsp;
	private String providedExporterClassName;
	private Map exporterParameters;
	private String jasperPrintAttribute = DEFAULT_JASPER_PRINT_ATTRIBUTE;
	private String pageIndexAttribute = DEFAULT_PAGE_INDEX_ATTRIBUTE;
	private String linkProducerFactoryAttribute = DEFAULT_LINK_PRODUCER_FACTORY_ATTRIBUTE;
	

	protected int doStartTagInternal() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException
	{
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

		try {			
			JasperPrint jasperPrint = getJasperPrint(request);
			if (jasperPrint == null) {
				log.error("There is no JasperPrint object cannot be accessed.");
				return EVAL_PAGE;
			}
			
			request.setAttribute("page", page);

			boolean emptyReport = jasperPrint.getPages().isEmpty();
			request.setAttribute(EMPTY_REPORT_ATTRIBUTE, Boolean.valueOf(emptyReport));
			
			if (!emptyReport) {
				Integer pageIndex = (Integer) request.getAttribute(getPageIndexAttribute());
				if (pageIndex == null)
					pageIndex = new Integer(0);

				int lastPageIndex = jasperPrint.getPages().size() - 1;
				
				request.setAttribute("pageIndex", pageIndex);
				request.setAttribute("lastPageIndex", new Integer(lastPageIndex));
				
				JRHtmlExporter exporter = new JRHtmlExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex);
				String imageURI = response.encodeURL(imageServlet + "image=");
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imageURI);
				exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
				exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
				
				HyperlinkProducerFactoryFlowFactory linkProducerFactory = (HyperlinkProducerFactoryFlowFactory) request.getAttribute(getLinkProducerFactoryAttribute());
				if (linkProducerFactory != null) {
					JRHyperlinkProducerFactory hyperlinkProducerFactory = linkProducerFactory.getHyperlinkProducerFactory(request, response);
					exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, hyperlinkProducerFactory);
				}
				
/*
 * Future enhancement
 * 
 * 				JRExporter exporter = (providedExporterClassName != null)
									? defaultExporter(jasperPrint, pageIndex) 
									: providedExporter(jasperPrint, pageIndex);
				setParameters(exporter);
*/			
				request.setAttribute("exporter", exporter);
			}
			
			BodyContent nestedContent = pageContext.pushBody();
			boolean popped = false;
			try {
				pageContext.include(getRenderJsp());
				
				popped = true;
				pageContext.popBody();
				nestedContent.writeOut(pageContext.getOut());
			} finally {
				if (!popped) {
					pageContext.popBody();
				}
			}

		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e, e);
			throw new JspException(e);
		}

		return EVAL_PAGE;
	}

	protected JasperPrint getJasperPrint(HttpServletRequest request) {
		SessionObjectSerieAccessor jasperPrintAccessor = getJasperPrintAccessor();
		ReportUnitResult result = (ReportUnitResult) jasperPrintAccessor.getObject(request, getJasperPrintAttribute());
		JasperPrint jasperPrint = (result == null ? null : result.getJasperPrint());
		if (jasperPrint == null) {
			throw new JSException("jsexception.jasperprint.not.found", new Object[] {getJasperPrintAttribute()});
		}
		return jasperPrint;
	}

	protected SessionObjectSerieAccessor getJasperPrintAccessor() {
		WebApplicationContext applicationContext = getRequestContext().getWebApplicationContext();
		SessionObjectSerieAccessor jasperPrintAccessor = (SessionObjectSerieAccessor) applicationContext.getBean(
				JASPER_PRINT_ACCESSOR_BEAN_NAME,
				SessionObjectSerieAccessor.class);
		return jasperPrintAccessor;
	}

	public String getImageServlet()
	{
		return imageServlet;
	}

	public void setImageServlet(String imageServlet)
	{
		this.imageServlet = imageServlet;
	}

	public String getPage()
	{
		return page;
	}

	public void setPage(String page)//FIXME used?
	{
		this.page = page;
	}

	public String getRenderJsp() {
		if (renderJsp == null || renderJsp.trim().length() == 0) {
			return DEFAULT_RENDER_JSP;
		}
		return renderJsp;
	}

	public void setRenderJsp(String renderJsp) {
		this.renderJsp = renderJsp;
	}

	public String getExporterClassName()
	{
		return providedExporterClassName;
	}

	public void setExporterClassName(String exporterClassName)
	{
		this.providedExporterClassName = exporterClassName;
	}
	
	/**
	 * @return Returns the exporterParameters.
	 */
	public Map getExporterParameters()
	{
		return exporterParameters;
	}

	/**
	 * @param exporterParameters The exporterParameters to set.
	 */
	public void setExporterParameters(Map exporterParameters)
	{
		this.exporterParameters = exporterParameters;
	}

	private JRExporter defaultExporter(JasperPrint jasperPrint, Integer pageIndex) {
		JRHtmlExporter exporter = new JRHtmlExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex);
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imageServlet + "?image=");
		exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
		exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
		return exporter;
	}
	
	private JRExporter providedExporter(JasperPrint jasperPrint, Integer pageIndex) throws Exception {
		Class exporterClass = Class.forName(providedExporterClassName);
		
		JRExporter exporter = (JRExporter) exporterClass.newInstance();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex);
		return exporter;
	}
	
	private void setParameters(JRExporter exporter) {
		if (exporterParameters == null || exporterParameters.size() == 0) {
			return;
		}
		Iterator it =  exporterParameters.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			exporter.setParameter((JRExporterParameter) entry.getKey(), entry.getValue());
		}
	}

	public String getJasperPrintAttribute() {
		return jasperPrintAttribute;
	}

	public void setJasperPrintAttribute(String jasperPrintAccessorAttribute) {
		this.jasperPrintAttribute = jasperPrintAccessorAttribute;
	}

	public String getPageIndexAttribute() {
		return pageIndexAttribute;
	}

	public void setPageIndexAttribute(String pageIndexAttribute) {
		this.pageIndexAttribute = pageIndexAttribute;
	}

	public String getLinkProducerFactoryAttribute() {
		return linkProducerFactoryAttribute;
	}

	public void setLinkProducerFactoryAttribute(String linkHandlerFactoryAttribute) {
		this.linkProducerFactoryAttribute = linkHandlerFactoryAttribute;
	}
}
