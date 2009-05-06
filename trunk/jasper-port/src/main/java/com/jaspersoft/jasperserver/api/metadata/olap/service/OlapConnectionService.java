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
package com.jaspersoft.jasperserver.api.metadata.olap.service;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

import com.tonbeller.jpivot.olap.model.OlapModel;
import mondrian.olap.Util;

/**
 * @author sbirney
 *
 */
public interface OlapConnectionService {

    public OlapModel createOlapModel( ExecutionContext context, 
				      OlapUnit olapUnit );
	
    public Util.PropertyList getMondrianConnectProperties( ExecutionContext context, 
							   MondrianConnection conn );

    /* currently validates MDX for native mondrian connections only */
    public ValidationResult validate(ExecutionContext context,
				     OlapUnit unit);

    public ValidationResult validate(ExecutionContext context, 
				     OlapUnit unit,
				     FileResource schema,
				     OlapClientConnection conn,
				     ReportDataSource dataSource);

    /* consider moving to RepositoryService */
    public void saveResource( ExecutionContext context, 
			      String path, 
			      Resource resource );

    /* consider moving to RepositoryService */
    public Resource dereference( ExecutionContext context, 
				 ResourceReference ref );	

    /* consider moving to RepositoryService */
    public String getFileResourceData(ExecutionContext context,
				      FileResource file);


}
