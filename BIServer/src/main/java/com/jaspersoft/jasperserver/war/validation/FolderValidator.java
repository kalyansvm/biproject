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
package com.jaspersoft.jasperserver.war.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.FolderWrapper;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: FolderValidator.java 10178 2007-09-20 13:57:21Z lucian $
 */
public class FolderValidator implements Validator
{
	private RepositoryService repository;

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public boolean supports(Class clazz) {
		return FolderWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object object, Errors errors) {
		FolderWrapper wrapper = (FolderWrapper)object;
		Folder folder = wrapper.getActualFolder();

		if (folder.getName() == null || size(folder.getName()) == 0) {
			errors.rejectValue("actualFolder.name", "error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(folder.getName())) {
				errors.rejectValue("actualFolder.name", "FolderValidator.error.invalid.chars");
			}else {
				if (folder.getName().trim().length() > JasperServerConst.MAX_LENGTH_NAME) {
					errors.rejectValue("actualFolder.name", "FolderValidator.error.too.long",
									   new Object[]{JasperServerConst.MAX_LENGTH_NAME_W}, null);
				}else{

					if (!wrapper.isEdit()) {
						if (repository.repositoryPathExists(null, folder.getURIString())) {
							errors.rejectValue("actualFolder.name", "FolderValidator.error.duplicate");
						}
					}
				}
			}
		}

		if (folder.getLabel() == null || size(folder.getLabel()) == 0) {
			errors.rejectValue("actualFolder.label", "FolderValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(folder.getLabel())) {
				errors.rejectValue("actualFolder.label", "FolderValidator.error.invalid.chars");
			}else {
				if (folder.getLabel().trim().length() > JasperServerConst.MAX_LENGTH_LABEL) {
					errors.rejectValue("actualFolder.label", "FolderValidator.error.too.long",
									   new Object[]{JasperServerConst.MAX_LENGTH_LABEL_W}, null);
				}
			}
		}
		
		if (folder.getDescription() != null && size(folder.getDescription()) > JasperServerConst.MAX_LENGTH_DESC) {
			errors.rejectValue("actualFolder.description", "FolderValidator.error.too.long",
							   new Object[]{JasperServerConst.MAX_LENGTH_DESC_W}, null);
		}
	}

	private int size(String text){
		return text.trim().length();
	}

}

