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

<%@ page language="java" contentType="text/html" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<c:if test='${dataResource.subflowMode}'>
    <meta name="pageHeading" content="<c:choose><c:when test='${dataResource.subEditMode}'><spring:message code="jsp.olapDS.pageHeading_edit"/></c:when><c:otherwise><spring:message code="jsp.olapDS.pageHeading_new"/></c:otherwise></c:choose>"/>
</c:if>
  <title><spring:message code="jsp.olapDS.chooseType.title"/></title>
  <script>
    function jumpTo(pageTo){
    document.forms[0].jumpToPage.value=pageTo;
    document.forms[0].jumpButton.click();
    }
  </script>
</head>

<body>

<table width="100%" border="0" cellpadding="20" cellspacing="0">
  <tr>
    <td>

<FORM name="fmCRChTyp" action="" method="post">
            <table cellpading="1" cellspacing="0" border="0" align="center">
                <tr>
                    <td>&nbsp;</td>
                    <td><span class="fsection"><c:if test='${dataResource.subflowMode}'></c:if><spring:message code="jsp.olapDS.chooseType.title"/></span></td>
                </tr>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><spring:message code="jsp.olapDS.chooseType.selectType"/></td>
                </tr>
                <spring:bind path="dataResource.type">
                	<c:forEach items="${dataResource.allTypes}" var="type" varStatus="counter">
		                <tr>
		                    <td colspan="2">&nbsp;</td>
		                </tr>
                	    <tr>
                          <td>&nbsp;</td>
	                      <td><input type="radio" id="radio${counter.index}" onClick="document.forms[0]._eventId_Next.click();" name="${status.expression}" <c:if test='${status.value==type || status.value==null}'>checked="true"</c:if>
	                        value="${type}" />&nbsp;<a href="javascript:document.forms[0].radio${counter.index}.click();" >${type}</a></td>
	                    </tr>
                	</c:forEach>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td>
                        <input type="submit" class="fnormal" name="_eventId_Cancel" value="<spring:message code="button.cancel"/>">&nbsp;
                        <c:if test='${dataResource.mode!=1 && dataResource.mode!=2}'>
                            <input type="submit" class="fnormal" name="_eventId_Back" value="<spring:message code="button.back"/>">&nbsp;
                        </c:if>
                        <input type="submit" class="fnormal" name="_eventId_Next" value="<spring:message code="button.next"/>">
                    </td>
                </tr>
            </table>
            <input type="hidden" name="jumpToPage">
            <input type="submit" class="fnormal" style="visibility:hidden;" value="" name="_eventId_Jump" id="jumpButton">
            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
</FORM>

    </td>
  </tr>
</table>

</body>

</html>
