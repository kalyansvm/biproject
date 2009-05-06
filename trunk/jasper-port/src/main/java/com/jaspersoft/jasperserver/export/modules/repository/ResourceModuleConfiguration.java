/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.export.modules.repository;

import java.util.Map;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.service.ImplementationObjectFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.modules.common.ReportParametersTranslator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceModuleConfiguration.java 12731 2008-03-28 13:37:04Z lucian $
 */
public class ResourceModuleConfiguration {
	
	private RepositoryService repository;
	private String indexFileName;
	private String resourcesDirName;
	private String folderDetailsFileName;
	private String folderIndexElement;
	private String resourceIndexElement;
	private ImplementationObjectFactory castorBeanMappings;
	private ObjectSerializer serializer;
	private Map resourceDataProviders;
	private ObjectPermissionService permissionService;
	private UserAuthorityService authorityService;
	private String permissionRecipientRole;
	private String permissionRecipientUser;
	private ReportParametersTranslator reportParametersTranslator;
	
	public ImplementationObjectFactory getCastorBeanMappings() {
		return castorBeanMappings;
	}
	
	public void setCastorBeanMappings(ImplementationObjectFactory castorBeanMappings) {
		this.castorBeanMappings = castorBeanMappings;
	}
	
	public String getFolderDetailsFileName() {
		return folderDetailsFileName;
	}
	
	public void setFolderDetailsFileName(String folderDetailsFileName) {
		this.folderDetailsFileName = folderDetailsFileName;
	}
	
	public String getFolderIndexElement() {
		return folderIndexElement;
	}
	
	public void setFolderIndexElement(String folderIndexElement) {
		this.folderIndexElement = folderIndexElement;
	}
	
	public RepositoryService getRepository() {
		return repository;
	}
	
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}
	
	public Map getResourceDataProviders() {
		return resourceDataProviders;
	}
	
	public void setResourceDataProviders(Map resourceDataProviders) {
		this.resourceDataProviders = resourceDataProviders;
	}
	
	public ResourceDataProvider getResourceDataProvider(String providerId) {
		ResourceDataProvider dataProvider = (ResourceDataProvider) resourceDataProviders.get(providerId);
		if (dataProvider == null) {
			throw new JSException("jsexception.no.resource.data.provider.found", new Object[] {providerId});
		}
		return dataProvider;
	}
	
	public String getResourceIndexElement() {
		return resourceIndexElement;
	}
	
	public void setResourceIndexElement(String resourceIndexElement) {
		this.resourceIndexElement = resourceIndexElement;
	}
	
	public String getResourcesDirName() {
		return resourcesDirName;
	}
	
	public void setResourcesDirName(String resourcesDirName) {
		this.resourcesDirName = resourcesDirName;
	}
	
	public ObjectSerializer getSerializer() {
		return serializer;
	}
	
	public void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}

	public String getIndexFileName() {
		return indexFileName;
	}

	public void setIndexFileName(String indexFileName) {
		this.indexFileName = indexFileName;
	}

	public UserAuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(UserAuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public ObjectPermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(ObjectPermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public String getPermissionRecipientRole() {
		return permissionRecipientRole;
	}

	public void setPermissionRecipientRole(String permissionRecipientRole) {
		this.permissionRecipientRole = permissionRecipientRole;
	}

	public String getPermissionRecipientUser() {
		return permissionRecipientUser;
	}

	public void setPermissionRecipientUser(String permissionRecipientUser) {
		this.permissionRecipientUser = permissionRecipientUser;
	}

	public ReportParametersTranslator getReportParametersTranslator() {
		return reportParametersTranslator;
	}

	public void setReportParametersTranslator(
			ReportParametersTranslator reportParametersTranslator) {
		this.reportParametersTranslator = reportParametersTranslator;
	}

}
