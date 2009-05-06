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
import java.text.MessageFormat;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ValidationErrorImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ValidationErrorImpl implements ValidationError, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String errorCode;
	private final Object[] arguments;
	private final String defaultMessage;
	private final String field;
	
	public ValidationErrorImpl(String errorCode, Object[] arguments, String defaultMessage, String field) {
		this.errorCode = errorCode;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
		this.field = field;
	}
	
	public ValidationErrorImpl(String errorCode, Object[] arguments, String defaultMessage) {
		this(errorCode, arguments, defaultMessage, null);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Object[] getErrorArguments() {
		return arguments;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public String getField() {
		return field;
	}

	public String toString() {
		if (getDefaultMessage() != null) {
			return MessageFormat.format(getDefaultMessage(), getErrorArguments());
		}
		
		if (getField() == null) {
			return getErrorCode();
		}
		
		return getErrorCode() + "." + getField();
	}
}
