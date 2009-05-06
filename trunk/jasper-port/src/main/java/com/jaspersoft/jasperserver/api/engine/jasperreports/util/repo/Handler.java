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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util.repo;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryUtil;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: Handler.java 8408 2007-05-29 23:29:12Z melih $
 */
public class Handler extends URLStreamHandler
{
	public final static String REPOSITORY_PROTOCOL = "repo";
	public final static String URL_PROTOCOL_PREFIX = REPOSITORY_PROTOCOL + ':';
	
	public Handler()
	{
		super();
	}
	
	protected void parseURL(URL u, String spec, int start, int limit)
	{
		spec = spec.trim();
		
		String protocol = null;
		String path;
		if (spec.startsWith(URL_PROTOCOL_PREFIX))
		{
			protocol = REPOSITORY_PROTOCOL;
			path = spec.substring(URL_PROTOCOL_PREFIX.length());
		}
		else
		{
			path = spec;
		}

		setURL(u, protocol, null, -1, null, null, path, null, null);
	}

	protected URLConnection openConnection(URL url) throws IOException
	{
		return new RepositoryConnection(RepositoryUtil.getThreadRepositoryContext(), url);
	}
}
