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
 * @version $Id: ReportJobMailNotificationBean.java 8408 2007-05-29 23:29:12Z melih $
 */


public class ReportJobMailNotificationBean {

	private long id;
	private int version;
	private String[] toAddresses;
	private String[] ccAddresses;
	private String[] bccAddresses;
	private String subject;
	private String messageText;
	private byte resultSendType;
	
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

	public String[] getToAddresses() {
		return toAddresses;
	}
	
	public void setToAddresses(String[] toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String[] getCcAddresses() {
		return ccAddresses;
	}
	
	public void setCcAddresses(String[] ccAddresses) {
		this.ccAddresses = ccAddresses;
	}
	
	public String[] getBccAddresses() {
		return bccAddresses;
	}
	public void setBccAddresses(String[] bccAddresses) {
		this.bccAddresses = bccAddresses;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}	
	
	public String getMessageText() {
		return messageText;
	}
	
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
	public byte getResultSendType() {
		return resultSendType;
	}
	
	public void setResultSendType(byte resultSendType) {
		this.resultSendType = resultSendType;
	}

}
