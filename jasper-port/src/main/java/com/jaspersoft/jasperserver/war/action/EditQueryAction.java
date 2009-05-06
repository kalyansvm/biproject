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

import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.QueryWrapper;
import com.jaspersoft.jasperserver.war.dto.ReportDataSourceWrapper;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class EditQueryAction extends FormAction
{
	private static final String DATA_SOURCE_PARENT_TYPE = "query";
	private static final String FORM_OBJECT_KEY = "query";
	private static final String PARENT_FOLDER_ATTR = "parentFolder";
	private static final String CURRENT_QUERY_ATTR = "currentQuery";
	private static final String IS_EDIT = "isEdit";//FIXME use wrapper to disable name in UI
	private static final String DATASOURCE_OBJECT_KEY = "dataResource";

	private RepositoryService repository;
	private JasperServerConstImpl constants = new JasperServerConstImpl();

	private String queryLanguagesRequestAttrName;
	private String[] queryLanguages;

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}


	protected void initBinder(RequestContext context, DataBinder binder) {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}


	/**
	 *
	 */
	public EditQueryAction(){
		setFormObjectClass(QueryWrapper.class); //custom form backing object class
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW); 		//this is a multi-page wizard!
	}


	/**
	 *
	 */
	public Object createFormObject(RequestContext context)
	{
		Query query;
		QueryWrapper wrapper;
		ExecutionContextImpl executionContext = new ExecutionContextImpl();

		String isEdit = (String)context.getFlowScope().get(IS_EDIT);
		if (isEdit == null) {
			isEdit = (String)context.getRequestParameters().get(IS_EDIT);
			context.getFlowScope().put(IS_EDIT, isEdit);
		}
		if (isEdit != null)
		{
			String currentQuery = (String) context.getFlowScope().get(CURRENT_QUERY_ATTR);
			if (currentQuery == null) {
				currentQuery = (String)context.getRequestParameters().get(CURRENT_QUERY_ATTR);
				context.getFlowScope().put(CURRENT_QUERY_ATTR, currentQuery);
			}
			query = (Query) repository.getResource(executionContext, currentQuery);
			if(query == null){
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {currentQuery});
			}
			wrapper = new QueryWrapper(query);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
		}
		else
		{
			query = (Query) repository.newResource(executionContext, Query.class);
			String parentFolder = (String) context.getFlowScope().get(PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
			   parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
			   context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);	
			}
			if (parentFolder == null || parentFolder.trim().length() == 0)
				parentFolder = "/";
			query.setParentFolder(parentFolder);
			query.setLanguage(getQueryLanguages()[0]);
			wrapper = new QueryWrapper(query);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
		}

		return wrapper;
	}


	/**
	 *
	 */
	public Event initAction(RequestContext context) throws Exception
	{
		MutableAttributeMap rs = context.getRequestScope();
		rs.put(FORM_OBJECT_KEY, getFormObject(context));
		return success();
	}


	/**
	 *
	 */
	public Event saveQuery(RequestContext context) throws Exception
	{
		QueryWrapper wrapper = (QueryWrapper) getFormObject(context);
		if (wrapper.isStandAloneMode()) {
			try {
				repository.saveResource(null, wrapper.getQuery());
				return yes();
			} catch (JSDuplicateResourceException e) {
				getFormErrors(context).rejectValue("query.name", "QueryValidator.error.duplicate");
				return error();
			}
		}
		return success();
	}


	/**
	 *
	 */
	public Event locateDataSource(RequestContext context) throws Exception {
		//log("In locate data source");
		QueryWrapper queryWrapper = (QueryWrapper) getFormObject(context);
		ResourceReference dsRef = queryWrapper.getQuery().getDataSource();
		ReportDataSourceWrapper rdWrapper = new ReportDataSourceWrapper();
		rdWrapper.setParentType(DATA_SOURCE_PARENT_TYPE);
		rdWrapper.setMode(BaseDTO.MODE_SUB_FLOW_NEW);
		if (dsRef == null) {
			//log("Found no previous ReportDataSource, creating new");
//			ReportDataSource ds = (ReportDataSource) repository.newResource(
//					null, JdbcReportDataSource.class);
//			dsRef = new ResourceReference(ds);
//			rdWrapper.setSource(constants.getFieldChoiceLocal());
//			rdWrapper.setType(constants.getJDBCDatasourceType());
//			rdWrapper.setReportDataSource(ds);

			rdWrapper.setSource(constants.getFieldChoiceNone());
			rdWrapper.setReportDataSource(null);

		} else {
			// if the dataSource exists decide source and type and set in
			// wrapper
			if (dsRef.isLocal()) {
				rdWrapper.setSource(constants.getFieldChoiceLocal());
				ReportDataSource ds = (ReportDataSource) dsRef.getLocalResource();
				if (JdbcReportDataSource.class.isAssignableFrom(ds.getClass())) {
					//log("Found JDBCReportDataSource");
					rdWrapper.setType(constants.getJDBCDatasourceType());
				} else if (JndiJdbcReportDataSource.class.isAssignableFrom(ds.getClass())) {
						rdWrapper.setType(constants.getJNDIDatasourceType());
				} else if (BeanReportDataSource.class.isAssignableFrom(ds.getClass())) {
					rdWrapper.setType(constants.getBeanDatasourceType());
				} else {
					//FIXME custom data source
				}
				rdWrapper.setReportDataSource(ds);
			} else {
				// DataSource object is a lookup
				//log("Found ReportDataSourceLookup");
				rdWrapper.setSource(constants.getFieldChoiceRepo());
				rdWrapper.setSelectedUri(dsRef.getReferenceURI());
			}
		}
		// Set the object into scope with the name that the reportDataSourceFlow
		// can pickup
		context.getFlowScope().put(ReportDataSourceAction.getFORM_OBJECT_KEY(),
				rdWrapper);
		return success();
	}


	/**
	 *
	 */
	public Event saveDatasource(RequestContext context) throws Exception {
		ReportDataSourceWrapper resource = (ReportDataSourceWrapper) context
				.getFlowScope().get(DATASOURCE_OBJECT_KEY);
		QueryWrapper queryWrapper = (QueryWrapper) getFormObject(context);
		if (resource.getSource().equals(constants.getFieldChoiceRepo())) {
			queryWrapper.getQuery().setDataSourceReference(
					resource.getSelectedUri());
		} else if (resource.getSource().equals(constants.getFieldChoiceLocal())) {
			queryWrapper.getQuery().setDataSource(
					resource.getReportDataSource());
		} else {
			queryWrapper.getQuery().setDataSource((ResourceReference) null);
	}
		return success();
	}


	public Event prepareQueryTextEdit(RequestContext context) {
		context.getRequestScope().put(getQueryLanguagesRequestAttrName(), getQueryLanguages());
		return success();
	}


	public Event validateQuery(RequestContext context) throws Exception 
	{
		Errors errors = getFormErrors(context);
		
		QueryWrapper wrapper = (QueryWrapper)getFormObject(context);
		
		getValidator().validate(wrapper, errors);
		
		List fieldErrors = errors.getFieldErrors();
		if (fieldErrors != null && !fieldErrors.isEmpty())
		{
			FieldError error = (FieldError)fieldErrors.get(0);
			String field = error.getField();
			
			if (
				"query.name".equals(field)
				|| "query.label".equals(field)
				|| "query.description".equals(field)
				)
			{
				return result("editQueryForm");
			} 
			else if ("query.sql".equals(field))
			{
				return result("editQueryTextForm");
			}
		}

		return success();
	}


	public String[] getQueryLanguages() {
		return queryLanguages;
	}

	public void setQueryLanguages(String[] queryLanguages) {
		this.queryLanguages = queryLanguages;
	}

	public String getQueryLanguagesRequestAttrName() {
		return queryLanguagesRequestAttrName;
	}

	public void setQueryLanguagesRequestAttrName(
			String queryLanguagesRequestAttrName) {
		this.queryLanguagesRequestAttrName = queryLanguagesRequestAttrName;
	}

}

