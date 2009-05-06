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
package com.jaspersoft.jasperserver.war.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: MessagesCalendarFormatProvider.java 12873 2008-04-08 14:10:40Z lucian $
 */
public class MessagesCalendarFormatProvider implements CalendarFormatProvider {

	private MessageSource messages;
	private String datePatternKey;
	private String calendarDatePatternKey;
	private String datetimePatternKey;
	private String calendarDatetimePatternKey;
	private boolean lenientFormats;
	
	public String getCalendarDatePattern() {
		return messages.getMessage(getCalendarDatePatternKey(), null, getLocale());
	}

	public String getCalendarDatetimePattern() {
		return messages.getMessage(getCalendarDatetimePatternKey(), null, getLocale());
	}

	public DateFormat getDateFormat() {
		String pattern = messages.getMessage(getDatePatternKey(), null, getLocale());
		return createFormat(pattern);
	}

	protected SimpleDateFormat createFormat(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		format.setLenient(isLenientFormats());
		return format;
	}

	public DateFormat getDatetimeFormat() {
		String pattern = messages.getMessage(getDatetimePatternKey(), null, getLocale());
		return createFormat(pattern);
	}

	public String getCalendarDatePatternKey() {
		return calendarDatePatternKey;
	}

	public void setCalendarDatePatternKey(String calendarDatePatternKey) {
		this.calendarDatePatternKey = calendarDatePatternKey;
	}

	public String getCalendarDatetimePatternKey() {
		return calendarDatetimePatternKey;
	}

	public void setCalendarDatetimePatternKey(String calendarDatetimePatternKey) {
		this.calendarDatetimePatternKey = calendarDatetimePatternKey;
	}

	public String getDatePatternKey() {
		return datePatternKey;
	}

	public void setDatePatternKey(String datePatternKey) {
		this.datePatternKey = datePatternKey;
	}

	public String getDatetimePatternKey() {
		return datetimePatternKey;
	}

	public void setDatetimePatternKey(String datetimePatternKey) {
		this.datetimePatternKey = datetimePatternKey;
	}

	public MessageSource getMessages() {
		return messages;
	}

	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	protected Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	public boolean isLenientFormats() {
		return lenientFormats;
	}

	public void setLenientFormats(boolean lenientFormats) {
		this.lenientFormats = lenientFormats;
	}

}
