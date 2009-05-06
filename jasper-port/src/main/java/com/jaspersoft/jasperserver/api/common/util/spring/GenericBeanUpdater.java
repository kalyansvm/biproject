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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.RuntimeBeanReference;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author bob
 *
 */
public class GenericBeanUpdater extends AbstractBeanPropertyProcessor {
	private GenericBeanUpdaterDefinition definition;
	private String key;
	private Object value;
	private String valueRef;
	private String before;
	private int order = 0;
	private boolean orderSet = false;


	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.util.spring.AbstractBeanPropertyProcessor#getProcessedPropertyValue(java.lang.Object)
	 */
	protected Object getProcessedPropertyValue(Object originalValue) {
		if (definition.getOperation().equals(GenericBeanUpdaterDefinition.APPEND)) {
			return append(originalValue);
		} else if (definition.getOperation().equals(GenericBeanUpdaterDefinition.SET)) {
			return set(originalValue);
		} else if (definition.getOperation().equals(GenericBeanUpdaterDefinition.INSERT)) {
			return insert(originalValue);
		} else {
			throw new JSException("jsexception.unknown.updater.operation", new Object[] { getBeanName(), definition.getOperation()});
		}
	}
	
	/**
	 * @param originalValue
	 * @return
	 */
	private Object append(Object originalValue) {
		// just set value if not set to anything now
		if (originalValue == null) {
			return getSinglePropertyValue();
		}
		if (originalValue instanceof Map) {
			Map newValue = (Map) originalValue;
			if (key != null) {
				newValue.put(key, getSinglePropertyValue());
			} else if (value instanceof Map) {
				newValue.putAll((Map) value);
			} else {
				throw new JSException("jsexception.cant.append.to.map", new Object[] {getPropertyName(), getBeanName(), value});
			}
			return newValue;
		} else if (originalValue instanceof List) {
			List newValue = (List) originalValue;
			if (value instanceof List) {
				newValue.addAll((List) value);
			} else {
				newValue.add(getSinglePropertyValue());
			}
			return newValue;
		} else if (originalValue instanceof String) {
			return ((String) originalValue) + value;
		} else {
			throw new JSException("jsexception.cant.append", new Object[] {getPropertyName(), getBeanName(), value});
		}
	}

	/**
	 * just set it
	 * @param originalValue
	 * @return
	 */
	private Object set(Object originalValue) {
		return getSinglePropertyValue();
	}
	
	/**
	 * @param originalValue
	 * @return
	 */
	private Object insert(Object originalValue) {
		if (originalValue instanceof String) {
			StringBuffer newValue = new StringBuffer((String) originalValue);
			int index = 0;
			if (before != null) {
				// look for "before" string
				index = newValue.indexOf(before);
				if (index == -1) {
					throw new JSException("jsexception.cant.find.before.string", new Object[] {getPropertyName(), getBeanName(), value});
				}
			}
			newValue.insert(index, value);
			return newValue.toString();
		} else {
			throw new JSException("jsexception.cant.insert", new Object[] {getPropertyName(), getBeanName(), value});
		}
	}

	public String getBeanName() {
		return definition.getBeanName();
	}

	protected Object getSinglePropertyValue() {
		Object propValue;
		if (value != null) {
			propValue = value;
		} else if (valueRef != null) {
			propValue = new RuntimeBeanReference(valueRef);
		} else {
			propValue = null;
		}
		return propValue;
	}
	
	public int getOrder() {
		return orderSet ? order : definition.getOrder();
	}

	// you can set order here to override the one in the def
	public void setOrder(int order) {
		orderSet = true;
		this.order = order;
	}
	
	public String getPropertyName() {
		return definition.getPropertyName();
	}

	public GenericBeanUpdaterDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(GenericBeanUpdaterDefinition definition) {
		this.definition = definition;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getValueRef() {
		return valueRef;
	}

	public void setValueRef(String valueRef) {
		this.valueRef = valueRef;
	}

}
