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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author swood
 *
 */
public class BeanReportDataSourceServiceFactory implements ReportDataSourceServiceFactory, ApplicationContextAware {

	ApplicationContext ctx;
	
	/**
	 * 
	 */
	public BeanReportDataSourceServiceFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ctx = arg0;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof BeanReportDataSource)) {
			throw new JSException("jsexception.invalid.bean.datasource", new Object[] {reportDataSource.getClass()});
		}
		BeanReportDataSource beanDataSource = (BeanReportDataSource) reportDataSource;
	
		Object bean = ctx.getBean(beanDataSource.getBeanName());
		
		if (bean == null) {
			throw new JSException("jsexception.bean.no.name", new Object[] {beanDataSource.getBeanName()});
		}
		
		if (beanDataSource.getBeanMethod() == null) {
			// The bean had better be a ReportDataSourceService
			if (!(bean instanceof ReportDataSourceService)) {
				throw new JSException("jsexception.bean.not.a.ReportDataSourceService", new Object[] {beanDataSource.getBeanName()});
			} else {
				return (ReportDataSourceService) bean;
			}
		} else {
			// The method on this bean returns a ReportDataSourceService
			Method serviceMethod;
			try {
				serviceMethod = bean.getClass().getMethod(beanDataSource.getBeanMethod(), null);
				return (ReportDataSourceService) serviceMethod.invoke(bean, null);
			} catch (SecurityException e) {
				throw new JSExceptionWrapper(e);
			} catch (NoSuchMethodException e) {
				throw new JSException("jsexception.bean.has.no.method", new Object[] {beanDataSource.getBeanName(), beanDataSource.getBeanMethod()});
			} catch (IllegalArgumentException e) {
				throw new JSExceptionWrapper(e);
			} catch (IllegalAccessException e) {
				throw new JSExceptionWrapper(e);
			} catch (InvocationTargetException e) {
				throw new JSExceptionWrapper(e);
			}
		}
	}

}
