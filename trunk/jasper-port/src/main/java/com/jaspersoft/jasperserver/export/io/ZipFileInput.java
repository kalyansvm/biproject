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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ZipFileInput.java 12510 2008-03-14 15:20:13Z lucian $
 */
public class ZipFileInput extends BaseImportInput {
	
	private final String zipFilename;
	private final ZipFileInputManager manager;
	private PathProcessor pathProcessor;
	
	private ZipFile zipFile;

	public ZipFileInput(String zipFilename, PathProcessor pathProcessor, ZipFileInputManager manager) {
		this.zipFilename = zipFilename;
		this.pathProcessor = pathProcessor;
		this.manager = manager;
	}

	public void open() throws IOException {
		zipFile = new ZipFile(new File(zipFilename), ZipFile.OPEN_READ);
	}

	public void close() throws IOException {
		zipFile.close();
	}

	public boolean fileExists(String path) {
		ZipEntry entry = zipFile.getEntry(getZipPath(path));
		return entry != null && !entry.isDirectory();
	}

	public boolean folderExists(String path) {
		ZipEntry entry = zipFile.getEntry(getZipPath(path));
		return entry != null && entry.isDirectory();
	}

	public InputStream getFileInputStream(String path) throws IOException {
		ZipEntry entry = zipFile.getEntry(getZipPath(path));
		return zipFile.getInputStream(entry);
	}

	protected String getZipPath(String path) {
		return pathProcessor.processPath(path);
	}
	
	public void propertiesRead(Properties properties) {
		manager.updateInputProperties(this, properties);
	}

	public void setPathProcessor(PathProcessor pathProcessor) {
		this.pathProcessor = pathProcessor;
	}

}
