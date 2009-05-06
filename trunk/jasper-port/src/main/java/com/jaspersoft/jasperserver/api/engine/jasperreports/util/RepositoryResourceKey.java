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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryResourceKey.java 8408 2007-05-29 23:29:12Z melih $
 */
public class RepositoryResourceKey {
	private final String uri;
	private final int version;
	private final Date creationDate;
	private final int hash;
	
	public RepositoryResourceKey(String uri, int version, Date creationDate) {
		this.uri = uri;
		this.version = version;
		this.creationDate = creationDate;
		
		this.hash = new HashCodeBuilder().append(this.uri).append(this.version).append(this.creationDate).toHashCode();
	}
	
	public RepositoryResourceKey(Resource res) {
		this(res.getURIString(), res.getVersion(), res.getCreationDate());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RepositoryResourceKey)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		RepositoryResourceKey key = (RepositoryResourceKey) obj;
		if (hash != key.hash) {
			return false;
		}
		return new EqualsBuilder().append(uri, key.uri).append(version, key.version).append(creationDate, key.creationDate).isEquals();
	}

	public int hashCode() {
		return hash;
	}

	public String getUri() {
		return uri;
	}

	public int getVersion() {
		return version;
	}
	
	public Date getCreatDate() {
		return creationDate;
	}
}
