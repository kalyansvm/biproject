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

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.UserWrapper;

public class CRUDUserValidator implements Validator {

	private UserAuthorityService userService;

	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}

	public boolean supports(Class clazz) {
		return UserWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object bean, Errors errors) {
		UserWrapper wrapper = (UserWrapper) bean;
		User user = wrapper.getUser();

		if(user.getUsername() == null || user.getUsername().trim().length() == 0) {
			errors.rejectValue("user.username", "CRUDUserValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(user.getUsername())) {
				errors.rejectValue("user.username", "CRUDUserValidator.error.invalid.chars");
			}

			if (wrapper.isNewMode() && userService.userExists(null, user.getUsername())) {
				errors.rejectValue("user.username", "CRUDUserValidator.error.duplicate");
			}

		}

		if(user.getFullName() == null || user.getFullName().trim().length() == 0) {
			errors.rejectValue("user.fullName", "CRUDUserValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(user.getFullName())) {
				errors.rejectValue("user.fullName", "CRUDUserValidator.error.invalid.chars");
			}
		}

		// Only internally defined users have a password

		if (!user.isExternallyDefined()) {
			if(user.getPassword() == null || user.getPassword().trim().length() == 0) {
				errors.rejectValue("user.password", "CRUDUserValidator.error.not.empty");
			} else if (wrapper.getConfirmedPassword() == null || !wrapper.getConfirmedPassword().equals(user.getPassword())) {
				errors.rejectValue("confirmedPassword", "CRUDUserValidator.error.password.not.matches");
			}
		}

		if(user.getEmailAddress() != null) {
			if(user.getEmailAddress().trim().length() > 0) {
				if(!JasperServerUtil.regExValidateEmail(user.getEmailAddress())) {
					errors.rejectValue("user.emailAddress", "CRUDUserValidator.error.invalid.format");
				}
			} else  {
				if(user.getEmailAddress().trim().length() != user.getEmailAddress().length()) {
					errors.rejectValue("user.emailAddress", "CRUDUserValidator.error.contains.spaces");
				}
			}
		}
	}
}
