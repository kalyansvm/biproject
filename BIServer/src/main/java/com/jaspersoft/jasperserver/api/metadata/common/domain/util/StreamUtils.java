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
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: StreamUtils.java 13765 2008-05-23 07:33:30Z swood $
 */
public class StreamUtils {
	private static final Log log = LogFactory.getLog(StreamUtils.class);
	private static final int READ_STREAM_BUFFER_SIZE = 10000;

	public static byte[] readData(InputStream is) {
		if (is == null) {
			return null;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] bytes = new byte[READ_STREAM_BUFFER_SIZE];
		int ln = 0;
		try {
			while ((ln = is.read(bytes)) > 0) {
				baos.write(bytes, 0, ln);
			}
		} catch (IOException e) {
			log.error("Error while reading data.", e);
			throw new JSExceptionWrapper(e);
		}

		return baos.toByteArray();
	}

	public static byte[] readData(InputStream is, int size) {
		if (is == null) {
			return null;
		}

		try {
			byte[] data = new byte[size];
			int offset = 0;
			while (size > 0) {
				int read = is.read(data, offset, size);
				if (read < 0) {
					throw new JSException("jsexception.input.stream.exhausted", new Object[]{new Integer(size)});
				}
				offset += read;
				size -= read;
			}
			return data;
		} catch (IOException e) {
			log.error("Error while reading data.", e);
			throw new JSExceptionWrapper(e);
		}
	}

	public static byte[] readData(Blob blob) {
		if (blob == null) {
			return null;
		}

		try {
			return readData(blob.getBinaryStream());
		} catch (SQLException e) {
			log.error("Error while reading blob data", e);
			throw new JSExceptionWrapper(e);
		}
	}


	public static void pipeData(InputStream is, OutputStream os) throws IOException {
		if (is == null) {
			return;
		}
		
		byte[] bytes = new byte[READ_STREAM_BUFFER_SIZE];
		int ln = 0;
		while ((ln = is.read(bytes)) > 0) {
			os.write(bytes, 0, ln);
		}
	}
	
	public static void writeObjectByteData(ObjectOutputStream objectStream, 
			InputStream data) throws IOException {
		pipeData(data, objectStream);
	}
	
	public static void readObjectByteData(ObjectInputStream objectStream, 
			int size, OutputStream outStream) throws IOException {
		byte[] buffer = new byte[READ_STREAM_BUFFER_SIZE];
		
		while (size > 0) {
			int read = buffer.length;
			if (read > size)
			{
				read = size;
			}
			objectStream.readFully(buffer, 0, read);
			
			outStream.write(buffer, 0, read);
			
			size -= read;
		}
	}
	
	public static void pipeData(InputStream is, DataContainer dataContainer) {
		boolean close = true;
		OutputStream out = dataContainer.getOutputStream();
		try {
			pipeData(is, out);
			
			close = false;
			out.close();
		} catch (IOException e) {
			throw new JSExceptionWrapper(e);
		} finally {
			if (close) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("Error closing stream", e);
				}
			}
		}
	}
	
}
