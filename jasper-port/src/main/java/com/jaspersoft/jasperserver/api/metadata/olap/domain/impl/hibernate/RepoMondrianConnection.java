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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;

/**
 * @author swood
 *
 * @hibernate.joined-subclass table="MondrianConnection"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoMondrianConnection extends RepoOlapClientConnection implements RepoReportDataSource {
	private RepoResource dataSource = null;
	private RepoFileResource schema = null;

	/**
	 * @hibernate.many-to-one
     *              column="mondrianSchema"
     *
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getSchema()
	 *
     */
	public RepoFileResource getSchema() {
		return schema;
	}

	/**
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#setSchema(com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource)
	 */
	public void setSchema(RepoFileResource f) {
		schema = f;
	}

	/**
     * @hibernate.many-to-one
     *              column="dataSource"
     *
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getDataSource()
     */
	public RepoResource getDataSource() {
		return dataSource;
	}

	/**
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#setDataSource(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public void setDataSource(RepoResource ds) {
		dataSource = ds;
	}

    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);

        MondrianConnection conn = (MondrianConnection) clientRes;
        conn.setDataSource(getClientReference(getDataSource(), resourceFactory));
        conn.setSchema(getClientReference( getSchema(), resourceFactory));
    }

    protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);

        MondrianConnection conn = (MondrianConnection) clientRes;
		copyDataSource(referenceResolver, conn);
		copySchema(referenceResolver, conn);
    }


	private void copyDataSource(ReferenceResolver referenceResolver, MondrianConnection conn) {
		ResourceReference ds = conn.getDataSource();
		RepoResource repoDS = getReference(ds, RepoReportDataSource.class, referenceResolver);
		if (repoDS != null && !(repoDS instanceof RepoReportDataSource)) {
			throw new JSException("jsexception.mondrian.jdbc.datasource.has.an.invalid.type", new Object[] {repoDS.getClass().getName()});
		}
		setDataSource(repoDS);
	}

	private void copySchema(ReferenceResolver referenceResolver, MondrianConnection conn) {
		ResourceReference report = conn.getSchema();
		RepoFileResource repoReport = (RepoFileResource) getReference(report, RepoFileResource.class, referenceResolver);
		setSchema(repoReport);
	}

	protected Class getClientItf() {
		return MondrianConnection.class;
	}

}
