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

import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.common.util.RepositoryLabelIDHelper;

/**
 * Test class to an end point to connect across SOAP
 * @author achan 
 *
 */
public class HibernateUtil {

	
	private RepositoryService repo;
	
	public HibernateUtil(RepositoryService repository) {
		repo = repository;
	}
	
	
	public RepositoryService getRepo() {
		return repo;
	}


	public void setRepo(RepositoryService repo) {
		this.repo = repo;
	}


	public boolean resourceDisplayNameExists(String parentUri, String name, String displayName) {
		
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentUri.startsWith("repo:") ? parentUri.substring(5) : parentUri));

		List resources = repo.loadResourcesList(null, criteria); 
		for (int i=0; i<resources.size(); i++) {
			ResourceLookupImpl res = (ResourceLookupImpl)resources.get(i);
			if (res.getLabel().equalsIgnoreCase(displayName) && (!res.getName().equalsIgnoreCase(name))) {
				return true;                   
			}
		}
		return false;
	}	
	

	public boolean folderDisplayNameExists(String parentUri, String displayName) {
		List repoFolderList = repo.getSubFolders(null, parentUri.startsWith("repo:") ? parentUri.substring(5) : parentUri);
		for (int i=0; i<repoFolderList.size(); i++) {
			FolderImpl repoFolder = (FolderImpl)repoFolderList.get(i);
			if (displayName.equalsIgnoreCase(repoFolder.getLabel())) {
				return true;                   
			}
		}
		return false;
	}
	
	public boolean doesDisplayNameExist(String parentFolderUri, String name, String displayName) {
		return resourceDisplayNameExists(parentFolderUri, name, displayName) || folderDisplayNameExists(parentFolderUri, displayName);
	}
	
	public String generateUniqueID(String parentFolderUri, String displayName) {
		return RepositoryLabelIDHelper.generateIdBasedOnLabel(repo, parentFolderUri, displayName);
	}

	
}
