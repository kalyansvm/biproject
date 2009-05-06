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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.ValidationUtil;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

import org.quartz.TriggerUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DefaultReportJobValidator.java 13173 2008-04-24 17:19:55Z lucian $
 */
public class DefaultReportJobValidator implements ReportJobValidator {

	private static final Pattern PATTERN_CRON_MINUTES;
	private static final Pattern PATTERN_CRON_HOURS;
	private static final Pattern PATTERN_CRON_MONTH_DAYS;
	private static final Pattern PATTERN_TIMESTAMP_FORMAT;
	
	static {
		String allPattern = "(\\*)";
		
		String minPattern = "(\\d|[0-5]\\d)";
		String minRangePattern = "(" + minPattern + "(\\-" + minPattern + ")?)";
		String minuteIncrementPattern = "(" + minPattern + "\\/\\d+)";
		PATTERN_CRON_MINUTES = Pattern.compile("^(" + minRangePattern + "(," + minRangePattern + ")*)|" + minuteIncrementPattern + "|" + allPattern + "$");
		
		String hourPattern = "(\\d|[01]\\d|2[0-3])";		
		String hourRangePattern = "(" + hourPattern + "(\\-" + hourPattern + ")?)";
		String hourIncrementPattern = "(" + hourPattern + "\\/\\d+)";
		PATTERN_CRON_HOURS = Pattern.compile("^(" + hourRangePattern + "(," + hourRangePattern + ")*)|" + hourIncrementPattern + "|" + allPattern + "$");
		
		String dayPattern = "([1-9]|[012]\\d|3[01])";		
		String dayRangePattern = "(" + dayPattern + "(\\-" + dayPattern + ")?)";
		String dayIncrementPattern = "(" + dayPattern + "\\/\\d+)";
		PATTERN_CRON_MONTH_DAYS = Pattern.compile("^(" + dayRangePattern + "(," + dayRangePattern + ")*)|" + dayIncrementPattern + "|" + allPattern + "$");
		
		PATTERN_TIMESTAMP_FORMAT = Pattern.compile("(\\p{L}|\\p{N}|(\\_)|(\\.)|(\\-))+");
	}
	
	private RepositoryService repository;
	
	public ValidationErrors validateJob(ExecutionContext context, ReportJob job) {
		ValidationErrors errors = new ValidationErrorsImpl();
		validateJobDetails(errors, job);
		validateSource(errors, job.getSource());
		validateJobTrigger(errors, job);
		validateJobOutput(errors, job);
		return errors;
	}

	protected void validateJobDetails(ValidationErrors errors, ReportJob job) {
		checkString(errors, "label", job.getLabel(), true, 100);
		checkString(errors, "description", job.getDescription(), false, 2000);
	}

	protected void validateSource(ValidationErrors errors, ReportJobSource source) {
		if (source == null) {
			errors.add(new ValidationErrorImpl("error.report.job.no.source", null, null, "source"));
		} else {
			String reportUnitURI = source.getReportUnitURI();
			if (checkString(errors, "source.reportUnitURI", reportUnitURI, true, 200)) {
				validateReportURI(errors, reportUnitURI);
			}
		}
	}

	protected void validateReportURI(ValidationErrors errors, String reportUnitURI) {
		if (!getRepository().resourceExists(null, reportUnitURI, ReportUnit.class)) {
			errors.add(new ValidationErrorImpl("error.report.job.report.inexistent", new Object[]{reportUnitURI}, 
					null, "source.reportUnitURI"));
		}
	}

	protected void validateJobTrigger(ValidationErrors errors, ReportJob job) {
		ReportJobTrigger trigger = job.getTrigger();
		if (trigger == null) {
			errors.add(new ValidationErrorImpl("error.report.job.no.trigger", null, "No trigger is defined for the job.", "trigger"));
			return;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date now = calendar.getTime();
		String tzId = trigger.getTimezone();
		if (tzId != null && tzId.length() > 0) {
			TimeZone tz = TimeZone.getTimeZone(tzId);
			now = TriggerUtils.translateTime(now, tz, TimeZone.getDefault());
		}
		
		if (trigger.getStartType() == ReportJobTrigger.START_TYPE_SCHEDULE) {
			Date startDate = trigger.getStartDate();
			if (startDate == null) {
				addNotEmpty(errors, "trigger.startDate");
			} else if (job.getVersion() == ReportJob.VERSION_NEW && startDate.before(now)) {
				errors.add(new ValidationErrorImpl("error.before.current.date", null, "Start date cannot be in the past.", "trigger.startDate"));
			}
		}
		
		if (trigger.getEndDate() != null) {
			switch (trigger.getStartType()) {
			case ReportJobTrigger.START_TYPE_NOW:
				if (trigger.getEndDate().before(now)) {
					errors.add(new ValidationErrorImpl("error.before.current.date", null, "End date cannot be in the past.", "trigger.endDate"));
				}
				break;
			case ReportJobTrigger.START_TYPE_SCHEDULE:
				if (trigger.getStartDate() != null && trigger.getEndDate().before(trigger.getStartDate())) {
					errors.add(new ValidationErrorImpl("error.before.start.date", null, "End date cannot be before start date", "trigger.endDate"));
				}
				break;
			}
		}
		
		if (trigger instanceof ReportJobSimpleTrigger) {
			validateJobSimpleTrigger(errors, (ReportJobSimpleTrigger) trigger);
		} else if (trigger instanceof ReportJobCalendarTrigger) {
			validateJobCalendarTrigger(errors, (ReportJobCalendarTrigger) trigger);
		} else {
			String quotedTriggerType = "\"" + trigger.getClass().getName() + "\"";
			throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedTriggerType});
		}
	}

	protected void validateJobSimpleTrigger(ValidationErrors errors, ReportJobSimpleTrigger trigger) {
		int occurrenceCount = trigger.getOccurrenceCount();
		if (occurrenceCount != ReportJobSimpleTrigger.RECUR_INDEFINITELY && occurrenceCount < 1) {
			errors.add(new ValidationErrorImpl("error.invalid", null, null, "trigger.occurrenceCount"));
		} else if (occurrenceCount > 1 || occurrenceCount == ReportJobSimpleTrigger.RECUR_INDEFINITELY) {
			if (trigger.getRecurrenceInterval() == null) {
				addNotEmpty(errors, "trigger.recurrenceInterval");
			} else if (trigger.getRecurrenceInterval().intValue() <= 0) {
				errors.add(new ValidationErrorImpl("error.positive", null, null, "trigger.recurrenceInterval"));
			}
			if (trigger.getRecurrenceIntervalUnit() == null) {
				addNotEmpty(errors, "trigger.recurrenceIntervalUnit");
			}
		}
	}

	protected void validateJobCalendarTrigger(ValidationErrors errors, ReportJobCalendarTrigger trigger) {
		if (checkString(errors, "trigger.minutes", trigger.getMinutes(), true, 200)) {
			validateCronMinutes(errors, trigger.getMinutes());
		}
		
		if (checkString(errors, "trigger.hours", trigger.getHours(), true, 80)) {
			validateCronHours(errors, trigger.getHours());
		}
		
		switch (trigger.getDaysType()) {
		case ReportJobCalendarTrigger.DAYS_TYPE_ALL:
			break;
		case ReportJobCalendarTrigger.DAYS_TYPE_WEEK:
			if (trigger.getWeekDays() == null || trigger.getWeekDays().isEmpty()) {
				errors.add(new ValidationErrorImpl("error.not.empty", null, "The value cannot be empty", "trigger.weekDays"));
			}
			break;
		case ReportJobCalendarTrigger.DAYS_TYPE_MONTH:
			if (checkString(errors, "trigger.monthDays", trigger.getMonthDays(), true, 100)) {
				validateCronMonthDays(errors, trigger.getMonthDays());
			}
			break;
		default:
			throw new JSException("jsexception.job.unknown.calendar.trigger.days.type", new Object[] {new Byte(trigger.getDaysType())});
		}
		
		if (trigger.getMonths() == null || trigger.getMonths().isEmpty()) {
			errors.add(new ValidationErrorImpl("error.not.empty", null, "The value cannot be empty", "trigger.months"));
		}
	}

	protected void validateCronMinutes(ValidationErrors errors, String minutes) {
		if (!PATTERN_CRON_MINUTES.matcher(minutes).matches()) {
			errors.add(new ValidationErrorImpl("error.pattern", null, "The minutes are not valid", "trigger.minutes"));
		}
	}

	protected void validateCronHours(ValidationErrors errors, String hours) {
		if (!PATTERN_CRON_HOURS.matcher(hours).matches()) {
			errors.add(new ValidationErrorImpl("error.pattern", null, "The hours are not valid", "trigger.hours"));
		}
	}

	protected void validateCronMonthDays(ValidationErrors errors, String days) {
		if (!PATTERN_CRON_MONTH_DAYS.matcher(days).matches()) {
			errors.add(new ValidationErrorImpl("error.pattern", null, "The hours are not valid", "trigger.monthDays"));
		}
	}

	protected void validateJobOutput(ValidationErrors errors, ReportJob job) {
		String baseOutputFilename = job.getBaseOutputFilename();
		if (checkString(errors, "baseOutputFilename", baseOutputFilename, true, 100)) {
			if (!ValidationUtil.regExValidateName(baseOutputFilename)) {
				errors.add(new ValidationErrorImpl("error.invalid.chars", 
						null, null, "baseOutputFilename"));
			}
		}
		
		Set outputFormats = job.getOutputFormats();
		if (outputFormats == null || outputFormats.isEmpty()) {
			errors.add(new ValidationErrorImpl("error.report.job.no.output.formats", null, "No output formats are selected for the job.", "outputFormats"));
		}
		
		if (job.getContentRepositoryDestination() == null) {
			errors.add(new ValidationErrorImpl("error.report.job.no.repository.output", null, "Repository output is not defined for the job.", "contentRepositoryDestination"));
		} else {
			validateRepositoryDestination(errors, job.getContentRepositoryDestination());
		}

		ReportJobMailNotification mailNotification = job.getMailNotification();
		if (mailNotification != null && !mailNotification.isEmpty()) {
			validateMailNotification(errors, mailNotification);
		}
	}

	protected void validateRepositoryDestination(ValidationErrors errors, ReportJobRepositoryDestination repositoryDestination) {
		String folderURI = repositoryDestination.getFolderURI();
		if (checkString(errors, "contentRepositoryDestination.folderURI", folderURI, true, 200)) {
			validateFolderURI(errors, folderURI);
		}
		
		checkString(errors, "contentRepositoryDestination.outputDescription", 
				repositoryDestination.getOutputDescription(), false, 250);
		
		validateTimestampPattern(errors, repositoryDestination);
	}

	protected void validateTimestampPattern(ValidationErrors errors, 
			ReportJobRepositoryDestination repositoryDestination) {
		String pattern = repositoryDestination.getTimestampPattern();
		if (pattern != null && pattern.length() > 0) {
			boolean valid;
			try {
				new SimpleDateFormat(pattern);
				valid = PATTERN_TIMESTAMP_FORMAT.matcher(pattern).matches();
			} catch (IllegalArgumentException e) {
				valid = false;
			}
			if (!valid) {
				errors.add(new ValidationErrorImpl("error.pattern", null, 
						"Specify a valid date pattern", "contentRepositoryDestination.timestampPattern"));
			}
		}
	}

	protected void validateFolderURI(ValidationErrors errors, String folderURI) {
		if (!getRepository().folderExists(null, folderURI)) {
			errors.add(new ValidationErrorImpl("error.report.job.output.folder.inexistent", new Object[]{folderURI}, 
					null, "contentRepositoryDestination.folderURI"));
		}
	}

	protected void validateMailNotification(ValidationErrors errors, ReportJobMailNotification mailNotification) {
		checkString(errors, "mailNotification.subject", mailNotification.getSubject(), true, 100);
		checkString(errors, "mailNotification.messageText", mailNotification.getMessageText(), true, 2000);
	}

	protected boolean checkString(ValidationErrors errors, String field, String value, boolean mandatory, int maxLength) {
		boolean valid = true;
		boolean empty = value == null || value.length() == 0;
		if (empty) {
			if (mandatory) {
				errors.add(new ValidationErrorImpl("error.not.empty", null, "The value cannot be empty", field));
				valid = false;
			}
		} else {
			if (value.length() > maxLength) {
				errors.add(new ValidationErrorImpl("error.length", new Object[]{new Integer(maxLength)}, "Maximum length is {0,number,integer}.", field));
				valid = false;
			}
		}
		return valid;
	}

	protected void addNotEmpty(ValidationErrors errors, String field) {
		errors.add(new ValidationErrorImpl("error.not.empty", null, "The value cannot be empty", field));
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

}
