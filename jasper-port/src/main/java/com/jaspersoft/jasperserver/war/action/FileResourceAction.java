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
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElementDisjunction;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.FileResourceWrapper;
import com.jaspersoft.jasperserver.war.dto.OlapClientConnectionWrapper;

public class FileResourceAction extends FormAction {

	protected final Log log = LogFactory.getLog(this.getClass());

	protected static final String FORM_OBJECT_KEY = "fileResource";

	private static final String FILERES_URI_PARAM = "resource";

	protected static final String PARENT_FOLDER_ATTR = "parentFolder";

	protected static final String CONSTANTS_KEY = "constants";

	protected RepositoryService repository;
	private ConfigurationBean configuration;

	public FileResourceAction() {
		setFormObjectClass(FileResourceWrapper.class);
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW);
	}

	public Event initAction(RequestContext context) throws Exception {
		FileResourceWrapper wrapper = (FileResourceWrapper) getFormObject(context);
		if (wrapper.isSubflowMode()) {			
			FilterCriteria criteria = FilterCriteria
					.createFilter(FileResource.class);
			if (wrapper.getFileResource().getFileType() != null
					&& wrapper.getFileResource().getFileType().trim().length() != 0) {
				criteria.addFilterElement(FilterCriteria
						.createPropertyEqualsFilter("fileType", wrapper
								.getFileResource().getFileType()));
			} else if (isMasterFlowReportUnit(context)) {
				FilterElementDisjunction olapTypesFilter = new FilterElementDisjunction();
				olapTypesFilter.addFilterElement(FilterCriteria.createPropertyEqualsFilter("fileType", 
						ResourceDescriptor.TYPE_MONDRIAN_SCHEMA));
				olapTypesFilter.addFilterElement(FilterCriteria.createPropertyEqualsFilter("fileType", 
						ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA));
				criteria.addNegatedFilterElement(olapTypesFilter);
			}
			ResourceLookup[] lookups = repository.findResource(JasperServerUtil.getExecutionContext(context), criteria);
			List allResources = null;
			if (lookups != null && lookups.length != 0) {
				allResources = new ArrayList();
				log("Found lookups size=" + lookups.length);
				for (int i = 0; i < lookups.length; i++) {
					allResources.add(lookups[i].getURIString());
				}
			}
			wrapper.setAllResources(allResources);
		}
		/* In new Mode get a list of all resources already present in the chosen
		 * folder, to validate resource name's uniqueness */
		if (wrapper.isNewMode()) {
			String folderURI = wrapper.getFileResource().getParentFolder();
			if (folderURI == null)
			{
				folderURI = "/";
			}
			FilterCriteria resourcesInFolder = FilterCriteria.createFilter();
			resourcesInFolder.addFilterElement(FilterCriteria
					.createParentFolderFilter(folderURI));
			log("Searching for resources in the chosen folder:"+folderURI);
			ResourceLookup[] existingResources = repository.findResource(null,
					resourcesInFolder);
			
			if (existingResources != null && existingResources.length != 0) {
				log("res lookup size="+existingResources.length);
				List allResources = new ArrayList();
				for (int i = 0; i < existingResources.length; i++) {
					ResourceLookup rLookup = existingResources[i];
					allResources.add(rLookup.getName());
					log("adding resource: "+rLookup.getName()+ " to the list");
				}
				wrapper.setExistingResources(allResources);
			}
		}
		
		if (wrapper.isSubflowMode()) {
			getAllFolders(wrapper); // TODO get this from main flow
			String folderURI = (String) context.getFlowScope().get(PARENT_FOLDER_ATTR);
			if (folderURI == null) {
				folderURI = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, folderURI);
			}			
			if (folderURI == null)
			{
				folderURI = "/";
			}
			if (!isMasterFlowReportUnit(context)) {
				wrapper.getFileResource().setParentFolder( // TODO put parent folder in flow scope in main flow
						folderURI);
			}
			// enable file resource jsp display mask
			if (wrapper.getParentFlowObject() instanceof OlapClientConnectionWrapper) {
				wrapper.getFileResource().setFileType(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA);
			}
		}
		// set default source
		
		if (wrapper.getSource() == null) {
			if (wrapper.isNewMode()) {
				wrapper.setSource(JasperServerConst.FIELD_CHOICE_FILE_SYSTEM);
			}
			else if (wrapper.getFileResource() != null) {
				wrapper.setSource(JasperServerConst.FIELD_CHOICE_CONT_REPO);
				wrapper.setNewUri(wrapper.getFileResource().getReferenceURI());
			}
		}
		context.getFlowScope().put(FORM_OBJECT_KEY, wrapper);
		context.getFlowScope().put(CONSTANTS_KEY, new JasperServerConstImpl()); 
		return success();
	}
	
	protected void getAllFolders(FileResourceWrapper wrapper) 
	{
		List allFolders = repository.getAllFolders(null);
		wrapper.setAllFolders(new ArrayList());
		for (int i = 0; i < allFolders.size(); i++) {
			String folderUri = ((Folder) allFolders.get(i)).getURIString();
			wrapper.getAllFolders().add(folderUri);
		}
	}

	public Event determineType(RequestContext context) throws Exception {
	
		FileResourceWrapper wrapper = (FileResourceWrapper) getFormObject(context);
		if (wrapper.getFileResource().getFileType() == null) {
			String fileExtension = context.getExternalContext().getRequestMap()
					.getString(JasperServerConst.UPLOADED_FILE_EXT);
			if (fileExtension != null && fileExtension.trim().length() != 0) {
				wrapper.getFileResource().setFileType(
						wrapper.getTypeForExtention(fileExtension));
			}
		}

		if (wrapper.getSource() != null
			&& wrapper.getSource().equals(
					JasperServerConst.FIELD_CHOICE_CONT_REPO)) {		
			// User opted for a lookup URI
			String newUri = wrapper.getNewUri();
			if (newUri != null && newUri.trim().length() != 0) {
				Resource resource = repository.getResource(null, newUri);
				if (resource == null)
					throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {newUri});
				if (FileResource.class.isAssignableFrom(resource.getClass())) {
					FileResource fileR = (FileResource) resource;
					wrapper.getFileResource().setFileType(fileR.getFileType());
				}	
				// for olap subflow reusing an existing resource
				if (wrapper.getFileResource().getFileType().equals(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA) ||
				    wrapper.getFileResource().getFileType().equals(ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA)) { // TODO: refactor pro				
					((FileResource) resource).setReferenceURI(wrapper.getFileResource().getReferenceURI());
					wrapper.setFileResource((FileResource) resource);
				}
			}
			wrapper.setLocated(true);
		}
		// allow file resource optional
		if (wrapper.getSource() != null) {
		    if (wrapper.getSource().equals(JasperServerConst.FIELD_CHOICE_FILE_SYSTEM)) {
			// clean up previsous selection
			wrapper.setNewUri(null);
			wrapper.setLocated(true);
		    }
		    else if (wrapper.getSource().equals(JasperServerConst.FIELD_CHOICE_NONE)) {
			// signal a remove file resource
			return no();
		    }
		}
		return success();
	}

	protected boolean isMasterFlowReportUnit(RequestContext context)
	{
		String masterFlow = context.getFlowScope().getString("masterFlow");
		return "reportUnit".equals(masterFlow);
	}

	public Event setupNamingForm(RequestContext context) throws Exception
	{
		context.getRequestScope().put("allTypes", configuration.getAllFileResourceTypes());
		return success();
	}
	/**
	 * Saves the changes made in alone mode back to repository
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event saveResource(RequestContext context) throws Exception {
		log("In saveresource");
		FileResourceWrapper wrapper = (FileResourceWrapper) getFormObject(context);
		if (wrapper.getFileResource().getParentFolder() == null)
			wrapper.getFileResource().setParentFolder("/");
		if (wrapper.isStandAloneMode()) {
			try {
				repository.saveResource(null, wrapper.getFileResource());
			}
			catch (JSDuplicateResourceException e) {
				getFormErrors(context).rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
				return error();
			}
			return yes();
		}
		return success();
	}

	public Object createFormObject(RequestContext context) {
		FileResourceWrapper formObject = null;
		String resourceUri = context.getRequestParameters().get(
				FILERES_URI_PARAM);			
		if (resourceUri == null) {
			resourceUri = context.getRequestParameters().get(
					"selectedResource");
		}		
		if (resourceUri != null && resourceUri.trim().length() != 0) {
			Resource resource = (Resource) repository.getResource(null,
					resourceUri);			
			
			if (resource == null) {
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {resourceUri});
			}	
			log("Found resource with uri=" + resourceUri);
			formObject = new FileResourceWrapper();
			formObject.setFileResource((FileResource) resource);
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
			formObject.setLocated(true);
		}
		if (formObject == null) {
			formObject = new FileResourceWrapper();
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
			// set default option for datasource type
			String parentFolder = (String) context.getFlowScope().get(
					PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
				parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
			}
			FileResource fileResource = (FileResource) repository.newResource(
					null, FileResource.class);
			fileResource.setParentFolder(parentFolder);
			fileResource.setVersion(FileResource.VERSION_NEW);
			formObject.setFileResource(fileResource);
		}
		return formObject;
	}

	/**
	 * Gets the repository service instance
	 * 
	 * @return
	 */
	public RepositoryService getRepository() {
		return repository;
	}

	/**
	 * Sets the Repository service instace, necessary to allow Spring inject the
	 * instance of Repository service
	 * 
	 * @param repository
	 */
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

	/**
	 * Registers a byte array editor to allow spring handle File uploads as byte
	 * arrays
	 */
	protected void initBinder(RequestContext context, DataBinder binder) {
		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
	}

	protected void log(String text) {
		log.debug(text);
	}

	public static String getFORM_OBJECT_KEY() {
		return FORM_OBJECT_KEY;
	}

	protected void doBind(RequestContext context, DataBinder binder) {
		super.doBind(context, binder);
		FileResourceWrapper res = (FileResourceWrapper) binder.getTarget();
		res.afterBind();
	}
}
