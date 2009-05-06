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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ValidationErrorsImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class SimpleValidationErrorFilter implements ValidationErrorFilter, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Set codesToInclude;
	private final Set codesToExclude;
	private final Set fieldsToInclude;
	private final Set fieldsToExclude;
	
	public SimpleValidationErrorFilter() 
	{
		codesToInclude = new HashSet();
		codesToExclude = new HashSet();
		fieldsToInclude = new HashSet();
		fieldsToExclude = new HashSet();
	}

	public boolean matchError(ValidationError error) 
	{
		return matchErrorCode(error.getErrorCode());
	}

	public boolean matchErrorCode(String code) 
	{
		return 
			(codesToInclude.isEmpty() || codesToInclude.contains(code))
			&& (codesToExclude.isEmpty() || !codesToExclude.contains(code));
			
	}

	public boolean matchErrorField(String field) 
	{
		return 
			(fieldsToInclude.isEmpty() || fieldsToInclude.contains(field))
			&& (codesToExclude.isEmpty() || !fieldsToExclude.contains(field));
			
	}

	public void addErrorCodeToInclude(String code) 
	{
		codesToInclude.add(code);
		codesToExclude.remove(code);
	}
	
	public void addErrorCodeToExclude(String code) 
	{
		codesToExclude.add(code);
		codesToInclude.remove(code);
	}
	
	public void addErrorFieldToInclude(String field) 
	{
		fieldsToInclude.add(field);
		fieldsToExclude.remove(field);
	}
	
	public void addErrorFieldToExclude(String field) 
	{
		fieldsToExclude.add(field);
		fieldsToInclude.remove(field);
	}
	
}
