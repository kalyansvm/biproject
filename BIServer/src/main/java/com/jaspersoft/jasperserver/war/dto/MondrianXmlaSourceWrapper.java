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
package com.jaspersoft.jasperserver.war.dto;

import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

public class MondrianXmlaSourceWrapper extends BaseDTO {
	private MondrianXMLADefinition mondrianXmlaDefinition;
	private String connectionUri;
	private List allMondrianConnections;
	private boolean connectionInvalid;

	/**
	 * MondrianXmlaSourceWrapper provides wrapper for the
	 * EditMondrianXmlaSourceAction
	 * 
	 * @param mondrianXmlaDefinition
	 */
	public MondrianXmlaSourceWrapper(
			MondrianXMLADefinition mondrianXmlaDefinition) {
		this.mondrianXmlaDefinition = mondrianXmlaDefinition;
		if (mondrianXmlaDefinition.getMondrianConnection() != null)
			this.connectionUri = mondrianXmlaDefinition.getMondrianConnection()
					.getReferenceURI();
	}

	public MondrianXMLADefinition getMondrianXmlaDefinition() {
		return mondrianXmlaDefinition;
	}

	public void setMondrianXmlaDefinition(
			MondrianXMLADefinition mondrianXmlaDefinition) {
		this.mondrianXmlaDefinition = mondrianXmlaDefinition;
	}

	public String getConnectionUri() {
		return connectionUri;
	}

	public void setConnectionUri(String connectionUri) {
		this.connectionUri = connectionUri;
	}

	public boolean isConnectionInvalid() {
		return connectionInvalid;
	}

	public void setConnectionInvalid(boolean connectionInvalid) {
		this.connectionInvalid = connectionInvalid;
	}

	public List getAllMondrianConnections() {
		return allMondrianConnections;
	}

	public void setAllMondrianConnections(List allMondrianConnections) {
		this.allMondrianConnections = allMondrianConnections;
	}
}
