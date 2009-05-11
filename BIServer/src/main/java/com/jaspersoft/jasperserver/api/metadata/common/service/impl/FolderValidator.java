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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceValidator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FolderValidator.java 12519 2008-03-17 11:41:38Z lucian $
 */
public class FolderValidator extends BaseResourceValidator implements
		ResourceValidator {

	protected String getErrorMessagePrefix() {
		return "FolderValidator.";
	}

	protected String getFieldPrefix() {
		return "";
	}

	public ValidationErrors validate(Resource resource,
			ValidationErrorFilter filter) {
		ValidationErrors errors = new ValidationErrorsImpl();
		Folder folder = (Folder) resource;
		
		validateLabel(folder, filter, errors);
		validateName(folder, filter, errors);
		validateDescription(folder, filter, errors);
		
		return errors;
	}

}
