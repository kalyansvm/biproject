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
package com.jaspersoft.jasperserver.api.metadata.olap.domain;

/**
 * @author sbirney
 *
 */
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

public interface OlapUnit extends Resource {

    /*
     * OlapConnection
     */
    public ResourceReference getOlapClientConnection();

    public void setOlapClientConnection(OlapClientConnection olapConnection);

    public void setOlapClientConnection(ResourceReference olapConnectionReference);
	
    public void setOlapClientConnectionReference(String referenceURI);

    /*
     * MdxQuery
     */
    public String getMdxQuery();
	
    public void setMdxQuery(String query);
    
    /*
     * OlapViewSaveOptions
     */
    public Object getOlapViewOptions();
    
    public void setOlapViewOptions(Object options);
    

    /*
     * For Resource management
     */
    
	/**
	 * Returns the reference to the
	 * {@link com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapDataSource data source}
	 * used by this olap unit.
	 * 
	 * @return a reference to the data source used by this olap unit
	 */
	public ResourceReference getDataSource();
	
	public void setDataSource(ResourceReference dataSourceReference);
	
	public void setDataSource(ReportDataSource dataSource);
	
	public void setDataSourceReference(String referenceURI);
}

