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
package com.jaspersoft.jasperserver.war.action;

import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.tonbeller.jpivot.olap.model.OlapModel;

public class DisplayOlapModelAction extends FormAction
{
	public static final String OLAPUNIT_ATTR = "olapUnit";
	
	/*
	 * properties
	 */
	private RequestContext requestContext;
	
	private RepositoryService repository;
	
	private OlapConnectionService olapConnection;
	
	private String olapUnitName;

	private OlapUnit olapUnit;
	
	private OlapModel olapModel;

	/*
	 * setters/getters
	 */
	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

    public OlapConnectionService getOlapConnection() {
        return olapConnection;
    }
                
    public void setOlapConnection(OlapConnectionService olapConnection) {
        this.olapConnection = olapConnection;
    }
    
    public OlapModel getOlapModel() {
        return olapModel;
    }
                
    public void setOlapModel(OlapModel olapModel) {
        this.olapModel = olapModel;
    }

	/**
	 * getOlapUnit() retrieves the uri of specified olap unit
	 *  
	 * @param requestContext
	 * @return success or error
	 */
	public Event getOlapUnit(RequestContext requestContext)
	{
		Event result = success();
		
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		
		this.requestContext = requestContext;

		if ((olapUnitName = (String) requestContext.getFlowScope().get(OLAPUNIT_ATTR)) == null)
		{
			result = error();
		}
		else if ((olapUnit = (OlapUnit) repository.getResource(executionContext, olapUnitName)) == null)
		{
			result = error();
		}
		
		return result;
	}
	
	/**
	 * createOlapModel() 
	 * 
	 * @param requestContext
	 * @return success or error
	 */
    public Event createOlapModel(RequestContext requestContext) 
    {
		Event result = success();
		
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		
	    olapModel = olapConnection.createOlapModel(executionContext, olapUnit);

		if (olapModel == null)
		{
			result = error();
		}
	    
		return result;
    }
    
    public Event displayOlapModel(RequestContext requestContext) 
    {
		Event result = success();
		
	    requestContext.getRequestScope().put("olapUnitName", olapUnitName);
	    
		return result;
    }
}
