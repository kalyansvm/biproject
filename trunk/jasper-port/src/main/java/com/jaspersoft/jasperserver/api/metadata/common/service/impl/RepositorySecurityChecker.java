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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateRepositoryServiceImpl.java 11286 2007-12-17 17:00:30Z lucian $
 */
public interface RepositorySecurityChecker {

	/** Filters allResources.
	 *  Populates removableResources with the resources that can be deleted.
	 *  Populates editableResources with the resources that can be edited.
	 */
	void filterResources(List allResources, Map removableResources,
			Map editableResources);

	/** Filters resource.
	 *  Adds resource to removableResources if it can be deleted.
	 *  Adds resource to editableResources if it can be edited.
	 */
	void filterResource(Resource resource, Map removableResources,
			Map editableResources);

	/** Checks whether the given resource can be edited */
	boolean isEditable(Resource resource);

	/** Checks whether the given resource can be deleted */
	boolean isRemovable(Resource resource);

	boolean isResourceReadable(String uri);

	boolean isFolderReadable(String uri);

}