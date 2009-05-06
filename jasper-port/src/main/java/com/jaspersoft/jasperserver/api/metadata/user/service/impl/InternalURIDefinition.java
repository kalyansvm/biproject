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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * @author swood
 *
 */
public class InternalURIDefinition implements InternalURI {
	
	private String uri;
	private String folderUri;
	
	public InternalURIDefinition(String uri) {
		this.uri = uri;
		
		if (uri == null || uri.length() == 0) {
			this.folderUri = null;
		} else {
			int lastSeparator = uri.lastIndexOf(Folder.SEPARATOR);
			
			if (lastSeparator <= 0) {
				this.folderUri = null;
			} else {
				this.folderUri = uri.substring(0, lastSeparator);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getParentPath()
	 */
	public String getParentPath() {
		return getParentFolder() == null ? null : getParentFolder();
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getParentURI()
	 */
	public String getParentURI() {
		return getParentFolder() == null ? null : getProtocol() + ":" + getParentFolder();
	}

	public String getParentFolder() {
		return folderUri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getPath()
	 */
	public String getPath() {
		return uri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getProtocol()
	 */
	public String getProtocol() {
		return Resource.URI_PROTOCOL;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getURI()
	 */
	public String getURI() {
		return getProtocol() + ":" + getPath();
	}

}
