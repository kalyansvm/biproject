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
import java.util.Date;

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
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.DataTypeWrapper;
import com.jaspersoft.jasperserver.war.util.CalendarFormatProvider;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: EditDataTypeAction.java 11513 2008-01-02 19:11:53Z achan $
 */
public class EditDataTypeAction extends FormAction
{
	private static final String FORM_OBJECT_KEY = "dataType";
	private static final String PARENT_FOLDER_ATTR = "parentFolder";
	private static final String CURRENT_DATATYPE_ATTR = "currentDataType";
	private static final String IS_EDIT = "isEdit";//FIXME use wrapper to disable name in UI

	private RepositoryService repository;
	private CalendarFormatProvider calendarFormatProvider;

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
	public EditDataTypeAction(){
		setFormObjectClass(DataTypeWrapper.class); //custom form backing object class
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW); 		//this is a multi-page wizard!
	}


	/**
	 *
	 */
	public Object createFormObject(RequestContext context)
	{
		DataType dataType;
		DataTypeWrapper wrapper;
		ExecutionContextImpl executionContext = new ExecutionContextImpl();

		String isEdit = (String)context.getFlowScope().get(IS_EDIT);
		if (isEdit == null) {
			isEdit = (String)context.getRequestParameters().get("isEdit");
			context.getFlowScope().put(IS_EDIT, isEdit);
		}
		
		if (isEdit != null)
		{
			String currentDataType = (String) context.getFlowScope().get(CURRENT_DATATYPE_ATTR);
			if (currentDataType == null) {
				currentDataType = (String)context.getRequestParameters().get("resource");
				context.getFlowScope().put(CURRENT_DATATYPE_ATTR, currentDataType);
			}
			dataType = (DataType) repository.getResource(executionContext, currentDataType);
			if(dataType == null){
				context.getFlowScope().remove("prevForm");
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {currentDataType});
			}
			wrapper = new DataTypeWrapper(dataType);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
			byte type = dataType.getType();
			if(type == DataType.TYPE_DATE || type == DataType.TYPE_DATE_TIME){
				DateFormat df = getFormat(type);
				if(dataType.getMinValue() != null){
					wrapper.setMinValueText(df.format((Date)dataType.getMinValue()));
				}
				if(dataType.getMaxValue() != null){
					wrapper.setMaxValueText(df.format((Date)dataType.getMaxValue()));
				}
			}
		}
		else
		{
			dataType = (DataType) repository.newResource(executionContext, DataType.class);
			String parentFolder = (String) context.getFlowScope().get(PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
				parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
			}
			if (parentFolder == null || parentFolder.trim().length() == 0)
				parentFolder = "/";
			dataType.setParentFolder(parentFolder);
			wrapper = new DataTypeWrapper(dataType);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_NEW);

			FilterCriteria criteria = FilterCriteria.createFilter();
			criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentFolder));
			ResourceLookup[] allDataTypes = repository.findResource(executionContext, criteria);
			wrapper.setAllDataTypes(allDataTypes);
		}

		return wrapper;
	}


	/**
	 *
	 */
	public Event saveDataType(RequestContext context) throws Exception
	{
		DataTypeWrapper wrapper = (DataTypeWrapper) getFormObject(context);
		DataType dataType = wrapper.getDataType();
		String minValue;
		String maxValue;
		DateFormat dateFormat;
		switch (dataType.getType()) {
			case DataType.TYPE_DATE:
				minValue = wrapper.getMinValueText();
				maxValue = wrapper.getMaxValueText();
				dateFormat = getCalendarFormatProvider().getDateFormat();
				if (minValue != null && minValue.length() > 0)
					dataType.setMinValue(dateFormat.parse(minValue));
				if (maxValue != null && maxValue.length() > 0)
					dataType.setMaxValue(dateFormat.parse(maxValue));
				break;
			case DataType.TYPE_DATE_TIME:
				minValue = wrapper.getMinValueText();
				maxValue = wrapper.getMaxValueText();
				dateFormat = getCalendarFormatProvider().getDatetimeFormat();
				if (minValue != null && minValue.length() > 0)
					dataType.setMinValue(dateFormat.parse(minValue));
				if (maxValue != null && maxValue.length() > 0)
					dataType.setMaxValue(dateFormat.parse(maxValue));
				break;
		}
		if (wrapper.isStandAloneMode())
			try {
				repository.saveResource(null, wrapper.getDataType());
				return yes();
			}
			catch (JSDuplicateResourceException e) {
				getFormErrors(context).rejectValue("dataType.name", "DataTypeValidator.error.duplicate");
				return error();
			}

		return success();
	}

	/**
	 *
	 */
	public Event setupEditForm(RequestContext context) throws Exception
	{
		MutableAttributeMap rs = context.getRequestScope();
		rs.put(FORM_OBJECT_KEY, getFormObject(context));

		rs.put("calendarDatePattern", getCalendarFormatProvider().getCalendarDatePattern());
		rs.put("calendarDatetimePattern", getCalendarFormatProvider().getCalendarDatetimePattern());
		return success();
	}

	public CalendarFormatProvider getCalendarFormatProvider()
	{
		return calendarFormatProvider;
	}

	public void setCalendarFormatProvider(CalendarFormatProvider calendarFormatProvider)
	{
		this.calendarFormatProvider = calendarFormatProvider;
	}
	
	private DateFormat getFormat(byte type)
	{
		if(type == DataType.TYPE_DATE){
			return getCalendarFormatProvider().getDateFormat();
		}
		return getCalendarFormatProvider().getDatetimeFormat();
	}
}

