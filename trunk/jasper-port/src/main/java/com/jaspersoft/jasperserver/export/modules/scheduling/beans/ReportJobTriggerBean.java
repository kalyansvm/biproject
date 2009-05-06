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
package com.jaspersoft.jasperserver.export.modules.scheduling.beans;

import java.util.Date;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;


/**
 * @author tkavanagh
 * @version $Id: ReportJobTriggerBean.java 8408 2007-05-29 23:29:12Z melih $
 */
public abstract class ReportJobTriggerBean {

	private long id;
	private int version;
	private String timezone;
	private byte startType;
	private Date startDate;
	private Date endDate;
	
	public void copyFrom(ReportJobTrigger trigger) {
		setId(trigger.getId());
		setVersion(trigger.getVersion());
		setTimezone(trigger.getTimezone());
		setStartType(trigger.getStartType());
		setStartDate(trigger.getStartDate());
		setEndDate(trigger.getEndDate());
	}
	
	public void copyTo(ReportJobTrigger trigger) {
		trigger.setTimezone(getTimezone());
		trigger.setStartType(getStartType());
		trigger.setStartDate(getStartDate());
		trigger.setEndDate(getEndDate());
	}
	
	public abstract ReportJobTrigger toJobTrigger();
	
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

	public byte getStartType() {
		return startType;
	}
	
	public void setStartType(byte startType) {
		this.startType = startType;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}	
	
}
