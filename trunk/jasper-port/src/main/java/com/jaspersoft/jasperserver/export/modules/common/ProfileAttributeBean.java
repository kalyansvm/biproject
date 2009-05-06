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

package com.jaspersoft.jasperserver.export.modules.common;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ProfileAttributeBean.java 9458 2007-08-10 14:30:48Z lucian $
 */
public class ProfileAttributeBean {

	private String name;
	private String value;
	
	public void copyFrom(ProfileAttribute attribute) {
		setName(attribute.getAttrName());
		setValue(attribute.getAttrValue());
	}
	
	public void copyTo(ProfileAttribute attribute) {
		attribute.setAttrName(getName());
		attribute.setAttrValue(getValue());
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
