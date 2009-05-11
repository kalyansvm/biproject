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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.RepoManager;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoResource.java 11251 2007-12-14 17:50:43Z lucian $
 * 
 * @disabled_hibernate.class table="Resource" abstract="true"
 */
public abstract class RepoResource extends RepoResourceBase
{
	private static final Log log = LogFactory.getLog(RepoResource.class);
	
	public final static String CLIENT_OPTION_FULL_DATA = "fullData";
	public final static String CLIENT_OPTION_AS_NEW = "asNew";
	
	private static final ThreadLocal clientOptions = new ThreadLocal();
	
	private RepoFolder childrenFolder;

	private Set newChildren;
	
	protected RepoResource() {
	}

	protected final ResourceReference getClientReference(RepoResource reference, ResourceFactory resourceFactory) {
		ResourceReference clientRef;
		if (reference == null) {
			clientRef = null;
		} else if (getChildrenFolder() != null && getChildrenFolder().equals(reference.getParent()) ) {
			//local resource
			Resource clientRes = (Resource) reference.toClient(resourceFactory);
			clientRef = new ResourceReference(clientRes);
		} else {
			ResourceLookup clientLookup = reference.toClientLookup();
			clientRef = new ResourceReference(clientLookup);
		}
		return clientRef;
	}

	public Object toClient(ResourceFactory resourceFactory) {
		return toClient(resourceFactory, null);
	}
	
	public Object toClient(ResourceFactory resourceFactory, Map options) {
		if (options != null) {
			clientOptions.set(options);
		}
		try {
			Resource clientRes = (Resource) super.toClient(resourceFactory);
			copyTo(clientRes, resourceFactory);
			return clientRes;
		} finally {
			if (options != null) {
				clientOptions.remove();
			}
		}
	}

	protected Map getClientOptions() {
		return (Map) clientOptions.get();
	}
	
	protected boolean hasClientOption(String option) {
		Map options = getClientOptions();
		return options != null && options.containsKey(option);
	}
	
	public ResourceLookup toClientLookup() {
		Class clientItf = getClientItf();
		//TODO context?
		ResourceLookup clientRes = new ResourceLookupImpl();
		clientRes.setResourceType(clientItf.getName());
		super.copyTo(clientRes);
		return clientRes;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes);
		
		if (hasClientOption(CLIENT_OPTION_AS_NEW)) {
			clientRes.setVersion(Resource.VERSION_NEW);
		}
	}

	public void copyFromClient(Resource clientRes, ReferenceResolver referenceResolver)
	{
		initNewChildren();
		
		copyFrom(clientRes, referenceResolver);
		
		filterChildren();
	}
	
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes);
	}

	protected void initNewChildren() {
		newChildren = null;
	}

	public void addNewChild(RepoResource resource) {
		RepoFolder local = getChildrenFolder();

		resource.setParent(local);
		local.addChild(resource);

		if (newChildren == null) {
			newChildren = new HashSet();
		}
		newChildren.add(resource);
	}

	protected void filterChildren() {
		RepoFolder children = getChildrenFolder();
		if (children != null) {
			children.filterChildren(newChildren);
		}
	}


	/**
	 * @hibernate.many-to-one
	 * 		column="childrenFolder" cascade="save-update,delete" unique="true"
	 */
	public RepoFolder getChildrenFolder() {
		return childrenFolder;
	}

	public void setChildrenFolder(RepoFolder childrenFolder) {
		this.childrenFolder = childrenFolder;
	}

	protected RepoResource getReference(ResourceReference resourceRef, Class persistentClass, ReferenceResolver referenceResolver) {
		return referenceResolver.getReference(this, resourceRef, persistentClass);
	}

	protected RepoResource getReference(Resource resource, Class persistentClass, ReferenceResolver referenceResolver) {
		return referenceResolver.getReference(this, resource, persistentClass);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RepoResource)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (isNew()) {
			return super.equals(obj);
		}
		RepoResource res = (RepoResource) obj;
		return new EqualsBuilder().append(getId(), res.getId()).isEquals();
	}

	public int hashCode() {
		if (isNew()) {
			return super.hashCode();
		}
		return new HashCodeBuilder().append(getId()).toHashCode();
	}

	public String toString() {
		return getResourceURI();
	}
	
	public void moveTo(RepoFolder parent, RepoManager repoManager) {
		String oldParentURI = getParent().getResourceURI();
		setParent(parent);
		repoManager.update(this);
		
		RepoFolder resourcesFolder = getChildrenFolder();
		if (resourcesFolder != null) {
			resourcesFolder.moveTo(parent, repoManager);
		}
		
		moved(oldParentURI, parent.getResourceURI(), repoManager);
	}
	
	protected void moved(String oldBaseURI, String newBaseURI, RepoManager repoManager) {
		repoManager.resourceMoved(this, oldBaseURI, newBaseURI);
	}
}
