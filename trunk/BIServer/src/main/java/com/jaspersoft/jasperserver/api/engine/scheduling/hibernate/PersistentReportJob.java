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

import java.util.HashSet;
import java.util.Set;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PersistentReportJob.java 9820 2007-08-29 17:46:14Z lucian $
 */
public class PersistentReportJob {

	private long id;
	private int version;
	private String username;
	private String label;
	private String description;
	private PersistentReportJobSource source;
	private PersistentReportJobTrigger trigger;
	private String baseOutputFilename;
	private Set outputFormats;
	private String outputLocale;
	private PersistentReportJobMailNotification mailNotification;
	private PersistentReportJobRepositoryDestination contentRepositoryDestination;

	public PersistentReportJob() {
		version = ReportJob.VERSION_NEW;
		outputFormats = new HashSet();
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

	public PersistentReportJobSource getSource() {
		return source;
	}

	public void setSource(PersistentReportJobSource source) {
		this.source = source;
	}

	public PersistentReportJobTrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(PersistentReportJobTrigger trigger) {
		this.trigger = trigger;
	}

	public PersistentReportJobMailNotification getMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(PersistentReportJobMailNotification mailNotification) {
		this.mailNotification = mailNotification;
	}
	
	public void copyFrom(ReportJob job, HibernateTemplate hibernateTemplate) {
		if (getVersion() != job.getVersion()) {
			throw new JSException("jsexception.job.no.versions.match", new Object[] {new Integer(job.getVersion()), new Integer(getVersion())});
		}

		setLabel(job.getLabel());
		setDescription(job.getDescription());
		copySource(job);
		copyTrigger(job, hibernateTemplate);
		setBaseOutputFilename(job.getBaseOutputFilename());
		setOutputFormats(job.getOutputFormats() == null ? null : new HashSet(job.getOutputFormats()));
		setOutputLocale(job.getOutputLocale());
		copyContentRepostoryDestination(job);
		copyMailNotification(job, hibernateTemplate);
	}

	protected void copySource(ReportJob job) {
		if (getSource() == null) {
			setSource(new PersistentReportJobSource());
		}
		getSource().copyFrom(job.getSource());
	}

	protected void copyTrigger(ReportJob job, HibernateTemplate hibernateTemplate) {
		ReportJobTrigger jobTrigger = job.getTrigger();
		PersistentReportJobTrigger persistentTrigger = getTrigger();
		if (persistentTrigger != null && !persistentTrigger.supports(jobTrigger.getClass())) {
			hibernateTemplate.delete(persistentTrigger);
			persistentTrigger = null;
		}
		
		if (persistentTrigger == null) {
			if (jobTrigger instanceof ReportJobSimpleTrigger) {
				persistentTrigger = new PersistentReportJobSimpleTrigger();
			} else if (jobTrigger instanceof ReportJobCalendarTrigger) {
				persistentTrigger = new PersistentReportJobCalendarTrigger();
			} else {
				String quotedJobTrigger = "\"" + jobTrigger.getClass().getName() + "\"";
				throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedJobTrigger});
			}
			setTrigger(persistentTrigger);
		}
		
		persistentTrigger.copyFrom(jobTrigger);
	}

	protected void copyContentRepostoryDestination(ReportJob job) {
		if (getContentRepositoryDestination() == null) {
			setContentRepositoryDestination(new PersistentReportJobRepositoryDestination());
		}
		getContentRepositoryDestination().copyFrom(job.getContentRepositoryDestination());
	}

	protected void copyMailNotification(ReportJob job, HibernateTemplate hibernateTemplate) {
		ReportJobMailNotification jobMail = job.getMailNotification();
		PersistentReportJobMailNotification persistentMail = getMailNotification();
		if (jobMail == null) {
			if (persistentMail != null) {
				hibernateTemplate.delete(persistentMail);
				setMailNotification(null);
			}
		} else {
			if (persistentMail == null) {
				persistentMail = new PersistentReportJobMailNotification();
				setMailNotification(persistentMail);
			}
			persistentMail.copyFrom(jobMail);
		}
	}

	public ReportJob toClient() {
		ReportJob job = new ReportJob();
		job.setId(getId());
		job.setVersion(getVersion());
		job.setUsername(getUsername());
		job.setLabel(getLabel());
		job.setDescription(getDescription());
		job.setSource(getSource().toClient());
		job.setTrigger(getTrigger().toClient());
		job.setBaseOutputFilename(getBaseOutputFilename());
		job.setOutputFormats(getOutputFormats() == null ? null : new HashSet(getOutputFormats()));
		job.setOutputLocale(getOutputLocale());
		job.setContentRepositoryDestination(getContentRepositoryDestination().toClient());
		job.setMailNotification(getMailNotification() == null ? null : getMailNotification().toClient());
		return job;
	}

	public ReportJobSummary toSummary() {
		ReportJobSummary job = new ReportJobSummary();
		job.setId(getId());
		job.setVersion(getVersion());
		job.setReportUnitURI(getSource().getReportUnitURI());
		job.setUsername(getUsername());
		job.setLabel(getLabel());
		return job;
	}

	public PersistentReportJobRepositoryDestination getContentRepositoryDestination() {
		return contentRepositoryDestination;
	}

	public void setContentRepositoryDestination(
			PersistentReportJobRepositoryDestination contentRepositoryDestination) {
		this.contentRepositoryDestination = contentRepositoryDestination;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBaseOutputFilename() {
		return baseOutputFilename;
	}

	public void setBaseOutputFilename(String baseOutputFilename) {
		this.baseOutputFilename = baseOutputFilename;
	}

	public Set getOutputFormats() {
		return outputFormats;
	}

	public void setOutputFormats(Set outputFormats) {
		this.outputFormats = outputFormats;
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}

	public void cascadeSave(HibernateTemplate hibernateTemplate) {
		if (getTrigger().isNew()) {
			hibernateTemplate.save(getTrigger());
		}
		if (getContentRepositoryDestination() != null && getContentRepositoryDestination().isNew()) {
			hibernateTemplate.save(getContentRepositoryDestination());
		}
		if (getMailNotification() != null && getMailNotification().isNew()) {
			hibernateTemplate.save(getMailNotification());
		}		
	}

	public void delete(HibernateTemplate hibernateTemplate) {
		hibernateTemplate.delete(this);
		hibernateTemplate.delete(getTrigger());
		if (getContentRepositoryDestination() != null) {
			hibernateTemplate.delete(getContentRepositoryDestination());
		}
		if (getMailNotification() != null) {
			hibernateTemplate.delete(getMailNotification());
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOutputLocale() {
		return outputLocale;
	}

	public void setOutputLocale(String outputLocale) {
		this.outputLocale = outputLocale;
	}

}
