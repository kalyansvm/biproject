/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaspersoft.jasperserver.api.common.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * A property resource configurer that resolves placeholders in bean property values of
 * context definitions, immediately after the bean has been initialized.
 * 
 * It is useful to resolve bean property placeholders in other <code>Ordered</code>
 * <code>BeanFactoryPostProcessor</code>. It must be noted that the other <code>Ordered</code>
 * <code>BeanFactoryPostProcessor</code> must be defined after this.
 * 
 * A caveat is that not all <code>ListableBeanFactory</code> implementations
 * return bean names in the order of definition as prescribed by the interface,
 * which is relied upon by this configurer.
 * 
 * <p>Example XML context definition:
 *
 * <pre>
 * &lt;bean id="systemPropertiesConfigurer"
 *      class="au.com.cardlink.common.spring.beans.factory.config.EagerPropertyPlaceholderConfigurer"&gt;
 *   &lt;property name="placeholderPrefix"&gt;&lt;value&gt;$${&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;
 * &lt;bean id="configPropertiesConfigurer"
 *      class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer"&gt;
 *   &lt;property name="location"&gt;&lt;value&gt;$${config.properties.location}&lt;/value&gt;&lt;/property&gt;
 *   &lt;property name="fileEncoding"&gt;&lt;value&gt;$${cardlink.properties.encoding}&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;</pre>
 * 
 * @author Alex Wei (ozgwei@gmail.com)
 * @since 26/07/2006
 * @see Ordered
 * @see BeanFactoryPostProcessor
 * @see ListableBeanFactory
 * @see PropertyPlaceholderConfigurer
 */
public class EagerPropertyPlaceholderConfigurer
	extends PropertyPlaceholderConfigurer implements InitializingBean {

	private ConfigurableListableBeanFactory beanFactory;
	private boolean processingCompleted = false;
	
	/**
	 * Zero-argument constructor.
	 */
	public EagerPropertyPlaceholderConfigurer() {
		super();
	}

	/**
	 * Eagerly resolves property placeholders so that the bean definitions of other <code>BeanFactoryPostProcessor</code>
	 * can be modified before instantiation.
	 */
	public void afterPropertiesSet() throws Exception {
		if (this.beanFactory != null) {
			super.postProcessBeanFactory(this.beanFactory);
			this.processingCompleted = true;
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		// Obtains the BeanFactory where bean definitions with unresolved property placeholders are stored.
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
		} else {
			this.beanFactory = null;
		}
		super.setBeanFactory(beanFactory);
	}

	/**
	 * Resolves property placeholders only if the post processing was not run in {@link #afterPropertiesSet}.
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
		throws BeansException {

		// Should beanFactory be compared with this.beanFactory to ensure they are the same factory?
		if (!processingCompleted) {
			super.postProcessBeanFactory(beanFactory);
			processingCompleted = true;
		}
	}

}

