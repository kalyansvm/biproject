/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.export.modules.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.collections.set.ListOrderedSet;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DefaultReportParametersTranslator.java 12731 2008-03-28 13:37:04Z lucian $
 */
public class DefaultReportParametersTranslator implements
		ReportParametersTranslator {

	private RepositoryService repository;

	private EngineService engine;

	public ReportParameterValueBean[] getBeanParameterValues(
			String reportUnitURI, Map values) {
		ReportParameterValueBean[] beanValues;
		if (values == null || values.isEmpty()) {
			beanValues = null;
		} else {
			beanValues = new ReportParameterValueBean[values.size()];
			int idx = 0;
			for (Iterator it = values.entrySet().iterator(); it.hasNext(); ++idx) {
				Map.Entry entry = (Map.Entry) it.next();
				String name = (String) entry.getKey();
				Object value = entry.getValue();
				Object[] beanValue = toBeanParameterValues(value);
				ReportParameterValueBean param = new ReportParameterValueBean(
						name, beanValue);
				beanValues[idx] = param;
			}
		}
		return beanValues;
	}

	protected Object[] toBeanParameterValues(Object value) {
		Object[] values;
		if (value == null) {
			values = null;
		} else if (value.getClass().isArray()) {
			int count = Array.getLength(value);
			values = new Object[count];
			for (int idx = 0; idx < count; idx++) {
				values[idx] = Array.get(value, idx);
			}
		} else if (value instanceof Collection) {
			Collection valueCollection = (Collection) value;
			values = valueCollection.toArray();
		} else {
			values = new Object[] { value };
		}

		return values;
	}

	public Map getParameterValues(String reportUnitURI,
			ReportParameterValueBean[] beanValues) {
		Map values;
		if (beanValues == null || beanValues.length == 0) {
			values = null;
		} else {
			values = new HashMap();
			ReportUnit reportUnit = (ReportUnit) getRepository().getResource(
					null, reportUnitURI, ReportUnit.class);
			Map inputControls = collectInputControls(reportUnit);
			JasperReport jReport = getEngine().getMainJasperReport(null,
					reportUnitURI);

			for (int i = 0; i < beanValues.length; i++) {
				ReportParameterValueBean beanParam = beanValues[i];
				String name = (String) beanParam.getName();
				Object[] beanValue = beanParam.getValues();
				Object value = toParameterValue(reportUnitURI, inputControls,
						jReport, name, beanValue);
				values.put(name, value);
			}
		}
		return values;
	}

	protected Map collectInputControls(ReportUnit reportUnit) {
		Map controls = new HashMap();
		for (Iterator it = reportUnit.getInputControls().iterator(); it
				.hasNext();) {
			ResourceReference ref = (ResourceReference) it.next();
			InputControl control;
			if (ref.isLocal()) {
				control = (InputControl) ref.getLocalResource();
			} else {
				control = (InputControl) getRepository().getResource(null,
						ref.getReferenceURI(), InputControl.class);
			}
			controls.put(control.getName(), control);
		}
		return controls;
	}

	protected Object toParameterValue(String reportUnitURI, Map inputControls,
			JasperReport jReport, String name, Object[] beanValue) {
		Object value;
		InputControl control = (InputControl) inputControls.get(name);
		if (control == null) {
			JRParameter parameter = getParameter(jReport, name);
			if (parameter == null) {
				value = beanValue;
			} else if (parameter.getValueClass().isArray()) {
				value = toArrayValue(parameter.getValueClass(), beanValue);
			} else if (Collection.class.isAssignableFrom(parameter
					.getValueClass())) {
				value = toCollectionValue(parameter.getValueClass(), beanValue);
			} else if (Object.class.equals(parameter.getValueClass())) {
				if (beanValue == null || beanValue.length == 0) {
					value = null;
				} else if (beanValue.length == 1) {
					value = beanValue[0];
				} else {
					value = beanValue;
				}
			} else {
				value = toSingleValue(reportUnitURI, name, beanValue);
			}
		} else if (isMulti(control)) {
			JRParameter parameter = getParameter(jReport, name);
			if (parameter == null) {
				if (beanValue == null) {
					value = new Object[0];
				} else {
					value = beanValue;
				}
			} else if (parameter.getValueClass().isArray()) {
				value = toArrayValue(parameter.getValueClass(), beanValue);
			} else {
				value = toCollectionValue(parameter.getValueClass(), beanValue);
			}
		} else {
			value = toSingleValue(reportUnitURI, name, beanValue);
		}
		return value;
	}

	protected boolean isMulti(InputControl control) {
		byte type = control.getType();
		return type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES
				|| type == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX
				|| type == InputControl.TYPE_MULTI_SELECT_QUERY
				|| type == InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX
				|| type == InputControl.TYPE_MULTI_VALUE;
	}

	protected JRParameter getParameter(JasperReport report, String name) {
		JRParameter parameter = null;
		JRParameter[] parameters = report.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			if (name.equals(parameters[i].getName())) {
				parameter = parameters[i];
				break;
			}
		}
		return parameter;
	}

	protected Object toSingleValue(String reportUnitURI, String name,
			Object[] beanValue) {
		Object value;
		if (beanValue == null || beanValue.length == 0) {
			value = null;
		} else if (beanValue.length == 1) {
			value = beanValue[0];
		} else {
			throw new JSException(
					"jsexception.import.multiple.values.for.single.parameter",
					new Object[] { name, reportUnitURI });
		}
		return value;
	}

	protected Object toArrayValue(Class valueClass, Object[] beanValue) {
		Class componentType = valueClass.getComponentType();
		Object value;
		if (beanValue == null) {
			value = Array.newInstance(componentType, 0);
		} else {
			value = Array.newInstance(componentType, beanValue.length);
			for (int i = 0; i < beanValue.length; i++) {
				Array.set(value, i, beanValue[i]);
			}
		}
		return value;
	}

	protected Object toCollectionValue(Class valueClass, Object[] beanValue) {
		Collection value;
		if (valueClass.equals(Object.class)
				|| valueClass.equals(Collection.class)
				|| valueClass.equals(Set.class)) {
			value = new ListOrderedSet();
			if (beanValue != null) {
				for (int i = 0; i < beanValue.length; i++) {
					value.add(beanValue[i]);
				}
			}
		} else if (valueClass.equals(List.class)) {
			if (beanValue == null) {
				value = new ArrayList(0);
			} else {
				value = new ArrayList(beanValue.length);
				for (int i = 0; i < beanValue.length; i++) {
					value.add(beanValue[i]);
				}
			}
		} else {
			throw new JSException(
					"jsexception.unknown.parameter.type.for.multiple.value.input",
					new Object[] { valueClass.getName() });
		}
		return value;
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public EngineService getEngine() {
		return engine;
	}

	public void setEngine(EngineService engine) {
		this.engine = engine;
	}

}
