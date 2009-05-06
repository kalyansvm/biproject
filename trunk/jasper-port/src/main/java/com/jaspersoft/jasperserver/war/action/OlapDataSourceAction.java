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
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.JdbcOlapDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.JndiJdbcOlapDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.OlapDataSourceWrapper;
import com.jaspersoft.jasperserver.war.validation.OlapDataSourceValidator;

/**
 * 
 * OlapDataSourceAction provides the actions for the olap data source web flow
 *
 * @author jshih
 */
public class OlapDataSourceAction extends FormAction {
	protected final Log log = LogFactory.getLog(this.getClass());
	private static final String FORM_OBJECT_KEY="dataResource";
	private static final String DATASOURCEURI_PARAM = "resource";
	private static final String PARENT_FOLDER_ATTR = "parentFolder";
	private RepositoryService repository;
	private OlapConnectionService olapConnection;
	private JasperServerConstImpl constants=new JasperServerConstImpl();

	/**
	 * initialize OlapDataSourceAction.class object
	 */
	public OlapDataSourceAction(){
		setFormObjectClass(OlapDataSourceWrapper.class);
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW);
		setValidator(new OlapDataSourceValidator());
	}

	/**
	 * initAction performs the initialization for the olap data source web flow
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event initAction(RequestContext context) throws Exception {
		OlapDataSourceWrapper formObject = 
			(OlapDataSourceWrapper) getFormObject(context);
		if (formObject.isSubflowMode()
				&& formObject.getAllDatasources() == null) {
			// get a list of all datasources in repo and set in the formObject
			FilterCriteria criteria = FilterCriteria
					.createFilter(ReportDataSource.class);
			ResourceLookup[] lookups = repository.findResource(JasperServerUtil.getExecutionContext(context), criteria);
			List allDataSources = null;
			if (lookups != null && lookups.length != 0) {
				log("Found OlapDataSource lookups size=" + lookups.length);
				allDataSources = new ArrayList(lookups.length);
				for (int i = 0; i < lookups.length; i++) {
					ResourceLookup dr = lookups[i];
					allDataSources.add(dr.getURIString());
				}
			}
			formObject.setAllDatasources(allDataSources);
		}
		log("Type of datasource=" + formObject.getType() + " Mode="
				+ formObject.getMode());
		context.getFlowScope().put("constants", constants);
		return success();
	}
	
	/**
	 * handleTypeSelection handles data source type selection
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event handleTypeSelection(RequestContext context) throws Exception {
		OlapDataSourceWrapper formObject = 
			(OlapDataSourceWrapper) getFormObject(context);
		//If the object instance held by OlapDataSource is not the type selected 
		// copy common things
		ReportDataSource ds=formObject.getOlapDataSource();
		if(ds==null){
			log("Type="+formObject.getType());
			if(formObject.getType().equals(constants.getJNDIDatasourceType())) {
				ds=(JndiJdbcReportDataSource) repository.newResource(
						null,JndiJdbcReportDataSource.class);
			} else
				ds=(JdbcReportDataSource)repository.newResource(
						null,JdbcReportDataSource.class);
			formObject.setOlapDataSource(ds);
		}
		
		if(JdbcOlapDataSource.class.isAssignableFrom(ds.getClass())) {
			if(formObject.getType().equals(constants.getJNDIDatasourceType())) {
				// User opted for JNDI type and the exisiting OlapDataSource instance is Jdbc
				JndiJdbcReportDataSource jndiSource=
					(JndiJdbcReportDataSource)repository.newResource(
							null,JndiJdbcReportDataSource.class);
				jndiSource.setParentFolder(ds.getParentFolder());
				jndiSource.setName(ds.getName());
				jndiSource.setLabel(ds.getLabel());
				jndiSource.setDescription(ds.getDescription());
				jndiSource.setVersion(ds.getVersion());
				formObject.setOlapDataSource(jndiSource);
			}
		} else {
			// OlapDataSource holds an instance of JdbcOlapDataSource
			if(formObject.getType().equals(constants.getJDBCDatasourceType())) {
				// User opted for Jndi type
				JdbcReportDataSource jdbcSource=
					(JdbcReportDataSource)repository.newResource(null,JdbcReportDataSource.class);
				jdbcSource.setParentFolder(ds.getParentFolder());
				jdbcSource.setName(ds.getName());
				jdbcSource.setLabel(ds.getLabel());
				jdbcSource.setDescription(ds.getDescription());
				jdbcSource.setVersion(ds.getVersion());
				formObject.setOlapDataSource(jdbcSource);
			}
		}
		return success();
	}
	
	/**
	 * saveLookup catptures the data source selected
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event saveLookup(RequestContext context) throws Exception {
		OlapDataSourceWrapper formObject=(OlapDataSourceWrapper) getFormObject(context);
		log("user selected a reusable OlapDataSource");
		String selectedUri=formObject.getSelectedUri();  
		Resource resource=repository.getResource(null, selectedUri);
		formObject.setOlapDataSource((ReportDataSource) resource);
		formObject.setSelectedUri(selectedUri);
		return success();
	}
	
	/**
	 * saveDatasource stores the selected data source
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event saveDatasource(RequestContext context) throws Exception {
		OlapDataSourceWrapper formObject=(OlapDataSourceWrapper) getFormObject(context);
		log("Saving the datasource back ");
		if(formObject.isStandAloneMode()) {
			if (formObject.getType()!=null) {
				ReportDataSource ds=formObject.getOlapDataSource();
				log("Saving DataSource name="+ds.getName()+
					" datasource desc="+ds.getDescription()+" in folder="+ds.getParentFolder());
				if(ds.getName()!=null)
					repository.saveResource(null,ds);
			}
		}
		return success();
	}
	
	/**
	 * createFormObject loads the form object
	 * 
	 * @param context
	 */
	public Object createFormObject(RequestContext context)
	{
		// TODO was not called during initAction(),
		// the one in OlapUnitAction was called instead
		OlapDataSourceWrapper formObject = null;
		String resourceUri = context.getRequestParameters().get(DATASOURCEURI_PARAM);
		if( resourceUri!=null && resourceUri.trim().length()!=0){
			Resource resource=(Resource)repository.getResource(null,resourceUri);
			if(resource==null)
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {resourceUri});
			log("Found resource with uri="+resourceUri);
			formObject=new OlapDataSourceWrapper();
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
			ReportDataSource dataSource=(ReportDataSource)resource;
			if(JdbcOlapDataSource.class.isAssignableFrom(dataSource.getClass())){
				formObject.setType(constants.getJDBCDatasourceType());
			}else
				if(JndiJdbcOlapDataSource.class.isAssignableFrom(dataSource.getClass()))
					formObject.setType(constants.getJNDIDatasourceType());
			formObject.setOlapDataSource(dataSource);
		}
		if(formObject==null){
			String parentFolder = (String) context.getFlowScope().get(PARENT_FOLDER_ATTR);
			if(parentFolder==null || parentFolder.trim().length()==0)
				parentFolder="/";
			log("Datasource flow: Stand alone new mode");
			formObject=new OlapDataSourceWrapper();
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
			//	set default options for datasource type
			formObject.setType(constants.getJNDIDatasourceType());
			formObject.setSource(constants.getFieldChoiceLocal());
			JndiJdbcReportDataSource jndiSource=
				(JndiJdbcReportDataSource)repository.newResource(null,JndiJdbcReportDataSource.class);
			jndiSource.setParentFolder(parentFolder);
			jndiSource.setVersion(Resource.VERSION_NEW);
			formObject.setOlapDataSource(jndiSource);
		}
		// setting selected uri allows jsp to position the selected data source item
		formObject.setSelectedUri(resourceUri);
		return formObject;
	}

	/**
	 * getRepository returns repository service property
	 * 
	 * @return
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
	 * method to get the olap connection service object
	 * 
	 * @return
	 */
	public OlapConnectionService getOlapConnection() {
		return this.olapConnection;
	}
	
	/**
	 * method to set the olap connection service object
	 * 
	 * @param olapConnection
	 */
	public void setOlapConnection(OlapConnectionService olapConnection) {
		this.olapConnection = olapConnection;
	}

	/**
	 * log logs debug message
	 *
	 * @param text
	 */private void log(String text) {
		log.debug(text);
	}
	 
	/**
	 * getDATASOURCEURI_PARAM
	 * @return
	 */
	public static String getDATASOURCEURI_PARAM() {
		return DATASOURCEURI_PARAM;
	}
	
	/**
	 * getFORM_OBJECT_KEY
	 * @return
	 */
	public static String getFORM_OBJECT_KEY() {
		return FORM_OBJECT_KEY;
	}
}
