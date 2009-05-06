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
package com.jaspersoft.jasperserver.util;

/**
 * @author tkavanagh
 * @version $Id: ExportImportBean.java 8408 2007-05-29 23:29:12Z melih $
 */

/**
 * This is the top level bean object used in the export and import of metadata
 * from the JI repository.
 */
public class ExportImportBean {

	
	/**
	 * The product version.
	 */
	private String version;
	private ResourceBean resource;
	private UserRoleHolderBean userRoleHolder;
	private ReportJobBean[] reportJobs;
	
	public ResourceBean getResource() {
		return resource;
	}
	
	public void setResource(ResourceBean resource) {
		this.resource = resource;
	}
	
	public UserRoleHolderBean getUserRoleHolder() {
		return userRoleHolder;
	}
	
	public void setUserRoleHolder(UserRoleHolderBean userRoleHolder) {
		this.userRoleHolder = userRoleHolder;
	}

	public ReportJobBean[] getReportJobs() {
		return reportJobs;
	}

	public void setReportJobs(ReportJobBean[] reportJobs) {
		this.reportJobs = reportJobs;
	}

	
	/**
	 * Returns the version of the product used to create this bean.
	 * 
	 * @return the product version
	 */
	public String getVersion() {
		return version;
	}

	
	/**
	 * Sets the version of the product used to create this bean.
	 * 
	 * @param version the product version
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	
}
