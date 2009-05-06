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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

/**
 * @author swood
 *
 * @hibernate.joined-subclass table="MondrianXMLADefinition"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoMondrianXMLADefinition extends RepoResource {

	private RepoMondrianConnection connection = null;
	private String catalog;

	/**
	 * @hibernate.property column="catalog" type="string" length="100"
	 *
	 * @return Returns the catalog.
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog The catalog to set.
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/**
     * @hibernate.many-to-one
     *              column="mondrianConnection"
     *
	 * @return Returns the connection.
	 */
	public RepoMondrianConnection getMondrianConnection() {
		return connection;
	}

	/**
	 * @param connection The connection to set.
	 */
	public void setMondrianConnection(RepoMondrianConnection connection) {
		this.connection = connection;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase#copyTo(com.jaspersoft.jasperserver.api.metadata.common.domain.Resource)
	 */
    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);

		MondrianXMLADefinition def = (MondrianXMLADefinition) clientRes;

		def.setCatalog(getCatalog());
		def.setMondrianConnection(getClientReference(getMondrianConnection(), resourceFactory));

	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase#copyFrom(com.jaspersoft.jasperserver.api.metadata.common.domain.Resource)
	 */
    protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);
		MondrianXMLADefinition def = (MondrianXMLADefinition) clientRes;

		setCatalog(def.getCatalog());
		ResourceReference ds = def.getMondrianConnection();
		RepoMondrianConnection repoConn = (RepoMondrianConnection) getReference(ds, RepoMondrianConnection.class, referenceResolver);
		setMondrianConnection(repoConn);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase#getClientItf()
	 */
	protected Class getClientItf() {
		return MondrianXMLADefinition.class;
	}

}
