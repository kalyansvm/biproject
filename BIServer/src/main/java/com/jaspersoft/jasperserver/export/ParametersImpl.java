/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package com.jaspersoft.jasperserver.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ParametersImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ParametersImpl implements Parameters {

	private final Map params;
	
	public ParametersImpl() {
		this.params = new LinkedHashMap();
	}
	
	public Iterator getParameterNames() {
		return params.keySet().iterator();
	}

	public boolean hasParameter(String parameterName) {
		return params.containsKey(parameterName);
	}

	public String getParameterValue(String parameterName) {
		String value;
		Object paramValue = params.get(parameterName);
		if (paramValue == null) {
			value = null;
		} else if (paramValue instanceof List) {
			List valuesList = (List) paramValue;
			int valuesCount = valuesList.size();
			if (valuesCount == 0) {
				value = null;
			} else if (valuesCount == 1) {
				value = (String) valuesList.get(0);
			} else {
				throw new JSException("jsexception.export.parameter.has.multiple.values", new Object[] {parameterName});
			}
		} else {
			value = (String) paramValue;
		}
		return value;
	}

	public String[] getParameterValues(String parameterName) {
		String[] values;
		Object value = params.get(parameterName);
		if (value == null) {
			values = null;
		} else if (value instanceof List) {
			List valuesList = (List) value;
			values = new String[valuesList.size()];
			values = (String[]) valuesList.toArray(values);
		} else {
			values = new String[]{(String) value};
		}
		return values;
	}
	
	public Parameters addParameter(String parameterName) {
		if (!params.containsKey(parameterName)) {
			params.put(parameterName, null);
		}
		return this;
	}
	
	public Parameters addParameterValue(String parameterName, String parameterValue) {
		Object value = params.get(parameterName);
		if (value == null) {
			params.put(parameterName, parameterValue);
		} else if (value instanceof List) {
			((List) value).add(parameterValue);
		} else {
			List values = new ArrayList();
			values.add(value);
			values.add(parameterValue);
			params.put(parameterName, values);
		}
		return this;
	}
	
	public Parameters addParameterValues(String parameterName, String[] parameterValues) {
		Object value = params.get(parameterName);
		if (value == null || !(value instanceof List)) {
			List values = new ArrayList();
			if (value != null) {
				values.add(value);
			}			
			for (int i = 0; i < parameterValues.length; i++) {
				values.add(parameterValues[i]);
			}
			params.put(parameterName, values);
		} else {
			List values = (List) value;
			for (int i = 0; i < parameterValues.length; i++) {
				values.add(parameterValues[i]);
			}
		}
		return this;
	}

}
