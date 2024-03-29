/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package com.jaspersoft.jasperserver.export.modules.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.repository.beans.FolderBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.RepositoryObjectPermissionBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceReferenceBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceExporter.java 13374 2008-05-05 11:45:36Z lucian $
 */
public class ResourceExporter extends BaseExporterModule implements ResourceExportHandler {
	
	private static final Log log = LogFactory.getLog(ResourceExporter.class);

	protected static class QueuedUri {
		private final String uri;
		private final boolean ignoreMissing;
		
		public QueuedUri(String uri, boolean ignoreMissing) {
			this.uri = uri;
			this.ignoreMissing = ignoreMissing;
		}

		public String getUri() {
			return uri;
		}

		public boolean isIgnoreMissing() {
			return ignoreMissing;
		}
	}
	
	protected static class UrisQueue {
		private final LinkedList queue = new LinkedList();
		
		public boolean isEmpty() {
			return queue.isEmpty();
		}
		
		public void push(String uri, boolean ignoreMissing) {
			queue.addLast(new QueuedUri(uri, ignoreMissing));
		}
		
		public QueuedUri pop() {
			return (QueuedUri) queue.removeFirst();
		}
	}

	private ResourceModuleConfiguration configuration;
	private String urisArgument;
	private String permissionsArgument;
	
	private String[] uris;
	private boolean exportPermissions;
	private UrisQueue urisQueue;
	private Set exportedURIs;
	
	public void init(ExporterModuleContext moduleContext) {
		super.init(moduleContext);
		
		initProcess();		
		uris = exportEverything ? new String[]{"/"} : getParameterValues(getUrisArgument());
		exportPermissions = exportEverything || hasParameter(getPermissionsArgument());
	}

	protected boolean isToProcess() {
		return uris != null && uris.length > 0;
	}

	public void process() {
		mkdir(configuration.getResourcesDirName());
		
		for (int i = 0; i < uris.length; i++) {
			processUri(uris[i], true, false);
		}
		
		while (!urisQueue.isEmpty()) {
			QueuedUri queuedUri = urisQueue.pop();
			processUri(queuedUri.getUri(), false, queuedUri.isIgnoreMissing());
		}
	}

	protected void initProcess() {
		urisQueue = new UrisQueue();
		exportedURIs = new HashSet();
	}

	protected void markExported(String uri) {
		exportedURIs.add(uri);
	}

	protected boolean alreadyExported(String uri) {
		return exportedURIs.contains(uri);
	}

	protected void processUri(String uri, boolean entry, boolean ignoreMissing) {
		if (alreadyExported(uri)) {
			return;
		}
		
		Resource resource = configuration.getRepository().getResource(executionContext, uri);
		if (resource == null) {
			Folder folder = configuration.getRepository().getFolder(executionContext, uri);
			if (folder == null) {
				if (!ignoreMissing) {
					throw new JSException("jsexception.uri.not.found", new Object[] {uri});
				}
				
				commandOut.info("URI " + uri + " was not found in the repository, skipping from export");
			} else {
				if (entry) {
					addFolderIndexElement(uri);
				}

				exportFolder(folder);
			}
		} else {
			if (entry) {
				addResourceIndexElement(uri);
			}
			
			exportResource(resource);
		}
	}

	protected void addFolderIndexElement(String uri) {
		Element folderElement = getIndexElement().addElement(configuration.getFolderIndexElement());
		folderElement.addText(uri);
	}

	protected void exportFolder(Folder folder) {
		String uri = folder.getURIString();
		if (alreadyExported(uri)) {
			return;
		}

		commandOut.debug("Exporting repository folder " + uri);
		
		List subFolders = getSubfolders(uri);		
		ResourceLookup[] resources = getFolderResources(uri);		
		writeFolder(folder, subFolders, resources);
		markExported(uri);
		
		exportFolders(subFolders);
		exportResources(resources);
	}

	protected List getSubfolders(String uri) {
		List subFolders = configuration.getRepository().getSubFolders(executionContext, uri);
		return subFolders;
	}

	protected ResourceLookup[] getFolderResources(String uri) {
		FilterCriteria filter = FilterCriteria.createFilter();
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(uri));
		ResourceLookup[] resources = configuration.getRepository().findResource(executionContext, filter);
		return resources;
	}

	protected void writeFolder(Folder folder, List subFolders, ResourceLookup[] resources) {
		FolderBean bean = createFolderBean(folder, subFolders, resources);
		if (exportPermissions) {
			RepositoryObjectPermissionBean[] permissions = handlePermissions(folder);
			bean.setPermissions(permissions);
		}
		
		String outputFolder = mkdir(configuration.getResourcesDirName(), folder.getURIString());
		serialize(bean, outputFolder, configuration.getFolderDetailsFileName(), configuration.getSerializer());
	}

	protected FolderBean createFolderBean(Folder folder, List subFolders, ResourceLookup[] resources) {
		FolderBean bean = new FolderBean();
		bean.copyFrom(folder);
		
		String[] subFolderNames;
		if (subFolders == null || subFolders.isEmpty()) {
			subFolderNames = null;
		} else {
			subFolderNames = new String[subFolders.size()];
			int c = 0;
			for (Iterator it = subFolders.iterator(); it.hasNext(); ++c) {
				Folder subFolder = (Folder) it.next();
				subFolderNames[c] = subFolder.getName();
			}
		}
		bean.setSubFolders(subFolderNames);
		
		String[] resourceNames;
		if (resources == null || resources.length == 0) {
			resourceNames = null;
		} else {
			resourceNames = new String[resources.length];
			for (int i = 0; i < resources.length; i++) {
				resourceNames[i] = resources[i].getName();
			}
		}
		bean.setResources(resourceNames);
		return bean;
	}

	protected void exportFolders(List subFolders) {
		if (subFolders != null && !subFolders.isEmpty()) {
			for (Iterator it = subFolders.iterator(); it.hasNext();) {
				Folder subFolder = (Folder) it.next();
				exportFolder(subFolder);
			}
		}
	}

	protected void exportResources(ResourceLookup[] resources) {
		if (resources != null && resources.length > 0) {
			for (int i = 0; i < resources.length; i++) {
				ResourceLookup resLookup = resources[i];
				exportResource(resLookup);
			}
		}
	}

	protected void addResourceIndexElement(String uri) {
		Element folderElement = getIndexElement().addElement(configuration.getResourceIndexElement());
		folderElement.addText(uri);
	}
	
	protected void exportResource(ResourceLookup lookup) {
		String uri = lookup.getURIString();
		if (!alreadyExported(uri)) {
			Resource resource = configuration.getRepository().getResource(executionContext, uri);
			exportResource(resource);
		}
	}

	protected void exportResource(Resource resource) {
		String uri = resource.getURIString();
		if (alreadyExported(uri)) {
			return;
		}
		
		commandOut.info("Exporting repository resource " + uri);
		
		writeResource(resource);
		
		markExported(uri);
	}

	protected void writeResource(Resource resource) {
		ResourceBean bean = handleResource(resource);
		if (exportPermissions) {
			RepositoryObjectPermissionBean[] permissions = handlePermissions(resource);
			bean.setPermissions(permissions);
		}
		
		String folder = mkdir(configuration.getResourcesDirName(), bean.getFolder());
		serialize(bean, folder, getResourceFileName(resource), configuration.getSerializer());
	}

	public ResourceBean handleResource(Resource resource) {
		ResourceBean bean = (ResourceBean) configuration.getCastorBeanMappings().newObject(resource.getClass());
		bean.copyFrom(resource, this);
		return bean;
	}

	protected String getResourceFileName(Resource resource) {
		return resource.getName() + ".xml";
	}

	public ResourceReferenceBean handleReference(ResourceReference reference) {
		ResourceReferenceBean beanRef;
		if (reference == null) {
			beanRef = null;
		} else if (reference.isLocal()) {
			beanRef = handleLocalResource(reference);
		} else {
			beanRef = handleExternalReference(reference);
		}
		return beanRef;
	}

	protected ResourceReferenceBean handleLocalResource(ResourceReference reference) {
		ResourceBean resourceDTO = handleResource(reference.getLocalResource());
		return new ResourceReferenceBean(resourceDTO);
	}

	protected ResourceReferenceBean handleExternalReference(ResourceReference reference) {
		String uri = reference.getReferenceURI();
		queueResource(uri);
		return new ResourceReferenceBean(uri);
	}

	public void queueResource(String uri) {
		queueResource(uri, false);
	}
	
	public void queueResource(String uri, boolean ignoreMissing) {
		if (!alreadyExported(uri)) {
			urisQueue.push(uri, ignoreMissing);
		}		
	}

	public String handleData(Resource resource, String dataProviderId) {
		ResourceDataProvider dataProvider = configuration.getResourceDataProvider(dataProviderId);
		
		InputStream dataIn = dataProvider.getData(exportContext, resource);
		String fileName = null;
		if (dataIn != null) {
			boolean closeInput = true;
			try {
				fileName = dataProvider.getFileName(resource);
				writeResourceData(resource, dataIn, fileName);
				closeInput = false;
				dataIn.close();
			} catch (IOException e) {
				log.error(e);
				throw new JSExceptionWrapper(e);
			} finally {
				if (closeInput) {
					try {
						dataIn.close();
					} catch (IOException e) {
						log.error(e);
					}
				}
			}
		}
		
		return fileName;
	}

	protected void writeResourceData(Resource resource, InputStream dataIn, String outDataFilename) {
		String folder = mkdir(configuration.getResourcesDirName(), resource.getParentFolder());
		writeData(dataIn, folder, outDataFilename);
	}

	protected RepositoryObjectPermissionBean[] handlePermissions(InternalURI object) {
		List permissions = configuration.getPermissionService().getObjectPermissionsForObject(executionContext, object);
		RepositoryObjectPermissionBean[] permissionBeans;
		if (permissions == null || permissions.isEmpty()) {
			permissionBeans = null;
		} else {
			commandOut.debug("Found " + permissions.size() + " permissions for " + object.getURI());
			
			permissionBeans = new RepositoryObjectPermissionBean[permissions.size()];
			int c = 0;
			for (Iterator i = permissions.iterator(); i.hasNext(); ++c) {
				ObjectPermission permission = (ObjectPermission) i.next();
				RepositoryObjectPermissionBean permissionBean = toPermissionBean(permission);
				permissionBeans[c] = permissionBean;
			}
		}
		return permissionBeans;
	}

	protected RepositoryObjectPermissionBean toPermissionBean(ObjectPermission permission) {
		RepositoryObjectPermissionBean permissionBean = new RepositoryObjectPermissionBean();
		
		Object permissionRecipient = permission.getPermissionRecipient();
		if (permissionRecipient instanceof Role) {
			permissionBean.setRecipientType(configuration.getPermissionRecipientRole());
			permissionBean.setRecipient(((Role) permissionRecipient).getRoleName());
		} else if (permissionRecipient instanceof User) {
			permissionBean.setRecipientType(configuration.getPermissionRecipientUser());
			permissionBean.setRecipient(((User) permissionRecipient).getUsername());
		} else {
			throw new JSException("jsexception.unknown.permission.recipient.type", new Object[] {permissionRecipient.getClass().getName()});
		}
		
		permissionBean.setPermissionMask(permission.getPermissionMask());
		
		return permissionBean;
	}

	public String getUrisArgument() {
		return urisArgument;
	}

	public void setUrisArgument(String urisArgument) {
		this.urisArgument = urisArgument;
	}

	public ResourceModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ResourceModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getPermissionsArgument() {
		return permissionsArgument;
	}

	public void setPermissionsArgument(String permissionsArgument) {
		this.permissionsArgument = permissionsArgument;
	}

}
