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
package com.jaspersoft.jasperserver.war.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author aztec
 * @version $Id: JasperViewerTag.java 2098 2006-02-12 17:49:07Z swood $
 */
public class ListReportsTag extends TagSupport {

	private static final Log log = LogFactory.getLog(ListReportsTag.class);
	
	/*
	 * Implement Default Method
	 */
	public int doStartTag() throws JspException {
		try {
			//this method should call the MD API - it may be a pseudo implementation also
			//It should return a Collection of RU-objects - We need to create a Domain
			//object for RU. We have to populate this RU object which we read from the Metadata Repository
			//set this Collection object from in request scope and access in the JSP page
		} catch (Exception _ex) {
			if (log.isErrorEnabled())
				log.error(_ex, _ex);
			throw new JspException(_ex);
		}
		return Tag.SKIP_BODY;
	}

	/*
	 * Implement Default Method
	 */
	public int doEndTag() {
		return Tag.EVAL_PAGE;
	}

	/*
	 * Implement Default Method
	 */
	public void release() {
		//fsdfsd
	}
}
