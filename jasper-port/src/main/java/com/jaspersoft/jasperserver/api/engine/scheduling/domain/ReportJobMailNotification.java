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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobMailNotification.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ReportJobMailNotification implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final byte RESULT_SEND = 1;
	public static final byte RESULT_SEND_ATTACHMENT = 2;
	
	private long id;
	private int version = ReportJob.VERSION_NEW;
	private List toAddresses;
	private List ccAddresses;
	private List bccAddresses;
	private String subject;
	private String messageText;
	private byte resultSendType = RESULT_SEND;
	private boolean skipEmptyReports;

	public ReportJobMailNotification() {
		super();
		toAddresses = new ArrayList();
		ccAddresses = new ArrayList();
		bccAddresses = new ArrayList();
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List getBccAddresses() {
		return bccAddresses;
	}

	public void setBccAddresses(List bccAddresses) {
		this.bccAddresses = bccAddresses;
	}
	
	public void addBcc(String address) {
		this.bccAddresses.add(address);
	}

	public List getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(List ccAddresses) {
		this.ccAddresses = ccAddresses;
	}
	
	public void addCc(String address) {
		this.ccAddresses.add(address);
	}

	public List getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(List toAddresses) {
		this.toAddresses = toAddresses;
	}
	
	public void addTo(String address) {
		this.toAddresses.add(address);
	}

	public boolean isEmpty() {
		return getToAddresses().isEmpty();
	}

	public boolean isSkipEmptyReports() {
		return skipEmptyReports;
	}

	public void setSkipEmptyReports(boolean skipEmptyReports) {
		this.skipEmptyReports = skipEmptyReports;
	}
}
