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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;


/**
 * Top level class for accessing the repository metadata.
 * This class has various methods for retrieving resources from the repository
 * and for uploading resources to the repository.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepositoryService.java 12895 2008-04-09 17:31:17Z lucian $
 */
public interface RepositoryService
{

	/**
	 * 
	 * @param context
	 * @param uri
	 * @return the resource if found or null otherwise
	 */
	public Resource getResource(ExecutionContext context, String uri);
	
	public Resource getResource(ExecutionContext context, String uri, Class resourceType);


	/**
	 * 
	 * @param context
	 * @param uri
	 * @return
	 * @throws JSResourceNotFoundException
	 */
	public FileResourceData getResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException;


	public FileResourceData getContentResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException;


	public Folder getFolder(ExecutionContext context, String uri);


	public void saveFolder(ExecutionContext context, Folder folder);


	public List getAllFolders(ExecutionContext context);


	public List getSubFolders(ExecutionContext context, String folderURI);
	
	public boolean folderExists(ExecutionContext context, String uri);

	/**
	 * 
	 */
	public ValidationErrors validateResource(ExecutionContext context, Resource resource, ValidationErrorFilter filter);
	
	public ValidationErrors validateFolder(ExecutionContext context, Folder folder, ValidationErrorFilter filter);
	

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
	
	public ResourceLookup[] findResources(ExecutionContext context, FilterCriteria[] criteria);

	public List loadResourcesList(final FilterCriteria filterCriteria);

	public List loadResourcesList(ExecutionContext context, FilterCriteria filterCriteria);

	public List loadResourcesList(ExecutionContext context, FilterCriteria[] filterCriteria);

	public List loadClientResources(FilterCriteria filterCriteria);

	/**
	 * Return a Resource that does not yet contain content.
	 * 
	 * @param context
	 * @param class - class of resource to create
	 * @return Resource
	 */
	public Resource newResource(ExecutionContext context, Class _class);

	public String getChildrenFolderName(String resourceName);

	public boolean resourceExists(ExecutionContext executionContext, String uri);

	public boolean resourceExists(ExecutionContext executionContext, String uri, Class resourceType);

	public boolean resourceExists(ExecutionContext executionContext, FilterCriteria filterCriteria);

	/**
	 * Determines whether a resource or folder having a specified URI exists in the repository.
	 * <p/>
	 * Repository entities are uniquely identified by their URI.  This method can be used to check
	 * whether a URI is already present in the repository.
	 * 
	 * @param executionContext the execution context
	 * @param uri the URI
	 * @return <code>true</code> iff the URI is present in the repository
	 */
	public boolean repositoryPathExists(ExecutionContext executionContext, String uri);
	
	/*
	 * set hidden flag for a given folder, so it won't appear in the results for any repo operation
	 */
	void hideFolder(String uri);
	/*
	 * unset hidden flag for a given folder
	 */
	void unhideFolder(String uri);
	
	void moveFolder(ExecutionContext context, String sourceURI, String destinationFolderURI);
	
	void moveResource(ExecutionContext context, String sourceURI, String destinationFolderURI);
	
	Resource copyResource(ExecutionContext context, String sourceURI, String destinationURI);
	
	Folder copyFolder(ExecutionContext context, String sourceURI, String destinationURI);
	
}
