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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

/**
 * @author swood
 *
 */
public interface XMLAConnection extends OlapClientConnection, ReportDataSource {
	
    /**
     * URI for XML/A service, like "http://localhost:8080/jpivot/xmla"
     */
    public String getURI();
    public void setURI(String uri);
    
    /**
     * XML/A Data Source, like "Provider=Mondrian;DataSource=MondrianFoodMart;"
     * 
     */
    public String getDataSource();
    public void setDataSource(String xmlaDataSource);
    
    /**
     * Catalog within the Data Source, like "FoodMart"
     * 
     */
    public String getCatalog();
    public void setCatalog(String catalog);

    /**
     * username for HTTP authentication of XMLA
     * 
     */
    public String getUsername();
    public void setUsername(String username);

    /**
     * password for HTTP authentication of XMLA
     * 
     */
    public String getPassword();
    public void setPassword(String password);


}
