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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;

/**
 * @author swood
 *
 */
public class MondrianConnectionImpl extends OlapClientConnectionImpl implements MondrianConnection {

	private ResourceReference schema = null;
	private ResourceReference dataSource = null;

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getDataSource()
	 */
	public ResourceReference getDataSource() {
		return dataSource;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getSchema()
	 */
	public ResourceReference getSchema() {
		return schema;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#setDataSource(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */

	public void setDataSource(ReportDataSource dataSource) {
		setDataSource(new ResourceReference(dataSource));
	}


	public void setDataSourceReference(String referenceURI) {
		setDataSource(new ResourceReference(referenceURI));
	}

	public void setDataSource(ResourceReference dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 * 
	 */
	public void setSchema(ResourceReference schema)	{
		this.schema = schema;
	}


	public void setSchema(FileResource schema) {
		setSchema(new ResourceReference(schema));
	}


	public void setSchemaReference(String referenceURI) {
		setSchema(new ResourceReference(referenceURI));
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl#getImplementingItf()
	 */
	protected Class getImplementingItf() {
		return MondrianConnection.class;
	}

}
