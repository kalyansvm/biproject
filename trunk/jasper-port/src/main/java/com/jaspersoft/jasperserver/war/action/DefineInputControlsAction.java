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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.DataTypeWrapper;
import com.jaspersoft.jasperserver.war.dto.InputControlWrapper;
import com.jaspersoft.jasperserver.war.dto.ListOfValuesDTO;
import com.jaspersoft.jasperserver.war.dto.ResourceReferenceDTO;

public class DefineInputControlsAction extends FormAction {
	private final Log log = LogFactory.getLog(this.getClass());

	private static final String FORM_OBJECT_KEY="control";
	private static final String CONSTANTS_KEY="constants";
	private static final String DATATYPE_KEY="dataType";
	private static final String LOV_KEY="listOfValuesDTO";
	private static final String INPUTCONTROLURI_PARAM = "resource";
	private static final String PARENT_FOLDER_ATTR = "parentFolder";
	private RepositoryService repository;
	private String queryReferenceReqAttrName;
	private String queryReferenceOutpuAttrName;
	private MessageSource messageSource;

	/*
	 * method to get the reposervice object
	 * @return Returns an instance of the repository service injected by spring IOC
	 */
	public RepositoryService getRepository() {
		return repository;
	}

	/*
	 * method to set the reposervice object
	 * @return void
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public DefineInputControlsAction(){
		setFormObjectClass(InputControlWrapper.class); //custom form backing object class
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW); 		//this is a multi-page wizard!
	}

	/** Sets up the objects required by the flow
	 *  and initilises to any default values that we want
	 * @param context
	 * @return
	 */public Event initControlDefinition(RequestContext context) throws Exception
	{
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);
		context.getFlowScope().put(CONSTANTS_KEY, new JasperServerConstImpl());
		/*When in Stand alone new mode get a list of all
		 * previously existing resources to validate name uniqueness*/
		if(formObject.isAloneNewMode()){
			InputControl ic=formObject.getInputControl();
			FilterCriteria criteria = FilterCriteria.createFilter();
			criteria.addFilterElement(FilterCriteria.createParentFolderFilter(ic.getParentFolder()));
			ResourceLookup[] resources = repository.findResource(JasperServerUtil.getExecutionContext(context), criteria);
			List allResources=Arrays.asList(resources);
			formObject.setAllResources(allResources);
		}

		return success();
	}

	/** Gets a list of exisiting control types
	 * for the type selected by the user
	 * @param context
	 * @return
	 */public Event prepareForControlType(RequestContext context) throws Exception
	{
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);
		InputControl control = formObject.getInputControl();
		byte selectedType = control.getType();
		if(selectedType==0)
		{
			log("Selected type was null");
			selectedType=InputControl.TYPE_SINGLE_VALUE;
		}

		FilterCriteria criteria=null;
		if(selectedType==InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES
				|| selectedType==InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES
				|| selectedType == InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO
				|| selectedType == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX){
			criteria=FilterCriteria.createFilter(ListOfValues.class);
			if (control.getListOfValues() != null) {
				String uri = control.getListOfValues().getReferenceURI();
				if (uri != null) {
					formObject.setExistingPath(uri);
					formObject.setSource(JasperServerConstImpl.getFieldChoiceRepo());
				} else {
					formObject.setSource(JasperServerConstImpl.getFieldChoiceLocal());
				}
			}
			else {
				formObject.setSource(JasperServerConstImpl.getFieldChoiceLocal());
			}
		}
		if(selectedType==InputControl.TYPE_MULTI_SELECT_QUERY
				|| selectedType==InputControl.TYPE_SINGLE_SELECT_QUERY
				|| selectedType==InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO
				|| selectedType==InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX){
			criteria=FilterCriteria.createFilter(Query.class);
			if (control.getQuery() != null) {
				String uri = control.getQuery().getReferenceURI();
				if (uri != null) {
					formObject.setExistingPath(uri);
					formObject.setSource(JasperServerConstImpl.getFieldChoiceRepo());
				} else {
					formObject.setSource(JasperServerConstImpl.getFieldChoiceLocal());
				}
			}
			else {
				formObject.setSource(JasperServerConstImpl.getFieldChoiceLocal());
			}
		}
		if(selectedType==InputControl.TYPE_MULTI_VALUE ||
		   selectedType==InputControl.TYPE_SINGLE_VALUE){
			criteria=FilterCriteria.createFilter(DataType.class);
			if (control.getDataType() != null) {
				String uri = control.getDataType().getReferenceURI();
				if (uri != null) {
					formObject.setExistingPath(uri);
					formObject.setSource(JasperServerConstImpl.getFieldChoiceRepo());
				} else {
					formObject.setSource(JasperServerConstImpl.getFieldChoiceLocal());
				}
			}
			else {
				formObject.setSource(JasperServerConstImpl.getFieldChoiceLocal());
			}
		}
		if(selectedType==InputControl.TYPE_BOOLEAN){
			//Save the boolean control and do nothing else
			return success();
			}
		log("control type="+selectedType);
		List existingPathsList=new ArrayList();
		ResourceLookup[] lookups=repository.findResource(JasperServerUtil.getExecutionContext(context), criteria);
		if(lookups!=null){
			log("Found lookups size="+lookups.length);
			for(int i=0;i<lookups.length;i++){
				existingPathsList.add(lookups[i].getURIString());
				if(lookups[i]!=null)
					log("Found path="+lookups[i].getURIString());
				else
					log("Found null Lookup at "+i);
			}
		}
		formObject.setExistingPathList(existingPathsList);
//		context.getFlowScope().put(FORM_OBJECT_KEY, dto);
		return success();
	}

	/**Handles user's selection at the DataType control form
	 * @param context
	 * @return
	 */public Event handleDataTypeControl(RequestContext context) throws Exception
	{
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);
		if(formObject==null)
			return error();
		InputControl control=formObject.getInputControl();
		formObject.setSource(context.getRequestParameters().get("source"));

		if (formObject.getSource().equals(JasperServerConstImpl.getFieldChoiceLocal())) {
			DataTypeWrapper wrapper;
			DataType dt = null;
			if (control.getDataType() == null || control.getDataType().getLocalResource() == null) {
				dt = (DataType)repository.newResource(null, DataType.class);
				control.setDataType(dt);
				wrapper = new DataTypeWrapper(dt);
			} else {
				if (control.getDataType().isLocal()) {
					dt = (DataType) control.getDataType().getLocalResource();
					if (dt.getDecimals() != null)
						formObject.setDtDecimals(dt.getDecimals().toString());
					if (dt.getMaxLength() != null)
						formObject.setDtMaxLength(dt.getMaxLength().toString());
				}
				wrapper = new DataTypeWrapper((DataType) control.getDataType().getLocalResource());
				if(dt != null){
					byte type = dt.getType();
					if(type == DataType.TYPE_DATE || type == DataType.TYPE_DATE_TIME){
						DateFormat df = getFormat(type);				
						if ((dt.getMinValue() != null) && (!dt.getMinValue().equals(""))) {
							wrapper.setMinValueText(df.format((Date)dt.getMinValue()));
						}
						if ((dt.getMaxValue() != null) && (!dt.getMaxValue().equals(""))){
							wrapper.setMaxValueText(df.format((Date)dt.getMaxValue()));
						}
					}
				}
				
			}
			wrapper.setMode(wrapper.getDataType().isNew() ? BaseDTO.MODE_SUB_FLOW_NEW : BaseDTO.MODE_SUB_FLOW_EDIT);
			context.getFlowScope().put(DATATYPE_KEY, wrapper);
		} else {
			String path = context.getRequestParameters().get("existingPath");
			control.setDataTypeReference(path);
			return yes();
		}
		return success();
	}

	/**Handles user's selection at the List of values control form
	 * @param context
	 * @return
	 */public Event handleLovControl(RequestContext context) throws Exception
	{
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);
		if(formObject==null)
			return error();

		InputControl control = formObject.getInputControl();
		formObject.setSource(context.getRequestParameters().get("source"));

		if (formObject.getSource().equals(JasperServerConstImpl.getFieldChoiceLocal())) {
			ListOfValuesDTO listOfValues;
			if (control.getListOfValues() == null || control.getListOfValues().getLocalResource() == null) {
				ListOfValues lov= (ListOfValues)repository.newResource(null, ListOfValues.class);
				control.setListOfValues(lov);
				listOfValues = new ListOfValuesDTO(lov);
				listOfValues.setMode(BaseDTO.MODE_SUB_FLOW_NEW);
			}
			else {
				listOfValues = new ListOfValuesDTO((ListOfValues) control.getListOfValues().getLocalResource());
				listOfValues.setMode(BaseDTO.MODE_SUB_FLOW_EDIT);
			}
			context.getFlowScope().put(LOV_KEY, listOfValues);
		} else {
			String path = context.getRequestParameters().get("existingPath");
			control.setListOfValuesReference(path);
			return yes();
		}

		return success();
	}


	public Event saveInputControl(RequestContext context) throws Exception
	{
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);
		InputControl control = formObject.getInputControl();
		if (control.getType() ==  InputControl.TYPE_SINGLE_VALUE ||
			control.getType() ==  InputControl.TYPE_MULTI_VALUE) {
			DataType dataType = (DataType) control.getDataType().getLocalResource();
			if (formObject.getDtMaxLength() != null && formObject.getDtMaxLength().length() > 0)
				dataType.setMaxLength(new Integer(formObject.getDtMaxLength()));
			if (formObject.getDtDecimals() != null && formObject.getDtDecimals().length() > 0)
				dataType.setDecimals(new Integer(formObject.getDtDecimals()));
		}
		if (formObject.isStandAloneMode()) {
			try {
			   repository.saveResource(null, control);
			   return yes();
			}
			catch (JSDuplicateResourceException e) {
				getFormErrors(context).rejectValue("inputControl.name", "InputControlsFlowValidator.error.already.exists");
				return error();
			}
		}

		return success();
	}


	public Object createFormObject(RequestContext context)
	{
		InputControlWrapper formObject = null;
		String resourceUri = context.getRequestParameters().get(INPUTCONTROLURI_PARAM);
		if( resourceUri!=null && resourceUri.trim().length()!=0){
			InputControl resource=(InputControl)repository.getResource(null,resourceUri);
			if(resource==null)
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {resourceUri});
			log("Found resource with uri="+resourceUri);
			formObject = new InputControlWrapper(resource);
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
		}
		if (formObject == null) {
			InputControl inputControl = (InputControl) repository.newResource(null, InputControl.class);
			String parentFolder = (String) context.getFlowScope().get(PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
				parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);		
			}
			inputControl.setParentFolder(parentFolder);
			formObject=new InputControlWrapper(inputControl);
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
		}
		InputControl control=formObject.getInputControl();
		if(control.getParentFolder()==null)
			control.setParentFolder("/");
		formObject.setSource(JasperServerConstImpl.getFieldChoiceRepo());
		return formObject;
	}


	public Event addVisibleColumn(RequestContext context) throws Exception
	{
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);

		formObject.getInputControl().addQueryVisibleColumn(formObject.getNewVisibleColumn());

		return success();
	}


	public Event removeVisibleColumn(RequestContext context) throws Exception
	{
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);

		if (context.getRequestParameters().contains("itemToDelete")) {
			String[] selectedColumns = context.getRequestParameters().getArray("itemToDelete");

			for (int i = 0; i < selectedColumns.length; i++)
				formObject.getInputControl().removeQueryVisibleColumn(selectedColumns[i]);
		}

		return success();
	}

	/** Helper method to help change the logging level in the code
	 * back to info once, debug is complete
	 * @param text The text to be logged
	 */private void log(String text){
		log.debug(text);
	}

	public static String getFORM_OBJECT_KEY() {
		return FORM_OBJECT_KEY;
	}

	public Event prepareQuery(RequestContext context) throws Exception {
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);
		ResourceReference queryRef = formObject.getInputControl().getQuery();
		ResourceReferenceDTO queryRefDTO = new ResourceReferenceDTO(queryRef);
		context.getRequestScope().put(getQueryReferenceReqAttrName(), queryRefDTO);
		return success();
	}

	public Event setQueryReference(RequestContext context) throws Exception {
		ResourceReferenceDTO queryRef = (ResourceReferenceDTO) context.getFlowScope().getRequired(getQueryReferenceOutpuAttrName(), ResourceReferenceDTO.class);
		InputControlWrapper formObject = (InputControlWrapper) getFormObject(context);
		formObject.getInputControl().setQuery(queryRef.toResourceReference());
		return success();
	}

	/**
	 * @return Returns the queryReferenceReqAttrName.
	 */
	public String getQueryReferenceReqAttrName() {
		return queryReferenceReqAttrName;
	}

	/**
	 * @param queryReferenceReqAttrName The queryReferenceReqAttrName to set.
	 */
	public void setQueryReferenceReqAttrName(String queryReferenceReqAttrName) {
		this.queryReferenceReqAttrName = queryReferenceReqAttrName;
	}

	/**
	 * @return Returns the queryReferenceOutpuAttrName.
	 */
	public String getQueryReferenceOutpuAttrName() {
		return queryReferenceOutpuAttrName;
	}

	/**
	 * @param queryReferenceOutpuAttrName The queryReferenceOutpuAttrName to set.
	 */
	public void setQueryReferenceOutpuAttrName(
			String queryReferenceOutpuAttrName) {
		this.queryReferenceOutpuAttrName = queryReferenceOutpuAttrName;
	}
	private DateFormat getFormat(byte type)
	{
		if(type == DataType.TYPE_DATE){
			return JasperServerUtil.createCalendarDateFormat(messageSource);
		}
		return JasperServerUtil.createCalendarDateTimeFormat(messageSource);
	}

	/**
	 * @return Returns the messageSource.
	 */
	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * @param messageSource The messageSource to set.
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
