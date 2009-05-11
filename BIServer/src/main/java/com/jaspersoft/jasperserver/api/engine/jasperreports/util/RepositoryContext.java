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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CompiledReportProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryContext.java 8408 2007-05-29 23:29:12Z melih $
 */
public class RepositoryContext {
	
	private RepositoryService repository;
	private ExecutionContext executionContext;
	private String contextURI;
	private ReportUnit reportUnit;
	private CompiledReportProvider compiledReportProvider;
	
	public RepositoryContext() {
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public String getContextURI() {
		return contextURI;
	}

	public void setContextURI(String contextURI) {
		this.contextURI = contextURI;
	}

	
	/**
	 * Sets the context URI as the context of local resource for a resource.
	 * <p>
	 * The repository service needs to be set before calling this method.
	 * 
	 * @param resourceURI the resource URI
	 * @see #setRepository(RepositoryService)
	 */
	public void setContextResourceURI(String resourceURI) {
		int lastSepIdx = resourceURI.lastIndexOf(Folder.SEPARATOR);
		String resourceName;
		String folder;
		if (lastSepIdx >= 0) {
			resourceName = resourceURI.substring(lastSepIdx + Folder.SEPARATOR_LENGTH);
			folder = resourceURI.substring(0, lastSepIdx + Folder.SEPARATOR_LENGTH);
		} else {
			resourceName = resourceURI;
			folder = "";
		}
		String childrenFolderName = getRepository().getChildrenFolderName(resourceName);
		String childrenFolderURI = folder + childrenFolderName;
		setContextURI(childrenFolderURI);
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public ReportUnit getReportUnit() {
		return reportUnit;
	}

	public void setReportUnit(ReportUnit reportUnit) {
		this.reportUnit = reportUnit;
	}

	public CompiledReportProvider getCompiledReportProvider() {
		return compiledReportProvider;
	}

	public void setCompiledReportProvider(CompiledReportProvider compiledReportProvider) {
		this.compiledReportProvider = compiledReportProvider;
	}

}
