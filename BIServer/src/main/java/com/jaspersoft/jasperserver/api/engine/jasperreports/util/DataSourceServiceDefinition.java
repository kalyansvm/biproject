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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Set;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataSourceServiceDefinition.java 8408 2007-05-29 23:29:12Z melih $
 */
public class DataSourceServiceDefinition {

	private String serviceBeanName;
	private Set supportedQueryLanguages;
	private boolean anyLanguage;

	public String getServiceBeanName() {
		return serviceBeanName;
	}

	public void setServiceBeanName(String serviceBeanName) {
		this.serviceBeanName = serviceBeanName;
	}

	public Set getSupportedQueryLanguages() {
		return supportedQueryLanguages;
	}

	public void setSupportedQueryLanguages(Set supportedQueryLanguages) {
		this.supportedQueryLanguages = supportedQueryLanguages;
	}

	public boolean isAnyLanguage() {
		return anyLanguage;
	}

	public void setAnyLanguage(boolean anyLanguage) {
		this.anyLanguage = anyLanguage;
	}

}
