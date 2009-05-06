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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;


/**
 * This class adds security/permission checking utilities for RepositoryService
 */
public class RepositoryServiceSecurityChecker extends BaseRepositorySecurityChecker {
	
	private static final Log log = LogFactory.getLog(RepositoryServiceSecurityChecker.class);
	private RepositoryService securityChecker;
	
	public RepositoryServiceSecurityChecker() {
		
	}
    
    /** Checks whether the given resource can be edited */
    public boolean isEditable(Resource resource) {
    	try {
			securityChecker.saveResource(null,resource);
			return true;
		} catch (Exception e) {
			log.debug("No UPDATE permission for < " + resource.getURIString() + ">:" +  e.getMessage());
			return false;
		}
    }
    
    /** Checks whether the given resource can be deleted */
    public boolean isRemovable(Resource resource) {
    	try {
			securityChecker.deleteResource(null,resource.getURI());
			return true;
		} catch (Exception e) {
			log.debug("No DELETE permission for < " + resource.getURIString() + ">:" +  e.getMessage());
			return false;
		}	
    }
    
    public boolean isResourceReadable(String uri) {
    	try {
			securityChecker.getResource(null, uri);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    public boolean isFolderReadable(String uri) {
    	try {
			securityChecker.getFolder(null, uri);
			return true;
		} catch (Exception e) {
			return false;
		}
    }

	public RepositoryService getSecurityChecker() {
		return securityChecker;
	}

	public void setSecurityChecker(RepositoryService securityChecker) {
		this.securityChecker = securityChecker;
	}
}
