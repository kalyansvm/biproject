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

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobSimpleTriggerBean.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ReportJobSimpleTriggerBean extends ReportJobTriggerBean {
	
	private int occurrenceCount;
	private Integer recurrenceInterval;
	private Byte recurrenceIntervalUnit;
	
	public void copyFrom(ReportJobTrigger trigger) {
		super.copyFrom(trigger);
		
		ReportJobSimpleTrigger simpleTrigger = (ReportJobSimpleTrigger) trigger;
		setOccurrenceCount(simpleTrigger.getOccurrenceCount());
		setRecurrenceInterval(simpleTrigger.getRecurrenceInterval());
		setRecurrenceIntervalUnit(simpleTrigger.getRecurrenceIntervalUnit());
	}

	public ReportJobTrigger toJobTrigger() {
		return new ReportJobSimpleTrigger();
	}
	
	public void copyTo(ReportJobTrigger trigger) {
		super.copyTo(trigger);
		
		ReportJobSimpleTrigger simpleTrigger = (ReportJobSimpleTrigger) trigger;
		simpleTrigger.setOccurrenceCount(getOccurrenceCount());
		simpleTrigger.setRecurrenceInterval(getRecurrenceInterval());
		simpleTrigger.setRecurrenceIntervalUnit(getRecurrenceIntervalUnit());
	}
	
	public int getOccurrenceCount() {
		return occurrenceCount;
	}
	public void setOccurrenceCount(int occurrenceCount) {
		this.occurrenceCount = occurrenceCount;
	}
	
	public Integer getRecurrenceInterval() {
		return recurrenceInterval;
	}
	public void setRecurrenceInterval(Integer recurrenceInterval) {
		this.recurrenceInterval = recurrenceInterval;
	}
	
	public Byte getRecurrenceIntervalUnit() {
		return recurrenceIntervalUnit;
	}
	public void setRecurrenceIntervalUnit(Byte recurrenceIntervalUnit) {
		this.recurrenceIntervalUnit = recurrenceIntervalUnit;
	}

}
