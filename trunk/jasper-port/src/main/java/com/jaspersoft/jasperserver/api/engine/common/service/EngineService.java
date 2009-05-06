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
package com.jaspersoft.jasperserver.api.engine.common.service;

import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.collections.OrderedMap;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.engine.common.domain.Request;
import com.jaspersoft.jasperserver.api.engine.common.domain.Result;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: AbstractAttributedObject.java 2140 2006-02-21 06:41:21Z tony $
 */
public interface EngineService
{

	/**
	 *
	 */
	public Result execute(ExecutionContext context, Request request);

	/**
	 * Returns a list of required resources for the report such as images, fonts, subreports, etc. 
	 */
	public Resource[] getResources(ResourceReference jrxmlReference); //FIXME move this to a different interface or service

	public ValidationResult validate(ExecutionContext context, ReportUnit reportUnit);

	public JasperReport getMainJasperReport(ExecutionContext context, String reportUnitURI);

	public void release();

	public void clearCaches(Class resourceItf, String resourceURI);

	public ReportDataSourceService createDataSourceService(ReportDataSource dataSource);

	public void exportToPdf(ExecutionContext context, String reportUnitURI, Map exportParameters);
	
	public OrderedMap executeQuery(ExecutionContext context, 
			ResourceReference queryReference, String keyColumn, String[] resultColumns, 
			ResourceReference defaultDataSourceReference);
	
	public ResourceLookup[] getDataSources(ExecutionContext context, String queryLanguage);
	
	public Set getDataSourceTypes(ExecutionContext context, String queryLanguage);
	
	public String getQueryLanguage(ExecutionContext context, ResourceReference jrxmlResource);
	
	public Map getReportInputControlDefaultValues(ExecutionContext context, String reportURI, Map initialParameters);
	
	public ReportInputControlsInformation getReportInputControlsInformation(
			ExecutionContext context, String reportURI, Map initialParameters);

}
