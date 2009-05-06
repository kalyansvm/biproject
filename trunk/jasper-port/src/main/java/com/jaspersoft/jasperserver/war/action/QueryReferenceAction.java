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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.QueryWrapper;
import com.jaspersoft.jasperserver.war.dto.ResourceReferenceDTO;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: QueryReferenceAction.java 10048 2007-09-13 16:23:56Z tdanciu $
 */
public class QueryReferenceAction extends FormAction {

	private RepositoryService repository;
	private String queryLookupsRequestAttrName;
	private String queryWrapperRequestAttrName;
	
	public Event prepareQuerySource(RequestContext context) {
		ResourceLookup[] queryLookups = getRepository().findResource(JasperServerUtil.getExecutionContext(context), FilterCriteria.createFilter(Query.class));
		context.getRequestScope().put(getQueryLookupsRequestAttrName(), queryLookups);
		return success();
	}
	
	public Event queryReference(RequestContext context) {
		if ("finish".equals(context.getRequestScope().get("endState")))
		{
			return result("finish");
		}
		return success();
	}
	
	public Event queryLocal(RequestContext context) throws Exception {
		ResourceReferenceDTO referenceDTO = referenceDTO(context);
		Query query = (Query) referenceDTO.getLocalResource();
		if (query == null) {
			query = (Query) repository.newResource(null, Query.class);
			query.setLanguage("sql"); //FIXME the first of the configured languages should be used
			referenceDTO.setLocalResource(query);
		}
		
		QueryWrapper queryWrapper = new QueryWrapper(query);
		byte mode = referenceDTO.getLocalResource().isNew() ? BaseDTO.MODE_SUB_FLOW_NEW : BaseDTO.MODE_SUB_FLOW_EDIT;
		queryWrapper.setMode(mode);
		context.getRequestScope().put(getQueryWrapperRequestAttrName(), queryWrapper);
		return success();
	}
	
	public Event queryNone(RequestContext context) {
		if ("finish".equals(context.getRequestScope().get("endState")))
		{
			return result("finish");
		}
		return success();
	}
	
	protected ResourceReferenceDTO referenceDTO(RequestContext context) throws Exception {
		return (ResourceReferenceDTO) getFormObject(context);
	}
	
	/**
	 * @return Returns the referenceURIListRequestAttrName.
	 */
	public String getQueryLookupsRequestAttrName() {
		return queryLookupsRequestAttrName;
	}
	
	/**
	 * @param referenceURIListRequestAttrName The referenceURIListRequestAttrName to set.
	 */
	public void setQueryLookupsRequestAttrName(
			String referenceURIListRequestAttrName) {
		this.queryLookupsRequestAttrName = referenceURIListRequestAttrName;
	}
	
	/**
	 * @return Returns the repository.
	 */
	public RepositoryService getRepository() {
		return repository;
	}
	
	/**
	 * @param repository The repository to set.
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}
	
	/**
	 * @return Returns the queryWrapperRequestAttrName.
	 */
	public String getQueryWrapperRequestAttrName() {
		return queryWrapperRequestAttrName;
	}
	
	/**
	 * @param queryWrapperRequestAttrName The queryWrapperRequestAttrName to set.
	 */
	public void setQueryWrapperRequestAttrName(
			String queryWrapperRequestAttrName) {
		this.queryWrapperRequestAttrName = queryWrapperRequestAttrName;
	}
}
