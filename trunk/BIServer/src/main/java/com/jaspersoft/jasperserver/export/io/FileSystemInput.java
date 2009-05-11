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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FileSystemInput.java 12510 2008-03-14 15:20:13Z lucian $
 */
public class FileSystemInput extends BaseImportInput {
	
	private static final Log log = LogFactory.getLog(FileSystemInput.class);
	
	private final String rootDirName;
	private final FileSystemInputManager manager;
	private PathProcessor pathProcessor;

	private File rootDir;

	public FileSystemInput(String rootDir, PathProcessor pathProcessor, FileSystemInputManager manager) {
		this.rootDirName = rootDir;
		this.pathProcessor = pathProcessor;
		this.manager = manager;
	}

	public void open() {
		rootDir = new File(rootDirName);
		if (!rootDir.exists() || !rootDir.isDirectory()) {
			throw new JSException("jsexception.import.directory.not.found", new Object[] {rootDirName});
		}
	}

	public void close() {
	}

	public InputStream getFileInputStream(String path) {
		try {
			File file = getFile(path);
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			return in;
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}		
	}

	protected File getFile(String path) {
		String fsPath = pathProcessor.processPath(path);
		File file = new File(rootDir, fsPath);
		return file;
	}

	public boolean fileExists(String path) {
		File file = getFile(path);
		return file.exists() && file.isFile();
	}

	public boolean folderExists(String path) {
		File file = getFile(path);
		return file.exists() && file.isDirectory();
	}

	public void setPathProcessor(PathProcessor pathProcessor) {
		this.pathProcessor = pathProcessor;
	}

	public void propertiesRead(Properties properties) {
		manager.updateInputProperties(this, properties);
	}

}
