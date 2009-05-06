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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: MapNameMethodAttributes.java 13093 2008-04-21 13:56:46Z lucian $
 */
public class MapNameMethodAttributes implements ReattemptAttributes {

	private Map methodAttributes;
	
	public ReattemptMethodAttributes getMethodAttributes(Method method) {
		String name = method.getName();
		return (ReattemptMethodAttributes) methodAttributes.get(name);
	}
	
	public Map getMethodAttributes() {
		return methodAttributes;
	}

	public void setMethodAttributes(Map methodAttributes) {
		this.methodAttributes = methodAttributes;
	}

}
