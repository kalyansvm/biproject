/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.war.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.intercept.ObjectDefinitionSource;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FlowDefinitionSource.java 8408 2007-05-29 23:29:12Z melih $
 */
public class FlowDefinitionSource implements ObjectDefinitionSource {

	protected static final String DEFAULT_FLOW = "*";
	
	private Map flowAttributes;
	
	public FlowDefinitionSource() {
		flowAttributes = new HashMap();
	}
	
	public void addFlow(String flowId, ConfigAttributeDefinition attributes) {
		flowAttributes.put(flowId, attributes);
	}
	
	public ConfigAttributeDefinition getAttributes(Object object) throws IllegalArgumentException {
		ConfigAttributeDefinition attributes = (ConfigAttributeDefinition) flowAttributes.get(object);
		if (attributes == null) {
			attributes = (ConfigAttributeDefinition) flowAttributes.get(DEFAULT_FLOW);
		}
		return attributes;
	}

	public Iterator getConfigAttributeDefinitions() {
		return flowAttributes.values().iterator();
	}

	public boolean supports(Class clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
	}

}
