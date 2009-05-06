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

package com.jaspersoft.jasperserver.export.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PathUtils.java 8408 2007-05-29 23:29:12Z melih $
 */
public class PathUtils {

	public static final char FILE_SEPARATOR = '/';
	
	protected static final Pattern MULTI_FILE_SEP_PATTERN = Pattern.compile("/{2,}");
	protected static final String MULTI_FILE_SEP_REPLACEMENT = "/";
	
	protected static final Pattern SPLIT_URI_PATTERN = Pattern.compile("^(.*)/([^/]+)/*$");
	protected static final int SPLIT_URI_PATTERN_PARENT_IDX = 1;
	protected static final int SPLIT_URI_PATTERN_NAME_IDX = 2;
	
	public static class SplittedPath {
		public final String parentPath;
		public final String name;
		
		public SplittedPath(final String parentPath, final String name) {
			this.parentPath = parentPath;
			this.name = name;
		}
	}
	
	public static SplittedPath splitPath(String uri) {
		Matcher matcher = SPLIT_URI_PATTERN.matcher(uri);
		SplittedPath splUri;
		if (matcher.matches()) {
			String parentURI = matcher.group(SPLIT_URI_PATTERN_PARENT_IDX);
			if (parentURI.length() == 0) {
				parentURI = null;
			}

			String name = matcher.group(SPLIT_URI_PATTERN_NAME_IDX);
			
			splUri = new SplittedPath(parentURI, name);
		} else {
			splUri = null;
		}
		return splUri;
	}

	public static String concatPaths(String path1, String path2) {
		if (path1 == null) {
			if (path2 == null) {
				return null;
			}
			
			return normalizePath(path2);
		}
		if (path2 == null) {
			return normalizePath(path1);
		}
		
		return normalizePath(path1 + FILE_SEPARATOR + path2);
	}

	public static String normalizePath(String path) {
		if (path == null) {
			return null;
		}

		path = MULTI_FILE_SEP_PATTERN.matcher(path).replaceAll(MULTI_FILE_SEP_REPLACEMENT);
		
		if (path.length() > 1 && path.charAt(path.length() - 1) == FILE_SEPARATOR) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

}
