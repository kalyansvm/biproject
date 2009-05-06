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

/**
 * @author Lucian Chirita
 *
 */
public class FileBufferedDataContainerFactory implements DataContainerFactory {

	private int memoryThreshold = FileBufferedDataContainer.DEFAULT_MEMORY_THRESHOLD;
	private int initialMemoryBuffer = FileBufferedDataContainer.DEFAULT_INITIAL_MEMORY_BUFFER;
	
	public DataContainer createDataContainer() {
		return new FileBufferedDataContainer(
				getMemoryThreshold(), getInitialMemoryBuffer());
	}

	public int getMemoryThreshold() {
		return memoryThreshold;
	}

	public void setMemoryThreshold(int memoryThreshold) {
		this.memoryThreshold = memoryThreshold;
	}

	public int getInitialMemoryBuffer() {
		return initialMemoryBuffer;
	}

	public void setInitialMemoryBuffer(int initialMemoryBuffer) {
		this.initialMemoryBuffer = initialMemoryBuffer;
	}

}
