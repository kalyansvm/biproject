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

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryUnsecure;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;

/**
 * @author swood
 *
 */
public interface HibernateRepositoryService extends RepositoryService, RepositoryUnsecure {
	/**
	 * 
	 * @param context
	 * @param uri
	 * @return the resource if found or null otherwise
	 */
	public Resource getResource(ExecutionContext context, String uri);


	/**
	 * 
	 * @param context
	 * @param uri
	 * @return
	 * @throws JSResourceNotFoundException
	 */
	public FileResourceData getResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException;

	

	/**
	 * 
	 */
	public void saveResource(ExecutionContext context, Resource resource);
	

	public void deleteResource(ExecutionContext context, String uri);

	
	public void deleteFolder(ExecutionContext context, String uri);

	
	public void delete(ExecutionContext context, String resourceURIs[], String folderURIs[]);
	
	
	/**
	 * Given filtering criteria, find relevant Resources.
	 * 
	 * @param context
	 * @param criteria
	 * @return Array of found Resources
	 */
	public ResourceLookup[] findResource(ExecutionContext context, FilterCriteria criteria);
	
	/*
	 * Return a Resource that does not yet contain content.
	 * 
	 * @param context
	 * @param class - class of resource to create
	 * @return Resource
	 */
	public Resource newResource(ExecutionContext context, Class _class);

	public RepoResource findByURI(Class persistentClass, String uri, boolean required);
	
	public RepoResource getRepoResource(Resource resource);
}
