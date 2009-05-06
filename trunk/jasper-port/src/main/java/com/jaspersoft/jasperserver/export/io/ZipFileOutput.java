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

package com.jaspersoft.jasperserver.export.io;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ZipFileOutput.java 12510 2008-03-14 15:20:13Z lucian $
 */
public class ZipFileOutput extends BaseExportOutput {
	
	private static final Log log = LogFactory.getLog(ZipFileOutput.class);

	protected static final String ZIP_ENTRY_DIR_SUFFIX = "/";

	private final String zipFile;
	private final int level;
	private final PathProcessor pathProcessor;
	
	private Set directories;

	protected class EntryOutputStream extends OutputStream {
		public void close() throws IOException {
			zipOut.closeEntry();
		}

		public void flush() throws IOException {
			zipOut.flush();
		}

		public void write(byte[] b, int off, int len) throws IOException {
			zipOut.write(b, off, len);
		}

		public void write(byte[] b) throws IOException {
			zipOut.write(b);
		}

		public void write(int b) throws IOException {
			zipOut.write(b);
		}
	}
	
	protected ZipOutputStream zipOut;

	public ZipFileOutput(String zipFile, int level, PathProcessor pathProcessor, Properties outputProperties) {
		super(outputProperties);
		this.zipFile = zipFile;
		this.level = level;
		this.pathProcessor = pathProcessor;
	}

	public void open() {
		 try {
			OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(zipFile));
			zipOut = new ZipOutputStream(fileOut);
			zipOut.setLevel(level);			
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}

		directories = new HashSet();
	}
	
	public void close() throws IOException {
		zipOut.finish();
		zipOut.close();
	}

	public OutputStream getFileOutputStream(String path) throws IOException {
		String zipPath = getZipPath(path);
		ZipEntry fileEntry = new ZipEntry(zipPath);
		zipOut.putNextEntry(fileEntry);
		EntryOutputStream entryOut = new EntryOutputStream();
		return entryOut;
	}

	protected String getZipPath(String path) {
		String zipPath = pathProcessor.processPath(path);
		return zipPath;
	}

	public void mkdir(String path) throws IOException {
		if (directories.add(path)) {
			String zipPath = getZipPath(path);
			ZipEntry dirEntry = new ZipEntry(zipPath + ZIP_ENTRY_DIR_SUFFIX);
			zipOut.putNextEntry(dirEntry);
			zipOut.closeEntry();
		}		
	}

}
