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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: ReportUnitBean.java 8545 2007-06-04 09:03:56Z lucian $
 */

public class ReportUnitBean extends ResourceBean {

	/*
	 * The following come from the ReportUnit interface 
	 */
	private ResourceReferenceBean dataSource;
	private ResourceReferenceBean query;
	private ResourceReferenceBean[] inputControls;
	private ResourceReferenceBean mainReport;
	private ResourceReferenceBean[] resources;
	private String inputControlRenderingView;
	private String reportRenderingView;
    private boolean alwaysPromptControls;
    private byte controlsLayout = ReportUnit.LAYOUT_SEPARATE_PAGE;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		ReportUnit ru = (ReportUnit) res;
		setDataSource(exportHandler.handleReference(ru.getDataSource()));
		setQuery(exportHandler.handleReference(ru.getQuery()));
		setInputControls(handleReferences(ru.getInputControls(), exportHandler));
		setMainReport(exportHandler.handleReference(ru.getMainReport()));
		setResources(handleReferences(ru.getResources(), exportHandler));
		setInputControlRenderingView(ru.getInputControlRenderingView());
		setReportRenderingView(ru.getReportRenderingView());
		setAlwaysPromptControls(ru.isAlwaysPromptControls());
		setControlsLayout(ru.getControlsLayout());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		ReportUnit ru = (ReportUnit) res;
		ru.setDataSource(importHandler.handleReference(getDataSource()));
		ru.setQuery(importHandler.handleReference(getQuery()));
		ru.setInputControls(handleReferences(getInputControls(), importHandler));
		ru.setMainReport(importHandler.handleReference(getMainReport()));
		ru.setResources(handleReferences(getResources(), importHandler));
		ru.setInputControlRenderingView(getInputControlRenderingView());
		ru.setReportRenderingView(getReportRenderingView());
		ru.setAlwaysPromptControls(isAlwaysPromptControls());
		ru.setControlsLayout(getControlsLayout());
	}

	public ResourceReferenceBean getDataSource() {
		return dataSource;
	}

	public void setDataSource(ResourceReferenceBean dataSource) {
		this.dataSource = dataSource;
	}
	
	public ResourceReferenceBean getQuery() {
		return query;
	}

	public void setQuery(ResourceReferenceBean query) {
		this.query = query;
	}	
	
	public ResourceReferenceBean[] getInputControls() {
		return inputControls;
	}

	public void setInputControls(ResourceReferenceBean[] inputControls) {
		this.inputControls = inputControls;
	}

	public ResourceReferenceBean getMainReport() {
		return mainReport;
	}

	public void setMainReport(ResourceReferenceBean mainReport) {
		this.mainReport = mainReport;
	}

	public ResourceReferenceBean[] getResources() {
		return resources;
	}

	public void setResources(ResourceReferenceBean[] resources) {
		this.resources = resources;
	}

	public boolean isAlwaysPromptControls() {
		return alwaysPromptControls;
	}

	public void setAlwaysPromptControls(boolean alwaysPromptControls) {
		this.alwaysPromptControls = alwaysPromptControls;
	}

	public byte getControlsLayout() {
		return controlsLayout;
	}

	public void setControlsLayout(byte controlsLayout) {
		this.controlsLayout = controlsLayout;
	}

	public String getInputControlRenderingView() {
		return inputControlRenderingView;
	}

	public void setInputControlRenderingView(String inputControlRenderingView) {
		this.inputControlRenderingView = inputControlRenderingView;
	}

	public String getReportRenderingView() {
		return reportRenderingView;
	}

	public void setReportRenderingView(String reportRenderingView) {
		this.reportRenderingView = reportRenderingView;
	}
}
