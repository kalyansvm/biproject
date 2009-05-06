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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import org.acegisecurity.intercept.method.aspectj.AspectJCallback;
import org.acegisecurity.intercept.method.aspectj.AspectJSecurityInterceptor;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * @author swood
 *
 */
public aspect ObjectSecurityAspect implements InitializingBean {
	private AspectJSecurityInterceptor securityInterceptor;

	pointcut inDomainModel(): 
		within(com.jaspersoft.jasperserver.api.metadata..*);
	
	pointcut javaLangObjectMethodExecution(): 
		execution(* Object.*(..));
	
	pointcut domainObjectInstanceExecution(): 
		target(Resource) && 
		execution(public * *(..)) && 
		!javaLangObjectMethodExecution() && 
		inDomainModel();

	Object around(): domainObjectInstanceExecution() {
		if (this.securityInterceptor != null) {
			AspectJCallback callback = new AspectJCallback() {
				public Object proceedWithObject() {
					return proceed();
				}
			};
			return this.securityInterceptor.invoke(thisJoinPoint, callback);
		} else {
			return proceed();
		}
	}

	public AspectJSecurityInterceptor getSecurityInterceptor() {
		return securityInterceptor;
	}

	public void setSecurityInterceptor(AspectJSecurityInterceptor securityInterceptor) {
		this.securityInterceptor = securityInterceptor;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */

	public void afterPropertiesSet() throws Exception {
		if (this.securityInterceptor == null)
			throw new IllegalArgumentException("securityInterceptor required");
	}

}
