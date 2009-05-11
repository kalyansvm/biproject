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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PersistentReportJobMailNotification.java 8408 2007-05-29 23:29:12Z melih $
 */
public class PersistentReportJobMailNotification {
	
	private long id;
	private int version;
	private List recipients;
	private String subject;
	private String messageText;
	private byte resultSendType;
	private boolean skipEmptyReports;

	public PersistentReportJobMailNotification() {
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

	public List getRecipients() {
		return recipients;
	}

	public void setRecipients(List toAddresses) {
		this.recipients = toAddresses;
	}

	public void copyFrom(ReportJobMailNotification mailNotification) {
		copyRecipientsFrom(mailNotification);
		setSubject(mailNotification.getSubject());
		setMessageText(mailNotification.getMessageText());
		setResultSendType(mailNotification.getResultSendType());
		setSkipEmptyReports(mailNotification.isSkipEmptyReports());
	}

	protected void copyRecipientsFrom(ReportJobMailNotification mailNotification) {
		List newRecipients = new ArrayList();
		collectRecipients(newRecipients, mailNotification.getToAddresses(), PersistentReportJobMailRecipient.TYPE_TO);
		collectRecipients(newRecipients, mailNotification.getCcAddresses(), PersistentReportJobMailRecipient.TYPE_CC);
		collectRecipients(newRecipients, mailNotification.getBccAddresses(), PersistentReportJobMailRecipient.TYPE_BCC);
		setRecipients(newRecipients);
	}

	protected void collectRecipients(List recipientsList, List addresses, byte type) {
		if (addresses != null && !addresses.isEmpty()) {
			for (Iterator it = addresses.iterator(); it.hasNext();) {
				String address = (String) it.next();
				PersistentReportJobMailRecipient recipient = new PersistentReportJobMailRecipient();
				recipient.setType(type);
				recipient.setAddress(address);
				recipientsList.add(recipient);
			}
		}
	}

	public ReportJobMailNotification toClient() {
		ReportJobMailNotification mail = new ReportJobMailNotification();
		mail.setId(getId());
		mail.setVersion(getVersion());
		copyAddressesTo(mail);
		mail.setSubject(getSubject());
		mail.setMessageText(getMessageText());
		mail.setResultSendType(getResultSendType());
		mail.setSkipEmptyReports(isSkipEmptyReports());
		return mail;
	}

	protected void copyAddressesTo(ReportJobMailNotification mail) {
		Map collectedAddresses = new HashMap();
		collectedAddresses.put(new Byte(PersistentReportJobMailRecipient.TYPE_TO), new ArrayList());
		collectedAddresses.put(new Byte(PersistentReportJobMailRecipient.TYPE_BCC), new ArrayList());
		collectedAddresses.put(new Byte(PersistentReportJobMailRecipient.TYPE_CC), new ArrayList());
		
		for (Iterator it = getRecipients().iterator(); it.hasNext();) {
			PersistentReportJobMailRecipient recipient = (PersistentReportJobMailRecipient) it.next();
			((List) collectedAddresses.get(new Byte(recipient.getType()))).add(recipient.getAddress());
		}
		
		mail.setToAddresses((List) collectedAddresses.get(new Byte(PersistentReportJobMailRecipient.TYPE_TO)));
		mail.setCcAddresses((List) collectedAddresses.get(new Byte(PersistentReportJobMailRecipient.TYPE_CC)));
		mail.setBccAddresses((List) collectedAddresses.get(new Byte(PersistentReportJobMailRecipient.TYPE_BCC)));
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}

	public boolean isSkipEmptyReports() {
		return skipEmptyReports;
	}

	public void setSkipEmptyReports(boolean skipEmptyReports) {
		this.skipEmptyReports = skipEmptyReports;
	}

}
