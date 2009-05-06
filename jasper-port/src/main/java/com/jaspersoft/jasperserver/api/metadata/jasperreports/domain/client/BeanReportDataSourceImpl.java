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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;

/**
 * @author swood
 *
 */
public class BeanReportDataSourceImpl extends ReportDataSourceImpl implements BeanReportDataSource {

	String beanName;
	String beanMethod;

	/**
	 *
	 */
	public BeanReportDataSourceImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#getBeanMethod()
	 */
	public String getBeanMethod() {
		return beanMethod;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#getBeanName()
	 */
	public String getBeanName() {
		return beanName;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#setBeanMethod(java.lang.String)
	 */
	public void setBeanMethod(String beanMethod) {
		this.beanMethod = beanMethod;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#setBeanName(java.lang.String)
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl#getImplementingItf()
	 */
	protected Class getImplementingItf() {
		return BeanReportDataSource.class;
	}

}
