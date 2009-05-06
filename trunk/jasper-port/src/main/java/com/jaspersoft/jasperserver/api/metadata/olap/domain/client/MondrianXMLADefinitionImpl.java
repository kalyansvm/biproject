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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

/**
 * @author swood
 *
 */
public class MondrianXMLADefinitionImpl extends ResourceImpl implements
		MondrianXMLADefinition {

	private String catalog;
	private ResourceReference connection = null;

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#getCatalog()
	 */
	public String getCatalog() {
		return catalog;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#getMondrianConnection()
	 */
	public ResourceReference getMondrianConnection() {
		return connection;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setMondrianConnection(com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection)
	 */
	public void setMondrianConnection(MondrianConnection connection) {
		setMondrianConnection(new ResourceReference(connection));
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setMondrianConnection(com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference)
	 */
	public void setMondrianConnection(ResourceReference connectionReference) {
		this.connection = connectionReference;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition#setMondrianConnectionReference(java.lang.String)
	 */
	public void setMondrianConnectionReference(String referenceURI) {
		setMondrianConnection(new ResourceReference(referenceURI));
	}

	protected Class getImplementingItf() {
		return MondrianXMLADefinition.class;
	}

}
