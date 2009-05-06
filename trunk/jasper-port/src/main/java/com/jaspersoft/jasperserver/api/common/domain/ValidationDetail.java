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
package com.jaspersoft.jasperserver.api.common.domain;

import com.jaspersoft.jasperserver.api.common.domain.Id;

/**
 * 
 * @author tkavanagh
 * @version $Id: ValidationDetail.java 8408 2007-05-29 23:29:12Z melih $
 *
 */
public interface ValidationDetail {

	public Id getId();
	
	/**
	 * 
	 * @return Class - Class of Resource being validated
	 */
	public Class getValidationClass();
	
	public String getName();
	
	public String getLabel();
	
	/**
	 * 
	 * @return String - such as VALID, VALID_STATIC, VALID_DYNAMIC, ERROR
	 */
	public String getResult();
	
	/**
	 * 
	 * @return String - holds value if result in non-valid
	 */
	public String getMessage();
	
	/**
	 * 
	 * @return Exception - potentially holds value if result if non-valid
	 */
	public Exception getException();
	
}
