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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.repository.beans.FolderBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.RepositoryObjectPermissionBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceReferenceBean;
import com.jaspersoft.jasperserver.export.util.PathUtils;
import com.jaspersoft.jasperserver.export.util.PathUtils.SplittedPath;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceImporter.java 13374 2008-05-05 11:45:36Z lucian $
 */
public class ResourceImporter extends BaseImporterModule implements ResourceImportHandler, InitializingBean {
	
	public final static String ATTRIBUTE_UPDATE_RESOURCES = "updateResources";
	
	private final static Log log = LogFactory.getLog(ResourceImporter.class);
	
	private ResourceModuleConfiguration configuration;
	private String prependPathArg;
	private String updateArg;

	private RepositoryService repository;
	private String prependPath;
	private boolean update;
	
	private Set importedURIs;
	private LinkedList folderQueue;
	private LinkedList resourceQueue;
	private Map roles;
	private Map users;

	public void afterPropertiesSet() {
		this.repository = configuration.getRepository();
	}

	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
		
		prependPath = getPrependPath();
		update = getUpdateFlag();
	}

	protected String getPrependPath() {
		String path = getParameterValue(getPrependPathArg());
		if (path != null) {
			path = PathUtils.normalizePath(path);
			if (path.length() == 0 || path.equals(Folder.SEPARATOR)) {
				path = null;
			} else if (!path.startsWith(Folder.SEPARATOR)) {
				path = Folder.SEPARATOR + path;
			}
		}
		return path;
	}

	protected boolean getUpdateFlag() {
		return hasParameter(getUpdateArg());
	}

	protected boolean isUpdate() {
		return update;
	}
	
	public void process() {
		initProcess();
		
		createPrependFolder();
		
		queueEntryFolders();
		queueEntryResources();
		
		while (!folderQueue.isEmpty() || !resourceQueue.isEmpty()) {
			if (folderQueue.isEmpty()) {
				String uri = (String) resourceQueue.removeFirst();
				importResource(uri, false);
			} else {
				String uri = (String) folderQueue.removeFirst();
				importFolder(uri, true);
			}
		}
	}

	protected void queueEntryFolders() {
		List entryFolders = new ArrayList();
		for (Iterator it = indexElement.elementIterator(configuration.getFolderIndexElement()); it.hasNext(); ) {
			Element folderElement = (Element) it.next();
			entryFolders.add(folderElement.getText());
		}

		if (!entryFolders.isEmpty()) {
			Collections.sort(entryFolders);
			for (Iterator it = entryFolders.iterator(); it.hasNext();) {
				String uri = (String) it.next();
				folderQueue.addLast(uri);
			}
		}
	}

	protected void queueEntryResources() {
		for (Iterator it = indexElement.elementIterator(configuration.getResourceIndexElement()); it.hasNext(); ) {
			Element resourceElement = (Element) it.next();
			String uri = resourceElement.getText();
			resourceQueue.addLast(uri);
		}
	}

	protected void createPrependFolder() {
		if (prependPath != null) {
			LinkedList toCreateURIs = new LinkedList();
			for (String path = prependPath; 
					repository.getFolder(executionContext, path) == null; 
					path = PathUtils.splitPath(path).parentPath) {
				toCreateURIs.addFirst(path);
			}
			
			while(!toCreateURIs.isEmpty()) {
				String path = (String) toCreateURIs.removeFirst();
				Folder folder = createFolder(path);
				
				commandOut.info("Creating repository folder " + path);
				
				repository.saveFolder(executionContext, folder);
			}
		}
	}

	protected void initProcess() {
		importedURIs = new HashSet();
		folderQueue = new LinkedList();
		resourceQueue = new LinkedList();
		
		roles = new HashMap();
		users = new HashMap();
	}

	protected void importFolder(String uri, boolean detailsRequired) {
		if (importedURIs.contains(uri)) {
			return;
		}
		
		FolderBean folderBean = getFolderDetails(uri, detailsRequired);
		
		String importUri = prependedPath(uri);
		Folder folder = repository.getFolder(executionContext, importUri);
		if (folder == null) {
			ensureParent(uri);

			if (folderBean == null) {
				folder = createFolder(importUri);
			} else {
				folder = createFolder(folderBean);
			}

			repository.saveFolder(executionContext, folder);
			
			if (folderBean != null) {
				setPermissions(folder, folderBean.getPermissions(), false);
			}
			
			commandOut.info("Created repository folder " + importUri);
		} else {
			if (folderBean != null) {
				commandOut.info("Folder " + importUri + " already exists, importing permissions only");
				
				setPermissions(folder, folderBean.getPermissions(), true);
			}
		}
		
		importedURIs.add(uri);
		
		if (folderBean != null) {
			queueSubFolders(uri, folderBean);
			
			queueResources(uri, folderBean);
		}
	}

	protected void queueSubFolders(String uri, FolderBean folderBean) {
		String[] subFolders = folderBean.getSubFolders();
		if (subFolders != null) {
			for (int i = 0; i < subFolders.length; i++) {
				String subfolderURI = appendPath(uri, subFolders[i]);
				folderQueue.addLast(subfolderURI);
			}
		}
	}

	protected void queueResources(String uri, FolderBean folderBean) {
		String[] resources = folderBean.getResources();
		if (resources != null) {
			for (int i = 0; i < resources.length; i++) {
				String resourceUri = appendPath(uri, resources[i]);
				resourceQueue.addLast(resourceUri);
			}
		}
	}

	protected String prependedPath(String uri) {
		return PathUtils.concatPaths(prependPath, uri);
	}

	protected String appendPath(String uri, String name) {
		String subUri;
		if (uri.equals(Folder.SEPARATOR)) {
			subUri = Folder.SEPARATOR + name;
		} else {
			subUri = uri + Folder.SEPARATOR + name;
		}
		return subUri;
	}

	protected FolderBean getFolderDetails(String uri, boolean required) {
		FolderBean folderBean = null;
		String folderPath = PathUtils.concatPaths(configuration.getResourcesDirName(), uri);
		if (input.fileExists(folderPath, configuration.getFolderDetailsFileName())) {
			folderBean = (FolderBean) deserialize(folderPath, configuration.getFolderDetailsFileName(), configuration.getSerializer());
		} else {
			if (required) {
				throw new JSException("jsexception.folder.details.not.found", new Object[] {uri});
			}
 		}
		return folderBean;
	}
	
	protected void ensureParent(String uri) {
		SplittedPath splitPath = PathUtils.splitPath(uri);
		if (splitPath != null && splitPath.parentPath != null) {
			importFolder(splitPath.parentPath, false);
		}
	}

	protected Folder createFolder(String uri) {
		Folder folder = new FolderImpl();
		SplittedPath splPath = PathUtils.splitPath(uri);
		folder.setParentFolder(splPath.parentPath);
		folder.setName(splPath.name);
		folder.setLabel(splPath.name);
		return folder;
	}
	
	protected Folder createFolder(FolderBean folderBean) {
		Folder folder = new FolderImpl();
		folderBean.copyTo(folder);
		folder.setParentFolder(prependedPath(folder.getParentFolder()));
		return folder;
	}

	protected String importResource(String uri, boolean ignoreMissing) {
		String importUri = prependedPath(uri);
		if (!importedURIs.contains(uri)) {
			if (ignoreMissing && !hasResourceBeanData(uri)) {
				commandOut.info("Resource " + uri + " data missing from the catalog, skipping from import");
			} else {
				Resource resource = repository.getResource(executionContext, importUri);
				if (resource == null) {
					ensureParent(uri);
					
					ResourceBean bean = readResourceBean(uri);
					resource = createResource(bean);
					repository.saveResource(executionContext, resource);
					
					setPermissions(resource, bean.getPermissions(), false);
					
					commandOut.info("Imported resource " + importUri);
				} else if (update) {
					registerUpdateResource(importUri);
					
					ResourceBean bean = readResourceBean(uri);
					Resource updated = createResource(bean);
					
					if (resource.isSameType(updated)) {
						updated.setVersion(resource.getVersion());
						repository.saveResource(executionContext, updated);
						commandOut.info("Updated resource " + importUri);
					} else {
						commandOut.warn("Resource " + importUri 
								+ " already exists in the repository and has a different type than in the catalog, not updating");
					}
				} else {
					commandOut.warn("Resource " + importUri + " already exists, not importing");
				}
				
				importedURIs.add(uri);
			}
		}
		return importUri;
	}

	protected void registerUpdateResource(String resourceUri) {
		Set updateResources = (Set) getContextAttributes().getAttribute(
				ATTRIBUTE_UPDATE_RESOURCES);
		if (updateResources == null) {
			updateResources = new HashSet();
			getContextAttributes().setAttribute(ATTRIBUTE_UPDATE_RESOURCES, 
					updateResources);
		}
		
		updateResources.add(resourceUri);
	}

	protected Resource createResource(ResourceBean bean) {
		Class resourceItf = configuration.getCastorBeanMappings().getInterface(bean.getClass());
		Resource resource = repository.newResource(executionContext, resourceItf);
		bean.copyTo(resource, this);
		resource.setParentFolder(prependedPath(resource.getParentFolder()));
		return resource;
	}

	protected boolean hasResourceBeanData(String uri) {
		String resourceFileName = getResourceFileName(uri);
		return input.fileExists(configuration.getResourcesDirName(), resourceFileName);
	}
	
	protected ResourceBean readResourceBean(String uri) {
		String resourceFileName = getResourceFileName(uri);
		ResourceBean bean = (ResourceBean) deserialize(configuration.getResourcesDirName(), resourceFileName, configuration.getSerializer());
		return bean;
	}
	
	protected String getResourceFileName(String uri) {
		return uri + ".xml";
	}

	public ResourceReference handleReference(ResourceReferenceBean beanReference) {
		ResourceReference reference;
		if (beanReference == null) {
			reference = null;
		} else if (beanReference.isLocal()) {
			ResourceBean localResBean = beanReference.getLocalResource();
			Resource localRes = createResource(localResBean);
			reference = new ResourceReference(localRes);
		} else {
			String referenceURI = beanReference.getExternalURI();
			importResource(referenceURI, false);
			reference = new ResourceReference(prependedPath(referenceURI));
		}
		return reference;
	}

	public byte[] handleData(ResourceBean resourceBean, String dataFile, String providerId) {
		String filename = PathUtils.concatPaths(resourceBean.getFolder(), dataFile);
		InputStream dataInput = getFileInput(configuration.getResourcesDirName(), filename);
		boolean closeInput = true;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			StreamUtils.pipeData(dataInput, out);
			
			closeInput = false;
			dataInput.close();
			
			return out.toByteArray();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeInput) {
				try {
					dataInput.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	public Resource handleResource(ResourceBean resource) {
		Resource res = createResource(resource);
		return res;
	}

	public String handleResource(String uri) {
		return handleResource(uri, false);
	}

	public String handleResource(String uri, boolean ignoreMissing) {
		return importResource(uri, ignoreMissing);
	}

	protected void setPermissions(InternalURI object, RepositoryObjectPermissionBean[] permissions, boolean checkExisting) {
		if (permissions != null) {
			for (int i = 0; i < permissions.length; i++) {
				RepositoryObjectPermissionBean permissionBean = permissions[i];
				setPermission(object, permissionBean, checkExisting);
			}
		}
	}

	protected void setPermission(InternalURI object, RepositoryObjectPermissionBean permissionBean, boolean checkExisting) {
		ObjectPermissionService permissionsService = configuration.getPermissionService();
		
		Object recipient;
		String recipientType = permissionBean.getRecipientType();
		if (recipientType.equals(configuration.getPermissionRecipientRole())) {
			recipient = getRole(permissionBean.getRecipient());
			if (recipient == null) {
				commandOut.warn("Role " + permissionBean.getRecipient() + " not found, skipping permission of " + object.getURI());
			}
		} else if (recipientType.equals(configuration.getPermissionRecipientUser())) {
			recipient = getUser(permissionBean.getRecipient());
			if (recipient == null) {
				commandOut.warn("User " + permissionBean.getRecipient() + " not found, skipping permission of " + object.getURI());
			}
		} else {
			recipient = null;
			commandOut.warn("Unknown object permission recipient type " + recipientType + ", skipping permission of " + object.getURI());
		}
		
		if (recipient != null) {
			boolean existing;
			if (checkExisting) {
				List permissions = permissionsService.getObjectPermissionsForObjectAndRecipient(executionContext, object, recipient);
				existing = permissions != null && !permissions.isEmpty();
			} else {
				existing = false;
			}
			
			if (existing) {
				if (log.isInfoEnabled()) {
					log.info("Permission on " + object.getURI() + " for " + permissionBean.getRecipient() + " already exists, skipping.");
				}
			} else {
				ObjectPermission permission = permissionsService.newObjectPermission(executionContext);
				permission.setURI(object.getURI());
				permission.setPermissionMask(permissionBean.getPermissionMask());
				permission.setPermissionRecipient(recipient);
				
				permissionsService.putObjectPermission(executionContext, permission);
			}
		}		
	}

	protected Role getRole(String roleName) {
		Role role;
		if (roles.containsKey(roleName)) {
			role = (Role) roles.get(roleName);
		} else {
			role = configuration.getAuthorityService().getRole(executionContext, roleName);
			roles.put(roleName, role);
		}
		return role;
	}

	protected User getUser(String username) {
		User user;
		if (users.containsKey(username)) {
			user = (User) users.get(username);
		} else {
			user = configuration.getAuthorityService().getUser(executionContext, username);
			users.put(username, user);
		}
		return user;
	}

	public ResourceModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ResourceModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getPrependPathArg() {
		return prependPathArg;
	}

	public void setPrependPathArg(String prependPathArg) {
		this.prependPathArg = prependPathArg;
	}

	public String getUpdateArg() {
		return updateArg;
	}

	public void setUpdateArg(String updateArg) {
		this.updateArg = updateArg;
	}

}
