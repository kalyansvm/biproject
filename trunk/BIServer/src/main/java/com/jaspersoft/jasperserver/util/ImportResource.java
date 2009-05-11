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
package com.jaspersoft.jasperserver.util;

/**
 * @author tkavanagh
 * @version $Id: ImportResource.java 8408 2007-05-29 23:29:12Z melih $
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.batik.script.Window.GetURLHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Unmarshaller;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.ReportSchedulingInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataSource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;

public class ImportResource {

	private static final Log log = LogFactory.getLog(ImportResource.class);
	private static final String PATH_SEP = "/";
	private static final String LABEL = "_label";
	private static final boolean TOP_LEVEL_OBJECT = true;
	private static final boolean NOT_TOP_LEVEL_OBJECT = false;
	
	private RepositoryService repo;
	private UserAuthorityService auth;
	private ReportSchedulingInternalService mReportScheduler;
	private ExecutionContext context;
	private String catalogPath;
	private String catalogFileName;
	private Reader reader;
	private final String characterEncoding;
	
	private String prependPath;
	private String prependPathNoSlash;
	private boolean usePrependPath;
	
	private boolean failed = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public ImportResource(RepositoryService repo,
			UserAuthorityService auth,
			ReportSchedulingInternalService reportScheduler,
			ExecutionContext context, 
			String catalogPath, 
			String catalogFileName, 
			String prependPath
			) {
		this(
				repo,
				auth,
				reportScheduler,
				context,
				catalogPath,
				catalogFileName,
				prependPath,
				ExportImportUtils.getDefaultCharacterEncoding());
	}

	
	public ImportResource(RepositoryService repo,
			UserAuthorityService auth,
			ReportSchedulingInternalService reportScheduler,
			ExecutionContext context, 
			String catalogPath, 
			String catalogFileName, 
			String prependPath,
			String characterEncoding) {
		
		this.catalogPath = catalogPath;
		this.catalogFileName = catalogFileName;
		this.context = context;
		this.repo = repo;
		this.auth = auth;
		this.mReportScheduler = reportScheduler;
		this.characterEncoding = characterEncoding;
		
        /*
         * Set the prepend path. Allows for importing to a named folder
         * location.
         */
        if (prependPath == null || prependPath.equals(PATH_SEP) || prependPath.equals("")) {
            usePrependPath = false;
            this.prependPath = "";					// set prepend path to empty string
        	log.debug("not using prepend path");
        } else {
        	usePrependPath = true;
            this.prependPath = setPrependPath(prependPath);
            this.prependPathNoSlash = setPrependPathNoSlash(this.prependPath);
            log.debug("using prepend path, path=" + this.prependPath);
        }		
	}
	
	
	/**
	 * @return true if successful, false otherwise
	 */
	public boolean process() {
		log.debug("top of process()");
		
		loadCatalogFile();
		
		try {
		
			Unmarshaller unmarshaller = new Unmarshaller(ExportImportBean.class);
			unmarshaller.setMapping(MappingHelper.getExportImportBeanMapping());
			ExportImportBean bean = (ExportImportBean) unmarshaller.unmarshal(reader);
			
			if (log.isDebugEnabled()) {
				log.debug("Unmarshalled a " + bean.getVersion() + " exported bean");
			}
		
			if (bean.getUserRoleHolder() != null) {
				
				processUsersRoles( bean.getUserRoleHolder() );
			}
			
			if (bean.getResource() != null) {
				
				processResource( bean.getResource() );
			}
			
			if (bean.getReportJobs() != null) {
				
				processReportJobs( bean.getReportJobs());
			}
			
		} catch (Exception e) {
			log.error("ERROR: caught exception unmarshalling catalog, file=" +
					catalogPath + PATH_SEP + ExportResource.CATALOG_FILE_NAME + 
					", exception: " + e.getMessage());
			e.printStackTrace();
			failed = true;
		}
		return !failed;
	}

	
	public void processResource(ResourceBean bean) {
		log.debug("top of processResource()");
		
		try {

			if (bean instanceof FolderBean) {
				
				process( (FolderBean) bean, TOP_LEVEL_OBJECT);
				
			} else if (bean instanceof ReportUnitBean) {
					
				process( (ReportUnitBean) bean, TOP_LEVEL_OBJECT);
					
			} else if (bean instanceof OlapUnitBean) {
								
				process( (OlapUnitBean) bean, TOP_LEVEL_OBJECT);				
							
			} else if (bean instanceof FileResourceBean) {
				
				process( (FileResourceBean) bean, TOP_LEVEL_OBJECT);
				
			} else if (bean instanceof ContentResourceBean) {
				
				process( (ContentResourceBean) bean, TOP_LEVEL_OBJECT);
								
			} else if (bean instanceof DataSourceBean) {
				
				process( (DataSourceBean) bean, TOP_LEVEL_OBJECT);
				
			} else if (bean instanceof OlapClientConnectionBean) {
				
				process( (OlapClientConnectionBean) bean, TOP_LEVEL_OBJECT);

			} else if (bean instanceof MondrianXmlaDefinitionBean) {
				
				process( (MondrianXmlaDefinitionBean) bean, TOP_LEVEL_OBJECT);
								
			} else if (bean instanceof InputControlBean) {
				
				process( (InputControlBean) bean, TOP_LEVEL_OBJECT);

			} else if (bean instanceof DataTypeBean) {
				
				process( (DataTypeBean) bean, TOP_LEVEL_OBJECT);
				
			} else if (bean instanceof ListOfValuesBean) {
				
				process( (ListOfValuesBean) bean, TOP_LEVEL_OBJECT);
				
			} else if (bean instanceof QueryBean) {
				
				process( (QueryBean) bean, TOP_LEVEL_OBJECT);
				
			} else {
				log.error("ERROR: unknown node type");
			}
			
		} catch (Exception e) {
			log.error("ERROR: caught exception processing, uri=" + bean.getUriString() +
					", exception: " + e.getMessage());
			e.printStackTrace();
			failed = true;
		}
	}
	
	
	public void processUsersRoles(UserRoleHolderBean bean) {

		process(bean, NOT_TOP_LEVEL_OBJECT);		
	}
	
	
	public void processReportJobs(ReportJobBean[] beans) {
		
		for (int i = 0; i < beans.length; i++) {
			
			process( beans[i], NOT_TOP_LEVEL_OBJECT);	
		}
	}
	
	
	public Folder process(FolderBean bean, boolean isTopLevel) {
		log.debug("top of process(FolderBean)");

		Folder folder = new FolderImpl();
		
		fillData(folder, bean);
		
		buildParentFolder(folder, bean);

		System.out.println("ImportResource: check and/or save folder, uri=" + folder.getURIString());
						
		handleSaveFolder(folder);
		
		/*
		 * get resources (ie files) in folder
		 */
		if (bean.getResources() != null) {
			
			ResourceBean[] resources = bean.getResources();
			
			for (int i = 0; i < resources.length; i++) {
				
				if (resources[i] instanceof ReportUnitBean) {
					
					ReportUnit res = process( (ReportUnitBean) resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);
					
					handleSaveResource(res);
					
				} else if (resources[i] instanceof OlapUnitBean) {
					
					OlapUnit res = process( (OlapUnitBean) resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);

					handleSaveResource(res);
					
				} else if (resources[i] instanceof FileResourceBean) {
					
					FileResource res = process( (FileResourceBean) resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);

					handleSaveResource(res);
					
				} else if (resources[i] instanceof ContentResourceBean) {
					
					ContentResource res = process( (ContentResourceBean) resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);
					
					handleSaveResource(res);
					
				} else if (resources[i] instanceof DataSourceBean) {
					
					DataSource res = process( (DataSourceBean)resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);

					handleSaveResource(res);

				} else if (resources[i] instanceof OlapClientConnectionBean) {
					
					OlapClientConnection res = process( (OlapClientConnectionBean)resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);

					handleSaveResource(res);

				} else if (resources[i] instanceof MondrianXmlaDefinitionBean) {
					
					MondrianXMLADefinition res = process( (MondrianXmlaDefinitionBean)resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);

					handleSaveResource(res);
					
				} else if (resources[i] instanceof InputControlBean) {
					
					InputControl res = process( (InputControlBean)resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);
					
					handleSaveResource(res);
					
				} else if (resources[i] instanceof DataTypeBean) {
					
					DataType res = process( (DataTypeBean)resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);
					
					handleSaveResource(res);
					
				} else if (resources[i] instanceof ListOfValuesBean) {
					
					ListOfValues res = process( (ListOfValuesBean)resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);
					
					handleSaveResource(res);

				} else if (resources[i] instanceof QueryBean) {
					
					Query res = process( (QueryBean)resources[i], NOT_TOP_LEVEL_OBJECT);
					
					buildParentFolder(res, resources[i]);
					
					handleSaveResource(res);

				} else {
					log.error("ERROR: unknown resource type, class=" + resources[i].getClass());
				}
			}
		}
		
		//OlapClientConnectionBean)
		
		/*
		 * get and recurse into sub-folders
		 */
		if (bean.getFolders() != null) {
			FolderBean[] folders = bean.getFolders();
			
			for (int j = 0; j < folders.length; j++) {
							
				process(folders[j], NOT_TOP_LEVEL_OBJECT);	// recurse into sub-folders
			}
		}
		
		return folder;
	}
	
	
	public ReportUnit process(ReportUnitBean bean, boolean isTopLevel) {		
		log.debug("top of processReportUnit()");

		ReportUnit unit = (ReportUnit) repo.newResource(context, ReportUnit.class);
		
		fillData(bean, unit);

		/*
		 * get datasource
		 */
		if (bean.getDataSource() != null) {
			
			DataSource ds = process( bean.getDataSource(), NOT_TOP_LEVEL_OBJECT);
						
			if (bean.getDataSource().getIsUriReference()) {
				
				unit.setDataSourceReference(ds.getURIString());
				
			} else {
				unit.setDataSource((ReportDataSource) ds);
			}
		}

		/*
		 * get query
		 */
		if (bean.getQuery() != null) {
			Query query = process( bean.getQuery(), NOT_TOP_LEVEL_OBJECT);
			if (bean.getQuery().getIsUriReference()) {
				unit.setQuery(new ResourceReference(query.getURIString()));
			} else {
				unit.setQuery(new ResourceReference(query));
			}
		}
		
		/*
		 * get main report jrxml 
		 */
		if (bean.getMainReport() != null) {

			FileResource repRes = process( (FileResourceBean) bean.getMainReport(), NOT_TOP_LEVEL_OBJECT);
			
			if (bean.getMainReport().getIsUriReference()) {
				
				unit.setMainReportReference(repRes.getURIString());
				
			} else {
				unit.setMainReport(repRes);
			}
		}
		
		/*
		 * get InputControls
		 */
		if (bean.getInputControls() != null) {
			
			InputControlBean[] icArray = bean.getInputControls();
			
			for (int i = 0; i < icArray.length; i++) {
				
				InputControl ic = process( (InputControlBean) icArray[i], NOT_TOP_LEVEL_OBJECT);
				
				if (icArray[i].getIsUriReference()) {
					
					unit.addInputControlReference(ic.getURIString());
					
				} else {
					unit.addInputControl(ic);
				}
			}
		}
		
		/*
		 * get resources (ie FileResources)
		 */
		FileResourceBean[] frArray = bean.getResources();
		
		if (frArray != null) {
			for (int j = 0; j < frArray.length; j++) {

				FileResource res = (FileResource) repo.newResource(context,
						FileResource.class);

				if (frArray[j].getIsReference()) {

					handleFileResourceRef(res, frArray[j]);

				} else { // not a reference (not a link)

					fillData(res, frArray[j]);
				}
				unit.addResource(res);
			}
		}		
		
		if (isTopLevel) {
			
			buildParentFolder(unit, bean);
			
			handleSaveResource(unit);
		}
		return unit;
	}
	

	public OlapUnit process(OlapUnitBean bean, boolean isTopLevel) {		
		log.debug("top of process(OlapUnit)");
		
		OlapUnit unit = (OlapUnit) repo.newResource(context, OlapUnit.class);
		
		fillData(unit, bean);
		
		/*
		 * get OlapClientConnection
		 */
		
		if (bean.getOlapClientConnection() != null) {
			
			OlapClientConnection oc = process( bean.getOlapClientConnection(), NOT_TOP_LEVEL_OBJECT);
						
			if (bean.getOlapClientConnection().getIsUriReference()) {
				
				unit.setOlapClientConnectionReference(oc.getURIString());
				
			} else {
				unit.setOlapClientConnection(oc);
			}
		}
		
		if (isTopLevel) {
			
			buildParentFolder(unit, bean);
			
			handleSaveResource(unit);
		}
		return unit;
	}
	
	
	/*
	 * This works for FileResources. Also, the FileResource that is the 
	 * mainReport contained by a ReportUnit is a special case. There is
	 * logic in this module to handle that special case (isUriReference)
	 */
	public FileResource process(FileResourceBean bean, boolean isTopLevel) {
		log.debug("top of process(FileResouceBean)");

		FileResource res = (FileResource) repo.newResource(context, FileResource.class);
		
		fillData(res, bean);
			
		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(res, bean);

			handleSaveResource(res);		 
		} 
		return res;
	}

	public ContentResource process(ContentResourceBean bean, boolean isTopLevel) {
		log.debug("top of process(ContentResourceBean)");

		ContentResource res = (ContentResource) repo.newResource(context, ContentResource.class);
		
		fillData(res, bean);
			
		/*
		 * handle child ContentResources
		 */
		
		if (bean.getResources() != null) {
			
			ContentResourceBean[] childResources = bean.getResources();
			
			for (int i =0; i < childResources.length; i++) {
				
				ContentResource childRes = process( childResources[i], NOT_TOP_LEVEL_OBJECT);
				
				res.addChildResource(childRes);
			}
		}
		
		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(res, bean);

			handleSaveResource(res);
		} 
		return res;
	}
	

	public DataSource process(DataSourceBean bean, boolean isTopLevel) {
		log.debug("top of process(DataSourceBean)");
		
		ReportDataSource ds = null;

		if (repo.getResource(null, prependPath + bean.getUriString()) == null) {

			if (bean.getDataSourceType().equals(DataSourceBean.DS_TYPE_JDBC)) {
				
				ds = (JdbcReportDataSource) repo.newResource(null, JdbcReportDataSource.class);
				
				fillData( (JdbcDataSourceBean) bean, (JdbcReportDataSource) ds);		
					
			} else if (bean.getDataSourceType().equals(DataSourceBean.DS_TYPE_JNDI_JDBC)) {
				
				ds = (JndiJdbcReportDataSource) repo.newResource(null, JndiJdbcReportDataSource.class);
				
				fillData( (JndiJdbcDataSourceBean) bean, (JndiJdbcReportDataSource) ds);
					
			} else {
				log.error("ERROR: unknown data source type, datasource=" + prependPath + bean.getUriString());
			}	
			
			if (isTopLevel || bean.getIsUriReference()) {
	
				buildParentFolder(ds, bean);

				handleSaveResource(ds);
			} 	

		} else {
			log.warn("WARN: ReportDataSource already exists, skipping creation, using existing, uri=" + 
					prependPath + bean.getUriString());
			
			ds = (ReportDataSource) repo.getResource(context, prependPath + bean.getUriString());
		}
		
		return ds;
	}

	public OlapClientConnection process(OlapClientConnectionBean bean, boolean isTopLevel) {
		log.debug("top of process(OlapClientConnectionBean)");
		
		OlapClientConnection oc = null; 
		
		if (repo.getResource(context, prependPath + bean.getUriString()) == null) {
		
			if (bean.getConnectionType().equals(OlapClientConnectionBean.CONN_TYPE_MONDRIAN)) {
				
				oc = process( (MondrianConnectionBean) bean, NOT_TOP_LEVEL_OBJECT);
				
			} else if (bean.getConnectionType().equals(OlapClientConnectionBean.CONN_TYPE_XMLA)) {

				oc = (XMLAConnection) repo.newResource(null, XMLAConnection.class);
				
				fillData( (XMLAConnection) oc, (XmlaConnectionBean) bean);		
				
			} else {
				log.error("ERROR: unknown data source type, datasource=" + prependPath + bean.getUriString());
			}
			
			if (isTopLevel || bean.getIsUriReference()) {
				
				buildParentFolder(oc, bean);
				
				handleSaveResource(oc);
			} 	

		} else {
			log.warn("WARN: OlapClientConnection already exists, skipping creation, using existing, uri=" + 
					prependPath + bean.getUriString());
			
			oc = (OlapClientConnection) repo.getResource(context, prependPath + bean.getUriString());
		}
				
		return oc;
	}
	
	public MondrianXMLADefinition process(MondrianXmlaDefinitionBean bean, boolean isTopLevel) {
		log.debug("top of process(MondrianXmlaDefinitionBean)");

		MondrianXMLADefinition xDef = (MondrianXMLADefinition) repo.newResource(context, MondrianXMLADefinition.class);
		
		fillData(xDef, bean);
		
		/*
		 * get mondrian connection
		 */
		
		if (bean.getMondrianConnection() != null) {
			
			MondrianConnection mc = process(bean.getMondrianConnection(), NOT_TOP_LEVEL_OBJECT);
			
			if (bean.getMondrianConnection().getIsUriReference()) {
				
				xDef.setMondrianConnectionReference(mc.getURIString());
				
			} else {
				xDef.setMondrianConnection(mc);
			}
		}
		
		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(xDef, bean);
			
			handleSaveResource(xDef);
		}
		return xDef;
	}
	
	
	public MondrianConnection process(MondrianConnectionBean bean, boolean isTopLevel) {	
		log.debug("top of process(MondrianConnectionBean)");
		
		MondrianConnection conn = (MondrianConnection) repo.newResource(context, MondrianConnection.class);
		
		fillData(conn, bean);

		/*
		 * get schema
		 */
		if (bean.getSchema() != null) {
			
			FileResource schema = process(bean.getSchema(), NOT_TOP_LEVEL_OBJECT);	// FileResource
			
			if (bean.getSchema().getIsUriReference()) {
		
				conn.setSchemaReference(schema.getURIString());
			} else {
				conn.setSchema(schema);
			}
		}
		
		/*
		 * get dataSource
		 */
		if (bean.getDataSource() != null) {
			
			DataSource ds = process( bean.getDataSource(), NOT_TOP_LEVEL_OBJECT);
			
			if (bean.getDataSource().getIsUriReference()) {
				
				conn.setDataSourceReference(ds.getURIString());
				
			} else {
				
				conn.setDataSource((ReportDataSource) ds);
			}		
		}
		
		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(conn, bean);
			
			handleSaveResource(conn);
		}
		return conn;
	}
	
	public InputControl process(InputControlBean bean, boolean isTopLevel) {
		log.debug("top of process(InputControlBean)");
		
		InputControl ic = (InputControl) repo.newResource(context, InputControl.class);

		fillData(bean, ic);
			
		/*
		 * get dataType
		 */
		
		if (bean.getDataType() != null) {
			
			DataType dt = process(bean.getDataType(), NOT_TOP_LEVEL_OBJECT);
	
			if (bean.getDataType().getIsUriReference()) {
				
				ic.setDataTypeReference(dt.getURIString());
				
			} else {
				ic.setDataType(dt);	
			}
		}
		
		/*
		 * get listOfValues
		 */
		
		if (bean.getListOfValues() != null) {
		
			ListOfValues lov = process(bean.getListOfValues(), NOT_TOP_LEVEL_OBJECT);
			
			if (bean.getListOfValues().getIsUriReference()) {
				
				ic.setListOfValuesReference(lov.getURIString());
				
			} else {
				ic.setListOfValues(lov);	
			}
		}
		
		/*
		 * get query
		 */
		
		if (bean.getQuery() != null) {
		
			Query query = process(bean.getQuery(), NOT_TOP_LEVEL_OBJECT);
			
			if (bean.getQuery().getIsUriReference()) {
				
				ic.setQueryReference(query.getURIString());
				
			} else {
				ic.setQuery(query);	
			}
		}
		
		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(ic, bean);
			
			handleSaveResource(ic);
		}
		return ic;
	}
	
	public DataType process(DataTypeBean bean, boolean isTopLevel) {
		log.debug("top of process(DataTypeBean)");

		DataType dt = (DataType) repo.newResource(context, DataType.class);
				
		fillData(bean, dt);
		
		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(dt, bean);

			handleSaveResource(dt);				
		}
		return dt;
	}
	
	public ListOfValues process(ListOfValuesBean bean, boolean isTopLevel) {
		log.debug("top of process(ListOfValuesBean)");
		
		ListOfValues lov = null;
		
		if (isTopLevel) {
			lov = (ListOfValues) repo.newResource(context, ListOfValues.class);
		} else {
			lov = new ListOfValuesImpl();
		}
		
		fillData(bean, lov);
		
		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(lov, bean);

			handleSaveResource(lov);
		}
		return lov;
	}
	
	public Query process(QueryBean bean, boolean isTopLevel) {
		log.debug("top of process(QueryBean)");
		
		Query query = null;
		
		if (isTopLevel) {
			query = (Query) repo.newResource(context, Query.class);
		} else {
			query = new QueryImpl();
		}
		
		fillData(bean, query);

		/*
		 * get datasource
		 */
		if (bean.getDataSource() != null) {
			DataSource ds = process( bean.getDataSource(), NOT_TOP_LEVEL_OBJECT);
			if (bean.getDataSource().getIsUriReference()) {
				query.setDataSourceReference(ds.getURIString());
			} else {
				query.setDataSource((ReportDataSource) ds);
			}
		}

		if (isTopLevel || bean.getIsUriReference()) {
			
			buildParentFolder(query, bean);

			handleSaveResource(query);
		}
		return query;
	}
	

	public void process(UserRoleHolderBean bean, boolean isTopLevel) {
		log.debug("top of process(UsersRoleHolderBean)");
				
		Map allRolesMap = null;
		
		/*
		 * Create Roles.
		 */
		if (bean.getRoles() != null) {
		
			createRoles(bean.getRoles());
		} 
		
		/* 
		 * create Users
		 */
		if (bean.getUsers() != null) {

			allRolesMap = getAllRoles();
			log.debug(" ### allRolesMap.size=" + allRolesMap.size());
			
			UserBean[] userBeans = bean.getUsers();
			
			for (int i = 0; i < userBeans.length; i++) {
				
				if (auth.getUser(context, userBeans[i].getUsername()) == null) {
				
					User user = auth.newUser(null);
					user.setUsername(userBeans[i].getUsername());
					user.setPassword(userBeans[i].getPassword());
					user.setFullName(userBeans[i].getFullName());
					user.setEnabled(userBeans[i].getEnabled());
					user.setExternallyDefined(userBeans[i].getExternallyDefined());
				
					RoleBean[] roleBeans = userBeans[i].getRoleSet();
					
					/*
					 * add Roles to User
					 */
					for (int j = 0; j < roleBeans.length; j++) {
					
						if (allRolesMap.containsKey(roleBeans[j].getRoleName())) {
							
							user.addRole( (Role) allRolesMap.get(roleBeans[j].getRoleName()));

						} else {
							log.error("ERROR: no role matching role held by user" + 
									", username=" + userBeans[i].getUsername() + ", rolename=" 
									+ roleBeans[j].getRoleName());
						}
					}
					
					auth.putUser(context, user);		// create user
					
				} else {
					log.warn("WARN: user already exists, skipping, username=" + 
							userBeans[i].getUsername());
				}
			}
		}
	}


	public ReportJob process(ReportJobBean bean, boolean isTopLevel) {
		
		ReportJob job = new ReportJob();
		
		job.setId(bean.getId());
		//job.setVersion(bean.getVersion());
		job.setUsername(bean.getUsername());
		job.setLabel(bean.getLabel());
		job.setDescription(bean.getDescription());
		job.setBaseOutputFilename(bean.getBaseOutputFilename());
		job.setOutputLocale(bean.getOutputLocale());
		
		// following need proper processing
		if (bean.getTrigger() != null) {
			
			try {
				ReportJobTriggerBean tb = bean.getTrigger();  
				ReportJobTrigger trig;
				if (tb instanceof ReportJobSimpleTriggerBean) {
					trig = new ReportJobSimpleTrigger();
					tb.copyTo(trig);
				} else if (tb instanceof ReportJobCalendarTriggerBean) {
					trig = new ReportJobCalendarTrigger();
					tb.copyTo(trig);
				} else {
					String quotedBeanClass = "\"" + tb.getClass().getName() + "\"";
					throw new JSException("jsexception.job.unknown.trigger.bean.class", new Object[] {quotedBeanClass});
				}
				
				job.setTrigger(trig);
				
			} catch (Exception e) {
				log.error("ERROR: parsing a date, exception=" + e.getMessage());
				e.printStackTrace();
				failed = true;
			}
		}
		
		if (bean.getSource() != null) {
			ReportJobSourceBean sb = bean.getSource();
			ReportJobSource src = new ReportJobSource();
			
			src.setReportUnitURI(sb.getReportUnitURI());
			
			Map params = new HashMap();
			if (sb.getParameters() != null) {
				
				ParameterBean[] pBeans = sb.getParameters();
				for (int i = 0; i < pBeans.length; i++) {
					
					params.put(pBeans[i].getKey(), pBeans[i].getValue());
				}
				src.setParametersMap(params);				
			}
			job.setSource(src);
		}
		

		if (bean.getOutputFormats() != null) {
			
			byte[] formats = bean.getOutputFormats();
			
			for (int i = 0; i < formats.length; i++) {
				
				job.addOutputFormat(formats[i]);
			}			
		}
		
		if (bean.getContentRepositoryDestination() != null) {
			ReportJobRepositoryDestinationBean rd = bean.getContentRepositoryDestination();
			ReportJobRepositoryDestination dest = new ReportJobRepositoryDestination();
			
			dest.setId(rd.getId());
			//dest.setVersion(rd.getVersion());
			dest.setFolderURI(rd.getFolderURI());
			dest.setSequentialFilenames(rd.isSequentialFilenames());
			dest.setOverwriteFiles(rd.isOverwriteFiles());
			
			job.setContentRepositoryDestination(dest);
		}
		
		if (bean.getMailNotification() != null) {
			ReportJobMailNotificationBean nBean = bean.getMailNotification();
			ReportJobMailNotification note = new ReportJobMailNotification();
			
			note.setId(nBean.getId());
			//note.setVersion(nBean.getVersion());
			
			String[] toAddrs = nBean.getToAddresses();
			if (toAddrs != null) {
				for (int i = 0; i < toAddrs.length; i++) {
					note.addTo(toAddrs[i]);
				}
			}
			
			String[] ccAddrs = nBean.getCcAddresses();
			if (ccAddrs != null) {
				for (int i = 0; i < ccAddrs.length; i++) {
					note.addCc(ccAddrs[i]);
				}
			}
			
			String[] bccAddrs = nBean.getBccAddresses();
			if (bccAddrs != null) {
				for (int i = 0; i < bccAddrs.length; i++) {
					note.addBcc(bccAddrs[i]);
				}
			}
			if (nBean.getSubject() != null) {
				note.setSubject(nBean.getSubject());
			} else {
				note.setSubject("subject");	// can't be null
			}
			if (nBean.getMessageText() != null) {
				note.setMessageText(nBean.getMessageText());
			} else {
				note.setMessageText("message text");
			}
			note.setResultSendType(nBean.getResultSendType());
			
			job.setMailNotification(note);
		}
		
		mReportScheduler.saveJob(context, job);
		
		return job;
	}
	
	
	/*
	 * Read passed in RoleBeans and create each role in the system.
	 * If role exists, skip it (ie. don't overwrite).
	 * 
	 */
	protected void createRoles(RoleBean[] roleBeans) {
		
		for (int i = 0; i < roleBeans.length; i++) {

			Role role = null; 
			
			if (auth.getRole(context, roleBeans[i].getRoleName()) == null) {
			
				role = auth.newRole(context);
				role.setRoleName(roleBeans[i].getRoleName());
				role.setExternallyDefined(roleBeans[i].getExternallyDefined());
				auth.putRole(context, role);
			} else {
				log.debug("role already exists, not creating, rolename=" + roleBeans[i].getRoleName());
			}
		}
	}
	
	/*
	 * Return all roles in the system as a HashMap.
	 */
	protected Map getAllRoles() {
		
		HashMap roleMap = new HashMap();
		
		List roleList = auth.getRoles(context, new FilterCriteria());

		for (Iterator it = roleList.iterator(); it.hasNext(); ) {
			
			Role role = (Role) it.next();
			
			roleMap.put(role.getRoleName(), role);
		}
		return roleMap;
	}
	
	
	/*
	 * Create the target FileResource and save it. Then fill the data for
	 * the link FileResource (that links to the target).
	 */
	protected void handleFileResourceRef(FileResource res, FileResourceBean bean) {
		
		/*
		 * make target resource, load it's data, and save
		 */
		FileResource targetRes = (FileResource) repo.newResource(context, FileResource.class);
		FileResourceBean targetBean = (FileResourceBean) bean.getLinkTarget();

		fillDataForRef(targetRes, targetBean);
		
		handleSaveResource(targetRes);
		
		/*
		 * fill the link resource
		 */
		fillCommonData(res, bean);
		
		res.setReferenceURI(bean.getReferenceUri());
	}
	
	/*
	 * Save a Resource. Skip save if resource already exists.
	 */
	protected void handleSaveResource(Resource res) {
		
		if (repo.getResource(context, res.getURIString()) == null) {
		
			repo.saveResource(context, res);
		
		} else {
			log.info("target resource already exists, leaving unchanged, uri=" + res.getURIString());
		}		
	}
	

	/*
	 * Save a Folder. Skip save if folder already exists.
	 */	
	protected void handleSaveFolder(Folder folder) {

		if (repo.getFolder(context, folder.getURIString()) == null) {
		
			repo.saveFolder(context, folder);
			
		} else {
			log.info("folder already exists, leaving unchanged, uri=" + folder.getURIString());
		}
	}
	
	protected void fillCommonData(Resource res, ResourceBean bean) {
		
		res.setName(bean.getName());
		res.setLabel(bean.getLabel());
		res.setDescription(bean.getDescription());
	}
	
	
	protected void fillData(Folder folder, FolderBean bean) {
		
		fillCommonData(folder, bean);
	}
	
	
	protected void fillData(ReportUnitBean bean, ReportUnit unit) {

		fillCommonData(unit, bean);
	}
	
	protected void fillData(OlapUnit unit, OlapUnitBean bean) {

		fillCommonData(unit, bean);

		unit.setMdxQuery(bean.getMdxQuery());
	}
	
	protected void fillData(JdbcDataSourceBean bean, JdbcReportDataSource ds) {
		
		fillCommonData(ds, bean);
		
		ds.setDriverClass(bean.getDriverClass());
		ds.setConnectionUrl(bean.getConnectionUrl());
		ds.setUsername(bean.getUsername());
		ds.setPassword(bean.getPassword());
	}
	
	protected void fillData(JndiJdbcDataSourceBean bean, JndiJdbcReportDataSource ds) {
		
		fillCommonData(ds, bean);
		
		ds.setJndiName(bean.getJndiName());		
	}
	
	protected void fillData(MondrianConnection conn, MondrianConnectionBean bean) {
		
		fillCommonData(conn, bean);
	}
	
	protected void fillData(XMLAConnection conn, XmlaConnectionBean bean) {
		
		fillCommonData(conn, bean);
		
		conn.setURI(bean.getUri());
		conn.setDataSource(bean.getDataSource());
		conn.setCatalog(bean.getCatalog());
		conn.setUsername(bean.getUsername());
		conn.setPassword(bean.getPassword());
	}
		
	protected void fillData(MondrianXMLADefinition mx, MondrianXmlaDefinitionBean bean) {
		
		fillCommonData(mx, bean);
		
		mx.setCatalog(bean.getCatalog());
	}
	
	protected void fillData(FileResource fr, FileResourceBean bean) {

		fillCommonData(fr, bean);
		
		fr.setFileType(bean.getFileType());
		
		/* 
		 * load fileResource data. Full path includes catalog folder
		 */ 
		String fullPath = catalogPath + bean.getParentFolder() + PATH_SEP + bean.getName();

		fr.readData( getBinaryData(fullPath) );
	}
	
	protected void fillData(ContentResource res, ContentResourceBean bean) {

		fillCommonData(res, bean);
		
		res.setFileType(bean.getFileType());
		
		/* 
		 * load ContentResource data. Full path includes catalog folder
		 */ 
		String fullPath = catalogPath + bean.getParentFolder() + PATH_SEP + bean.getName();

		res.readData( getBinaryData(fullPath) );
	}
	
	protected void fillDataForRef(FileResource targetRes, FileResourceBean targetBean) {
		
		fillCommonData(targetRes, targetBean);

		targetRes.setFileType(targetBean.getFileType());
		
		buildRepoFolderPath(context, prependPath + targetBean.getParentFolder());
		targetRes.setParentFolder(prependPath + targetBean.getParentFolder());
		
		String fullPath = 
			catalogPath + targetBean.getParentFolder() + PATH_SEP + targetBean.getName();
		
		targetRes.readData( getBinaryData(fullPath) );
	}
	

	protected void fillData(DataTypeBean bean, DataType dt) {
		
		fillCommonData(dt, bean);
		
		dt.setType(bean.getType());
		dt.setMaxLength(bean.getMaxLength());
		dt.setDecimals(bean.getDecimals());
		dt.setRegularExpr(bean.getRegularExpr());
		dt.setMinValue(bean.getMinValue());
		dt.setMaxValue(bean.getMaxValue());
		dt.setStrictMin(bean.getIsStrictMin());
		dt.setStrictMax(bean.getIsStrictMax());
	}
	
	protected void fillData(ListOfValuesBean bean, ListOfValues lov) {
		
		fillCommonData(lov, bean);

		if (bean.getValues() != null) {
			
			ListOfValuesItemBean[] items = bean.getValues();
			
			for (int i = 0; i < items.length; i++) {
				
				ListOfValuesItem lovi = new ListOfValuesItemImpl(); // no setter so must loop through
				lovi.setLabel(items[i].getLabel());
				lovi.setValue(items[i].getValue());
				lov.addValue(lovi);
			}	
		}
	}
	
	protected void fillData(QueryBean bean, Query query) {

		fillCommonData(query, bean);

		String queryLanguage = bean.getLanguage();
		if (queryLanguage == null) {//FIXME handle backward compat differently?
			queryLanguage = QueryBean.DEFAULT_LANGUAGE;
		}
		query.setLanguage(queryLanguage);
		query.setSql(bean.getSql());
	}
	
	protected void fillData(InputControlBean bean, InputControl ic) {
		
		fillCommonData(ic, bean);
		
		ic.setType(bean.getType());
		ic.setMandatory(bean.getIsMandatory());
		ic.setReadOnly(bean.getIsReadOnly());
		
		if (bean.getQueryVisibleColumns() != null) {
			String[] visCols = bean.getQueryVisibleColumns();
			
			for (int i = 0; i < visCols.length; i++) {	// there is no setter must loop through
				ic.addQueryVisibleColumn(visCols[i]);
			}
		}
		
		ic.setQueryValueColumn(bean.getQueryValueColumn());
		ic.setDefaultValue(bean.getDefaultValue());
		
		if (bean.getDefaultValues() != null) {		// convert from array to List
			
			List defValsList = new ArrayList();
			String[] defVals = bean.getDefaultValues();
			
			for (int i = 0; i < defVals.length; i++) {
				defValsList.add(defVals[i]);
			}
			ic.setDefaultValues(defValsList);
		}
	}
	
	protected FileInputStream getBinaryData(String fullPath) {

		FileInputStream input = null;
		
		try {
			input = new FileInputStream(fullPath);
			
		    log.debug("file=" + fullPath + ", fileInputStream.available()=" + input.available());
		    
		} catch (Exception e) {
			log.error("ERROR: caught exception opening file, file=" 
					+ fullPath + ", e=" + e.getMessage());
			e.printStackTrace();
			failed = true;
		}	
		return input;
	}
	

	protected void loadCatalogFile() {
		
		try {
			this.reader = new InputStreamReader(new FileInputStream(new File(catalogPath, catalogFileName)), characterEncoding);
			
		} catch (Exception e) {
			log.error("ERROR: problem opening catalog file, e: " + e.getMessage());
			e.printStackTrace();
			failed = true;
		}
	}
	
	
	protected void buildParentFolder(Resource res, ResourceBean bean) {
		
		/*
		 * Check for case of root folder
		 */
		
		if (bean.getUriString().equals("/") && bean.getParentFolder() == null) {
			
			if (prependPath == null || prependPath.equals("")) {	// if no prependPath
				
				buildRepoFolderPath(context, bean.getUriString());	// parentFolder null for root folder
				
			} else {
				
				res.setName(prependPathNoSlash);					// ie "myNewDir"
				res.setParentFolder("/");
			}
			
		} else {
		
			/*
			 * account for a parent folder of "/". If you prepend a dir name
			 * to a parent folder of "/" you would end up with "/myNewDir/"
			 * which is invalid. 
			 */

			if (bean.getParentFolder().equals("/") && usePrependPath) {
				
				buildRepoFolderPath(context, prependPath);							// ie "/myNewDir"
				res.setParentFolder(prependPath);
				
			} else {
				
				buildRepoFolderPath(context, prependPath + bean.getParentFolder());	// ie "/myNewDir" + "/images"				
				res.setParentFolder(prependPath + bean.getParentFolder());			
			}
		}
	}
	
	
    /* 
     * Ensure that a "prepend" uri has a slash "/" 
     * at the beginning.
     */
    protected String setPrependPath(String inPath) {
        
        if (inPath.indexOf('/') != 0) { 
            return PATH_SEP + inPath;
        } else {
            return inPath;
        }
    }
  	

    /*
     * Return a String that does not have a slash at the 
     * beginning. In: "/myNewDir"  out: "myNewDir"
     */
    protected String setPrependPathNoSlash(String inPath) {
        
        if (inPath.indexOf('/') != 0) { 
            return inPath;
        } else {
            return inPath.substring(1);
        }
    }
    
    
    /*
     * todo: make this logic part of repository api
	 * Build the the uri folder structure that a resource needs in order
	 * to be able to save it.
	 * 
	 * Each resource has a parent folder path associated with it. Each folder
	 * referenced in the parent folder uri path must exist or be created  
	 * in the repository before the resource can be saved.
	 * 
     */
	private Folder buildRepoFolderPath(ExecutionContext context, String path) {
		
		//System.out.println("buildRepoFolderPath: build path=" + path);
		log.debug("buildRepoFolderPath: build path=" + path);
		
		// travel down the elements of the path
		String[] splitPath = path.split(Folder.SEPARATOR);
		String folderName = ""; // start with root
		Folder parentFolder = null; // root's parent is null
		Folder folder = repo.getFolder(context, folderName);
		
		for (int i = 0; i < splitPath.length; i++) {
		    //log.debug("Current path element is, name=" + splitPath[i]);
			if ("".equals(splitPath[i])) {
				//log.debug("Skipping this element, name=" + splitPath[i]);
				continue; // ignore extra slashes
			}
			
			folderName += ("/" + splitPath[i]);
			parentFolder = folder; // remember parent
			folder = repo.getFolder(context, folderName);
			if (folder == null) {
				folder = new FolderImpl();
				folder.setName(splitPath[i]);
				folder.setLabel(splitPath[i]);
				folder.setDescription(splitPath[i] + " folder");
				folder.setParentFolder(parentFolder);
				repo.saveFolder(context, folder);
		    }
		}
		
		return folder;
	}
	
}
