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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.util.Map;

import org.acegisecurity.providers.AbstractAuthenticationToken;

/**
 * @author swood
 *
 */
public class RequestAuthenticationToken extends AbstractAuthenticationToken {

	private Map requestParameters;
	private Object principal;
	private Object credentials;
	
	public RequestAuthenticationToken() {
		super(null);
	}
	
	/**
	 * 
	 */
	public RequestAuthenticationToken(Map requestParameters) {
		super(null);
		this.requestParameters = requestParameters;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.Authentication#getCredentials()
	 */
	public Object getCredentials() {
		if (credentials != null)
			return credentials;
		else
			return requestParameters;
	}

	/**
	 * @param credentials The credentials to set.
	 */
	public void setCredentials(Object credentials) {
		this.credentials = credentials;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.Authentication#getPrincipal()
	 */
	public Object getPrincipal() {
		return principal;
	}

	/**
	 * @param principal The principal to set.
	 */
	public void setPrincipal(Object principal) {
		this.principal = principal;
	}

}
