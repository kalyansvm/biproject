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

import java.sql.Blob;
import java.util.Date;
import java.util.Set;

import org.hibernate.Hibernate;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CachedItem.java 12706 2008-03-27 15:13:42Z lucian $
 * 
 * @hibernate.class table="repository_cache"
 */
public class CachedItem {
	private long id;
	private String uri;
	private String cacheName;
	private int version;
	private Blob data;
	private CachedItem reference;
	private Date versionDate;
	private Set referrers;
	
	/**
	 * @hibernate.property column="data" type="blob"
	 */
	public Blob getData() {
		return data;
	}
	
	public void setData(Blob data) {
		this.data = data;
	}
	
	public byte[] getDataBytes() {
		return StreamUtils.readData(getData());
	}

	public void setDataBytes(byte[] bytes) {
		Blob blob = Hibernate.createBlob(bytes);
		setData(blob);
	}
	
	/**
	 * @hibernate.id generator-class="identity"
	 */
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @hibernate.property column="uri" type="string" length="200" not-null="true"
	 */
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * @hibernate.property column="version" not-null="true"
	 */
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	/**
	 * @hibernate.property column="cache_name" type="string" length="20" not-null="true"
	 */
	public String getCacheName() {
		return cacheName;
	}
	
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	/**
	 * @hibernate.many-to-one column="reference"
	 */
	public CachedItem getReference() {
		return reference;
	}
	
	public void setReference(CachedItem reference) {
		this.reference = reference;
	}

	public boolean isItemReference() {
		return getReference() != null;
	}

	/**
	 * @hibernate.property
	 * 		column="version_date" type="timestamp" not-null="true"

	 * @return Returns the versionDate.
	 */
	public Date getVersionDate() {
		return versionDate;
	}

	/**
	 * @param versionDate The versionDate to set.
	 */
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public Set getReferrers() {
		return referrers;
	}

	public void setReferrers(Set referrers) {
		this.referrers = referrers;
	}
}
