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

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceValidator;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.ReportDataSourceWrapper;

public class ReportDataSourceValidator implements Validator {

	private JasperServerConstImpl constants=new JasperServerConstImpl();
	private RepositoryService repository;
	private CustomReportDataSourceServiceFactory customDataSourceFactory;

	public CustomReportDataSourceServiceFactory getCustomDataSourceFactory() {
		return customDataSourceFactory;
	}

	public void setCustomDataSourceFactory(
			CustomReportDataSourceServiceFactory customDataSourceFactory) {
		this.customDataSourceFactory = customDataSourceFactory;
	}

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public boolean supports(Class clazz) {
		return ReportDataSourceWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) 
	{
		ReportDataSourceWrapper wrapper = (ReportDataSourceWrapper)obj;
		validateSource(wrapper, errors);
		validateType(wrapper, errors);
		if (!errors.hasErrors())
		{
			if (JasperServerConstImpl.getJDBCDatasourceType().equals(wrapper.getType()))
			{
				jdbcPropsForm(wrapper, errors);
			}
			else if (JasperServerConstImpl.getJNDIDatasourceType().equals(wrapper.getType()))
			{
				jndiPropsForm(wrapper, errors);
			}
			else if (JasperServerConstImpl.getBeanDatasourceType().equals(wrapper.getType()))
			{
				beanPropsForm(wrapper, errors);
			}
			else if (wrapper.getType() != null)
			{
				customPropsForm(wrapper, errors);
			}
		}
	}


	public void validateSource(ReportDataSourceWrapper wrapper, Errors errors){
		if (wrapper.getSource() == null)
			errors.rejectValue("source", "ReportDataSourceValidator.error.not.empty");
		if (constants.getFieldChoiceRepo().equals(wrapper.getSource())
				&&( wrapper.getSelectedUri()==null || wrapper.getSelectedUri().trim().length()==0))
			errors.rejectValue("selectedUri", "ReportDataSourceValidator.error.not.empty");
	}

	public void validateType(ReportDataSourceWrapper wrapper, Errors errors){
		if (constants.getFieldChoiceLocal().equals(wrapper.getSource()) && wrapper.getType() == null)
		{
			errors.rejectValue("type", "ReportDataSourceValidator.error.not.empty");
		}
	}

	public void jndiPropsForm(ReportDataSourceWrapper wrapper, Errors errors){
		JndiJdbcReportDataSource jndiSource=(JndiJdbcReportDataSource)wrapper.getReportDataSource();
		if(jndiSource.getJndiName()==null || jndiSource.getJndiName().trim().length()==0) {
			errors.rejectValue("reportDataSource.jndiName", "ReportDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateJndiServiceName(jndiSource.getJndiName())) {
				errors.rejectValue("reportDataSource.jndiName", "ReportDataSourceValidator.error.invalid.chars");
			}
		}
		namingForm(wrapper, errors);
	}

	public void namingForm(ReportDataSourceWrapper wrapper, Errors errors){
		ReportDataSource ds=wrapper.getReportDataSource();
		if(ds.getName()==null || ds.getName().trim().length()==0) {
			errors.rejectValue("reportDataSource.name", "ReportDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(ds.getName())) {
				errors.rejectValue("reportDataSource.name", "ReportDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getLabel()==null || ds.getLabel().trim().length()==0) {
			errors.rejectValue("reportDataSource.label", "ReportDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(ds.getLabel())) {
				errors.rejectValue("reportDataSource.label", "ReportDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getDescription() != null && ds.getDescription().trim().length() > 250)
			errors.rejectValue("reportDataSource.description", "ReportDataSourceValidator.error.too.long");

		if (wrapper.isAloneNewMode()) {
			if (repository.repositoryPathExists(null, ds.getURIString())) {
				errors.rejectValue("reportDataSource.name", "ReportDataSourceValidator.error.duplicate");
			}
		}
	}

	public void jdbcPropsForm(ReportDataSourceWrapper wrapper, Errors errors){
		JdbcReportDataSource ds=(JdbcReportDataSource)wrapper.getReportDataSource();
		if(ds.getDriverClass()==null || ds.getDriverClass().trim().length()==0) {
			errors.rejectValue("reportDataSource.driverClass", "ReportDataSourceValidator.error.not.empty");
		} else {
			ds.setDriverClass(ds.getDriverClass().trim());
			if(!JasperServerUtil.regExValidateDbDriver(ds.getDriverClass())) {
				errors.rejectValue("reportDataSource.driverClass", "ReportDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getConnectionUrl()==null || ds.getConnectionUrl().trim().length()==0) {
			errors.rejectValue("reportDataSource.connectionUrl", "ReportDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateJdbcURL(ds.getConnectionUrl())) {
				errors.rejectValue("reportDataSource.connectionUrl", "ReportDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getUsername()==null || ds.getUsername().trim().length()==0)
			errors.rejectValue("reportDataSource.username", "ReportDataSourceValidator.error.not.empty");

		namingForm(wrapper, errors);
	}

	public void beanPropsForm(ReportDataSourceWrapper wrapper, Errors errors){
		BeanReportDataSource beanSource=(BeanReportDataSource)wrapper.getReportDataSource();
		if(beanSource.getBeanName()==null || beanSource.getBeanName().trim().length()==0) {
			errors.rejectValue("reportDataSource.beanName", "ReportDataSourceValidator.error.not.empty");
		} else {
			// TODO Try and find the bean in the application context
			// If found, see if it has the given method
			// If it has the given method, does that method return the right thing
		}
		namingForm(wrapper, errors);
	}

	public void customPropsForm(ReportDataSourceWrapper wrapper, Errors errors){
		CustomReportDataSource cds = (CustomReportDataSource)wrapper.getReportDataSource();
		// see if there is a validator
		CustomDataSourceDefinition cdef = customDataSourceFactory.getDefinitionByServiceClass(cds.getServiceClass());
		CustomDataSourceValidator val = cdef.getValidator();
		if (val != null) {
			val.validatePropertyValues(cds, errors);
		}
		namingForm(wrapper, errors);
	}
}
