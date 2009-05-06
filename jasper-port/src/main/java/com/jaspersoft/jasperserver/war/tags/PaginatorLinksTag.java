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
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class PaginatorLinksTag extends TagSupport
{
	public static final String DEFAULT_RENDER_JSP = "/WEB-INF/jsp/defaultPaginatorLinks.jsp";
	
	private String renderJsp = null;

	
	/**
	 *
	 */
	public int doEndTag() throws JspException
	{
		try 
		{
			pageContext.include(getRenderJsp());
		}
		catch (Exception e) 
		{
			throw new JspException(e);
		}

		return EVAL_PAGE;
	}


	/**
	 *
	 */
	public String getRenderJsp() 
	{
		if (renderJsp == null || renderJsp.trim().length() == 0) 
		{
			return DEFAULT_RENDER_JSP;
		}
		return renderJsp;
	}


	/**
	 *
	 */
	public void setRenderJsp(String renderJsp) 
	{
		this.renderJsp = renderJsp;
	}
	

	/**
	 *
	 */
	public static class PaginatorInfo
	{
		public int currentPage = 1;
		public int firstPage = 1;
		public int lastPage = 1;
		public int pageCount = 1;
		
		public int getCurrentPage()
		{
			return currentPage;
		}
		
		public int getFirstPage()
		{
			return firstPage;
		}
		
		public int getLastPage()
		{
			return lastPage;
		}
		
		public int getPageCount()
		{
			return pageCount;
		}
	}
	
}
