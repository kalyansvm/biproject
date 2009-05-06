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

package com.jaspersoft.jasperserver.export.modules.repository;

import java.io.InputStream;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.lob.SerializableBlob;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: OlapUnitViewOptionsDataProvider.java 12568 2008-03-19 15:57:56Z lucian $
 */
public class OlapUnitViewOptionsDataProvider implements ResourceDataProvider {
	
	private static final Log log = LogFactory.getLog(OlapUnitViewOptionsDataProvider.class);

	private String filenameSuffix;

	public String getFileName(Resource resource) {
		return resource.getName() + getFilenameSuffix();
	}

	public InputStream getData(ExporterModuleContext exportContext, Resource resource) {
		OlapUnit unit = (OlapUnit) resource;
		InputStream dataStream;
		Object viewOptions = unit.getOlapViewOptions();
		if (viewOptions == null) {
			dataStream = null;
		} else if (viewOptions instanceof SerializableBlob) {
				SerializableBlob blob = (SerializableBlob) viewOptions;
				try {
					dataStream = blob.getBinaryStream();
				} catch (SQLException e) {
					throw new JSExceptionWrapper(e);
				}
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Olap unit view options is not an instance of SerializableBlob, not exporting");
			}
			dataStream = null;
		}
		return dataStream;
	}

	public String getFilenameSuffix() {
		return filenameSuffix;
	}

	public void setFilenameSuffix(String filenameSuffix) {
		this.filenameSuffix = filenameSuffix;
	}

}
