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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import org.springframework.beans.factory.FactoryBean;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita
 *
 */
public class SimplePropertyFactoryBean implements FactoryBean{

	private Class objectType;
	private String value;
	
	public boolean isSingleton() {
		return true;
	}

	public Object getObject() {
		PropertyEditor editor = PropertyEditorManager.findEditor(objectType);
		if (editor == null) {
			throw new JSException("No property editor was found for class " + objectType.getName());
		}
		editor.setAsText(getValue());
		return editor.getValue();
	}

	public Class getObjectType() {
		return objectType;
	}

	public void setObjectType(Class objectType) {
		this.objectType = objectType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
