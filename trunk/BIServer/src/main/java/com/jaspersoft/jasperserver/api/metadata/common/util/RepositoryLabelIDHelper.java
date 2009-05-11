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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;


/**
 * @author Alex Chan (achan@jaspersoft.com)
 * @version $Id: RepositoryLableIDHelper.java$
 *
 * */
public class RepositoryLabelIDHelper {

	/**
	 * @author Alex Chan (achan@jaspersoft.com)
	 * 
	 *
	 * @param RepositoryService repository service for accessing the repository
	 * @param parentFolder Full Parent Folder URI for the resource
	 * @param label The display label for the resource
	 * @return returns a generated ID converting non-alphanumberic characters into underscore, if the id already exists, _1, _2 so on and so forth will be appended until it's unique.
	 */
	public static String generateIdBasedOnLabel(RepositoryService repository, String parentFolder, String label)  {
		
		// validation
    	String inputLabel = ((label == null) ? "" : label.trim());
    	if ("".equals(inputLabel)) {
    		return "";
    	}
    	
    	// get list of resource id(name) in the parent folder, so make sure the generated id(name) will be unique
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentFolder));
        List listOfResources = repository.loadResourcesList(null, criteria);    
        
        List repoFolderList = repository.getSubFolders(null, parentFolder);
        listOfResources.addAll(repoFolderList);
    	
        // replace any non-alphanumeric characters into underscore
        String id = inputLabel.replaceAll("[^a-zA-Z0-9]", "_");
        String newId = id;
        boolean doesInternalNameExist = true;
        int i = 0;
        // if the same generated id already exists, append _1, _2, so on and so forth until it's unique.
        while (doesInternalNameExist) {
        	doesInternalNameExist = false;
        	for (int j=0; j<listOfResources.size(); j++) {
            	String curInternalName = ((Resource)listOfResources.get(j)).getName();
            	if (curInternalName == null) {
            		curInternalName = "";
             	}
            	if (curInternalName.equalsIgnoreCase(newId)) {           		
            		doesInternalNameExist = true;
            		break;
            	}
        	} 
        	if (doesInternalNameExist) {
               i++;
        	   newId = id + "_" + i;
        	}
        }
    	return newId;
	}	
	
}
