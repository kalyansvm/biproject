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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.commons.collections.ReferenceMap;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JarURLStreamHandler.java 8408 2007-05-29 23:29:12Z melih $
 */
public class JarURLStreamHandler extends URLStreamHandler {
	
	private static final String PROTOCOL = "jsjar";
	private static final String URL_PREFIX = PROTOCOL + ":";
	private static final int URL_PREFIX_LENGHT = URL_PREFIX.length();
	private static final String SEPARATOR = "!";
	private static final int SEPARATOR_LENGHT = SEPARATOR.length();
	
	private final ReferenceMap urlStreams;

	protected void parseURL(URL u, String spec, int start, int limit) {
		String quotedSpec = "\"" + spec + "\"";
		if (!spec.startsWith(URL_PREFIX)) {
			throw new JSException("jsexception.jar.malformed.url", new Object[] {quotedSpec});
		}
		int sep = spec.indexOf(SEPARATOR);
		if (sep < 0) {
			throw new JSException("jsexception.jar.malformed.url", new Object[] {quotedSpec});
		}
		String jar = spec.substring(URL_PREFIX_LENGHT, sep);
		String res = spec.substring(sep + SEPARATOR_LENGHT, spec.length());
		setURL(u, PROTOCOL, null, -1, null, null, jar, null, res);
	}

	public JarURLStreamHandler() {
		urlStreams = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);
	}

	protected URLConnection openConnection(URL u) throws IOException {
		JarFileEntry entry = getURLEntry(u);
		if (entry == null) {
			throw new JSException("jsexception.jar.entry.not.found", new Object[] {u});
		}
		return new JarConnection(u, entry);
	}

	public URL createURL(JarFileEntry jarEntry) {
		try {
			String spec = URL_PREFIX + jarEntry.getJarFile().getName()
					+ SEPARATOR + jarEntry.getEntry().getName();
			URL url = new URL(null, spec, this);
			putURL(url, jarEntry);
			return url;
		} catch (MalformedURLException e) {
			throw new JSExceptionWrapper(e);
		}
	}
	
	public synchronized void putURL(URL u, JarFileEntry jarEntry) {
		urlStreams.put(u, jarEntry);
	}

	protected synchronized JarFileEntry getURLEntry(URL u) {
		return (JarFileEntry) urlStreams.get(u);
	}

	protected boolean equals(URL u1, URL u2) {
		return u1 == u2;
	}

	protected int hashCode(URL u) {
		return System.identityHashCode(u);
	}

	protected String toExternalForm(URL u) {
		return URL_PREFIX + u.getPath() + SEPARATOR + u.getRef();
	}
}
