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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

/**
 * @author Lucian Chirita
 *
 */
public class RepositoryUtils {

	public static String getParentPath(String path) {
		if (path.equals(Folder.SEPARATOR)) {
			return null;
		}
		
		int lastSep = path.lastIndexOf(Folder.SEPARATOR);
		if (lastSep < 0) {
			throw new JSException("Path " + path + " is not an absolute repository path");
		}
		
		String parentPath;
		if (lastSep == 0) {
			parentPath = Folder.SEPARATOR;
		} else {
			parentPath = path.substring(0, lastSep);
		}
		return parentPath;
	}
	
	public static String getName(String path) {
		if (path.equals(Folder.SEPARATOR)) {
			return Folder.SEPARATOR;
		}
		
		int lastSep = path.lastIndexOf(Folder.SEPARATOR);
		if (lastSep < 0) {
			throw new JSException("Path " + path + " is not an absolute repository path");
		}
		
		return path.substring(lastSep + 1);
	}

	public static String concatenatePath(String parentPath, String name) {
		StringBuffer concantenated = new StringBuffer(parentPath);
		if (!parentPath.equals(Folder.SEPARATOR)) {
			concantenated.append(Folder.SEPARATOR);
		}
		concantenated.append(name);
		return concantenated.toString();
	}
	
}
