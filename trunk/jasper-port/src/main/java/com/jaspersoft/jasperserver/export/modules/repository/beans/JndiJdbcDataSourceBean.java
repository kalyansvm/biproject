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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id: JndiJdbcDataSourceBean.java 8408 2007-05-29 23:29:12Z melih $
 */
public class JndiJdbcDataSourceBean extends ResourceBean {

	private String jndiName;
	private String timezone;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) res;
		setJndiName(ds.getJndiName());
		setTimezone(ds.getTimezone());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) res;
		ds.setJndiName(getJndiName());
		ds.setTimezone(getTimezone());
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}	

}
