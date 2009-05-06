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
 * @author tkavanagh
 * @version $Id: Argument.java 4307 2006-08-24 08:13:55Z giulio $
 */

public class Argument {

    public static final String MODIFY_REPORTUNIT = "MODIFY_REPORTUNIT_URI";
    public static final String CREATE_REPORTUNIT = "CREATE_REPORTUNIT_BOOLEAN";
    public static final String LIST_DATASOURCES  = "LIST_DATASOURCES";
    public static final String IC_GET_QUERY_DATA  = "IC_GET_QUERY_DATA";
    
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    
    public static final String RUN_OUTPUT_FORMAT = "RUN_OUTPUT_FORMAT";
    public static final String RUN_OUTPUT_FORMAT_PDF = "PDF";
    public static final String RUN_OUTPUT_FORMAT_JRPRINT = "JRPRINT";
    public static final String RUN_OUTPUT_FORMAT_HTML = "HTML";
    public static final String RUN_OUTPUT_FORMAT_XLS = "XLS";
    public static final String RUN_OUTPUT_FORMAT_XML = "XML";
    public static final String RUN_OUTPUT_FORMAT_CSV = "CSV";
    public static final String RUN_OUTPUT_FORMAT_RTF = "RTF";
    
    public static final String RUN_OUTPUT_IMAGES_URI = "IMAGES_URI";
            
    public static final String RUN_OUTPUT_PAGE = "PAGE";
    
    public static final String LIST_RESOURCES = "LIST_RESOURCES";
    public static final String RESOURCE_TYPE = "RESOURCE_TYPE";
    public static final String REPORT_TYPE = "REPORT_TYPE";
    public static final String START_FROM_DIRECTORY = "START_FROM_DIRECTORY";
    
    public static final String NO_RESOURCE_DATA_ATTACHMENT = "NO_ATTACHMENT";
    public static final String NO_SUBRESOURCE_DATA_ATTACHMENTS = "NO_SUBRESOURCE_ATTACHMENTS";
    
    /**
     * Argument used to pass the destination URI for the resource/folder copy/move operations.
     */
    public static final String DESTINATION_URI = "DESTINATION_URI";
    
    private String name;
    private String value;
    
    /** Creates a new instance of Argument */
    public Argument(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public Argument() {
        
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
	
}
