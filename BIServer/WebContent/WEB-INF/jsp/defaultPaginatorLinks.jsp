<%--
 Copyright (C) 2005 - 2007 JasperSoft Corporation.  All rights reserved.
 http://www.jaspersoft.com.

 Unless you have purchased a commercial license agreement from JasperSoft,
 the following license terms apply:

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 2 as published by
 the Free Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; and without the
 implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 or write to:

 Free Software Foundation, Inc.,
 59 Temple Place - Suite 330,
 Boston, MA  USA  02111-1307
--%>

<%@ page import="java.util.Collection, java.util.Map,
	com.jaspersoft.jasperserver.war.dto.RuntimeInputControlWrapper,
	com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues,
	com.jaspersoft.jasperserver.war.tags.*,
	com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/spring" prefix="spring"%>


<%
PaginatorLinksTag.PaginatorInfo info = (PaginatorLinksTag.PaginatorInfo)request.getAttribute(PaginatorTag.PAGINATOR_INFO_REQUEST_PARAMETER);
%>

<table cellpadding="0" cellspacing="0" border="0">
  <tr valign="middle">
<c:if test="${paginatorInfo.currentPage > 1}">
  <td><a href="javascript:document.forms[0].currentPage.value=1;document.forms[0].goToPage.click();" title="<spring:message code="paginator.links.hint.first.page"/>"><img border="0" src="images/first.gif" class="imageborder" onMouseover="borderImage(this,'#C0C0C0')" onMouseout="borderImage(this,'white')"/></a></td>
  <td><a href="javascript:document.forms[0].currentPage.value=${paginatorInfo.currentPage - 1};document.forms[0].goToPage.click();" title="<spring:message code="paginator.links.hint.previous.page"/>"><img border="0" src="images/prev.gif" class="imageborder" onMouseover="borderImage(this,'#C0C0C0')" onMouseout="borderImage(this,'white')"/></a></td>
  <td>&nbsp;</td>
</c:if>
<c:if test="${paginatorInfo.pageCount > 1 && paginatorInfo.currentPage <= 1}">
  <td><img border="0" src="images/first-d.gif" class="imageborder"/></td>
  <td><img border="0" src="images/prev-d.gif" class="imageborder"/></td>
  <td>&nbsp;</td>
</c:if>
<%
			for (int i = info.firstPage; i < info.currentPage; i++) 
			{
%>
  <td><a href="javascript:document.forms[0].currentPage.value=<%=i%>;document.forms[0].goToPage.click();" title="<spring:message code="paginator.links.hint.go.to.page" arguments="<%= new Integer(i) %>"/>"><%=i%></a>&nbsp;</td>
<%
			}
%>
<c:if test="${paginatorInfo.pageCount > 1}">
  <td><%=info.currentPage%>&nbsp;</td>
</c:if>
<%
			for (int i = info.currentPage + 1; i <= info.lastPage; i++) 
			{
%>
  <td><a href="javascript:document.forms[0].currentPage.value=<%=i%>;document.forms[0].goToPage.click();" title="<spring:message code="paginator.links.hint.go.to.page" arguments="<%= new Integer(i) %>"/>"><%=i%></a>&nbsp;</td>
<%
			}
%>
<c:if test="${paginatorInfo.pageCount > paginatorInfo.currentPage}">
  <td><a href="javascript:document.forms[0].currentPage.value=<%=info.currentPage + 1%>;document.forms[0].goToPage.click();" title="<spring:message code="paginator.links.hint.next.page"/>"><img border="0" src="images/next.gif" class="imageborder" onMouseover="borderImage(this,'#C0C0C0')" onMouseout="borderImage(this,'white')"/></a></td>
  <td><a href="javascript:document.forms[0].currentPage.value=<%=info.pageCount%>;document.forms[0].goToPage.click();" title="<spring:message code="paginator.links.hint.last.page"/>"><img border="0" src="images/last.gif" class="imageborder" onMouseover="borderImage(this,'#C0C0C0')" onMouseout="borderImage(this,'white')"/></a></td>
</c:if>
<c:if test="${paginatorInfo.pageCount > 1 && paginatorInfo.pageCount <= paginatorInfo.currentPage}">
  <td><img border="0" src="images/next-d.gif" class="imageborder"/></td>
  <td><img border="0" src="images/last-d.gif" class="imageborder"/></td>
</c:if>
  </tr>
</table>
<input type="submit" name="_eventId_goToPage" id="goToPage" value="edit" style="visibility:hidden;" onClick="javascript:return (js_pagination_hookActions())"/>
<input type="hidden" name="currentPage" id="currentPage"/>

<script type="text/javascript">
   function js_pagination_hookActions() {
      if (window.js_hookActions) {
         return (js_hookActions());
      }
      return true;
   }

</script>
