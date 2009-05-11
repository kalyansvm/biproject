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

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author Robert Matei (robert.matei@geminisols.ro)
 * @version $Id: DefaultCalendarFormatProvider.java 8408 2007-05-29 23:29:12Z melih $
 */
public class DefaultCalendarFormatProvider implements CalendarFormatProvider {

	public String getCalendarDatePattern() {
		String pattern = getDateFormatPattern();
		pattern = pattern.replaceAll("(?i)m+", "%m");
		pattern = pattern.replaceAll("(?i)d+", "%d");
		pattern = pattern.replaceAll("(?i)y+", "%Y");
		return pattern;
	}

	public String getCalendarDatetimePattern() {
		String pattern = getDatetimeFormatPattern();
		pattern = pattern.replaceAll("d+", "%d");
		pattern = pattern.replaceAll("M+", "%m");
		pattern = pattern.replaceAll("y+", "%Y");
		pattern = pattern.replaceAll("H+", "%H");
		pattern = pattern.replaceAll("h+", "%I");
		pattern = pattern.replaceAll("a+", "%p");
		pattern = pattern.replaceAll("(?<=^|[^%])m+", "%M");
		return pattern;
	}

	public DateFormat getDateFormat() {
		String pattern = getDateFormatPattern();		
		return new SimpleDateFormat(pattern);
	}

	public DateFormat getDatetimeFormat() {
		String pattern = getDatetimeFormatPattern();		
		return new SimpleDateFormat(pattern);
	}

	protected String getDatetimeFormatPattern() {
		SimpleDateFormat defaultFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, LocaleContextHolder.getLocale());
		String pattern = (defaultFormat).toPattern();
		pattern = pattern.replaceAll("d+","dd");
		pattern = pattern.replaceAll("M+","MM");
		pattern = pattern.replaceAll("y+","yyyy");
		pattern = pattern.replaceAll("H+","HH");
		pattern = pattern.replaceAll("h+","hh");
		pattern = pattern.replaceAll("k+","HH");
		pattern = pattern.replaceAll("K+","hh");
		pattern = pattern.replaceAll("m+","mm");
		return pattern;
	}

	protected String getDateFormatPattern() {
		SimpleDateFormat defaultFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, LocaleContextHolder.getLocale());		
		String pattern = (defaultFormat).toPattern();
		pattern = pattern.replaceAll("(?i)d+","dd");
		pattern = pattern.replaceAll("(?i)m+","MM");
		pattern = pattern.replaceAll("(?i)y+","yyyy");
		return pattern;
	}

}
