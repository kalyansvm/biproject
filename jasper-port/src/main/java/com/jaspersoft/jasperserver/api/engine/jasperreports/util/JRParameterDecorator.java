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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.JSException;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRParameterDecorator.java 11039 2007-12-04 16:16:58Z lucian $
 */
public class JRParameterDecorator implements JRParameter {

	private final JRParameter decorated;
	
	public JRParameterDecorator(JRParameter decorated) {
		this.decorated = decorated;
	}
	
	public JRParameter getDecorated() {
		return decorated;
	}

	public JRExpression getDefaultValueExpression() {
		return decorated.getDefaultValueExpression();
	}

	public String getDescription() {
		return decorated.getDescription();
	}

	public String getName() {
		return decorated.getName();
	}

	public Class getValueClass() {
		return decorated.getValueClass();
	}

	public String getValueClassName() {
		return decorated.getValueClassName();
	}

	public boolean isForPrompting() {
		return decorated.isForPrompting();
	}

	public boolean isSystemDefined() {
		return decorated.isSystemDefined();
	}

	public void setDescription(String description) {
		decorated.setDescription(description);
	}

	public JRPropertiesMap getPropertiesMap() {
		return decorated.getPropertiesMap();
	}

	public JRPropertiesHolder getParentProperties() {
		return decorated.getParentProperties();
	}

	public boolean hasProperties() {
		return decorated.hasProperties();
	}
	
	public Object clone() {
		throw new JSException("Clone not supported");
	}

}
