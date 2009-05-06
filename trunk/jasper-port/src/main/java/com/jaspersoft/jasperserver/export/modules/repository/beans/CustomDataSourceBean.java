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

package com.jaspersoft.jasperserver.export.modules.repository.beans;

import java.util.HashMap;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CustomDataSourceBean.java 9474 2007-08-10 21:16:47Z bob $
 */
public class CustomDataSourceBean extends ResourceBean {

	private String serviceClass;
	private Map propertyMap;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		CustomReportDataSource ds = (CustomReportDataSource) res;
		setServiceClass(ds.getServiceClass());
		setPropertyMap(new HashMap(ds.getPropertyMap()));
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		CustomReportDataSource ds = (CustomReportDataSource) res;
		ds.setServiceClass(getServiceClass());
		ds.setPropertyMap(new HashMap(getPropertyMap()));
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public Map getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}
	
}
