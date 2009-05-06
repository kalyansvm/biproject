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


<html>
<head>
<meta name="pageHeading" content="<spring:message code="jsp.querySource.pageHeading"/>"/>
<title><spring:message code="jsp.querySource.title"/></title>
<script>
function jumpTo(pageTo){
    document.forms[0].jumpToPage.value=pageTo;
    document.forms[0].jumpButton.click();
}
</script>
</head>
<body bgcolor="#CCCCCC">

<FORM name="fmCRContLov" action="" method="post">
<table width="100%" border="0" cellpadding="20" cellspacing="0">
  <tr valign="top">
<c:if test='${masterFlow == "reportUnit"}'>
<c:choose>
<c:when test='${parentFlow == "reportUnit"}'>
    <td width="1">
<table width="100%" border="0" cellpading="0" cellspacing="0">
  <tr><td nowrap="true"><a class="wizard_menu" href="javascript:jumpTo('reportNaming');"><spring:message code="jsp.reportWizard.naming"/></a></td></tr>
  <tr><td nowrap="true"><a class="wizard_menu" href="javascript:jumpTo('jrxmlUpload');"><spring:message code="jsp.reportWizard.jrxml"/></a></td></tr>
  <tr><td nowrap="true"><a class="wizard_menu" href="javascript:jumpTo('resources');"><spring:message code="jsp.reportWizard.resources"/></a></td></tr>
  <tr><td nowrap="true"><a class="wizard_menu" href="javascript:jumpTo('dataSource');"><spring:message code="jsp.reportWizard.dataSource"/></a></td></tr>
  <tr><td nowrap="true"><a class="wizard_menu_current" href="javascript:jumpTo('query');"><spring:message code="jsp.reportWizard.query"/></a></td></tr>
  <tr><td nowrap="true"><a class="wizard_menu" href="javascript:jumpTo('customization');"><spring:message code="jsp.reportWizard.customization"/></a></td></tr>
</table>
<input type="hidden" name="mainFlow" id="mainFlow" value="mainFlow"/>
    </td>
</c:when>
<c:otherwise>
    <td width="1">
<table width="100%" border="0" cellpading="0" cellspacing="0">
  <tr><td nowrap="true"><span class="wizard_menu_disabled"><spring:message code="jsp.reportWizard.naming"/></span></td></tr>
  <tr><td nowrap="true"><span class="wizard_menu_disabled"><spring:message code="jsp.reportWizard.jrxml"/></span></td></tr>
  <tr><td nowrap="true">
  <c:choose>
  <c:when test='${masterFlowStep == "resources"}'>
    <a class="wizard_menu_current" href="javascript:document.forms[0]._eventId_Cancel.click();"><spring:message code="jsp.reportWizard.resources"/></a>  
  </c:when>
  <c:otherwise>
    <span class="wizard_menu_disabled"><spring:message code="jsp.reportWizard.resources"/></span>  
  </c:otherwise>
  </c:choose>
  </td></tr>
  <tr><td nowrap="true"><span class="wizard_menu_disabled"><spring:message code="jsp.reportWizard.dataSource"/></span></td></tr>
  <tr><td nowrap="true">
  <c:choose>
  <c:when test='${masterFlowStep == "query"}'>
    <a class="wizard_menu_current" href="javascript:document.forms[0]._eventId_Cancel.click();"><spring:message code="jsp.reportWizard.query"/></a>  
  </c:when>
  <c:otherwise>
    <span class="wizard_menu_disabled"><spring:message code="jsp.reportWizard.query"/></span>  
  </c:otherwise>
  </c:choose>
  </td></tr>
  <tr><td nowrap="true"><span class="wizard_menu_disabled"><spring:message code="jsp.reportWizard.customization"/></span></td></tr>
</table>
    </td>
</c:otherwise>
</c:choose>
</c:if>
<td>
<table align="center" cellspacing="1" cellpadding="0" border="0">

	<tr>
	  <td>&nbsp;</td>
	  <td colspan="2"><span class="fsection"><spring:message code="jsp.querySource.title"/></span></td>
	</tr>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>

	<spring:bind path="queryReference.source">
		<tr>
			<td>&nbsp;</td>
			<td><input name="${status.expression}" type="radio" value="CONTENT_REPOSITORY" id="CONTENT_REPOSITORY"
				<c:if test='${status.value == "CONTENT_REPOSITORY" or (not allowNone and status.value == "NONE" and not empty queryLookups)}'>checked="true"</c:if>
				<c:if test="${empty queryLookups}">disabled</c:if>
			></td>
			<td>
				<c:choose>
					<c:when test="${empty queryLookups}"><spring:message code="label.fromRepository"/></c:when>
					<c:otherwise><a href="#" onclick="javascript:document.forms[0].CONTENT_REPOSITORY.click();"><spring:message code="label.fromRepository"/></a></c:otherwise>
				</c:choose>
			</td>
		</tr>
	</spring:bind>
	<spring:bind path="queryReference.referenceURI">
		<tr>
			<td colspan="2">&nbsp;</td>
			<td>
				<select name="${status.expression}" size="1"<c:if test="${empty queryLookups}">disabled</c:if> class="fnormal">
					<c:forEach items='${queryLookups}' var='queryLookup'>
						<option value="${queryLookup.URIString}" <c:if test='${queryLookup.URIString==status.value}'>selected="true"</c:if>><c:out value="${queryLookup.URIString}"/></option>
					</c:forEach>
				<c:if test="${status.error!=null}"><BR><span class="ferror">${status.errorMessage}</span></c:if>
				</select>
			</td>
		</tr>
	</spring:bind>

	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>

	<spring:bind path="queryReference.source">
		<tr>
			<td>&nbsp;</td>
			<td><input type="radio" name="${status.expression}" value="LOCAL" id="LOCAL"
				<c:if test='${status.value=="LOCAL" or (not allowNone and status.value == "NONE" and empty queryLookups)}'>checked="true"</c:if>
			></td>
			<td><a href="#" onclick="javascript:document.forms[0].LOCAL.click();"><spring:message code="label.locallyDefined"/></a></td>
		</tr>
		<c:if test="${allowNone}">
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="radio" name="${status.expression}" value="NONE" id="NONE" <c:if test='${status.value=="NONE"}'>checked="true"</c:if>></td>
			<td><a href="#" onclick="javascript:document.forms[0].NONE.click();"><spring:message code="label.none"/></a></td>
		</tr>
		</c:if>
	</spring:bind>

	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>

	<tr>
		<td>&nbsp;</td>
		<td colspan="2">
		    <c:if test='${masterFlow == "reportUnit"}'>
		     <input type="submit" class="fnormal" name="_eventId_Cancel" value="<spring:message code="button.cancel"/>" />&nbsp;
		    </c:if>
		    
		    <c:if test='${masterFlow != "reportUnit"}'>
		     <input type="button" class="fnormal" name="_eventId_Cancel" value="<spring:message code="button.cancel"/>" OnClick='javascript:window.location.href="flow.html?_flowId=repositoryExplorerFlow&showFolder=<%=request.getParameter("ParentFolderUri")%>"'/>&nbsp;
		    </c:if>
			
			<input type="submit" class="fnormal" name="_eventId_Back" value="<spring:message code="button.back"/>"/>&nbsp;
			<input type="submit" class="fnormal" name="_eventId_Next" value="<spring:message code="button.next"/>"/>
			<c:if test='${parentFlow == "reportUnit"}'><input type="submit" class="fnormal" name="_eventId_Finish" value="<spring:message code="button.finish"/>"/></c:if>
		</td>
	</tr>
</table>

    <input type="hidden" name="jumpToPage">
    <input type="submit" class="fnormal" style="visibility:hidden;" value="" name="_eventId_Jump" id="jumpButton">   
	<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

</td>
</tr>
</table>
</FORM>
</body>
</html>
