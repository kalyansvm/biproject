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
<%@ taglib uri="/spring" prefix="spring"%>

<html>
<head>
  <title><spring:message code="jsp.olapDS.chooseSource.title"/></title>
<c:if test='${dataResource.subflowMode}'>
    <meta name="pageHeading" content="<c:choose><c:when test='${dataResource.subEditMode}'><spring:message code="jsp.olapDS.pageHeading_edit"/></c:when><c:otherwise><spring:message code="jsp.olapDS.pageHeading_new"/></c:otherwise></c:choose>"/>
</c:if>
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

<FORM name="fmCRContQd" action="" method="post">
            <table border="0" cellpadding="1" cellspacing="0" align="center">
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="2"><span class="fsection"><c:if test='${dataResource.subflowMode}'></c:if><spring:message code="jsp.olapDS.chooseSource.header"/></span></td>
                </tr>
                <tr>
                    <td colspan="3">&nbsp;</td>
                </tr>
                <spring:bind path="dataResource.source">
                    <tr>
                        <td>&nbsp;</td>
                        <td><input name="${status.expression}" type="radio" id="CONTENT_REPOSITORY" value="CONTENT_REPOSITORY" <c:if test='${status.value!="LOCAL"}'>checked="true"</c:if>></td>
                        <td><spring:message code="label.fromRepository"/></td>
                    </tr>
                    <c:if test="${status.error}">
                    <tr>
                        <td>&nbsp;</td>
                        <td collspan="2"><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <spring:bind path="dataResource.selectedUri">
                    <tr>
                        <td colspan="2">&nbsp;</td>
                        <td>
							<select name="${status.expression}" size="1" onClick="document.forms[0].CONTENT_REPOSITORY.click();" title="<spring:message code="jsp.olapDS.chooseSource.selectedUri.title"/>" class="fnormal">
								<c:forEach items="${dataResource.allDatasources}" var="path">
									<option value="${path}" 
										<c:if test='${path==status.value}'>selected="true"</c:if>>${path}</option>   
								</c:forEach>
							</select>	                        
                        </td>
                    </tr>
                    <c:if test="${status.error}">
                    <tr>
                        <td colspan="2">&nbsp;</td>
                        <td><span class="ferror">${status.errorMessage}</span></td>
                    </tr>
                    </c:if>
                </spring:bind>
                <spring:bind path="dataResource.source">
	                <tr>
	                    <td colspan="3">&nbsp;</td>
	                </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td><input type="radio" name="${status.expression}" id="LOCAL" onClick="document.forms[0]._eventId_Next.click();" value="LOCAL" <c:if test='${status.value=="LOCAL"}'>checked="true"</c:if>></td>
                        <td><a href="javascript:document.forms[0].LOCAL.click();"><spring:message code="label.locallyDefined"/></a></td>
                    </tr>
                </spring:bind>
                <tr><td colspan="3">&nbsp;</td></tr>
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="2">
                    <input type="submit" class="fnormal" name="_eventId_Cancel" value="<spring:message code="button.cancel"/>"/>&nbsp;
                    <input type="submit" class="fnormal" name="_eventId_Back" value="<spring:message code="button.back"/>">&nbsp;
					<%-- standalone: 1=new, 2=edit --%>
<%--
					<c:if test="${dataResource.parentMode==1 || dataResource.parentMode==2}"> 
						<input type="submit" class="fnormal" name="_eventId_Save" value="Save">
					</c:if>
--%>
					<input type="submit" class="fnormal" name="_eventId_Next" value="<spring:message code="button.next"/>">
                    </td>
                </tr>
            </table>
            <input type="hidden" name="jumpToPage">
            <input type="submit" class="fnormal" style="visibility:hidden;" value="" name="_eventId_Jump" id="jumpButton">   
            <input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
</FORM>

    </td>
  </tr>
</table>

</body>

</html>
