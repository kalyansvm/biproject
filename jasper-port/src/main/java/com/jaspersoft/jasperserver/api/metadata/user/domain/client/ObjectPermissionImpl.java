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
package com.jaspersoft.jasperserver.api.metadata.user.domain.client;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;

/**
 * @author swood
 *
 * @hibernate.class table="ObjectPermission"
 */
public class ObjectPermissionImpl implements ObjectPermission {

	private String uri;
	private Object permissionRecipient;
	private int permissionMask;

	/**
	 *  (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#getURI()
	 */
	public String getURI() {
		return uri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#setObjectIdentity(java.lang.String)
	 */
	public void setURI(String URI) {
		this.uri = URI;
	}

	/**
	 *  (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#getPermissionRecipient()
	 */
	public Object getPermissionRecipient() {
		return permissionRecipient;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#setPermissionRecipient(java.lang.String)
	 */
	public void setPermissionRecipient(Object permissionRecipient) {
		this.permissionRecipient = permissionRecipient; 
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#getPermissionMask()
	 */
	public int getPermissionMask() {
		return permissionMask;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#setPermissionMask(java.lang.String)
	 */
	public void setPermissionMask(int newPermissionMask) {
		this.permissionMask = newPermissionMask; 
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("URI", getURI())
			.append("permissionRecipient", getPermissionRecipient())
			.toString();
	}

    public boolean equals(Object other) {
        if ( !(other instanceof ObjectPermissionImpl) ) return false;
        ObjectPermissionImpl castOther = (ObjectPermissionImpl) other;
        return new EqualsBuilder()
			.append(getURI(), castOther.getURI())
			.append(getPermissionRecipient(), castOther.getPermissionRecipient())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
			.append(getURI())
			.append(getPermissionRecipient())
            .toHashCode();
    }

}
