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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.LogEvent;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.LoggingService;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.ContentResourceURIResolver;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.CsvExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PdfExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.XlsExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequest;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainerFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.MemoryDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockHandle;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockManager;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportExecutionJob.java 13767 2008-05-23 07:36:12Z swood $
 */
public class ReportExecutionJob implements Job {
	
	private static final Log log = LogFactory.getLog(ReportExecutionJob.class);
	
	public static final String REPORT_PARAMETER_SCHEDULED_TIME = "_ScheduledTime";
	
	public static final String REPOSITORY_FILENAME_SEQUENCE_SEPARATOR = "-";
	public static final String REPOSITORY_FILENAME_TIMESTAMP_SEQUENCE_PATTERN = "yyyyMMddHHmm";
	
	public static final String SCHEDULER_CONTEXT_KEY_APPLICATION_CONTEXT = "applicationContext";
	
	public static final String SCHEDULER_CONTEXT_KEY_JOB_PERSISTENCE_SERVICE = "jobPersistenceService";
	public static final String SCHEDULER_CONTEXT_KEY_ENGINE_SERVICE = "engineService";
	public static final String SCHEDULER_CONTEXT_KEY_VIRTUALIZER_FACTORY = "virtualizerFactory";
	public static final String SCHEDULER_CONTEXT_KEY_REPOSITORY = "repositoryService";
	public static final String SCHEDULER_CONTEXT_KEY_MAIL_SENDER = "mailSender";
	public static final String SCHEDULER_CONTEXT_KEY_MAIL_FROM_ADDRESS = "mailFromAddress";
	public static final String SCHEDULER_CONTEXT_KEY_LOGGING_SERVICE = "loggingService";
	public static final String SCHEDULER_CONTEXT_KEY_SECURITY_CONTEXT_PROVIDER = "securityContextProvider";
	public static final String SCHEDULER_CONTEXT_KEY_HYPERLINK_PRODUCER_FACTORY = "hyperlinkProducerFactory";
	public static final String SCHEDULER_CONTEXT_KEY_ENCODING_PROVIDER = "encodingProvider";
	public static final String SCHEDULER_CONTEXT_KEY_EXPORT_PARAMETRES_MAP = "exportParametersMap";
	public static final String SCHEDULER_CONTEXT_KEY_DATA_CONTAINER_FACTORY = "dataContainerFactory";
	public static final String SCHEDULER_CONTEXT_KEY_CONTENT_RESOURCE_URI_RESOLVER = "contentResourceURIResolver";
	public static final String SCHEDULER_CONTEXT_KEY_LOCK_MANAGER = "lockManager";
	
	public static final String JOB_DATA_KEY_DETAILS_ID = "jobDetailsID";	
	public static final String JOB_DATA_KEY_USERNAME = "jobUser";
	
	public static final String LOGGING_COMPONENT = "reportScheduler";

	protected static final String LOCK_NAME_CONTENT_RESOURCE = "reportSchedulerOutput";
	
	protected static class ExceptionInfo {
		private final Throwable exception;
		private final String message;
		
		public ExceptionInfo(String message, Throwable exception) {
			this.exception = exception;
			this.message = message == null ? exception.getMessage() : message;
		}

		public Throwable getException() {
			return exception;
		}

		public String getMessage() {
			return message;
		}
	}
	
	protected List exceptions = new ArrayList();
	protected ApplicationContext applicationContext;
	protected String username;
	protected ReportJob jobDetails;
	protected JobExecutionContext jobContext;
	protected SchedulerContext schedulerContext;
	protected ExecutionContext executionContext;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			this.jobContext = context;
			this.schedulerContext = jobContext.getScheduler().getContext();

			this.applicationContext = (ApplicationContext) schedulerContext.get(SCHEDULER_CONTEXT_KEY_APPLICATION_CONTEXT);
			this.username = getUsername();
			SecurityContextProvider securityContextProvider = getSecurityContextProvider();
			securityContextProvider.setAuthenticatedUser(this.username);
			try {
				executeAndSendReport();
			} finally {
				securityContextProvider.revertAuthenticatedUser();
			}
		} catch (JobExecutionException e) {
			throw e;
		} catch (SchedulerException e) {
			throw new JobExecutionException(e);
		} finally {
			clear();
		}
	}

	protected void initJobExecution() {
		updateExecutionContextDetails();
	}

	protected void clear() {
		exceptions.clear();
		jobContext = null;
		schedulerContext = null;
		jobDetails = null;
		executionContext = null;
		username = null;
	}

	protected String getUsername() {
		JobDataMap jobDataMap = jobContext.getTrigger().getJobDataMap();
		return jobDataMap.getString(JOB_DATA_KEY_USERNAME);
	}

	protected ExecutionContext getExecutionContext() {
		return new ExecutionContextImpl();
	}

	protected void updateExecutionContextDetails() {
		((ExecutionContextImpl) executionContext).setLocale(getJobLocale());	
	}
	
	protected Locale getJobLocale() {
		String localeCode = jobDetails.getOutputLocale();
		Locale locale;
		if (localeCode != null && localeCode.length() > 0) {
			locale = LocaleHelper.getInstance().getLocale(localeCode);
		} else {
			locale = null;
		}
		return locale;
	}
	
	protected Locale getMessageLocale() {
		return getLocale();
	}

	protected Locale getLocale() {
		Locale locale = getJobLocale();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
	
	protected String getMessage(String key, Object[] arguments) {
		return applicationContext.getMessage(key, arguments, getMessageLocale());
	}
	
	protected void handleException(String message, Throwable exc) {
		log.error(message, exc);
		exceptions.add(new ExceptionInfo(message, exc));
	}

	protected boolean hasExceptions() {
		return !exceptions.isEmpty();
	}
	
	protected void checkExceptions() throws JobExecutionException {
		if (hasExceptions()) {
			ExceptionInfo firstException = ((ExceptionInfo) exceptions.get(0));
			
			try {
				logExceptions();
			} catch (Exception e) {
				log.error(e, e);
				throwJobExecutionException(firstException);
			}

			throwJobExecutionException(firstException);
		}
	}
	
	protected void throwJobExecutionException(ExceptionInfo exceptionInfo) throws JobExecutionException {
		JobExecutionException jobException;
		Throwable exception = exceptionInfo.getException();
		if (exception instanceof Exception) {
			jobException = new JobExecutionException(exceptionInfo.getMessage(), (Exception) exception, false);
		} else {
			jobException = new JobExecutionException(exceptionInfo.getMessage());
		}
		throw jobException;
	}

	protected void logExceptions() {
		LoggingService loggingService = getLoggingService();
		LogEvent event = loggingService.instantiateLogEvent();
		event.setComponent(LOGGING_COMPONENT);
		event.setType(LogEvent.TYPE_ERROR);
		event.setMessageCode("log.error.report.job.failed");
		if (jobDetails != null) {
			event.setResourceURI(jobDetails.getSource().getReportUnitURI());
		}
		
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		if (jobDetails != null) {
			printWriter.println("Job: " + jobDetails.getLabel() + " (ID: " + jobDetails.getId() + ")");
			printWriter.println("Report unit: " + jobDetails.getSource().getReportUnitURI());			
		}
		printWriter.println("Quartz Job: " + jobContext.getJobDetail().getFullName());
		printWriter.println("Quartz Trigger: " + jobContext.getTrigger().getFullName());
		
		printWriter.println("Exceptions:");
		for (ListIterator it = exceptions.listIterator(); it.hasNext();) {
			ExceptionInfo exceptionInfo = (ExceptionInfo) it.next();
			printWriter.println();
			printWriter.println(exceptionInfo.getMessage());
			
			Throwable exception = exceptionInfo.getException();
			if (exception != null) {
				exception.printStackTrace(printWriter);
			}
		}
		
		printWriter.flush();
		event.setText(writer.toString());
		event.setState(LogEvent.STATE_UNREAD);
		
		loggingService.log(event);
	}

	protected SecurityContextProvider getSecurityContextProvider() {
		return (SecurityContextProvider) schedulerContext.get(SCHEDULER_CONTEXT_KEY_SECURITY_CONTEXT_PROVIDER);
	}

	protected LoggingService getLoggingService() {
		return (LoggingService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_LOGGING_SERVICE);
	}

	protected ReportJob getJobDetails() {
		ReportJobsPersistenceService persistenceService = getPersistenceService();
		JobDataMap jobDataMap = jobContext.getTrigger().getJobDataMap();
		long jobId = jobDataMap.getLong(JOB_DATA_KEY_DETAILS_ID);
		ReportJob job = persistenceService.loadJob(executionContext, new ReportJobIdHolder(jobId));
		return job;
	}

	protected ReportJobsPersistenceService getPersistenceService() {
		return (ReportJobsPersistenceService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_JOB_PERSISTENCE_SERVICE);
	}

	protected EngineService getEngineService() {
		EngineService engineService = (EngineService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_ENGINE_SERVICE);
		return engineService;
	}

	protected VirtualizerFactory getVirtualizerFactory() {
		return (VirtualizerFactory) schedulerContext.get(SCHEDULER_CONTEXT_KEY_VIRTUALIZER_FACTORY);
	}

	protected void executeAndSendReport() throws JobExecutionException
	{
		try
		{
			executionContext = getExecutionContext();
			jobDetails = getJobDetails();
			initJobExecution();
			
			ReportUnitResult result = null;
			try
			{
				try
				{
					result = executeReport();
				}
				catch (Exception e)
				{
					handleException(getMessage("report.scheduling.error.filling.report", null), e);
				}
				
				List outputs = null;
				if (result != null)
				{
					outputs = createReportOutputs(result);
					saveToRepository(outputs);
				}
				
				ReportJobMailNotification mailNotification = jobDetails.getMailNotification();
				if (mailNotification != null)
				{
					boolean skipEmpty = mailNotification.isSkipEmptyReports() && isEmpty(result);
					if (!skipEmpty || hasExceptions())
					{
						List attachments = skipEmpty ? null : outputs;
						try
						{
							sendMailNotification(attachments);
						}
						catch (Exception e)
						{
							handleException(getMessage("report.scheduling.error.sending.email.notification", null), e);
						}
					}
				}
			}
			finally
			{
				if (result != null)
				{
					VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
					if (virtualizerFactory != null)
					{
						virtualizerFactory.disposeVirtualizer(result.getVirtualizer());
					}
				}
			}
		}
		catch (Throwable e)
		{
			handleException(getMessage("report.scheduling.error.system", null), e);
		}
		finally
		{
			checkExceptions();
		}
	}

	protected void setReadOnly(ReportUnitResult result) {
		if (result.getVirtualizer() != null)
		{
			JRVirtualizationContext virtualizationContext = 
				JRVirtualizationContext.getRegistered(result.getJasperPrint());
			if (virtualizationContext != null)
			{
				virtualizationContext.setReadOnly(true);
			}
		}
	}

	protected ReportUnitResult executeReport() {
		Map parametersMap = collectReportParameters();
		
		ReportUnitRequest request = new ReportUnitRequest(getReportUnitURI(), parametersMap);

		EngineService engineService = getEngineService();
		ReportUnitResult result = (ReportUnitResult) engineService.execute(executionContext, request);
		setReadOnly(result);
		return result;
	}

	protected Map collectReportParameters() {
		Map params = new HashMap();
		Map jobParams = jobDetails.getSource().getParametersMap();
		if (jobParams != null) {
			params.putAll(jobParams);
		}
		putAdditionalParameters(params);
		return params;
	}

	protected void putAdditionalParameters(Map parametersMap) {
		if (!parametersMap.containsKey(REPORT_PARAMETER_SCHEDULED_TIME)) {
			Date scheduledFireTime = jobContext.getScheduledFireTime();
			parametersMap.put(REPORT_PARAMETER_SCHEDULED_TIME, scheduledFireTime);
		}
		VirtualizerFactory virtualizerFactory = getVirtualizerFactory();
		if (virtualizerFactory != null)
		{
			parametersMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizerFactory.getVirtualizer());
		}
	}

	protected List createReportOutputs(ReportUnitResult result) throws JobExecutionException {
		JasperPrint jasperPrint = result.getJasperPrint();
		
		String baseFilename = jobDetails.getBaseOutputFilename();
		if (jobDetails.getContentRepositoryDestination().isSequentialFilenames()) {
			Date scheduledTime = jobContext.getScheduledFireTime();
			SimpleDateFormat format = getTimestampFormat();
			baseFilename = jobDetails.getBaseOutputFilename() + REPOSITORY_FILENAME_SEQUENCE_SEPARATOR + format.format(scheduledTime);
		} else {
			baseFilename = jobDetails.getBaseOutputFilename();
		}
		
		Set outputFormats = jobDetails.getOutputFormats();
		List outputs = new ArrayList(outputFormats.size());
		for (Iterator it = outputFormats.iterator(); it.hasNext();) {
			Byte format = (Byte) it.next();

			ReportOutput output = null;
			try {
				output = getReportOutput(jasperPrint, format.byteValue(), baseFilename);
				outputs.add(output);
			} catch (Exception e) {
				String formatKey = getMessage("report.scheduling.output.format." + format, null);
				String formatLabel = getMessage("report.output." + formatKey + ".label", null);
				handleException(getMessage("report.scheduling.error.exporting.report", new Object[]{formatLabel}), e);
			}
		}
		
		return outputs;
	}

	protected SimpleDateFormat getTimestampFormat() {
		String pattern = jobDetails.getContentRepositoryDestination().getTimestampPattern();
		if (pattern == null || pattern.length() == 0) {
			pattern = REPOSITORY_FILENAME_TIMESTAMP_SEQUENCE_PATTERN;
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern, getLocale());
		return format;
	}

	protected void saveToRepository(List outputs) {
		for (Iterator it = outputs.iterator(); it.hasNext();) {
			ReportOutput output = (ReportOutput) it.next();
			
			try {
				saveToRepository(output);
			} catch (Exception e) {
				handleException(
						getMessage("report.scheduling.error.saving.to.repository", new Object[]{output.getFilename()}), 
						e);
			}
		}
	}

	protected boolean isEmpty(ReportUnitResult result) {
		if (result == null) {
			return false;
		}
		JasperPrint jasperPrint = result.getJasperPrint();
		List pages = jasperPrint.getPages();
		boolean empty;
		if (pages == null || pages.isEmpty()) {
			empty = true;
		} else if (pages.size() == 1) {
			JRPrintPage page = (JRPrintPage) pages.get(0);
			List elements = page.getElements();
			empty = elements == null || elements.isEmpty();
		} else {
			empty = false;
		}
		return empty;
	}

	protected ReportOutput getReportOutput(JasperPrint jasperPrint, byte format, String baseFilename) throws JobExecutionException {
		ReportOutput output;
		switch (format) {
		case ReportJob.OUTPUT_FORMAT_PDF: {
			output = getPdfOutput(jasperPrint, baseFilename);
			break;
		}
		case ReportJob.OUTPUT_FORMAT_HTML: {
			output = getHtmlOutput(jasperPrint, baseFilename);
			break;
		}
		case ReportJob.OUTPUT_FORMAT_XLS: {
			output = getXlsOutput(jasperPrint, baseFilename);
			break;
		}
		case ReportJob.OUTPUT_FORMAT_RTF: {
			output = getRtfOutput(jasperPrint, baseFilename);
			break;
		}
		case ReportJob.OUTPUT_FORMAT_CSV: {
			output = getCsvOutput(jasperPrint, baseFilename);
			break;
		}
		default:
			throw new JSException("jsexception.report.unknown.output.format", new Object[] {new Byte(format)});
		}
		return output;
	}

	protected String getChildrenFolderName(String resourceName) {
		return getRepository().getChildrenFolderName(resourceName);
	}
	
	protected ReportOutput getPdfOutput(JasperPrint jasperPrint, String baseFilename) throws JobExecutionException {
		Map params = new HashMap();
		params.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
		
		DataContainer pdfData = createDataContainer();
		boolean close = true;
		OutputStream pdfDataOut = pdfData.getOutputStream();
		
		try {
			params.put(JRExporterParameter.OUTPUT_STREAM, pdfDataOut);
			
			PdfExportParametersBean exportParams =(PdfExportParametersBean)getExportParametersMap().get("pdf");
			String locale = LocaleContextHolder.getLocale().getLanguage();
			Map localizedFontMap = exportParams.getLocalizedFontMap().get(locale) != null ?
					(Map)exportParams.getLocalizedFontMap().get(locale) :
						(Map)exportParams.getLocalizedFontMap().get(locale.substring(0,2));
			if(localizedFontMap != null){
				params.put(JRExporterParameter.FONT_MAP, localizedFontMap);
			}
			
			getEngineService().exportToPdf(executionContext, getReportUnitURI(), params);
			
			close = false;
			pdfDataOut.close();
		} catch (IOException e) {
			throw new JSExceptionWrapper(e);
		} finally {
			if (close) {
				try {
					pdfDataOut.close();
				} catch (IOException e) {
					log.error("Error closing stream", e);
				}
			}
		}
		
		String filename = baseFilename + ".pdf";
		return new ReportOutput(pdfData, ContentResource.TYPE_PDF, filename);
	}

	protected String getReportUnitURI() {
		return jobDetails.getSource().getReportUnitURI();
	}

	protected ReportOutput getHtmlOutput(JasperPrint jasperPrint, String baseFilename) throws JobExecutionException {
		try {
			String filename = baseFilename + ".html";
			JRHtmlExporter exporter = new JRHtmlExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, getCharacterEncoding());
			
			DataContainer htmlData = createDataContainer();
			boolean close = true;
			OutputStream htmlDataOut = htmlData.getOutputStream();
			
			try {
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, htmlDataOut);
				Map images = new HashMap();//TODO don't keep in memory
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, images);
				exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,
						getChildrenFolderName(filename) + '/');
				
				JRHyperlinkProducerFactory hyperlinkProducerFactory = getHyperlinkProducerFactory();
				if (hyperlinkProducerFactory != null) {
					exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, hyperlinkProducerFactory);
				}
				
				exporter.exportReport();

				close = false;
				htmlDataOut.close();

				ReportOutput htmlOutput = new ReportOutput(htmlData,
						ContentResource.TYPE_HTML, filename);

				for (Iterator it = images.entrySet().iterator(); it.hasNext();) {
					Map.Entry imageEntry = (Map.Entry) it.next();
					String imageName = (String) imageEntry.getKey();
					byte[] imageData = (byte[]) imageEntry.getValue();
					MemoryDataContainer imageDataContainer = new MemoryDataContainer(imageData);
					ReportOutput image = new ReportOutput(imageDataContainer,
							ContentResource.TYPE_IMAGE, imageName);
					htmlOutput.addChild(image);
				}

				return htmlOutput;
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						htmlDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	protected JRHyperlinkProducerFactory getHyperlinkProducerFactory() {
		JRHyperlinkProducerFactory engineService = (JRHyperlinkProducerFactory) schedulerContext.get(SCHEDULER_CONTEXT_KEY_HYPERLINK_PRODUCER_FACTORY);
		return engineService;
	}

	protected ReportOutput getXlsOutput(JasperPrint jasperPrint, String baseFilename) throws JobExecutionException {
		try {
			JExcelApiExporter exporter = new JExcelApiExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			
			DataContainer xlsData = createDataContainer();
			boolean close = false;
			OutputStream xlsDataOut = xlsData.getOutputStream();
			try {
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsDataOut);
				exporter.setParameter(JExcelApiExporterParameter.CREATE_CUSTOM_PALETTE, Boolean.TRUE);

				XlsExportParametersBean exportParams = (XlsExportParametersBean)getExportParametersMap().get("xls");
				if(exportParams != null)
				{
					if(exportParams.getOnePagePerSheet() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, exportParams.getOnePagePerSheet());
					if(exportParams.getDetectCellType() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, exportParams.getDetectCellType());
					if(exportParams.getRemoveEmptySpaceBetweenRows() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, exportParams.getRemoveEmptySpaceBetweenRows());
					if(exportParams.getRemoveEmptySpaceBetweenColumns() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, exportParams.getRemoveEmptySpaceBetweenColumns());
					if(exportParams.getWhitePageBackground() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, exportParams.getWhitePageBackground());
					if(exportParams.getIgnoreGraphics() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, exportParams.getIgnoreGraphics());
					if(exportParams.getCollapseRowSpan() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, exportParams.getCollapseRowSpan());
					if(exportParams.getIgnoreCellBorder() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, exportParams.getIgnoreCellBorder());
					if(exportParams.getFontSizeFixEnabled() != null);
						exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, exportParams.getFontSizeFixEnabled());
					if(exportParams.getMaximumRowsPerSheet() != null);
						exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, exportParams.getMaximumRowsPerSheet());
					if(exportParams.getXlsFormatPatternsMap() != null && !exportParams.getXlsFormatPatternsMap().isEmpty());
						exporter.setParameter(JRXlsExporterParameter.FORMAT_PATTERNS_MAP, exportParams.getXlsFormatPatternsMap());
				}	
				exporter.exportReport();

				close = false;
				xlsDataOut.close();
				
				String filename = baseFilename + ".xls";
				return new ReportOutput(xlsData, ContentResource.TYPE_XLS, filename);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						xlsDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	protected ReportOutput getRtfOutput(JasperPrint jasperPrint, String baseFilename) throws JobExecutionException {
		try {
			JRRtfExporter exporter = new JRRtfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			
			DataContainer rtfData = createDataContainer();
			boolean close = false;
			OutputStream rtfDataOut = rtfData.getOutputStream();
			try {
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, rtfDataOut);
				exporter.exportReport();
				
				close = false;
				rtfDataOut.close();

				String fileName = baseFilename + ".rtf";
				return new ReportOutput(rtfData, ContentResource.TYPE_RTF, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						rtfDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	protected ReportOutput getCsvOutput(JasperPrint jasperPrint, String baseFilename) throws JobExecutionException {
		try {
			JRCsvExporter exporter = new JRCsvExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			
			DataContainer csvData = createDataContainer();
			boolean close = false;
			OutputStream csvDataOut = csvData.getOutputStream();
			try {
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, getCharacterEncoding());
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, csvDataOut);
				
				CsvExportParametersBean exportParams = (CsvExportParametersBean)getExportParametersMap().get("csv");
				if(exportParams != null)
					exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, exportParams.getFieldDelimiter());
				
				exporter.exportReport();
				
				close = false;
				csvDataOut.close();

				String fileName = baseFilename + ".csv";
				return new ReportOutput(csvData, ContentResource.TYPE_CSV, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						csvDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
			
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	protected void saveToRepository(ReportOutput output) {		
		RepositoryService repositoryService = getRepository();
		ReportJobRepositoryDestination repositoryDestination = jobDetails.getContentRepositoryDestination();
		
		List children = output.getChildren();
		List childResources = new ArrayList(children.size());
		for (Iterator it = children.iterator(); it.hasNext();) {
			ReportOutput childOutput = (ReportOutput) it.next();
			
			ContentResource childRes = (ContentResource) repositoryService.newResource(executionContext, ContentResource.class);;		
			childRes.setName(childOutput.getFilename());
			childRes.setLabel(childOutput.getFilename());
			childRes.setFileType(childOutput.getFileType());
			childRes.setDataContainer(childOutput.getData());
			childResources.add(childRes);
		}

		ContentResource contentRes = null;
		String resURI = repositoryDestination.getFolderURI() + Folder.SEPARATOR + output.getFilename();
		LockHandle lock = lockOutputResource(resURI);
		try {
			Resource existingRes = repositoryService.getResource(executionContext, resURI);
			if (repositoryDestination.isOverwriteFiles()) {
				if (existingRes != null) {
					if (!(existingRes instanceof ContentResource)) {
						String quotedResURI = "\"" + resURI + "\"";
						throw new JSException("jsexception.report.no.content.resource", new Object[] {quotedResURI});
					}
					contentRes = (ContentResource) existingRes;
				}
			} else if (existingRes != null) {
				throw new JSException("jsexception.report.resource.already.exists.no.overwrite", new Object[] {resURI});
			}
			
			if (contentRes == null) {
				contentRes = (ContentResource) repositoryService.newResource(executionContext, ContentResource.class);
				contentRes.setName(output.getFilename());
				//contentRes.setLabel(jobDetails.getBaseOutputFilename());
				contentRes.setLabel(output.getFilename());
				contentRes.setDescription(getOutputDescription());
				contentRes.setParentFolder(repositoryDestination.getFolderURI());
			}
			
			contentRes.setFileType(output.getFileType());
			contentRes.setDataContainer(output.getData());
			contentRes.setResources(childResources);

			repositoryService.saveResource(null, contentRes);
			output.setRepositoryPath(resURI);
		} finally {
			unlock(lock);
		}
	}

	protected String getOutputDescription() {
		return jobDetails.getContentRepositoryDestination().getOutputDescription();
	}

	protected RepositoryService getRepository() {
		RepositoryService repositoryService = (RepositoryService) schedulerContext.get(SCHEDULER_CONTEXT_KEY_REPOSITORY);
		return repositoryService;
	}

	protected void sendMailNotification(List reportOutputs) throws JobExecutionException {
		ReportJobMailNotification mailNotification = jobDetails.getMailNotification();
		if (mailNotification != null) {
			JavaMailSender mailSender = getMailSender();
			String fromAddress = getFromAddress();
			try {
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, getCharacterEncoding());
				messageHelper.setFrom(fromAddress);				
				messageHelper.setSubject(mailNotification.getSubject());
				StringBuffer messageText = new StringBuffer(mailNotification.getMessageText());
				
				addMailRecipients(mailNotification, messageHelper);
				
				if (reportOutputs != null && !reportOutputs.isEmpty()) {
					byte resultSendType = jobDetails.getMailNotification().getResultSendType();
					if (resultSendType == ReportJobMailNotification.RESULT_SEND_ATTACHMENT) {
						for (Iterator it = reportOutputs.iterator(); it.hasNext();) {
							ReportOutput output = (ReportOutput) it.next();
							attachOutput(messageHelper, output);
						}
					} else {
						appendRepositoryLinks(messageText, reportOutputs);
					}
				}

				if (hasExceptions()) {
					for (Iterator it = exceptions.iterator(); it.hasNext();) {
						ExceptionInfo exception = (ExceptionInfo) it.next();
						
						messageText.append("\n");
						messageText.append(exception.getMessage());
						
						attachException(messageHelper, exception);
					}
				}
				
				messageHelper.setText(messageText.toString());
				mailSender.send(message);
			} catch (MessagingException e) {
				log.error("Error while sending report job result mail", e);
				throw new JSExceptionWrapper(e);
			}
		}
	}

	protected void appendRepositoryLinks(StringBuffer notificationMessage, List reportOutputs) {
		if (reportOutputs != null && !reportOutputs.isEmpty()) {
			String preamble = getMessage("report.scheduling.notification.repository.links.preamble", null);
			notificationMessage.append(preamble);
			String linkDescription = getRepositoryLinkDescription();
			ContentResourceURIResolver contentResourceURIResolver = getContentResourceURIResolver();
			for (Iterator it = reportOutputs.iterator(); it.hasNext();) {
				ReportOutput output = (ReportOutput) it.next();
				String resourcePath = output.getRepositoryPath();
				if (resourcePath != null) {
					String resourceURI = contentResourceURIResolver.resolveURI(resourcePath);
					String line = getMessage("report.scheduling.notification.repository.link.line", 
							new Object[]{linkDescription, resourceURI});
					notificationMessage.append(line);
				}
			}
		}
	}

	protected String getRepositoryLinkDescription() {
		String reportURI = jobDetails.getSource().getReportUnitURI();
		Resource report = getRepository().getResource(getExecutionContext(), reportURI, ReportUnit.class);
		return report.getLabel();
	}
	
	protected ContentResourceURIResolver getContentResourceURIResolver() {
		return (ContentResourceURIResolver) schedulerContext.get(
				SCHEDULER_CONTEXT_KEY_CONTENT_RESOURCE_URI_RESOLVER);
	}
	
	protected LockManager getLockManager() {
		return (LockManager) schedulerContext.get(
				SCHEDULER_CONTEXT_KEY_LOCK_MANAGER);
	}
	
	protected LockHandle lockOutputResource(String uri) {
		return getLockManager().lock(LOCK_NAME_CONTENT_RESOURCE, uri);
	}
	
	protected void unlock(LockHandle lock) {
		getLockManager().unlock(lock);
	}
	
	protected String getCharacterEncoding() {
		CharacterEncodingProvider encodingProvider = (CharacterEncodingProvider) schedulerContext.get(SCHEDULER_CONTEXT_KEY_ENCODING_PROVIDER);
		return encodingProvider.getCharacterEncoding();
	}

	protected void addMailRecipients(ReportJobMailNotification mailNotification, MimeMessageHelper messageHelper) throws MessagingException {
		List toAddresses = mailNotification.getToAddresses();
		if (toAddresses != null && !toAddresses.isEmpty()) {
			String[] addressArray = new String[toAddresses.size()];
			toAddresses.toArray(addressArray);
			messageHelper.setTo(addressArray);
		}
		
		List ccAddresses = mailNotification.getCcAddresses();
		if (ccAddresses != null && !ccAddresses.isEmpty()) {
			String[] addressArray = new String[ccAddresses.size()];
			ccAddresses.toArray(addressArray);
			messageHelper.setCc(addressArray);
		}
		
		List bccAddresses = mailNotification.getBccAddresses();
		if (bccAddresses != null && !bccAddresses.isEmpty()) {
			String[] addressArray = new String[bccAddresses.size()];
			bccAddresses.toArray(addressArray);
			messageHelper.setBcc(addressArray);
		}
	}

	protected void attachOutput(MimeMessageHelper messageHelper, ReportOutput output) throws MessagingException, JobExecutionException {
		String attachmentName;
		DataContainer attachmentData;
		if (output.getChildren().isEmpty()) {
			attachmentName = output.getFilename();
			attachmentData = output.getData();
		} else {
			attachmentData = createDataContainer();
			boolean close = true;
			ZipOutputStream zipOut = new ZipOutputStream(attachmentData.getOutputStream());
			try {
				zipOut.putNextEntry(new ZipEntry(output.getFilename()));
				StreamUtils.pipeData(output.getData().getInputStream(), zipOut);
				zipOut.closeEntry();
				
				for (Iterator it = output.getChildren().iterator(); it.hasNext();) {
					ReportOutput child = (ReportOutput) it.next();
					String childName = getChildrenFolderName(output.getFilename()) + '/' + child.getFilename();
					zipOut.putNextEntry(new ZipEntry(childName));
					StreamUtils.pipeData(child.getData().getInputStream(), zipOut);
					zipOut.closeEntry();					
				}
				
				zipOut.finish();
				zipOut.flush();
				
				close = false;
				zipOut.close();
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						zipOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}

			attachmentName = output.getFilename() + ".zip";
		}
		
		try {
			attachmentName = MimeUtility.encodeWord(attachmentName, getCharacterEncoding(), null);
		} catch (UnsupportedEncodingException e) {
			throw new JSExceptionWrapper(e);
		}
		messageHelper.addAttachment(attachmentName, new DataContainerResource(attachmentData));
	}

	protected void attachException(MimeMessageHelper messageHelper, ExceptionInfo exceptionInfo) throws MessagingException {
		Throwable exception = exceptionInfo.getException();
		if (exception == null) {
			return;
		}

		ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(bufOut);
		exception.printStackTrace(printOut);
		printOut.flush();
		
		String attachmentName = "exception_" + System.identityHashCode(exception) + ".txt";
		messageHelper.addAttachment(attachmentName, new ByteArrayResource(bufOut.toByteArray()));
	}

	protected String getFromAddress() {
		String fromAddress = (String) schedulerContext.get(SCHEDULER_CONTEXT_KEY_MAIL_FROM_ADDRESS);
		return fromAddress;
	}

	protected JavaMailSender getMailSender() {
		JavaMailSender mailSender = (JavaMailSender) schedulerContext.get(SCHEDULER_CONTEXT_KEY_MAIL_SENDER);
		return mailSender;
	}
	
	protected Map getExportParametersMap() {
		return (Map) schedulerContext.get(SCHEDULER_CONTEXT_KEY_EXPORT_PARAMETRES_MAP);
	}

	protected DataContainer createDataContainer() {
		DataContainerFactory factory = 
			(DataContainerFactory) schedulerContext.get(SCHEDULER_CONTEXT_KEY_DATA_CONTAINER_FACTORY);
		return factory.createDataContainer();
	}
	
	protected static class ReportOutput {
		private final DataContainer data;
		private final String fileType;
		private final String filename;
		private final List children;
		private String repositoryPath;
		
		public ReportOutput(DataContainer data, String fileType, String filename) {
			this.data = data;
			this.fileType = fileType;
			this.filename = filename;
			this.children = new ArrayList();
		}

		public DataContainer getData() {
			return data;
		}

		public String getFilename() {
			return filename;
		}

		public String getFileType() {
			return fileType;
		}
		
		public List getChildren() {
			return children;
		}
		
		public void addChild(ReportOutput child) {
			children.add(child);
		}

		public String getRepositoryPath() {
			return repositoryPath;
		}

		public void setRepositoryPath(String repositoryPath) {
			this.repositoryPath = repositoryPath;
		}
	}
}
