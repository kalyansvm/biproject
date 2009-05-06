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

package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Lucian Chirita
 *
 */
public class MemoryDataContainer implements DataContainer {

	private byte[] data;
	
	public MemoryDataContainer() {
		this(null);
	}
	
	public MemoryDataContainer(byte[] data) {
		this.data = data;
	}

	public OutputStream getOutputStream() {
		return new DataOutputStream();
	}

	public int dataSize() {
		return data == null ? 0 : data.length;
	}
	
	public InputStream getInputStream() {
		return data == null ? null : new ByteArrayInputStream(data);
	}

	public byte[] getData() {
		return data;
	}

	public boolean hasData() {
		return data != null;
	}
	
	protected class DataOutputStream extends ByteArrayOutputStream {
		public void close() throws IOException {
			super.close();
			data = toByteArray();
		}
	}
}
