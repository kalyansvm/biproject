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

package com.jaspersoft.jasperserver.api.common.util;

import java.util.Map;

/**
 * @author Lucian Chirita
 *
 */
public class MapEntry implements Map.Entry {

	private final Object key;
	private final Object value;
	
	public MapEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public Object setValue(Object value) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return key + "=" + value;
	}
	
	public int hashCode() {
		int hash = 17;
		if (key != null) {
			hash += key.hashCode();
		}
		hash *= 37;
		if (value != null) {
			hash += value.hashCode();
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Map.Entry)) {
			return false;
		}
		if (this == o) {
			return true;
		}
		Map.Entry entry = (Map.Entry) o;
		return
			(key == null ? entry.getKey() == null : key.equals(entry.getKey()))
			&& (value == null ? entry.getValue() == null : value.equals(entry.getValue()));
	}
}
