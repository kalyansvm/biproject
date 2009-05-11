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
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.ValidationUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseResourceValidator.java 12519 2008-03-17 11:41:38Z lucian $
 */
public abstract class BaseResourceValidator {
	
	private RepositoryService repository;

	protected void validateName(Resource resource, ValidationErrorFilter filter, ValidationErrors errors)
	{
		String nameField = getFieldPrefix() + "name";
		if (filter == null || filter.matchErrorField(nameField))
		{
			if (resource.getName() == null || resource.getName().trim().length() == 0) 
			{
				errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.not.empty", 
						null, null, nameField));
			}
			else 
			{
				if (resource.getName().length() > 100) 
				{
					errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.too.long", 
							new Object[]{new Integer(100)}, null, nameField));
				}
				else if (!ValidationUtil.regExValidateName(resource.getName()))
				{
					errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.invalid.chars", 
							null, null, nameField));
				}
				else 
				{
					if (filter != null && filter.matchErrorCode(getErrorMessagePrefix() + "error.duplicate")) 
					{
						if (getRepositoryService().repositoryPathExists(null, resource.getURIString())) 
						{
							errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.duplicate", 
									null, null, nameField));
						}
					}
				}
			}
		}
	}
	
	protected void validateLabel(Resource resource, ValidationErrorFilter filter, ValidationErrors errors)
	{
		String labelField = getFieldPrefix() + "label";
		if (filter == null || filter.matchErrorField(labelField))
		{
			if (resource.getLabel() == null || resource.getLabel().trim().length() == 0) 
			{
				errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.not.empty", 
						null, null, labelField));
			}
			else 
			{
				if (resource.getLabel().length() > 100) 
				{
					errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.too.long", 
							new Object[]{new Integer(100)}, null, labelField));
				}
			}
		}
	}
	
	protected void validateDescription(Resource resource, ValidationErrorFilter filter, ValidationErrors errors)
	{
		String descriptionField = getFieldPrefix() + "description";
		if (filter == null || filter.matchErrorField(descriptionField))
		{
			if (resource.getDescription() != null && resource.getDescription().length() > 250)
			{
				errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.too.long", 
						new Object[]{new Integer(250)}, null, descriptionField));
			}
		}
	}
	
	protected abstract String getErrorMessagePrefix();
	
	protected abstract String getFieldPrefix();

	public RepositoryService getRepositoryService() {
		return repository;
	}

	public void setRepositoryService(RepositoryService repository) {
		this.repository = repository;
	}

	
}
