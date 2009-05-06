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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import java.io.Serializable;
import java.util.SortedSet;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobCalendarTrigger.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ReportJobCalendarTrigger extends ReportJobTrigger implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final byte DAYS_TYPE_ALL = 1;
	public static final byte DAYS_TYPE_WEEK = 2;
	public static final byte DAYS_TYPE_MONTH = 3;
	
	private String minutes;
	private String hours;
	private byte daysType;
	private SortedSet weekDays;
	private String monthDays;
	private SortedSet months;
	
	public ReportJobCalendarTrigger() {
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public byte getDaysType() {
		return daysType;
	}

	public void setDaysType(byte daysType) {
		this.daysType = daysType;
	}

	public String getMonthDays() {
		return monthDays;
	}

	public void setMonthDays(String monthDays) {
		this.monthDays = monthDays;
	}

	public SortedSet getMonths() {
		return months;
	}

	public void setMonths(SortedSet months) {
		this.months = months;
	}

	public SortedSet getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(SortedSet weekDays) {
		this.weekDays = weekDays;
	}

}
