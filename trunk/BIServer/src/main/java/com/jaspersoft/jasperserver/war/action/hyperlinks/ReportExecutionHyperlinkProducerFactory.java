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
package com.jaspersoft.jasperserver.war.action.hyperlinks;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;

import org.springframework.context.i18n.LocaleContextHolder;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.BaseReportExecutionHyperlinkProducerFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportExecutionHyperlinkProducerFactory.java 12009 2008-02-04 17:33:22Z lucian $
 */
public class ReportExecutionHyperlinkProducerFactory extends BaseReportExecutionHyperlinkProducerFactory implements HyperlinkProducerFlowFactory {
	
	private static final long serialVersionUID = 1L;
	
	private String attributeReportLocale;
	private String urlParameterReportLocale;
	
	public JRHyperlinkProducer getHyperlinkProducer(HttpServletRequest request, HttpServletResponse response) {
		HyperlinkProducer hyperlinkProducer = new HyperlinkProducer(request, response);
		return hyperlinkProducer;
	}
	
	
	public class HyperlinkProducer extends BaseHyperlinkProducer {
		
		private final HttpServletResponse response;
		private final String contextPath;
		private final String flowExecutionKey;
		private final Locale reportLocale;

		public HyperlinkProducer(final HttpServletRequest request, HttpServletResponse response) {
			this.response = response;
			this.flowExecutionKey = (String) request.getAttribute("flowExecutionKey");
			this.contextPath = request.getContextPath();
			reportLocale = (Locale) request.getAttribute(getAttributeReportLocale());
		}
		
		public String getHyperlink(JRPrintHyperlink hyperlink) {
			String uri = super.getHyperlink(hyperlink);
			return response.encodeURL(uri);
		}
		
		protected void appendSubflowParams(StringBuffer sb) {
			if (flowExecutionKey != null) {
				sb.append("&_eventId_drillReport=");
				sb.append("&_flowExecutionKey=");
				sb.append(encode(flowExecutionKey));
			}
		}

		protected void appendAdditionalParameters(JRPrintHyperlink hyperlink, StringBuffer sb) {
			if (hyperlink.getHyperlinkTarget() == JRHyperlink.HYPERLINK_TARGET_SELF) {
				appendSubflowParams(sb);
			}
			
			if (reportLocale != null) {
				appendParameter(sb, getUrlParameterReportLocale(), reportLocale.toString());
			}
		}

		protected void appendHyperlinkStart(JRPrintHyperlink hyperlink, StringBuffer sb) {			
			sb.append(contextPath);
		}

		protected Locale getLocale() {
			return LocaleContextHolder.getLocale();
		}
	}

	public String getAttributeReportLocale() {
		return attributeReportLocale;
	}

	public void setAttributeReportLocale(String attributeReportLocale) {
		this.attributeReportLocale = attributeReportLocale;
	}

	public String getUrlParameterReportLocale() {
		return urlParameterReportLocale;
	}

	public void setUrlParameterReportLocale(String urlParameterReportLocale) {
		this.urlParameterReportLocale = urlParameterReportLocale;
	}

}
