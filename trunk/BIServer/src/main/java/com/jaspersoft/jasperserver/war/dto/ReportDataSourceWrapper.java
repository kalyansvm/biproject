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
package com.jaspersoft.jasperserver.war.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

public class ReportDataSourceWrapper extends BaseDTO implements Serializable {
	private ReportDataSource reportDataSource;
	private List allDatasources;
	private boolean lookup;
	private String type;
	private String source;
	private String selectedUri;
	private List allFolders;
	private Object parentFlowObject;
	private String parentType;
	private ResourceLookup[] existingResources;
	private List customProperties;
	private String customDatasourceLabel;
	public boolean isLookup() {
		return lookup;
	}
	public void setLookup(boolean lookup) {
		this.lookup = lookup;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List getAllDatasources() {
		return allDatasources;
	}
	public void setAllDatasources(List allDatasources) {
		this.allDatasources = allDatasources;
	}
	public ReportDataSource getReportDataSource() {
		return reportDataSource;
	}
	public void setReportDataSource(ReportDataSource reportDataSource) {
		this.reportDataSource = reportDataSource;
	}
	public String getSelectedUri() {
		return selectedUri;
	}
	public void setSelectedUri(String selectedUri) {
		this.selectedUri = selectedUri;
	}
	public List getAllFolders() {
		return allFolders;
	}
	public void setAllFolders(List allFolders) {
		this.allFolders = allFolders;
	}
	
	public Object getParentFlowObject() {
		return parentFlowObject;
	}

	public void setParentFlowObject(Object parentFlowObject) {
		this.parentFlowObject = parentFlowObject;
	}
	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public ResourceLookup[] getExistingResources()
	{
		return existingResources;
	}

	public void setExistingResources(ResourceLookup[] existingResources)
	{
		this.existingResources = existingResources;
	}

	public List getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(List customProperties) {
		this.customProperties = customProperties;
	}
	
	public String getCustomDatasourceLabel() {
		return customDatasourceLabel;
	}
	public void setCustomDatasourceLabel(String customDatasourceLabel) {
		this.customDatasourceLabel = customDatasourceLabel;
	}
}
