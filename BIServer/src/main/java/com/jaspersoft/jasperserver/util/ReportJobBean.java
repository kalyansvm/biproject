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
package com.jaspersoft.jasperserver.util;


/**
 * @author tkavanagh
 * @version $Id: ReportJobBean.java 8408 2007-05-29 23:29:12Z melih $
 */


public class ReportJobBean {

	private long id;
	private int version;
	private String username;
	private String label;
	private String description;
	private ReportJobTriggerBean trigger;
	private ReportJobSourceBean source;
	private String baseOutputFilename;
	private byte[] outputFormats;
	private String outputLocale;		// todo: ### new field, need to populate
	private ReportJobRepositoryDestinationBean contentRepositoryDestination;
	private ReportJobMailNotificationBean mailNotification;
	
	public String getBaseOutputFilename() {
		return baseOutputFilename;
	}
	
	public void setBaseOutputFilename(String baseOutputFilename) {
		this.baseOutputFilename = baseOutputFilename;
	}
	
	public ReportJobRepositoryDestinationBean getContentRepositoryDestination() {
		return contentRepositoryDestination;
	}
	
	public void setContentRepositoryDestination(
			ReportJobRepositoryDestinationBean contentRepositoryDestination) {
		this.contentRepositoryDestination = contentRepositoryDestination;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public ReportJobMailNotificationBean getMailNotification() {
		return mailNotification;
	}
	
	public void setMailNotification(ReportJobMailNotificationBean mailNotification) {
		this.mailNotification = mailNotification;
	}
	
	public byte[] getOutputFormats() {
		return outputFormats;
	}
	
	public void setOutputFormats(byte[] outputFormats) {
		this.outputFormats = outputFormats;
	}
	
	public String getOutputLocale() {
		return outputLocale;
	}

	public void setOutputLocale(String outputLocale) {
		this.outputLocale = outputLocale;
	}

	public ReportJobSourceBean getSource() {
		return source;
	}
	
	public void setSource(ReportJobSourceBean source) {
		this.source = source;
	}
	
	public ReportJobTriggerBean getTrigger() {
		return trigger;
	}
	
	public void setTrigger(ReportJobTriggerBean trigger) {
		this.trigger = trigger;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
