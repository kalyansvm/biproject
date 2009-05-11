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

import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

/**
 * ListOlapViewsAction provides the action to display olap views
 *
 * @author jshih
 */
public class ListOlapViewsAction extends FormAction {
	private RepositoryService repository;

	/**
	 * listOlapViews performs the list olap views action
	 * 
	 * @param context
	 * @return
	 */
	public Event listOlapViews(RequestContext context) {
		ExecutionContext executionContext = JasperServerUtil.getExecutionContext(context);
		ResourceLookup[] olapUnits = repository.findResource(executionContext,
				FilterCriteria.createFilter(OlapUnit.class));
		context.getRequestScope().put("olapUnits", olapUnits);
		return success();
	}

	/**
	 * getRepository returns the repository service property
	 * 
	 * @return
	 */
	public RepositoryService getRepository() {
		return repository;
	}

	/**
	 * setRepository sets the repository service property
	 * @param repository
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}
}
