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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.MondrianXmlaSourceWrapper;

/**
 * The EditMondrianXmlaSourceAction class provides action methods for 
 * the mondrianXmlaSourceFlow web flow
 * 
 * @author jshih
 */
public class EditMondrianXmlaSourceAction extends FormAction {
	public final Log log = LogFactory.getLog(this.getClass());
	private static final String FORM_OBJECT_KEY = "mondrianXmlaSource";
	private static final String PARENT_FOLDER_ATTR = "parentFolder";
	private static final String CURRENT_MONDRIAN_XMLA_DEFINITION_ATTR = "currentMondrianXmlaDefinition";
	private static final String IS_EDIT = "isEdit";//FIXME use wrapper to disable name in UI
	private RepositoryService repository;
	private OlapConnectionService connectionService;

	/**
	 * initialize EditMondrianXmlaSourceAction.class object
	 */
	public EditMondrianXmlaSourceAction() {
		setFormObjectClass(MondrianXmlaSourceWrapper.class);
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW);
	}
	
	/**
	 * createFormObject initializes form object
	 * 
	 * @param context 
	 * @return wrapper
	 */
	public Object createFormObject(RequestContext context) {
		MondrianXMLADefinition mondrianXmlaDefinition;
		MondrianXmlaSourceWrapper wrapper;
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		
		String isEdit = (String)context.getFlowScope().get(IS_EDIT);
		if (isEdit == null) {
			isEdit = (String)context.getRequestParameters().get("isEdit");
			context.getFlowScope().put(IS_EDIT, isEdit);
		}
		
		if (isEdit != null) {
			String currentMondrianXmlaDefinition = (String) context
					.getFlowScope().get(CURRENT_MONDRIAN_XMLA_DEFINITION_ATTR);
			if (currentMondrianXmlaDefinition == null) {
				currentMondrianXmlaDefinition = (String)context.getRequestParameters().get("selectedResource");
			    context.getFlowScope().put(CURRENT_MONDRIAN_XMLA_DEFINITION_ATTR, currentMondrianXmlaDefinition);
			}
			mondrianXmlaDefinition = (MondrianXMLADefinition) repository
					.getResource(executionContext,
							currentMondrianXmlaDefinition);
			if(mondrianXmlaDefinition == null){
				context.getFlowScope().remove("prevForm");
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {currentMondrianXmlaDefinition});
			}
			wrapper = new MondrianXmlaSourceWrapper(mondrianXmlaDefinition);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
		} else {
			mondrianXmlaDefinition = (MondrianXMLADefinition) repository
					.newResource(executionContext, MondrianXMLADefinition.class);
			String parentFolder = (String) context.getFlowScope().get(
					PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
				parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
			}
			if (parentFolder == null || parentFolder.trim().length() == 0)
				parentFolder = "/";
			mondrianXmlaDefinition.setParentFolder(parentFolder);
			wrapper = new MondrianXmlaSourceWrapper(mondrianXmlaDefinition);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
		}
		getAllMondrianConnections(context, wrapper);
		return wrapper;
	}
	
	/**
	 * getAllMondrianConnections retrieves all Mondrian connections
	 * 
	 * @param context 
	 * @param wrapper
	 */
	private void getAllMondrianConnections(RequestContext context, MondrianXmlaSourceWrapper wrapper) {
		FilterCriteria filterCriteria = FilterCriteria
				.createFilter(MondrianConnection.class);
		ResourceLookup[] resourceLookup = repository.findResource(JasperServerUtil.getExecutionContext(context),
				filterCriteria);
		List allMondrianConnections = null;
		if (resourceLookup != null && resourceLookup.length != 0) {
			log("Found Mondrian conneciton lookups size="
					+ resourceLookup.length);
			allMondrianConnections = new ArrayList(resourceLookup.length);
			for (int i = 0; i < resourceLookup.length; i++) {
				Resource resource = resourceLookup[i];
				Object resourceObj = repository.getResource(null, resource
						.getURIString());
				if (!allMondrianConnections
						.contains(((OlapClientConnection) resourceObj)
								.getURIString())) {
					allMondrianConnections
							.add(((OlapClientConnection) resourceObj)
									.getURIString());
				}
			}
			wrapper.setAllMondrianConnections(allMondrianConnections);
		}
	}
	
	/**
	 * saveMondrianXmlaSource saves mondrian xmla source definitions.
	 * 
	 * @param context
	 * @return success() if valid, otherwise error()
	 * @throws Exception
	 */
	public Event saveMondrianXmlaSource(RequestContext context)
			throws Exception {
		MondrianXmlaSourceWrapper wrapper = (MondrianXmlaSourceWrapper) getFormObject(context);
		try {
			if (wrapper.isStandAloneMode()) {
				MondrianConnection mondrianConnection = (MondrianConnection) repository
						.getResource(null, wrapper.getConnectionUri());
				wrapper.getMondrianXmlaDefinition().setMondrianConnection(
						mondrianConnection);
				wrapper.getMondrianXmlaDefinition()
						.setMondrianConnectionReference(
								mondrianConnection.getURIString());
				repository.saveResource(null, wrapper
						.getMondrianXmlaDefinition());
				wrapper.setConnectionInvalid(false);
			}
		} catch (JSDuplicateResourceException e) {
			getFormErrors(context).rejectValue("mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.duplicate");
			return error();
		} catch (Exception e) {
			wrapper.setConnectionInvalid(true);
		}
		return success();
	}

	/**
	 * getRepository returns repository service property
	 * 
	 * @return repository
	 */
	public RepositoryService getRepository() {
		return repository;
	}

	/**
	 * setRepository sets repository service property
	 * 
	 * @param repository
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	/**
	 * getConnectionService returns connection service
	 * 
	 * @return connectionService
	 */
	public OlapConnectionService getConnectionService() {
		return connectionService;
	}

	/**
	 * setConnectionService sets connection service property
	 * 
	 * @param connectionService
	 */
	public void setConnectionService(OlapConnectionService connectionService) {
		this.connectionService = connectionService;
	}
	
	/**
	 * initBinder initializes binder object
	 * 
	 * @param context
	 * @param binder
	 */
	public void initBinder(RequestContext context, DataBinder binder) {
		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
	}
	
	/**
	 * setupEditForm set the form object
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event setupEditForm(RequestContext context) throws Exception {
		MutableAttributeMap rs = context.getRequestScope();
		rs.put(FORM_OBJECT_KEY, getFormObject(context));
		return success();
	}
	
	/**
	 * log logs debug message
	 * 
	 * @param text
	 */
	private void log(String text) {
		log.debug(text);
	}
}

