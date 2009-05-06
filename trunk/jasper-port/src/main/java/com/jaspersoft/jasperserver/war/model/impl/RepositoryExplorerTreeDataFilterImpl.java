package com.jaspersoft.jasperserver.war.model.impl;


import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

import org.acegisecurity.Authentication;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.acegisecurity.context.SecurityContextHolder;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

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

/**
 * @author achan
 *
 */
public class RepositoryExplorerTreeDataFilterImpl implements TreeDataFilter {

    
    private List uriList;
    private UserAuthorityService userService;
    private String roleToShowTempFolder;

    /**
     * Returns false if node is a resource or a child of a resource with uri 
     * which is in uriList.
     * Returns true otherwise.
     * Returns true if no uriList configured.
     */
    public boolean filter(TreeNode node) {
    	
        if (uriList != null) {
            String nodeUri = node.getUriString();
            for (Iterator iter = uriList.iterator(); iter.hasNext(); ) {
                String uri = (String) iter.next();
                if ((nodeUri.equalsIgnoreCase(uri)) || (nodeUri.indexOf(uri + "/") == 0)) {
                	Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                	String userName = (String)existingAuth.getName();
                	List userList = userService.getUsersInRole(null, roleToShowTempFolder);
                	//if user has the role, in this case ROLE_ADMIN from the xml file, return true
                	for (Iterator userIter = userList.iterator(); userIter.hasNext();) {
                		String user = ((User)userIter.next()).getUsername();
                		if (user.equalsIgnoreCase(userName)) {
                			return true;
                		}
                	}
                    return false;
                }
            }
        }
        return true;
    }

    public List getUriList() {
        return uriList;
    }

    public void setUriList(List uriList) {
        this.uriList = uriList;
    }
    
    public UserAuthorityService getUserService() {
        return userService;
    }

    public void setUserService(UserAuthorityService service) {
        this.userService = service;
    }  
    
    public String getRoleToShowTempFolder() {
        return roleToShowTempFolder;
    }

    public void setRoleToShowTempFolder(String folder) {
        this.roleToShowTempFolder = folder;
    } 
    
}
