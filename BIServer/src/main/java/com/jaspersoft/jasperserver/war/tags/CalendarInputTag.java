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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CalendarInputTag.java 12070 2008-02-13 13:34:31Z lucian $
 */
public class CalendarInputTag extends BaseTagSupport {

	public static final String DEFAULT_DATE_FORMAT_MESSAGE = "calendar.date.format";
	public static final String DEFAULT_DATETIME_FORMAT_MESSAGE = "calendar.datetime.format";

	public static final String DEFAULT_IMAGE = "/images/cal.gif";

	private boolean time = true;
	private String formatPattern;
	private String name;
	private String value;
	private String timezoneOffset;
	private boolean readOnly = false;
	private String onchange;
	private String imageSrc;
	private String imageTipMessage;
	private String calendarInputJsp;

	protected int doStartTagInternal() {
		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		WebApplicationContext applicationContext = RequestContextUtils.getWebApplicationContext(request);
		Locale locale = RequestContextUtils.getLocale(request);

		Map attributes = new HashMap();
		attributes.put("name", name);
		attributes.put("value", value);
		attributes.put("onchange", onchange);
		attributes.put("readOnly", Boolean.valueOf(isReadOnly()));
		attributes.put("imageSrc", getImageSource(request));
		attributes.put("imageTooltip", getImageTooltip(applicationContext, locale));
		attributes.put("pattern", getPattern(applicationContext, locale));
		attributes.put("hasTime", Boolean.valueOf(time));
		attributes.put("timezoneOffset", getTimezoneOffset(request));
		
		includeNested(getJsp(), attributes);
		return SKIP_BODY;
	}

	protected String getPattern(WebApplicationContext applicationContext, Locale locale) {
		String pattern = formatPattern;
		if (pattern == null) {
			String message = isTime() ? DEFAULT_DATETIME_FORMAT_MESSAGE : DEFAULT_DATE_FORMAT_MESSAGE;
			pattern = applicationContext.getMessage(message, null, locale);
		}
		return pattern;
	}

	protected String getImageSource(HttpServletRequest request) {
		String imageSource = imageSrc;
		if (imageSource == null) {
			imageSource = request.getContextPath() + DEFAULT_IMAGE;
		}
		return imageSource;
	}

	protected String getImageTooltip(WebApplicationContext applicationContext, Locale locale) {
		String message = null;
		if (imageTipMessage != null) {
			message = applicationContext.getMessage(imageTipMessage, null, locale);
		}
		return message;
	}

	protected String getTimezoneOffset(HttpServletRequest request) {
		String tzOffset = timezoneOffset;
		if (tzOffset == null) {
			TimeZone timezone = JasperServerUtil.getTimezone(request);
			int offset = timezone.getOffset(System.currentTimeMillis());
			tzOffset = Integer.toString(offset);
		}
		return tzOffset;
	}

	protected String getJsp() {
		String jsp = calendarInputJsp;
		if (jsp == null) {
			jsp = getConfiguration().getCalendarInputJsp();
		}
		return jsp;
	}

	public void release() {
		time = true;
		formatPattern = null;
		name = null;
		value = null;
		readOnly = false;
		onchange = null;
		imageSrc = null;
		imageTipMessage = null;

		super.release();
	}

	public String getFormatPattern() {
		return formatPattern;
	}

	public void setFormatPattern(String formatPattern) {
		this.formatPattern = formatPattern;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getImageTipMessage() {
		return imageTipMessage;
	}

	public void setImageTipMessage(String imageTipMessage) {
		this.imageTipMessage = imageTipMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onChange) {
		this.onchange = onChange;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isTime() {
		return time;
	}

	public void setTime(boolean time) {
		this.time = time;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(String timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}
	
	public String getCalendarInputJsp() {
		return calendarInputJsp;
	}

	public void setCalendarInputJsp(String calendarInputJsp) {
		this.calendarInputJsp = calendarInputJsp;
	}

}
