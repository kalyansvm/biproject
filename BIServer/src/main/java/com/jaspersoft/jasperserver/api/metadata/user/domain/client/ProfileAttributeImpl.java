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

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import java.io.Serializable;

/**
 * @author sbirney
 */
public class ProfileAttributeImpl implements ProfileAttribute, Serializable {

    private String attrName;
    private String attrValue;
    private Object principal;
	
    public String getAttrName() {
	return attrName;
    }
    public void setAttrName(String s) {
	this.attrName = s;
    }
	
    public String getAttrValue() {
	return attrValue;
    }
    public void setAttrValue(String s) {
	this.attrValue = s;
    }
	
    public Object getPrincipal() {
	return principal;
    }
    public void setPrincipal(Object o) {
	this.principal = o;
    }

    public String toString() {
	return new ToStringBuilder(this)
	    .append("attrName", getAttrName())
	    .append("attrValue", getAttrValue())
	    .append("principal", getPrincipal())
	    .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof ProfileAttributeImpl) ) return false;
        ProfileAttributeImpl castOther = (ProfileAttributeImpl) other;
        return new EqualsBuilder()
            .append(this.getAttrName(), castOther.getAttrName())
            .append(this.getPrincipal(), castOther.getPrincipal())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getAttrName())
            .append(getPrincipal())
            .toHashCode();
    }

}
