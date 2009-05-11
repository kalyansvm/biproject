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
 *
 * ResourceProperty.java
 *
 * Created on October 23, 2006, 12:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.jaspersoft.jasperserver.api.metadata.xml.domain.impl;

/**
 *
 * @author gtoffoli
 */
public class ResourceProperty {
    
    private String name = "";
    private String value = "";
    
    private java.util.List properties = new java.util.ArrayList();
    
    /** Creates a new instance of ResourceProperty */
    public ResourceProperty(String name) {
        this(name, null);
    }
    
    /** Creates a new instance of ResourceProperty */
    public ResourceProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public java.util.List getProperties() {
        return properties;
    }

    public void setProperties(java.util.List properties) {
        this.properties = properties;
    }
    
}
