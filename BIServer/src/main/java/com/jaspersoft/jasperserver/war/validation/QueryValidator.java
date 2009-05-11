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

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.jaspersoft.jasperserver.war.dto.DataTypeWrapper;
import com.jaspersoft.jasperserver.war.dto.QueryWrapper;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: QueryValidator.java 10178 2007-09-20 13:57:21Z lucian $
 */
public class QueryValidator implements Validator
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
		return QueryWrapper.class.isAssignableFrom(klass);
	}

	public void validate(Object object, Errors errors)
	{
		QueryWrapper wrapper = (QueryWrapper) object;
		validateNameLabelDesc(wrapper, errors);
		validateQueryText(wrapper, errors);
	}

	public void validateNameLabelDesc(QueryWrapper wrapper, Errors errors)
	{
		Query query = wrapper.getQuery();
		if (query.getName() == null || query.getName().trim().length() == 0) {
			errors.rejectValue("query.name", "QueryValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(query.getName())) {
				errors.rejectValue("query.name", "QueryValidator.error.invalid.chars");
			}
			if (query.getName().length() > 100) {
				errors.rejectValue("query.name", "QueryValidator.error.too.long");
			}

			if (wrapper.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, query.getURIString())) {
					errors.rejectValue("query.name", "QueryValidator.error.duplicate");
				}
			}
		}

		if (query.getLabel() == null || query.getLabel().trim().length() == 0) {
			errors.rejectValue("query.label", "QueryValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(query.getLabel())) {
				errors.rejectValue("query.label", "QueryValidator.error.invalid.chars");
			}
			if (query.getLabel().length() > 100) {
				errors.rejectValue("query.label", "QueryValidator.error.too.long");
			}
		}

		if (query.getDescription() != null && query.getDescription().length() > 250) {
			errors.rejectValue("query.description", "QueryValidator.error.too.long");
		}
	}

	public void validateQueryText(QueryWrapper wrapper, Errors errors)
	{
		Query query = wrapper.getQuery();

		if (query.getSql() == null || query.getSql().trim().length() == 0) {
			errors.rejectValue("query.sql", "QueryValidator.error.not.empty");
		}
	}
}
