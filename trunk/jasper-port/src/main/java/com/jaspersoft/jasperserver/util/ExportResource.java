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
 * @version $Id: ExportResource.java 8408 2007-05-29 23:29:12Z melih $
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.Marshaller;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataSource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
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

/**
 * This module handles the exporting of metadata from the JasperServer
 * repository to an XML file. This XML file and the binary data that is
 * written to disk is used to import to a target repository.
 * 
 * Users can specify which resources in the repository are desired for export.
 * For example, this can be a FileResource (such as an image, jrxml, etc), a
 * ReportUnit, a DataSource, a Folder, etc. If the exported resource is a folder,
 * all of the files and folders will be recursively exported.  
 * 
 * The attributes of the resource are written to a "bean" representation class.
 * This class is processed (unmarshalled) by the Castor library to turn it 
 * directly into XML.
 * 
 * This process is very straightforward for an object such as a DataSource which
 * does not contain any binary information. However, for a FileResource such as 
 * an image, a jar, or even a JRXML, this module must manage the writing out of this
 * binary (or in the case of JRXML, text data) information to disk in order to save 
 * it. Binary data is written out to disk and saved under a folder/directory structure
 * that matches the repository URI structure specified by the uriString member variable
 * for each resource. 
 * 
 * A typical export structure would look like the following:
 * 
 *    ji-catalog/ji-catalog.xml			- xml file describing all exported resources
 *              /dir1/dir2/myImage		- data on disk
 *              /dir1/dir2/myJrxml		- data on disk
 *              /dir1/dir2/myClassJar	- data on disk
 * 
 * There is a heirarchy of bean objects that can hold each of the exported resources.
 * The highest level bean object is ExportImportBean. 
 * 
 */

public class ExportResource {

	private static final Log log = LogFactory.getLog(ExportResource.class);

	public static final String PATH_SEP = "/";
	
	public static final String CATALOG_DIR_NAME = "target/ji-catalog";
	public static final String CATALOG_FILE_NAME = "ji-catalog.xml";
	
	public static final String PRODUCT_VERSION_1_0 = "1.0";
	public static final String PRODUCT_VERSION_0_9_2 = "0.9.2";
	public static final String PRODUCT_VERSION_1_1_0 = "1.1.0";
	public static final String PRODUCT_VERSION = PRODUCT_VERSION_1_1_0;
	
	public static final boolean TOP_LEVEL_OBJECT = true;
	public static final boolean NOT_TOP_LEVEL_OBJECT = false;
	
	private RepositoryService mRepo;
	private UserAuthorityService mAuth;
	private ReportSchedulingService mReportScheduler;
	private ExecutionContext mContext;
	private String mStartUri;
	private boolean mProcessUsersRoles;
	private String[] mUserNames;
	private String[] mRoleNames;
	private boolean mProcessReportJobs;
	private String[] mReportJobUnitNames;
	private String mCatalogDirName;
	private String mCatalogFileName;
	private final String mCharacterEncoding;
	
	private FilterCriteria filterCriteria;
	
	/*
	 * ExportImportBean it the top level data holder
	 * "bean" object. It is used for marshalling and 
	 * unmarshalling from java objects to XML and back.
	 * 
	 * It holds child beans such as: 
	 * 
	 *   ResourceBean
	 *   UserRoleHolderBean,
	 *   ReportJobBeans
	 *   etc.
	 */
	private ExportImportBean mExportImportBean;
	
	/*
	 * Processing operation indicators 
	 */
	boolean mProcessUserRoleOnly = false;
	boolean mProcessUriOnly = false;
	boolean mProcessReportJobsOnly = false;
	boolean mProcessAll = false;
	boolean mProcessNothing = false;

	/*
	 * Processing operation indicators for Users/Roles
	 */
	private boolean mIsAllUsersRoles = false;
	private boolean mIsNamedUsers = false;
	private boolean mIsNamedRoles = false;
	private boolean mIsNamedUsersRoles = false;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public ExportResource(RepositoryService repo,
			UserAuthorityService userAuthService,
			ReportSchedulingService reportSchedulingService,
			ExecutionContext context, 
			String startUri, 
			boolean processUsersRoles,
			String[] userNames,
			String[] roleNames,
			boolean processReportJobs,
			String[] reportJobUnitNames,
			String catalogDirName, 
			String catalogFileName
			) {
		this(
				repo,
				userAuthService, 
				reportSchedulingService,
				context,
				startUri,
				processUsersRoles,
				userNames,
				roleNames,
				processReportJobs,
				reportJobUnitNames,
				catalogDirName,
				catalogFileName,
				ExportImportUtils.getDefaultCharacterEncoding()
				);
	}

	/**
	 * Constructor
	 * 
	 * @param repo
	 * @param userAuthService
	 * @param reportSchedulingService
	 * @param context
	 * @param startUri
	 * @param processUsersRoles
	 * @param userNames
	 * @param roleNames
	 * @param processReportJobs
	 * @param reportJobUnitNames
	 * @param catalogDirName
	 * @param catalogFile
	 */
	public ExportResource(RepositoryService repo,
			UserAuthorityService userAuthService,
			ReportSchedulingService reportSchedulingService,
			ExecutionContext context, 
			String startUri, 
			boolean processUsersRoles,
			String[] userNames,
			String[] roleNames,
			boolean processReportJobs,
			String[] reportJobUnitNames,
			String catalogDirName, 
			String catalogFileName,
			String characterEncoding) {
	
		this.mRepo = repo;
		this.mAuth = userAuthService;
		this.mReportScheduler = reportSchedulingService;
		this.mContext = context;
		this.mStartUri = startUri;
		this.mProcessUsersRoles = processUsersRoles;
		this.mUserNames = userNames;
		this.mRoleNames = roleNames;
		this.mProcessReportJobs = processReportJobs;
		this.mReportJobUnitNames = reportJobUnitNames;
		this.mCatalogDirName = catalogDirName;
		this.mCatalogFileName = catalogFileName;
		this.mCharacterEncoding = characterEncoding;
		
		this.filterCriteria = new FilterCriteria();	// not currently used for search
	}


	/*
	 * Check the processing input values to see what operations
	 * are being requested.
	 * 
	 * In the 1.0 system there are three main processing types:
	 * 
	 * 		- All resources that implement the Resource interface
	 * 		  (ie repository resources)
	 * 
	 * 		- Users, Roles (and related objects)
	 * 
	 * 		- ReportJob Scheduling related objects
	 * 
	 * There are separate processing/logic trees for each of these main types.
	 */
	protected void checkForProcessing() {		

		boolean processUri = false;
		
		if (mStartUri == null || mStartUri.length() == 0) {
			processUri = false;
		} else {
			processUri = true;
		}

		if (processUri && mProcessUsersRoles) {
			mProcessAll = true;
		} else if (processUri && !mProcessUsersRoles) {
			mProcessUriOnly = true;
		} else if (!processUri && mProcessUsersRoles) {
			mProcessUserRoleOnly = true;
		} else {
			
			// We will include ReportJobs if we have processAll
			// so, just check for job processing only operation
			if (!processUri && !mProcessUsersRoles && mProcessReportJobs) {
				mProcessReportJobsOnly = true;
				
			} else {	
				mProcessNothing = true;
				log.error("ERROR: Nothing to process. Check for valid inputs.");
			}
		}
		
		log.warn("----- Processing Values:");
		log.warn("        mProcessAll=" + mProcessAll);
		log.warn("        mProcessUriOnly=" + mProcessUriOnly);
		log.warn("        mProcessUserRoleOnly=" + mProcessUserRoleOnly);
		log.warn("        mProcessReportJobsOnly=" + mProcessReportJobsOnly);
		log.warn("        mProcessNothing=" + mProcessNothing);
		
		System.out.println("----- Processing Values:");
		System.out.println("        mProcessAll=" + mProcessAll);
		System.out.println("        mProcessUriOnly=" + mProcessUriOnly);
		System.out.println("        mProcessUserRoleOnly=" + mProcessUserRoleOnly);
		System.out.println("        mProcessReportJobsOnly=" + mProcessReportJobsOnly);
		System.out.println("        mProcessNothing=" + mProcessNothing);
	}
	
	/*
	 * This is the top level processing method.
	 * 
	 */
	public void process() {
		System.out.println("ExportResource: top of process()");
		log.debug("top of process()");
		
		mExportImportBean = new ExportImportBean();
		mExportImportBean.setVersion(PRODUCT_VERSION);
		
		checkForProcessing();
		
		if (mProcessUriOnly) {
			
			processUri();
			
		} else if (mProcessUserRoleOnly) {
			
			processUsersRoles();
			
		} else if (mProcessReportJobsOnly) {
			
			processReportJobs(TOP_LEVEL_OBJECT);
			
		} else if (mProcessAll) {
			
			processUsersRoles();
			
			processReportJobs(TOP_LEVEL_OBJECT);
			
			processUri();
			
		} else if (mProcessNothing) {
			// put up usage messsage
			log.warn("WARN: Nothing to process. Check for valid inputs");
		} else {
			log.error("ERROR: Unknown processing operation. Check for valid inputs");
		}
	}
	
	/**
	 * Main processing routine for all repository resources that inherit
	 * from the Resource interface (and are handled by the RepositoryService).
	 * 
	 */
	public void processUri() {
		
		try {
			/*
			 * Figure out if we have a resource or a folder. The Repository
			 * Service handles folders and other resources a bit differently
			 * (ie with different methods).
			 */
			
			Resource res =  mRepo.getResource(null, mStartUri);
			
			if (res == null) {		// not a resource, must be a folder
				
				Folder folder = mRepo.getFolder(null, mStartUri);
				
				if (folder != null) {
					
					process(folder, TOP_LEVEL_OBJECT);
					
				} else {
					throw new Exception("unknown resource uri, uri=" + mStartUri); 
				}				
			} else {
				
				if (res instanceof ReportUnit) {
					process( (ReportUnit) res, TOP_LEVEL_OBJECT);

				} else if (res instanceof OlapUnit) {
					process( (OlapUnit) res, TOP_LEVEL_OBJECT);
					
				} else if (res instanceof FileResource) {
					process( (FileResource) res, TOP_LEVEL_OBJECT);

				} else if (res instanceof ContentResource) {
					process( (ContentResource) res, TOP_LEVEL_OBJECT);
						
				} else if (res instanceof DataSource) {
					process( (DataSource) res, TOP_LEVEL_OBJECT);
					
				} else if (res instanceof OlapClientConnection) {
					process( (OlapClientConnection) res, TOP_LEVEL_OBJECT);

				} else if (res instanceof MondrianXMLADefinition) {
					process( (MondrianXMLADefinition) res, TOP_LEVEL_OBJECT);
					
				} else if (res instanceof InputControl) {
					process( (InputControl) res, TOP_LEVEL_OBJECT);

				} else if (res instanceof DataType) {
					process( (DataType) res, TOP_LEVEL_OBJECT);
					
				} else if (res instanceof ListOfValues) {
					process( (ListOfValues) res, TOP_LEVEL_OBJECT);
						
				} else if (res instanceof Query) {
					process( (Query) res, TOP_LEVEL_OBJECT);
										
				} else {
					throw new Exception("Unhandled resourceType, resourceType=" + res.getResourceType());
				}
			}
		} catch (Exception e) {
			log.error(" caught exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Main processing routine for Users and Roles.
	 * 
	 * Users and Roles are handled by the UserAuthorityService (not RepositoryService).
	 * They do not inherit from the Resource interface and do not have URIs associated
	 * with them. However, they are stored in the repository as are any JasperIntelligence
	 * objects.
	 *
	 * Processing of Users and Roles has four operations:
	 * 	1) if usernames and rolenames are empty, get all users and roles
	 *     in the system
	 *	2) if usernames array has values, export all of these names and
	 *     keep information on any roles they reference
	 *  3) if rolenames array has values *and* usernames does not have values, 
	 *     export all of these roles and include all users that reference these 
	 *     roles (but don't include any additional roles that users reference)        
	 * 	4) if usernames and rolesnames has values then export the users and export
	 *     the roles.
	 * 
	 * @return
	 */
	public void processUsersRoles() {
		
		checkForUserRoleProcessing();
		
		UserRoleHolderBean bean = null;
		
		if (mIsAllUsersRoles) {
			
			bean = processAllUsersRoles(TOP_LEVEL_OBJECT);
			
		} else if (mIsNamedUsers) {
			
			bean = processNamedUsers(mUserNames, TOP_LEVEL_OBJECT);
			
		} else if (mIsNamedRoles) {
			
			bean = processNamedRoles(mRoleNames, TOP_LEVEL_OBJECT);
			
		} else if (mIsNamedUsersRoles) {
			
			bean = processNamedUsersRoles(mUserNames, mRoleNames, TOP_LEVEL_OBJECT);
			
		} else {
			log.error("ERROR: unknown user-role operation type");
		}
		
		mExportImportBean.setUserRoleHolder(bean);
		
		if (mProcessUserRoleOnly) {
			
			writeCatalogFile(mExportImportBean);
		}
	}

	
	/**
	 * Main processing routine for Report Jobs (ie scheduled reports).
	 * 
	 * This routine processes all jobs for reportUris that are specified via a 
	 * constructor parameter.
	 * 
	 */
	public void processReportJobs(boolean isTopLevel) {
		
		if (mReportJobUnitNames != null) {
			
			List reportJobBeanList = new ArrayList();
			
			for (int i = 0; i < mReportJobUnitNames.length; i++) {

				List jobs = mReportScheduler.getScheduledJobs(mContext, mReportJobUnitNames[i]);
				
				if (jobs != null && !jobs.isEmpty()) {
					for (Iterator it = jobs.iterator(); it.hasNext();) {

						ReportJobSummary sum = (ReportJobSummary) it.next();
						ReportJob job = mReportScheduler.getScheduledJob(mContext, sum.getId());

						reportJobBeanList.add(process(job, NOT_TOP_LEVEL_OBJECT));
					}
				}
			}
			
			ReportJobBean[] reportJobs = null;
			
			if (reportJobBeanList != null && reportJobBeanList.size() > 0) {
			
				reportJobs = new  ReportJobBean[reportJobBeanList.size()];
				
				int j = 0;
				for (Iterator it2 = reportJobBeanList.iterator(); it2.hasNext(); j++) {
					
					reportJobs[j] = (ReportJobBean) it2.next();
				}			
			}
			
			mExportImportBean.setReportJobs(reportJobs);
			
			if (mProcessReportJobsOnly) {
				
				writeCatalogFile(mExportImportBean);
			}
		}
	}
	
	
	public FolderBean process(Folder topFolder, boolean isTopLevel) {

		FolderBean topFolderBean = new FolderBean();
		
		fillCommonBeanFields(topFolderBean, topFolder);
		
		/*
		 * process the files (resources) in the folder
		 */
		
		ResourceBean[] resourceBeans = handleResourcesInFolder(topFolder);
		
		topFolderBean.setResources(resourceBeans);
		
		/*
		 * process the subfolders in the folder
		 */
			
		/* 
		 * recurse subfolders
		 */
		List subFolders = mRepo.getSubFolders(null, topFolder.getURIString());
		FolderBean[] subFolderBeans = new FolderBean[subFolders.size()]; 
		
		int i = 0;
		for (Iterator it = subFolders.iterator(); it.hasNext(); i++) {
			
			subFolderBeans[i] = new FolderBean();
			processFolder( (Folder) it.next(), subFolderBeans[i]);
		}
		topFolderBean.setFolders(subFolderBeans);

		/*
		 * write everything to disk
		 */
		if (isTopLevel) {
			mExportImportBean.setResource(topFolderBean);
			writeCatalogFile(mExportImportBean);
			writeCatalogData(topFolderBean);
		}
		
		return topFolderBean;
	}
	
	// todo: see about removing this method
	public void processFolder(Folder folder, FolderBean bean) {
		
		fillCommonBeanFields(bean, folder);

		ResourceBean[] resourceBeans = handleResourcesInFolder(folder);		
		bean.setResources(resourceBeans);

		List subFolders = mRepo.getSubFolders(null, folder.getURIString());
		FolderBean[] subFolderBeans = new FolderBean[subFolders.size()]; 
		
		int i = 0;
		for (Iterator it = subFolders.iterator(); it.hasNext(); i++) {
			
			subFolderBeans[i] = new FolderBean();
			processFolder( (Folder) it.next(), subFolderBeans[i]);
		}
		bean.setFolders(subFolderBeans);
	}
	
	
	public FileResourceBean process(FileResource fileResource, boolean isTopLevel) {
		
		FileResourceBean bean = new FileResourceBean();

		fillBean(bean, fileResource);
		
		if (fileResource.isReference()) {
			
			/*
			 * If isReference is true, then the FileResource is a "link" to 
			 * a target FileResource. We will build the target (linked to)
			 * FileResourceBean and save it in the linkTarget member variable.
			 * referenceURI points to the target object.
			 * 
			 * Also, a "link" FileResource (in the repository) does not a
			 * fileType setting.
			 */
			
			bean.setIsReference(fileResource.isReference());
			bean.setReferenceUri(fileResource.getReferenceURI());
			
			FileResourceBean targetBean = 
				process((FileResource) mRepo.getResource(mContext, fileResource.getReferenceURI()), 
						NOT_TOP_LEVEL_OBJECT);
			
			bean.setLinkTarget(targetBean);
			
		} else {
			
			bean.setFileType(fileResource.getFileType());
		}
				
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
			writeCatalogData(bean);
		}
		
		return bean;
	}


	/* 
	 * ContentResource java class has a different implementation than the FileResource
	 * java class even though the functionality is very similar. Thus, creating new 
	 * method to handle ContentResource.
	 */
	public ContentResourceBean process(ContentResource contentResource, boolean isTopLevel) {
		log.debug("top of process(ContentResource)");
		
		ContentResourceBean bean = new ContentResourceBean();

		fillBean(bean, contentResource);
		
		if (contentResource.isReference()) {
			
			/*
			 * If isReference is true, then the ContentResource is a "link" to 
			 * a target ContentResource. We will build the target (linked to)
			 * ContentResourceBean and save it in the linkTarget member variable.
			 * referenceURI points to the target object.
			 * 
			 * Also, a "link" ContentResource (in the repository) does not have a
			 * fileType setting.
			 */
			
			bean.setReferenceUri(contentResource.getReferenceURI());
			
			ContentResourceBean targetBean = 
				process((ContentResource) mRepo.getResource(mContext, contentResource.getReferenceURI()), 
						NOT_TOP_LEVEL_OBJECT);
			
			bean.setLinkTarget(targetBean);
			
			bean.setFileType(contentResource.getFileType());
			
		} else {
			
			bean.setFileType(contentResource.getFileType());
		}
			
		/*
		 * ContentResource can hold a list of ContentResources. This is typically
		 * an html parent that has a list of child images.
		 * 
		 * NOTE: assuming that the children are of type ContentResource
		 */
		if (contentResource.getResources() != null) {
			
			List resources = contentResource.getResources();
			ContentResourceBean[] childResources = new ContentResourceBean[resources.size()];
			
			int i = 0;
			for (Iterator it = resources.iterator(); it.hasNext(); i++) {
				childResources[i] = process(  (ContentResource) it.next(), NOT_TOP_LEVEL_OBJECT);
			}
			
			bean.setResources(  childResources );
		}
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
			writeCatalogData(bean);
		}
		
		return bean;
	}

		
	// doc: because of the way that dataSource and mainReport are handled (by
	//      being stored as a ReferenceResource - it's not necessary to 
	//      store a whole separate object (the target object). Instead,
	//      all our uri/parentFolder info is correct whether it's a 
	//      reference or stored directly in the reportUnit object...
	// isReference is a "traditional" link style
	// isUriReference  ### (change to isRUReference?) - is link within
	//                 context of RU only
	//
	// doc: so processing mainReport may be confusing... ie. it's both
	//      process mainReport in combo way of ds and fileResource

	public ReportUnitBean process(ReportUnit reportUnit, boolean isTopLevel) {
		
		ReportUnitBean bean = new ReportUnitBean();
		
		fillCommonBeanFields(bean, reportUnit);

		/*
		 * get data source
		 */
		if(reportUnit.getDataSource() != null) {
			
			Resource dataSource = dereference(null, reportUnit.getDataSource());
						
			DataSourceBean dsBean = process( (DataSource) dataSource, NOT_TOP_LEVEL_OBJECT);
			
			/*
			 * check whether it's local to the report unit or not
			 */	
			if (reportUnit.getDataSource().isLocal()) {
				
				dsBean.setIsUriReference(false);
			} else {
				dsBean.setIsUriReference(true);
			}
					
			bean.setDataSource(dsBean);
		}

		/*
		 * get query
		 */
		if(reportUnit.getQuery() != null) {
			Query query = (Query) dereference(null, reportUnit.getQuery());
			QueryBean queryBean = process(query, NOT_TOP_LEVEL_OBJECT);

			/*
			 * check whether it's local to the report unit or not
			 */	
			if (reportUnit.getQuery().isLocal()) {
				queryBean.setIsUriReference(false);
			} else {
				queryBean.setIsUriReference(true);
			}

			bean.setQuery(queryBean);
		}
		
		/* 
		 * get main jrxml
		 */
		if (reportUnit.getMainReport() != null) {
			
			FileResource reportRes = (FileResource) dereference(null, reportUnit.getMainReport());
			
			FileResourceBean reportBean = process(reportRes, NOT_TOP_LEVEL_OBJECT);	
			
			if (reportUnit.getMainReport().isLocal()) {
				reportBean.setIsUriReference(false);
			} else {
				reportBean.setIsUriReference(true);
			}
			
			bean.setMainReport(reportBean);
		}
		
		/*
		 * get input controls
		 */
		if (reportUnit.getInputControls() != null) {
			
			InputControlBean[] inputControlBeans = handleInputControls(reportUnit, NOT_TOP_LEVEL_OBJECT);
							
			bean.setInputControls(inputControlBeans);
		}
		
		/*
		 * get resources (ie FileResources)
		 */
		
		if (reportUnit.getResources() != null) {
			List resources = reportUnit.getResources();
	
			FileResourceBean[] resourcesArray = new FileResourceBean[resources.size()];
			int i = 0;
			for (Iterator it = resources.iterator(); it.hasNext(); i++) {
				
				FileResource res = (FileResource) dereference(null, (ResourceReference) it.next());
				
				if (res instanceof FileResource) {
					
					FileResourceBean frBean = process(res, NOT_TOP_LEVEL_OBJECT);
					
					resourcesArray[i] = frBean;
				} else {
					log.error("ERROR: unexpected report unit resource type, res.name=" + res.getName());
				}
			}
			bean.setResources(resourcesArray);
		}
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
			writeCatalogData(bean);
		}
		return bean;
	}	
	
	public OlapUnitBean process(OlapUnit unit, boolean isTopLevel) {
		log.debug("top of process(OlapUnit)");
		
		OlapUnitBean bean = new OlapUnitBean();
		
		fillBean(bean, unit);

		/*
		 * get olap client connection
		 */
		
		OlapClientConnection conn = (OlapClientConnection) dereference(mContext, unit.getOlapClientConnection());
		
		OlapClientConnectionBean connBean = process(conn, NOT_TOP_LEVEL_OBJECT);
		
		if (unit.getOlapClientConnection().isLocal()) {
			
			connBean.setIsUriReference(false);
			
		} else {
			connBean.setIsUriReference(true);
		}
		
		bean.setOlapClientConnection(connBean);
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
			writeCatalogData(bean);
		}
		return bean;
	}
	
	public OlapClientConnectionBean process(OlapClientConnection conn, boolean isTopLevel) {
		log.debug("top of process(OlapClientConnection)");
		
		if (conn instanceof XMLAConnection) {
			
			return process( (XMLAConnection) conn, isTopLevel);
			
		} else if (conn instanceof MondrianConnection) {
			
			return process( (MondrianConnection) conn, isTopLevel);
			
		} else {
			log.error("Unknown OlapClientConnection type, conn.uri=" + conn.getURIString());
			return null;			
		}
	}
	
	public XmlaConnectionBean process(XMLAConnection conn, boolean isTopLevel) {
		
		XmlaConnectionBean bean = new XmlaConnectionBean();
		
		fillBean(bean, conn);

		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
		}
		
		return bean;
	}
	
	public MondrianConnectionBean process(MondrianConnection conn, boolean isTopLevel) {
		
		MondrianConnectionBean bean = new MondrianConnectionBean();
		
		fillBean(bean, conn);

		/*
		 * get schema
		 */
		FileResource schema = (FileResource) dereference(mContext, conn.getSchema());
		
		FileResourceBean schemaBean = process(schema, NOT_TOP_LEVEL_OBJECT);
		
		if (conn.getSchema().isLocal()) {
			schemaBean.setIsUriReference(false);
		} else {
			schemaBean.setIsUriReference(true);
		}
		
		bean.setSchema(schemaBean);
		
		/*
		 * get data source
		 */
		DataSource ds = (DataSource) dereference(mContext, conn.getDataSource());
		
		DataSourceBean dsBean = process(ds, NOT_TOP_LEVEL_OBJECT);
		
		if (conn.getDataSource().isLocal()) {
			dsBean.setIsUriReference(false);
		} else {
			dsBean.setIsUriReference(true);
		}
		
		bean.setDataSource(dsBean);
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
			writeCatalogData(bean);
		}
		
		return bean;
	}
	
	
	public MondrianXmlaDefinitionBean process(MondrianXMLADefinition xDef, boolean isTopLevel) {
		log.debug("top of process(MondrianXMLADefinition)");
		
		MondrianXmlaDefinitionBean bean = new MondrianXmlaDefinitionBean();
		
		fillBean(bean, xDef);
		
		/*
		 * get mondrian connection
		 */
		MondrianConnection conn = (MondrianConnection) dereference(mContext, xDef.getMondrianConnection());
		
		MondrianConnectionBean mondBean = process(conn, NOT_TOP_LEVEL_OBJECT);
		
		if (xDef.getMondrianConnection().isLocal()) {
			mondBean.setIsUriReference(false);
		} else {
			mondBean.setIsUriReference(true);
		}
		
		bean.setMondrianConnection(mondBean);

		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
			writeCatalogData(bean);
		}

		return bean;
	}
	
	public DataSourceBean process(DataSource dataSource, boolean isTopLevel) {
		
		if (dataSource instanceof JdbcReportDataSource) {
			
			return process( (JdbcReportDataSource) dataSource, isTopLevel);	
			
		} else if (dataSource instanceof JndiJdbcReportDataSource) {
			
			return process((JndiJdbcReportDataSource) dataSource, isTopLevel);
			
		} else {
			log.error("Unknown datasource type, dataSource.name=" + dataSource.getName());
			return null;
		}
	}
	
	public JdbcDataSourceBean process(JdbcReportDataSource dataSource, boolean isTopLevel) {
		
		JdbcDataSourceBean bean = new JdbcDataSourceBean();
		
		fillBean(bean, dataSource);

		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
		}
		
		return bean;
	}
	
	public JndiJdbcDataSourceBean process(JndiJdbcReportDataSource dataSource, boolean isTopLevel) {
		
		JndiJdbcDataSourceBean bean = new JndiJdbcDataSourceBean();
		
		fillBean(bean, dataSource);
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
		}
		
		return bean;
	}
	

	public InputControlBean process(InputControl inputControl, boolean isTopLevel) {
		log.debug("top of process(InputControl)");
		
		InputControlBean bean = new InputControlBean();
		
		fillBean(bean, inputControl);	// fill all data except bean objects handled below
		
		/*
		 * get dataType
		 */
		
		if (inputControl.getDataType() != null) {
			
			DataType dataType = (DataType) dereference(null, inputControl.getDataType());
			
			DataTypeBean dataTypeBean = process(dataType, NOT_TOP_LEVEL_OBJECT);	
			
			if (inputControl.getDataType().isLocal()) {
				dataTypeBean.setIsUriReference(false); 
			} else {
				dataTypeBean.setIsUriReference(true);
			}
			
			bean.setDataType(dataTypeBean);
		}
		
		/*
		 * get listOfValues
		 */
		
		if (inputControl.getListOfValues() != null) {
			
			ListOfValues listOfValues = (ListOfValues) dereference(null, inputControl.getListOfValues());
			
			ListOfValuesBean listOfValuesBean = process(listOfValues, NOT_TOP_LEVEL_OBJECT);	
			
			if (inputControl.getListOfValues().isLocal()) {
				listOfValuesBean.setIsUriReference(false);
			} else {
				listOfValuesBean.setIsUriReference(true);
			}
			
			bean.setListOfValues(listOfValuesBean);
		}
		
		/*
		 * get query
		 */
		
		if (inputControl.getQuery() != null) {
			
			Query query = (Query) dereference(null, inputControl.getQuery());
			
			QueryBean queryBean = process(query, NOT_TOP_LEVEL_OBJECT);	
			
			if (inputControl.getQuery().isLocal()) {
				queryBean.setIsUriReference(false);
			} else {
				queryBean.setIsUriReference(true);
			}
			
			bean.setQuery(queryBean);
		}
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
		}
		
		return bean;
	}
	
	
	public QueryBean process(Query query, boolean isTopLevel) {
		
		QueryBean bean = new QueryBean();
		
		fillBean(bean, query);

		/*
		 * get data source
		 */
		if(query.getDataSource() != null) {
			DataSource dataSource = (DataSource) dereference(null, query.getDataSource());
			DataSourceBean dsBean = process(dataSource, NOT_TOP_LEVEL_OBJECT);
			
			/*
			 * check whether it's local to the query or not
			 */	
			if (query.getDataSource().isLocal()) {
				dsBean.setIsUriReference(false);
			} else {
				dsBean.setIsUriReference(true);
			}
					
			bean.setDataSource(dsBean);
		}
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
		}
		
		return bean;
	}
	
	
	public DataTypeBean process(DataType dataType, boolean isTopLevel) {
		
		DataTypeBean bean = new DataTypeBean();
		
		fillBean(bean, dataType);
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
		}
		
		return bean;
	}
	
	
	public ListOfValuesBean process(ListOfValues listOfValues, boolean isTopLevel) {
		
		ListOfValuesBean bean = new ListOfValuesBean();
		
		fillBean(bean, listOfValues);
		
		if (isTopLevel) {
			mExportImportBean.setResource(bean);
			writeCatalogFile(mExportImportBean);
		}
		
		return bean;
	}
	
	
	public ReportJobBean process(ReportJob job, boolean isTopLevel) {
		
		ReportJobBean bean = new ReportJobBean();
		
		bean.setId(job.getId());
		bean.setVersion(job.getVersion());
		bean.setUsername(job.getUsername());
		bean.setLabel(job.getLabel());
		bean.setDescription(job.getDescription());
		bean.setBaseOutputFilename(job.getBaseOutputFilename());
		bean.setOutputLocale(job.getOutputLocale());
		
		ReportJobTrigger trigger = job.getTrigger();
		if (trigger != null) {
			ReportJobTriggerBean jtBean;
			if (trigger instanceof ReportJobSimpleTrigger) {
				jtBean = new ReportJobSimpleTriggerBean();
				jtBean.copyFrom(trigger);
			} else if (trigger instanceof ReportJobCalendarTrigger) {
				jtBean = new ReportJobCalendarTriggerBean();
				jtBean.copyFrom(trigger);
			} else {
				String quotedTriggerType = "\"" + trigger.getClass().getName() + "\"";
				throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedTriggerType});
			}
			bean.setTrigger(jtBean);
		}
		
		if (job.getSource() != null) {
			
			ReportJobSourceBean srcBean = new ReportJobSourceBean();
			ReportJobSource src = job.getSource();
			
			srcBean.setReportUnitURI(src.getReportUnitURI());
			
			if (src.getParametersMap() != null) {
				
				Map parms = src.getParametersMap();
				Set keys = parms.keySet();
				ParameterBean[] parmBeans = new ParameterBean[keys.size()];
				
				int i = 0;
				for(Iterator it = keys.iterator(); it.hasNext(); i++) {
					
					String key = (String) it.next();
					String value = parms.get(key).toString();
					
					ParameterBean pb = new ParameterBean();
					pb.setKey(key);
					pb.setValue(value);
					parmBeans[i] = pb;
				}
				srcBean.setParameters(parmBeans);
			}	
			bean.setSource(srcBean);
		}
		
		if (job.getOutputFormats() != null) {
			
			Set outputFormats = job.getOutputFormats();
			byte[] outArray = new byte[outputFormats.size()];
			
			int i = 0;
			for (Iterator it = outputFormats.iterator(); it.hasNext(); i++) {
				
				outArray[i] = ((Byte) it.next()).byteValue();
			}			
			bean.setOutputFormats(outArray);
		}
		
		if (job.getContentRepositoryDestination() != null) {
			
			ReportJobRepositoryDestinationBean destBean = new ReportJobRepositoryDestinationBean();
			ReportJobRepositoryDestination dest = job.getContentRepositoryDestination();
			
			destBean.setId(dest.getId());
			destBean.setVersion(dest.getVersion());
			destBean.setFolderURI(dest.getFolderURI());
			destBean.setSequentialFilenames(dest.isSequentialFilenames());
			destBean.setOverwriteFiles(dest.isOverwriteFiles());
			
			bean.setContentRepositoryDestination(destBean);
		}
		
		if (job.getMailNotification() != null) {
			
			ReportJobMailNotificationBean noteBean = new ReportJobMailNotificationBean();
			ReportJobMailNotification note = job.getMailNotification();
			
			noteBean.setId(note.getId());
			noteBean.setVersion(note.getVersion());
			
			if (note.getToAddresses() != null) {
				noteBean.setToAddresses( 
					(String[]) note.getToAddresses().toArray(new String[note.getToAddresses().size()]));
			}
			
			//noteBean.setCcAddresses();	// todo
			//noteBean.setBccAddresses();
			noteBean.setSubject(note.getSubject());
			noteBean.setMessageText(note.getMessageText());
			noteBean.setResultSendType(note.getResultSendType());
			
			bean.setMailNotification(noteBean);
		}
		
		return bean;
	}
	
	
	public UserRoleHolderBean processAllUsersRoles(boolean isTopLevel) {
			
		UserRoleHolderBean bean = new UserRoleHolderBean();
		
		/*
		 * get users
		 */
		List userList = mAuth.getUsers(mContext, filterCriteria);
		
		
		String[] usernames = new String[userList.size()];
		
		int i = 0;
		for (Iterator it = userList.iterator(); it.hasNext(); i++) {
			
			usernames[i] = ( (User) it.next() ).getUsername();
		}
		
		bean.setUsers( handleUsers(usernames, NOT_TOP_LEVEL_OBJECT) );

		/*
		 * get roles
		 */
		List roleList = mAuth.getRoles(mContext, filterCriteria);
		
		String[] rolenames = new String[roleList.size()];
		
		int j = 0;
		for (Iterator it = roleList.iterator(); it.hasNext(); j++) {
			
			rolenames[j] = ( (Role) it.next() ).getRoleName();
		}
		
		bean.setRoles( handleRoles(rolenames, NOT_TOP_LEVEL_OBJECT) );
		
		if (isTopLevel) {
			// need to check???
		}
		return bean;
	}
	
	/* 
	 * User has requested a set of users to be exported. Process this 
	 * operation.
	 * 
	 * In this operation, we export just the users. We do not export
	 * a full set of data describing the roles that the user has (but
	 * we do save the names of the roles that the user has).
	 * 
	 */
	public UserRoleHolderBean processNamedUsers(String[] usernames, boolean isTopLevel) {
		
		UserRoleHolderBean bean = new UserRoleHolderBean();
		
		bean.setUsers( handleUsers(usernames, NOT_TOP_LEVEL_OBJECT)  );
		
		return bean;
	}
		
	/**
	 * Processing of named roles is a bit different than similar processing for
	 * users. By default, if the user has included a list of roles to export, then 
	 * we are also going to get the users that this role. 
	 * 
	 * 
	 * @param rolenames
	 * @param isTopLevel
	 * @return
	 */
	public UserRoleHolderBean processNamedRoles(String[] rolenames, boolean isTopLevel) {
		
		UserRoleHolderBean bean = new UserRoleHolderBean();
		
		RoleBean[] roleBeans = handleRoles(rolenames, NOT_TOP_LEVEL_OBJECT);
		
		bean.setRoles(roleBeans);
		
		//if (mIsIncludeUsersWithRole) {	// todo: make this a command line option
			UserBean[] userBeans = getUsersFromRole(roleBeans);
		//}
		
		bean.setUsers(userBeans);
		
		return bean;
	}
	
	
	public UserRoleHolderBean processNamedUsersRoles(String[] usernames,
			String[] rolenames,
			boolean isTopLevel) {
		
		UserRoleHolderBean bean = new UserRoleHolderBean();
		
		bean.setUsers( handleUsers(usernames, NOT_TOP_LEVEL_OBJECT)  );
		bean.setRoles( handleRoles(rolenames, NOT_TOP_LEVEL_OBJECT)  );
		
		return bean;
	}
	
	/* 
	 * Given an array of usernames, return an array of populated UserBean
	 * objects.
	 */
	public UserBean[] handleUsers(String[] usernames, boolean isTopLevel) {
		
		UserBean[] userBeans = new UserBean[usernames.length];
		
		for (int i = 0; i < usernames.length; i++) {
			
			UserBean bean = new UserBean();
			User user = mAuth.getUser(mContext, usernames[i]);
				
			bean.setUsername(user.getUsername());
			bean.setFullName(user.getFullName());
			bean.setPassword(user.getPassword());
			bean.setEmailAddress(user.getEmailAddress());
			bean.setExternallyDefined(user.isExternallyDefined());
			bean.setEnabled(user.isEnabled());
			
			bean.setRoleSet( getRolesFromUser(user) );
			
			userBeans[i] = bean;
		}
		
		return userBeans;	
	}
	

	/* 
	 * Given an array of rolenames, return an array of populated RoleBean
	 * objects.
	 */
	public RoleBean[] handleRoles(String[] rolenames, boolean isTopLevel) {
		
		RoleBean[] roleBeans = new RoleBean[rolenames.length];
		
		for (int i = 0; i < rolenames.length; i++) {
			
			RoleBean bean = new RoleBean();
			Role role = mAuth.getRole(mContext, rolenames[i]);
			
			bean.setRoleName(role.getRoleName());
			bean.setExternallyDefined(role.isExternallyDefined());
			
			roleBeans[i] = bean;
		}
		
		return roleBeans;
	}
	
	
	
	/*
	 * Given a User object, get the roles that this User has. 
	 * 
	 * In the returned RoleBean objects, we will only populate the 
	 * rolename.
	 */	
	protected RoleBean[] getRolesFromUser(User user) {
		
		Set roleSet = user.getRoles();
		RoleBean[] roleArray = new RoleBean[roleSet.size()];
		
		int i = 0;
		for (Iterator it = roleSet.iterator(); it.hasNext(); i++) {
			
			RoleBean bean = new RoleBean();
			Role role = (Role) it.next();

			/*
			 * only need the rolename
			 */
			bean.setRoleName(role.getRoleName());

			roleArray[i] = bean;
		}
		return roleArray;
	}
	
	
	/*
	 * For roles in the exportRoleBeans parameter, get the set of users that have
	 * these roles
	 * 
	 * Do the following:
	 *   Get all users, get the roles they have, see if any of these roles
	 *   are roles found in exportRoleBeans. 
	 */
	protected UserBean[] getUsersFromRole(RoleBean[] exportRoleBeans) {
		
		List userList = mAuth.getUsers(mContext, filterCriteria);	// get all users in system
		
		List finalUserNameList = new ArrayList();
		
		for (Iterator it = userList.iterator(); it.hasNext(); ) {	// loop thru all users

			User user = (User) it.next();
			Set roleSet = user.getRoles();
			
			for (Iterator it2 = roleSet.iterator(); it2.hasNext(); ) {		// loop thru this user's roles
				
				Role role = (Role) it2.next();
				
				for (int i = 0; i < exportRoleBeans.length; i++) {				// check if user's role is in export list 
					
					if ( role.getRoleName().equals(exportRoleBeans[i].getRoleName())  ) {
							
						if (!finalUserNameList.contains(user.getUsername())) {			// skip dups
							log.debug("   Adding this user, username=" + user.getUsername());
							finalUserNameList.add(user.getUsername());
						}
					}
				}
			}
		}

		return handleUsers(	(String[]) finalUserNameList.toArray(new String[finalUserNameList.size()]), 
				NOT_TOP_LEVEL_OBJECT);
	}
	
	
	protected InputControlBean[] handleInputControls(ReportUnit unit, boolean isTopLevel) {
		
		List inputControls = unit.getInputControls();
		
		InputControlBean[] inputControlBeans = new InputControlBean[inputControls.size()];

		int i = 0;
		for (Iterator it = inputControls.iterator(); it.hasNext(); i++) {
			
			ResourceReference resRef = (ResourceReference) it.next();
			InputControl ic = (InputControl) dereference(null, resRef);
			InputControlBean bean = process(ic, isTopLevel);
			
			if (resRef.isLocal()) {
				bean.setIsUriReference(false);
			} else {
				bean.setIsUriReference(true);
			}
			
			inputControlBeans[i] = bean;
		}
		
		return inputControlBeans;
	}
	
	
	protected FolderBean[] handleSubFolders(Folder topFolder) {
		
		List subFolders = mRepo.getSubFolders(null, topFolder.getURIString());
		
		FolderBean[] subFolderBeans = new FolderBean[subFolders.size()]; 
		int i = 0;
		
		for (Iterator it = subFolders.iterator(); it.hasNext(); i++) {
			FolderBean bean = new FolderBean();
			Folder folder = (Folder) it.next();
			fillCommonBeanFields(bean, folder);
			subFolderBeans[i] = bean;	
		}
		return subFolderBeans;
	}
	
	
	protected ResourceBean[] handleResourcesInFolder(Folder folder) {
		
		FilterCriteria filterCriteria = new FilterCriteria();
		filterCriteria.addFilterElement( FilterCriteria.createParentFolderFilter(folder.getURIString()) );
		
		ResourceLookup[] files = mRepo.findResource(null, filterCriteria);
		ResourceBean[] resourceBeans = new ResourceBean[files.length];
		
		for (int i = 0; i < files.length; i++) {
			log.debug("file name=" + files[i].getName());
			
			Resource res = mRepo.getResource(null, files[i].getURIString());
						
			if (res instanceof ReportUnit) {
				log.debug("file inst of RU");
				
				ReportUnitBean bean = process( (ReportUnit) res, NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof OlapUnit) {
				log.debug("file inst of OlapUnit");
				
				OlapUnitBean bean = process( (OlapUnit) res, NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof FileResource) {
				log.debug("file inst of FR");
				
				FileResourceBean bean = process( (FileResource) res, NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof ContentResource) {
				log.debug("file inst of CR");
				
				ContentResourceBean bean = process( (ContentResource) res, NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof DataSource) {
				log.debug("file inst of DS");
				
				DataSourceBean bean = process( (DataSource) res, NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof OlapClientConnection) {
				log.debug("file inst of OlapClientConnection");
				
				OlapClientConnectionBean bean = process((OlapClientConnection) res, NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;

			} else if (res instanceof MondrianXMLADefinition) {
				log.debug("file inst of MondrianXMLADefinition");
				
				MondrianXmlaDefinitionBean bean = process((MondrianXMLADefinition) res, NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof InputControl) {
				log.debug("file inst of InputControl");
				
				InputControlBean bean = process( (InputControl) res,  NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof DataType) {
				log.debug("file inst of DataType");
				
				DataTypeBean bean = process( (DataType) res,  NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof ListOfValues) {
				log.debug("file inst of ListOfValues");
				
				ListOfValuesBean bean = process( (ListOfValues) res,  NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;
				
			} else if (res instanceof Query) {
				log.debug("file inst of Query");
				
				QueryBean bean = process( (Query) res,  NOT_TOP_LEVEL_OBJECT);
				resourceBeans[i] = bean;

			} else {
				log.error("NOT HANDLING THIS RESOURCE TYPE YET, resourceType=" + files[i].getResourceType());
			}
		}
		
		return resourceBeans;
	}
	
	
	/**
	 * Determine the processing operations for Users and Roles.
	 * 
	 * 1) if processAll, then export all users and all roles
	 * 2) if namedUsers, then export the users listed in the input
	 *    parameter (usernames) (do not include any roles)
	 * 3) if namesdRoles, then export the roles listed in the input
	 *    paramter (rolenames), *AND* include any users that are 
	 *    referenced by these roles.
	 * 4) if namedUserRoles, then export the users and roles listed
	 *    in the two input parameters (usernames and rolenames).
	 *
	 */
	protected void checkForUserRoleProcessing() {
		
		if ((mUserNames == null || mUserNames.length == 0) &&
				(mRoleNames == null || mRoleNames.length == 0)) {
			mIsAllUsersRoles = true;
			
		} else if ((mUserNames != null && mUserNames.length > 0) &&
				(mRoleNames == null || mRoleNames.length == 0)) {
			mIsNamedUsers = true;
			
		} else if ((mRoleNames != null && mRoleNames.length > 0) &&
				(mUserNames == null || mUserNames.length == 0)) {
			mIsNamedRoles = true;
			
		} else if ((mUserNames != null && mUserNames.length > 0) && 
				(mRoleNames != null && mRoleNames.length > 0)) {
			mIsNamedUsersRoles = true;
			
		} else {
			log.error("ERROR: User Role processing operation unknown");
		}
		
		log.warn(" ----- User-Role Processing Values:");
		log.warn("         mIsAllUsersRoles=" + mIsAllUsersRoles);
		log.warn("         mIsNamedUsers=" + mIsNamedUsers);
		log.warn("         mIsNamedRoles=" + mIsNamedRoles);
		log.warn("         mIsNamedUsersRoles=" + mIsNamedUsersRoles);
	}
	
	
	/**
	 * Write out the ji-catalog xml file
	 * 
	 * @param bean
	 */
	protected void writeCatalogFile(ExportImportBean bean) {
		log.debug("top of writeCatalogFile()");
		
		try {
			File dir = new File(mCatalogDirName);
			dir.mkdir();
			File outCatalog = new File(dir, mCatalogFileName);
			Writer writer = new OutputStreamWriter(new FileOutputStream(outCatalog), mCharacterEncoding);

			Marshaller marshaller = new Marshaller(writer);
			marshaller.setMapping(MappingHelper.getExportImportBeanMapping());
			marshaller.marshal(bean);
			
			System.out.println("ExportResource: writeCatalogFile(): catDir=" + mCatalogDirName + ", catFile=" + mCatalogFileName);
			log.debug("ExportResource: writeCatalogFile(): outCatalog=" + outCatalog.toString());
			
		} catch (Exception e) {
			log.error("caught exception creating catalog file: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	/*
	 * recurse through the FolderBean and write data
	 */
	protected void writeCatalogData(FolderBean bean) {
		
		ResourceBean[] resourceBeans = bean.getResources();
		
		for (int i = 0; i < resourceBeans.length; i++) {
			
			// write data for resources that hold data
			if (resourceBeans[i] instanceof FileResourceBean) {
				
				writeCatalogData( (FileResourceBean) resourceBeans[i]);
				
			} else if (resourceBeans[i] instanceof ContentResourceBean) {
				
				writeCatalogData( (ContentResourceBean) resourceBeans[i]);
				
			} else if (resourceBeans[i] instanceof ReportUnitBean) {
				
				writeCatalogData( (ReportUnitBean) resourceBeans[i]);
				
			} else {
				log.debug("DEBUG: skipped bean resource (for write data), name=" + resourceBeans[i].getName());
			}
		}
		
		FolderBean[] folderBeans = bean.getFolders();
		
		for (int j = 0; j < folderBeans.length; j++) {
			
			writeCatalogDataFolder(folderBeans[j]);
		}
	}
	
	// todo: see about removing this method
	protected void writeCatalogDataFolder(FolderBean bean) {
		
		ResourceBean[] resourceBeans = bean.getResources();
		
		for (int i = 0; i < resourceBeans.length; i++) {
			
			// write data for resources that hold data
			if (resourceBeans[i] instanceof FileResourceBean) {
				
				writeCatalogData( (FileResourceBean) resourceBeans[i]);
				
			} else if (resourceBeans[i] instanceof ContentResourceBean) {
				
				writeCatalogData( (ContentResourceBean) resourceBeans[i]);
				
			} else if (resourceBeans[i] instanceof ReportUnitBean) {
				
				writeCatalogData( (ReportUnitBean) resourceBeans[i]);
				
			} else {
				log.debug("DEBUG2: skipped bean resource (for write data), name=" + resourceBeans[i].getName());
			}
		}
		
		FolderBean[] folderBeans = bean.getFolders();
		
		for (int j = 0; j < folderBeans.length; j++) {
			
			writeCatalogDataFolder(folderBeans[j]);
		}
	}
	
	
	protected void writeCatalogData(ReportUnitBean bean) {
		log.debug("top of writeCatalogData(RU)");
		
		writeCatalogDataMainReport(bean.getMainReport());
		
		FileResourceBean[] resourceBeans = bean.getResources();
		
		for (int i = 0; i < resourceBeans.length; i++) {
			writeCatalogData(resourceBeans[i]);
		}
	}

	protected void writeCatalogData(OlapUnitBean bean) {
		log.debug("top of writeCatalogData(OlapUnitBean)");

		writeCatalogData(bean.getOlapClientConnection());
	}
	
	protected void writeCatalogData(OlapClientConnectionBean bean) {
		log.debug("top of writeCatalogData(OlapClientConnectionBean)");

		if (bean.getConnectionType().equals(OlapClientConnectionBean.CONN_TYPE_MONDRIAN)) {
			
			writeCatalogData( ((MondrianConnectionBean) bean).getSchema());	// schema is a FileResource
		} 
	}
	
	protected void writeCatalogData(MondrianXmlaDefinitionBean bean) {
		log.debug("top of writeCatalogData(MondrianXmlaDefinitionBean)");
		
		MondrianConnectionBean mondBean = bean.getMondrianConnection();
		
		writeCatalogData(mondBean.getSchema());		// schema is a FileResource
	}
	

	protected void writeCatalogDataMainReport(FileResourceBean bean) {

		File parentPath = null;
		String name = null;
		FileResourceData data = null;

		try {
			parentPath = createParentPath(bean);
			data = mRepo.getResourceData(null, bean.getUriString());
			
			if (data != null) {				
				FileOutputStream out = new FileOutputStream(new File(parentPath, bean.getName()));
				out.write(data.getData());
			}
		} catch (Exception e) {
			log.error("ERROR: caught exception processing data to disk, file=" 
					+ parentPath + PATH_SEP + bean.getName() + ", exception: " + e.getMessage());
			e.printStackTrace();
		}	
	}
	
	
	protected void writeCatalogData(FileResourceBean bean) {		
		log.debug("top of writeCatalogData(FileResourceBean)");
		
		File parentPath = null;
		String name = null;
		FileResourceData data = null;
		
		try {
			
			if (bean.getIsReference()) {
				
				/*
				 * it's a link so save data from linkedTarget
				 */
				parentPath = createParentPath(bean.getLinkTarget());
				data = mRepo.getResourceData(null, bean.getLinkTarget().getUriString());	
				name = bean.getLinkTarget().getName();
				
			} else {
				
				parentPath = createParentPath(bean);
				data = mRepo.getResourceData(null, bean.getUriString());
				name = bean.getName();
			}
			
			if (data != null) {				
				FileOutputStream out = new FileOutputStream(new File(parentPath, name));
				
				log.debug("number of bytes to write out=" + data.getData().length);
				
				out.write(data.getData());
			} 			
						
		} catch (Exception e) {
			log.error("ERROR: caught exception processing data to disk, file=" 
					+ parentPath + PATH_SEP + name + ", exception: " + e.getMessage());
			e.printStackTrace();
		}		
	}
	
	protected void writeCatalogData(ContentResourceBean bean) {		

		File parentPath = null;
		String name = null;
		FileResourceData data = null;
		
		try {
			
			if (bean.getIsReference()) {
				
				log.debug(" in writeCatalogData(CRB), bean is reference, bean.name=" + bean.getName());
				
				/*
				 * it's a link so save data from linkedTarget
				 */
				parentPath = createParentPath(bean.getLinkTarget());
				data = mRepo.getContentResourceData(null, bean.getLinkTarget().getUriString());	
				name = bean.getLinkTarget().getName();
				
			} else {
			
				log.debug(" in writeCatalogData(CRB), bean is NOT reference, bean.name=" + bean.getName());
				
				parentPath = createParentPath(bean);
				data = mRepo.getContentResourceData(null, bean.getUriString());
				name = bean.getName();
			}
			
			if (data != null) {				
				FileOutputStream out = new FileOutputStream(new File(parentPath, name));
				out.write(data.getData());
			} 	
			
			/*
			 * write out child resources 
			 */
			
			if (bean.getResources() != null) {
				
				ContentResourceBean[] childResources = bean.getResources();
				
				for (int i = 0; i < childResources.length; i++) {
					writeCatalogData(childResources[i]);
				}
			}
			
		} catch (Exception e) {
			log.error("ERROR: caught exception processing data to disk, file=" 
					+ parentPath + PATH_SEP + name + ", exception: " + e.getMessage());
			e.printStackTrace();
		}		
	}
	
	
	
	/**
	 * Create a path on disk. This will be the path to the resource data to be
	 * written out. The on-disk path matches the path for the resource in the 
	 * repository.
	 * 
	 * @param ResourceBean
	 * @return File
	 */
	protected File createParentPath(ResourceBean bean) {
		File parentPath = new File(mCatalogDirName, bean.getParentFolder());
		
		if (!parentPath.exists()) {
			if(!parentPath.mkdirs()) {		// create path on disk
				log.error("ERROR: Error creating parent path, path=" + bean.getParentFolder());
			}
		}
		return parentPath;
	}
	
	
    public Resource dereference( ExecutionContext mContext, ResourceReference ref ) {
    	if (ref.isLocal()) { 
    		return ref.getLocalResource();
    	} else {
    		return mRepo.getResource( null, ref.getReferenceLookup().getURIString() );
    	}
    }
	
    
	/**
	 * Set the member vars found in the ResourceBean
	 * 
	 * @param uri
	 * @throws Exception
	 */
	protected void fillCommonBeanFields(ResourceBean bean, Resource res) {
		bean.setName(res.getName());
		bean.setLabel(res.getLabel());
		bean.setDescription(res.getDescription());
		bean.setUriString(res.getURIString());
		bean.setParentFolder(res.getParentFolder());
		bean.setResourceType(res.getResourceType());
		bean.setIsNew(res.isNew());
		bean.setCreationDate(res.getCreationDate());
		bean.setVersion(res.getVersion());
		bean.setProductReleaseVersion(PRODUCT_VERSION_1_0);
	}	
	
	protected void fillBean(FileResourceBean bean, FileResource fileResource) {
		
		fillCommonBeanFields(bean, fileResource);
		
		bean.setHasData(fileResource.hasData());
		
		bean.setResourceBeanType( bean.getClass().toString() );
	}		
	
	protected void fillBean(ContentResourceBean bean, ContentResource contentResource) {
		
		fillCommonBeanFields(bean, contentResource);
		
		bean.setHasData(contentResource.hasData());
		
		bean.setResourceBeanType( bean.getClass().toString() );
		
	}	
	
	protected void fillBean(JdbcDataSourceBean bean, JdbcReportDataSource dataSource) {

		fillCommonBeanFields(bean, dataSource);

		bean.setDataSourceType(DataSourceBean.DS_TYPE_JDBC);
		bean.setDriverClass(dataSource.getDriverClass());
		bean.setConnectionUrl(dataSource.getConnectionUrl());
		bean.setUsername(dataSource.getUsername());
		bean.setPassword(dataSource.getPassword());
	}
	
	protected void fillBean(JndiJdbcDataSourceBean bean, JndiJdbcReportDataSource dataSource) {
	
		fillCommonBeanFields(bean, dataSource);
		
		bean.setDataSourceType(DataSourceBean.DS_TYPE_JNDI_JDBC);
		bean.setJndiName(dataSource.getJndiName());
	}
	
	protected void fillBean(OlapUnitBean bean, OlapUnit unit) {
		
		fillCommonBeanFields(bean, unit);

		bean.setMdxQuery(unit.getMdxQuery());		
	}
	
	protected void fillBean(XmlaConnectionBean bean, XMLAConnection conn) {
		
		fillCommonBeanFields(bean, conn);
		
		bean.setConnectionType(OlapClientConnectionBean.CONN_TYPE_XMLA);
		bean.setUri(conn.getURI());
		bean.setDataSource(conn.getDataSource());
		bean.setCatalog(conn.getCatalog());
		bean.setUsername(conn.getUsername());
		bean.setPassword(conn.getPassword());
	}
	
	protected void fillBean(MondrianConnectionBean bean, MondrianConnection conn) {
		
		fillCommonBeanFields(bean, conn);
		
		bean.setConnectionType(OlapClientConnectionBean.CONN_TYPE_MONDRIAN);
		
	}
	
	protected void fillBean(MondrianXmlaDefinitionBean bean, MondrianXMLADefinition xDef) {
		
		fillCommonBeanFields(bean, xDef);
		
		bean.setCatalog(xDef.getCatalog());		
	}
	
	protected void fillBean(InputControlBean bean, InputControl ic) {
		
		fillCommonBeanFields(bean, ic);
		
		bean.setType(ic.getType());
		bean.setIsMandatory(ic.isMandatory());
		bean.setIsReadOnly(ic.isReadOnly());
		bean.setQueryVisibleColumns(ic.getQueryVisibleColumns());
		bean.setQueryValueColumn(ic.getQueryValueColumn());
		if (ic.getDefaultValue() != null) {
			bean.setDefaultValue(ic.getDefaultValue().toString());
		}
		
		// convert List to String array
		if (ic.getDefaultValues() != null) {
			List values = ic.getDefaultValues();
			bean.setDefaultValues((String[]) values.toArray(new String[values.size()]));
		}
	}
	
	protected void fillBean(DataTypeBean bean, DataType dt) {
		
		fillCommonBeanFields(bean, dt);
		
		bean.setType(dt.getType());
		bean.setMaxLength(dt.getMaxLength());
		bean.setDecimals(dt.getDecimals());
		bean.setRegularExpr(dt.getRegularExpr());
		
		if (dt.getMinValue() != null) {
			bean.setMinValue(dt.getMinValue().toString());
		}
		if (dt.getMaxValue() != null) {
			bean.setMaxValue(dt.getMaxValue().toString());
		}
		bean.setIsStrictMax(dt.isStrictMax());
		bean.setIsStrictMin(dt.isStrictMin());
	}
	
	protected void fillBean(ListOfValuesBean bean, ListOfValues listOfValues) {

		fillCommonBeanFields(bean, listOfValues);
		
		ListOfValuesItem[] items = listOfValues.getValues();
		ListOfValuesItemBean[] beanItems = new ListOfValuesItemBean[items.length];
		
		for (int i = 0; i < items.length; i++) {
			ListOfValuesItemBean itemBean = new ListOfValuesItemBean();
			itemBean.setLabel(items[i].getLabel());
			itemBean.setValue(items[i].getValue().toString());
			beanItems[i] = itemBean;
		}
		
		bean.setValues(beanItems);
	}
	
	protected void fillBean(QueryBean bean, Query query) {
		
		fillCommonBeanFields(bean, query);
		
		bean.setLanguage(query.getLanguage());
		bean.setSql(query.getSql());
	}	
	
}
