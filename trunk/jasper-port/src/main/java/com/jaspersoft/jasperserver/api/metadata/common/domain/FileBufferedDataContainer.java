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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.FileBufferedOutputStream;

/**
 * @author Lucian Chirita
 * 
 */
public class FileBufferedDataContainer implements DataContainer {
	
	public static final int DEFAULT_MEMORY_THRESHOLD = 1 << 16;
	public static final int DEFAULT_INITIAL_MEMORY_BUFFER = 1 << 14;

	private int memoryThreshold;
	private transient FileBufferedOutputStream data;
	
	public FileBufferedDataContainer() {
		this(DEFAULT_MEMORY_THRESHOLD, DEFAULT_INITIAL_MEMORY_BUFFER);
	}
	
	public FileBufferedDataContainer(int memoryThreshold, int initialMemoryBuffer) {
		this.memoryThreshold = memoryThreshold;
		createDataBuffer(initialMemoryBuffer);
	}

	private void createDataBuffer(int initialMemoryBuffer) {
		data = new FileBufferedOutputStream(memoryThreshold, initialMemoryBuffer);
	}

	public OutputStream getOutputStream() {
		return data;
	}
	
	public byte[] getData() {
		return StreamUtils.readData(getInputStream());
	}

	public InputStream getInputStream() {
		try {
			return data.getDataInputStream();
		} catch (IOException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	public int dataSize() {
		return data.size();
	}

	public boolean hasData() {
		return true;
	}

	private void writeObject(java.io.ObjectOutputStream stream)
			throws IOException {
		stream.writeInt(memoryThreshold);
		
		stream.writeInt(data.size());
		StreamUtils.writeObjectByteData(stream, data.getDataInputStream());
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		memoryThreshold = stream.readInt();
		
		int size = stream.readInt();
		createDataBuffer(size <= memoryThreshold ? size : memoryThreshold);
		StreamUtils.readObjectByteData(stream, size, data);
		data.close();
	}

}
