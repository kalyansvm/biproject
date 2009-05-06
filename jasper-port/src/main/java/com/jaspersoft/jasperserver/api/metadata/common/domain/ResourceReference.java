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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.io.Serializable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceReference.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ResourceReference implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean local;
	private String referenceURI;
	private ResourceLookup referenceLookup;
	private Resource localResource;

	public ResourceReference(String referenceURI) {
		setReference(referenceURI);
	}

	public ResourceReference(ResourceLookup referenceLookup) {
		setReference(referenceLookup);
	}
	
	public ResourceReference(Resource localResource) {
		setLocalResource(localResource);
	}
	
	public boolean isLocal() {
		return local;
	}

	public Resource getLocalResource() {
		return localResource;
	}

	public String getReferenceURI() {
		return referenceURI;
	}

	public ResourceLookup getReferenceLookup() {
		return referenceLookup;
	}

	public void setLocalResource(Resource localResource) {
		this.local = true;
		this.referenceURI = null;
		this.referenceLookup = null;
		this.localResource = localResource;
	}

	public void setReference(String referenceURI) {
		this.local = false;
		this.referenceURI = referenceURI;
		this.referenceLookup = null;
		this.localResource = null;
	}

	public void setReference(ResourceLookup referenceLookup) {
		this.local = false;
		this.referenceURI = referenceLookup.getURIString();
		this.referenceLookup = referenceLookup;
		this.localResource = null;
	}

}
