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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryConnection.java 10517 2007-10-19 13:33:28Z lucian $
 */
public class RepositoryConnection extends URLConnection
{
	private static final Log log = LogFactory.getLog(RepositoryConnection.class);
	
	private final RepositoryContext repositoryContext;
	
	public RepositoryConnection(RepositoryContext repository, URL url)
	{
		super(url);

		this.repositoryContext = repository;
	}

	public void connect() throws IOException
	{
		connected = true;
	}

    public InputStream getInputStream() throws IOException
    {
    	try {
        	InputStream data = null;
			String path = url.getPath();
    		if (!path.startsWith(Folder.SEPARATOR)) {
				ReportUnit reportUnit = repositoryContext.getReportUnit();
				FileResource resource = null;
				if (reportUnit != null) {
					resource = reportUnit.getResourceLocal(path);
				}

				if (resource == null) {
					path = repositoryContext.getContextURI() + Folder.SEPARATOR + path;
				} else {
					if (resource.isReference()) {
						path = resource.getReferenceURI();
					}
					else if (resource.hasData()) {
						if (log.isDebugEnabled()) {
							log.debug("Loading resource \"" + resource.getName() + "\" from in-memory report unit");
						}
						data = resource.getDataStream();
						if (resource.getFileType().equals(FileResource.TYPE_JRXML)) {
							data = repositoryContext.getCompiledReportProvider().getCompiledReport(
									repositoryContext.getExecutionContext(),
									data);
						}
					} else {
						path = resource.getURIString();
					}
				}
    		}

    		if (data == null) {
				if (log.isDebugEnabled()) {
					log.debug("Loading resource \"" + path + "\" from repository");
				}
				
				RepositoryService repository = repositoryContext.getRepository();
				FileResource resource = (FileResource) repository.getResource(repositoryContext.getExecutionContext(), path);
				while (resource != null && resource.isReference()) {
					resource = (FileResource) repository.getResource(repositoryContext.getExecutionContext(), resource.getReferenceURI());
				}
				
				if (resource == null) {
					throw new IOException("Repository file resource " + path + " could not be loaded");
				}
				
				if (resource.getFileType().equals(FileResource.TYPE_JRXML)) {
					data = repositoryContext.getCompiledReportProvider().getCompiledReport(
							repositoryContext.getExecutionContext(),
							path);
				} else {
					FileResourceData resourceData = repository.getResourceData(repositoryContext.getExecutionContext(), path);
					data = resourceData.getDataStream();
				}
			}

    		return data;
		} catch (JSResourceNotFoundException e) {
			throw new IOException(e.getMessage());
		}
    }
}
