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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain;

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ReportUnit.java 11965 2008-01-30 13:13:51Z lucian $
 */
public interface ReportUnit extends Resource {

    public static final byte LAYOUT_POPUP_SCREEN = 1;
    public static final byte LAYOUT_SEPARATE_PAGE = 2;
    public static final byte LAYOUT_TOP_OF_PAGE = 3;

	/**
	 * Returns the reference to the
	 * {@link com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource data source}
	 * used by this report unit.
	 *
	 * @return a reference to the data source used by this report unit
	 */
	public ResourceReference getDataSource();

	public void setDataSource(ResourceReference dataSourceReference);

	public void setDataSource(ReportDataSource dataSource);

	public void setDataSourceReference(String referenceURI);

	/**
	 * Returns the reference to the
	 * {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Query query}
	 * used by this report unit.
	 *
	 * @return a reference to the query used by this report unit
	 */
	public ResourceReference getQuery();

	public void setQuery(ResourceReference queryReference);


	/**
	 * Returns a list of {@link ResourceReference references} to
	 * {@link InputControl input controls}
	 * used by this report unit.
	 *
	 * @return a list of references to the input controls used by this report unit
	 */
	public List getInputControls();

	public InputControl getInputControl(String name);

	public void setInputControls(List inputControls);

	public void addInputControl(InputControl inputControl);

	public void addInputControl(ResourceReference inputControlReference);

	public void addInputControlReference(String referenceURI);

	public ResourceReference removeInputControl(int index);

	public boolean removeInputControlReference(String referenceURI);

	public InputControl removeInputControlLocal(String name);

	/**
	 * Returns the reference to the {@link FileResource JRXML resource}
	 * used by this report unit as master report
	 *
	 * @return a reference to the master report of this report unit
	 */
	public ResourceReference getMainReport();

	public void setMainReport(ResourceReference reportReference);

	public void setMainReport(FileResource report);

	public void setMainReportReference(String referenceURI);

	/**
	 *
	 */
	public List getResources();

	public FileResource getResourceLocal(String name);

	public void setResources(List resources);

	public void addResource(FileResource resource);

	public void addResource(ResourceReference resourceReference);

	public void addResourceReference(String referenceURI);

	public ResourceReference removeResource(int index);

	public FileResource removeResourceLocal(String name);

	public boolean removeResourceReference(String referenceURI);

	/**
	 *
	 */

	public void setInputControlRenderingView(String viewName);
	public String getInputControlRenderingView();

	public void setReportRenderingView(String viewName);
	public String getReportRenderingView();

    public void setAlwaysPromptControls(boolean alwaysPromptControls);
    public boolean isAlwaysPromptControls();

    public void setControlsLayout(byte controlsLayout);
    public byte getControlsLayout();

	/**
	 * 
	 */
	public ValidationResult validate();

	public void replaceInputControlReference(String referenceURI, ResourceReference inputControlReference);

	public void replaceInputControlReference(String referenceURI, String newReferenceURI);

	public void replaceInputControlReference(String referenceURI, InputControl inputControl);

	public void replaceInputControlLocal(String name, ResourceReference inputControlReference);

	public void replaceInputControlLocal(String name, String newReferenceURI);

	public void replaceInputControlLocal(String name, InputControl inputControl);
	
}
