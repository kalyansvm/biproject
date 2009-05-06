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
 *
 * InputControlQueryDataRow.java
 *
 * Created on August 23, 2006, 2:00 PM
 *
 * Class to store all values in a row for query based input controls
 */

package com.jaspersoft.jasperserver.api.metadata.xml.domain.impl;

/**
 *
 * @author gtoffoli
 */
public class InputControlQueryDataRow {
    
    private Object value = null;
    private java.util.List columnValues = null;
    
    /** Creates a new instance of InputControlQueryDataRow */
    public InputControlQueryDataRow() {
        columnValues = new java.util.ArrayList();
    }

    public java.util.List getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(java.util.List columnValues) {
        this.columnValues = columnValues;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
}
