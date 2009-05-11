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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.core.Ordered;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BeanReferenceOverrider.java 8408 2007-05-29 23:29:12Z melih $
 */
public class BeanReferenceOverrider implements BeanFactoryPostProcessor, Ordered {

	private static final Log log = LogFactory.getLog(BeanReferenceOverrider.class);
	
	private int order;
	private String originalBeanName;
	private String overridingBeanName;
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (int i = 0; i < beanDefinitionNames.length; i++) {
			String beanName = beanDefinitionNames[i];
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			process(beanDefinition, beanName);
		}
	}

	protected void process(BeanDefinition beanDefinition, String beanPath) {
		final MutablePropertyValues properties = beanDefinition.getPropertyValues();
		PropertyValue[] propertyValues = properties.getPropertyValues();
		for (int i = 0; i < propertyValues.length; i++) {
			final PropertyValue propertyValue = propertyValues[i];
			Object value = propertyValue.getValue();
			String propertyPath = beanPath + "." + propertyValue.getName();
			processValue(value, new ReferenceUpdater() {
				public void updateReference(RuntimeBeanReference newRef) {
					properties.addPropertyValue(propertyValue.getName(), newRef);
				}
			}, propertyPath);
		}
	}

	protected static interface ReferenceUpdater {
		void updateReference(RuntimeBeanReference newRef);
	}
	
	protected void processValue(Object value, ReferenceUpdater updater, String propertyPath) {
		if (value == null) {
			return;
		}
		
		if (value instanceof BeanReference) {
			if (((BeanReference) value).getBeanName().equals(getOriginalBeanName())) {
				RuntimeBeanReference newRef = new RuntimeBeanReference(getOverridingBeanName());
				updater.updateReference(newRef);
				
				if (log.isInfoEnabled()) {
					log.info("Changed reference " + propertyPath + " from " + getOriginalBeanName() + " to " + getOverridingBeanName());
				}
			}
		} else if (value instanceof BeanDefinitionHolder) {
			BeanDefinition innerBeanDef = ((BeanDefinitionHolder) value).getBeanDefinition();
			process(innerBeanDef, propertyPath);
		} else if (value instanceof ManagedList) {
			ManagedList list = (ManagedList) value;
			for (final ListIterator it = list.listIterator(); it.hasNext();) {
				Object listValue = it.next();
				String listPath = propertyPath + "[" + it.previousIndex() + "]";
				processValue(listValue, new ReferenceUpdater() {
					public void updateReference(RuntimeBeanReference newRef) {
						it.set(newRef);
					}
				}, listPath);
			}
		}
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getOriginalBeanName() {
		return originalBeanName;
	}

	public void setOriginalBeanName(String originalBeanName) {
		this.originalBeanName = originalBeanName;
	}

	public String getOverridingBeanName() {
		return overridingBeanName;
	}

	public void setOverridingBeanName(String overridingBeanName) {
		this.overridingBeanName = overridingBeanName;
	}

}
