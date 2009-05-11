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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.tags.PaginatorLinksTag.PaginatorInfo;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class PaginatorTag extends TagSupport
{
	public static final String CURRENT_PAGE_REQUEST_PARAMETER = "currentPage";
	public static final String PAGINATED_ITEMS_REQUEST_PARAMETER = "paginatedItems";
	public static final String PAGINATOR_INFO_REQUEST_PARAMETER = "paginatorInfo";
	
	private List items = null;
	private String page = null;
	private String strItemsPerPage = null;
	private String strPagesRange = null;


	public List getItems() {
		return items;
	}

	public void setItems(List items) {
		this.items = items;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getItemsPerPage() {
		return strItemsPerPage;
	}

	public void setItemsPerPage(String itemsPerPage) {
		this.strItemsPerPage = itemsPerPage;
	}

	public String getPagesRange() {
		return strPagesRange;
	}

	public void setPagesRange(String pagesRange) {
		this.strPagesRange = pagesRange;
	}


	public int doStartTag() throws JspException
	{
		if(items == null || items.size() == 0) 
			return SKIP_BODY;

		int itemsPerPage = -1;

		if(strItemsPerPage != null && strItemsPerPage.trim().length() > 0)
			itemsPerPage = Integer.parseInt(strItemsPerPage);

		if(itemsPerPage <= 0)
			itemsPerPage = 
				((ConfigurationBean)WebApplicationContextUtils.getRequiredWebApplicationContext(
					pageContext.getServletContext()
					).getBean("configurationBean")).getPaginatorItemsPerPage();

		int pagesRange = -1;

		if(strPagesRange != null && strPagesRange.trim().length() > 0)
			pagesRange = Integer.parseInt(strPagesRange);

		if(pagesRange <= 0)
			pagesRange = 
				((ConfigurationBean)WebApplicationContextUtils.getRequiredWebApplicationContext(
					pageContext.getServletContext()
					).getBean("configurationBean")).getPaginatorPagesRange();

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		int allItemsCount = items.size();
		int allPagesCount = allItemsCount / itemsPerPage;
		if (allItemsCount % itemsPerPage > 0)
			allPagesCount += 1;

		List paginatedItems = null;

		int crtPage = 1;
		String strCrtPage = request.getParameter(CURRENT_PAGE_REQUEST_PARAMETER);

		if(strCrtPage == null || strCrtPage.trim().length() == 0) 
			strCrtPage = page;

		if(strCrtPage != null && strCrtPage.trim().length() > 0) 
			crtPage = Integer.parseInt(strCrtPage);

		if(allItemsCount <= crtPage * itemsPerPage) 
		{
			crtPage = allPagesCount;
		}

		if(allItemsCount < crtPage * itemsPerPage)
			paginatedItems = items.subList((crtPage - 1) * itemsPerPage, allItemsCount);
		else
			paginatedItems = items.subList((crtPage - 1) * itemsPerPage, crtPage * itemsPerPage);

		PaginatorInfo info = new PaginatorLinksTag.PaginatorInfo();
		info.currentPage = crtPage;
		info.firstPage = (crtPage - pagesRange >= 1 ? crtPage - pagesRange : 1);
		info.lastPage = info.firstPage + 2 * pagesRange;
		info.lastPage = (info.lastPage <= allPagesCount ? info.lastPage : allPagesCount);
		info.firstPage = info.lastPage - 2 * pagesRange;
		info.firstPage = (info.firstPage >= 1 ? info.firstPage : 1);
		info.pageCount = allPagesCount;
		
		request.setAttribute(PAGINATED_ITEMS_REQUEST_PARAMETER, paginatedItems);
		request.setAttribute(PAGINATOR_INFO_REQUEST_PARAMETER, info);

		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		request.setAttribute(PAGINATED_ITEMS_REQUEST_PARAMETER, null);
		request.setAttribute(PAGINATOR_INFO_REQUEST_PARAMETER, null);

		return EVAL_PAGE;
	}

}
