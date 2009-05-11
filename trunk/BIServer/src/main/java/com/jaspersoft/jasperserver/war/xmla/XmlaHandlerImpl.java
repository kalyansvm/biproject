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
package com.jaspersoft.jasperserver.war.xmla;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;

import mondrian.xmla.XmlaRequest;
import mondrian.xmla.XmlaException;
import mondrian.xmla.XmlaHandler;
import mondrian.xmla.XmlaConstants;
import mondrian.xmla.DataSourcesConfig;

import mondrian.rolap.RolapConnectionProperties;
import mondrian.olap.Util;
import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.rolap.RolapConnection;
import mondrian.spi.CatalogLocator;

/**
 * @author sbirney
 */

public class XmlaHandlerImpl extends XmlaHandler {

    private static final Log log = LogFactory.getLog(XmlaHandlerImpl.class);
    private static final String XMLA_PREFIX = "xmla";

    public XmlaHandlerImpl(
            DataSourcesConfig.DataSources dataSources,
            CatalogLocator catalogLocator,
	    RepositoryService repository,
	    OlapConnectionService connectionService) 
    {
	super(dataSources, catalogLocator, XMLA_PREFIX);
	setRepository(repository);
	setConnectionService(connectionService);
    }

    /**
     * Gets a Connection given a catalog (and implicitly the catalog's data
     * source) and a user role.  overrides XmlaHandler version 18.
     *
     * @param catalog Catalog
     * @param role User role
     * @return Connection
     * @throws XmlaException
     */
    protected Connection getConnection(
            DataSourcesConfig.Catalog catalog,
            String role)
            throws XmlaException {

	Map dsMap = getDataSourceEntries();
	for (Iterator keys = dsMap.keySet().iterator(); keys.hasNext();) {
	    Object key = keys.next();
	    if (log.isDebugEnabled()) {
		log.debug("DATASOURCE KEY=" + key + " " + key.getClass() + 
		      " ,VALUE=" + dsMap.get(key) + " " + dsMap.get(key).getClass());
	    }
	}
	
        DataSourcesConfig.DataSource ds = catalog.getDataSource();
	
	ExecutionContext context = null;
	MondrianConnection monConn = lookupXmlaConnection(context, ds);
	Util.PropertyList connectProperties =
	    getConnectionService().getMondrianConnectProperties(context, monConn);

        // Checking access
        if (!DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED.
	    equalsIgnoreCase(ds.getAuthenticationMode()) && null == role) {
            throw new XmlaException(
                CLIENT_FAULT_FC,
                HSB_ACCESS_DENIED_CODE,
                HSB_ACCESS_DENIED_FAULT_FS,
                new SecurityException("Access denied for data source needing authentication")
                );
        }

        connectProperties.put("role", role);
        RolapConnection conn =
            (RolapConnection) DriverManager.getConnection(connectProperties, null, false);
        return conn;
    }

    /** 
     * Get a Connection given the Datasource, catalogUrl and user role. 
     * 
     * @param ds 
     * @param catalogUrl 
     * @param role 
     * @return mondrian.olap.Connection
     * @throws XmlaException 
     *
     * overides XmlaHandler version 15, package access must be protected
     */
    protected Connection getConnection(DataSourcesConfig.DataSource ds,
				       String catalogUrl, String role) 
            throws XmlaException {


	ExecutionContext context = null;
	MondrianConnection monConn = lookupXmlaConnection(context, ds);
	Util.PropertyList connectProperties =
	    getConnectionService().getMondrianConnectProperties(context, monConn);

        // Checking access
        if (!DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED.equalsIgnoreCase(ds.getAuthenticationMode()) && null == role) {
            throw new XmlaException(
                CLIENT_FAULT_FC,
                HSB_ACCESS_DENIED_CODE,
                HSB_ACCESS_DENIED_FAULT_FS,
                new SecurityException("Access denied for data source needing authentication")
                );
        }

        connectProperties.put("role", role);
        RolapConnection conn =
            (RolapConnection) DriverManager.getConnection(connectProperties, null, false);

        return conn;
    }

    /**
     * Returns a Mondrian connection as specified by a set of properties
     * which are used to locate the MondrianXMLADefinition in the repository
     *
     * overides XmlaHandler version #14, except package access must be protected
     */
    protected Connection getConnection(XmlaRequest request) 
	        throws XmlaException {
	log.debug("XmlaHandlerImpl:getConnection");
	Map dataSourcesMap = getDataSourceEntries();
        Map properties = request.getProperties();

	// too bad PropertyDefinition is not accessible outside package
        //final String dataSourceInfo =
        //    (String) properties.get(PropertyDefinition.DataSourceInfo.name);
	final String dataSourceInfo = (String) properties.get("DataSourceInfo");

        if (!dataSourcesMap.containsKey(dataSourceInfo)) {
            throw new XmlaException(
                CLIENT_FAULT_FC,
                HSB_CONNECTION_DATA_SOURCE_CODE, 
                HSB_CONNECTION_DATA_SOURCE_FAULT_FS,
                Util.newError("no data source is configured with name '" + dataSourceInfo + "'"));
        }
        if (log.isDebugEnabled()) {
            log.debug("XmlaHandlerImpl.getConnection: dataSourceInfo=" + 
                    dataSourceInfo);
        }

        DataSourcesConfig.DataSource ds =
            (DataSourcesConfig.DataSource) dataSourcesMap.get(dataSourceInfo);
        if (log.isDebugEnabled()) {
            if (ds == null) {
                log.debug("XmlaHandlerImpl.getConnection: ds is null");
            } else {
                log.debug("XmlaHandlerImpl.getConnection: ds.dataSourceInfo=" + 
                    ds.getDataSourceInfo());
            }
        }

	ExecutionContext context = null;
	MondrianConnection monConn = lookupXmlaConnection(context, ds);
	Util.PropertyList connectProperties =
	    getConnectionService().getMondrianConnectProperties(context, monConn);

        // Checking access
        if (!DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED.equalsIgnoreCase(ds.getAuthenticationMode()) &&
                null == request.getRole()) {
            throw new XmlaException(
                CLIENT_FAULT_FC,
                HSB_ACCESS_DENIED_CODE, 
                HSB_ACCESS_DENIED_FAULT_FS,
                new SecurityException("Access denied for data source needing authentication")
                );
        }

        connectProperties.put("role", request.getRole());
        RolapConnection conn =
            (RolapConnection) DriverManager.getConnection(connectProperties, null, false);

        return conn;
    }

    protected MondrianConnection lookupXmlaConnection(ExecutionContext context,
						      DataSourcesConfig.DataSource ds) {
	if (log.isDebugEnabled()) {
	    log.debug("looking up DataSource name=" + ds.name + 
		     ", info=" + ds.dataSourceInfo + ", url=" + ds.url);
	}

	MondrianConnection result = null;
	RepositoryService rep = getRepository();

	// Use loadResourcesList to filter with security
	
	// TODO Make this more efficient: cache? use a URI to lookup?
	
	FilterCriteria f = FilterCriteria.createFilter(MondrianXMLADefinition.class);
	List lookups = rep.loadResourcesList(context, f);

	if (lookups == null || lookups.size() == 0) {
	    log.error("No XMLA Definitions");
	} else {
	
		for (Iterator it = lookups.iterator(); it.hasNext(); ) {
		    
			MondrianXMLADefinition  xmlaDef = (MondrianXMLADefinition) rep.getResource(context, ((ResourceLookup) it.next()).getURIString());
		    if (ds.name != null &&
		    		ds.name.endsWith( "DataSource=" + xmlaDef.getCatalog() + ";" )) {
				result = (MondrianConnection)
				    getConnectionService().dereference(context, xmlaDef.getMondrianConnection());
				log.debug("Connection Found for catalog: " + xmlaDef.getCatalog());
				break;
		    }
		}
	}
	if (result == null) {
	    log.error("Mondrian XMLA Definition not found for name: " + ds.name);
	}
	return result;
    }
    
    /**
     * property: olapConnectionService
     */
    private OlapConnectionService mConnectionService;
    public OlapConnectionService getConnectionService() {
        return mConnectionService;
    }
    public void setConnectionService( OlapConnectionService cs ) {
        mConnectionService = cs;
    }

    /**
     * property: repository
     */
    private RepositoryService mRepository;
    public RepositoryService getRepository() {
        return mRepository;
    }
    public void setRepository(RepositoryService repository) {
        mRepository = repository;
    }

}
