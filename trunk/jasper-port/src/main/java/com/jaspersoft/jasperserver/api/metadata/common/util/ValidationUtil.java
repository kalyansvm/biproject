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
package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.validation.Errors;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JasperServerUtil.java 9052 2007-07-11 02:10:37Z melih $
 */
public class ValidationUtil 
{

	private static final Pattern PATTERN_NAME = Pattern.compile("(\\p{L}|\\p{N}|(\\_)|(\\.)|(\\-)|[;@])+");

	/*
	 * function to validate name
	 * allows only valid word characters and doesn't allow
	 * any space or any special characters for this field
	 * arguments string
	 * returns boolean
	 */
	public static boolean regExValidateName(String inp) throws PatternSyntaxException 
	{
		Matcher mat = PATTERN_NAME.matcher(inp.trim());
		return mat.matches();
	}

	/**
	 * 
	 */
	public static void copyErrors(ValidationErrors errors, Errors uiErrors) 
	{
		if (errors != null && uiErrors != null)
		{
			for(Iterator it = errors.getErrors().iterator(); it.hasNext();)
			{
				ValidationError error = (ValidationError)it.next();
				uiErrors.rejectValue(error.getField(), error.getErrorCode());
			}
		}
	}

}
