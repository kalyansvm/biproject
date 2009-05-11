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

package com.jaspersoft.jasperserver.api.common.service.impl;

import java.util.Map;

import com.jaspersoft.jasperserver.api.common.service.ClassMappingsObjectFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SimpleClassMappObjectFactory.java 12519 2008-03-17 11:41:38Z lucian $
 */
public class SimpleClassMappObjectFactory extends BaseClassMappings 
		implements ClassMappingsObjectFactory {

	private Map mappings;
	
	public Object getClassObject(String type) {
		return getClassMapping(mappings, type);
	}

	public Map getMappings() {
		return mappings;
	}

	public void setMappings(Map mappings) {
		this.mappings = mappings;
	}

}
