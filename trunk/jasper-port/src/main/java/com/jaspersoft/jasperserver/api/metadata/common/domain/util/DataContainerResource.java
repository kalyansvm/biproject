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

package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.io.InputStream;

import org.springframework.core.io.AbstractResource;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

/**
 * @author Lucian Chirita
 *
 */
public class DataContainerResource extends AbstractResource {

	private static final String DESCRIPTION = "Contained data";
	
	private final DataContainer data;
	
	public DataContainerResource(DataContainer data) {
		this.data = data;
	}
	
	public String getDescription() {
		return DESCRIPTION;
	}

	public InputStream getInputStream() {
		return data.getInputStream();
	}
	
	public boolean exists() {
		return true;
	}

}
