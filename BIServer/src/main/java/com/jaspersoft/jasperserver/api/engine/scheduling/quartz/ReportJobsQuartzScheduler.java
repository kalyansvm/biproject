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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.TriggerUtils;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulerListener;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobsQuartzScheduler.java 12072 2008-02-13 15:31:12Z lucian $
 */
public class ReportJobsQuartzScheduler implements ReportJobsScheduler, InitializingBean {

	protected static final Log log = LogFactory.getLog(ReportJobsQuartzScheduler.class);

	private static final String GROUP = "ReportJobs";
	private static final String TRIGGER_LISTENER_NAME = "reportSchedulerTriggerListener";
	
	private static final long COEFFICIENT_MINUTE = 60l * 1000l;
	private static final long COEFFICIENT_HOUR = 60l * COEFFICIENT_MINUTE;
	private static final long COEFFICIENT_DAY = 24l * COEFFICIENT_HOUR;
	private static final long COEFFICIENT_WEEK = 7l * COEFFICIENT_DAY;
	
	private static final int COUNT_WEEKDAYS = 7;
	private static final int COUNT_MONTHS = 12;
	
	private Scheduler scheduler;
	private Class reportExecutionJobClass;
	
	private final Set listeners;
	private final SchedulerListener schedulerListener;
	private final TriggerListener triggerListener;
	
	public ReportJobsQuartzScheduler() {
		listeners = new HashSet();

		schedulerListener = new ReportSchedulerQuartzListener();
		triggerListener = new ReportSchedulerTriggerListener(TRIGGER_LISTENER_NAME);
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Class getReportExecutionJobClass() {
		return reportExecutionJobClass;
	}

	public void setReportExecutionJobClass(Class reportExecutionJobClass) {
		this.reportExecutionJobClass = reportExecutionJobClass;
	}


	public void afterPropertiesSet() {
		try {
			getScheduler().addSchedulerListener(schedulerListener);
			getScheduler().addTriggerListener(triggerListener);
		} catch (SchedulerException e) {
			log.error("Error (de)registering Quartz listener", e);
			throw new JSExceptionWrapper(e);
		}
	}
	
	public void scheduleJob(ExecutionContext context, ReportJob job) {
		JobDetail jobDetail = createJobDetail(job);
		Trigger trigger = createTrigger(job);
		try {
			scheduler.scheduleJob(jobDetail, trigger);
			
			if (log.isDebugEnabled()) {
				log.debug("Created job " + jobDetail.getFullName() + " and trigger " + trigger.getFullName() + " for job " + job.getId());
			}
		} catch (SchedulerException e) {
			log.error("Error scheduling Quartz job", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected JobDetail createJobDetail(ReportJob job) {
		String jobName = jobName(job.getId());
		JobDetail jobDetail = new JobDetail(jobName, GROUP, getReportExecutionJobClass(), false, false, false);
		return jobDetail;
	}


	public void rescheduleJob(ExecutionContext context, ReportJob job) {
		try {
			Trigger oldTrigger = getReportJobTrigger(job.getId());
			
			String jobName = jobName(job.getId());
			Trigger trigger = createTrigger(job);
			trigger.setJobName(jobName);
			trigger.setJobGroup(GROUP);
			
			if (oldTrigger == null) {
				scheduler.scheduleJob(trigger);
				
				if (log.isDebugEnabled()) {
					log.debug("Scheduled trigger " + trigger.getFullName() + " for job " + job.getId());
				}
			} else {
				scheduler.rescheduleJob(oldTrigger.getName(), oldTrigger.getGroup(), trigger);

				if (log.isDebugEnabled()) {
					log.debug("Trigger " + oldTrigger.getFullName() + " rescheduled by " + trigger.getFullName() + " for job " + job.getId());
				}
			}
		} catch (SchedulerException e) {
			log.error("Error rescheduling Quartz job", e);
			throw new JSExceptionWrapper(e);
		}
	}
	
	protected Trigger getReportJobTrigger(long jobId) throws SchedulerException {
		Trigger trigger;
		String jobName = jobName(jobId);
		Trigger[] triggers = scheduler.getTriggersOfJob(jobName, GROUP);
		if (triggers == null || triggers.length == 0) {
			trigger = null;
			
			if (log.isDebugEnabled()) {
				log.debug("No trigger found for job " + jobId);
			}
		} else if (triggers.length == 1) {
			trigger = triggers[0];

			if (log.isDebugEnabled()) {
				log.debug("Trigger " + trigger.getFullName() + " found for job " + jobId);
			}
		} else {
			throw new JSException("jsexception.job.has.more.than.one.trigger", new Object[] {new Long(jobId)});
		}
		return trigger;
	}

	protected String jobName(long jobId) {
		return "job_" + jobId;
	}

	protected String triggerName(ReportJobTrigger jobTrigger) {
		return "trigger_" + jobTrigger.getId() + "_" + jobTrigger.getVersion();
	}

	protected Trigger createTrigger(ReportJob reportJob) {
		Trigger trigger;
		ReportJobTrigger jobTrigger = reportJob.getTrigger();
		if (jobTrigger instanceof ReportJobSimpleTrigger) {
			trigger = createTrigger((ReportJobSimpleTrigger) jobTrigger);
		} else if (jobTrigger instanceof ReportJobCalendarTrigger) {
			trigger = createTrigger((ReportJobCalendarTrigger) jobTrigger);
		} else {
			String quotedJobTrigger = "\"" + jobTrigger.getClass().getName() + "\"";
			throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedJobTrigger});
		}
		
		JobDataMap jobDataMap = trigger.getJobDataMap();
		jobDataMap.put(ReportExecutionJob.JOB_DATA_KEY_DETAILS_ID, new Long(reportJob.getId()));
		jobDataMap.put(ReportExecutionJob.JOB_DATA_KEY_USERNAME, reportJob.getUsername());
		
		trigger.addTriggerListener(TRIGGER_LISTENER_NAME);
		
		return trigger;
	}
	
	protected Trigger createTrigger(ReportJobSimpleTrigger jobTrigger) {
		String triggerName = triggerName(jobTrigger);
		Date startDate = getStartDate(jobTrigger);
		Date endDate = getEndDate(jobTrigger);
			
		int repeatCount = repeatCount(jobTrigger);
		SimpleTrigger trigger;
		if (repeatCount == 0) {
			trigger = new SimpleTrigger(triggerName, GROUP, startDate);
		} else {
			int recurrenceInterval = jobTrigger.getRecurrenceInterval().intValue();

			switch (jobTrigger.getRecurrenceIntervalUnit().byteValue()) {
			case ReportJobSimpleTrigger.INTERVAL_MINUTE:
				trigger = new SimpleTrigger(triggerName, GROUP, startDate, endDate, repeatCount, recurrenceInterval * COEFFICIENT_MINUTE);
				break;
			case ReportJobSimpleTrigger.INTERVAL_HOUR:
				trigger = new SimpleTrigger(triggerName, GROUP, startDate, endDate, repeatCount, recurrenceInterval * COEFFICIENT_HOUR);
				break;
			case ReportJobSimpleTrigger.INTERVAL_DAY:
				trigger = new SimpleTrigger(triggerName, GROUP, startDate, endDate, repeatCount, recurrenceInterval * COEFFICIENT_DAY);
				break;
			case ReportJobSimpleTrigger.INTERVAL_WEEK:
				trigger = new SimpleTrigger(triggerName, GROUP, startDate, endDate, repeatCount, recurrenceInterval * COEFFICIENT_WEEK);
				break;
			default:
				throw new JSException("jsexception.job.unknown.interval.unit", new Object[] {jobTrigger.getRecurrenceIntervalUnit()});
			}
		}
		
		trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);

		return trigger;
	}

	protected Date getEndDate(ReportJobTrigger jobTrigger) {
		return translateFromTriggerTimeZone(jobTrigger, jobTrigger.getEndDate());
	}
	
	protected Date translateFromTriggerTimeZone(ReportJobTrigger jobTrigger, Date date) {
		if (date != null) {
			TimeZone tz = getTriggerTimeZone(jobTrigger);
			if (tz != null) {
				date = TriggerUtils.translateTime(date, TimeZone.getDefault(), tz);
			}
		}
		return date;
	}

	protected Date getStartDate(ReportJobTrigger jobTrigger) {
		Date startDate;
		switch (jobTrigger.getStartType()) {
		case ReportJobTrigger.START_TYPE_NOW:
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			startDate = calendar.getTime();
			break;
		case ReportJobTrigger.START_TYPE_SCHEDULE:
			startDate = translateFromTriggerTimeZone(jobTrigger, jobTrigger.getStartDate());
			break;
		default:
			throw new JSException("jsexception.job.unknown.start.type", new Object[] {new Byte(jobTrigger.getStartType())});
		}
		return startDate;
	}

	protected int repeatCount(ReportJobSimpleTrigger jobTrigger) {
		int recurrenceCount = jobTrigger.getOccurrenceCount();
		int repeatCount;
		switch (recurrenceCount) {
		case ReportJobSimpleTrigger.RECUR_INDEFINITELY:
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
			break;
		default:
			repeatCount = recurrenceCount - 1;
			break;
		}
		return repeatCount;
	}
	
	protected Trigger createTrigger(ReportJobCalendarTrigger jobTrigger) {
		String triggerName = triggerName(jobTrigger);
		Date startDate = getStartDate(jobTrigger);
		Date endDate = getEndDate(jobTrigger);
		
		String cronExpression = getCronExpression(jobTrigger);
		
		try {
			CronTrigger trigger = new CronTrigger(triggerName, GROUP, cronExpression);
			trigger.setStartTime(startDate);
			trigger.setEndTime(endDate);
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
			
			TimeZone timeZone = getTriggerTimeZone(jobTrigger);
			if (timeZone != null) {
				trigger.setTimeZone(timeZone);
			}
			
			return trigger;
		} catch (ParseException e) {
			log.error("Error creating Quartz Cron trigger", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected TimeZone getTriggerTimeZone(ReportJobTrigger jobTrigger) {
		String tzId = jobTrigger.getTimezone();
		TimeZone tz;
		if (tzId == null || tzId.length() == 0) {
			tz = null;
		} else {
			tz = TimeZone.getTimeZone(tzId);
			if (tz == null) {
				String quotedTzId = "\"" + tzId + "\"";
				throw new JSException("jsexception.unknown.timezone", new Object[] {quotedTzId});
			}
		}
		return tz;
	}

	protected String getCronExpression(ReportJobCalendarTrigger jobTrigger) {
		String minutes = jobTrigger.getMinutes();
		String hours = jobTrigger.getHours();
		String weekDays;
		String monthDays;
		switch (jobTrigger.getDaysType()) {
		case ReportJobCalendarTrigger.DAYS_TYPE_ALL:
			weekDays = "?";
			monthDays = "*";
			break;
		case ReportJobCalendarTrigger.DAYS_TYPE_WEEK:
			weekDays = enumerateCronVals(jobTrigger.getWeekDays(), COUNT_WEEKDAYS);
			monthDays = "?";
			break;
		case ReportJobCalendarTrigger.DAYS_TYPE_MONTH:
			weekDays = "?";
			monthDays = jobTrigger.getMonthDays();
			break;
		default:
			throw new JSException("jsexception.job.unknown.calendar.trigger.days.type", new Object[] {new Byte(jobTrigger.getDaysType())});
		}
		String months = enumerateCronVals(jobTrigger.getMonths(), COUNT_MONTHS);
		
		StringBuffer cronExpression = new StringBuffer();
		cronExpression.append("0 ");
		cronExpression.append(minutes);
		cronExpression.append(' ');
		cronExpression.append(hours);
		cronExpression.append(' ');
		cronExpression.append(monthDays);
		cronExpression.append(' ');
		cronExpression.append(months);
		cronExpression.append(' ');
		cronExpression.append(weekDays);
		
		return cronExpression.toString();
	}

	protected String enumerateCronVals(SortedSet vals, int totalCount) {
		if (vals == null || vals.isEmpty()) {
			throw new JSException("jsexception.no.values.to.enumerate");
		}
		
		if (vals.size() == totalCount) {
			return "*";
		}
		
		StringBuffer enumStr = new StringBuffer();
		for (Iterator it = vals.iterator(); it.hasNext();) {
			Byte val = (Byte) it.next();
			enumStr.append(val.byteValue());
			enumStr.append(',');
		}
		return enumStr.substring(0, enumStr.length() - 1);
	}

	public void removeScheduledJob(ExecutionContext context, long jobId) {
		try {
			String jobName = jobName(jobId);
			if (scheduler.deleteJob(jobName, GROUP)) {
				if (log.isDebugEnabled()) {
					log.debug("Job " + jobName + "deleted");
				}
			} else {
				log.info("Quartz job " + jobId + " was not found to be deleted");
			}
		} catch (SchedulerException e) {
			log.error("Error deleting Quartz job " + jobId, e);
			throw new JSExceptionWrapper(e);
		}
	}


	public ReportJobRuntimeInformation[] getJobsRuntimeInformation(ExecutionContext context, long[] jobIds) {
		if (jobIds == null) {
			return null;
		}
		
		try {
			Set executingJobNames = getExecutingJobNames();
			ReportJobRuntimeInformation[] infos = new ReportJobRuntimeInformation[jobIds.length];
			for (int i = 0; i < jobIds.length; i++) {
				infos[i] = getJobRuntimeInformation(jobIds[i], executingJobNames);
			}
			return infos;
		} catch (SchedulerException e) {
			log.error("Error while fetching Quartz runtime information", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected ReportJobRuntimeInformation getJobRuntimeInformation(long jobId, Set executingJobNames) throws SchedulerException {
		ReportJobRuntimeInformation info = new ReportJobRuntimeInformation();
		Trigger trigger = getReportJobTrigger(jobId);
		if (trigger == null) {
			info.setState(ReportJobRuntimeInformation.STATE_UNKNOWN);
		} else {
			info.setPreviousFireTime(trigger.getPreviousFireTime());
			if (trigger.mayFireAgain()) {
				info.setNextFireTime(trigger.getNextFireTime());
			}
			
			byte state = getJobState(trigger, executingJobNames);
			info.setState(state);
		}
		return info;
	}

	protected byte getJobState(Trigger trigger, Set executingJobNames) throws SchedulerException {
		byte state;
		int quartzState = scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
		switch (quartzState) {
		case Trigger.STATE_NORMAL:
		case Trigger.STATE_BLOCKED:
			state = executingJobNames.contains(trigger.getJobName()) ? 
					ReportJobRuntimeInformation.STATE_EXECUTING : 
						ReportJobRuntimeInformation.STATE_NORMAL;
			break;
		case Trigger.STATE_COMPLETE:
			state = ReportJobRuntimeInformation.STATE_COMPLETE;
			break;
		case Trigger.STATE_PAUSED:
			state = ReportJobRuntimeInformation.STATE_PAUSED;
			break;
		case Trigger.STATE_ERROR:
			state = ReportJobRuntimeInformation.STATE_ERROR;
			break;
		default:
			state = ReportJobRuntimeInformation.STATE_UNKNOWN;
			break;
		}
		return state;
	}

	protected Set getExecutingJobNames() throws SchedulerException {
		List executingJobs = scheduler.getCurrentlyExecutingJobs();
		Set executingJobNames = new HashSet();
		for (Iterator iter = executingJobs.iterator(); iter.hasNext();) {
			JobExecutionContext executionContext = (JobExecutionContext) iter.next();
			JobDetail jobDetail = executionContext.getJobDetail();
			if (jobDetail.getGroup().equals(GROUP)) {
				executingJobNames.add(jobDetail.getName());
			}
		}
		return executingJobNames;
	}

	public void addReportSchedulerListener(ReportSchedulerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public synchronized void removeReportSchedulerListener(ReportSchedulerListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	protected void notifyListenersOfFinalizedJob(long jobId) {
		synchronized (listeners) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				ReportSchedulerListener listener = (ReportSchedulerListener) it.next();
				listener.reportJobFinalized(jobId);
			}
		}
	}

	protected void reportTriggerFinalized(Trigger trigger) {
		long jobId = trigger.getJobDataMap().getLong(ReportExecutionJob.JOB_DATA_KEY_DETAILS_ID);
		notifyListenersOfFinalizedJob(jobId);
	}

	protected class ReportSchedulerQuartzListener implements SchedulerListener {
		
		public ReportSchedulerQuartzListener() {
		}

		public void jobScheduled(Trigger trigger) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz job " + trigger.getFullJobName() + " scheduled by trigger " + trigger.getFullName());
			}
		}

		public void jobUnscheduled(String name, String group) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz job unscheduled " + group + "." + name);
			}
		}

		public void triggerFinalized(Trigger trigger) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz trigger finalized " + trigger.getFullName());
			}
			
			if (trigger.getGroup().equals(GROUP)) {
				reportTriggerFinalized(trigger);
			}
		}

		public void triggersPaused(String name, String group) {
		}

		public void triggersResumed(String name, String group) {
		}

		public void jobsPaused(String name, String group) {
		}

		public void jobsResumed(String name, String group) {
		}

		public void schedulerError(String msg, SchedulerException cause) {
			if (log.isInfoEnabled()) {
				log.info("Quartz scheduler error: " + msg, cause);
			}
		}

		public void schedulerShutdown() {
			if (log.isInfoEnabled()) {
				log.info("Quartz scheduler shutdown");
			}
		}
		
	}
	
	
	protected class ReportSchedulerTriggerListener implements TriggerListener {

		private final String name;

		public ReportSchedulerTriggerListener(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public void triggerFired(Trigger trigger, JobExecutionContext context) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz trigger fired " + trigger.getFullName());
			}
		}

		public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
			return false;
		}

		public void triggerMisfired(Trigger trigger) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz trigger misfired " + trigger.getFullName());
			}
			
			if (trigger.getGroup().equals(GROUP) && trigger.getFireTimeAfter(new Date()) == null) {
				reportTriggerFinalized(trigger);
			}
		}

		public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz trigger complete " + trigger.getFullName() + " " + triggerInstructionCode);
			}
		}

	}


	public void validate(ReportJob job, ValidationErrors errors) {
		Trigger quartzTrigger = createTrigger(job);
		Date firstFireTime = quartzTrigger.computeFirstFireTime(null);
		if (firstFireTime == null) {
			errors.add(new ValidationErrorImpl("error.report.job.trigger.no.fire", null, null, "trigger"));
		}
	}

}
