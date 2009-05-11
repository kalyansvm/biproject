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

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.FileResourceWrapper;
import com.jaspersoft.jasperserver.war.dto.ReportUnitWrapper;

public class FileResourceValidator implements Validator {

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
		return FileResourceWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object arg0, Errors arg1) {
		// TODO Auto-generated method stub
	}

	public void validateNaming(FileResourceWrapper wrapper, Errors errors) {
		if (wrapper.getFileResource().getLabel() == null
				|| wrapper.getFileResource().getLabel().trim().length() == 0) {
			errors.rejectValue("fileResource.label", "FileResourceValidator.error.not.empty");
		} else {
			if (wrapper.getFileResource().getLabel().length() > 100) {
				errors.rejectValue("fileResource.label", "FileResourceValidator.error.too.long");
			} else if (!JasperServerUtil.regExValidateLabel(wrapper
					.getFileResource().getLabel())) {
				errors.rejectValue("fileResource.label", "FileResourceValidator.error.invalid.chars");
			}
		}
		if (wrapper.getFileResource().getName() == null
				|| wrapper.getFileResource().getName().trim().length() == 0) {
			errors.rejectValue("fileResource.name", "FileResourceValidator.error.not.empty");
		} else {
			wrapper.getFileResource().setName(
					wrapper.getFileResource().getName().trim());
			if (wrapper.getFileResource().getName().length() > 100) {
				errors.rejectValue("fileResource.name", "FileResourceValidator.error.too.long");
			} else if (!JasperServerUtil.regExValidateName(wrapper
					.getFileResource().getName())) {
				errors.rejectValue("fileResource.name", "FileResourceValidator.error.invalid.chars");
			} else if (wrapper.isSubflowMode()) {
				// must check if the resource by this name is already added in
				// the subflow mode
				Object parentObject = wrapper.getParentFlowObject();
				if (parentObject != null
						&& ReportUnitWrapper.class
								.isAssignableFrom(parentObject.getClass())) {
					ReportUnitWrapper parent = (ReportUnitWrapper) parentObject;
					List resources = parent.getReportUnit().getResources();
					if (resources != null && !resources.isEmpty())
						for (int i = 0; i < resources.size(); i++) {
							ResourceReference resRef = (ResourceReference) resources
									.get(i);
							Resource res = null;
							if (resRef.isLocal())
								res = resRef.getLocalResource();
							else
								res = resRef.getReferenceLookup();
							if (wrapper.getFileResource().getName().equals(
									res.getName())) {
								errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
								break;
							}
						}
					List controls = parent.getReportUnit().getInputControls();
					if (controls != null && !controls.isEmpty())
						for (int i = 0; i < controls.size(); i++) {
							ResourceReference resRef = (ResourceReference) controls
									.get(i);
							Resource res = null;
							if (resRef.isLocal())
								res = resRef.getLocalResource();
							else
								res = resRef.getReferenceLookup();
							if (wrapper.getFileResource().getName().equals(
									res.getName())) {
								errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate.inputControl");
								break;
							}
						}
				}
			}
//			else if (wrapper.isNewMode())
//					&& wrapper.getExistingResources() != null) {
//				// When in stand alone new mode check for name uniqueness
//				List res = wrapper.getExistingResources();
//				for (int i = 0; i < res.size(); i++) {
//					String preExtName = (String) res.get(i);
//					if (preExtName.equalsIgnoreCase(wrapper.getFileResource()
//							.getName().trim())) {
//						errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
//						break;
//					}
//				}


			if (wrapper.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, wrapper.getFileResource().getURIString())) {
					errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
				}
			}

			if (FileResource.TYPE_RESOURCE_BUNDLE.equals(wrapper.getFileResource().getFileType())) {
				String name = wrapper.getFileResource().getName();
				if (!name.endsWith(".properties"))
					errors.rejectValue("fileResource.name", "FileResourceValidator.error.resourceBundle");
			}
		}
		if (wrapper.getFileResource().getDescription() != null
				&& wrapper.getFileResource().getDescription().length() > 250)
			errors.rejectValue("fileResource.description", "FileResourceValidator.error.too.long");
	}

	public void validateUpload(FileResourceWrapper wrapper, Errors errors) {
		if (!wrapper.isSubflowMode()) {
			// There is only a upload field on the JSP, edit or new mode
			if (!wrapper.isLocated()) {
				if (wrapper.getNewData() == null
						|| wrapper.getNewData().length == 0)
					errors.rejectValue("newData", "FileResourceValidator.error.not.uploaded");
			}
		} else {
			if (wrapper.getSource() == null) {
				errors.rejectValue("source", "FileResourceValidator.error.no.file");
			} else {
				if (wrapper.getSource().equals(
						JasperServerConst.FIELD_CHOICE_FILE_SYSTEM)) {
					if (!wrapper.isLocated()
							&& (wrapper.getFileResource().getData() == null || wrapper
									.getFileResource().getData().length == 0)) {
						errors.rejectValue("newData", "FileResourceValidator.error.not.uploaded");
					}
				} else {
					if (!wrapper.isLocated()
							&& (wrapper.getNewUri() == null || wrapper
									.getNewUri().trim().length() == 0)) {
						errors.rejectValue("newUri", "FileResourceValidator.error.no.folder");
					}
				}

			}
		}
	}
}
