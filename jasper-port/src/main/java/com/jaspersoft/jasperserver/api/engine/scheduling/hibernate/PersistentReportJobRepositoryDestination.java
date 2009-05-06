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

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PersistentReportJobRepositoryDestination.java 12304 2008-02-29 14:12:37Z lucian $
 */
public class PersistentReportJobRepositoryDestination {
	
	private long id;
	private int version;
	private String folderURI;
	private boolean sequentialFilenames;
	private boolean overwriteFiles;
	private String outputDescription;
	private String timestampPattern;

	public PersistentReportJobRepositoryDestination() {
		version = ReportJob.VERSION_NEW;
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

	public String getFolderURI() {
		return folderURI;
	}

	public void setFolderURI(String folder) {
		this.folderURI = folder;
	}
	
	public boolean isSequentialFilenames() {
		return sequentialFilenames;
	}

	public void setSequentialFilenames(boolean sequentialFilenames) {
		this.sequentialFilenames = sequentialFilenames;
	}

	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}

	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}

	public void copyFrom(ReportJobRepositoryDestination repositoryDestination) {
		setFolderURI(repositoryDestination.getFolderURI());
		setSequentialFilenames(repositoryDestination.isSequentialFilenames());
		setOverwriteFiles(repositoryDestination.isOverwriteFiles());
		setOutputDescription(repositoryDestination.getOutputDescription());
		setTimestampPattern(repositoryDestination.getTimestampPattern());
	}

	public ReportJobRepositoryDestination toClient() {
		ReportJobRepositoryDestination repositoryDestination = new ReportJobRepositoryDestination();
		repositoryDestination.setId(getId());
		repositoryDestination.setVersion(getVersion());
		repositoryDestination.setFolderURI(getFolderURI());
		repositoryDestination.setSequentialFilenames(isSequentialFilenames());
		repositoryDestination.setOverwriteFiles(isOverwriteFiles());
		repositoryDestination.setOutputDescription(getOutputDescription());
		repositoryDestination.setTimestampPattern(getTimestampPattern());
		return repositoryDestination;
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}

	public String getOutputDescription() {
		return outputDescription;
	}

	public void setOutputDescription(String outputDescription) {
		this.outputDescription = outputDescription;
	}

	public String getTimestampPattern() {
		return timestampPattern;
	}

	public void setTimestampPattern(String timestampPattern) {
		this.timestampPattern = timestampPattern;
	}

}
