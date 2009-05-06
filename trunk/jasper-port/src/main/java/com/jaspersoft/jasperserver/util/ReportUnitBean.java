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
 * @version $Id: ReportUnitBean.java 8408 2007-05-29 23:29:12Z melih $
 */

public class ReportUnitBean extends ResourceBean {

	/*
	 * The following come from the ReportUnit interface 
	 */
	private DataSourceBean dataSource;
	private QueryBean query;
	private InputControlBean[] inputControls;
	private FileResourceBean mainReport;
	private FileResourceBean[] resources;

	
	public DataSourceBean getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceBean dataSource) {
		this.dataSource = dataSource;
	}
	
	public QueryBean getQuery() {
		return query;
	}

	public void setQuery(QueryBean query) {
		this.query = query;
	}	
	
	public InputControlBean[] getInputControls() {
		return inputControls;
	}

	public void setInputControls(InputControlBean[] inputControls) {
		this.inputControls = inputControls;
	}

	public FileResourceBean getMainReport() {
		return mainReport;
	}

	public void setMainReport(FileResourceBean mainReport) {
		this.mainReport = mainReport;
	}

	public FileResourceBean[] getResources() {
		return resources;
	}

	public void setResources(FileResourceBean[] resources) {
		this.resources = resources;
	}	
}
