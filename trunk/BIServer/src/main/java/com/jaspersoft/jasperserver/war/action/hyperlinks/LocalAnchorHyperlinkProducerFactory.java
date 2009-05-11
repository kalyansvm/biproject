/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;

import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LocalAnchorHyperlinkProducerFactory.java 8408 2007-05-29 23:29:12Z melih $
 */
public class LocalAnchorHyperlinkProducerFactory implements HyperlinkProducerFlowFactory, Serializable {

	private static final long serialVersionUID = 1L;

	private SessionObjectSerieAccessor jasperPrintAccessor;
	private String jasperPrintNameRequestAttribute;
	private String flowControllerMapping;
	private String navigateEventID;
	private String pageIndexParameter;

	public JRHyperlinkProducer getHyperlinkProducer(HttpServletRequest request, HttpServletResponse response) {
		return new LocalAnchorHyperlinkProducer(request, response);
	}

	protected class LocalAnchorHyperlinkProducer implements JRHyperlinkProducer {
		private final HttpServletResponse response;
		private final String appContext;
		private final JasperPrint jasperPrint;
		private final String flowExecutionKey;
		
		public LocalAnchorHyperlinkProducer(HttpServletRequest request, HttpServletResponse response) {
			this.response = response;
			this.appContext = request.getContextPath();
			
			this.flowExecutionKey = (String) request.getAttribute("flowExecutionKey");
			
			String jasperPrintName = (String) request.getAttribute(getJasperPrintNameRequestAttribute());
			ReportUnitResult result = (ReportUnitResult) getJasperPrintAccessor().getObject(request, jasperPrintName);
			this.jasperPrint = (result == null ? null : result.getJasperPrint());
			if (jasperPrint == null) {
				throw new JSException("jsexception.jasperprint.not.found", new Object[] {jasperPrintName});
			}
		}

		public String getHyperlink(JRPrintHyperlink hyperlink) {
			String ref = null;
			String anchor = hyperlink.getHyperlinkAnchor();
			if (anchor != null) {
				Map anchorIndexes = jasperPrint.getAnchorIndexes();
				JRPrintAnchorIndex anchorIndex = (JRPrintAnchorIndex) anchorIndexes.get(anchor);
				if (anchorIndex != null) {
					int page = anchorIndex.getPageIndex();
					ref = createAnchorURL(anchor, page);
				}
			}
			return ref;
		}

		protected String createAnchorURL(String anchor, int page) {
			StringBuffer uri = new StringBuffer(200);
			uri.append(appContext);
			uri.append(getFlowControllerMapping());
			uri.append("?_eventId_");
			uri.append(getNavigateEventID());
			uri.append("=&");
			uri.append(getPageIndexParameter());
			uri.append("=");
			uri.append(page);
			uri.append("&_flowExecutionKey=");
			uri.append(flowExecutionKey);
			uri.append("#");
			uri.append(anchor);
			return response.encodeURL(uri.toString());
		}
	}

	public String getJasperPrintNameRequestAttribute() {
		return jasperPrintNameRequestAttribute;
	}

	public void setJasperPrintNameRequestAttribute(String jasperPrintNameRequestAttribute) {
		this.jasperPrintNameRequestAttribute = jasperPrintNameRequestAttribute;
	}
	
	public String getFlowControllerMapping() {
		return flowControllerMapping;
	}

	public void setFlowControllerMapping(String flowControllerMapping) {
		this.flowControllerMapping = flowControllerMapping;
	}

	public String getNavigateEventID() {
		return navigateEventID;
	}

	public void setNavigateEventID(String navigateEventID) {
		this.navigateEventID = navigateEventID;
	}

	public String getPageIndexParameter() {
		return pageIndexParameter;
	}

	public void setPageIndexParameter(String pageIndexParameter) {
		this.pageIndexParameter = pageIndexParameter;
	}

	public SessionObjectSerieAccessor getJasperPrintAccessor() {
		return jasperPrintAccessor;
	}

	public void setJasperPrintAccessor(
			SessionObjectSerieAccessor jasperPrintAccessor) {
		this.jasperPrintAccessor = jasperPrintAccessor;
	}

}
