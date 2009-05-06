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
package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PersistentReportJobCalendarTrigger.java 8408 2007-05-29 23:29:12Z melih $
 */
public class PersistentReportJobCalendarTrigger extends PersistentReportJobTrigger {
	
	private static final String ENUM_SEPARATOR = ",";
	private static final String ENUM_SEPARATOR_REGEX = ",";
	
	private String minutes;
	private String hours;
	private byte daysType;
	private String weekDays;
	private String monthDays;
	private String months;
	
	public PersistentReportJobCalendarTrigger() {
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public void copyFrom(ReportJobTrigger trigger) {
		super.copyFrom(trigger);
		
		ReportJobCalendarTrigger calendarTrigger = (ReportJobCalendarTrigger) trigger;
		setMinutes(calendarTrigger.getMinutes());
		setHours(calendarTrigger.getHours());
		setDaysType(calendarTrigger.getDaysType());
		setWeekDays(toEnumerationString(calendarTrigger.getWeekDays()));
		setMonthDays(calendarTrigger.getMonthDays());
		setMonths(toEnumerationString(calendarTrigger.getMonths()));
	}

	public ReportJobTrigger toClient() {
		ReportJobCalendarTrigger trigger = new ReportJobCalendarTrigger();
		super.copyTo(trigger);
		trigger.setMinutes(getMinutes());
		trigger.setHours(getHours());
		trigger.setDaysType(getDaysType());
		trigger.setWeekDays(parseEnumerationString(getWeekDays()));
		trigger.setMonthDays(getMonthDays());
		trigger.setMonths(parseEnumerationString(getMonths()));
		return trigger;
	}

	public boolean supports(Class triggerClass) {
		return ReportJobCalendarTrigger.class.isAssignableFrom(triggerClass);
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

	public String getMonths() {
		return months;
	}

	public void setMonths(String months) {
		this.months = months;
	}

	public String getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(String weekDays) {
		this.weekDays = weekDays;
	}

	protected String toEnumerationString(Set vals) {
		if (vals == null || vals.isEmpty()) {
			return null;
		}
		StringBuffer str = new StringBuffer();
		SortedSet sorted = new TreeSet(vals);
		for (Iterator it = sorted.iterator(); it.hasNext();) {
			Byte val = (Byte) it.next();
			str.append(val.byteValue());
			str.append(ENUM_SEPARATOR);
		}
		return str.substring(0, str.length() - 1);
	}

	protected SortedSet parseEnumerationString(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		SortedSet valsSet = new TreeSet();
		String[] vals = str.split(ENUM_SEPARATOR_REGEX);
		for (int i = 0; i < vals.length; i++) {
			String strVal = vals[i];
			try {
				Byte val = Byte.valueOf(strVal);
				valsSet.add(val);
			} catch (NumberFormatException e) {
				throw new JSExceptionWrapper(e);
			}
		}
		return valsSet;
	}

}
