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
 * @version $Id: Request.java 3614 2006-06-09 12:14:38Z giulio $
 */

import java.util.List;
import java.util.ArrayList;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

public class Request {

    public static final String OPERATION_RUN_REPORT = "runReport";
    public static final String OPERATION_LIST = "list";
    public static final String OPERATION_PUT = "put";
    public static final String OPERATION_GET = "get";
    public static final String OPERATION_LOGIN = "login";
    public static final String OPERATION_CHECK_DEPENDS = "checkForDependentResources";
    
    /**
     * List of arguments
     */
    private List arguments = new ArrayList();
    private ResourceDescriptor resourceDescriptor;
    
    private String operationName = null;
    private String locale = null; // a string defining locale...
    
    /**
     * Creates a new instance of Request
     */
    public Request() {
    }

    public List getArguments() {
        return arguments;
    }

    public void setArguments(List arguments) {
        this.arguments = arguments;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public ResourceDescriptor getResourceDescriptor() {
        return resourceDescriptor;
    }

    public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
        this.resourceDescriptor = resourceDescriptor;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public String getArgumentValue(String argumentName) {
    	String value = null;
        for (int i=0; i < arguments.size(); ++i) {
            Argument a = (Argument) arguments.get(i);
            if (a.getName() == null ? a.getName() == argumentName : a.getName().equals(argumentName)) {
            	value = a.getValue();
            	break;
            }
        }
        return value;
    }
	
}
