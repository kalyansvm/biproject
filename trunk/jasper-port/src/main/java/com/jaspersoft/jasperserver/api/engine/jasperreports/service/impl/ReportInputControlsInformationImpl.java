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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlsInformation;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportInputControlsInformationImpl.java 13482 2008-05-09 16:01:54Z lucian $
 */
public class ReportInputControlsInformationImpl implements
		ReportInputControlsInformation, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map infos = new HashMap();
	
	public ReportInputControlsInformationImpl() {
	}
	
	public ReportInputControlInformation getInputControlInformation(String name) {
		return (ReportInputControlInformation) infos.get(name);
	}
	
	public void setInputControlInformation(String name,
			ReportInputControlInformation info) {
		infos.put(name, info);
	}

	public Set getControlNames() {
		return infos.keySet();
	}

}
