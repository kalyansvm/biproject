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
package com.jaspersoft.jasperserver.api.metadata.user.domain;

import java.util.Set;
import java.util.Date;

import com.jaspersoft.jasperserver.api.common.domain.AttributedObject;
import java.util.List;

/**
 * @author swood
 * @version $Id: User.java 13657 2008-05-20 01:23:00Z swood $
 */
public interface User extends AttributedObject {
	
	public String getUsername();
	public void setUsername(String username);
	
	public String getFullName();
	public void setFullName(String fullName);
	
	/**
	 * Only if we are using our own authentication
	 * 
	 * @return
	 */
	public String getPassword();
	public void setPassword(String password);

	public String getEmailAddress();
	public void setEmailAddress(String emailAddress);

	public boolean isExternallyDefined();
	public void setExternallyDefined(boolean externallyDefined);

	public boolean isEnabled();
	public void setEnabled(boolean enabled);
	
	public Set getRoles();
	public void setRoles(Set newRoles);
	
	public void addRole(Role aRole);
	public void removeRole(Role aRole);
	
	public Date getPreviousPasswordChangeTime();
	public void setPreviousPasswordChangeTime(Date timeStamp);
	
        public List getAttributes();
        public void setAttributes(List attrs);
}
