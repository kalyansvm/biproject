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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseClassMappings.java 12519 2008-03-17 11:41:38Z lucian $
 */
public abstract class BaseClassMappings {
	
	protected final Comparator itfComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			Class itf1 = (Class) o1;
			Class itf2 = (Class) o2;
			
			if (itf1.equals(itf2)) {
				return 0;
			} else if (itf2.isAssignableFrom(itf1)) {
				return -1;
			} else if (itf1.isAssignableFrom(itf2)) {
				return 1;
			} else {
				return itf1.getName().compareTo(itf2.getName());
			}
		}
	};

	protected final Object getClassMapping(Map classMappings, Class itfClass) {
		if (classMappings == null) {
			return null;
		}
		
		//TODO cache
		SortedSet interfaces = new TreeSet(itfComparator);

		for (Iterator it = classMappings.keySet().iterator(); it.hasNext();) {
			String itfName = (String) it.next();
			Class itf = resolveClass(itfName);
			if (itf.isAssignableFrom(itfClass)) {
				interfaces.add(itf);
			}
		}

		Object mapping;
		if (interfaces.isEmpty()) {
			mapping = null;
		} else {
			Class itf = (Class) interfaces.iterator().next();
			mapping = classMappings.get(itf.getName());
		}
		return mapping;
	}

	protected Class resolveClass(String name) {
		try {
			return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	protected final Object getClassMapping(Map classMappings, String itfClassName) {
		return getClassMapping(classMappings, resolveClass(itfClassName));
	}
}
