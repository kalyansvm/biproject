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

import java.util.Date;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.RepoManager;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepoResourceBase.java 11000 2007-12-03 17:37:40Z lucian $
 */
public abstract class RepoResourceBase implements IdedObject {
	
	protected long id;
	
	protected int version;
	
	protected Date creationDate;

	protected String name = null;
	protected String label = null;
	protected String description = null;
	
	protected RepoFolder parent;

	protected RepoResourceBase() {
		version = Resource.VERSION_NEW;
	}
	
	/**
	 * @return
	 * @hibernate.id generator-class="identity"
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @hibernate.version column="version" unsaved-value="negative"
	 */
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @hibernate.property
	 * 		column="name" type="string" length="100" not-null="true"
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @hibernate.property
	 * 		column="label" type="string" length="100" not-null="true"
	 * 
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * 
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @hibernate.property
	 * 		column="description" type="string" length="100"
	 * 
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	

	/**
	 * @hibernate.many-to-one
	 * 		column="parent_folder"
	 */
	public RepoFolder getParent() {
		return parent;
	}

	public void setParent(RepoFolder parent) {
		this.parent = parent;
	}

	protected abstract Class getClientItf();
	
	public final Class getClientType() {
		return getClientItf();
	}

	public Object toClient(ResourceFactory resourceFactory) {
		Class clientItf = getClientItf();
		//TODO context?
		Resource clientRes = resourceFactory.newResource(null, clientItf);
		return clientRes;
	}
	
	public void copyFromClient(Object objIdent, PersistentObjectResolver resolver){
		copyFrom((Resource) objIdent);
	}

	protected void copyFrom(Resource clientRes)
	{
		if (!isNew() && getVersion() != clientRes.getVersion()) {
			throw new JSException("jsexception.resource.no.match.versions", new Object[] {getResourceURI(), new Integer(clientRes.getVersion()), new Integer(getVersion())});
		}

		setName(clientRes.getName());
		setLabel(clientRes.getLabel());
		setDescription(clientRes.getDescription());
	}

	protected void copyTo(Resource clientRes)
	{
		clientRes.setVersion(getVersion());
		clientRes.setCreationDate(getCreationDate());
		clientRes.setName(getName());
		clientRes.setLabel(getLabel());
		clientRes.setDescription(getDescription());
		
		RepoFolder parentFolder = getParent();
		if (parentFolder != null) {
			clientRes.setParentFolder(parentFolder.getURI());
		}
	}

	public String getResourceURI() {
		RepoFolder parentFolder = getParent();
		String uri;
		if (parentFolder == null || parentFolder.isRoot()) {
			uri = Folder.SEPARATOR + getName();
		} else {
			uri = parentFolder.getURI() + Folder.SEPARATOR + getName();
		}
		return uri;
	}
	
	public boolean isNew() {
		return getVersion() == Resource.VERSION_NEW;
	}

	
	/**
	 * @hibernate.property
	 * 		column="creation_date" type="timestamp" not-null="true"
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public abstract void moveTo(RepoFolder parent, RepoManager repoManager);

	protected abstract void moved(String oldBaseURI, String newBaseURI, RepoManager repoManager);
	
}
