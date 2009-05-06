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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryURLHandlerFactory.java 8408 2007-05-29 23:29:12Z melih $
 */
public class RepositoryURLHandlerFactory implements URLStreamHandlerFactory
{
	protected static final Log log = LogFactory.getLog(RepositoryURLHandlerFactory.class);
	
	private final static RepositoryURLHandlerFactory instance = new RepositoryURLHandlerFactory();
	
	public static RepositoryURLHandlerFactory getInstance()
	{
		return instance;
	}
	
	private final Handler repositoryHandler;
	
	protected RepositoryURLHandlerFactory()
	{
		repositoryHandler = new Handler();
	}
	
	public URLStreamHandler createURLStreamHandler(String protocol)
	{
		if (protocol.equals(Handler.REPOSITORY_PROTOCOL))
		{
			return repositoryHandler;
		}

		return null;
	}
	
	public Handler getRepoHandler() {
		return repositoryHandler;
	}

	public static URL createRepoURL(String uri) {
		try {
			return new URL(null, Handler.URL_PROTOCOL_PREFIX + uri, getInstance().getRepoHandler());
		} catch (MalformedURLException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}
}
