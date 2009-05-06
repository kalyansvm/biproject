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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImporter;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportJobBean;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportUnitJobsIndexBean;
import com.jaspersoft.jasperserver.export.util.PathUtils;
import com.jaspersoft.jasperserver.export.util.PathUtils.SplittedPath;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobsImporter.java 12757 2008-03-31 16:58:16Z lucian $
 */
public class ReportJobsImporter extends BaseImporterModule {

	private final static Log log = LogFactory.getLog(ResourceImporter.class);
	
	protected SchedulingModuleConfiguration configuration;
	private String prependPathArg;
	
	private String prependPath;
	private Map userIndicator;
	
	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
		
		prependPath = getPrependPath();
	}

	protected String getPrependPath() {
		String path = getParameterValue(getPrependPathArg());
		if (path != null) {
			path = PathUtils.normalizePath(path);
			if (path.length() == 0 || path.equals(Folder.SEPARATOR)) {
				path = null;
			} else if (!path.startsWith(Folder.SEPARATOR)) {
				path = Folder.SEPARATOR + path;
			}
		}
		return path;
	}

	public void process() {
		initProcess();
		
		for (Iterator i = indexElement.elementIterator(configuration.getIndexReportUnitElement()); i.hasNext(); ) {
			Element ruElement = (Element) i.next();
			String uri = ruElement.getText();
			processReportUnit(uri);
		}
	}

	protected void initProcess() {
		userIndicator = new HashMap();
	}

	protected void processReportUnit(String uri) {
		String newUri = prependedPath(uri);
		if (checkReportUnit(newUri)) {
			if (isUpdateResource(newUri)) {
				commandOut.warn("Report " + newUri + " was updated in repository, skipping jobs");
			} else {
				String ruPath = PathUtils.concatPaths(configuration.getReportJobsDir(), uri);
				
				ReportUnitJobsIndexBean indexBean = (ReportUnitJobsIndexBean) deserialize(ruPath, configuration.getReportUnitIndexFilename(), configuration.getSerializer());
				long[] jobIds = indexBean.getJobIds();
				int imported = 0;
				for (int i = 0; i < jobIds.length; i++) {
					long jobId = jobIds[i];
					if (importReportJob(newUri, ruPath, jobId)) {
						++imported;
					}
				}
				
				commandOut.info("Created " + imported + " job(s) for report " + newUri);
			}
		} else {
			commandOut.warn("Report unit " + newUri + " not found in repository, skipping jobs");
		}
	}

	protected boolean checkReportUnit(String uri) {
		SplittedPath splittedPath = PathUtils.splitPath(uri);
		FilterCriteria filter = FilterCriteria.createFilter(ReportUnit.class);
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(splittedPath.parentPath));
		filter.addFilterElement(FilterCriteria.createPropertyEqualsFilter("name", splittedPath.name));
		ResourceLookup[] lookups = configuration.getRepository().findResource(executionContext, filter);
		return lookups != null && lookups.length > 0;
	}

	protected boolean isUpdateResource(String resourceUri) {
		Collection updateResources = (Collection) getContextAttributes().getAttribute(
				ResourceImporter.ATTRIBUTE_UPDATE_RESOURCES);
		return updateResources != null && updateResources.contains(resourceUri);
	}

	protected boolean importReportJob(String reportUri, String jobsPath, long jobId) {
		boolean imported;
		String jobFilename = getJobFilename(jobId);
		ReportJobBean jobBean = (ReportJobBean) deserialize(jobsPath, jobFilename, configuration.getSerializer());
		if (userExists(jobBean.getUsername())) {
			importJob(reportUri, jobBean);
			imported = true;
		} else {
			commandOut.warn("User " + jobBean.getUsername() + " does not exist, skipping job " + jobBean.getId() + " of report " + reportUri);
			imported = false;
		}
		return imported;
	}

	protected String getJobFilename(long jobId) {
		return jobId + ".xml";
	}

	protected boolean userExists(String username) {
		Boolean indicator = (Boolean) userIndicator.get(username);
		if (indicator == null) {
			indicator = Boolean.valueOf(configuration.getAuthorityService().getUser(executionContext, username) != null);
			userIndicator.put(username, indicator);
		}
		return indicator.booleanValue();
	}

	protected void importJob(String newUri, ReportJobBean jobBean) {
		ReportJob job = new ReportJob();
		jobBean.copyTo(job, newUri, getConfiguration());
		
		ReportJob savedJob = configuration.getInternalReportScheduler().saveJob(executionContext, job);
		
		if (log.isDebugEnabled()) {
			log.debug("Created job " + savedJob.getId() + " for report " + newUri + " (old id " + jobBean.getId() + ")");
		}
	}
	
	protected String prependedPath(String uri) {
		return PathUtils.concatPaths(prependPath, uri);
	}

	public SchedulingModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SchedulingModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getPrependPathArg() {
		return prependPathArg;
	}

	public void setPrependPathArg(String prependPathArg) {
		this.prependPathArg = prependPathArg;
	}

}
