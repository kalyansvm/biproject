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

import java.sql.Timestamp;
import java.util.Date;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PersistentReportJobTrigger.java 8408 2007-05-29 23:29:12Z melih $
 */
public abstract class PersistentReportJobTrigger {

	private long id;
	private int version;
	private String timezone;
	private byte startType;
	private Timestamp startDate;
	private Timestamp endDate;
	
	public PersistentReportJobTrigger() {
		version = ReportJob.VERSION_NEW;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public byte getStartType() {
		return startType;
	}

	public void setStartType(byte startType) {
		this.startType = startType;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public void copyFrom(ReportJobTrigger trigger) {
		setTimezone(trigger.getTimezone());
		setStartType(trigger.getStartType());		
		setStartDate(toTimestamp(trigger.getStartDate()));
		setEndDate(toTimestamp(trigger.getEndDate()));
	}
	
	protected Timestamp toTimestamp(Date date) {
		return date == null ? null : new Timestamp(date.getTime());
	}

	public abstract ReportJobTrigger toClient();

	protected final void copyTo(ReportJobTrigger trigger) {
		trigger.setId(getId());
		trigger.setVersion(getVersion());
		trigger.setTimezone(getTimezone());
		trigger.setStartType(getStartType());
		trigger.setStartDate(getStartDate());
		trigger.setEndDate(getEndDate());
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}
	
	public abstract boolean supports(Class triggerClass);

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
}
