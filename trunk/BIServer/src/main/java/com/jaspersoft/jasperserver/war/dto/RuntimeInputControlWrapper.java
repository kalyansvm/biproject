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
package com.jaspersoft.jasperserver.war.dto;

import java.io.Serializable;
import java.text.Format;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.jaspersoft.jasperserver.api.common.util.MapEntry;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;

/**
 *
 */
public class RuntimeInputControlWrapper implements Serializable
{
	private InputControl inputControl;
	private Object value;
	private String errorMessage;
	private Format format;
	private Map queryResults;
	private ReportInputControlInformation controlInfo;
	
	private transient Map multiValueIndicator;

	public RuntimeInputControlWrapper(InputControl inputControl)
	{
		this.inputControl = inputControl;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public InputControl getInputControl()
	{
		return inputControl;
	}

	public void setInputControl(InputControl inputControl)
	{
		this.inputControl = inputControl;
	}

	public Format getFormat()
	{
		return format;
	}

	public void setFormat(Format format)
	{
		this.format = format;
	}

	public String getFormattedValue()
	{
		try {
			return format.format(value);
		} catch(Exception e) {
			return value != null ? value.toString() : null;
		}
	}

	public Map getQueryResults()
	{
		return queryResults;
	}

	public void setQueryResults(Map queryResults)
	{
		this.queryResults = queryResults;
	}
	
	public boolean isMulti() {
		if (inputControl == null) {
			return false;
		}

		byte type = inputControl.getType();
		return type == InputControl.TYPE_MULTI_VALUE
				|| type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES
				|| type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX
				|| type == InputControl.TYPE_MULTI_SELECT_QUERY
				|| type == InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX;
	}

	public boolean isMultiDistinctValues() {
		if (inputControl == null) {
			return false;
		}

		byte type = inputControl.getType();
		return 
				type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES
				|| type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX
				|| type == InputControl.TYPE_MULTI_SELECT_QUERY
				|| type == InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX;
	}
	
	public boolean isListOfValues() {
		byte type = inputControl.getType();
		return
			type == InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES
			|| type == InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO
			|| type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES
			|| type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX;
	}
	
	public boolean isQuery() {
		byte type = inputControl.getType();
		return
			type == InputControl.TYPE_SINGLE_SELECT_QUERY
			|| type == InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO
			|| type == InputControl.TYPE_MULTI_SELECT_QUERY
			|| type == InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX;
	}
	
	public int getCollectionSize() {
		int size;
		if (isListOfValues()) {
			size = ((ListOfValues) getInputControl().getListOfValues().getLocalResource()).getValues().length;
		} else if (isQuery()) {
			size = getQueryResults().size();
		} else {
			throw new UnsupportedOperationException("getCollectionSize can only be called for LOV or query input controls");
		}
		return size;
	}
	
	public Map getCollectionValueIndicator() {
		if (multiValueIndicator == null) {
			if (isListOfValues() || isQuery()) {
				multiValueIndicator = new IndicatorMap((Collection) getValue());
			}
		}
		return multiValueIndicator;
	}
	
	protected static class IndicatorMap extends AbstractMap {

		private final Set entries;
		
		public IndicatorMap(Collection keys) {
			entries = new HashSet();
			if (keys != null) {
				for (Iterator it = keys.iterator(); it.hasNext();) {
					Object key = (Object) it.next();
					Map.Entry entry = new MapEntry(key, Boolean.TRUE);
					entries.add(entry);
				}
			}
		}
		
		public Set entrySet() {
			return entries;
		}
		
	}

	public ReportInputControlInformation getControlInfo() {
		return controlInfo;
	}

	public void setControlInfo(ReportInputControlInformation controlInfo) {
		this.controlInfo = controlInfo;
	}
}
