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

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.war.action.ReportJobEditAction;
import com.jaspersoft.jasperserver.war.util.ValidationErrorsUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobValidator.java 12667 2008-03-25 15:02:21Z lucian $
 */
public class ReportJobValidator implements Validator {

	private ReportSchedulingService schedulingService;
	private ValidationErrorsUtils validationUtils = ValidationErrorsUtils.instance();
	
	public ReportSchedulingService getSchedulingService() {
		return schedulingService;
	}

	public void setSchedulingService(ReportSchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}

	public ValidationErrorsUtils getValidationUtils() {
		return validationUtils;
	}

	public void setValidationUtils(ValidationErrorsUtils validationUtils) {
		this.validationUtils = validationUtils;
	}

	public boolean supports(Class clazz) {
		return ReportJob.class.isAssignableFrom(clazz);
	}

	public void validateJobDetails(ReportJob job, Errors errors) {
		validate(job, errors, ReportJobEditAction.VALIDATION_FIELDS_DETAILS);
	}

	public void validateJobTrigger(ReportJob job, Errors errors) {
		validate(job, errors, ReportJobEditAction.VALIDATION_FIELDS_TRIGGER);
	}

	public void validateJobOutput(ReportJob job, Errors errors) {
		validate(job, errors, ReportJobEditAction.VALIDATION_FIELDS_OUTPUT);
	}

	public void validate(Object obj, Errors errors) {
		validate((ReportJob) obj, errors, null);
	}
	
	protected void validate(ReportJob job, Errors errors, String[] fieldPrefixes) {
		ValidationErrors validationErrors = schedulingService.validateJob(null, job);//TODO context
		validationUtils.setErrors(errors, validationErrors, fieldPrefixes);
	}

}
