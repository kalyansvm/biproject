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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.io.InputStream;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FileResourceData.java 11040 2007-12-04 16:21:12Z lucian $
 */
public class FileResourceData {
	private final DataContainer dataContainer;

	public FileResourceData(byte[] data) {
		this.dataContainer = new MemoryDataContainer(data);
	}

	public FileResourceData(InputStream is) {
		this.dataContainer = new FileBufferedDataContainer();
		StreamUtils.pipeData(is, this.dataContainer);
	}

	public boolean hasData() {
		return dataContainer.hasData();
	}
	
	public int dataSize() {
		return dataContainer.dataSize();
	}
	
	public byte[] getData() {
		return dataContainer.getData();
	}

	public InputStream getDataStream() {
		return dataContainer.getInputStream();
	}
}
