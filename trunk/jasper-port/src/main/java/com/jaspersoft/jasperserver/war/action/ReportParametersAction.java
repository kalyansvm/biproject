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


import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlsInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.RuntimeInputControlWrapper;
import com.jaspersoft.jasperserver.war.util.CalendarFormatProvider;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: ReportParametersAction.java 13503 2008-05-12 09:18:23Z lucian $
 */
public abstract class ReportParametersAction extends FormAction implements ReportInputControlsAction
{

	private static final Log log = LogFactory.getLog(ReportParametersAction.class);

	public static final String INPUTWRAPPERS_ATTR = "wrappers";

	private static final String COLUMN_VALUE_SEPARATOR = " | ";
	private static final int COLUMN_VALUE_SEPARATOR_LENGTH = COLUMN_VALUE_SEPARATOR.length();

	private String reportUnitAttrName;
	private String reportUnitObjectAttrName;
	private String controlsDisplayFormAttrName;
	private String controlsDisplayViewAttrName;
	private String defaultTopControlsView;
	private String reportDisplayFormAttrName;
	private String calendarDatePatternAttrName;
	private String calendarDatetimePatternAttrName;
	private EngineService engine;
	private RepositoryService repository;
	private MessageSource messages;
	private String hasInputControlsAttrName;
	private String staticDatePattern;
	private CalendarFormatProvider calendarFormatProvider;
	private String inputNamePrefix;
	private String attributeInputControlsInformation;
	private String markerParameterPrefix;

	protected Event createWrappers(RequestContext context)
	{
		ReportUnit reportUnit = loadReportUnit(context);

		if (reportUnit == null) {
			throw new JSException("jsexception.view.report.could.not.load");
		}

		context.getFlowScope().put(getReportUnitObjectAttrName(), reportUnit);

		setReportUnitAttributes(context, reportUnit);

		context.getFlowScope().put(getCalendarDatePatternAttrName(), getCalendarDatePattern());
		context.getFlowScope().put(getCalendarDatetimePatternAttrName(), getCalendarDatetimePattern());

		List wrappers = createWrappers(context, reportUnit);
		context.getFlowScope().put(INPUTWRAPPERS_ATTR, wrappers);

		context.getFlowScope().put(getHasInputControlsAttrName(), Boolean.valueOf(!wrappers.isEmpty()));

		boolean needsInput = needsInput(context, wrappers);
		return needsInput ? yes() : no();
	}

	protected void setReportUnitAttributes(RequestContext context, ReportUnit reportUnit) {
		MutableAttributeMap flowScope = context.getFlowScope();
		String controlsView = reportUnit.getInputControlRenderingView();
		if ((controlsView == null || controlsView.length() == 0)
				&& reportUnit.getControlsLayout() == ReportUnit.LAYOUT_TOP_OF_PAGE) {
			controlsView = getDefaultTopControlsView();
		}
		if (controlsView != null && controlsView.endsWith(".jsp")) {
			String controlsFlowView = controlsView.substring(0, controlsView.length() - ".jsp".length());
			flowScope.put(getControlsDisplayViewAttrName(), controlsFlowView);
		}
		flowScope.put(getControlsDisplayFormAttrName(), controlsView);
		flowScope.put(getReportDisplayFormAttrName(), reportUnit.getReportRenderingView());
	}

	public String getReportURI(RequestContext context) {
		String reportURI = context.getFlowScope().getString(getReportUnitAttrName());
		return reportURI;
	}

	protected ReportUnit loadReportUnit(RequestContext context)
	{
		String reportUnitUri = (String) context.getFlowScope().get(getReportUnitAttrName());

		if (reportUnitUri == null) {
			reportUnitUri = context.getRequestParameters().get(getReportUnitAttrName());
			if (reportUnitUri != null) {
				context.getFlowScope().put(getReportUnitAttrName(), reportUnitUri);
			}
		}

		ReportUnit reportUnit;
		if (reportUnitUri == null || reportUnitUri.trim().length() == 0) {
			reportUnit = null;
		} else {
			reportUnit = (ReportUnit) repository.getResource(null, reportUnitUri);
		}

		return reportUnit;
	}

	protected boolean needsInput(RequestContext context, List wrappers)
	{
		return !wrappers.isEmpty();
	}

	public Map getReportParameters(RequestContext context) {
		return getParameterValues(context, true);
	}

	protected Map getParameterValues(RequestContext context, boolean requestParsed)
	{
		List wrappers = getInputControlWrappers(context);
		boolean valid = requestParsed || parseRequest(context, wrappers, true);

		Map parameterValues;
		if (valid || wrappers.size() == 0) {
			String reportUnitUri = getReportURI(context);
			parameterValues = bindParameterValues(reportUnitUri, wrappers);
			addCustomParameters(context, parameterValues);
		} else {
			parameterValues = null;
		}

		return parameterValues;
	}

	public List getInputControlWrappers(RequestContext context)
	{
		List wrappers = (List) context.getFlowScope().get(INPUTWRAPPERS_ATTR);
		return wrappers;
	}

	protected boolean hasInputs(RequestContext context)
	{
		List wrappers = getInputControlWrappers(context);
		return wrappers != null && !wrappers.isEmpty();
	}

	public void setParameterValues(RequestContext context, Map values) {
		List inputControls = getInputControlWrappers(context);
		MapValueProvider valueProvider = new MapValueProvider(values, defaultValuesProvider(context));
		setInputParameterValues(inputControls, valueProvider);
	}

	public void resetValuesToDefaults(RequestContext context) {
		List inputControls = getInputControlWrappers(context);
		setInputParameterValues(inputControls, defaultValuesProvider(context));
	}

	public static int getDSTSavings(TimeZone tz) {

		if (tz.useDaylightTime()) {
			return 3600000;
		}
		return 0;
	}

	protected void addCustomParameters(RequestContext context, Map parameterValues) {
		//nothing
	}


	protected InputValueProvider initialValueProvider(RequestContext context) {
		return defaultValuesProvider(context);
	}

	protected static abstract class InputValueProvider {
		private final InputValueProvider parent;

		protected InputValueProvider() {
			this(null);
		}

		protected InputValueProvider(InputValueProvider parent) {
			this.parent = parent;
		}

		public Object getValue(String inputName) {
			Object value;
			if (hasOwnValue(inputName)) {
				value = getOwnValue(inputName);
			} else if (parent != null) {
				value = parent.getValue(inputName);
			} else {
				value = null;
			}
			return value;
		}

		protected abstract boolean hasOwnValue(String inputName);

		protected abstract Object getOwnValue(String inputName);
	}

	protected static class MapValueProvider extends InputValueProvider {
		private final Map values;

		public MapValueProvider(Map values) {
			this.values = values;
		}

		public MapValueProvider(Map values, InputValueProvider parent) {
			super(parent);
			this.values = values;
		}

		protected boolean hasOwnValue(String inputName) {
			return values.containsKey(inputName);
		}

		protected Object getOwnValue(String inputName) {
			return values.get(inputName);
		}
	}

	protected InputValueProvider defaultValuesProvider(RequestContext context) {
		ReportInputControlsInformation controlsInfo = getControlsInformation(context);
		Map defaults = new HashMap();
		for (Iterator it = controlsInfo.getControlNames().iterator(); it.hasNext();) {
			String name = (String) it.next();
			ReportInputControlInformation info = controlsInfo.getInputControlInformation(name);
			if (info != null) {
				defaults.put(name, info.getDefaultValue());
			}
		}
		return new MapValueProvider(defaults);
	}

	protected ReportInputControlsInformation getControlsInformation(
			RequestContext context) {
		return (ReportInputControlsInformation) 
				context.getFlowScope().getRequired(getAttributeInputControlsInformation(), 
						ReportInputControlsInformation.class);
	}

	protected void setInputParameterValues(List wrappers, InputValueProvider valueProvider) {
		for (Iterator it = wrappers.iterator(); it.hasNext();) {
			RuntimeInputControlWrapper inputControl = (RuntimeInputControlWrapper) it.next();
			String inputName = inputControl.getInputControl().getName();
			Object paramValue = valueProvider.getValue(inputName);
			setInputControlParameterValue(inputControl, paramValue);
		}
	}

	protected void setInputControlParameterValue(RuntimeInputControlWrapper inputControl, Object paramValue) {
		Object inputValue = toInputControlValue(inputControl, paramValue);
		inputControl.setValue(inputValue);
	}

	protected Object toInputControlValue(RuntimeInputControlWrapper inputControl, Object paramValue) {
		if (paramValue == null) {
			return null;
		}

		Object value;
		if (inputControl.isMulti()) {
			Set values = toMultiInputControlValue(paramValue);
			value = values;
		} else {
			DataType datatype = getDatatype(inputControl.getInputControl());
			if (datatype != null) {
				switch (datatype.getType()) {
				case DataType.TYPE_NUMBER:
					value = toInputControlNumber(paramValue);
					break;
				case DataType.TYPE_TEXT:
					value = paramValue.toString();
					break;
				case DataType.TYPE_DATE:
				case DataType.TYPE_DATE_TIME:
					value = toInputControlDate(paramValue);
					break;
				default:
					value = paramValue;
					break;
				}
			} else {
				value = paramValue;
			}
		}
		return value;
	}

	protected Object toInputControlNumber(Object paramValue) {
		BigDecimal inputValue;
		if (paramValue instanceof Byte
				|| paramValue instanceof Short
				|| paramValue instanceof Integer
				|| paramValue instanceof Long) {
			inputValue = BigDecimal.valueOf(((Number) paramValue).longValue());
		} else if (paramValue instanceof Float
				|| paramValue instanceof Double) {
			inputValue = BigDecimal.valueOf(((Number) paramValue).doubleValue());
		} else if (paramValue instanceof BigDecimal) {
			inputValue = (BigDecimal) paramValue;
		} else if (paramValue instanceof BigInteger) {
			inputValue = new BigDecimal((BigInteger) paramValue);
		} else if (paramValue instanceof String) {
			inputValue = new BigDecimal((String) paramValue);
		} else {
			throw new JSException("exception.report.unrecognized.number.type", new Object[]{paramValue.getClass().getName()});
		}
		return inputValue;
	}

	protected Object toInputControlDate(Object paramValue) {
		Date inputValue;
		if (paramValue instanceof Date) {
			Date dateValue = (Date) paramValue;
			inputValue = new Date(dateValue.getTime());
		} else {
			throw new JSException("exception.report.unrecognized.date.type", new Object[]{paramValue.getClass().getName()});
		}
		return inputValue;
	}
	
	protected Set toMultiInputControlValue(Object paramValue) {
		Set values = new ListOrderedSet();
		if (paramValue != null) {
			if (paramValue instanceof Collection) {
				values.addAll((Collection) paramValue);
			} else if (paramValue.getClass().isArray()) {
				int length = Array.getLength(paramValue);
				for (int idx = 0; idx < length; ++idx) {
					Object val = Array.get(paramValue, idx);
					values.add(val);
				}
			}
		}
		return values;
	}

	protected ReportInputControlsInformation loadInputControlsInformation(RequestContext context, ReportUnit report) {
		String reportURI = report.getURIString();
		Map params = new HashMap();
		addCustomParameters(context, params);
		return getEngine().getReportInputControlsInformation(
				JasperServerUtil.getExecutionContext(context), reportURI, params);
	}

	protected List createWrappers(RequestContext context, ReportUnit reportUnit) {
		return createWrappers(JasperServerUtil.getExecutionContext(context), context, reportUnit);
    }

	/**
     * New signature takes ExecutionContext because we may not be coming from webflow
	 * @param exContext
	 * @param context
	 * @param reportUnit
	 * @return
	 */
	protected List createWrappers(ExecutionContext exContext, RequestContext context, ReportUnit reportUnit)
	{
		List wrappers = new ArrayList();

		//TODO use other repository control that brings the required data
		List inputControls = reportUnit.getInputControls();
		if (!inputControls.isEmpty())
		{
			for (int i = 0; i < inputControls.size(); i++)
			{
				ResourceReference inputControlRef = (ResourceReference) inputControls.get(i);
				InputControl control;
				if (inputControlRef.isLocal()) {
					control = (InputControl) inputControlRef.getLocalResource();
				} else {
					control = (InputControl) repository.getResource(new ExecutionContextImpl(), inputControlRef.getReferenceURI());
				}

				Format format = null;
				ResourceReference dataTypeRef = control.getDataType();
				if (dataTypeRef != null) {
					DataType dataType;
					if (!dataTypeRef.isLocal()) {
						dataType = (DataType) repository.getResource(new ExecutionContextImpl(), dataTypeRef.getReferenceURI());
						control.setDataType(dataType);
					}
					else
						dataType = (DataType) dataTypeRef.getLocalResource();

					switch (dataType.getType()) {
						case DataType.TYPE_DATE:
							format = getDateFormat(true);
							break;
						case DataType.TYPE_DATE_TIME:
							format = getDatetimeFormat(true);
							((DateFormat)format).setTimeZone(JasperServerUtil.getTimezone(exContext));
							break;
						case DataType.TYPE_NUMBER:
							if (dataType.getRegularExpr() != null && dataType.getRegularExpr().length() > 0)
								format = new DecimalFormat(dataType.getRegularExpr());
					}
				}
				ResourceReference listOfValuesRef = control.getListOfValues();
				if (listOfValuesRef != null && !listOfValuesRef.isLocal()) {
					ListOfValues listOfValues = (ListOfValues) repository.getResource(new ExecutionContextImpl(), listOfValuesRef.getReferenceURI());
					control.setListOfValues(listOfValues);
				}

				RuntimeInputControlWrapper wrapper = new RuntimeInputControlWrapper(control);
				wrapper.setFormat(format);

				ResourceReference queryRef = control.getQuery();
				if (queryRef != null) {
					OrderedMap results = executeQuery(control.getQuery(), reportUnit.getDataSource(), wrapper);
					wrapper.setQueryResults(results);
				}

				wrappers.add(wrapper);
			}
		}

        //webflow requests only
		if ((context != null) && !wrappers.isEmpty()) {
			ReportInputControlsInformation controlsInfo = loadInputControlsInformation(context, reportUnit);
			context.getFlowScope().put(getAttributeInputControlsInformation(), controlsInfo);
			
			for (Iterator it = wrappers.iterator(); it.hasNext();) {
				RuntimeInputControlWrapper control = (RuntimeInputControlWrapper) it.next();
				ReportInputControlInformation controlInfo = 
					controlsInfo.getInputControlInformation(control.getInputControl().getName());
				control.setControlInfo(controlInfo);
			}
			
            InputValueProvider valueProvider = initialValueProvider(context);
            setInputParameterValues(wrappers, valueProvider);
		}

		return wrappers;
	}


	/**
	 *
	 */
	protected OrderedMap executeQuery(ResourceReference queryReference, ResourceReference dataSourceReference, RuntimeInputControlWrapper wrapper)
	{
		InputControl control = wrapper.getInputControl();
		String valueColumn = control.getQueryValueColumn();
		String[] visibleColumns = control.getQueryVisibleColumns();

		OrderedMap results = engine.executeQuery(null, queryReference, valueColumn, visibleColumns, dataSourceReference);

		if (results == null) {
			return null;
		}

		OrderedMap inputData = new LinkedMap();
		for (Iterator it = results.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Object keyValue = entry.getKey();
			String[] columnValues = (String[]) entry.getValue();

			String columnValuesString = "";
			if (columnValues != null && columnValues.length > 0) {
				StringBuffer visibleColumnBuffer = new StringBuffer();
				for (int i = 0; i < columnValues.length; i++) {
					visibleColumnBuffer.append(COLUMN_VALUE_SEPARATOR);
					visibleColumnBuffer.append(columnValues[i] != null ? columnValues[i] : "");
				}
				columnValuesString = visibleColumnBuffer.substring(COLUMN_VALUE_SEPARATOR_LENGTH);
			}

			inputData.put( (keyValue == null ? null : keyValue.toString()), new Object[] {keyValue, columnValuesString});
		}

		return inputData;
	}

	public Event setInputValues(RequestContext context)
	{
		List wrappers = getInputControlWrappers(context);
		boolean valid = parseRequest(context, wrappers, true);

		if (log.isDebugEnabled())
		{
			log.debug("Input values valid: " + valid);
		}

		return success();
	}

	/**
	 * @deprecated Use parseRequest(Map requestParameters, List wrappers, boolean interactiveParameters)
	 * to allow wider usage
	 */
	protected boolean parseRequest(RequestContext context, List wrappers, boolean interactiveParameters) {
        ServletRequest request = JasperServerUtil.getServletRequestFromRequestContext(context);
        if (request != null) {
            return parseRequest(request.getParameterMap(), wrappers, interactiveParameters);
        } else {
            return false;
        }
	}


	protected boolean parseRequest(Map requestParameters, List wrappers, boolean interactiveParameters)
	{
		boolean isValid = true;

		for (int i = 0; i < wrappers.size(); i++)
		{
			RuntimeInputControlWrapper wrapper = (RuntimeInputControlWrapper) wrappers.get(i);
			boolean parsed = parseRequestInput(requestParameters, interactiveParameters, wrapper);
			boolean valueValid = parsed && validateValue(wrapper, interactiveParameters);
			isValid = isValid && valueValid;
		}

		return isValid;
	}

	/**
	 * @deprecated Use parseRequestInput(Map requestParameters, boolean interactiveParameters, RuntimeInputControlWrapper wrapper)
	 * to allow wider usage
	 */
	protected boolean parseRequestInput(RequestContext context, boolean interactiveParameters, RuntimeInputControlWrapper wrapper) {
        ServletRequest request = JasperServerUtil.getServletRequestFromRequestContext(context);
        if (request != null) {
            return parseRequestInput(request.getParameterMap(), interactiveParameters, wrapper);
        } else {
            return false;
        }
	}

	protected boolean parseRequestInput(Map requestParameters, boolean interactiveParameters, RuntimeInputControlWrapper wrapper) {
		wrapper.setErrorMessage(null);

		boolean parsed;
		if (wrapper.isMulti()) {
			parsed = parseRequestValues(requestParameters, wrapper, interactiveParameters);
		} else {
			parsed = parseRequestValue(requestParameters, wrapper, interactiveParameters);
		}
		return parsed;
	}

	protected String getParameterName(RuntimeInputControlWrapper wrapper)
	{
		InputControl control = wrapper.getInputControl();
		String paramName;
		if (getInputNamePrefix() == null)
		{
			paramName = control.getName();
		}
		else
		{
			paramName = getInputNamePrefix() + control.getName();
		}
		return paramName;
	}

	/**
	 * @deprecated Use parseRequestValue(Map requestParameters, RuntimeInputControlWrapper wrapper, boolean interactiveParameters)
	 * to allow wider usage
	 */
	protected boolean parseRequestValue(RequestContext context, RuntimeInputControlWrapper wrapper, boolean interactiveParameters)
	{
        ServletRequest request = JasperServerUtil.getServletRequestFromRequestContext(context);
        if (request != null) {
            return parseRequestValue(request.getParameterMap(), wrapper, interactiveParameters);
        } else {
            return false;
        }
	}

	protected boolean parseRequestValue(Map requestParameters, RuntimeInputControlWrapper wrapper, boolean interactiveParameters)
	{
		wrapper.setErrorMessage(null);//resetting error message

		String parameterName = getParameterName(wrapper);

		if (!interactiveParameters && !requestParameters.containsKey(parameterName))
		{
			return true;
		}

 		String[] strValues = ((String[])requestParameters.get(parameterName));
        String strValue;
        if (strValues==null) {
            strValue = null; 
        } else {
            strValue = strValues[0]; 
        }
            

		InputControl control = wrapper.getInputControl();
		DataType dataType = getDatatype(control);

		if (control.getType() == InputControl.TYPE_BOOLEAN)
		{
			if (interactiveParameters)
			{
				wrapper.setValue(Boolean.valueOf(strValue != null));
			}
			else
			{
				wrapper.setValue(Boolean.valueOf(strValue == null || !strValue.equals("false")));
			}
			return true;
		}


		if (strValue != null && strValue.trim().length() == 0)
		{
			strValue = null;
		}

		if (control.getType() == InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES
				|| control.getType() == InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO) {
			wrapper.setValue(getLovValue(wrapper, strValue));
			return true;
		}

		if (control.getType() == InputControl.TYPE_SINGLE_SELECT_QUERY
				|| control.getType() == InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO) {
			wrapper.setValue(getQueryValue(wrapper, strValue));
			return true;
		}

		if (dataType == null || strValue == null)
		{
			wrapper.setValue(null);
			return true;
		}

		switch(dataType.getType())
		{
			case DataType.TYPE_TEXT :
			{
				wrapper.setValue(strValue);
				break;
			}
			case DataType.TYPE_NUMBER :
			{
				//FIXME take care of input mask
				try
				{
					wrapper.setValue(new BigDecimal(strValue));
				}
				catch(NumberFormatException e)
				{
					wrapper.setErrorMessage(messages.getMessage("fillParameters.error.invalidFloat", null, LocaleContextHolder.getLocale()));
					wrapper.setValue(strValue);
				}
				break;
			}
			case DataType.TYPE_DATE :
			{
				DateFormat format = getDateFormat(interactiveParameters);

				try
				{
					wrapper.setValue(format.parse(strValue));
				}
				catch (ParseException e)
				{
					wrapper.setErrorMessage(messages.getMessage("fillParameters.error.invalidDate", null, LocaleContextHolder.getLocale()));
					wrapper.setValue(strValue);
				}
				break;
			}
			case DataType.TYPE_DATE_TIME :
			{
				DateFormat format = getDatetimeFormat(interactiveParameters);
				format.setTimeZone(((DateFormat) wrapper.getFormat()).getTimeZone());
				try
				{
					wrapper.setValue(format.parse(strValue));
				}
				catch (ParseException e)
				{
					wrapper.setErrorMessage(messages.getMessage("fillParameters.error.invalidDateTime", null, LocaleContextHolder.getLocale()));
					wrapper.setValue(strValue);
				}
				break;
			}
		}

		return wrapper.getErrorMessage() == null;
	}

	/**
	 * @deprecated Use parseRequestValues(Map requestParameters, RuntimeInputControlWrapper wrapper, boolean interactiveParameters)
	 * to allow wider usage
	 */
	protected boolean parseRequestValues(RequestContext context, RuntimeInputControlWrapper wrapper, boolean interactiveParameters) {
        ServletRequest request = JasperServerUtil.getServletRequestFromRequestContext(context);
        if (request != null) {
            return parseRequestValues(request.getParameterMap(), wrapper, interactiveParameters);
        } else {
            return false;
        }
	}

	protected boolean parseRequestValues(Map requestParameters, RuntimeInputControlWrapper wrapper, boolean interactiveParameters) {
		InputControl control = wrapper.getInputControl();

		String parameterName = getParameterName(wrapper);

		if (!interactiveParameters)
		{
			//for direct URL, if no value on the request and no marker parameter found, don't set any value
			String markerParamName = getMarkerParameterPrefix() + parameterName;
			if (!requestParameters.containsKey(parameterName) && !requestParameters.containsKey(markerParamName))
			{
				return true;
			}
		}

        String[] strValues = (String[])requestParameters.get(parameterName);
        if (strValues == null) {
            strValues = new String[0];
        }           
        
		if (control.getType() == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES
			|| control.getType() == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX) {
			Set values = new ListOrderedSet();
			for (int i = 0; i < strValues.length; i++) {
				values.add(getLovValue(wrapper, strValues[i]));
			}
			wrapper.setValue(values);
			return true;
		}

		if (control.getType() == InputControl.TYPE_MULTI_SELECT_QUERY
			|| control.getType() == InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX) {
			Set values = new ListOrderedSet();
			for (int i = 0; i < strValues.length; i++) {
				values.add(getQueryValue(wrapper, strValues[i]));
			}
			wrapper.setValue(values);
			return true;
		}

		return true;
	}

	protected boolean validateValues(List wrappers, boolean setMissingMessage) {
		boolean valid = true;
		for (Iterator it = wrappers.iterator(); it.hasNext();) {
			RuntimeInputControlWrapper inputWrapper = (RuntimeInputControlWrapper) it.next();
			valid &= validateValue(inputWrapper, setMissingMessage);
		}
		return valid;
	}

	protected boolean validateValue(RuntimeInputControlWrapper wrapper, boolean setMissingMessage) {
		Object value = wrapper.getValue();

		boolean empty;
		if (wrapper.isMulti()) {
			empty = value == null || ((Collection) value).isEmpty();
		} else {
			empty = value == null;
		}

		if (empty) {
			if (wrapper.getInputControl().isMandatory()) {
				if (setMissingMessage) {
					wrapper.setErrorMessage(messages.getMessage("fillParameters.error.mandatoryField", null, LocaleContextHolder.getLocale()));
				}
				return false;
			}

			return true;
		}

		DataType dataType = getDatatype(wrapper.getInputControl());
		if (dataType == null) {
			return true;
		}

		if (dataType.getType() == DataType.TYPE_TEXT) {
			String strValue = (String) value;
			if (dataType.getMaxLength() != null
					&& dataType.getMaxLength().intValue() < strValue.length()) {
				wrapper.setErrorMessage(messages.getMessage("fillParameters.error.invalidType", null, LocaleContextHolder.getLocale()));
				return false;
			} else if (dataType.getRegularExpr() != null
					&& dataType.getRegularExpr().trim().length() > 0
					&& !Pattern.matches(dataType.getRegularExpr(), strValue)) {
				wrapper.setErrorMessage(messages.getMessage("fillParameters.error.invalidPattern", null, LocaleContextHolder.getLocale()));
				return false;
			}
		}

		Comparable compValue = (Comparable) value;

		final Comparable minValue = realDatatypeValue(dataType, dataType.getMinValue());
		if (minValue != null) {
			if (dataType.isStrictMin()) {
				if (minValue.compareTo(compValue) >= 0) {
					wrapper.setErrorMessage(messages.getMessage(
							"fillParameters.error.smallerThan", null, LocaleContextHolder.getLocale()));
				}
			} else if (minValue.compareTo(compValue) > 0) {
				wrapper.setErrorMessage(messages.getMessage(
						"fillParameters.error.smallerOrEqual", null, LocaleContextHolder.getLocale()));
			}
		}

		final Comparable maxValue = realDatatypeValue(dataType, dataType.getMaxValue());
		if (maxValue != null) {
			if (dataType.isStrictMax()) {
				if (maxValue.compareTo(compValue) <= 0) {
					wrapper.setErrorMessage(messages.getMessage(
							"fillParameters.error.greaterThan", null, LocaleContextHolder.getLocale()));
				}
			} else if (maxValue.compareTo(compValue) < 0) {
				wrapper.setErrorMessage(messages.getMessage(
						"fillParameters.error.greaterOrEqual", null, LocaleContextHolder.getLocale()));
			}
		}

		return wrapper.getErrorMessage() == null;
	}

	protected DataType getDatatype(InputControl control) {
		ResourceReference dataTypeRef = control.getDataType();
		DataType dataType;
		if (dataTypeRef == null) {
			dataType = null;
		} else if (dataTypeRef.isLocal()) {
			dataType = (DataType) dataTypeRef.getLocalResource();
		} else {
			dataType = (DataType) repository.getResource(new ExecutionContextImpl(), dataTypeRef.getReferenceURI());
		}
		return dataType;
	}

	protected Comparable realDatatypeValue(DataType dataType, Comparable value) {
		Comparable rValue = value;
		if (rValue != null) {
			if (rValue instanceof String && ((String) rValue).length() == 0) {
				rValue = null;
			} else if (dataType.getType() == DataType.TYPE_NUMBER) {
				rValue = new BigDecimal((String) value);
			}
		}
		return rValue;
	}

	protected Object getLovValue(RuntimeInputControlWrapper wrapper, String strValue) {
		return strValue;
	}


	protected Object getQueryValue(RuntimeInputControlWrapper wrapper, String strValue) {
		Object[] result =  (Object[]) wrapper.getQueryResults().get(strValue);
		Object value;
		if (result != null) {
			value = result[0];
		} else {
			value = null;
		}
		return value;
	}

	protected DateFormat getDateFormat(boolean interactiveParameters) {
		DateFormat format;

		if (interactiveParameters) {
			format = getCalendarFormatProvider().getDateFormat();
		} else {
			format = new SimpleDateFormat(getStaticDatePattern());
		}
		return format;
	}

	protected DateFormat getDatetimeFormat(boolean interactiveParameters) {
		DateFormat format;
		if (interactiveParameters) {
			format = getCalendarFormatProvider().getDatetimeFormat();
		} else {
			format = new SimpleDateFormat(getStaticDatePattern());
		}
		return format;
	}


	/**
	 * Converts BigDecimal numbers to the the type they should be based on
	 * what the JRParameters say.
	 *
	 * @todo add float and dobule
	 * @todo move this routine to a shared static location so others can use it
	 *
	 * @param reportName the name of the report
	 * @param wrappers Wrappers around InputControls which allow it to store values and error messages
	 */
	protected Map bindParameterValues(String reportName, List wrappers)
	{
		Map parameterValues = new HashMap();

		for (Iterator it = wrappers.iterator(); it.hasNext();)
		{
			RuntimeInputControlWrapper wrapper  = (RuntimeInputControlWrapper)it.next();
			String parameterName = wrapper.getInputControl().getName();//FIXME naming convention to replace when adding mapping
			ReportInputControlInformation controlInfo = wrapper.getControlInfo();
			if (controlInfo != null)
			{
				Object value = wrapper.getValue();

				if (value != null) {
					if (Number.class.isAssignableFrom(controlInfo.getValueType())) {
						value = getParameterNumberValue(wrapper, controlInfo.getValueType(), value);
					} else if (Date.class.isAssignableFrom(controlInfo.getValueType())) {
						value = getParameterDateValue(wrapper, controlInfo.getValueType(), value);
					} else if (String.class.equals(controlInfo.getValueType())) {
						value = value.toString();
					}

					if (wrapper.isMulti()) {
						value = getParameterMultiValue(wrapper, controlInfo.getValueType(), (Set) value);
					}
				}

				parameterValues.put(parameterName, value);
			}
		}

		return parameterValues;
	}

	protected Object getParameterNumberValue(RuntimeInputControlWrapper wrapper, Class type, Object value) {
		if (value instanceof String)
		{
			value = new BigDecimal((String) value);
		}
		else if (!(value instanceof Number))
		{
			throw new JSException("Cannot convert input value \"" + value + "\" of type " + value.getClass().getName() + " to a numerical parameter");
		}

		String className = type.getName();
		if (Byte.class.getName().equals(className))
		{
			value = new Byte(((Number)value).byteValue());
		}
		else if (Short.class.getName().equals(className))
		{
			value = new Short(((Number)value).shortValue());
		}
		else if (Integer.class.getName().equals(className))
		{
			value = new Integer(((Number)value).intValue());
		}
		else if (Long.class.getName().equals(className))
		{
			value = new Long(((Number)value).longValue());
		}
		else if (Float.class.getName().equals(className))
		{
			value = new Float(((Number)value).floatValue());
		}
		else if (Double.class.getName().equals(className))
		{
			value = new Double(((Number)value).doubleValue());
		}
		else if (BigInteger.class.getName().equals(className))
		{
			if (value instanceof BigDecimal)
			{
				value = ((BigDecimal) value).toBigInteger();
			}
			else if (!(value instanceof BigInteger))
			{
				value = BigInteger.valueOf(((Number) value).longValue());
			}
		}
		return value;
	}

	protected Object getParameterDateValue(RuntimeInputControlWrapper wrapper, Class type, Object value) {
		if (!(value instanceof Date)) {
			throw new JSException("exception.report.unsupported.date.input.value",
					new Object[]{value, value.getClass().getName()});
		}
		Date dateValue = (Date) value;

		Date parameterValue;
		String parameterClassName = type.getName();
		if (Date.class.getName().equals(parameterClassName)) {
			parameterValue = dateValue;
		} else if (java.sql.Date.class.getName().equals(parameterClassName)) {
			parameterValue = new java.sql.Date(dateValue.getTime());
		} else if (java.sql.Timestamp.class.getName().equals(parameterClassName)) {
			parameterValue = new java.sql.Timestamp(dateValue.getTime());
		} else {
			throw new JSException("exception.report.unsupported.date.parameter.type",
					new Object[]{parameterClassName});
		}
		return parameterValue;
	}

	protected Object getParameterMultiValue(RuntimeInputControlWrapper wrapper, Class paramType, Set values) {
		Object value;
		if (paramType.equals(Object.class)
			|| paramType.equals(Collection.class)
			|| paramType.equals(Set.class)) {
			value = values;
		} else if (paramType.equals(List.class)) {
			value = new ArrayList(values);
		} else if (paramType.isArray()) {
			Class componentType = paramType.getComponentType();
			value = Array.newInstance(componentType, values.size());
			int idx = 0;
			for (Iterator iter = values.iterator(); iter.hasNext(); ++idx) {
				Object val = iter.next();
				Array.set(value, idx, val);
			}
		} else {
			throw new JSException("jsexception.unknown.parameter.type.for.multiple.value.input", new Object[] {paramType.getName()});
		}
		return value;
	}


	protected String getCalendarDatePattern()
	{
		return getCalendarFormatProvider().getCalendarDatePattern();
	}


	protected String getCalendarDatetimePattern()
	{
		return getCalendarFormatProvider().getCalendarDatetimePattern();
	}

	/*
		 * method to get the reposervice object arguments: none returns:
		 * RepositoryService
		 */
	public RepositoryService getRepository() {
		return repository;
	}

	/*
	 * method to set the reposervice object arguments: RepositoryService
	 * returns: void
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}


	public MessageSource getMessages()
	{
		return messages;
	}

	public void setMessages(MessageSource messages)
	{
		this.messages = messages;
	}

	public EngineService getEngine() {
		return engine;
	}

	public void setEngine(EngineService engine) {
		this.engine = engine;
	}

	public String getReportUnitAttrName() {
		return reportUnitAttrName;
	}

	public void setReportUnitAttrName(String reportUnitAttrName) {
		this.reportUnitAttrName = reportUnitAttrName;
	}

	public String getHasInputControlsAttrName() {
		return hasInputControlsAttrName;
	}

	public void setHasInputControlsAttrName(String hasInputControlsAttrName) {
		this.hasInputControlsAttrName = hasInputControlsAttrName;
	}

	public String getStaticDatePattern() {
		return staticDatePattern;
	}

	public void setStaticDatePattern(String staticDatePattern) {
		this.staticDatePattern = staticDatePattern;
	}

	/**
	 * @return Returns the reportUnitObjectAttrName.
	 */
	public String getReportUnitObjectAttrName() {
		return reportUnitObjectAttrName;
	}

	/**
	 * @param reportUnitObjectAttrName The reportUnitObjectAttrName to set.
	 */
	public void setReportUnitObjectAttrName(String reportUnitObjectAttrName) {
		this.reportUnitObjectAttrName = reportUnitObjectAttrName;
	}

	/**
	 * @return Returns the controlsDisplayFormAttrName.
	 */
	public String getControlsDisplayFormAttrName() {
		return controlsDisplayFormAttrName;
	}

	/**
	 * @param controlsDisplayFormAttrName The controlsDisplayFormAttrName to set.
	 */
	public void setControlsDisplayFormAttrName(String controlsDisplayFormAttrName) {
		this.controlsDisplayFormAttrName = controlsDisplayFormAttrName;
	}

	/**
	 * @return Returns the reportDisplayFormAttrName.
	 */
	public String getReportDisplayFormAttrName() {
		return reportDisplayFormAttrName;
	}

	/**
	 * @param reportDisplayFormAttrName The reportDisplayFormAttrName to set.
	 */
	public void setReportDisplayFormAttrName(String reportDisplayFormAttrName) {
		this.reportDisplayFormAttrName = reportDisplayFormAttrName;
	}

	/**
	 * @return Returns the calendarDatePatternAttrName.
	 */
	public String getCalendarDatePatternAttrName()
	{
		return calendarDatePatternAttrName;
	}

	/**
	 * @param calendarDatePatternAttrName The calendarDatePatternAttrName to set.
	 */
	public void setCalendarDatePatternAttrName(String calendarDatePatternAttrName)
	{
		this.calendarDatePatternAttrName = calendarDatePatternAttrName;
	}

	public CalendarFormatProvider getCalendarFormatProvider() {
		return calendarFormatProvider;
	}

	public void setCalendarFormatProvider(
			CalendarFormatProvider calendarFormatProvider) {
		this.calendarFormatProvider = calendarFormatProvider;
	}

	public String getCalendarDatetimePatternAttrName() {
		return calendarDatetimePatternAttrName;
	}

	public void setCalendarDatetimePatternAttrName(
			String calendarDatetimePatternAttrName) {
		this.calendarDatetimePatternAttrName = calendarDatetimePatternAttrName;
	}

	public String getInputNamePrefix() {
		return inputNamePrefix;
	}

	public void setInputNamePrefix(String inputNamePrefix) {
		this.inputNamePrefix = inputNamePrefix;
	}

	public String getMarkerParameterPrefix() {
		return markerParameterPrefix;
	}

	public void setMarkerParameterPrefix(String markerParameterPrefix) {
		this.markerParameterPrefix = markerParameterPrefix;
	}

	public String getDefaultTopControlsView() {
		return defaultTopControlsView;
	}

	public void setDefaultTopControlsView(String defaultTopControlsView) {
		this.defaultTopControlsView = defaultTopControlsView;
	}

	public String getControlsDisplayViewAttrName() {
		return controlsDisplayViewAttrName;
	}

	public void setControlsDisplayViewAttrName(String controlsDisplayViewAttrName) {
		this.controlsDisplayViewAttrName = controlsDisplayViewAttrName;
	}

	public String getAttributeInputControlsInformation() {
		return attributeInputControlsInformation;
	}

	public void setAttributeInputControlsInformation(
			String attributeInputControlsInformation) {
		this.attributeInputControlsInformation = attributeInputControlsInformation;
	}
}
