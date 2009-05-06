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
package com.jaspersoft.jasperserver.api.common.service;

import java.util.List;
import java.util.Map;

/**
 * @author swood
 *
 */
public interface ObjectFactory {

	public Class getImplementationClass(Map classMappings, Class itfClass);

	public Class getImplementationClass(Map classMappings, String id);
	
	public String getImplementationClassName(Map classMappings, Class itfClass);

	public String getImplementationClassName(Map classMappings, String id);

	public Class getInterface(Map classMappings, Class _class);

	public String getIdForClass(Map classMappings, Class _class);

	public String getInterfaceName(Map classMappings, Class _class);

	public Object newObject(Map classMappings, Class itfClass);

	public Object newObject(Map classMappings, String id);
	
	public List getKeys(Map classMappings);
	
}
