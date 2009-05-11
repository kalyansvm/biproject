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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseReportExecutionHyperlinkProducerFactory.java 13506 2008-05-12 15:07:47Z tdanciu $
 */
public abstract class BaseReportExecutionHyperlinkProducerFactory implements Serializable {

	protected final static Log log = LogFactory.getLog(BaseReportExecutionHyperlinkProducerFactory.class);

	private HyperlinkParameterFormatter dateFormatter;
	private CharacterEncodingProvider encodingProvider;
	private String flowControllerMapping;
	private String reportExecutionFlowId;
	private String hyperlinkParameterReportUnit;
	private String urlParameterReportUnit;
	private String defaultOutputChannel;
	private String hyperlinkParameterOutputChannel;
	private String urlParameterOutputChannel;
		
	public abstract class BaseHyperlinkProducer implements JRHyperlinkProducer, Serializable {
		
		public BaseHyperlinkProducer() {
		}

		public String getHyperlink(JRPrintHyperlink hyperlink) {
			StringBuffer sb = new StringBuffer();
			appendHyperlinkStart(hyperlink, sb);
			sb.append(getFlowControllerMapping());
			sb.append("?_flowId=");
			sb.append(getReportExecutionFlowId());

			JRPrintHyperlinkParameters parameters = hyperlink.getHyperlinkParameters();
			if (parameters != null) {
				appendParameters(sb, parameters);
			}
			
			appendAdditionalParameters(hyperlink, sb);

			return sb.toString();
		}

		protected void appendParameters(StringBuffer sb, JRPrintHyperlinkParameters parameters) {
			boolean outputChannelSet = false;
			for (Iterator it = parameters.getParameters().iterator(); it.hasNext();) {
				JRPrintHyperlinkParameter parameter = (JRPrintHyperlinkParameter) it.next();
				if (parameter.getName().equals(getHyperlinkParameterReportUnit())) {
					appendParameter(sb, getUrlParameterReportUnit(), (String) parameter.getValue());
				} else if (parameter.getName().equals(getHyperlinkParameterOutputChannel())) {
					appendParameter(sb, getUrlParameterOutputChannel(), (String) parameter.getValue());
					outputChannelSet = true;
				} else {
					appendParameter(sb, parameter);
				}
			}
			if (!outputChannelSet && getDefaultOutputChannel() != null)
			{
				appendParameter(sb, getUrlParameterOutputChannel(), getDefaultOutputChannel());
			}
		}
		
		protected String encode(String text) {
			try {
				String encoding = getEncodingProvider().getCharacterEncoding();
				return URLEncoder.encode(text, encoding);
			} catch (UnsupportedEncodingException e) {
				throw new JSExceptionWrapper(e);
			}
		}

		protected void appendParameter(StringBuffer sb, JRPrintHyperlinkParameter parameter) {
			String name = parameter.getName();
			String valueClassName = parameter.getValueClass();
			Class valueClass = loadClass(valueClassName);
			Object value = parameter.getValue();
			if (valueClass.equals(String.class)) {
				appendParameter(sb, name, (String) value);
			} else if (valueClass.equals(Boolean.class)) {
				if (value != null && ((Boolean) value).booleanValue()) {
					appendParameter(sb, name, "true");
				}
			} else if (Number.class.isAssignableFrom(valueClass)) {
				if (value != null) {
					appendParameter(sb, name, value.toString());
				}
			} else if (Date.class.isAssignableFrom(valueClass)) {
				if (value != null) {
					String formattedValue = getDateFormatter().format(value);
					appendParameter(sb, name, formattedValue);
				}
			}
		}

		protected Class loadClass(String valueClassName) {
			try {
				return Class.forName(valueClassName);
			} catch (ClassNotFoundException e) {
				log.error("Parameter class \"" + valueClassName + "\" not found", e);
				throw new JSExceptionWrapper(e);
			}
		}
		
		protected final void appendParameter(StringBuffer sb, String name, String value) {
			sb.append('&');
			sb.append(encode(name));
			
			if (value != null) {
				sb.append('=');
				sb.append(encode(value));
			}
		}
		
		protected abstract void appendHyperlinkStart(JRPrintHyperlink hyperlink, StringBuffer sb);
		
		protected abstract void appendAdditionalParameters(JRPrintHyperlink hyperlink, StringBuffer sb);
	}

	public BaseReportExecutionHyperlinkProducerFactory() {
	}

	public HyperlinkParameterFormatter getDateFormatter() {
		return dateFormatter;
	}

	public void setDateFormatter(HyperlinkParameterFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	public String getFlowControllerMapping() {
		return flowControllerMapping;
	}

	public void setFlowControllerMapping(String flowControllerMapping) {
		this.flowControllerMapping = flowControllerMapping;
	}

	public String getReportExecutionFlowId() {
		return reportExecutionFlowId;
	}

	public void setReportExecutionFlowId(String reportExecutionFlowId) {
		this.reportExecutionFlowId = reportExecutionFlowId;
	}

	public String getHyperlinkParameterReportUnit() {
		return hyperlinkParameterReportUnit;
	}

	public void setHyperlinkParameterReportUnit(
			String hyperlinkParameterReportUnitURI) {
		this.hyperlinkParameterReportUnit = hyperlinkParameterReportUnitURI;
	}

	public String getUrlParameterReportUnit() {
		return urlParameterReportUnit;
	}

	public void setUrlParameterReportUnit(String urlParameterReportUnitURI) {
		this.urlParameterReportUnit = urlParameterReportUnitURI;
	}

	public String getDefaultOutputChannel() {
		return defaultOutputChannel;
	}

	public void setDefaultOutputChannel(
			String defaultOutputChannel) {
		this.defaultOutputChannel = defaultOutputChannel;
	}

	public String getHyperlinkParameterOutputChannel() {
		return hyperlinkParameterOutputChannel;
	}

	public void setHyperlinkParameterOutputChannel(
			String hyperlinkParameterOutputChannel) {
		this.hyperlinkParameterOutputChannel = hyperlinkParameterOutputChannel;
	}

	public String getUrlParameterOutputChannel() {
		return urlParameterOutputChannel;
	}

	public void setUrlParameterOutputChannel(String urlParameterOutputChannel) {
		this.urlParameterOutputChannel = urlParameterOutputChannel;
	}

	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}

	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

}
