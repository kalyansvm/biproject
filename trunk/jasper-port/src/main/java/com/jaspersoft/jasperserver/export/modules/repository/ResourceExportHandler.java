/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.export.modules.repository;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceReferenceBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceExportHandler.java 13374 2008-05-05 11:45:36Z lucian $
 */
public interface ResourceExportHandler {

	ResourceReferenceBean handleReference(ResourceReference reference);
	
	ResourceBean handleResource(Resource res);
	
	void queueResource(String uri);
	
	void queueResource(String uri, boolean ignoreMissing);
	
	String handleData(Resource resource, String dataProviderId);
	
	ResourceModuleConfiguration getConfiguration();
	
}
