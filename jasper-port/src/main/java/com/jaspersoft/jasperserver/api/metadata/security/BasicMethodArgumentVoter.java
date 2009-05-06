/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.api.metadata.security;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthorizationServiceException;
import org.acegisecurity.acl.AclEntry;
import org.acegisecurity.acl.AclManager;
import org.acegisecurity.acl.basic.BasicAclEntry;
import org.aopalliance.intercept.MethodInvocation;

import com.jaspersoft.jasperserver.api.common.util.Functor;

/**
 * @author Lucian Chirita
 *
 */
public class BasicMethodArgumentVoter implements MethodArgumentAclVoter {

	private Class argumentType;
	private int argumentIndex = 1;
	private int[] accessPermissions;
	private Functor argumentFunctor;
	
	public boolean allow(MethodInvocation methodCall, Authentication authentication, AclManager aclManager) {
		Object secureObject = getSecureObject(methodCall);
		AclEntry[] acls = aclManager.getAcls(secureObject, authentication);
		return accessPermitted(acls);
	}

	protected Object getSecureObject(MethodInvocation methodCall) {
		Class[] types = methodCall.getMethod().getParameterTypes();
		int idx = -1;
		int cnt = 0;
		for (int i = 0; i < types.length; i++) {
			if (argumentType.isAssignableFrom(types[i])) {
				++cnt;
				if (cnt == argumentIndex) {
					idx = i;
					break;
				}
			}
		}
		
		if (idx == 0) {
			throw new AuthorizationServiceException("Argument #" + argumentIndex + " of type " + argumentType
					+ " not found in method " + methodCall.getMethod());
		}
		
		Object arg = methodCall.getArguments()[idx];
		if (argumentFunctor != null) {
			arg = argumentFunctor.transform(arg);
		}
		return arg;
	}

	protected boolean accessPermitted(AclEntry[] acls) {
		boolean matches = false;
		if (acls != null && acls.length > 0) {
			for (int i = 0; i < acls.length; i++) {
				AclEntry aclEntry = acls[i];
				if (aclEntry instanceof BasicAclEntry) {
					BasicAclEntry basicAclEntry = (BasicAclEntry) aclEntry;
					if (accessPermitted(basicAclEntry)) {
						matches = true;
						break;
					}
				}
			}
		}
		return matches;
	}
	
	protected boolean accessPermitted(BasicAclEntry basicAclEntry) {
		boolean access = false;
		for (int i = 0; i < accessPermissions.length; i++) {
			if (basicAclEntry.isPermitted(accessPermissions[i])) {
				access = true;
				break;
			}
		}
		return access;
	}

	public Class getArgumentType() {
		return argumentType;
	}

	public void setArgumentType(Class argumentType) {
		this.argumentType = argumentType;
	}

	public int getArgumentIndex() {
		return argumentIndex;
	}

	public void setArgumentIndex(int argumentIndex) {
		this.argumentIndex = argumentIndex;
	}

	public int[] getAccessPermissions() {
		return accessPermissions;
	}

	public void setAccessPermissions(int[] accessPermissions) {
		this.accessPermissions = accessPermissions;
	}

	public Functor getArgumentFunctor() {
		return argumentFunctor;
	}

	public void setArgumentFunctor(Functor argumentFunctor) {
		this.argumentFunctor = argumentFunctor;
	}

}
