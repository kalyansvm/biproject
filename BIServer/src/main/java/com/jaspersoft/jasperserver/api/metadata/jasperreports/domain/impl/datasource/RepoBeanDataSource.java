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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;

/**
 * @author swood
 *
 * @hibernate.joined-subclass table="BeanDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoBeanDataSource extends RepoDataSource implements RepoReportDataSource {

	String beanName;
	String beanMethod;

	/**
	 * 
	 */
	public RepoBeanDataSource() {
		super();
	}
	
	/**
	 * @hibernate.property
	 * 		column="beanMethod" type="string" length="100"
	 * 
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource#getBeanMethod()
	 */
	public String getBeanMethod() {
		return beanMethod;
	}

	/**
	 * 	 * @hibernate.property
	 * 		column="beanName" type="string" length="100" not-null="true"
	 * 
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

	protected Class getClientItf() {
		return BeanReportDataSource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		BeanReportDataSource ds = (BeanReportDataSource) clientRes;
		ds.setBeanName(getBeanName());
		ds.setBeanMethod(getBeanMethod());
	}
	
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		BeanReportDataSource ds = (BeanReportDataSource) clientRes;
		setBeanName(ds.getBeanName());
		setBeanMethod(ds.getBeanMethod());
	}

}
