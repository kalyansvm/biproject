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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ValidationErrorsImpl.java 12787 2008-04-02 18:01:13Z lucian $
 */
public class ValidationErrorsImpl implements ValidationErrors, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List errors;
	
	public ValidationErrorsImpl() {
		errors = new ArrayList();
	}

	public boolean isError() {
		return !errors.isEmpty();
	}

	public List getErrors() {
		return errors;
	}

	public void add(ValidationError error) {
		errors.add(error);
	}
	
	public String toString() {
		if (!isError()) {
			return "No errors";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(errors.size());
		sb.append(" error(s)\n");
		for (Iterator it = errors.iterator(); it.hasNext();) {
			ValidationError error = (ValidationError) it.next();
			sb.append(error.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	public void removeError(String code, String field) {
		for (Iterator it = errors.iterator(); it.hasNext();) {
			ValidationError error = (ValidationError) it.next();
			if (matches(error, code, field)) {
				it.remove();
			}
		}
	}

	protected boolean matches(ValidationError error, String code, String field) {
		return code.equals(error.getErrorCode())
				&& field.equals(error.getField());
	}
}
