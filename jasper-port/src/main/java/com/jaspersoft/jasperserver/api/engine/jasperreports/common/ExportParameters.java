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

/**
 * @author sanda zaharia
 * @version $Id: ExportParameters.java 10154 2007-09-19 16:55:09Z lucian $
 */
public interface ExportParameters {
	
	/**
	 * This method checks the 'object' argument (which should be a compatible  
	 * ExportParameters object) properties and copies their values into the 
	 * caller object
	 * @param object
	 */
	public void setPropertyValues(Object object);

	public boolean isOverrideReportHints();

	public void setOverrideReportHints(boolean overriderHints);

}
