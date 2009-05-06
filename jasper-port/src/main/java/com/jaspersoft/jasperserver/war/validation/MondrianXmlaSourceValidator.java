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
import com.jaspersoft.jasperserver.war.dto.MondrianXmlaSourceWrapper;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;


/**
 * 
 * MondrianXmlaSourceValidator provides validation methods for the
 * mondrianXmlaSourceFlow
 *
 * @author jshih
 */
public class MondrianXmlaSourceValidator implements Validator
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

	public boolean supports(Class klass) {
		return MondrianXmlaSourceWrapper.class.isAssignableFrom(klass);
	}

	public void validate(Object o, Errors errors) {
		MondrianXmlaSourceWrapper details = (MondrianXmlaSourceWrapper) o;
		validateNameLabelDesc(details, errors);
	}

	public void validateNameLabelDesc(MondrianXmlaSourceWrapper wrapper, Errors errors) {
		MondrianXMLADefinition mondrianXmlaDefinition =
			wrapper.getMondrianXmlaDefinition();
		if (mondrianXmlaDefinition.getName() == null ||
			mondrianXmlaDefinition.getName().trim().length() == 0) {
			errors.rejectValue("mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(mondrianXmlaDefinition.getName())) {
				errors.rejectValue(
						"mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.invalid.chars");
			}
			if (mondrianXmlaDefinition.getName().length() > 100) {
				errors.rejectValue(
						"mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.too.long");
			}

			if (wrapper.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, mondrianXmlaDefinition.getURIString())) {
					errors.rejectValue("mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.duplicate");
				}
			}
		}

		if (mondrianXmlaDefinition.getLabel() == null ||
			mondrianXmlaDefinition.getLabel().trim().length() == 0) {
			errors.rejectValue("mondrianXmlaDefinition.label", "MondrianXmlaSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(mondrianXmlaDefinition.getLabel())) {
				errors.rejectValue(
						"mondrianXmlaDefinition.label", "MondrianXmlaSourceValidator.error.invalid.chars");
			}
			if (mondrianXmlaDefinition.getLabel().length() > 100) {
				errors.rejectValue(
						"mondrianXmlaDefinition.label", "MondrianXmlaSourceValidator.error.too.long");
			}
		}

		if (mondrianXmlaDefinition.getDescription() != null &&
			mondrianXmlaDefinition.getDescription().length() > 100) {
			errors.rejectValue(
					"mondrianXmlaDefinition.description", "MondrianXmlaSourceValidator.error.too.long");
		}

		if (mondrianXmlaDefinition.getCatalog() == null ||
			mondrianXmlaDefinition.getCatalog().trim().length() == 0) {
			errors.rejectValue("mondrianXmlaDefinition.catalog", "MondrianXmlaSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(mondrianXmlaDefinition.getCatalog())) {
				errors.rejectValue(
						"mondrianXmlaDefinition.catalog", "MondrianXmlaSourceValidator.error.invalid.chars");
			}
			if (!validateUniqueCatalog(mondrianXmlaDefinition.getCatalog(),
						   mondrianXmlaDefinition.getURIString())) {
			    errors.rejectValue("mondrianXmlaDefinition.catalog", 
					       "MondrianXmlaSourceValidator.error.catalog.exists");
			}
		}
	}

    private boolean validateUniqueCatalog(String catalog, String uri) {
	RepositoryService rep = getRepository();
	FilterCriteria criteria = 
	    FilterCriteria.createFilter(MondrianXMLADefinition.class);
	ResourceLookup[] lookups = rep.findResource(null, criteria);
	catalog = catalog.toLowerCase();

	for (int i = 0; i < lookups.length; i++) {
	    Resource res = rep.getResource(null, lookups[i].getURIString());
	    MondrianXMLADefinition def = (MondrianXMLADefinition) res;
	    if (def.getCatalog().toLowerCase().equals(catalog) &&
		!def.getURIString().equals(uri)) {
		return false;
	    }
	}
	return true;
    }
    
}
