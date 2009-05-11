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
 * @version $Id: ExportImportCommand.java 8408 2007-05-29 23:29:12Z melih $
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.engine.scheduling.ReportSchedulingInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;


public class ExportImportCommand {


	private static final Log log = LogFactory.getLog(ExportImportCommand.class);

	private static final String REPORT_SCHEDULING_SERVICE_BEAN_NAME = "reportSchedulingService";
	private static final String REPORT_SCHEDULING_INTERNAL_SERVICE_BEAN_NAME = "reportSchedulingInternalService";
	private static final String ENCODING_PROVIDER_BEAN_NAME = "encodingProvider";

	
	private RepositoryService mRepo;
	private UserAuthorityService mAuth;
	private ReportSchedulingService mReportScheduler;
	private ReportSchedulingInternalService mReportSchedulerInternal;
	private CharacterEncodingProvider encodingProvider;

	private ExecutionContext mContext;
	
	private Properties mJdbcProps;

	static boolean sImportOperation = false;
	static boolean sExportOperation = false;
	
	private boolean mProcessUri = false;
	private boolean mProcessUsers = false;
	private boolean mProcessRoles = false;
	private boolean mProcessJobReportUnits = false;
	private boolean mProcessNothing = false;
	
	private boolean mUseNamedOutputDir = false;		// used for both export and import
	private boolean mUseNamedOutputFile = false;		// used for both export and import
	private String mOutputDirName;
	private String mOutputFileName;
	
	private List mUriValuesList;
	private List mUserNamesList;
	private List mRoleNamesList;
	private List mJobReportUnitsList;
	private List mOutputFileNameList;
	private List mOutputDirNameList;
	
	private boolean mImportProcessPrependPath = false;
	private List mPrependPathValuesList;
	
	private boolean mVerbose = false;
	private boolean mDevEnv = false;
	
	public static void main(String[] args) {
		
		System.out.println("ExportImportCommand: START");
		
		boolean result = true;
		
		ExportImportCommand command = new ExportImportCommand();
		
		if (args[0].equalsIgnoreCase("--export")) {
			
			sExportOperation = true;
			
			command.runExport(args);
			
		} else if (args[0].equalsIgnoreCase("--import")) {
		
			sImportOperation = true;
			
			result = command.runImport(args);
			
		} else {	
			System.out.println("\nExportImportCommand: unknown main process option, option=" + args[0]);
		}
		
		if (result) {
			System.out.println("ExportImportCommand: Successful");
			System.exit(0);
		} else {
			System.out.println("ExportImportCommand: ERROR");
			System.exit(1);
		}
	}
	
	public void runExport(String[] args) {
		
		String uriValue = null;
		String[] userNames = null;
		String[] roleNames = null;
		String[] jobReportUnits = null;
		
		processCommandLineArgsExport(args);

		setParameterValues();
		
		printoutOperationValues();
		
		setUpRepositoryConnections();
		
		if (!mProcessNothing) {
		
			if (mUriValuesList != null && mUriValuesList.size() > 0) {
				uriValue = (String) mUriValuesList.get(0);
			}
			
			if (mUserNamesList != null && mUserNamesList.size() > 0) {
				userNames = (String[]) mUserNamesList.toArray(new String[mUserNamesList.size()]);
			}
			
			if (mRoleNamesList != null && mRoleNamesList.size() > 0) {
				roleNames = (String[]) mRoleNamesList.toArray(new String[mRoleNamesList.size()]);
			}		
			
			if (mJobReportUnitsList != null && mJobReportUnitsList.size() > 0) {
				jobReportUnits = (String[]) mJobReportUnitsList.toArray(new String[mJobReportUnitsList.size()]);
			}
			
			if (!mProcessNothing) {
				
				ExportResource exporter = new ExportResource(mRepo, 
						mAuth,
						mReportScheduler,
						mContext, 
						uriValue,
						mProcessUsers,
						userNames,
						roleNames,
						mProcessJobReportUnits,
						jobReportUnits,
						mOutputDirName, 
						mOutputFileName,
						encodingProvider.getCharacterEncoding());
				
				exporter.process();
			}
		}
	}

	public boolean runImport(String[] args) {

		String prependPath = null;
		
		processCommandLineArgsImport(args);
		
		setParameterValues();
		
		printoutOperationValues();

		if (!mProcessNothing) {
			
			if (mPrependPathValuesList != null && mPrependPathValuesList.size() > 0) {
				
				String[] strArray = 
					(String[]) mPrependPathValuesList.toArray(new String[mPrependPathValuesList.size()]);
				prependPath = strArray[0];
			}
			
			setUpRepositoryConnections();
			
			ImportResource importer = new ImportResource(mRepo,
					mAuth,
					mReportSchedulerInternal,
					mContext, 
					mOutputDirName, 
					mOutputFileName, 
					prependPath,
					encodingProvider.getCharacterEncoding());
			
			return importer.process();
		}
		return false;
	}
	
	protected void setParameterValues() {
		
		if (mUseNamedOutputDir) {
		
			if (mOutputDirNameList != null && mOutputDirNameList.size() > 0) {
				
				mOutputDirName = (String) mOutputDirNameList.get(0);
				
			} else {
				System.out.println("ExportImportCommand: ERROR with output dir name");
			}
		} else {
			
			mOutputDirName = ExportResource.CATALOG_DIR_NAME;
		}
		
		
		if (mUseNamedOutputFile) {
			
			if (mOutputFileNameList != null && mOutputFileNameList.size() > 0) {
			
				mOutputFileName = (String) mOutputFileNameList.get(0);
			
			} else {
				System.out.println("ExportImportCommand: ERROR with output file name");
			}

		} else {
			
			mOutputFileName = ExportResource.CATALOG_FILE_NAME;
		}
	}
	
	private void processCommandLineArgsExport(String[] args) {
		
		mUriValuesList = new ArrayList();
		mUserNamesList = new ArrayList();
		mRoleNamesList = new ArrayList();
		mJobReportUnitsList = new ArrayList();
		mOutputDirNameList = new ArrayList();
		mOutputFileNameList = new ArrayList();
		
		
		// TEMP
//		System.out.println("ExportImportCommand: --- test: Print out all args");
//		for (int i = 0; i < args.length; i++) {
//			System.out.println("ExportImportCommand: ----- args[" + i + "]=" + args[i]);
//		}
		
		
		if (args.length <= 1) {
			System.out.println("\nError: No command line options found.\n");
			printUsage();
			System.exit(0);
		}
		
		for (int i = 1; i < args.length; i++) {		// NOTE: skip first arg (--export or --import)
		
			if (args[i].startsWith("--uri")) {
				
				if (args[i].length() == "--uri".length()) {
				
					mProcessUri = true;
					
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
													
							mUriValuesList.add(args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--uri option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--uri".length();
					if (args[i].length() > strLen) {
						mProcessUri = true;
						mUriValuesList.add(args[i].substring(strLen + 1));
					}
				}
					
			} else if (args[i].startsWith("--users")) {
				
				if (args[i].length() == "--users".length()) {
					
					mProcessUsers = true;
					
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
							
							mUserNamesList.add(args[j]);
							System.out.println("username value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--users option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--users".length();
					if (args[i].length() > strLen) {
						mProcessUsers = true;
						mUserNamesList.add(args[i].substring(strLen + 1));
						System.out.println("users value=" + args[i].substring(strLen + 1));
					}
				}

			} else if (args[i].startsWith("--roles")) {
				
				if (args[i].length() == "--roles".length()) {
				
					mProcessRoles = true;
					
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
							
							mRoleNamesList.add(args[j]);
							System.out.println("rolename value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--roles option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--roles".length();
					if (args[i].length() > strLen) {
						mProcessRoles = true;
						mRoleNamesList.add(args[i].substring(strLen + 1));
						System.out.println("roles value=" + args[i].substring(strLen + 1));
					}
				}
				
			} else if (args[i].startsWith("--scheduled-jobs")) {
				
				if (args[i].length() == "--scheduled-jobs".length()) {
					
					mProcessJobReportUnits = true;
					
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
							
							mJobReportUnitsList.add(args[j]);
							System.out.println("job reportunit name value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--job-schedules option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--scheduled-jobs".length();
					if (args[i].length() > strLen) {
						mProcessJobReportUnits = true;
						mJobReportUnitsList.add(args[i].substring(strLen + 1));
						System.out.println("scheduled jobs value=" + args[i].substring(strLen + 1));
					}
				}
				
			} else if (args[i].startsWith("--export-file")) {
				
				if (args[i].length() == "--export-file".length()) {

					mUseNamedOutputFile = true;
					
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
							
							mOutputFileNameList.add(args[j]);
							System.out.println("export filename value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--export-filename option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--export-file".length();
					if (args[i].length() > strLen) {
						mUseNamedOutputFile = true;
						mOutputFileNameList.add(args[i].substring(strLen + 1));
					}
				}
				
			} else if (args[i].startsWith("--export-path")) {
				
				if (args[i].length() == "--export-path".length()) {
				
					mUseNamedOutputDir = true;
						
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
							
							mOutputDirNameList.add(args[j]);
							System.out.println("export dirname value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--export-directory option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--export-path".length();
					if (args[i].length() > strLen) {
						mUseNamedOutputDir = true;
						mOutputDirNameList.add(args[i].substring(strLen + 1));
					}
				}

			} else if (args[i].equalsIgnoreCase("--dev-env")) {
				
				mDevEnv = true;
				
			} else if (args[i].equalsIgnoreCase("--verbose")) {
				
				mVerbose = true;
				
			} else if (args[i].equalsIgnoreCase("--help")) {
				
				printUsage();
				System.exit(0);
				
			} else {
				//System.out.println("ExportImportCommand: skipping option, option=" + args[i]);
				//log.warn("ExportImportCommand: unknown command line option, option=" + args[i]);
				//System.out.println("ExportImportCommand: unknown command line option, option=" + args[i]);
			}
		}
		
		// check for nothing to do
		if (mProcessUri == false && mProcessUsers == false 
				&& mProcessRoles == false && mProcessJobReportUnits == false) {
			
			mProcessNothing = true;
		}
		
	}

	/*
	 * Note: this method has a work-around for command line options returned
	 *       from a DOS batch file. --prepend-path=foobar arrives as
	 *       args[0] = --prepend-path
	 *       args[1] = foobar
	 *       
	 *       Under a linux script the same is:
	 *       args[0] = --prepend-path=foobar
	 * todo: fix in DOS batch script and clean up code       
	 */
	private void processCommandLineArgsImport(String[] args) {
		
		mPrependPathValuesList = new ArrayList();
		mOutputDirNameList = new ArrayList();
		mOutputFileNameList = new ArrayList();
		
		// TEMP
		System.out.println("ExportImportCommand: --- test: Print out all args");
		for (int i = 0; i < args.length; i++) {
			System.out.println("ExportImportCommand: ----- args[" + i + "]=" + args[i]);
		}
		
		for (int i = 1; i < args.length; i++) {		// NOTE: skip first arg (--export or --import)
			
			if (args[i].startsWith("--prepend-path")) {
				
				// work-around for DOS batch file arg parsing - fix in batch file
				// and clean up this code.
				if (args[i].length() == "--prepend-path".length()) {	// DOS breaks option into two bits
				
					mImportProcessPrependPath = true;
					
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
													
							mPrependPathValuesList.add(args[j]);
							System.out.println("prepend value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--prepend option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					// process from linux shell script
					int strLen = "--prepend-path".length();
					if (args[i].length() > strLen) {
						mImportProcessPrependPath = true;
						mPrependPathValuesList.add(args[i].substring(strLen + 1));
					}
				}
				
			} else if (args[i].startsWith("--import-file")) {
				
				if (args[i].length() == "--import-file".length()) {
					
					mUseNamedOutputFile = true;
					
					for (int j = i + 1; j < args.length; j++) {
						
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
							
							mOutputFileNameList.add(args[j]);
							System.out.println("import filename value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--import-filename option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--import-file".length();
					if (args[i].length() > strLen) {
						mUseNamedOutputFile = true;
						mOutputFileNameList.add(args[i].substring(strLen + 1));
					}
				}
				
			} else if (args[i].startsWith("--import-path")) {
				
				if (args[i].length() == "--import-path".length()) {
				
					mUseNamedOutputDir = true;
					
					for (int j = i + 1; j < args.length; j++) {
				
						if (args[j] != null && args[j].length() > 0 && !args[j].startsWith("-")) {
							
							mOutputDirNameList.add(args[j]);
							System.out.println("import dirname value=" + args[j]);
							
						} else {
							if (j == i) {
								System.out.println("--import-directory option does not include a value, args[j-" + j + "]=" + args[j]);
								printUsage();
							} else {
								break;
							}
						}
					}
				} else {
					int strLen = "--import-path".length();
					if (args[i].length() > strLen) {
						mUseNamedOutputDir = true;
						mOutputDirNameList.add(args[i].substring(strLen + 1));
					}
				}
				
			} else if (args[i].equalsIgnoreCase("--dev-env")) {
				
				mDevEnv = true;
				
			} else if (args[i].equalsIgnoreCase("--verbose")) {
				
				mVerbose = true;
				
			} else if (args[i].equalsIgnoreCase("--help")) {
				
				printUsage();
				System.exit(0);
				
			} else {
				
				// todo: ### if the arg[] starts with "-" then we have an unrecognized arg, set doNothing and print usage					

			}
		}
	}

	
	public void printUsage() {
		
		if (sExportOperation) {
					
			System.out.println("");
			System.out.println("usage: ji-export [OPTIONS]");
			
			System.out.println("Specify repository resources such as reports, images, folders, users,");
			System.out.println("roles, and scheduled jobs to export to an XML format file on disk.");
			System.out.println("The export file is known as a \"repository catalog\" file.");
			
			System.out.println("");
			System.out.println("Options:");
			System.out.println("  --uri             URI path of a repository resource");
			System.out.println("  --users           comma separated list of users to export");
			System.out.println("  --roles           comma separated list of roles to export");
			System.out.println("  --scheduled-jobs  comma separated list of scheduled jobs to export");
			System.out.println("                    (specify the URI path of the associated report)");
			System.out.println("  --export-path     path for export catalog file");
			System.out.println("  --export-file     name of export catalog file");
			System.out.println("  --help            print usage message");
			System.out.println("  --verbose         print flag settings used in the operation");
			System.out.println("");
			System.out.println("Examples:");
			System.out.println("  ji-export --uri=/reports/samples/AllAccounts");
			System.out.println("  ji-export --uri=/reports");
			System.out.println("  ji-export --uri=/images/JRLogo --users=jasperadmin,joeuser");
			System.out.println("  ji-export --roles=ROLE_USER,ROLE_ADMINISTRATOR");
			System.out.println("  ji-export --uri=/images/JRLogo --export-path=myDir/myDir2 --export-file=myFileName");
			System.out.println("  ji-export --scheduled-jobs=/reports/samples/AllAccounts");
			System.out.println("  ji-export --uri=/images/JRLogo --users=jasperadmin,joeuser");
			System.out.println("  ji-export --uri=/images/JRLogo --users=jasperadmin,joeuser");
			System.out.println("  ji-export --uri=/images/JRLogo --users=jasperadmin,joeuser");
			System.out.println("");
			System.out.println("Notes:");
			System.out.println("  The --uri option only allows for specifying one resource (ie a list of");
			System.out.println("  resources is not currently supported). A URI can specify a resource such as");
			System.out.println("  a ReportUnit. In this case, all associated resources such as images, ");
			System.out.println("  sub-reports, datasources, resource bundles, and classfiles will be exported.");
			System.out.println("  A URI can also specify a folder. If a folder is specified, the export operation");
			System.out.println("  will export all files and folders contained in the folder. In addition, it will");
			System.out.println("  recurse through all sub-folders.");
			System.out.println("");
			System.out.println("  If you export a user, the user information will be exported. In addition, the export");
			System.out.println("  will keep information on roles that the user belongs to. On the import operation,");
			System.out.println("  if the role names exist, the user will be added to these roles.");
			System.out.println("");
			System.out.println("  Special handling for roles: if you specify roles to export (and do not specify "); 
			System.out.println("  users to export), the associated users will also get exported. This is in order");
			System.out.println("  to support the functionality where you, for instance, specify two roles ROLE_USER ");
			System.out.println("  and ROLE_ADMINISTRATOR, and you would like all users who belong to these two roles to");
			System.out.println("  also be exported. This is the current default behavior for roles.");
			System.out.println("  Todo: enable this functionality with a --include-users-with-role option");
			System.out.println("");
			System.out.println("  If no catalog path or catalog file is specified, the default of ");
			System.out.println("  target/ji-catalog/ji-catalog.xml will be used.");
			System.out.println("");
			System.out.println("");
						
		} else if (sImportOperation) {

			System.out.println("");
			System.out.println("usage: ji-import [OPTIONS]");
			
			System.out.println("Read a repository catalog file from disk (created using the ji-export command) and ");
			System.out.println("create the named resources in the current JapserIntelligence application repository.");
			System.out.println("");
			System.out.println("Options:");
			System.out.println("  --prepend-path    string to prepend to a URI path for all imported resources");
			System.out.println("  --import-path     path for import catalog file");
			System.out.println("  --import-file     name of import catalog file");
			System.out.println("  --help            print usage message");
			System.out.println("  --verbose         print flag settings used in the operation");
			System.out.println("");
			System.out.println("Examples:");
			System.out.println("  ji-import");
			System.out.println("  ji-import --import-path=myDir/myDir2 --import-file=myFileName");
			System.out.println("  ji-import --prepend-path=myNewDir");
			System.out.println("");
			System.out.println("Notes:");
			System.out.println("  The prepend-path option is handy for avoiding uri path conflicts on an import operation.");
			System.out.println("  If the resource in the catalog file is \"/images/JRLogo\" and you set a prepend-path of \"myNewDir\"");
			System.out.println("  then the resource will be imported and created under the URI path \"/myNewDir/images/JRLogo \".");
			System.out.println("  So, if you are importing a set of resources into a repository and there is the possibility of");
			System.out.println("  URI naming conflicts in the target repository, adding a prepend-path can help avoid these ");
			System.out.println("  these naming conflicts.");
			System.out.println("");
			System.out.println("  On an import operation, if a resource is found in the target repository that has the same URI");
			System.out.println("  as the resource that is attempting to be created, the create operation will be skipped and");
			System.out.println("  the existing resource will be left unchanged (ie. an overwrite will not occur).");
			System.out.println("");
			System.out.println("");
			
			
		} else {
			System.out.println("ExportImportCommand: unknown usage");
		}		
	}
	
	
	
	protected void printoutOperationValues() {
		
		if (mVerbose) {
			if (sExportOperation) {
				System.out.println("");
				System.out.println("------ Export Operation Values: ");
				System.out.println("   --- mProcessUri=" + mProcessUri);
				System.out.println("   --- mProcessUsers=" + mProcessUsers);
				System.out.println("   --- mProcessRoles=" + mProcessRoles);
				System.out.println("   --- mProcessJobReportUnits=" + mProcessJobReportUnits);
				System.out.println("   --- mProcessNothing=" + mProcessNothing);
				System.out.println("   ---  additional: mUseNamedOutputDir=" + mUseNamedOutputDir);
				System.out.println("   ---  additional: mOutputDirName=" + mOutputDirName);
				System.out.println("   ---  additional: mUseNamedOutputFile=" + mUseNamedOutputFile);
				System.out.println("   ---  additional: mOutputFileName=" + mOutputFileName);
				System.out.println("   ---  additional: mVerbose=" + mVerbose);
				System.out.println("   ---  additional: mDevEnv=" + mDevEnv);
				System.out.println("");
				
			} else if (sImportOperation) {
				System.out.println("");
				System.out.println("------ Import Operation Values: ");
				System.out.println("   --- mImportProcessPrependPath=" + mImportProcessPrependPath);
				System.out.println("   ---  additional: mUseNamedOutputDir=" + mUseNamedOutputDir);
				System.out.println("   ---  additional: mOutputDirName=" + mOutputDirName);
				System.out.println("   ---  additional: mUseNamedOutputFile=" + mUseNamedOutputFile);
				System.out.println("   ---  additional: mOutputFileName=" + mOutputFileName);
				System.out.println("   ---  additional: mVerbose=" + mVerbose);
				System.out.println("   ---  additional: mDevEnv=" + mDevEnv);
				System.out.println("");
			} else {
				System.out.println("ERROR: unknown operation");
			}
		}
	}
	
	protected void setUpRepositoryConnections() {
		
		ClassPathXmlApplicationContext appContext = null;
		
		try {
			
			if (mDevEnv) {
				
				System.out.println("ExportImportCommand: - running DEV Env mode");
				appContext = 
					new ClassPathXmlApplicationContext(
							new String[] {"hibernateConfig.xml", 
				        		"viewService.xml", 
				        		"userAuthorityService.xml", 
				        		"engine.xml"});
				
				mRepo = (RepositoryService) appContext.getBean("repoService");  // bean name from dev
				
			} else {
				
				System.out.println("ExportImportCommand: - running PROD Env mode");

				appContext = 
					new ClassPathXmlApplicationContext(
							new String[] {"applicationContext-for-export.xml", 
				        		"applicationContext-report-scheduling-for-export.xml"});
				
				mRepo = (RepositoryService) appContext.getBean("repositoryService");
			}
			
		
		mAuth = (UserAuthorityService) appContext.getBean("userAuthorityService");
		mReportScheduler = (ReportSchedulingService) appContext.getBean(REPORT_SCHEDULING_SERVICE_BEAN_NAME);
		mReportSchedulerInternal = (ReportSchedulingInternalService) appContext.getBean(REPORT_SCHEDULING_INTERNAL_SERVICE_BEAN_NAME);
		encodingProvider = (CharacterEncodingProvider) appContext.getBean(ENCODING_PROVIDER_BEAN_NAME);
		
		this.mContext = new ExecutionContextImpl();
		
		} catch (Exception e) {
			System.out.println("ExportImportCommand: caught exception, exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	protected void tearDown() {
		
		
	}
	
}
