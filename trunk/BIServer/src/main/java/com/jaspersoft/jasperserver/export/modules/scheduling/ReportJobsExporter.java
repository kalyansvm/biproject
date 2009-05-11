/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package com.jaspersoft.jasperserver.export.modules.scheduling;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportJobBean;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportUnitJobsIndexBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobsExporter.java 12731 2008-03-28 13:37:04Z lucian $
 */
public class ReportJobsExporter extends BaseExporterModule {

	protected SchedulingModuleConfiguration configuration;
	protected String reportJobsArg;

	protected Set exportedURIs;

	protected boolean isToProcess() {
		return hasParameter(reportJobsArg);
	}
	
	public void process() {
		mkdir(configuration.getReportJobsDir());

		exportedURIs = new HashSet();
		
		String[] reportURIs = exportEverything ? new String[]{"/"} : getParameterValues(reportJobsArg);
		for (int i = 0; i < reportURIs.length; i++) {
			String uri = reportURIs[i];
			processUri(uri);
		}
	}

	private void processUri(String uri) {
		Folder folder = configuration.getRepository().getFolder(executionContext, uri);
		if (folder == null) {
			Resource resource = configuration.getRepository().getResource(executionContext, uri);
			if (resource == null) {
				throw new JSException("jsexception.repository.uri.neither.report.nor.folder", new Object[] {uri});
			}
			
			processResource(resource);
		} else {
			processFolder(uri);
		}
	}
	
	protected void processResource(Resource resource) {
		if (resource instanceof ReportUnit) {
			processReportUnit(resource.getURIString());
		} else {
			throw new JSException("jsexception.resource.not.report.unit", new Object[] {resource.getURIString()});
		}
	}

	protected void processFolder(String uri) {
		processFolderResources(uri);
		
		List subFolders = configuration.getRepository().getSubFolders(executionContext, uri);
		if (subFolders != null && !subFolders.isEmpty()) {
			for (Iterator it = subFolders.iterator(); it.hasNext();) {
				Folder subFolder = (Folder) it.next();
				processFolder(subFolder.getURIString());
			}
		}
	}

	protected void processFolderResources(String folderURI) {
		FilterCriteria filter = FilterCriteria.createFilter(ReportUnit.class);
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(folderURI));
		ResourceLookup[] reportUnits = configuration.getRepository().findResource(executionContext, filter);
		if (reportUnits != null && reportUnits.length > 0) {
			for (int i = 0; i < reportUnits.length; i++) {
				ResourceLookup reportUnit = reportUnits[i];
				processReportUnit(reportUnit.getURIString());
			}
		}
	}

	protected void processReportUnit(String uri) {
		if (exportedURIs.contains(uri)) {
			return;
		}

		List jobs = configuration.getReportScheduler().getScheduledJobs(executionContext, uri);
		if (jobs != null && !jobs.isEmpty()) {
			exportJobs(uri, jobs);
			
			writeIndexReportUnitEntry(uri);
			
			exportedURIs.add(uri);

			commandOut.info("Exported " + jobs.size() + " job(s) for the " + uri + " report unit");
		} else {
			commandOut.debug("Report " + uri + " does not have any scheduled jobs");
		}
	}

	protected void exportJobs(String uri, List jobs) {
		String ruPath = mkdir(configuration.getReportJobsDir(), uri);
		
		long[] jobIds = new long[jobs.size()];
		int c = 0;
		for (Iterator iter = jobs.iterator(); iter.hasNext(); ++c) {
			ReportJobSummary jobSummary = (ReportJobSummary) iter.next();
			long jobId = jobSummary.getId();
			jobIds[c] = jobId;
			ReportJob job = configuration.getReportScheduler().getScheduledJob(executionContext, jobId);
			exportJob(ruPath, job);
		}
		
		ReportUnitJobsIndexBean indexBean = new ReportUnitJobsIndexBean();
		indexBean.setJobIds(jobIds);
		serialize(indexBean, ruPath, configuration.getReportUnitIndexFilename(), configuration.getSerializer());
	}

	protected void exportJob(String folderPath, ReportJob job) {
		ReportJobBean jobBean = new ReportJobBean();
		jobBean.copyFrom(job, getConfiguration());
		serialize(jobBean, folderPath, getJobFilename(job), configuration.getSerializer());
	}

	protected String getJobFilename(ReportJob job) {
		return job.getId() + ".xml";
	}

	protected void writeIndexReportUnitEntry(String uri) {
		Element ruElement = getIndexElement().addElement(configuration.getIndexReportUnitElement());
		ruElement.setText(uri);
	}

	public SchedulingModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SchedulingModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getReportJobsArg() {
		return reportJobsArg;
	}

	public void setReportJobsArg(String reportJobsArg) {
		this.reportJobsArg = reportJobsArg;
	}

}
