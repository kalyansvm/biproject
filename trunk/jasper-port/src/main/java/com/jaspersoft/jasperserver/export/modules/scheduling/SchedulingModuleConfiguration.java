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

import com.jaspersoft.jasperserver.api.engine.scheduling.ReportSchedulingInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.modules.common.ReportParametersTranslator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SchedulingModuleConfiguration.java 12731 2008-03-28 13:37:04Z lucian $
 */
public class SchedulingModuleConfiguration {
	
	private RepositoryService repository;
	private ReportSchedulingService reportScheduler;
	private ReportSchedulingInternalService internalReportScheduler;
	private UserAuthorityService authorityService;
	private ReportParametersTranslator reportParametersTranslator;

	private ObjectSerializer serializer;
	private String reportJobsDir;
	private String indexReportUnitElement;
	private String reportUnitIndexFilename;
	
	public ReportSchedulingService getReportScheduler() {
		return reportScheduler;
	}
	
	public void setReportScheduler(ReportSchedulingService reportScheduler) {
		this.reportScheduler = reportScheduler;
	}
	
	public ObjectSerializer getSerializer() {
		return serializer;
	}
	
	public void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}

	public String getReportJobsDir() {
		return reportJobsDir;
	}

	public void setReportJobsDir(String reportJobsDir) {
		this.reportJobsDir = reportJobsDir;
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public String getIndexReportUnitElement() {
		return indexReportUnitElement;
	}

	public void setIndexReportUnitElement(String indexReportUnitElement) {
		this.indexReportUnitElement = indexReportUnitElement;
	}

	public String getReportUnitIndexFilename() {
		return reportUnitIndexFilename;
	}

	public void setReportUnitIndexFilename(String reportUnitIndexFilename) {
		this.reportUnitIndexFilename = reportUnitIndexFilename;
	}

	public ReportSchedulingInternalService getInternalReportScheduler() {
		return internalReportScheduler;
	}

	public void setInternalReportScheduler(
			ReportSchedulingInternalService internalReportScheduler) {
		this.internalReportScheduler = internalReportScheduler;
	}

	public UserAuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(UserAuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public ReportParametersTranslator getReportParametersTranslator() {
		return reportParametersTranslator;
	}

	public void setReportParametersTranslator(
			ReportParametersTranslator reportParametersTranslator) {
		this.reportParametersTranslator = reportParametersTranslator;
	}

}
