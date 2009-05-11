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
package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.metadata.common.util.NullValue;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: PersistentReportJobSource.java 8492 2007-06-01 09:11:39Z lucian $
 */
public class PersistentReportJobSource {

	private String reportUnitURI;
	private Map parameters;
	
	public PersistentReportJobSource() {
	}

	public String getReportUnitURI() {
		return reportUnitURI;
	}

	public void setReportUnitURI(String reportUnitURI) {
		this.reportUnitURI = reportUnitURI;
	}

	public Map getParametersMap() {
		return parameters;
	}

	public void setParametersMap(Map parameters) {
		this.parameters = parameters;
	}

	public void copyFrom(ReportJobSource source) {
		setReportUnitURI(source.getReportUnitURI());
		setParametersMap(NullValue.replaceWithNullValues(source.getParametersMap()));
	}

	public ReportJobSource toClient() {
		ReportJobSource source = new ReportJobSource();
		source.setReportUnitURI(getReportUnitURI());
		source.setParametersMap(NullValue.restoreNulls(getParametersMap()));
		return source;
	}

}
