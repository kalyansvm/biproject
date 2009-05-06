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
 * @version $Id: OperationResult.java 4724 2006-09-25 08:23:22Z tdanciu $
 */

import java.util.List;
import java.util.ArrayList;

public class OperationResult {

    public static final int SUCCESS = 0;
    
    private int returnCode = 0;
    private String message;
    private List resourceDescriptors = new ArrayList();
    private String version = "1.2.1";
    
    /**
     * Creates a new instance of OperationResult
     */
    public OperationResult() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List getResourceDescriptors() {
        return resourceDescriptors;
    }

    public void setResourceDescriptors(List resourceDescriptors) {
        this.resourceDescriptors = resourceDescriptors;
    }

    public void addResourceDescriptor(ResourceDescriptor descriptor) {
    	resourceDescriptors.add(descriptor);
    }
    
    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
	
}
