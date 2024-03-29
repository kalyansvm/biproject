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
package com.jaspersoft.jasperserver.api.metadata.xml.domain.impl;

/**
 * It's an implementation of ListOfValuesItem used in Webservices.
 * This class does not implements directly ListOfValuesItem to avoid
 * the need of this interface on client side.
 *
 * @author gtoffoli
 */
public class ListItem {
    
    private Object value;
    private String label;
    private boolean isListItem = false;
    
    /** Creates a new instance of ListItem */
    public ListItem() {
    }
    
    /** Creates a new instance of ListItem */
    public ListItem(String label, Object value) {
        this.value = value;
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isIsListItem() {
        return isListItem;
    }

    public void setIsListItem(boolean isListItem) {
        this.isListItem = isListItem;
    }
    
}
