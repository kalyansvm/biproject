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
package com.jaspersoft.jasperserver.war.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.tags.PaginatorTag;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class ObjectPermissionAction extends FormAction
{
	protected final String RESOURCE = "resource";
	protected final String ATTRIBUTE_PERMISSIONS = "permissions";
	protected final String ATTRIBUTE_ROLES = "roles";
	protected final String ATTRIBUTE_USERS = "users";
	protected final int NO_ACCESS = 0;
	protected final int NO_PERMISSION_SET = -1;
	protected final int INHERITED = 0x100;
	protected final String PERMISSION_PARM = "permission_";
	protected final String PREV_PERMISSION_PARM = "prev";

	protected final Log log = LogFactory.getLog(this.getClass());

	private ObjectPermissionService objectPermissionService;
	private RepositoryService repository;
	private UserAuthorityService userService;


	public Event loadPermissionsForRoles(RequestContext context)
	{
		ExecutionContext executionContext = getExecutionContext(context);
		List roleList = userService.getRoles(executionContext, null);
		Map permissionsMap = new HashMap();

		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		if (resource == null) {
			String resourceUri = context.getRequestParameters().get(RESOURCE);
			resource = repository.getResource(executionContext, resourceUri);
			if (resource == null) {
				resource = repository.getFolder(executionContext, resourceUri);
			}
			context.getFlowScope().put(RESOURCE, resource);
		}
		for (int i = 0; i < roleList.size(); i++) {
			Role role = (Role) roleList.get(i);
			Integer permissionToDisplay = new Integer(NO_PERMISSION_SET);
			List permissions = objectPermissionService.getObjectPermissionsForObjectAndRecipient(executionContext, resource, role);
			if (permissions != null && permissions.size() > 0) {			
				ObjectPermission objectPermission = (ObjectPermission) permissions.get(0);

				if (objectPermission != null && objectPermission.getPermissionRecipient() != null) {
					permissionToDisplay = new Integer(objectPermission.getPermissionMask() + goUpFolderChainToGetPermission(executionContext, resource, role));
					
				}
			}
			// if no permission is set, go up the folder chain until hitting a folder with permission set.
			if ((!("/".equals(resource.getURIString()))) && (permissionToDisplay.intValue() == NO_PERMISSION_SET)) {
				permissionToDisplay = new Integer(goUpFolderChainToGetPermission(executionContext, resource, role) + INHERITED); 
			} else if (("/".equals(resource.getURIString())) && (permissionToDisplay.intValue() == NO_PERMISSION_SET)) {
				permissionToDisplay = new Integer(INHERITED + NO_ACCESS);
			} 
			permissionsMap.put(role, permissionToDisplay);
		}

		context.getRequestScope().put(ATTRIBUTE_PERMISSIONS, permissionsMap);
		context.getRequestScope().put(ATTRIBUTE_ROLES, roleList);
		return success();
	}
	
	private int goUpFolderChainToGetPermission(ExecutionContext context, Resource resource, Object roleOrUser) {
		
		String parentFolderPath = resource.getParentFolder();
		if (parentFolderPath == null) {
			return 0;
		}
		Resource parentResource = repository.getFolder(context, parentFolderPath);
		Integer permissionToDisplay = new Integer(NO_PERMISSION_SET);

		List permissions = objectPermissionService.getObjectPermissionsForObjectAndRecipient(context, parentResource, roleOrUser);
		if (permissions != null && permissions.size() > 0) {
			ObjectPermission objectPermission = (ObjectPermission) permissions.get(0);

			if (objectPermission != null && objectPermission.getPermissionRecipient() != null) {
				permissionToDisplay = new Integer((objectPermission.getPermissionMask() << 9));
			}
		}
		// some roles don't have any access, and there is no inherited, so no permission is set
		if ((permissionToDisplay.intValue() == NO_PERMISSION_SET) && (!("/".equals(parentFolderPath)))) {
			permissionToDisplay = new Integer(goUpFolderChainToGetPermission(context, parentResource, roleOrUser)); 	
		}
		
		if (permissionToDisplay.intValue() == NO_PERMISSION_SET) {
			permissionToDisplay = new Integer(NO_ACCESS);
		}
		return permissionToDisplay.intValue();
	}

	public Event loadPermissionsForUsers(RequestContext context)
	{

		ExecutionContext executionContext = getExecutionContext(context);
		List userList = userService.getUsers(executionContext, null);
		Map permissionsMap = new HashMap();

		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		if (resource == null) {
			String resourceUri = context.getRequestParameters().get(RESOURCE);
			resource = repository.getResource(executionContext, resourceUri);
			if (resource == null) {
				resource = repository.getFolder(executionContext, resourceUri);
			}
			context.getFlowScope().put(RESOURCE, resource);
		}
		
		for (int i = 0; i < userList.size(); i++) {
			User user = (User) userList.get(i);
			Integer permissionToDisplay = new Integer(NO_PERMISSION_SET);
			List permissions = objectPermissionService.getObjectPermissionsForObjectAndRecipient(executionContext, resource, user);
			if (permissions != null && permissions.size() > 0) {			
				ObjectPermission objectPermission = (ObjectPermission) permissions.get(0);

				if (objectPermission != null && objectPermission.getPermissionRecipient() != null) {
					permissionToDisplay = new Integer(objectPermission.getPermissionMask() + goUpFolderChainToGetPermission(executionContext, resource, user));
				}
			}
			// if no permission is set, go up the folder chain until hitting a folder with permission set.
			if ((!("/".equals(resource.getURIString()))) && (permissionToDisplay.intValue() == NO_PERMISSION_SET)) {
				permissionToDisplay = new Integer(goUpFolderChainToGetPermission(executionContext, resource, user) + INHERITED); 
			} else if (("/".equals(resource.getURIString())) && (permissionToDisplay.intValue() == NO_PERMISSION_SET)) {
				permissionToDisplay = new Integer(INHERITED + NO_ACCESS);
			} 
			permissionsMap.put(user, permissionToDisplay);
		}
		
		context.getRequestScope().put(ATTRIBUTE_PERMISSIONS, permissionsMap);
		context.getRequestScope().put(ATTRIBUTE_USERS, userList);
		return success();
	}

	public Event setRolePermission(RequestContext context)
	{		
		Map parameters = context.getRequestParameters().asMap();
		Iterator iter = parameters.keySet().iterator();
		ArrayList parmList = new ArrayList();
		while (iter.hasNext()) {
			String currentParameterName = (String)iter.next();
			if (currentParameterName.startsWith(PERMISSION_PARM)) {
			   parmList.add(currentParameterName);
			}
		}
		// nothing to do
		if (parmList.size() <= 0) {
			return success();
		}		
		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		for (int i=0; i<parmList.size(); i++) {
			String roleName = ((String)parmList.get(i)).substring(PERMISSION_PARM.length());
			Role role = userService.getRole(getExecutionContext(context), roleName);
			String permission = context.getRequestParameters().get((String)parmList.get(i));
			String prevPermission = context.getRequestParameters().get(PREV_PERMISSION_PARM + (String)parmList.get(i));
			int permissionValue = Integer.parseInt(permission);
			int prevPermissionValue = Integer.parseInt(prevPermission);
			if (prevPermissionValue != permissionValue){
				if (permissionValue <= 0xff) {
					if (permissionValue == 0){
						int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, role);
						if (upChainPermission == NO_ACCESS) {
                  	       if (!performObjectPermissionDelete(getExecutionContext(context), resource, role)) {
						       throw new RuntimeException(" Error occurred in object-permission delete. ");
                  	       }
						} else {
  				          if (!performObjectPermissionSave(getExecutionContext(context), resource, role, permissionValue)) { 				        	  
					          throw new RuntimeException(" Error occurred in object-permission Save. ");  
  				          }                 	    	
                  	    }
					} else {
					
				      int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, role);
				      if (upChainPermission == (permissionValue << 9)) {
				    	  if (!performObjectPermissionDelete(getExecutionContext(context), resource, role)) {
							  throw new RuntimeException(" Error occurred in object-permission delete. ");
	                 	  }	
				      } else {
				          if (!performObjectPermissionSave(getExecutionContext(context), resource, role, permissionValue)) {
					          throw new RuntimeException(" Error occurred in object-permission Save. ");
				          }
				      }
					}
				} else {
                      if (("/".equals(resource.getURIString())) && (permissionValue != 256)){
                    	  permissionValue = permissionValue / 512;
                    	  if (!performObjectPermissionSave(getExecutionContext(context), resource, role, permissionValue)) {
        					  throw new RuntimeException(" Error occurred in object-permission Save. "); 
                          }
                      } else if (permissionValue >= 256) {
                    	  permissionValue = permissionValue / 512;
                    	  if (!performObjectPermissionSave(getExecutionContext(context), resource, role, permissionValue)) {
        					  throw new RuntimeException(" Error occurred in object-permission Save. "); 
                          }                    	  
                      } else {
                    	  if (!performObjectPermissionDelete(getExecutionContext(context), resource, role)) {
						     throw new RuntimeException(" Error occurred in object-permission delete. ");
                    	  }
                      }	
				}
			}		
		}	
		return success();
	}

	public Event setUserPermission(RequestContext context)
	{
		
		Map parameters = context.getRequestParameters().asMap();
		Iterator iter = parameters.keySet().iterator();
		ArrayList parmList = new ArrayList();
		while (iter.hasNext()) {
			String currentParameterName = (String)iter.next();
			if (currentParameterName.startsWith(PERMISSION_PARM)) {
			   parmList.add(currentParameterName);
			}
		}
		// nothing to do
		if (parmList.size() <= 0) {
			return success();
		}		
		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		for (int i=0; i<parmList.size(); i++) {
			String userName = ((String)parmList.get(i)).substring(PERMISSION_PARM.length());
			User user = userService.getUser(getExecutionContext(context), userName);
			String permission = context.getRequestParameters().get((String)parmList.get(i));
			String prevPermission = context.getRequestParameters().get(PREV_PERMISSION_PARM + (String)parmList.get(i));
			int permissionValue = Integer.parseInt(permission);
			int prevPermissionValue = Integer.parseInt(prevPermission);
			if (prevPermissionValue != permissionValue){
				if (permissionValue <= 0xff) {
					if (permissionValue == 0){
						int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, user);
						if (upChainPermission == NO_ACCESS) {
                  	       if (!performObjectPermissionDelete(getExecutionContext(context), resource, user)) {
						       throw new RuntimeException(" Error occurred in object-permission delete. ");
                  	       }
						} else {
  				          if (!performObjectPermissionSave(getExecutionContext(context), resource, user, permissionValue)) { 				        	  
					          throw new RuntimeException(" Error occurred in object-permission Save. ");  
  				          }                 	    	
                  	    }
					} else {
					
				      int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, user);
				      if (upChainPermission == (permissionValue << 9)) {
				    	  if (!performObjectPermissionDelete(getExecutionContext(context), resource, user)) {
							  throw new RuntimeException(" Error occurred in object-permission delete. ");
	                 	  }	
				      } else {
				          if (!performObjectPermissionSave(getExecutionContext(context), resource, user, permissionValue)) {
					          throw new RuntimeException(" Error occurred in object-permission Save. ");
				          }
				      }
					}
				} else {
                      if (("/".equals(resource.getURIString())) && (permissionValue != 256)){
                    	  permissionValue = permissionValue / 512;
                    	  if (!performObjectPermissionSave(getExecutionContext(context), resource, user, permissionValue)) {
        					  throw new RuntimeException(" Error occurred in object-permission Save. "); 
                          }
                      } else if (permissionValue >= 256) {
                    	  permissionValue = permissionValue / 512;
                    	  if (!performObjectPermissionSave(getExecutionContext(context), resource, user, permissionValue)) {
        					  throw new RuntimeException(" Error occurred in object-permission Save. "); 
                          }                    	  
                      } else {
                    	  if (!performObjectPermissionDelete(getExecutionContext(context), resource, user)) {
						     throw new RuntimeException(" Error occurred in object-permission delete. ");
                    	  }
                      }	
				}
			}		
		}			
		return success();
	}

	private boolean performObjectPermissionSave(ExecutionContext context, Resource targetObject, Object recipientObject, int permission) {

		if (recipientObject == null) {
			log.warn("performObjectPermissionDelete: recipient is null");
			return false;
		}

		if (targetObject == null) {
			log.warn("performObjectPermissionDelete: target is null");
			return false;
		}

		ObjectPermission objectPermission = null;

		List lstObjPerms = objectPermissionService.getObjectPermissionsForObjectAndRecipient(context, targetObject, recipientObject);

		if (lstObjPerms != null && lstObjPerms.size() > 0)
			objectPermission = (ObjectPermission) lstObjPerms.get(0);

		if (objectPermission == null) {
			objectPermission = objectPermissionService.newObjectPermission(context);
		}

		// Because of default permissions, we could get something that has no recipient

		objectPermission.setURI(targetObject.getProtocol() + ":" + targetObject.getURIString());
		objectPermission.setPermissionMask(permission);
		objectPermission.setPermissionRecipient(recipientObject);

		objectPermissionService.putObjectPermission(context, objectPermission);
		return true;
	}

	/*
	 * Function to perform the Delete action on Object Permissions
	 * @args
	 * @return boolean
	 */
	private boolean performObjectPermissionDelete(ExecutionContext context, Resource targetObject, Object recipientObject) {

		if (recipientObject == null) {
			log.warn("performObjectPermissionDelete: recipient is null");
			return false;
		}

		if (targetObject == null) {
			log.warn("performObjectPermissionDelete: target is null");
			return false;
		}

		ObjectPermission objectPermission = null;
		List lstObjPerms = objectPermissionService.getObjectPermissionsForObjectAndRecipient(context, targetObject, recipientObject);

		if (lstObjPerms != null && lstObjPerms.size() > 0)
			objectPermission = (ObjectPermission) lstObjPerms.get(0);
		// Because of default permissions, we could get something that has no recipient
		if (objectPermission == null || objectPermission.getPermissionRecipient() == null) {
			log.warn("performObjectPermissionDelete: no permission for target and recipient");
			return true;
		}
		objectPermissionService.deleteObjectPermission(context, objectPermission);
		return true;
	}

	public Event goToPage(RequestContext context)
	{
		context.getFlowScope().put(
			PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER, 
			context.getRequestParameters().get(PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER)
			);

		return success();
	}

	public ObjectPermissionService getObjectPermissionService()
	{
		return objectPermissionService;
	}

	public void setObjectPermissionService(ObjectPermissionService objectPermissionService)
	{
		this.objectPermissionService = objectPermissionService;
	}

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}

	protected ExecutionContext getExecutionContext(RequestContext context) {
		return JasperServerUtil.getExecutionContext(context);
	}

}
