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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;

/**
 * @author sbirney
 *
 */
//public class OlapUnitImpl extends ResourceImpl implements OlapUnit {
public class OlapUnitImpl extends ResourceImpl implements OlapUnit {

    private String mdxQuery;
    private ResourceReference olapClientConnection;
    private Object olapViewOptions;
    

    /*
     * OlapConnection
     */
    public ResourceReference getOlapClientConnection() {
	return olapClientConnection;
    }

    public void setOlapClientConnection(OlapClientConnection olapConnection) {
	setOlapClientConnection(new ResourceReference(olapConnection));
    }

    public void setOlapClientConnection(ResourceReference olapConnectionReference) {
	olapClientConnection = olapConnectionReference;
    }
	
    public void setOlapClientConnectionReference(String referenceURI) {
	setOlapClientConnection(new ResourceReference(referenceURI));
    }

    /*
     * MdxQuery
     */
    public String getMdxQuery() {
	return mdxQuery;
    }
	
    public void setMdxQuery(String query) {
	mdxQuery = query;
    }
    
    /*
     * OlapViewSaveOptions
     */
    public Object getOlapViewOptions() {
    	return olapViewOptions;
    }
    
    public void setOlapViewOptions(Object options) {
    	olapViewOptions = options;
    }


    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl#getImplementingItf()
     */
    protected Class getImplementingItf() {
	return OlapUnit.class;
    }

    /*
     * For Resource maintenance 
     */
    
	private ResourceReference dataSource = null;
	
	public ResourceReference getDataSource()
	{
		return dataSource;
	}
	
	/**
	 * 
	 */
	public void setDataSource(ResourceReference dataSource)
	{
		this.dataSource = dataSource;
	}

	public void setDataSource(ReportDataSource dataSource) {
		setDataSource(new ResourceReference(dataSource));
	}

	
	public void setDataSourceReference(String referenceURI) {
		setDataSource(new ResourceReference(referenceURI));
	}
}
