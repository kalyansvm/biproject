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

import java.io.InputStream;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FileResourceDataProvider.java 8408 2007-05-29 23:29:12Z melih $
 */
public class FileResourceDataProvider extends BaseResoueceDataProvider {

	public InputStream getData(ExporterModuleContext exportContext, Resource resource) {
		FileResource fileRes = (FileResource) resource;
		InputStream data;
		if (fileRes.isReference()) {
			data = null;
		} else {
			ExecutionContext executionContext = exportContext.getExportTask().getExecutionContext();
			String uri = resource.getURIString();
			FileResourceData resourceData = getRepository().getResourceData(executionContext, uri);
			data = resourceData.getDataStream();
		}
		return data;
	}

}
