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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.ListOfValuesDTO;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: ListOfValuesValidator.java 10178 2007-09-20 13:57:21Z lucian $
 */
public class ListOfValuesValidator implements Validator
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

	public boolean supports(Class klass)
	{
		return ListOfValuesDTO.class.isAssignableFrom(klass);
	}

	public void validate(Object object, Errors errors)
	{
	}

	public void validateResourceData(ListOfValuesDTO listOfValuesDTO, Errors errors)
	{
		ListOfValues listOfValues = listOfValuesDTO.getListOfValues();
		if (listOfValues.getName() == null || size(listOfValues.getName()) == 0) {
			errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.not.empty");
			} else {
				if(listOfValues.getName().length()> JasperServerConst.MAX_LENGTH_NAME){
					errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.too.long",
									   new Object[]{JasperServerConst.MAX_LENGTH_NAME_W}, null);
				}else{
					if(!JasperServerUtil.regExValidateName(listOfValues.getName())) {
						errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.invalid.chars");
						}/*else{
							// When in stand alone new mode check name uniquenesss in folder
							List allResources=listOfValuesDTO.getAllResources();
							if(listOfValuesDTO.isAloneNewMode() && allResources!=null){
								for(int i=0;i<allResources.size();i++){
									Resource res=(Resource)allResources.get(i);
									if(res.getName().equals(listOfValues.getName().trim())){
										errors.rejectValue("listOfValues.name",
												null, "A Resource with this name already exists");
										break;
									}
								}
							}
						}*/
				}
			if (listOfValuesDTO.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, listOfValues.getURIString())) {
					errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.duplicate");
				}
			}

			}

		if (listOfValues.getLabel() == null || size(listOfValues.getLabel()) == 0) {
			errors.rejectValue("listOfValues.label", "ListOfValuesValidator.error.not.empty");
		} else {
			if(listOfValues.getLabel().length()> JasperServerConst.MAX_LENGTH_LABEL){
				errors.rejectValue("listOfValues.label", "ListOfValuesValidator.error.too.long",
								   new Object[]{JasperServerConst.MAX_LENGTH_LABEL_W}, null);
			}else{
				if(!JasperServerUtil.regExValidateLabel(listOfValues.getLabel())) {
					errors.rejectValue("listOfValues.label", "ListOfValuesValidator.error.invalid.chars");
				}
			}
		}

		if (listOfValues.getDescription() != null && 
				size(listOfValues.getDescription()) >JasperServerConst.MAX_LENGTH_DESC) {
			errors.rejectValue("listOfValues.description", "ListOfValuesValidator.error.too.long",
							   new Object[]{JasperServerConst.MAX_LENGTH_DESC_W}, null);
		}
	}

	public void validateAddValue(ListOfValuesDTO listOfValuesDTO, Errors errors){
		String newLabel=listOfValuesDTO.getNewLabel();
		if( newLabel == null || size(newLabel) == 0){
			errors.rejectValue("newLabel", "ListOfValuesValidator.error.not.empty");
		}else{
			if(newLabel.length()>JasperServerConst.MAX_LENGTH_LABEL){
				errors.rejectValue("newLabel", "ListOfValuesValidator.error.too.long",
								   new Object[]{JasperServerConst.MAX_LENGTH_LABEL_W}, null);
			}else{
				if(!JasperServerUtil.regExValidateLabel(newLabel)) {
					errors.rejectValue("newLabel", "ListOfValuesValidator.error.invalid.chars");
				}
			}						
		}
			
		if(listOfValuesDTO.getNewValue() == null || size(listOfValuesDTO.getNewValue()) == 0){
			errors.rejectValue("newValue", "ListOfValuesValidator.error.not.empty");
		}else{
			if(listOfValuesDTO.getNewValue().length()>JasperServerConst.MAX_LENGTH_LABEL){
				errors.rejectValue("newValue", "ListOfValuesValidator.error.too.long",
								   new Object[]{JasperServerConst.MAX_LENGTH_LABEL_W}, null);
			}
		}
			
	}
	
	public void validateLovSave(ListOfValuesDTO listOfValuesDTO, Errors errors){
		Object []values=listOfValuesDTO.getListOfValues().getValues();
		if(values==null || values.length==0){
			// Let the no values error show up at the label error field
			errors.rejectValue("newLabel", "ListOfValuesValidator.error.no.data");
		}
	}

	private int size(String text){
		return text.trim().length();
	}

}

