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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.i18n.LocaleContextHolder;

import com.jaspersoft.jasperserver.api.JSException;
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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;

import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationDetailImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationResultImpl;
import com.jaspersoft.jasperserver.api.common.util.StaticCharacterEncodingProvider;

import mondrian.rolap.RolapConnectionProperties;
import mondrian.olap.Connection;
import mondrian.olap.Util;
import mondrian.olap.DriverManager;

import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.tags.OlapModelProxy;

import com.tonbeller.jpivot.tags.MondrianOlapModelTag;
import com.tonbeller.jpivot.xmla.XMLA_Model;
import com.tonbeller.jpivot.xmla.XMLA_OlapModelTag;

/**
 * @author sbirney
 * $Id: OlapConnectionServiceImpl.java 13769 2008-05-23 07:46:00Z swood $
 */

public class
OlapConnectionServiceImpl implements OlapConnectionService, ReportDataSourceServiceFactory {

    private static final Log log = LogFactory
	.getLog(OlapConnectionServiceImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService.createOlapModel()
     *
     * @return a newly constructed and configured OlapModel initialize is not
     * yet called.
     */
    public OlapModel createOlapModel(ExecutionContext context, OlapUnit olapUnit) {
	OlapClientConnection clientConn = (OlapClientConnection)
	    dereference(context, olapUnit.getOlapClientConnection());
	if (clientConn instanceof XMLAConnection) {
	    return createXmlaModel(context, olapUnit);
	}
	String mdx = olapUnit.getMdxQuery();
	MondrianConnection conn = (MondrianConnection) clientConn;
	/*
	 * FIXME Need to be able to configure the extensions
	 *
	 * URL url; if (config == null) url = getDefaultConfig(); else url =
	 * pageContext.getServletContext().getResource(config);
	 */
	MondrianModel model = null;
	try {
	    model = (MondrianModel) ModelFactory
		.instance(getDefaultMondrianConfig());
	} catch (Exception e) {
	    throw new JSException(e);
	}

	model.setMdxQuery(mdx);
	model.setConnectProperties(getMondrianConnectProperties(context, conn));
	model.setDynLocale( String.valueOf(LocaleContextHolder.getLocale()) );

	/*
	 * FIXME use of other values?
	 *
	 * mm.setDynresolver(cfg.getDynResolver());
	 * mm.setDynLocale(cfg.getDynLocale()); if
	 * ("false".equalsIgnoreCase(cfg.getConnectionPooling()))
	 * mm.setConnectionPooling(false);
	 * mm.setExternalDataSource(cfg.getExternalDataSource());
	 */
	return model;
    }

    protected URL getDefaultMondrianConfig() {
	return MondrianOlapModelTag.class
	    .getResource("/com/tonbeller/jpivot/mondrian/config.xml");
    }

    protected URL getDefaultXMLAConfig() {
	return XMLA_OlapModelTag.class.getResource("config.xml");
    }

    public OlapModel createXmlaModel(ExecutionContext context, OlapUnit xmlaUnit) {
	String mdx = xmlaUnit.getMdxQuery();
	XMLAConnection xmlaConn = (XMLAConnection) dereference(context,
							       xmlaUnit.getOlapClientConnection());

	URL url;
	/*
	 * if (config == null) url = getClass().getResource("config.xml"); else
	 * url = pageContext.getServletContext().getResource(config);
	 */
	url = getDefaultXMLAConfig();

	// let Digester create a model from config input
	// the config input stream MUST refer to the XMLA_Model class
	// <model class="com.tonbeller.bii.xmla.XMLA_Model"> is required
	Model model;
	try {
	    model = ModelFactory.instance(url);
	} catch (Exception e) {
	    throw new JSException(e);
	}

	if (!(model instanceof XMLA_Model))
	    throw new JSException("jsexception.invalid.class.attribute.for.model.tag", new Object[] {getDefaultXMLAConfig()});

	XMLA_Model xmlaModel = (XMLA_Model) model;

	xmlaModel.setCatalog(xmlaConn.getCatalog());
	xmlaModel.setDataSource(xmlaConn.getDataSource());
	xmlaModel.setMdxQuery(mdx);
	xmlaModel.setID(xmlaConn.getCatalog() + "-" + xmlaUnit.hashCode()); // ???
	xmlaModel.setUri(xmlaConn.getURI());

	// if the xmlaConnection metadata object does not specify
	// username or password, then use the login-in user's credentials
	if (lacksAuthentication(xmlaConn)) {
	    MetadataUserDetails user = getCurrentUserDetails();
	    xmlaModel.setUser(user.getUsername());
	    xmlaModel.setPassword(user.getPassword());
	} else {
	    xmlaModel.setUser(xmlaConn.getUsername());
	    xmlaModel.setPassword(xmlaConn.getPassword());
	}
	log.debug("XMLA USERNAME = " + xmlaModel.getUser());
	log.debug("XMLA PASSWORD = " + xmlaModel.getPassword());


	return xmlaModel;
    }

	protected boolean lacksAuthentication(XMLAConnection xmlaConn) {
		return xmlaConn.getUsername() == null ||
		    xmlaConn.getPassword() == null ||
		    xmlaConn.getUsername().equals("") ||
		    xmlaConn.getPassword().equals("");
	}

    protected MetadataUserDetails getCurrentUserDetails() {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth != null) {
	    return (MetadataUserDetails)auth.getPrincipal();
	}
	return null;
    }

    /*
     * Because of the way the repository works, this version of validate
     * only works if the OlapUnit has already been saved.
     */
    public ValidationResult validate(ExecutionContext context, OlapUnit unit) {
	return validate(context, unit, null, null, null);
    }

    /*
     * if your OlapUnit has not yet been saved as a repository resource,
     * you must call this version of validate and pass in the schema, connection,
     * and datasource.
     */

    public ValidationResult validate(ExecutionContext context,
				     OlapUnit unit,
				     FileResource schema,
				     OlapClientConnection conn,
				     ReportDataSource dataSource) {
	ValidationResultImpl result = new ValidationResultImpl();
	validateMDX(context, result, unit, schema, conn, dataSource);
	// TODO: validate the datasource as well
	return result;
    }

    /*
     * Because of the way the repository works, this version of validateMDX
     * only works if the OlapUnit has already been saved.
     */
    protected void validateMDX(ExecutionContext context,
			       ValidationResultImpl result, OlapUnit unit) {
	validateMDX(context, result, unit, null, null, null);
    }

    protected void validateMDX(ExecutionContext context,
			       ValidationResultImpl result,
			       OlapUnit unit,
			       FileResource schema,
			       OlapClientConnection conn,
			       ReportDataSource dataSource) {
	MondrianConnection resource = null;
	if (conn instanceof MondrianConnection)
	    resource = (MondrianConnection)conn;
	if (resource == null)
	    resource = getConnectionResource(context, unit);
	if (resource == null)
	    return;
	try {
	    mondrian.olap.Connection monConnection
		= getMondrianConnection(context, resource,
					schema, dataSource);
	    monConnection.parseQuery(unit.getMdxQuery());
	} catch (Exception e) {
	    ValidationDetailImpl detail = new ValidationDetailImpl();
	    detail.setValidationClass(OlapUnit.class);
	    detail.setName(unit.getName());
	    detail.setLabel(unit.getLabel());
	    detail.setResult(ValidationResult.STATE_ERROR);
	    detail.setException(e);
	    detail.setMessage(e.getMessage());
	    result.addValidationDetail(detail);
	    log.warn("Validation Failed for Olap Unit: " + unit.getName()
		     + "\n" + e.getStackTrace());
	    log.error(e);
	    e.printStackTrace();
	}

    }

    public MondrianConnection getConnection(ExecutionContext context,
					    String resourceName) {
	return (MondrianConnection) getRepository().getResource(context,
								resourceName);
    }

    /*
     * mondrianConnection
     */
    public MondrianConnection getConnectionResource(ExecutionContext context,
						    OlapUnit unit) {
	Resource clientConn = dereference(context, unit
					  .getOlapClientConnection());
	if (clientConn instanceof MondrianConnection) {
	    return (MondrianConnection) clientConn;
	}
	if (clientConn instanceof XMLAConnection) {
	    // TODO: have to find the matching MondrianXMLADefinition (see
	    // XmlaHandlerImpl)
	}
	return null;
    }

    public mondrian.olap.Connection
	getMondrianConnection(ExecutionContext context,
			      String connResourceName) {
	MondrianConnection conn = getConnection(context, connResourceName);

        if (conn == null) {
            log.error("missing MondrianConnection resource: " + connResourceName);
            throw new JSException("jsexception.mondrian.no.connection.for.resource", new Object[] {connResourceName});
        }
	return getMondrianConnection(context, conn);
    }

    private mondrian.olap.Connection
	getMondrianConnection(ExecutionContext context,
			      MondrianConnection conn) {
	return getMondrianConnection(context, conn, null, null);
    }

    protected mondrian.olap.Connection
	getMondrianConnection(ExecutionContext context,
			      MondrianConnection conn,
			      FileResource schema,
			      ReportDataSource dataSource) {

	Util.PropertyList connectProps = getMondrianConnectProperties(context,
								      conn,
								      schema,
								      dataSource);
	log.debug("connection properties prepared");
	return DriverManager.getConnection(connectProps, null, false);
    }

    public void initializeAndShow(OlapModelProxy omp,
				  String viewUri,
				  OlapModel model,
				  OlapUnit unit)
	throws Exception
    {
	omp.initializeAndShow(viewUri, model);
    }

    // this could be a general repository method too...
    public String getFileResourceData(ExecutionContext context,
					 FileResource file) {
	RepositoryService rep = getRepository();
	StringBuffer fileString = new StringBuffer();
	InputStream data;
	if (file.hasData()) {
	    data = file.getDataStream();
	} else {
	    log.debug("FILE URI STRING = " + file.getURIString());
	    FileResourceData resourceData = rep.getResourceData(context, file
								.getURIString());
	    data = resourceData.getDataStream();
	}
	log.debug("FILE = " + file);

	// use character encoding
	String encoding = getEncodingProvider().getCharacterEncoding();
	String line;
	try {
	    BufferedReader in =
		new BufferedReader(new InputStreamReader(data, encoding));
	    while ((line = in.readLine()) != null) {
	    	fileString.append(line);
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
	return fileString.toString();
    }

    public Util.PropertyList
	getMondrianConnectProperties(ExecutionContext context,
				     MondrianConnection conn) {
	return getMondrianConnectProperties(context, conn, null, null);
    }

    public Util.PropertyList
	getMondrianConnectProperties(ExecutionContext context,
				     MondrianConnection conn,
				     FileResource schema,
				     ReportDataSource dataSource) {

	RepositoryService rep = getRepository();
	// assemble a mondrian connection PropertyList
	if (dataSource == null)
	    dataSource = (ReportDataSource) dereference(context,
							conn.getDataSource());
	if (schema == null) {
	    schema = (FileResource) dereference(context, conn.getSchema());
	}

	Util.PropertyList connectProps = new Util.PropertyList();
	connectProps.put(RolapConnectionProperties.Provider.toString(), "mondrian");

	connectProps.put(RolapConnectionProperties.CatalogName.toString(), conn.getName());
	// writing a DynamicSchemaProcessor looks like the way to update schema
	// on the fly
	connectProps.put(RolapConnectionProperties.DynamicSchemaProcessor.toString(),
			 "NONE");
	connectProps.put(RolapConnectionProperties.CatalogContent.toString(),
			 getFileResourceData(context, schema));

	if (dataSource instanceof JdbcReportDataSource) {
	    JdbcReportDataSource jdbcDataSource
		= (JdbcReportDataSource) dataSource;
	    connectProps.put(RolapConnectionProperties.Jdbc.toString(),
			     jdbcDataSource.getConnectionUrl());
	    String driverClassName = jdbcDataSource.getDriverClass();
	    connectProps.put(RolapConnectionProperties.JdbcDrivers.toString(),
			     driverClassName);

	    try {
		// load the driver- may not have been done, mondrian expects it
		log.info("Loading jdbc driver: " + driverClassName);
		Class.forName(driverClassName);
	    } catch (ClassNotFoundException cnfe) {
		log.error("CANNOT LOAD DRIVER: " + driverClassName);
	    }

	    if (jdbcDataSource.getUsername() != null
		&& jdbcDataSource.getUsername().trim().length() > 0) {
		connectProps.put(RolapConnectionProperties.JdbcUser.toString(),
				 jdbcDataSource.getUsername());
	    }

	    if (jdbcDataSource.getPassword() != null
		&& jdbcDataSource.getPassword().trim().length() > 0) {
		connectProps.put(RolapConnectionProperties.JdbcPassword.toString(),
				 jdbcDataSource.getPassword());
	    }

	} else {
	    // We have a JNDI data source
	    JndiJdbcReportDataSource jndiDataSource = (JndiJdbcReportDataSource) dataSource;

	    String jndiURI = (jndiDataSource.getJndiName() != null &&
			      !jndiDataSource.getJndiName().startsWith("java:")) ?
		"java:comp/env/" : "";
	    jndiURI = jndiURI + jndiDataSource.getJndiName();
	    connectProps.put(RolapConnectionProperties.DataSource.toString(),
			     jndiURI);
	}
	// TODO get from web context and metadata
	// + "RoleXX='California manager';";
	return connectProps;
    }

    /* should something like this be part of the repository api? */
    public Resource dereference(ExecutionContext context, ResourceReference ref) {
	RepositoryService rep = getRepository();
	if (ref.isLocal())
	    return ref.getLocalResource();
	return rep
	    .getResource(context, ref.getReferenceURI());
    }

    /**
     * saveResource creates path of folders as necessary and put the resource in
     * the bottommost folder does not update if the target already exists. maybe
     * this can be added to the RepositoryService API?
     */
    public void saveResource(ExecutionContext context, String path,
			     Resource resource) {
	RepositoryService rep = getRepository();

	// check if the target already exists
	String targetUri = path
	    + (path.endsWith(Folder.SEPARATOR) ? "" : Folder.SEPARATOR)
	    + resource.getName();
	if (rep.resourceExists(context, targetUri)) {
	    return;
	}

	Folder folder = mkdirs(context, path);
	resource.setParentFolder(folder);
	rep.saveResource(context, resource);
    }

    public Folder mkdirs(ExecutionContext context, String path) {
	RepositoryService rep = getRepository();

	// travel down the elements of the path
	String[] splitPath = path.split(Folder.SEPARATOR);
	String folderName = ""; // start with root
	Folder parentFolder = null; // root's parent is null
	Folder folder = rep.getFolder(context, folderName);
	for (int i = 0; i < splitPath.length; i++) {
	    log.debug("Current path element is " + splitPath[i]);
	    if ("".equals(splitPath[i])) {
		continue; // ignore extra slashes
	    }
	    log.debug("Folder name '" + folderName + "' yields folder '"
		      + folder + "'");
	    folderName += ("/" + splitPath[i]);
	    parentFolder = folder; // remember parent
	    folder = rep.getFolder(context, folderName);
	    if (folder == null) {
		folder = new FolderImpl();
		folder.setName(splitPath[i]);
		folder.setLabel(splitPath[i]);
		folder.setDescription(splitPath[i] + " folder");
		folder.setParentFolder(parentFolder);
		rep.saveFolder(context, folder);
	    }
	}
	log.debug("Folder name '" + folderName + "' yields folder '" + folder
		  + "'");
	return folder;
    }

    // PROPERTIES

    private RepositoryService mRepository;

    public RepositoryService getRepository() {
	return mRepository;
    }

    public void setRepository(RepositoryService repository) {
	mRepository = repository;
    }

    private StaticCharacterEncodingProvider encodingProvider;

    /**
     * returns character encoding provided by jaspersoft
     * @return
     */
    public StaticCharacterEncodingProvider getEncodingProvider() {
  	  return encodingProvider;
    }

    /**
     * sets character encoding provided by jaspersoft
     * @param encodingProvider
     */
    public void setEncodingProvider(StaticCharacterEncodingProvider encodingProviderIn) {
  	  encodingProvider = encodingProviderIn;
    }

	public ReportDataSourceService createService(ReportDataSource dataSource) {
		ReportDataSourceService dsService;
		if (dataSource instanceof MondrianConnection) {
			MondrianConnection mondrianConnection = (MondrianConnection) dataSource;
			Connection connection = getMondrianConnection(null, mondrianConnection);
			dsService = new MondrianConnectionDataSourceService(connection);
		} else if (dataSource instanceof XMLAConnection) {
			XMLAConnection xmlaConnection = (XMLAConnection) dataSource;
			if (lacksAuthentication(xmlaConnection)) {
			    User user = getCurrentUserDetails();
				dsService = new XmlaConnectionDataSourceService(xmlaConnection, user);
			} else {
				dsService = new XmlaConnectionDataSourceService(xmlaConnection);
			}
		} else {
			throw new JSException("jsexception.invalid.olap.datasource", new Object[] {dataSource.getClass()});
		}
		return dsService;
	}

}
