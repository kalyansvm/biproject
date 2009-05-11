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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: NullValue.java 8492 2007-06-01 09:11:39Z lucian $
 */
public final class NullValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final NullValue singleton = new NullValue();
	
	private NullValue() {
	}
	
	public static NullValue instance() {
		return singleton;
	}
	
	public static boolean isNullValue(Object o) {
		return o instanceof NullValue;
	}

	public static Map replaceWithNullValues(Map map) {
		Map replaced;
		if (map == null) {
			replaced = null;
		} else {
			replaced = new HashMap(map);
			for (Iterator it = replaced.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				if (entry.getValue() == null) {
					entry.setValue(singleton);
				}
			}
		}
		return replaced;
	}

	public static Map restoreNulls(Map map) {
		Map replaced;
		if (map == null) {
			replaced = null;
		} else {
			replaced = new HashMap(map);
			for (Iterator it = replaced.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				if (isNullValue(entry.getValue())) {
					entry.setValue(null);
				}
			}
		}
		return replaced;
	}
	
	public String toString() {
		return "<null value>";
	}
	
}
