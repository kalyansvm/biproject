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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

/**
 * 
 * @author tkavanagh
 * @version $Id: ExecutionContextImpl.java 9116 2007-07-17 00:04:55Z sbirney $
 */
public class ExecutionContextImpl implements ExecutionContext {

	private Locale locale;
	private TimeZone timeZone;
        private List attributes = new ArrayList();
	
	/**
	 * @return List of Attributes for the object
	 */
	public List getAttributes() {
	    return attributes;
	}

        public void setAttributes(List attrs) {
	    attributes = attrs;
        }

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public TimeZone getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}
}
