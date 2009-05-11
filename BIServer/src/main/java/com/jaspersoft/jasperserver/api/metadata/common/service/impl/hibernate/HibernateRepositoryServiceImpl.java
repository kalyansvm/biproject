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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.common.service.ClassMappingsObjectFactory;
import com.jaspersoft.jasperserver.api.common.util.CollatorFactory;
import com.jaspersoft.jasperserver.api.common.util.DefaultCollatorFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceValidator;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.ContentRepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.SortingUtils;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateRepositoryServiceImpl.java 12895 2008-04-09 17:31:17Z lucian $
 */
public class HibernateRepositoryServiceImpl extends HibernateDaoImpl implements HibernateRepositoryService, ReferenceResolver, RepoManager {
	
	private static final Log log = LogFactory.getLog(HibernateRepositoryServiceImpl.class);
	
	protected static final int RESOURCE_NAME_LENGHT = 100;
	protected static final String TEMP_NAME_PREFIX = "*";
	protected static final int TEMP_NAME_PREFIX_LENGTH = TEMP_NAME_PREFIX.length();
	protected static final String CHILDREN_FOLDER_SUFFIX = "_files";
	protected static final String COPY_GENERATED_NAME_SEPARATOR = "_";

	private ResourceFactory resourceFactory;
	private ResourceFactory persistentClassMappings;
	private ClassMappingsObjectFactory validatorMappings;
	private ThreadLocal tempNameResources;
	
	private CollatorFactory collatorFactory = new DefaultCollatorFactory();
	
	private List repositoryListeners = new ArrayList();
	private RepositorySecurityChecker securityChecker;
	private boolean lockFoldersOnPathChange = true;
	
	public HibernateRepositoryServiceImpl() {
		tempNameResources = new ThreadLocal();
	}

	public ResourceFactory getPersistentClassMappings() {
		return persistentClassMappings;
	}

	public void setPersistentClassMappings(ResourceFactory persistentClassMappings) {
		this.persistentClassMappings = persistentClassMappings;
	}

	public ResourceFactory getResourceFactory() {
		return resourceFactory;
	}

	public void setResourceFactory(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	public ClassMappingsObjectFactory getValidatorMappings() {
		return validatorMappings;
	}

	public void setValidatorMappings(ClassMappingsObjectFactory validatorMappings) {
		this.validatorMappings = validatorMappings;
	}

	public ResourceValidator getValidator(Resource resource) {
		return resource == null ? null : 
			(ResourceValidator) validatorMappings.getClassObject(resource.getResourceType());
	}

	public RepositorySecurityChecker getSecurityChecker() {
		return securityChecker;
	}

	public void setSecurityChecker(RepositorySecurityChecker securityChecker) {
		this.securityChecker = securityChecker;
	}

	public Resource getResource(ExecutionContext context, final String uri) {
		return getResourceUnsecure(context, uri);
	}
	
	public Resource getResourceUnsecure(ExecutionContext context, final String uri) {
		return (Resource) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResource(uri, null);
			}
		});
	}

	public Resource getResource(ExecutionContext context, final String uri, final Class resourceType) {
		return (Resource) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResource(uri, resourceType);
			}
		});
	}
	
	protected Object loadResource(final String uri, Class resourceType) {
		Class persistentClass = resourcePersistentClass(resourceType);

		RepoResource repoResource = findByURI(persistentClass, uri, false);
		Resource resource;
		if (repoResource == null) {
			log.debug("Resource not found at \"" + uri + "\"");
			resource = null;
		} else {
			resource = (Resource) repoResource.toClient(resourceFactory);
		}

		return resource;
	}

	protected Class resourcePersistentClass(Class resourceType) {
		Class persistentClass;
		if (resourceType == null) {
			persistentClass = RepoResource.class;
		} else {			
			persistentClass = getPersistentClassMappings().getImplementationClass(resourceType);
		}
		return persistentClass;
	}

	public void saveFolder(ExecutionContext context, final Folder folder) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				saveFolder(folder);
				return null;
			}
		});
	}

	protected void saveFolder(Folder folder) {
		RepoFolder repoFolder = getFolder(folder.getURIString(), false);

		if (folder.isNew()) {
			if (repoFolder != null || resourceExists(folder.getURIString())) {
				throw new JSDuplicateResourceException("jsexception.folder.already.exists", new Object[] {folder.getURIString()});
			}
			
			repoFolder = createNewRepoFolder();
		} else {
			if (repoFolder == null) {
				String quotedURI = "\"" + folder.getURIString() + "\"";
				throw new JSException("jsexception.folder.not.found", new Object[] {quotedURI});
			}
		}

		String parentURI = folder.getParentFolder();
		RepoFolder parent;
		if (parentURI == null && folder.getName().equals(Folder.SEPARATOR)) {
			parent = null;
		} else {
			parent = getFolder(parentURI, true);
		}
		
		//TODO don't set parent, name when updating
		repoFolder.set(folder, parent, this);
		getHibernateTemplate().saveOrUpdate(repoFolder);
	}

	protected RepoFolder createNewRepoFolder() {
		RepoFolder repoFolder = new RepoFolder();
		repoFolder.setCreationDate(getOperationTimestamp());
		return repoFolder;
	}


	protected RepoFolder getFolder(String uri, boolean required) {
		if (uri == null || uri.length() == 0 || uri.equals(Folder.SEPARATOR)) {
			return getRootFolder();
		}
		
		// Deal with URIs that come with "repo:" on the front
		
		final String repoURIPrefix = Resource.URI_PROTOCOL + ":";
		String workUri = uri.startsWith(repoURIPrefix) ? uri.substring(repoURIPrefix.length()) : uri;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
		criteria.add(Restrictions.naturalId().set("URI", workUri));
		List foldersList = getHibernateTemplate().findByCriteria(criteria);
		RepoFolder folder;
		if (foldersList.isEmpty()) {
			if (required) {
				String quotedURI = "\"" + uri + "\"";
				throw new JSResourceNotFoundException("jsexception.folder.not.found.at", new Object[] {quotedURI});
			}

			log.debug("Folder not found at \"" + uri + "\"");
			folder = null;
		} else {
			folder = (RepoFolder) foldersList.get(0);
		}
		return folder;
	}

	protected RepoFolder getRootFolder() {
		DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
		criteria.add(Restrictions.naturalId().set("URI", Folder.SEPARATOR));
		List foldersList = getHibernateTemplate().findByCriteria(criteria);
		RepoFolder root;
		if (foldersList.isEmpty()) {
			root = null;
		} else {
			root = (RepoFolder) foldersList.get(0);
		}
		return root;
	}

	public ValidationErrors validateResource(ExecutionContext context, final Resource resource, final ValidationErrorFilter filter) {
		return validate(resource, filter);
	}

	protected ValidationErrors validate(final Resource resource,
			final ValidationErrorFilter filter) {
		ResourceValidator validator = getValidator(resource);
		
		if (validator != null) {
			return validator.validate(resource, filter);
		}
		
		return new ValidationErrorsImpl();
	}

	public ValidationErrors validateFolder(ExecutionContext context, Folder folder, ValidationErrorFilter filter) {
		return validate(folder, filter);
	}

	public void saveResource(ExecutionContext context, final Resource resource) {
		initTempNameResources();
		try {
			executeWriteCallback(new DaoCallback() {
				public Object execute() {
					RepoResource repo = getRepoResource(resource);
					repo.copyFromClient(resource, HibernateRepositoryServiceImpl.this);
					RepoResource repositoryResource = repo;
					getHibernateTemplate().saveOrUpdate(repositoryResource);
					return null;
				}
			});
			
			if (!tempNameResources().isEmpty()) {
				executeWriteCallback(new DaoCallback() {
					public Object execute() {
						HibernateTemplate template = getHibernateTemplate();
						for (Iterator it = tempNameResources().iterator(); it.hasNext();) {
							RepoResource res = (RepoResource) it.next();
							res.setName(res.getName().substring(TEMP_NAME_PREFIX_LENGTH));
							
							RepoFolder childrenFolder = res.getChildrenFolder();
							if (childrenFolder != null) {
								childrenFolder.setName(childrenFolder.getName().substring(TEMP_NAME_PREFIX_LENGTH));
								childrenFolder.refreshURI(HibernateRepositoryServiceImpl.this);
							}
							
							template.save(res);
						}
						return null;
					}
				});
			}
		} finally {
			resetTempNameResources();
		}
	}

	protected void initTempNameResources() {
		tempNameResources.set(new HashSet());
	}

	protected void resetTempNameResources() {
		tempNameResources.set(null);
	}
	
	protected Set tempNameResources() {
		return (Set) tempNameResources.get();	
	}

	public RepoResource getRepoResource(Resource resource) {
		Class persistentClass = getPersistentClassMappings().getImplementationClass(resource.getClass());
		if (persistentClass == null) {
			String quotedResource = "\"" + resource.getClass().getName() + "\"";
			throw new JSException("jsexception.no.persistent.class.mapped.to", new Object[] {quotedResource});
		}

		RepoResource repo;
		if (resource.isNew()) {
			if (pathExists(resource.getURIString())) {
				String quotedResource = "\"" + resource.getURIString() + "\"";
				throw new JSDuplicateResourceException("jsexception.resource.already.exists", new Object[] {quotedResource});
			}

			repo = createPersistentResource(persistentClass);

			RepoFolder parent = getFolder(resource.getParentFolder(), true);
			repo.setParent(parent);
		} else {
			repo = findByURI(persistentClass, resource.getURIString(), false);
			if (repo == null) {
				String quotedURI = "\"" + resource.getURIString() + "\""; 
				throw new JSException("jsexception.resource.does.not.exist", new Object[] {quotedURI});
			}
		}
		return repo;
	}

	public boolean resourceExists(ExecutionContext executionContext, String uri) {
		return resourceExists(uri);
	}

	public boolean resourceExists(ExecutionContext executionContext, String uri, Class resourceType) {
		return resourceExists(uri, resourceType);
	}

	public boolean resourceExists(ExecutionContext executionContext, FilterCriteria filterCriteria) {
		DetachedCriteria criteria = translateFilterToCriteria(filterCriteria);
		boolean exists;
		if (criteria == null) {
			exists = false;
		} else {
			criteria.setProjection(Projections.rowCount());
			List countList = getHibernateTemplate().findByCriteria(criteria);
			int count = ((Integer) countList.get(0)).intValue();
			exists = count > 0;
		}
		return exists;
	}

	protected boolean resourceExists(String uri) {
		return resourceExists(uri, null);
	}
	
	protected boolean resourceExists(String uri, Class type) {
		int sep = uri.lastIndexOf(Folder.SEPARATOR);
		boolean exists = false;
		if (sep >= 0) {
			String name = uri.substring(sep + Folder.SEPARATOR_LENGTH);
			String folderName = uri.substring(0, sep);
			RepoFolder folder = getFolder(folderName, false);
			if (folder != null) {
				exists = resourceExists(folder, name, type);
			}
		}
		return exists;
	}

	public ResourceLookup[] findResource(final ExecutionContext context, final FilterCriteria filterCriteria) 
	{
		return (ResourceLookup[]) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResources(context, filterCriteria);
			}
		});		
	}

	public ResourceLookup[] findResources(final ExecutionContext context, final FilterCriteria[] filterCriteria) 
	{
		return (ResourceLookup[]) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResources(context, filterCriteria);
			}
		});		
	}

	public List loadClientResources(FilterCriteria filterCriteria) {

		List repoResources = loadRepoResourceList(filterCriteria);
		
		List result = new ArrayList(repoResources.size());
		
		for (Iterator iter = repoResources.iterator(); iter.hasNext(); ) {
			RepoResource repoResource = (RepoResource) iter.next();
			result.add(repoResource.toClient(resourceFactory));
		}
		
		return result;
	}
	
	public List loadResourcesList(FilterCriteria filterCriteria) {
		return loadResourcesList(null, filterCriteria);
	}
	
	public List loadResourcesList(ExecutionContext context, FilterCriteria filterCriteria) {
		List repoResources = loadRepoResourceList(context, filterCriteria);		
		return toLookups(repoResources);
	}
	
	public List loadResourcesList(ExecutionContext context, FilterCriteria[] filterCriteria) {
		List repoResources;
		if (filterCriteria.length == 1) {
			repoResources = loadRepoResourceList(context, filterCriteria[0]);
		} else {
			repoResources = new ArrayList();
			for (int i = 0; i < filterCriteria.length; i++) {
				FilterCriteria criteria = filterCriteria[i];
				List criteriaRes = loadRepoResourceList(context, criteria, false);
				if (criteriaRes != null) {
					repoResources.addAll(criteriaRes);
				}
			}
			
			sortRepoResourcesByURI(context, repoResources);
		}
		
		return toLookups(repoResources);
	}

	protected List toLookups(List repoResources) {
		List result = new ArrayList(repoResources.size());
		
		for (Iterator iter = repoResources.iterator(); iter.hasNext(); ) {
			RepoResource repoResource = (RepoResource) iter.next();
			result.add(repoResource.toClientLookup());
		}
		return result;
	}

	protected ResourceLookup[] loadResources(ExecutionContext context, FilterCriteria filterCriteria) {
		List repoResources = loadResourcesList(context, filterCriteria);
		
		ResourceLookup[] resourceLookups = new ResourceLookup[repoResources.size()];
		resourceLookups = (ResourceLookup[]) repoResources.toArray(resourceLookups);
		return resourceLookups;
	}

	protected ResourceLookup[] loadResources(ExecutionContext context, FilterCriteria[] filterCriteria) {
		List repoResources = loadResourcesList(context, filterCriteria);
		
		ResourceLookup[] resourceLookups = new ResourceLookup[repoResources.size()];
		resourceLookups = (ResourceLookup[]) repoResources.toArray(resourceLookups);
		return resourceLookups;
	}
	
	public List loadRepoResourceList(final FilterCriteria filterCriteria) {
		return loadRepoResourceList(null, filterCriteria);
	}
	
	protected List loadRepoResourceList(final ExecutionContext context, final FilterCriteria filterCriteria) {
		return loadRepoResourceList(context, filterCriteria, true);
	}

	public List loadRepoResourceList(final ExecutionContext context, final FilterCriteria filterCriteria,
			final boolean sort) {
		DetachedCriteria criteria = translateFilterToCriteria(filterCriteria);
		
		// If we don't have a mapping, ignore it
		if (criteria == null) {
			return new ArrayList();
		}
		
		List repoResources = getHibernateTemplate().findByCriteria(criteria);
		if (sort) {
			sortRepoResourcesByURI(context, repoResources);
		}
		
		return repoResources;
	}

	protected DetachedCriteria translateFilterToCriteria(FilterCriteria filterCriteria) {
		Class filterClass = filterCriteria == null ? null : filterCriteria.getFilterClass();
		Class persistentClass;
		if (filterClass == null) {
			persistentClass = RepoResource.class;
            //persistentClass = RepoOlapUnit.class;    
		} else {			
			persistentClass = getPersistentClassMappings().getImplementationClass(filterClass);
		}

		DetachedCriteria criteria;
		if (persistentClass == null) {			
			criteria = null;
		} else {
			criteria = DetachedCriteria.forClass(persistentClass);
			criteria.createAlias("parent", "parent");
			criteria.add(Restrictions.eq("parent.hidden", Boolean.FALSE));
			if (filterCriteria != null) {
				List filterElements = filterCriteria.getFilterElements();
				if (!filterElements.isEmpty()) {
					Conjunction conjunction = Restrictions.conjunction();
					HibernateFilter filter = new HibernateFilter(conjunction, this);
					for (Iterator it = filterElements.iterator(); it.hasNext();) {
						FilterElement filterElement = (FilterElement) it.next();
						filterElement.apply(filter);
					}
					criteria.add(conjunction);
				}
			}
		}
		
		return criteria;
	}
	
	protected void sortRepoResourcesByURI(final ExecutionContext context, List repoResources) {
		SortingUtils.sortRepoResourcesByURI(getCollator(context), repoResources);
	}
	
	public Resource newResource(ExecutionContext context, Class _class) {
		return resourceFactory.newResource(context, _class);
	}
	
	public RepoResource findByURI(Class persistentClass, String uri, boolean required) {
		if (uri == null) {
			throw new JSException("jsexception.null.uri");
		}
		
		// Deal with URIs that come with "repo:" on the front
		
		final String repoURIPrefix = Resource.URI_PROTOCOL + ":";
		String workUri = uri.startsWith(repoURIPrefix) ? uri.substring(repoURIPrefix.length()) : uri;
		
		int sep = workUri.lastIndexOf(Folder.SEPARATOR);
		RepoResource res = null;
		if (sep >= 0) {
			String name = workUri.substring(sep + Folder.SEPARATOR_LENGTH);
			String folderName = workUri.substring(0, sep);
			log.debug("Looking for name: " + name + " in folder: " + folderName);
			RepoFolder folder = getFolder(folderName, false);
			if (folder != null) {
				res = findByName(persistentClass, folder, name, required);
			} else {
				log.debug("No folder: " + folderName);
			}
		}
		
		if (required && res == null) {
			String quotedURI = "\"" + uri + "\"";
			throw new JSResourceNotFoundException("jsexception.resource.of.type.not.found", new Object[] {quotedURI, persistentClass});
		}
		
		return res;
	}

	protected RepoResource findByName(Class persistentClass, RepoFolder folder, String name, boolean required) {
		DetachedCriteria criteria = DetachedCriteria.forClass(persistentClass);
		criteria.add(Restrictions.naturalId().set("name", name).set("parent", folder));
		List resourceList = getHibernateTemplate().findByCriteria(criteria);
		
		RepoResource res;
		if (resourceList.isEmpty()) {
			if (required) {
				String uri = "\"" + folder.getURI() + Folder.SEPARATOR + name + "\"";
				throw new JSResourceNotFoundException("jsexception.resource.of.type.not.found", new Object[] {uri, persistentClass});
			}

			res = null;
		}
		else {
			res = (RepoResource) resourceList.get(0);
		}
		
		return res;
	}

	protected boolean resourceExists(RepoFolder folder, String name, Class resourceType) {
		Class persistentClass = resourcePersistentClass(resourceType);
		DetachedCriteria criteria = DetachedCriteria.forClass(persistentClass);
		criteria.add(Restrictions.naturalId().set("name", name).set("parent", folder));
		criteria.setProjection(Projections.rowCount());
		List countList = getHibernateTemplate().findByCriteria(criteria);
		int count = ((Integer) countList.get(0)).intValue();		
		return count > 0;
	}
/*	
	protected boolean resourceDisplayNameExists(String parentUri, String displayName) {
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentUri));

		List resources = loadResourcesList(null, criteria); 
		for (int i=0; i<resources.size(); i++) {
			ResourceLookupImpl res = (ResourceLookupImpl)resources.get(i);
			if (displayName.equalsIgnoreCase(res.getLabel())) {
				return true;                   
			}
		}
		return false;
	}	
	
*/
	protected boolean folderExists(RepoFolder parent, String name) {
		String uri;
		if (parent.isRoot()) {
			uri = Folder.SEPARATOR + name;
		} else {
			uri = parent.getURI() + Folder.SEPARATOR + name;
		}
		return folderExists(uri);
	}

/*	
	protected boolean folderDisplayNameExists(String parentUri, String displayName) {
		List repoFolderList = getSubFolders(null, parentUri);
		for (int i=0; i<repoFolderList.size(); i++) {
			FolderImpl repoFolder = (FolderImpl)repoFolderList.get(i);
			if (displayName.equalsIgnoreCase(repoFolder.getLabel())) {
				return true;                   
			}
		}
		return false;
	}	
*/
	protected RepoResource createPersistentResource(Class persistentClass) {
		try {
			RepoResource repo = (RepoResource) persistentClass.newInstance();
			repo.setCreationDate(getOperationTimestamp());
			return repo;
		} catch (InstantiationException e) {
			log.fatal("Error instantiating persistent resource", e);
			throw new JSExceptionWrapper(e);
		} catch (IllegalAccessException e) {
			log.fatal("Error instantiating persistent resource", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected RepoResource getRepositoryReference(RepoResource owner, Resource res) {
		Class persistentClass = getPersistentClassMappings().getImplementationClass(res.getClass());
		if (persistentClass == null) {
			String quotedClass = "\"" + res.getClass().getName() + "\"";
			throw new JSException("jsexception.no.persistent.class.mapped.to", new Object[] {quotedClass});
		}
		
		RepoResource repo = null;
		RepoFolder folder = owner.getChildrenFolder();
		if (res.isNew()) {
			//if a local resource with the same name already exists it will be orphaned and deleted
			boolean tempName = folder != null && !folder.isNew() && resourceExists(folder, res.getName(), null);
			repo = createPersistentResource(persistentClass);
			if (tempName) {
				tempNameResources().add(repo);
			}
		} else {
			if (folder != null && !folder.isNew()) {
				repo = findByName(persistentClass, folder, res.getName(), false);
			}
			if (repo == null) {
				String quotedResource = "\"" + res.getURIString() + "\"";
				throw new JSException("jsexception.resource.does.not.exist", new Object[] {quotedResource});
			}
		}

		return repo;
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver#getReference(com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource, com.jaspersoft.jasperserver.api.metadata.common.domain.Resource, java.lang.Class)
	 */
	public RepoResource getReference(RepoResource owner, ResourceReference resourceRef, Class persistentReferenceClass) {
		if (resourceRef == null) {
			return null;
		}
		RepoResource repoRes;
		if (resourceRef.isLocal()) {
			repoRes = getReference(owner, resourceRef.getLocalResource(), persistentReferenceClass);
		} else {
			repoRes = findByURI(persistentReferenceClass, resourceRef.getReferenceURI(), true);
		}
		return repoRes;
	}
	
	public RepoResource getReference(RepoResource owner, Resource resource, Class persistentReferenceClass) {
		return getReference(owner, resource, persistentReferenceClass, this);
	}
	
	protected RepoResource getReference(RepoResource owner, 
			Resource resource, Class persistentReferenceClass,
			ReferenceResolver referenceResolver) {
		if (resource == null) {
			return null;
		}
		
		RepoResource repoRes = getRepositoryReference(owner, resource);

		RepoFolder local = owner.getChildrenFolder();
		if (local == null) {
			if (log.isInfoEnabled()) {
				log.info("Creating children folder for " + this);
			}
			
			local = createNewRepoFolder();
			local.setName(getChildrenFolderName(owner.getName()));
			local.setLabel(owner.getLabel());
			local.setDescription(owner.getDescription());
			local.setParent(owner.getParent());
			local.setHidden(true);
			local.refreshURI(this);
			owner.setChildrenFolder(local);
		}

		owner.addNewChild(repoRes);
		repoRes.copyFromClient(resource, referenceResolver);
		
		if (tempNameResources().contains(repoRes)) {
			repoRes.setName(TEMP_NAME_PREFIX + repoRes.getName());
			RepoFolder childrenFolder = repoRes.getChildrenFolder();
			if (childrenFolder != null) {
				childrenFolder.setName(TEMP_NAME_PREFIX + childrenFolder.getName());
				childrenFolder.refreshURI(this);
			}
		}
		
		return repoRes;
	}

	public List getAllFolders(final ExecutionContext context) {
		return (List) executeCallback(new DaoCallback() {
			public Object execute() {
				DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
				criteria.add(Restrictions.eq("hidden", Boolean.FALSE));
				
				List repoFolders = getHibernateTemplate().findByCriteria(criteria);
				List folders = new ArrayList(repoFolders.size());
				for (Iterator iter = repoFolders.iterator(); iter.hasNext();) {
					RepoFolder repoFolder = (RepoFolder) iter.next();
					Folder folder = repoFolder.toClient();
					folders.add(folder);
				}
				
				SortingUtils.sortFoldersByURI(getCollator(context), folders);
				
				return folders;
			}
		});
	}

	public List getSubFolders(final ExecutionContext context, final String folderURI) {
		return (List) executeCallback(new DaoCallback() {
			public Object execute() {
				final RepoFolder folder = getFolder(folderURI, false);
				List subfolders;
				if (folder == null) {
					//return empty list for non-existing folder
					subfolders = new ArrayList();
				} else {
					List folders = getHibernateTemplate().executeFind(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException, SQLException {
							return session.createFilter(folder.getSubFolders(), "where hidden = false").list();
						}
					});

					subfolders = new ArrayList(folders.size());
					for (Iterator it = folders.iterator(); it.hasNext();) {
						RepoFolder repoFolder = (RepoFolder) it.next();
						subfolders.add(repoFolder.toClient());
					}
					
					SortingUtils.sortFoldersByName(getCollator(context), subfolders);
				}
				return subfolders;
			}
		});
	}

	public FileResourceData getResourceData(ExecutionContext context, final String uri) throws JSResourceNotFoundException {
		return (FileResourceData) executeCallback(new DaoCallback() {
			public Object execute() {
				RepoFileResource res = (RepoFileResource) findByURI(RepoFileResource.class, uri, true);
				while (res.isFileReference()) {
					res = res.getReference();
				}
				return res.copyData();
			}
		});
	}

	public FileResourceData getContentResourceData(ExecutionContext context, final String uri) throws JSResourceNotFoundException {
		return (FileResourceData) executeCallback(new DaoCallback() {
			public Object execute() {
				ContentRepoFileResource res = (ContentRepoFileResource) findByURI(ContentRepoFileResource.class, uri, true);
				return res.copyData();
			}
		});
	}

	public RepoResource getExternalReference(String uri, Class persistentReferenceClass) {
		return findByURI(persistentReferenceClass, uri, true);
	}

	public RepoResource getPersistentReference(String uri, Class clientReferenceClass) {
		return findByURI(resourcePersistentClass(clientReferenceClass), uri, true);
	}

	public void deleteResource(ExecutionContext context, final String uri) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				deleteResource(uri);
				return null;
			}
		});
	}

	protected void deleteResource(String uri) {
		RepoResource repoResource = findByURI(RepoResource.class, uri, true);
		getHibernateTemplate().delete(repoResource);
	}

	public void deleteFolder(ExecutionContext context, final String uri) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				deleteFolder(uri);
				return null;
			}
		});
	}

	protected void deleteFolder(String uri) {
		RepoFolder folder = getFolder(uri, true);
		if (folder.isRoot()) {
			throw new JSException("jsexception.folder.delete.root");
		}
		getHibernateTemplate().delete(folder);
	}

	public void delete(ExecutionContext context, final String[] resourceURIs, final String[] folderURIs) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				if (resourceURIs != null && resourceURIs.length > 0) {
					for (int i = 0; i < resourceURIs.length; i++) {
						deleteResource(resourceURIs[i]);
					}
				}

				if (folderURIs != null && folderURIs.length > 0) {
					for (int i = 0; i < folderURIs.length; i++) {
						deleteFolder(folderURIs[i]);
					}
				}
				
				return null;
			}
		});
	}

        public Folder getFolder(ExecutionContext context, final String uri) {
            
            return getFolderUnsecure(context, uri);
        }

	public boolean folderExists(ExecutionContext context, String uri)
	{
		return folderExists(uri); 
	}

	public Folder getFolderUnsecure(ExecutionContext context, final String uri) {
		return (Folder) executeCallback(new DaoCallback() {
			public Object execute() {
				RepoFolder repoFolder = getFolder(uri, false);
				Folder folder = repoFolder == null ? null : repoFolder.toClient();
				return folder;
			}
		});
	}

	protected boolean folderExists(String uri) {
		return getFolder(uri, false) != null;
	}

	public String getChildrenFolderName(String resourceName)
	{
		return resourceName + CHILDREN_FOLDER_SUFFIX;
	}

	public CollatorFactory getCollatorFactory() {
		return collatorFactory;
	}

	public void setCollatorFactory(CollatorFactory collatorFactory) {
		this.collatorFactory = collatorFactory;
	}
	
	protected Collator getCollator(ExecutionContext context) {
		return getCollatorFactory().getCollator(context);
	}

	protected boolean pathExists(String uri) {
		return resourceExists(uri) || folderExists(uri);
	}
	
	public boolean repositoryPathExists(ExecutionContext context, String uri) {
		return pathExists(uri);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService#hideFolder(java.lang.String)
	 */
	public void hideFolder(String uri) {
		setFolderHiddenFlag(uri, true);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService#unhideFolder(java.lang.String)
	 */
	public void unhideFolder(String uri) {
		setFolderHiddenFlag(uri, false);
	}

	private void setFolderHiddenFlag(String uri, boolean hidden) {
		RepoFolder folder = getFolder(uri, false);
		if (folder != null && folder.isHidden() != hidden) {
			folder.setHidden(hidden);
            getHibernateTemplate().saveOrUpdate(folder);
		}
	}

	public void moveFolder(ExecutionContext context, final String sourceURI,
			final String destinationFolderURI) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				RepoFolder source = getFolder(sourceURI, true);
				RepoFolder dest = getFolder(destinationFolderURI, true);
				
				if (isAncestorOrEqual(source.getResourceURI(), dest.getResourceURI())) {
					throw new JSException("jsexception.move.folder.source.uri.ancestor.destination", 
							new Object[]{sourceURI, destinationFolderURI});
				}
				if (source.getParent().getResourceURI().equals(dest.getResourceURI())) {
					throw new JSException("jsexception.move.folder.to.same.folder",
							new Object[]{sourceURI, destinationFolderURI});
				}
				if (nameExistsInFolder(dest, source.getName())) {
					throw new JSException("jsexception.move.folder.path.already.exists",
							new Object[]{sourceURI, destinationFolderURI});
				}
				
				if (log.isDebugEnabled()) {
					log.debug("Moving folder " + source.getResourceURI() + " to " + dest.getResourceURI());
				}
				
				source.moveTo(dest, HibernateRepositoryServiceImpl.this);
				return null;
			}
		});
	}

	public void moveResource(ExecutionContext context, final String sourceURI,
			final String destinationFolderURI) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				RepoResource source = findByURI(RepoResource.class, sourceURI, true);
				RepoFolder dest = getFolder(destinationFolderURI, true);
				
				if (source.getParent().getResourceURI().equals(dest.getResourceURI())) {
					throw new JSException("jsexception.move.resource.to.same.folder",
							new Object[]{sourceURI, destinationFolderURI});
				}
				if (nameExistsInFolder(dest, source.getName())) {
					throw new JSException("jsexception.move.resource.path.already.exists",
							new Object[]{sourceURI, destinationFolderURI});
				}
				
				if (log.isDebugEnabled()) {
					log.debug("Moving resource " + source.getResourceURI() + " to " + dest.getResourceURI());
				}
				
				source.moveTo(dest, HibernateRepositoryServiceImpl.this);
				return null;
			}
		});
	}
	
	protected boolean isAncestorOrEqual(String ancestorURI, String childURI) {
		return childURI.startsWith(ancestorURI);
	}

	public void update(Object repoObject) {
		getHibernateTemplate().update(repoObject);
	}

	public List getRepositoryListeners() {
		return repositoryListeners;
	}

	public void setRepositoryListeners(List repositoryListeners) {
		this.repositoryListeners = repositoryListeners;
	}

	protected boolean nameExistsInFolder(RepoFolder folder, String name) {
		return resourceExists(folder, name, null)
				|| folderExists(folder, name);
	}
	
	public void folderMoved(RepoFolder folder, String oldBaseURI,
			String newBaseURI) {
		String newURI = folder.getResourceURI();
		String oldURI = getMovedOldURI(newURI, oldBaseURI, newBaseURI);
		
		if (log.isDebugEnabled()) {
			log.debug("Folder " + oldURI + " has been moved to " + newURI);
		}
		
		FolderMoveEvent folderMove = new FolderMoveEvent(oldBaseURI, newBaseURI, oldURI, newURI);
		for (Iterator it = getRepositoryListeners().iterator(); it.hasNext();) {
			RepositoryListener listener = (RepositoryListener) it.next();
			listener.folderMoved(folderMove);
		}
	}

	public void resourceMoved(RepoResource resource, String oldBaseURI,
			String newBaseURI) {
		String newURI = resource.getResourceURI();
		String oldURI = getMovedOldURI(newURI, oldBaseURI, newBaseURI);
		Class type = resource.getClientType();
		
		if (log.isDebugEnabled()) {
			log.debug("Resource " + oldURI + " has been moved to " + newURI);
		}
		
		ResourceMoveEvent resourceMove = new ResourceMoveEvent(type, oldBaseURI, newBaseURI, oldURI, newURI);
		for (Iterator it = getRepositoryListeners().iterator(); it.hasNext();) {
			RepositoryListener listener = (RepositoryListener) it.next();
			listener.resourceMoved(resourceMove);
		}
	}

	protected String getMovedOldURI(String newURI, String oldBaseURI,
			String newBaseURI) {
		if (!newURI.startsWith(newBaseURI)) {
			throw new JSException("New folder URI " + newURI + " does not match new base URI " + newBaseURI);
		}
		
		String newPostBaseURI;
		if (newBaseURI.equals(Folder.SEPARATOR)) {
			newPostBaseURI = newURI;
		} else {
			newPostBaseURI = newURI.substring(newBaseURI.length());
		}
		
		String oldURI;
		if (oldBaseURI.equals(Folder.SEPARATOR)) {
			oldURI = newPostBaseURI;
		} else {
			oldURI = oldBaseURI + newPostBaseURI;
		}
		return oldURI;
	}

	public Resource copyResource(ExecutionContext context, 
			final String sourceURI, final String destinationURI) {
		initTempNameResources();
		try {
			Resource copy = (Resource) executeWriteCallback(new DaoCallback() {
				public Object execute() {
					RepoResource sourceRepoResource = findByURI(RepoResource.class, sourceURI, true);
					String copyURI = getCopyURI(destinationURI, true);
					RepoResource repoCopy = copyResource(sourceRepoResource, copyURI);
					return repoCopy.toClient(resourceFactory);
				}
			});
			return copy;
		} finally {
			resetTempNameResources();
		}
	}

	protected String getCopyURI(String destinationURI, boolean resource) {
		String parentURI = RepositoryUtils.getParentPath(destinationURI);
		RepoFolder parent = getFolder(parentURI, true);
		lockReadFolder(parent);
		String destinationName = RepositoryUtils.getName(destinationURI);
		
		String copyURI;
		if (nameExistsInFolder(parent, destinationName)) {
			int maxNameLenght = RESOURCE_NAME_LENGHT;
			if (resource) {
				maxNameLenght -= CHILDREN_FOLDER_SUFFIX.length();
			}
			
			String baseName = destinationName;
			String copyName;
			int counter = 1;
			do {
				copyName = baseName + COPY_GENERATED_NAME_SEPARATOR + counter;
				if (copyName.length() > maxNameLenght) {
					String truncatedBaseName = baseName.substring(0, 
							baseName.length() - copyName.length() + maxNameLenght);
					copyName = truncatedBaseName
							+ COPY_GENERATED_NAME_SEPARATOR + counter;
				}
				++counter;
			} while (nameExistsInFolder(parent, copyName));
			
			copyURI = RepositoryUtils.concatenatePath(parentURI, copyName);
		} else {
			copyURI = destinationURI;
		}
		
		return copyURI;
	}
	
	protected RepoResource copyResource(RepoResource resource, String copyURI) {
		Resource copy = getClientClone(resource);
		copy.setURIString(copyURI);
		
		RepoResource repoCopy = getRepoResource(copy);
		repoCopy.copyFromClient(copy, this);
		getHibernateTemplate().save(repoCopy);
		
		return repoCopy;
	}

	protected Resource getClientClone(RepoResource resource) {
		Map options = new HashMap();
		options.put(RepoResource.CLIENT_OPTION_FULL_DATA, null);
		options.put(RepoResource.CLIENT_OPTION_AS_NEW, null);
		Resource copy = (Resource) resource.toClient(resourceFactory, options);
		return copy;
	}

	public Folder copyFolder(ExecutionContext context, 
			final String sourceURI, final String destinationURI) {
		initTempNameResources();
		try {
			Folder copy = (Folder) executeWriteCallback(new DaoCallback() {
				public Object execute() {
					RepoFolder folderCopy = copyFolder(sourceURI, destinationURI);
					return folderCopy.toClient();
				}
			});
			return copy;
		} finally {
			resetTempNameResources();
		}
	}
	
	protected RepoFolder copyFolder(String sourceURI, String destinationURI) {
		RepoFolder repoSource = getFolder(sourceURI, true);
		
		String copyURI = getCopyURI(destinationURI, false);
		RecursiveCopier copier = new RecursiveCopier(repoSource, copyURI);
		copier.copy();
		return copier.getCopiedRootFolder();
	}
	
	protected class RecursiveCopier implements ReferenceResolver {
		
		private final RepoFolder sourceRoot;
		private final String destinationRootURI;
		
		private final LinkedHashMap copiedFolders = new LinkedHashMap();
		private final LinkedHashMap copiedResources = new LinkedHashMap();
		private final Set copyingResourceStack = new HashSet();
		
		public RecursiveCopier(RepoFolder sourceRoot, String destinationRootURI) {
			this.sourceRoot = sourceRoot;
			this.destinationRootURI = destinationRootURI;
		}

		public void copy() {
			copyFolderRecursively(sourceRoot);
			save();
		}
		
		protected void save() {
			HibernateTemplate template = getHibernateTemplate();
			
			for (Iterator it = copiedFolders.values().iterator(); it.hasNext();) {
				RepoFolder folder = (RepoFolder) it.next();
				template.save(folder);
			}
			
			for (Iterator it = copiedResources.values().iterator(); it.hasNext();) {
				RepoResource resource = (RepoResource) it.next();
				template.save(resource);
			}
		}

		protected void copyFolderRecursively(RepoFolder folder) {
			if (log.isDebugEnabled()) {
				log.debug("Recursively copying folder " + folder.getURI());
			}
			
			copyFolder(folder);
			
			Set subfolders = folder.getSubFolders();
			if (subfolders != null) {
				for (Iterator it = subfolders.iterator(); it.hasNext();) {
					RepoFolder subfolder = (RepoFolder) it.next();
					if (!subfolder.isHidden() && getSecurityChecker().isFolderReadable(subfolder.getURI())) {
						copyFolderRecursively(subfolder);
					}
				}
			}
			
			Set resources = folder.getChildren();
			if (resources != null) {
				for (Iterator it = resources.iterator(); it.hasNext();) {
					RepoResource resource = (RepoResource) it.next();
					if (getSecurityChecker().isResourceReadable(resource.getResourceURI())) {
						copyResource(resource);
					}
				}
			}
		}

		protected RepoFolder copyFolder(RepoFolder folder) {
			String copyURI = getCopyURI(folder.getURI());
			RepoFolder folderCopy = (RepoFolder) copiedFolders.get(copyURI);
			if (folderCopy == null) {
				if (log.isDebugEnabled()) {
					log.debug("Creating copy of folder " + folder.getURI() + " at " + copyURI);
				}
				
				Folder copy = folder.toClient();
				copy.setVersion(Resource.VERSION_NEW);
				copy.setURIString(copyURI);
				
				folderCopy = createNewRepoFolder();
				RepoFolder copyParent = getCopyParent(folder);
				folderCopy.set(copy, copyParent, HibernateRepositoryServiceImpl.this);

				copiedFolders.put(copyURI, folderCopy);
			}
			
			return folderCopy;
		}
		
		protected RepoResource copyResource(RepoResource resource) {
			String copyURI = getCopyURI(resource.getResourceURI());
			RepoResource resourceCopy = (RepoResource) copiedResources.get(copyURI);
			if (resourceCopy == null) {
				resourceCopy = copyResource(resource, copyURI);
			}
			return resourceCopy;
		}

		protected RepoResource copyResource(RepoResource resource, String copyURI) {
			if (log.isDebugEnabled()) {
				log.debug("Creating copy of resource " + resource.getResourceURI() + " at " + copyURI);
			}
			
			if (!copyingResourceStack.add(copyURI)) {
				throw new JSException("jsexception.copy.resource.circular.reference", 
						new Object[]{resource.getResourceURI()});
			}
			try {
				Resource copy = getClientClone(resource);
				copy.setURIString(copyURI);
				
				Class persistentClass = resourcePersistentClass(copy.getClass());
				RepoResource resourceCopy = createPersistentResource(persistentClass);
				RepoFolder copyParent = getCopyParent(resource);
				resourceCopy.setParent(copyParent);
				resourceCopy.copyFromClient(copy, this);
				
				copiedResources.put(copyURI, resourceCopy);

				return resourceCopy;
			} finally {
				copyingResourceStack.remove(copyURI);
			}
		}

		protected String getCopyURI(String uri) {
			String copyURI;
			if (sourceRoot.getURI().equals(uri)) {
				copyURI = destinationRootURI;
			} else if (sourceRoot.isRoot()) {
				copyURI = destinationRootURI + uri;
			} else {
				copyURI = destinationRootURI + uri.substring(sourceRoot.getURI().length());
			}
			return copyURI;
		}
		
		protected RepoFolder getCopyParent(RepoResourceBase resource) {
			String uri = resource.getResourceURI();
			RepoFolder parent;
			if (sourceRoot.getURI().equals(uri)) {
				String destinationParentURI = RepositoryUtils.getParentPath(destinationRootURI);
				parent = getFolder(destinationParentURI, true);
			} else {
				String copyParentURI = getCopyURI(resource.getParent().getURI());
				parent = (RepoFolder) copiedFolders.get(copyParentURI);
				if (parent == null) {
					parent = copyFolder(resource.getParent());
				}
			}
			return parent;
		}

		protected boolean isCopiedResource(String uri) {
			return uri.startsWith(sourceRoot.getURI());
		}

		protected RepoResource getCopiedResourceReference(String uri, Class persistentClass) {
			String copyURI = getCopyURI(uri);
			RepoResource resourceCopy = (RepoResource) copiedResources.get(copyURI);
			if (resourceCopy == null) {
				if (!getSecurityChecker().isResourceReadable(uri)) {
					throw new JSException("jsexception.copy.resource.referenced.not.readable", new Object[]{uri});
				}
				RepoResource resource = findByURI(persistentClass, uri, true);
				resourceCopy = copyResource(resource, copyURI);
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Reference to " + uri + " has been changed to " + copyURI);
			}
			
			return resourceCopy;
		}

		protected RepoResource getCopyReference(String uri, Class persistentReferenceClass) {
			RepoResource reference;
			if (isCopiedResource(uri)) {
				reference = getCopiedResourceReference(uri, persistentReferenceClass);
			} else {
				reference = findByURI(persistentReferenceClass, uri, true);
			}
			return reference;
		}
		
		public RepoResource getExternalReference(String uri, Class persistentReferenceClass) {
			return getCopyReference(uri, persistentReferenceClass);
		}

		public RepoResource getPersistentReference(String uri, Class clientReferenceClass) {
			return getCopyReference(uri, resourcePersistentClass(clientReferenceClass));
		}

		public RepoResource getReference(RepoResource owner,
				ResourceReference resourceReference, Class persistentReferenceClass) {
			if (resourceReference == null) {
				return null;
			}
			RepoResource repoRes;
			if (resourceReference.isLocal()) {
				repoRes = getReference(owner, resourceReference.getLocalResource(), persistentReferenceClass);
			} else {
				repoRes = getCopyReference(resourceReference.getReferenceURI(), persistentReferenceClass);
			}
			return repoRes;
		}

		public RepoResource getReference(RepoResource owner, 
				Resource resource, Class persistentReferenceClass) {
			return HibernateRepositoryServiceImpl.this.getReference(owner, 
					resource, persistentReferenceClass, this);
		}
		
		public RepoFolder getCopiedRootFolder() {
			return (RepoFolder) copiedFolders.get(destinationRootURI);
		}
	}

	public void lockPath(RepoFolder folder) {
		if (!isLockFoldersOnPathChange()) {
			return;
		}

		HibernateTemplate template = getHibernateTemplate();
		RepoFolder parent = folder.getParent();
		if (parent != null && !parent.isNew()) {
			lockReadFolder(parent);
		}
		
		if (!folder.isNew()) {
			//lock the folder to indicate that its path has changed
			template.lock(folder, LockMode.UPGRADE);
		}
	}

	protected void lockReadFolder(RepoFolder folder) {
		HibernateTemplate template = getHibernateTemplate();
		Session session = template.getSessionFactory().getCurrentSession();
		LockMode folderLockMode = session.getCurrentLockMode(folder);
		if (LockMode.READ.equals(folderLockMode)) {
			String currentURI = folder.getURI();
			
			//refresh and lock the parent
			template.refresh(folder, LockMode.UPGRADE);
			
			//check whether the parent URI has changed
			if (!currentURI.equals(folder.getURI())) {
				throw new JSException("jsexception.folder.moved",
						new Object[] {currentURI, folder.getURI()});
			}
		}
	}
	
	public boolean isLockFoldersOnPathChange() {
		return lockFoldersOnPathChange;
	}

	public void setLockFoldersOnPathChange(boolean lockFoldersOnPathChange) {
		this.lockFoldersOnPathChange = lockFoldersOnPathChange;
	}
}
