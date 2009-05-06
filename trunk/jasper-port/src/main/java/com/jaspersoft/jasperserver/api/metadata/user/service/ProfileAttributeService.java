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
package com.jaspersoft.jasperserver.api.metadata.user.service;

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;


/**
 * @author sbirney
 *
 */
public interface ProfileAttributeService {

    /*
     * Pass in an attr with principal and attrName to find matching 
     * persisted attr
     */
    public ProfileAttribute getProfileAttribute(ExecutionContext context, 
						ProfileAttribute attr);

    /*
     * Find all matching attrs for the passed in principal
     */
    public List getProfileAttributesForPrincipal(ExecutionContext context, 
						 Object principal);

    /*
     * Create an empty new attr
     */
    public ProfileAttribute newProfileAttribute(ExecutionContext context);

    /*
     * Save or update the given attr
     */
    public void putProfileAttribute(ExecutionContext context, 
				    ProfileAttribute attr);

    /**
     * @return Returns the userService.
     */
    public UserAuthorityService getUserAuthorityService();

    /**
     * @param userService The userService to set.
     */
    public void setUserAuthorityService(UserAuthorityService userService);

    /*
    public void deleteProfileAttribute(ExecutionContext context, 
				       ProfileAttribute attr);
	
    public void deleteProfileAttributesForPrincipal(ExecutionContext context, 
						    Object principal);
    */
	
}
