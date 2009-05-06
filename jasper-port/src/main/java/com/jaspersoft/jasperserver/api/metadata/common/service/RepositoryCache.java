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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import java.io.InputStream;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryCache.java 12981 2008-04-14 15:03:59Z lucian $
 */
public interface RepositoryCache {

	InputStream cache(ExecutionContext context, FileResource resource, RepositoryCacheableItem cacheableItem);

	InputStream cache(ExecutionContext context, String uri, RepositoryCacheableItem cacheableItem);
	
	void clearCache(String uri, RepositoryCacheableItem cacheableItem);
	
	void clearCache(RepositoryCacheableItem cacheableItem);

}
