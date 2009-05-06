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

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.RoleWrapper;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class RoleValidator implements Validator
{
	private UserAuthorityService userService;

	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}

	public boolean supports(Class klass)
	{
		return RoleWrapper.class.isAssignableFrom(klass);
	}

	public void validate(Object object, Errors errors)
	{

	}


	public void validateName(RoleWrapper wrapper, Errors errors)
	{
		Role role = wrapper.getRole();
		if (role.getRoleName() == null || role.getRoleName().length() == 0) {
			errors
					.rejectValue("role.roleName",
							"RoleValidator.error.not.empty");
		} 
		else {
			if (!JasperServerUtil.regExValidateName(role.getRoleName())) {
				errors.rejectValue("role.roleName",
						"RoleValidator.error.invalid.chars");
			}
			if (wrapper.isNewMode()
					&& userService.roleExists(null, role.getRoleName())) {
				errors.rejectValue("role.roleName",
						"RoleValidator.error.duplicate");
			}
		}
		
	}
}
