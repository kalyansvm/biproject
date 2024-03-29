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
package com.jaspersoft.jasperserver.war.tags;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FormatDateTag.java 8408 2007-05-29 23:29:12Z melih $
 */
public class FormatDateTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(FormatDateTag.class); 
	
	public static final String DEFAULT_DATE_FORMAT_MESSAGE = "date.format";
	public static final String DEFAULT_DATETIME_FORMAT_MESSAGE = "datetime.format";

	private Date value;
	private boolean time = true;
	private String pattern;
	private String patternMessage;
	
	public FormatDateTag () {
	}

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
	}

	public boolean isTime() {
		return time;
	}

	public void setTime(boolean time) {
		this.time = time;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getPatternMessage() {
		return patternMessage;
	}

	public void setPatternMessage(String patternMessage) {
		this.patternMessage = patternMessage;
	}
	

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		WebApplicationContext applicationContext = RequestContextUtils.getWebApplicationContext(request);
		
		Locale locale = RequestContextUtils.getLocale(request);
		TimeZone timezone = JasperServerUtil.getTimezone(request);
		
		String formattingPattern = getFormattingPattern(applicationContext, locale);
		SimpleDateFormat format = new SimpleDateFormat(formattingPattern, locale);
		format.setTimeZone(timezone);

		String formattedValue = format.format(getValue());
		
		try {
			//TODO html escaping?
			pageContext.getOut().write(formattedValue);
		} catch (IOException e) {
			log.error(e);
			throw new JspException(e);
		}

		return SKIP_BODY;
	}

	protected String getFormattingPattern(WebApplicationContext applicationContext, Locale locale) {
		String formattingPattern = getPattern();
		
		if (formattingPattern == null && getPatternMessage() != null) {
			formattingPattern = applicationContext.getMessage(getPatternMessage(), null, locale);
		}
		
		if (formattingPattern == null) {
			String message = isTime() ? DEFAULT_DATETIME_FORMAT_MESSAGE : DEFAULT_DATE_FORMAT_MESSAGE; 
			formattingPattern = applicationContext.getMessage(message, null, locale);
		}
		
		return formattingPattern;
	}

	public void release() {
		value = null;
		time = true;
		pattern = null;
		patternMessage = null;
		
		super.release();
	}
	
	
}
