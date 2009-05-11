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

package com.jaspersoft.jasperserver.api.engine.jasperreports.common;

import java.io.Serializable;
import java.util.Map;

/**
 * @author sanda zaharia
 * @version $Id: PdfExportParametersBean.java 10174 2007-09-20 08:01:37Z lucian $
 */
public class PdfExportParametersBean extends AbstractExportParameters {
	
	private Map localizedFontMap;
	
	public void setPropertyValues(Object object){
		if(object instanceof PdfExportParametersBean){
			PdfExportParametersBean bean =(PdfExportParametersBean)object;
			this.setLocalizedFontMap(bean.getLocalizedFontMap());
		}
	}

	/**
	 * @return Returns the localizedFontMap.
	 */
	public Map getLocalizedFontMap() {
		return localizedFontMap;
	}

	/**
	 * @param localizedFontMap The localizedFontMap to set.
	 */
	public void setLocalizedFontMap(Map localizedFontMap) {
		this.localizedFontMap = localizedFontMap;
	}
}
