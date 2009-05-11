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

package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DefaultContentResourceURIResolver.java 13166 2008-04-24 09:57:31Z lucian $
 */
public class DefaultContentResourceURIResolver implements
		ContentResourceURIResolver, InitializingBean {

	private WebDeploymentInformation deploymentInformation;
	private String contentControllerPath;
	private URI baseURI;

	public void afterPropertiesSet() throws Exception {
		baseURI = new URI(deploymentInformation.getDeploymentURI() + contentControllerPath);
	}
	
	public String resolveURI(String repositoryPath) {
		String uriPath = baseURI.getPath() + repositoryPath;
		try {
			URI uri = new URI(baseURI.getScheme(), baseURI.getUserInfo(), baseURI.getHost(), baseURI.getPort(), 
					uriPath, baseURI.getQuery(), baseURI.getFragment());
			return uri.toASCIIString();
		} catch (URISyntaxException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	public WebDeploymentInformation getDeploymentInformation() {
		return deploymentInformation;
	}

	public void setDeploymentInformation(
			WebDeploymentInformation deploymentInformation) {
		this.deploymentInformation = deploymentInformation;
	}

	public String getContentControllerPath() {
		return contentControllerPath;
	}

	public void setContentControllerPath(String contentControllerPath) {
		this.contentControllerPath = contentControllerPath;
	}

}
