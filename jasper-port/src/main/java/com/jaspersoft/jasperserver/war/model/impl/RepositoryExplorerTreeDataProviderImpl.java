/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.war.model.impl;

import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.model.JSONObject;
import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * @author achan
 *
 */
public class RepositoryExplorerTreeDataProviderImpl implements TreeDataProvider {
	
    private RepositoryService repositoryService;
    private RepositorySecurityChecker repositoryServiceSecurityChecker;
    private TreeDataFilter filter;
    
    private static class Permissions implements JSONObject {
        public boolean isWritable = true;
        public boolean isRemovable = true;
        public String toJSONString() {
        	StringBuffer str = new StringBuffer("{");
        	str.append((isWritable) ? "\"isWritable\":true" : "\"isWritable\":false");
        	str.append((isRemovable) ? ",\"isRemovable\":true" : ",\"isRemovable\":false");        	
        	str.append("}");
        	
            return str.toString();
        }
    }

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.model.TreeDataProvider#getChildren(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String, int)
	 */
	public List getChildren(ExecutionContext executionContext,
			String parentUri, int depth) {
        TreeNode n = getNode(executionContext, parentUri, depth + 1);
        if (n != null) {
            return n.getChildren();
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.model.TreeDataProvider#getNode(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String, int)
	 */
	public TreeNode getNode(ExecutionContext executionContext, String uri,
			int depth) {
				
        Resource resource = repositoryService.getResource(executionContext, uri);

        if (resource != null) {
            return createNode(resource, false);
        }
      
        Folder folder = repositoryService.getFolder(executionContext, uri);
        if (folder != null) {

            TreeNode node = createNode(folder, true);
            if (depth > 0) {
                processFolder(node, depth - 1);
            }

            return node;
        }
        
        return null;
	}
	
    private TreeNode createNode(Resource resource, boolean isFolder) {
        Permissions extraProperty = new Permissions();
        extraProperty.isWritable = repositoryServiceSecurityChecker.isEditable(resource);
        extraProperty.isRemovable = repositoryServiceSecurityChecker.isRemovable(resource);
        if (isFolder) {
            return new TreeNodeImpl(this, 
                    resource.getName(), resource.getLabel(), 
                    resource.getResourceType(), resource.getURIString(),
                    1, extraProperty);
        }
        return new TreeNodeImpl(this, 
                resource.getName(), resource.getLabel(), 
                resource.getResourceType(), resource.getURIString(),
                extraProperty);
    }
    
    private void processFolder(TreeNode folder, int depth) {
        
        String folderURI = folder.getUriString();
        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folderURI));
        
        List folders = repositoryService.getSubFolders(null, folderURI);
        
        List resources = repositoryService.loadResourcesList(null, criteria);
        
        /*List allResources = new ArrayList();
        allResources.addAll(folders);
        allResources.addAll(resources);*/
        
        if (folders != null) {
            for (Iterator iter = folders.iterator(); iter.hasNext(); ) {
                Folder f = (Folder) iter.next();
                TreeNode n = createNode(f, true);
                if (filter == null || filter.filter(n)) {
                    folder.getChildren().add(n);
                    if (depth > 0) {
                        processFolder(n, depth - 1);
                    }
                }
            }
        }
        if (resources != null) {
            for (Iterator iter = resources.iterator(); iter.hasNext(); ) {
                Resource r = (Resource) iter.next();
                TreeNode n = createNode(r, false);
                if (filter == null || filter.filter(n)) {
                    folder.getChildren().add(n);
                }
            }
        }
        
    }

	public TreeDataFilter getFilter() {
		return filter;
	}

	public void setFilter(TreeDataFilter filter) {
		this.filter = filter;
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public RepositorySecurityChecker getRepositoryServiceSecurityChecker() {
		return repositoryServiceSecurityChecker;
	}

	public void setRepositoryServiceSecurityChecker(
			RepositorySecurityChecker repositoryServiceSecurityChecker) {
		this.repositoryServiceSecurityChecker = repositoryServiceSecurityChecker;
	}
	
	

}
