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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: ParametersFormTag.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ParametersFormTag extends TagSupport
{
	private static final Log log = LogFactory.getLog(ParametersFormTag.class);
	
	public static final String DEFAULT_RENDER_JSP = "/WEB-INF/jsp/DefaultParametersForm.jsp";
	public static final String REPORT_PARAMETERS_ATTR = "reportparams";
	public static final String WRAPPERS_ATTR = "wrappers";
	public static final String SUBMIT_PARAM = "_eventId_submit";
	public static final String CANCEL_PARAM = "_eventId_cancel";
	
	public static final String ATTRIBUTE_ON_INPUT_CHANGE = "onInputChange";
	public static final String ATTRIBUTE_INPUT_NAME_PREFIX = "inputNamePrefix";
	public static final String ATTRIBUTE_READ_ONLY = "readOnlyForm";

	private String renderJsp;
	private String reportName;
	private String onInputChange;
	private String inputNamePrefix;
	private boolean readOnly;

	public int doStartTag() throws JspException
	{
		setAttributes();
		
        return Tag.SKIP_BODY;
	}


	protected void setAttributes()
	{
		ServletRequest request = pageContext.getRequest();
		
		if (onInputChange != null && onInputChange.length() > 0)
		{
			request.setAttribute(ATTRIBUTE_ON_INPUT_CHANGE, onInputChange);
		}
		
		request.setAttribute(ATTRIBUTE_INPUT_NAME_PREFIX, getInputNamePrefix());
		request.setAttribute(ATTRIBUTE_READ_ONLY, Boolean.valueOf(isReadOnly()));
	}


	/**
	 *
	 */
	public int doEndTag() throws JspException
	{
		try {
			pageContext.include(getRenderJsp());

		} catch (Exception e) {
			log.error(e, e);
			throw new JspException(e);
		}

		return EVAL_PAGE;
	}


	/**
	 *
	 */
	public String getRenderJsp() {
		if (renderJsp == null || renderJsp.trim().length() == 0) {
			return DEFAULT_RENDER_JSP;
		}
		return renderJsp;
	}

	/**
	 *
	 */
	public void setRenderJsp(String renderJsp) {
		this.renderJsp = renderJsp;
	}

	public String getReportName()
	{
		return reportName;
	}

	public void setReportName(String reportName)
	{
		this.reportName = reportName;
	}

	public String getOnInputChange()
	{
		return onInputChange;
	}

	public void setOnInputChange(String onInputChange)
	{
		this.onInputChange = onInputChange;
	}

	public String getInputNamePrefix()
	{
		return inputNamePrefix == null ? "" : inputNamePrefix;
	}

	public void setInputNamePrefix(String inputNamePrefix)
	{
		this.inputNamePrefix = inputNamePrefix;
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}
}
