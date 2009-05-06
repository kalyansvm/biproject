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

import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.repo.RepositoryURLHandlerFactory;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryResourceClassLoader.java 8408 2007-05-29 23:29:12Z melih $
 */
public class RepositoryResourceClassLoader extends ClassLoader {

	private final Map resourceKeys;
	private final boolean localURLs;

	public RepositoryResourceClassLoader(ClassLoader parent, Map resourceKeys, boolean localURLs) {
		super(parent);
		
		this.resourceKeys = resourceKeys;
		this.localURLs = localURLs;
	}

	protected URL findResource(String name) {
		return getRepositoryURL(name);
	}

	protected URL getRepositoryURL(String name) {
		URL url = null;
		RepositoryResourceKey resourceKey = (RepositoryResourceKey) resourceKeys.get(name);
		if (resourceKey != null) {
			if (localURLs) {
				url = RepositoryURLHandlerFactory.createRepoURL(name);
			} else {
				url = RepositoryURLHandlerFactory.createRepoURL(resourceKey.getUri());
			}
		}
		return url;
	}

	protected Enumeration findResources(String name) {
		final URL url = getRepositoryURL(name);
		return new Enumeration() {
			private Object obj = url;

			public boolean hasMoreElements() {
				return obj != null;
			}

			public Object nextElement() {
				Object next = obj;
				obj = null;
				return next;
			}
		};
	}
}
