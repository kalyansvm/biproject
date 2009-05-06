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

import java.util.List;
import java.util.ArrayList;

import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.tags.PaginatorTag;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

public class ListReportsAction extends FormAction
{
	private RepositoryService repository;
	private ConfigurationBean configuration;

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public ConfigurationBean getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(ConfigurationBean configuration)
	{
		this.configuration = configuration;
	}

	public Event listReports(RequestContext context)
	{
		List reportUnits = loadReports(context);
		
        // filter out everything under /adhoc/temp/,  fix bug 8100
		List filteredReportUnits = new ArrayList();
		String tempDir = "/adhoc/temp/";
		for (int i=0; i<reportUnits.size(); i++) {
			if (!(((ResourceLookup)(reportUnits.get(i))).getURIString()).startsWith(tempDir)) {
				//String parentUri = ((Resource)reportUnits.get(i)).getParentFolder();
				//((Resource)reportUnits.get(i)).setName(getParentFolderDisplayName(parentUri));
				filteredReportUnits.add(reportUnits.get(i));
			}				
		}
		context.getRequestScope().put("reportUnits", filteredReportUnits);
		return success();
	}

	protected List loadReports(RequestContext context)
	{
		List reportUnits = repository.loadResourcesList(
			JasperServerUtil.getExecutionContext(context),
			FilterCriteria.createFilter(ReportUnit.class)
			);
		return reportUnits;
	}

	public Event goToPage(RequestContext context)
	{
		context.getFlowScope().put(
			PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER, 
			context.getRequestParameters().get(PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER)
			);

		return success();
	}
	
	private String getParentFolderDisplayName(String uri) {
		int fromIndex = 1;

		if (uri.equals("/")) {
		   return "/root";
		}
		
	    StringBuffer displayLabel = new StringBuffer("/root");
		if (uri.length() > 1) {
			int lastIndex = uri.lastIndexOf("/");
			while ((fromIndex = uri.indexOf('/', fromIndex)) != -1) {	    		   
				String currentUri = uri.substring(0, uri.indexOf('/', fromIndex));	 	
 
				displayLabel.append("/").append(repository.getFolder(null, currentUri).getLabel());	 


				if (lastIndex == fromIndex) {
					break; 
				}
				fromIndex++;
			}
			displayLabel.append("/").append(repository.getFolder(null, uri).getLabel()); 	    		   


		}	       
	
		return displayLabel.toString();
	}
}
