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

/**
 * 
 * Maps from interfaces to implementations and back
 * 
 * @author swood
 *
 */
public interface ImplementationObjectFactory {
	public Class getImplementationClass(Class _class);
	public Class getImplementationClass(String id);
	public String getImplementationClassName(Class _class);
	public String getImplementationClassName(String id);
	public Class getInterface(Class _class);
	public String getInterfaceName(Class _class);
	public String getIdForClass(Class _class);
	public Object newObject(Class _class);
	public Object newObject(String id);
	public List getKeys();
}
