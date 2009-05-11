/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRParameter;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JasperReportInputControlInformation.java 13482 2008-05-09 16:01:54Z lucian $
 */
public class JasperReportInputControlInformation implements
		ReportInputControlInformation, Serializable {

	private static final long serialVersionUID = 1L;
	
	private JRParameter reportParameter;
	private String promptLabel;
	private Object defaultValue;

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getPromptLabel() {
		return promptLabel;
	}

	public Class getValueType() {
		return reportParameter.getValueClass();
	}

	public JRParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(JRParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public void setPromptLabel(String promptLabel) {
		this.promptLabel = promptLabel;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

}
