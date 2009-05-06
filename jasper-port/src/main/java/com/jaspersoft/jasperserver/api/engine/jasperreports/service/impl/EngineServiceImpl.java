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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.JarFile;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRParameterDefaultValuesEvaluator;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRResourcesUtil;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlTemplateLoader;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationDetailImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationResultImpl;
import com.jaspersoft.jasperserver.api.engine.common.domain.Request;
import com.jaspersoft.jasperserver.api.engine.common.domain.Result;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlsInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ReportExecuter;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DefaultProtectionDomainProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JRQueryExecuterAdapter;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.JarsClassLoader;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.ProtectionDomainProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryCacheMap;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryResourceClassLoader;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryResourceKey;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryUtil;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.ResourceCollector;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryCacheMap.CacheObject;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryCacheMap.ObjectCache;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.repo.RepositoryURLHandlerFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryCache;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryCacheableItem;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: AbstractAttributedObject.java 2140 2006-02-21 06:41:21Z tony $
 */
public class EngineServiceImpl implements EngineService, ReportExecuter, CompiledReportProvider
{
	protected static final Log log = LogFactory.getLog(EngineServiceImpl.class);

	private DataSourceServiceFactory dataSourceServiceFactories;
	protected RepositoryService repository;
	private SecurityContextProvider securityContextProvider;
	private String loggedInUserReportParameterName;
	private ProtectionDomainProvider reportJarsProtectionDomainProvider = new DefaultProtectionDomainProvider();
	private String reportParameterLabelKeyPrefix;

	private RepositoryCacheMap tempJarFiles;
	private RepositoryCache compiledReportsCache;
	private final ReferenceMap jarsClassLoaderCache;
	private final ReferenceMap resourcesClassLoaderCache;
	private final RepositoryCacheableItem cacheableCompiledReports;

	public EngineServiceImpl()
	{
		jarsClassLoaderCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.SOFT);
		resourcesClassLoaderCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.SOFT);
		cacheableCompiledReports = new CacheableCompiledReports();
	}

	/**
	 *
	 */
	public RepositoryService getRepositoryService()
	{
		return repository;
	}

	protected class CacheableCompiledReports implements RepositoryCacheableItem {
		private static final String CACHE_NAME = "JasperReport";

		public String getCacheName() {
			return CACHE_NAME;
		}

		public byte[] getData(ExecutionContext context, FileResource resource) {
			InputStream jrxmlData = getFileResourceDataStream(context, resource);
			JasperReport report = compileReport(jrxmlData);
			byte[] reportBytes = reportBytes(report);
			return reportBytes;
		}
	}

	public void setRepositoryService(RepositoryService repository) {
		this.repository = repository;

		createJarFilesCache();
	}

	public RepositoryCache getCompiledReportsCache() {
		return compiledReportsCache;
	}

	public void setCompiledReportsCache(RepositoryCache compiledReportsCache) {
		this.compiledReportsCache = compiledReportsCache;
	}

	protected final class TempJarFileCacheObject implements ObjectCache {
		public boolean isValid(Object o) {
			return true;
		}

		public Object create(ExecutionContext context, FileResource res) {
			try {
				File tempFile = File.createTempFile("report_jar", ".jar");
				tempFile.deleteOnExit();

				if (log.isInfoEnabled()) {
					log.info("Created temp jar file \"" + tempFile.getPath() + "\" for resource \"" + res.getURIString() + "\"");
				}

				byte[] data = getFileResourceData(context, res);
				OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(tempFile));
				try {
					fileOut.write(data);
					fileOut.flush();
				} finally {
					fileOut.close();
				}

				JarFile jarFile = new JarFile(tempFile);

				return jarFile;
			} catch (IOException e) {
				log.error(e, e);
				throw new JSExceptionWrapper(e);
			}
		}

		public void release(Object o) {
			dispose((JarFile) o);
		}
	}

	protected void createJarFilesCache() {
		this.tempJarFiles = new RepositoryCacheMap(this.repository, new TempJarFileCacheObject());
	}

	protected InputStream getFileResourceDataStream(ExecutionContext context, FileResource fileResource) {
		InputStream data;
		if (fileResource.hasData()) {
			data = fileResource.getDataStream();
		} else {
			FileResourceData resourceData = repository.getResourceData(context, fileResource.getURIString());
			data = resourceData.getDataStream();
		}
		return data;
	}

	protected CacheObject getCacheJarFile(ExecutionContext context, FileResource jar, boolean cache) {
		return tempJarFiles.cache(context, jar, cache);
	}

	protected byte[] getFileResourceData(ExecutionContext context, FileResource fileResource) {
		byte[] data;
		if (fileResource.hasData()) {
			data = fileResource.getData();
		} else {
			FileResourceData resourceData = repository.getResourceData(context, fileResource.getURIString());
			data = resourceData.getData();
		}
		return data;
	}

	public DataSourceServiceFactory getDataSourceServiceFactories() {
		return dataSourceServiceFactories;
	}

	public void setDataSourceServiceFactories(DataSourceServiceFactory dataSourceServiceFactories) {
		this.dataSourceServiceFactories = dataSourceServiceFactories;
	}

	/**
	 *
	 */
	public Result execute(ExecutionContext context, Request request)
	{
		ReportUnitRequestBase reportUnitRequest = (ReportUnitRequestBase) request;
		return reportUnitRequest.execute(context, this);
	}


	/**
	 *
	 */
	public void exportToPdf(ExecutionContext context, String reportUnitURI, Map exportParameters)
	{
		ReportUnit reportUnit = (ReportUnit) getRepositoryResource(context, reportUnitURI);
		setThreadRepositoryContext(context, null, reportUnitURI, false);
		try {
			OrigContextClassLoader origContext = setContextClassLoader(context, reportUnit, false);
			try {
				exportParameters.put(JRExporterParameter.URL_HANDLER_FACTORY, RepositoryURLHandlerFactory.getInstance());
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setParameters(exportParameters);
				exporter.exportReport();
			} finally {
				revert(origContext);
			}
		} catch(JRException e) {
			log.error("Error while exporting report to PDF", e);
			throw new JSExceptionWrapper(e);
		} finally {
			RepositoryUtil.clearThreadRepositoryContext();
		}
	}


	protected void setThreadRepositoryContext(ExecutionContext context, ReportUnit reportUnit, String reportUnitURI, boolean inMemoryUnit)
	{
		RepositoryContext repositoryContext = new RepositoryContext();
		repositoryContext.setRepository(repository);
		repositoryContext.setContextResourceURI(reportUnitURI);
		if (inMemoryUnit) {
			repositoryContext.setReportUnit(reportUnit);
		}
		repositoryContext.setExecutionContext(context);
		repositoryContext.setCompiledReportProvider(this);
		RepositoryUtil.setThreadRepositoryContext(repositoryContext);

	}


	protected ReportUnitResult fillReport(ExecutionContext context, ReportUnit reportUnit, Map parameters, boolean inMemoryUnit) {

		setThreadRepositoryContext(context, reportUnit, reportUnit.getURIString(), inMemoryUnit);
		try {
			Map unitResources = loadFinalResources(context, reportUnit);
			OrigContextClassLoader origContext = setContextClassLoader(context, unitResources, inMemoryUnit);
			try {
				JasperReport report = getJasperReport(context, reportUnit, inMemoryUnit);
				Map reportParameters = getReportParameters(context, parameters);
				setReportTemplates(context, unitResources, reportParameters);

				ReportDataSource datasource = null;
				ResourceReference queryRef = reportUnit.getQuery();
				Query query = queryRef == null ? null : (Query) getFinalResource(context, queryRef);
				if (query != null && query.getDataSource() != null) {
					datasource = (ReportDataSource) getFinalResource(context, query.getDataSource());
				}

				ResourceReference dsRef = reportUnit.getDataSource();
				if (datasource == null && dsRef != null) {
					datasource = (ReportDataSource) getFinalResource(context, dsRef);
				}

				return fillReport(context, report, reportParameters, datasource, query);
			} finally {
				revert(origContext);
			}
		} finally {
			RepositoryUtil.clearThreadRepositoryContext();
		}
	}

	protected Map getReportParameters(ExecutionContext context, Map requestParameters) {
		Map reportParameters = new HashMap();

		reportParameters.put(JRParameter.REPORT_URL_HANDLER_FACTORY, RepositoryURLHandlerFactory.getInstance());

		if (context != null && context.getLocale() != null
				&& reportParameters.get(JRParameter.REPORT_LOCALE) == null) {
			reportParameters.put(JRParameter.REPORT_LOCALE, context.getLocale());
		}

		if (context != null && context.getTimeZone() != null) {
			reportParameters.put(JRParameter.REPORT_TIME_ZONE, context.getTimeZone());
		}

		if (getSecurityContextProvider() != null) {
			User user = getSecurityContextProvider().getContextUser();
			if (user != null) {
				user.setPassword(null);
				reportParameters.put(getLoggedInUserReportParameterName(), user);
			}
		}

		if (requestParameters != null) {
			reportParameters.putAll(requestParameters);
		}

		return reportParameters;
	}

	protected void setReportTemplates(ExecutionContext context, Map unitResources, Map reportParameters) {
		if (!reportParameters.containsKey(JRParameter.REPORT_TEMPLATES)) {
			List templates = new ArrayList();
			for (Iterator it = unitResources.values().iterator(); it.hasNext();) {
				FileResource resource = (FileResource) it.next();
				if (resource.getFileType().equals(FileResource.TYPE_STYLE_TEMPLATE)) {
					JRTemplate template = loadTemplate(context, resource);
					templates.add(template);
				}
			}
			reportParameters.put(JRParameter.REPORT_TEMPLATES, templates);
		}
	}

	protected JRTemplate loadTemplate(ExecutionContext context, FileResource resource) {
		InputStream templateDataStream = getFileResourceDataStream(context, resource);
		try {
			return JRXmlTemplateLoader.load(templateDataStream);
		} catch (JRRuntimeException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	protected void revert(OrigContextClassLoader origContext) {
		if (origContext.set) {
			Thread.currentThread().setContextClassLoader(origContext.origClassLoader);

			for (Iterator it = origContext.jars.iterator(); it.hasNext();) {
				CacheObject cacheJarFile = (CacheObject) it.next();
				if (!cacheJarFile.isCached()) {
					JarFile jarFile = (JarFile) cacheJarFile.getObject();
					dispose(jarFile);
				}
			}
		}
	}

	protected static class OrigContextClassLoader {
		public final boolean set;
		public final ClassLoader origClassLoader;
		public final List jars;

		public static final OrigContextClassLoader NOT_SET = new OrigContextClassLoader(false);

		private OrigContextClassLoader(boolean set) {
			this.set = set;
			this.origClassLoader = null;
			this.jars = null;
		}

		public OrigContextClassLoader(ClassLoader origClassLoader, List jars) {
			this.set = true;
			this.origClassLoader = origClassLoader;
			this.jars = jars;
		}
	}

	protected OrigContextClassLoader setContextClassLoader(ExecutionContext context, ReportUnit reportUnit, boolean inMemoryUnit) {
		Map unitResources = loadFinalResources(context, reportUnit);
		return setContextClassLoader(context, unitResources, inMemoryUnit);
	}

	protected Map loadFinalResources(ExecutionContext context, ReportUnit reportUnit) {
		List resources = reportUnit.getResources();
		Map finalResources = new LinkedHashMap();
		if (resources != null && !resources.isEmpty()) {
			for (Iterator it = resources.iterator(); it.hasNext();) {
				ResourceReference resRef = (ResourceReference) it.next();
				FileResource resource = getFinalFileResource(context, resRef);
				finalResources.put(resRef, resource);
			}
		}

		return finalResources;
	}

	protected OrigContextClassLoader setContextClassLoader(ExecutionContext context, Map unitResources, boolean inMemoryUnit) {
		Thread thread = Thread.currentThread();
		ClassLoader origClassLoader = thread.getContextClassLoader();
		ClassLoader jarsClassLoader;
		ClassLoader newClassLoader = null;

		List jarFiles = getJarFiles(context, unitResources, !inMemoryUnit);
		if (jarFiles.isEmpty()) {
			jarsClassLoader = origClassLoader;
		} else {
			newClassLoader = jarsClassLoader = getJarsClassLoader(origClassLoader, jarFiles);
		}

		Map resourceBundleKeys = getResourceBundleKeys(context, unitResources);
		if (!resourceBundleKeys.isEmpty()) {
			newClassLoader = getResourcesClassLoader(jarsClassLoader, resourceBundleKeys, inMemoryUnit);
		}

		OrigContextClassLoader origContext;
		if (newClassLoader == null) {
			origContext = OrigContextClassLoader.NOT_SET;
		} else {
			origContext = new OrigContextClassLoader(origClassLoader, jarFiles);
			thread.setContextClassLoader(newClassLoader);
		}

		return origContext;
	}

	protected ClassLoader getJarsClassLoader(ClassLoader origClassLoader, List jarFiles) {
		boolean caching = true;
		for (Iterator it = jarFiles.iterator(); caching && it.hasNext();) {
			CacheObject cacheJarFile = (CacheObject) it.next();
			caching &= cacheJarFile.isCached();
		}

		ClassLoader classLoader;
		if (caching) {
			Map childrenClassLoaders;
			synchronized (jarsClassLoaderCache) {
				childrenClassLoaders = (Map) jarsClassLoaderCache.get(origClassLoader);
				if (childrenClassLoaders == null) {
					childrenClassLoaders = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT);
					jarsClassLoaderCache.put(origClassLoader, childrenClassLoaders);
				}
			}
			Object classLoaderKey = getJarFileNames(jarFiles);
			synchronized (childrenClassLoaders) {
				classLoader = (ClassLoader) childrenClassLoaders.get(classLoaderKey);
				if (classLoader == null) {
					if (log.isDebugEnabled()) {
						log.debug("Creating class loader for parent " + origClassLoader + " and jars " + classLoaderKey);
					}
					classLoader = createJarsClassLoader(origClassLoader, jarFiles);
					childrenClassLoaders.put(classLoaderKey, classLoader);
				}
			}
		} else {
			classLoader = createJarsClassLoader(origClassLoader, jarFiles);
		}
		return classLoader;
	}

	protected ClassLoader createJarsClassLoader(ClassLoader origClassLoader, List jarFiles) {
		JarFile[] jars = new JarFile[jarFiles.size()];
		int i = 0;
		for (Iterator it = jarFiles.iterator(); it.hasNext(); ++i) {
			jars[i] = (JarFile) ((CacheObject) it.next()).getObject();
		}

		return new JarsClassLoader(jars, origClassLoader,
				reportJarsProtectionDomainProvider.getProtectionDomain());
	}

	private Object getJarFileNames(List jarFiles) {
		List jarFileNames = new ArrayList(jarFiles.size());
		for (Iterator it = jarFiles.iterator(); it.hasNext();) {
			JarFile jar = (JarFile) ((CacheObject) it.next()).getObject();
			jarFileNames.add(jar.getName());
		}
		return jarFileNames;
	}

	protected ClassLoader getResourcesClassLoader(ClassLoader parent, Map resourceBundleKeys, boolean inMemoryUnit) {
		ClassLoader repositoryResourceClassLoader;
		if (inMemoryUnit) {
			repositoryResourceClassLoader = new RepositoryResourceClassLoader(parent, resourceBundleKeys, true);
		} else {
			Map childrenClassLoaders;
			synchronized (resourcesClassLoaderCache) {
				childrenClassLoaders = (Map) resourcesClassLoaderCache.get(parent);
				if (childrenClassLoaders == null) {
					childrenClassLoaders = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT);
					resourcesClassLoaderCache.put(parent, childrenClassLoaders);
				}
			}
			synchronized (childrenClassLoaders) {
				repositoryResourceClassLoader = (ClassLoader) childrenClassLoaders.get(resourceBundleKeys);
				if (repositoryResourceClassLoader == null) {
					if (log.isDebugEnabled()) {
						log.debug("Creating class loader for parent " + parent + " and resources " + resourceBundleKeys);
					}
					repositoryResourceClassLoader = new RepositoryResourceClassLoader(parent, resourceBundleKeys, false);
					childrenClassLoaders.put(resourceBundleKeys, repositoryResourceClassLoader);
				}
			}
		}
		return repositoryResourceClassLoader;
	}

	protected List getJarFiles(ExecutionContext context, Map unitResources, boolean cache) {
		List jarFiles = new ArrayList();
		for (Iterator it = unitResources.values().iterator(); it.hasNext();) {
			FileResource resource = (FileResource) it.next();
			if (resource.getFileType().equals(FileResource.TYPE_JAR)) {
				CacheObject cacheJarFile = getCacheJarFile(context, resource, cache);
				jarFiles.add(cacheJarFile);
			}
		}
		return jarFiles;
	}

	protected Map getResourceBundleKeys(ExecutionContext context, Map unitResources) {
		Map resourceBundleKeys = new HashMap();
		for (Iterator it = unitResources.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			ResourceReference resRef = (ResourceReference) entry.getKey();
			FileResource finalResource = (FileResource) entry.getValue();
			if (finalResource.getFileType().equals(FileResource.TYPE_RESOURCE_BUNDLE)) {
				String resName;
				if (resRef.isLocal()) {
					resName = resRef.getLocalResource().getName();
				} else {
					resName = finalResource.getName();//TODO first reference name?
				}
				resourceBundleKeys.put(resName, new RepositoryResourceKey(finalResource));
			}
		}
		return resourceBundleKeys;
	}

	protected JasperReport getJasperReport(ExecutionContext context, ReportUnit reportUnit, boolean inMemoryUnit) {
		FileResource reportRes = (FileResource) getFinalResource(context,
				reportUnit.getMainReport());
		JasperReport report;
		try {
			if (inMemoryUnit) {
				InputStream fileResourceData = getFileResourceDataStream(context, reportRes);
				report = compileReport(fileResourceData);
			} else {
				InputStream compiledReport = getCompiledReport(context, reportRes);
				try {
					report = (JasperReport) JRLoader.loadObject(compiledReport);
				} catch (JRException e) {
					Throwable cause = e.getCause();
					if (cause == null || !(cause instanceof InvalidClassException)) {
						throw e;
					}
					
					if (log.isInfoEnabled()) {
						log.info("InvalidClassException caught while loading compiled report, clearing the compiled report cache");
					}
					clearCompiledReportCache();

					//recompiling the report
					compiledReport = getCompiledReport(context, reportRes);
					report = (JasperReport) JRLoader.loadObject(compiledReport);
				}
			}
			return report;
		} catch (JRException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected void clearCompiledReportCache() {
		compiledReportsCache.clearCache(cacheableCompiledReports);
	}
	
	protected ReportUnitResult fillReport(ExecutionContext context, JasperReport report, Map reportParameters, ReportDataSource datasource, Query query) {
		ReportDataSourceService dataSourceService = null;
		boolean dsClosing = false;
		try {
			if (datasource != null) {
				dataSourceService = createDataSourceService(datasource);
				dataSourceService.setReportParameterValues(reportParameters);
			}

			JasperPrint print;
			if (query == null) {
				print = JasperFillManager.fillReport(report, reportParameters);
			} else {
				print = fillQueryReport(context, report, reportParameters, query);
			}

			dsClosing = true;
			if (dataSourceService != null) {
				dataSourceService.closeConnection();
				dataSourceService = null;
			}

			return
				new ReportUnitResult(
					print,
					(JRVirtualizer)reportParameters.get(JRParameter.REPORT_VIRTUALIZER)
					);
		} catch (JRException e) {
			log.error("Error while filling report", e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (!dsClosing && dataSourceService != null) {
				//only exception cases
				try {
					dataSourceService.closeConnection();
				} catch (Exception e) {
					log.error("Error while closing data source connection", e);
				}
			}
		}
	}

	protected JasperPrint fillQueryReport(ExecutionContext context, JasperReport report, Map reportParameters, Query query) throws JRException {
		JRQueryExecuter queryExecuter = JRQueryExecuterAdapter.createQueryExecuter(report, reportParameters, query);
		boolean closing = false;
		try {
			JRDataSource reportDatasource = queryExecuter.createDatasource();
			JasperPrint printReport = JasperFillManager.fillReport(report, reportParameters, reportDatasource);
			closing = true;
			queryExecuter.close();
			return printReport;
		} finally {
			if (!closing) {
				queryExecuter.close();
			}
		}
	}

	public ReportDataSourceService createDataSourceService(ReportDataSource dataSource) {
		ReportDataSourceServiceFactory factory = (ReportDataSourceServiceFactory) getDataSourceServiceFactories().getBean(dataSource.getClass());
		return factory.createService(dataSource);
	}

	/**
	 *
	 */
	public Resource[] getResources(ResourceReference jrxmlReference)
	{
		//TODO context?
		FileResource jrxml = (FileResource) getFinalResource(null, jrxmlReference);
		return ResourceCollector.getResources(getFileResourceDataStream(null, jrxml));
	}

	protected Resource getRepositoryResource(ExecutionContext context, String uri)
	{
		return getRepositoryService().getResource(context, uri);
	}

	protected Resource getFinalResource(ExecutionContext context, ResourceReference res) {
		Resource finalRes;
		if (res.isLocal()) {
			finalRes = res.getLocalResource();
		} else {
			finalRes = getRepositoryResource(context, res.getReferenceURI());
		}
		return finalRes;
	}

	protected FileResource getFinalFileResource(ExecutionContext context, ResourceReference resRef) {
		FileResource res = (FileResource) getFinalResource(context, resRef);
		while (res.isReference()) {
			res = (FileResource) getRepositoryResource(context, res.getReferenceURI());
		}
		return res;
	}

	public ValidationResult validate(ExecutionContext context, ReportUnit reportUnit) {
		OrigContextClassLoader origContext = setContextClassLoader(context, reportUnit, true);
		ValidationResultImpl result = new ValidationResultImpl();
		try {
			ResourceReference mainReport = reportUnit.getMainReport();
			if (mainReport != null) {
				validateJRXML(context, result, mainReport);
			}

			List resources = reportUnit.getResources();
			if (resources != null && !resources.isEmpty()) {
				for (Iterator iter = resources.iterator(); iter.hasNext();) {
					ResourceReference resource = (ResourceReference) iter.next();
					validateJRXML(context, result, resource);
				}
			}
		} finally {
			revert(origContext);
		}
		return result;
	}

	protected void validateJRXML(ExecutionContext context, ValidationResultImpl result, ResourceReference resourceRef) {
		FileResource resource = getFinalFileResource(context, resourceRef);
		if (resource.getFileType().equals(FileResource.TYPE_JRXML)) {
			try {
				JasperCompileManager.compileReport(getFileResourceDataStream(context, resource));
			} catch (JRException e) {
				ValidationDetailImpl detail = new ValidationDetailImpl();
				detail.setValidationClass(FileResource.class);
				detail.setName(resource.getName());
				detail.setLabel(resource.getLabel());
				detail.setResult(ValidationResult.STATE_ERROR);
				detail.setException(e);
				detail.setMessage(e.getMessage());
				result.addValidationDetail(detail);
			}
		}
	}

	public ReportUnitResult executeReport(ExecutionContext context, String reportUnitURI, Map parameters) {
		ReportUnit reportUnit = (ReportUnit) getRepositoryResource(context, reportUnitURI);
		return fillReport(context, reportUnit, parameters, false);
	}

	public ReportUnitResult executeReport(ExecutionContext context, ReportUnit reportUnit, Map parameters) {
		return fillReport(context, reportUnit, parameters, true);
	}

	public InputStream getCompiledReport(ExecutionContext context, InputStream jrxmlData) {
		JasperReport report = compileReport(jrxmlData);
		byte[] reportBytes = reportBytes(report);
		return new ByteArrayInputStream(reportBytes);
	}

	protected JasperReport compileReport(InputStream jrxmlData) {
		try {
			JasperDesign design = JRXmlLoader.load(jrxmlData);
			JasperReport report = JasperCompileManager.compileReport(design);
			return report;
		} catch (JRException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected InputStream getCompiledReport(ExecutionContext context, FileResource jrxml) {
		return compiledReportsCache.cache(context, jrxml, cacheableCompiledReports);
	}

	protected byte[] reportBytes(JasperReport report) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			JRSaver.saveObject(report, bout);
			byte[] reportBytes = bout.toByteArray();
			return reportBytes;
		} catch (JRException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	public InputStream getCompiledReport(ExecutionContext context, String jrxmlURI) {
		return compiledReportsCache.cache(context, jrxmlURI, cacheableCompiledReports);
	}

	public JasperReport getMainJasperReport(ExecutionContext context, String reportUnitURI) {
		ReportUnit reportUnit = (ReportUnit) getRepositoryResource(context, reportUnitURI);
		OrigContextClassLoader origContext = setContextClassLoader(context, reportUnit, false);
		try {
			JasperReport jasperReport = getJasperReport(context, reportUnit, false);
			return jasperReport;
		} finally {
			revert(origContext);
		}
	}

	public void release() {
		tempJarFiles.release();
	}

	protected void dispose(JarFile jarFile) {
		try {
			jarFile.close();
		} catch (IOException e) {
			log.warn("Unable to close jar file \"" + jarFile.getName() + "\"", e);
		}
		File file = new File(jarFile.getName());
		if (file.exists() && !file.delete()) {
			log.warn("Unable to delete jar file \"" + jarFile.getName() + "\"");
		}
	}

	public void clearCaches(Class resourceItf, String resourceURI) {
		if (FileResource.class.isAssignableFrom(resourceItf)) {
			//TODO check JRXML type
			compiledReportsCache.clearCache(resourceURI, cacheableCompiledReports);

			tempJarFiles.remove(resourceURI);
		}
	}

	public String getLoggedInUserReportParameterName() {
		return loggedInUserReportParameterName;
	}

	public void setLoggedInUserReportParameterName(
			String loggedInUserReportParameterName) {
		this.loggedInUserReportParameterName = loggedInUserReportParameterName;
	}

	public SecurityContextProvider getSecurityContextProvider() {
		return securityContextProvider;
	}

	public void setSecurityContextProvider(
			SecurityContextProvider securityContextProvider) {
		this.securityContextProvider = securityContextProvider;
	}


	public OrderedMap executeQuery(ExecutionContext context,
			ResourceReference queryReference, String keyColumn, String[] resultColumns,
			ResourceReference defaultDataSourceReference) {
		Query query = (Query) getFinalResource(context, queryReference);

		ResourceReference dataSourceReference = query.getDataSource();
		if (dataSourceReference == null) {
			dataSourceReference = defaultDataSourceReference;
		}

		if (dataSourceReference == null) {
			return null;
		}

		ReportDataSource dataSource = (ReportDataSource) getFinalResource(context, dataSourceReference);

		ReportDataSourceService dataSourceService = createDataSourceService(dataSource);
		boolean dsClosing = false;
		try {
			Map parameters = new HashMap();
			dataSourceService.setReportParameterValues(parameters);

			OrderedMap result = JRQueryExecuterAdapter.executeQuery(query, keyColumn, resultColumns, parameters);

			dsClosing = true;
			if (dataSourceService != null) {
				dataSourceService.closeConnection();
				dataSourceService = null;
			}

			return result;
		} finally {
			if (!dsClosing && dataSourceService != null) {
				//only exception cases
				try {
					dataSourceService.closeConnection();
				} catch (Exception e) {
					log.error("Error while closing data source connection", e);
				}
			}
		}

	}

	public ResourceLookup[] getDataSources(ExecutionContext context, String queryLanguage) {
		ResourceLookup[] datasources;
		if (queryLanguage == null) {
			datasources = repository.findResource(context, FilterCriteria.createFilter(ReportDataSource.class));
		} else {
			Set dataSourceTypes = dataSourceServiceFactories.getSupportingDataSourceTypes(queryLanguage);
			if (dataSourceTypes == null || dataSourceTypes.isEmpty()) {
				datasources = null;
			} else {
				FilterCriteria[] criteria = new FilterCriteria[dataSourceTypes.size()];
				int i = 0;
				for (Iterator it = dataSourceTypes.iterator(); it.hasNext(); ++i) {
					Class type = (Class) it.next();
					criteria[i] = FilterCriteria.createFilter(type);
				}
				datasources = repository.findResources(context, criteria);
			}
		}
		return datasources;
	}

	public String getQueryLanguage(ExecutionContext context, ResourceReference jrxmlResource) {
		JasperDesign jasperDesign = loadJRXML(context, jrxmlResource);
		JRQuery query = jasperDesign.getQuery();
		return query == null ? null : query.getLanguage();
	}

	protected JasperDesign loadJRXML(ExecutionContext context, ResourceReference jrxmlResource) {
		JasperDesign jasperDesign;
		FileResource jrxmlRes = getFinalFileResource(context, jrxmlResource);
		InputStream jrxmlData = getFileResourceDataStream(context, jrxmlRes);
		boolean close = true;
		try {
			jasperDesign = JRXmlLoader.load(jrxmlData);

			close = false;
			jrxmlData.close();
		} catch (JRException e) {
			log.error("Error parsing JRXML", e);
			throw new JSExceptionWrapper(e);
		} catch (IOException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (close) {
				try {
					jrxmlData.close();
				} catch (IOException e) {
					log.error(e, e);
				}
			}
		}
		return jasperDesign;
	}

	public Set getDataSourceTypes(ExecutionContext context, String queryLanguage) {
		return dataSourceServiceFactories.getSupportingDataSourceTypes(queryLanguage);
	}

	public Map getReportInputControlDefaultValues(ExecutionContext context, String reportURI, Map initialParameters) {
		ReportUnit reportUnit = (ReportUnit) getRepositoryResource(context, reportURI);
		List inputControls = reportUnit.getInputControls();
		if (inputControls == null || inputControls.isEmpty()) {
			return null;
		}

		setThreadRepositoryContext(context, reportUnit, reportUnit.getURIString(), false);
		try {
			OrigContextClassLoader origContext = setContextClassLoader(context, reportUnit, false);
			try {
				JasperReport report = getJasperReport(context, reportUnit, false);
				initialParameters = getReportParameters(context, initialParameters);

				Map paramValues = JRParameterDefaultValuesEvaluator.evaluateParameterDefaultValues(report, initialParameters);
				Map inputValues = new HashMap();
				for (Iterator it = inputControls.iterator(); it.hasNext();) {
					ResourceReference inputControlRef = (ResourceReference) it.next();
					InputControl inputControl = (InputControl) getFinalResource(context, inputControlRef);
					Object value = paramValues.get(inputControl.getName());
					inputValues.put(inputControl.getName(), value);
				}

				return inputValues;
			} catch (JRException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				revert(origContext);
			}
		} finally {
			RepositoryUtil.clearThreadRepositoryContext();
		}
	}

	public ProtectionDomainProvider getReportJarsProtectionDomainProvider() {
		return reportJarsProtectionDomainProvider;
	}

	public void setReportJarsProtectionDomainProvider(ProtectionDomainProvider protectionDomainProvider) {
		this.reportJarsProtectionDomainProvider = protectionDomainProvider;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.engine.common.service.EngineService#getReportInputControlsInformation(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String, java.util.Map)
	 */
	public ReportInputControlsInformation getReportInputControlsInformation(
			ExecutionContext context, String reportURI, Map initialParameters) {
		ReportUnit reportUnit = (ReportUnit) getRepositoryResource(context, reportURI);
		List inputControls = reportUnit.getInputControls();
		if (inputControls == null || inputControls.isEmpty()) {
			return null;
		}

		setThreadRepositoryContext(context, reportUnit, reportUnit.getURIString(), false);
		try {
			OrigContextClassLoader origContext = setContextClassLoader(context, reportUnit, false);
			try {
				JasperReport report = getJasperReport(context, reportUnit, false);
				initialParameters = getReportParameters(context, initialParameters);
				
				Map parametersMap = new HashMap();
				JRParameter[] params = report.getParameters();
				for (int i = 0; i < params.length; i++) {
					parametersMap.put(params[i].getName(), params[i]);
				}
				
				ResourceBundle reportBundle = loadResourceBundle(context, report);

				Map paramValues = JRParameterDefaultValuesEvaluator.evaluateParameterDefaultValues(report, initialParameters);
				ReportInputControlsInformationImpl infos = new ReportInputControlsInformationImpl();
				for (Iterator it = inputControls.iterator(); it.hasNext();) {
					ResourceReference inputControlRef = (ResourceReference) it.next();
					InputControl inputControl = (InputControl) getFinalResource(context, inputControlRef);
					
					String name = inputControl.getName();
					JRParameter param = (JRParameter) parametersMap.get(name);
					if (param != null) {
						JasperReportInputControlInformation info = new JasperReportInputControlInformation();
						info.setReportParameter(param);
						
						Object value = paramValues.get(name);
						info.setDefaultValue(value);
						
						String label = inputControl.getLabel();
						if (reportBundle != null) {
							String paramLabelKey = reportParameterLabelKeyPrefix + name;
							try {
								label = reportBundle.getString(paramLabelKey);
							} catch (MissingResourceException e) {
								//nothing
							}
						}
						info.setPromptLabel(label);
						
						infos.setInputControlInformation(name, info);
					}
				}

				return infos;
			} catch (JRException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				revert(origContext);
			}
		} finally {
			RepositoryUtil.clearThreadRepositoryContext();
		}
	}

	protected ResourceBundle loadResourceBundle(ExecutionContext context, JasperReport report) {
		ResourceBundle bundle = null;
		String baseName = report.getResourceBundle();
		if (baseName != null) {
			Locale locale = context == null ? null : context.getLocale();
			if (locale == null) {
				locale = Locale.getDefault();
			}
			
			bundle = JRResourcesUtil.loadResourceBundle(baseName, locale);
		}
		return bundle;
	}
	
	public String getReportParameterLabelKeyPrefix() {
		return reportParameterLabelKeyPrefix;
	}

	public void setReportParameterLabelKeyPrefix(
			String reportParameterLabelKeyPrefix) {
		this.reportParameterLabelKeyPrefix = reportParameterLabelKeyPrefix;
	}

}
