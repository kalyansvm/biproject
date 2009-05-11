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
package com.jaspersoft.jasperserver.api.engine.scheduling;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulerListener;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportSchedulingFacade.java 12787 2008-04-02 18:01:13Z lucian $
 */
public class ReportSchedulingFacade 
	implements ReportSchedulingService, ReportSchedulingInternalService, ReportSchedulerListener, InitializingBean {
	
	private static final Log log = LogFactory.getLog(ReportSchedulingFacade.class);
	
	private ReportJobsPersistenceService persistenceService;
	private ReportJobsInternalService jobsInternalService;
	private ReportJobsScheduler scheduler;
	private ReportJobValidator validator;

	public ReportJobsPersistenceService getPersistenceService() {
		return persistenceService;
	}

	public void setPersistenceService(
			ReportJobsPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public ReportJobsScheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(ReportJobsScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public ReportJobValidator getValidator() {
		return validator;
	}

	public void setValidator(ReportJobValidator validator) {
		this.validator = validator;
	}

	public ReportJobsInternalService getJobsInternalService() {
		return jobsInternalService;
	}

	public void setJobsInternalService(ReportJobsInternalService jobsInternalService) {
		this.jobsInternalService = jobsInternalService;
	}

	public void afterPropertiesSet() throws Exception {
		getScheduler().addReportSchedulerListener(this);
	}

	public ReportJob scheduleJob(ExecutionContext context, ReportJob job) {
		validate(context, job);
		ReportJob savedJob = persistenceService.saveJob(context, job);
		scheduler.scheduleJob(context, savedJob);
		return savedJob;
	}

	protected void validate(ExecutionContext context, ReportJob job) {
		ValidationErrors errors = validator.validateJob(context, job);
		if (errors.isError()) {
			throw new JSValidationException(errors);
		}
	}

	public List getScheduledJobs(ExecutionContext context, String reportUnitURI) {
		List jobs = persistenceService.listJobs(context, reportUnitURI);
		setSummaryRuntimeInformation(context, jobs);
		return jobs;
	}

	public List getScheduledJobs(ExecutionContext context) {
		List jobs = persistenceService.listJobs(context);
		setSummaryRuntimeInformation(context, jobs);
		return jobs;
	}

	protected void setSummaryRuntimeInformation(ExecutionContext context, List jobs) {
		if (jobs != null && !jobs.isEmpty()) {
			long[] jobIds = new long[jobs.size()];
			int idx = 0;
			for (Iterator it = jobs.iterator(); it.hasNext(); ++idx) {
				ReportJobSummary job = (ReportJobSummary) it.next();
				jobIds[idx] = job.getId();
			}
			
			ReportJobRuntimeInformation[] runtimeInfos = scheduler.getJobsRuntimeInformation(context, jobIds);
			
			idx = 0;
			for (Iterator it = jobs.iterator(); it.hasNext(); ++idx) {
				ReportJobSummary job = (ReportJobSummary) it.next();
				job.setRuntimeInformation(runtimeInfos[idx]);
			}			
		}
	}

	public void removeScheduledJob(ExecutionContext context, long jobId) {
		deleteJob(context, jobId);
	}

	public void removeScheduledJobs(ExecutionContext context, long[] jobIds) {
		for (int i = 0; i < jobIds.length; i++) {
			long jobId = jobIds[i];
			deleteJob(context, jobId);
		}
	}

	public void removeReportUnitJobs(String reportUnitURI) {
		long[] deletedJobIds = getJobsInternalService().deleteReportUnitJobs(reportUnitURI);
		unscheduleJobs(deletedJobIds);		
	}

	protected void unscheduleJobs(long[] deletedJobIds) {
		if (deletedJobIds != null && deletedJobIds.length > 0) {
			for (int i = 0; i < deletedJobIds.length; i++) {
				long jobId = deletedJobIds[i];
				scheduler.removeScheduledJob(null, jobId);				
			}
		}
	}

	protected void deleteJob(ExecutionContext context, long jobId) {
		scheduler.removeScheduledJob(context, jobId);
		persistenceService.deleteJob(context, new ReportJobIdHolder(jobId));
	}

	public ReportJob getScheduledJob(ExecutionContext context, long jobId) {
		return persistenceService.loadJob(context, new ReportJobIdHolder(jobId));
	}

	public void reportJobFinalized(long jobId) {
		if (log.isDebugEnabled()) {
			log.debug("Job " + jobId + " finalized, deleting data");
		}

		getJobsInternalService().deleteJob(jobId);
	}

	public void updateScheduledJob(ExecutionContext context, ReportJob job) {
		validate(context, job);

		ReportJobTrigger origTrigger = job.getTrigger();
		long origTriggerId = origTrigger.getId();
		int origTriggerVersion = origTrigger.getVersion();

		ReportJob savedJob = persistenceService.updateJob(context, job);
		ReportJobTrigger updatedTrigger = savedJob.getTrigger();

		if (updatedTrigger.getId() != origTriggerId || updatedTrigger.getVersion() != origTriggerVersion) {
			scheduler.rescheduleJob(context, savedJob);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Trigger attributes not changed for job " + job.getId() + ", the job will not be rescheduled");
			}
		}
	}

	public ValidationErrors validateJob(ExecutionContext context, ReportJob job) {
		ValidationErrors errors = validator.validateJob(context, job);
		if (!hasTriggerErrors(errors)) {
			scheduler.validate(job, errors);
		}
		return errors;
	}

	protected boolean hasTriggerErrors(ValidationErrors errors) {
		boolean triggerError = false;
		for(Iterator it = errors.getErrors().iterator(); !triggerError && it.hasNext(); ) {
			ValidationError error = (ValidationError) it.next();
			String field = error.getField();
			if (field != null && (field.equals("trigger") || field.startsWith("trigger."))) {
				triggerError = true;
			}
		}
		return triggerError;
	}

	public ReportJob saveJob(ExecutionContext context, ReportJob job) {
		validateSaveJob(context, job);
		ReportJob savedJob = jobsInternalService.saveJob(context, job, false);
		scheduler.scheduleJob(context, savedJob);
		return savedJob;
	}

	protected void validateSaveJob(ExecutionContext context, ReportJob job) {
		ValidationErrors errors = validator.validateJob(context, job);
		
		// allow jobs with past start dates to be saved
		errors.removeError("error.before.current.date", "trigger.startDate");
		
		if (errors.isError()) {
			throw new JSValidationException(errors);
		}
	}

	public void updateReportUnitURI(String oldURI, String newURI) {
		getJobsInternalService().updateReportUnitURI(oldURI, newURI);
	}
}
