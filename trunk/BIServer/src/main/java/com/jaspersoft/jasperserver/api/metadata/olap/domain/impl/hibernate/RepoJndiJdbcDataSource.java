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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.impl.hibernate;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.JndiJdbcOlapDataSource;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataSourceImpl.java 2281 2006-03-02 18:05:23Z lucian $
 *
 * @hibernate.joined-subclass table="JNDIJdbcDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoJndiJdbcDataSource extends RepoOlapDataSource
{
	private String jndiName;
	
	public RepoJndiJdbcDataSource()
	{
	}

	/**
	 * @hibernate.property
	 * 		column="jndiName" type="string" length="100" not-null="true"
	 */
	public String getJndiName()
	{
		return jndiName;
	}

	public void setJndiName(String jndiName)
	{
		this.jndiName = jndiName;
	}


	protected Class getClientItf() {
		return JndiJdbcOlapDataSource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		JndiJdbcOlapDataSource ds = (JndiJdbcOlapDataSource) clientRes;
		ds.setJndiName(getJndiName());
	}
	
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		JndiJdbcOlapDataSource ds = (JndiJdbcOlapDataSource) clientRes;
		setJndiName(ds.getJndiName());
	}
}
