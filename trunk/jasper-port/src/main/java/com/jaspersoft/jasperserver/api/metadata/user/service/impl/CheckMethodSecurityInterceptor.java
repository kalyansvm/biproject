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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import org.acegisecurity.intercept.AbstractSecurityInterceptor;
import org.acegisecurity.intercept.InterceptorStatusToken;
import org.acegisecurity.intercept.ObjectDefinitionSource;
import org.acegisecurity.intercept.method.MethodDefinitionSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * Used to check access to methods, not run them.
 * 
 * @author swood
 *
 */
public class CheckMethodSecurityInterceptor extends AbstractSecurityInterceptor
		implements MethodInterceptor {
    //~ Instance fields ================================================================================================

    private MethodDefinitionSource objectDefinitionSource;

    //~ Methods ========================================================================================================

    public MethodDefinitionSource getObjectDefinitionSource() {
        return this.objectDefinitionSource;
    }

    public Class getSecureObjectClass() {
        return MethodInvocation.class;
    }

    public ObjectDefinitionSource obtainObjectDefinitionSource() {
        return this.objectDefinitionSource;
    }

    public void setObjectDefinitionSource(MethodDefinitionSource newSource) {
        this.objectDefinitionSource = newSource;
    }

	/* (non-Javadoc)
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	public Object invoke(MethodInvocation mi) throws Throwable {

        try {
             super.beforeInvocation(mi);
        } catch (Exception e) {
			// you don't have access
			throw e;
		}

        return null; // you have access
	}

}
