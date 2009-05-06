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

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;



/**
 * @author sanda zaharia
 * @version $Id: ExporterConfigurationBean.java 10154 2007-09-19 16:55:09Z lucian $
 */
public class ExporterConfigurationBean implements Serializable{
	
	private String descriptionKey;
	private String iconSrc;
	private String parameterDialogName;
	private ExportParameters exportParameters;
	private AbstractReportExporter currentExporter;
	/**
	 * @return Returns the iconSrc.
	 */
	public String getIconSrc() {
		return iconSrc;
	}
	/**
	 * @param iconSrc The iconSrc to set.
	 */
	public void setIconSrc(String iconSrc) {
		this.iconSrc = iconSrc;
	}
	/**
	 * @return Returns the parameterDialogName.
	 */
	public String getParameterDialogName() {
		return parameterDialogName;
	}
	/**
	 * @param parameterDialogName The parameterDialogName to set.
	 */
	public void setParameterDialogName(String parameterDialogName) {
		this.parameterDialogName = parameterDialogName;
	}
	/**
	 * @return Returns the descriptionKey.
	 */
	public String getDescriptionKey() {
		return descriptionKey;
	}
	/**
	 * @param descriptionKey The descriptionKey to set.
	 */
	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters() {
		return exportParameters;
	}
	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(ExportParameters exportParameters) {
		this.exportParameters = exportParameters;
	}
	/**
	 * @return Returns the currentExporter.
	 */
	public AbstractReportExporter getCurrentExporter() {
		return currentExporter;
	}
	/**
	 * @param currentExporter The currentExporter to set.
	 */
	public void setCurrentExporter(AbstractReportExporter currentExporter) {
		this.currentExporter = currentExporter;
	}
}
