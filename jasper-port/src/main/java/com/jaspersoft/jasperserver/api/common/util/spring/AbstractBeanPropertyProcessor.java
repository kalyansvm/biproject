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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AbstractBeanPropertyProcessor.java 8745 2007-06-15 08:07:11Z cmatei $
 * 
 * From http://forum.springframework.org/showthread.php?t=30455&highlight=BeanFactoryPostProcessor :
 * 
 * "I saw that BeanFactoryPostProcessor-implementing beans that implement the Ordered interface get created 
 * and run their processing before BeanFactoryPostProcessor-implementing beans that do not implement the 
 * Ordered interface."
 * 
 * This turns out to be not to help in some cases. BeanPostProcessors like PropertyPlaceholderConfigurers 
 * will always run after BeanFactoryPostProcessors, so you will get unresolved substitutions for beans you directly
 * or indirectly try to update.
 * 
 * The workaround is to use EagerPropertyPlaceholderConfigurer http://opensource.atlassian.com/projects/spring/browse/SPR-1076
 * 
 */
public abstract class AbstractBeanPropertyProcessor implements BeanFactoryPostProcessor, Ordered {

	private int order = Ordered.LOWEST_PRECEDENCE;
	private String beanName;
	private String propertyName;
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition(getBeanName());
		MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
		Object value = null;
		PropertyValue propertyValue = propertyValues.getPropertyValue(getPropertyName());
		if (propertyValue != null) {
			value = propertyValue.getValue();
		}
		Object appendedValue = getProcessedPropertyValue(value);
		propertyValues.addPropertyValue(getPropertyName(), appendedValue);
	}

	protected abstract Object getProcessedPropertyValue(Object originalValue);

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}


}
