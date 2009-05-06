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
package com.jaspersoft.jasperserver.war.action;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.DataBinder;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.util.TimeZonesList;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.common.LocalesList;
import com.jaspersoft.jasperserver.war.common.UserLocale;
import com.jaspersoft.jasperserver.war.dto.ByteEnum;
import com.jaspersoft.jasperserver.war.util.ValidationErrorsUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobEditAction.java 12668 2008-03-25 18:30:50Z achan $
 */
public class ReportJobEditAction extends FormAction {
	
	protected static final Log log = LogFactory.getLog(ReportJobEditAction.class);
	
	public static final byte RECURRENCE_TYPE_NONE = 1;
	public static final byte RECURRENCE_TYPE_SIMPLE = 2;
	public static final byte RECURRENCE_TYPE_CALENDAR = 3;
	
	public static final String ATTR_NAME_TRIGGER_RECURRENCE_TYPE = "triggerRecurrenceType";
	public static final String ATTR_NAME_TRIGGER_ALL_WEEK_DAYS = "allWeekDays";	
	public static final String ATTR_NAME_TRIGGER_ALL_MONTHS = "allMonths";
	
	public static final String EVENT_DETAILS_ERROR = "detailsError";
	public static final String EVENT_TRIGGER_ERROR = "triggerError";
	public static final String EVENT_OUTPUT_ERROR = "outputError";
	
	public static final String[] VALIDATION_FIELDS_DETAILS = {"label", "description"};
	public static final String[] VALIDATION_FIELDS_TRIGGER = {"trigger"};
	public static final String[] VALIDATION_FIELDS_OUTPUT = {"baseOutputFilename", 
		"outputFormats", "contentRepositoryDestination", "mailNotification"};
	
	protected static final Map VALIDATION_FIELDS_MAPPINGS;
	static
	{
		VALIDATION_FIELDS_MAPPINGS = new LinkedHashMap();
		VALIDATION_FIELDS_MAPPINGS.put(EVENT_DETAILS_ERROR, VALIDATION_FIELDS_DETAILS);
		VALIDATION_FIELDS_MAPPINGS.put(EVENT_TRIGGER_ERROR, VALIDATION_FIELDS_TRIGGER);
		VALIDATION_FIELDS_MAPPINGS.put(EVENT_OUTPUT_ERROR, VALIDATION_FIELDS_OUTPUT);
	}
	
	private MessageSource messageSource;
	private RepositoryService repositoryService;
	private ReportSchedulingService schedulingService;
	private String isNewModeAttrName;
	private String reportUnitURIAttrName;
	private String editJobIdParamName;
	private String contentFoldersAttrName;
	private String outputFormatsAttrName;
	private String intervalUnitsAttrName;
	
	private LocalesList localesList;
	private String localesAttrName;

	private TimeZonesList timeZonesList;
	private String timeZonesAttrName;
	
	private ValidationErrorsUtils validationUtils = ValidationErrorsUtils.instance();

	private MailAddressesEditor mailAddressesEditor;
	private CustomDateEditor customDateEditor;
	private CustomNumberEditor customNumberEditor;
	private CustomCollectionEditor byteSetEditor;
	private CustomCollectionEditor byteSortedSetEditor;

	private final List intervalUnits;
	private final List allOutputFormats;
	private final List weekDays;
	private final List months;
	
	public ReportJobEditAction() {
		intervalUnits = getRecurrenceIntervalUnits();
		allOutputFormats = getOutputFormats();
		weekDays = getWeekDays();
		months = getMonths();
	}

	protected List getRecurrenceIntervalUnits() {
		List intervalUnitsList = new ArrayList();
		intervalUnitsList.add(new ByteEnum(ReportJobSimpleTrigger.INTERVAL_MINUTE, "job.interval.unit.minute.label"));
		intervalUnitsList.add(new ByteEnum(ReportJobSimpleTrigger.INTERVAL_HOUR, "job.interval.unit.hour.label"));
		intervalUnitsList.add(new ByteEnum(ReportJobSimpleTrigger.INTERVAL_DAY, "job.interval.unit.day.label"));
		intervalUnitsList.add(new ByteEnum(ReportJobSimpleTrigger.INTERVAL_WEEK, "job.interval.unit.week.label"));
		return intervalUnitsList;
	}
	
	protected List getOutputFormats() {
		List allOutputFormatsList = new ArrayList();
		allOutputFormatsList.add(new ByteEnum(ReportJob.OUTPUT_FORMAT_PDF, "report.output.pdf.label"));
		allOutputFormatsList.add(new ByteEnum(ReportJob.OUTPUT_FORMAT_HTML, "report.output.html.label"));
		allOutputFormatsList.add(new ByteEnum(ReportJob.OUTPUT_FORMAT_XLS, "report.output.xls.label"));
		allOutputFormatsList.add(new ByteEnum(ReportJob.OUTPUT_FORMAT_RTF, "report.output.rtf.label"));
		allOutputFormatsList.add(new ByteEnum(ReportJob.OUTPUT_FORMAT_CSV, "report.output.csv.label"));
		return allOutputFormatsList;
	}
	
	protected List getWeekDays() {
		List weekDaysList = new ArrayList();
		weekDaysList.add(new ByteEnum((byte) 2, "week.days.label.mon"));
		weekDaysList.add(new ByteEnum((byte) 3, "week.days.label.tue"));
		weekDaysList.add(new ByteEnum((byte) 4, "week.days.label.wen"));
		weekDaysList.add(new ByteEnum((byte) 5, "week.days.label.thu"));
		weekDaysList.add(new ByteEnum((byte) 6, "week.days.label.fri"));
		weekDaysList.add(new ByteEnum((byte) 7, "week.days.label.sat"));
		weekDaysList.add(new ByteEnum((byte) 1, "week.days.label.sun"));
		return weekDaysList;
	}
	
	protected List getMonths() {
		List monthsList = new ArrayList();
		monthsList.add(new ByteEnum((byte) 1, "monts.label.jan"));
		monthsList.add(new ByteEnum((byte) 2, "monts.label.feb"));
		monthsList.add(new ByteEnum((byte) 3, "monts.label.mar"));
		monthsList.add(new ByteEnum((byte) 4, "monts.label.apr"));
		monthsList.add(new ByteEnum((byte) 5, "monts.label.may"));
		monthsList.add(new ByteEnum((byte) 6, "monts.label.jun"));
		monthsList.add(new ByteEnum((byte) 7, "monts.label.jul"));
		monthsList.add(new ByteEnum((byte) 8, "monts.label.aug"));
		monthsList.add(new ByteEnum((byte) 9, "monts.label.sep"));
		monthsList.add(new ByteEnum((byte) 10, "monts.label.oct"));
		monthsList.add(new ByteEnum((byte) 11, "monts.label.nov"));
		monthsList.add(new ByteEnum((byte) 12, "monts.label.dec"));
		return monthsList;
	}
	
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		
		mailAddressesEditor = new MailAddressesEditor();
		customDateEditor = new CustomDateEditor(JasperServerUtil.createCalendarDateTimeFormat(getMessageSource()), true);
		customNumberEditor = new CustomNumberEditor(Integer.class, true);
		byteSetEditor = new ByteCollectionEditor(Set.class);
		byteSortedSetEditor = new ByteCollectionEditor(SortedSet.class);
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public RepositoryService getRepositoryService()
	{
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService)
	{
		this.repositoryService = repositoryService;
	}

	public ReportSchedulingService getSchedulingService() {
		return schedulingService;
	}

	public void setSchedulingService(ReportSchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}

	public String getEditJobIdParamName() {
		return editJobIdParamName;
	}

	public void setEditJobIdParamName(String editJobIdParamName) {
		this.editJobIdParamName = editJobIdParamName;
	}

	public String getIsNewModeAttrName() {
		return isNewModeAttrName;
	}

	public void setIsNewModeAttrName(String isNewModeAttrName) {
		this.isNewModeAttrName = isNewModeAttrName;
	}

	public String getReportUnitURIAttrName() {
		return reportUnitURIAttrName;
	}

	public void setReportUnitURIAttrName(String reportUnitURIAttrName) {
		this.reportUnitURIAttrName = reportUnitURIAttrName;
	}

	public String getContentFoldersAttrName() {
		return contentFoldersAttrName;
	}

	public void setContentFoldersAttrName(String contentFoldersAttrName) {
		this.contentFoldersAttrName = contentFoldersAttrName;
	}

	public String getOutputFormatsAttrName() {
		return outputFormatsAttrName;
	}

	public void setOutputFormatsAttrName(String outputFormatsAttrName) {
		this.outputFormatsAttrName = outputFormatsAttrName;
	}

	public String getIntervalUnitsAttrName() {
		return intervalUnitsAttrName;
	}

	public void setIntervalUnitsAttrName(String intervalUnitsAttrName) {
		this.intervalUnitsAttrName = intervalUnitsAttrName;
	}

	public String getLocalesAttrName() {
		return localesAttrName;
	}

	public void setLocalesAttrName(String localesAttrName) {
		this.localesAttrName = localesAttrName;
	}

	public LocalesList getLocalesList() {
		return localesList;
	}

	public void setLocalesList(LocalesList localesList) {
		this.localesList = localesList;
	}

	public String getTimeZonesAttrName() {
		return timeZonesAttrName;
	}

	public void setTimeZonesAttrName(String timeZonesAttrName) {
		this.timeZonesAttrName = timeZonesAttrName;
	}

	public TimeZonesList getTimeZonesList() {
		return timeZonesList;
	}

	public void setTimeZonesList(TimeZonesList timeZonesList) {
		this.timeZonesList = timeZonesList;
	}

	public ValidationErrorsUtils getValidationUtils() {
		return validationUtils;
	}

	public void setValidationUtils(ValidationErrorsUtils validationUtils) {
		this.validationUtils = validationUtils;
	}

	protected void initBinder(RequestContext context, DataBinder binder) {
		super.initBinder(context, binder);
		
		binder.registerCustomEditor(List.class, "mailNotification.toAddresses", mailAddressesEditor);
		binder.registerCustomEditor(List.class, "mailNotification.ccAddresses", mailAddressesEditor);
		binder.registerCustomEditor(List.class, "mailNotification.bccAddresses", mailAddressesEditor);
		binder.registerCustomEditor(Date.class, customDateEditor);
		binder.registerCustomEditor(Integer.class, customNumberEditor);
		binder.registerCustomEditor(Set.class, "outputFormats", byteSetEditor);
		binder.registerCustomEditor(SortedSet.class, "trigger.weekDays", byteSortedSetEditor);
		binder.registerCustomEditor(SortedSet.class, "trigger.months", byteSortedSetEditor);
		binder.registerCustomEditor(String.class, "contentRepositoryDestination.timestampPattern", new StringTrimmerEditor(true));
	}

	public Event setupForm(RequestContext context) throws Exception {
		// transfer request parameter "isRunNowMode" to flowScope
		String isRunNowMode = context.getRequestParameters().get("isRunNowModeRequest");
		if (isRunNowMode != null) {
			Boolean isRunNow = null;
			if ("true".equalsIgnoreCase(isRunNowMode)) {
				isRunNow = new Boolean(true);
			} else if ("false".equalsIgnoreCase(isRunNowMode)) {
				isRunNow = new Boolean(false);
			}
			context.getFlowScope().put("isRunNowMode", isRunNow);
		}
		
		try {
			return super.setupForm(context);
		} catch (ReportJobNotFoundException e) {
			context.getFlowScope().put("errorMessage", "report.job.edit.not.found");
			context.getFlowScope().put("errorArguments", new Long(e.getJobId()));
			return new Event(this, "notFound");
		}
	}

	protected Object createFormObject(RequestContext context) {
		ReportJob job;
		if (isNewMode(context)) {
			job = createNewReportJob(context);
		} else {
			Long jobIdParam = context.getRequestParameters().getRequiredLong(getEditJobIdParamName());
			long jobId = jobIdParam.longValue();
			job = schedulingService.getScheduledJob(JasperServerUtil.getExecutionContext(context), jobId);
			if (job == null) {
				throw new ReportJobNotFoundException(jobId);
			}
		}
		
		if (job.getMailNotification() == null) {
			job.setMailNotification(new ReportJobMailNotification());
		}
		
		return job;
	}

	protected ReportJob createNewReportJob(RequestContext context) {
		ReportJob job;
		job = new ReportJob();
		ReportJobSimpleTrigger trigger = new ReportJobSimpleTrigger();
		job.setTrigger(trigger);
		job.setSource(new ReportJobSource());
		
		String ownerURI = newJobOwnerURI(context);
		job.getSource().setReportUnitURI(ownerURI);
		
		String reportName;
		int lastSepIdx = ownerURI.lastIndexOf(Folder.SEPARATOR);
		if (lastSepIdx >= 0) {
			reportName = ownerURI.substring(lastSepIdx + Folder.SEPARATOR_LENGTH);
			job.setBaseOutputFilename(reportName);
		} else {
			String quotedURI = "\"" + ownerURI + "\"";
			throw new JSException("jsexception.no.values.to.enumerate", new Object[] {quotedURI});
		}
		
		trigger.setStartType(ReportJobTrigger.START_TYPE_NOW);
		trigger.setOccurrenceCount(1);
		
		ReportJobRepositoryDestination repositoryDestination = new ReportJobRepositoryDestination();
		job.setContentRepositoryDestination(repositoryDestination);
		
		job.addOutputFormat(ReportJob.OUTPUT_FORMAT_PDF);
		
		return job;
	}

	protected String newJobOwnerURI(RequestContext context) {
		String reportUnitURI = context.getFlowScope().getString(getReportUnitURIAttrName());
		// get report uri from request parameter
		if (reportUnitURI == null) {
			reportUnitURI = (String)context.getRequestParameters().get(getReportUnitURIAttrName()+"Request");
			context.getFlowScope().put(getReportUnitURIAttrName(), reportUnitURI);
		}
		return reportUnitURI;
	}
	
	protected ReportJob getReportJob(RequestContext context) throws Exception {
		return (ReportJob) getFormObject(context);
	}

	protected boolean isNewMode(RequestContext context) {
		Boolean isNewMode = context.getFlowScope().getBoolean(getIsNewModeAttrName());
		// get value from request parameter if exist
		String newModeFromRequest = (String)context.getRequestParameters().get(getIsNewModeAttrName()+"Request");
		if (newModeFromRequest != null) {
			if ("true".equalsIgnoreCase(newModeFromRequest)) {
				isNewMode = new Boolean(true);
			} else if ("false".equalsIgnoreCase(newModeFromRequest)) {
				isNewMode = new Boolean(false);
			}
			context.getFlowScope().put(getIsNewModeAttrName(), isNewMode);
		}
		return isNewMode != null && isNewMode.booleanValue();
	}

	public Event setNowModeDefaults(RequestContext context) throws Exception {
		ReportJob job = getReportJob(context);
		String jobLabel = messageSource.getMessage("report.scheduling.job.runNow.label", null, "Run once job", getUserLocale());
		job.setLabel(jobLabel);
		return success();
	}
	
	public Event setOutputReferenceData(RequestContext context) {
		List folders = repositoryService.getAllFolders(JasperServerUtil.getExecutionContext(context));
		context.getRequestScope().put(getContentFoldersAttrName(), folders);
		
		context.getRequestScope().put(getOutputFormatsAttrName(), allOutputFormats);
		
		if (getLocalesList() != null) {
			UserLocale[] userLocales = getLocalesList().getUserLocales(getUserLocale());
			if (userLocales != null && userLocales.length > 0) {
				context.getRequestScope().put(getLocalesAttrName(), userLocales);
			}
		}
		
		return success();
	}

	protected Locale getUserLocale() {
		return LocaleContextHolder.getLocale();
	}
	
	public Event setTriggerReferenceData(RequestContext context) throws Exception {
		context.getRequestScope().put(getIntervalUnitsAttrName(), intervalUnits);
		
		ReportJob reportJob = getReportJob(context);
		byte triggerRecurrenceType = getTriggerRecurrenceType(reportJob.getTrigger());
		context.getRequestScope().put(ATTR_NAME_TRIGGER_RECURRENCE_TYPE, new Byte(triggerRecurrenceType));
		
		if (triggerRecurrenceType == RECURRENCE_TYPE_CALENDAR) {
			context.getRequestScope().put(ATTR_NAME_TRIGGER_ALL_WEEK_DAYS, weekDays);
			context.getRequestScope().put(ATTR_NAME_TRIGGER_ALL_MONTHS, months);
		}
		
		if (getTimeZonesList() != null) {
			List timeZones = getTimeZonesList().getTimeZones(getUserLocale());
			context.getRequestScope().put(getTimeZonesAttrName(), timeZones);
			
			TimeZone userTz = JasperServerUtil.getTimezone(context);
			context.getRequestScope().put("preferredTimezone", userTz.getID());
		}
		
		return success();
	}

	protected byte getTriggerRecurrenceType(ReportJobTrigger trigger) {
		byte type;
		if (trigger instanceof ReportJobSimpleTrigger) {
			ReportJobSimpleTrigger simpleTrigger = (ReportJobSimpleTrigger) trigger;
			if (simpleTrigger.getOccurrenceCount() == 1) {
				type = RECURRENCE_TYPE_NONE;
			} else {
				type = RECURRENCE_TYPE_SIMPLE;
			}
		} else if (trigger instanceof ReportJobCalendarTrigger) {
			type = RECURRENCE_TYPE_CALENDAR;
		} else {
			String quotedTriggerType ="\"" + trigger.getClass().getName() + "\""; 
			throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedTriggerType});
		}
		return type;
	}

	public Event setTriggerRecurrenceNone(RequestContext context) throws Exception {
		ReportJob job = getReportJob(context);
		ReportJobSimpleTrigger trigger = new ReportJobSimpleTrigger();
		copyCommonTriggerAttributes(trigger, job.getTrigger());
		trigger.setOccurrenceCount(1);
		job.setTrigger(trigger);
		return success();
	}

	public Event setTriggerRecurrenceSimple(RequestContext context) throws Exception {
		ReportJob job = getReportJob(context);
		ReportJobSimpleTrigger trigger = new ReportJobSimpleTrigger();
		copyCommonTriggerAttributes(trigger, job.getTrigger());
		trigger.setOccurrenceCount(ReportJobSimpleTrigger.RECUR_INDEFINITELY);
		trigger.setRecurrenceInterval(1);
		trigger.setRecurrenceIntervalUnit(ReportJobSimpleTrigger.INTERVAL_DAY);
		job.setTrigger(trigger);
		return success();
	}

	public Event setTriggerRecurrenceCalendar(RequestContext context) throws Exception {
		ReportJob job = getReportJob(context);
		ReportJobCalendarTrigger trigger = new ReportJobCalendarTrigger();
		copyCommonTriggerAttributes(trigger, job.getTrigger());
		trigger.setMinutes("0");
		trigger.setHours("0");
		trigger.setDaysType(ReportJobCalendarTrigger.DAYS_TYPE_ALL);
		
		TreeSet selectedMonths = new TreeSet();
		for (Iterator it = months.iterator(); it.hasNext();) {
			ByteEnum month = (ByteEnum) it.next();
			selectedMonths.add(new Byte(month.getCode()));
		}
		trigger.setMonths(selectedMonths);
		
		job.setTrigger(trigger);
		return success();
	}

	protected void copyCommonTriggerAttributes(ReportJobTrigger newTrigger, ReportJobTrigger trigger) {
		newTrigger.setTimezone(trigger.getTimezone());
		newTrigger.setStartType(trigger.getStartType());
		newTrigger.setStartDate(trigger.getStartDate());
		newTrigger.setEndDate(trigger.getEndDate());
	}
	
	public Event saveJob(RequestContext context) throws Exception {
		ReportJob job = getReportJob(context);
		
		if (job.getMailNotification().isEmpty()) {
			job.setMailNotification(null);
		}
		
		try {
			if (isNewMode(context)) {
				schedulingService.scheduleJob(JasperServerUtil.getExecutionContext(context), job);
			} else {
				try {
					schedulingService.updateScheduledJob(JasperServerUtil.getExecutionContext(context), job);
				} catch (ReportJobNotFoundException e) {
					context.getFlowScope().put("errorMessage", "report.job.save.not.found");
					context.getFlowScope().put("errorArguments", new Long(e.getJobId()));
					return result("notFound");
				}
			}
		} catch (JSValidationException e) {
			String errorEvent = resolveValidationErrorEvent(e);
			if (errorEvent != null) {
				validationUtils.setErrors(getFormErrors(context), e.getErrors(), null);
				return result(errorEvent);
			}

			throw e;
		} finally {
			if (job.getMailNotification() == null) {
				job.setMailNotification(new ReportJobMailNotification());
			}
		}
		
		if ("reportJobFlow".equalsIgnoreCase(context.getRequestParameters().get("_flowId"))) {
		   return yes();	
		}
		
		return success();
	}

	protected String resolveValidationErrorEvent(JSValidationException e) {
		String event = null;
		errors:
		for (Iterator it = e.getErrors().getErrors().iterator(); it.hasNext();) {
			ValidationError error = (ValidationError) it.next();
			String field = error.getField();
			if (field != null) {
				for (Iterator mapIt = VALIDATION_FIELDS_MAPPINGS.entrySet().iterator(); 
						mapIt.hasNext();) {
					Map.Entry entry = (Map.Entry) mapIt.next();
					String[] prefixes = (String[]) entry.getValue();
					if (fieldMatches(field, prefixes)) {
						event = (String) entry.getKey();
						break errors;
					}
				}
			}
		}
		return event;
	}
	
	protected boolean fieldMatches(String field, String[] fieldPrefixes) {
		boolean matches = false;
		for (int i = 0; i < fieldPrefixes.length; i++) {
			if (field.startsWith(fieldPrefixes[i])) {
				matches = true;
				break;
			}
		}
		return matches;
	}

	protected static class MailAddressesEditor extends PropertyEditorSupport {
		
		public String getAsText() {
			StringBuffer sb = new StringBuffer();
			List addresses = (List) getValue();
			if (addresses != null && !addresses.isEmpty()) {
				Iterator it = addresses.iterator();
				String address = (String) it.next();
				sb.append(address);
				while (it.hasNext()) {
					sb.append(", ");
					address = (String) it.next();
					sb.append(address);
				}
			}
			return sb.toString();
		}

		public void setAsText(String text) throws IllegalArgumentException {
			List addressList = new ArrayList();
			if (text != null && text.trim().length() > 0) {
				String[] addresses = text.trim().split("\\,");
				for (int i = 0; i < addresses.length; i++) {
					addressList.add(addresses[i].trim());
				}
			}
			setValue(addressList);
		}
		
	}
	
	protected static class ByteCollectionEditor extends CustomCollectionEditor {
		
		public ByteCollectionEditor(Class collectionClass) {
			super(collectionClass);
		}

		protected Object convertElement(Object val) {
			if (val == null || val instanceof Byte) {
				return val;
			}
			
			try {
				return Byte.valueOf(val.toString());
			} catch (NumberFormatException e) {
				log.error("error parsing byte value", e);
				throw new JSExceptionWrapper(e);
			}
		}

	}
}
