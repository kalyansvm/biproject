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
package com.jaspersoft.jasperserver.war.dto;

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceReferenceDTO.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ResourceReferenceDTO implements Serializable {

	private String source;
	private Resource localResource;
	private String referenceURI;
	
	public ResourceReferenceDTO() {
		this(null);
	}
	
	public ResourceReferenceDTO(ResourceReference reference) {
		if (reference == null) {
			source = JasperServerConst.FIELD_CHOICE_NONE;
		} else if (reference.isLocal()) {
			source = JasperServerConst.FIELD_CHOICE_LOCAL;
			localResource = (Query) reference.getLocalResource();
		} else {
			source = JasperServerConst.FIELD_CHOICE_CONT_REPO;
			referenceURI = reference.getReferenceURI();
		}
	}
	
	/**
	 * @return Returns the referenceURI.
	 */
	public String getReferenceURI() {
		return referenceURI;
	}
	
	/**
	 * @param referenceURI The referenceURI to set.
	 */
	public void setReferenceURI(String referenceURI) {
		this.referenceURI = referenceURI;
	}
	
	/**
	 * @return Returns the source.
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * @param source The source to set.
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * @return Returns the localResource.
	 */
	public Resource getLocalResource() {
		return localResource;
	}
	/**
	 * @param localResource The localResource to set.
	 */
	public void setLocalResource(Resource localResource) {
		this.localResource = localResource;
	}
	
	public ResourceReference toResourceReference() {
		ResourceReference ref;
		if (getSource().equals(JasperServerConst.FIELD_CHOICE_NONE)) {
			ref = null;
		} else if (getSource().equals(JasperServerConst.FIELD_CHOICE_LOCAL)) {
			ref = new ResourceReference(getLocalResource());
		} else if (getSource().equals(JasperServerConst.FIELD_CHOICE_CONT_REPO)) {
			ref = new ResourceReference(getReferenceURI());
		} else {
			String quotedSource = "\"" + getSource() + "\"";
			throw new JSException("jsexception.invalid.resource.reference.source", new Object[] {quotedSource});
		}
		return ref;
	}
}
