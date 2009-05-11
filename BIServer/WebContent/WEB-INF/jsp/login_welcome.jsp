<%--
* Copyright (C) 2005 - 2007 JasperSoft Corporation. All rights reserved.
* http://www.jaspersoft.com.
* Licensed under commercial JasperSoft Subscription License Agreement
--%>

<%@ page import="
        com.jaspersoft.jasperserver.war.dto.StringOption,
        com.jaspersoft.jasperserver.war.common.UserLocale
        " %>

<%@ taglib prefix="spring" uri="/spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
  <title><spring:message code='jsp.Login.title'/></title>
  <link href="${pageContext.request.contextPath}/stylesheets/stylesheet.css" rel="stylesheet" type="text/css">
 <link href="${pageContext.request.contextPath}/stylesheets/dialog.css" rel="stylesheet" type="text/css">
  <meta name="noMenu" content="true">
  <meta name="pageHeading" content="<spring:message code='jsp.Login.pageHeading'/>"/>
 <script language=JavaScript src="${pageContext.request.contextPath}/scripts/jasperserver.js"></script>
 <script language=JavaScript src="${pageContext.request.contextPath}/scripts/common.js"></script>

<% response.setHeader("LoginRequested","true");
   session.removeAttribute("js_uname");
   session.removeAttribute("js_upassword");
%>

<script src="scripts/common.js"></script>

</head>

<body>
<!-- $Id$ -->
<form name="fmLogin" method="POST" action="j_acegi_security_check" onSubmit="return (validatePassword());">
<table width="100%"  height="96%" bgcolor="#FFFFFF" border="0" >
<tr height="15">
 <td align="right">
 </td>
</tr>
<tr>
<td valign="top">
<table width="1024" bgcolor="#FFFFFF" border=0 align="center">
<tr>
<td valign="top" align="center" width="60%">
<table cellpadding="0" cellspacing="0" width="80%" border="0" >
  <tr valign="top">
    <td align="left" valign="top">


                <table border="0" cellpadding="0" cellspacing="0" style="width:340px">
                    <tbody>
                        <tr>
                          <td width="1" height="1"><img src="images/top_left.PNG"></td>
                          <td bgcolor="#f1f3f4" colspan="2" >&nbsp;</td>
                          <td width="1" height="1"><img src="images/top_right.PNG"></td>
                        </tr>
                        <tr>
                          <td width="1" bgcolor="#f1f3f4"></td>
                          <td bgcolor="#f1f3f4" colspan="2" nowrap align="middle" class="welcome_login"><spring:message code='LOGIN_WELCOME_OS'/></td>
                          <td width="1" bgcolor="#f1f3f4"></td>
                        </tr>
                        <tr>
                          <td width="1" bgcolor="#f1f3f4"></td>
                          <td bgcolor="#f1f3f4" colspan="2" align="left">&nbsp;</td>
                          <td width="1" bgcolor="#f1f3f4"></td>
                        </tr>
                        <tr>
                          <td width="1" bgcolor="#f1f3f4"></td>
                          <td bgcolor="#f1f3f4" colspan="2" align="left" nowrap>
                               &nbsp;
                          </td>
                          <td width="1" bgcolor="#f1f3f4"></td>
                        </tr>
                        <tr>
                          <td width="1" bgcolor="#f1f3f4"></td>
                          <td bgcolor="#f1f3f4" align="left" nowrap width="1%">
                                <spring:message code='LOGIN_SIGN_IN_AS'/> -
                          </td>
                          <td bgcolor="#f1f3f4" align="left">
                           <span class="user_login">biadmin</span>&nbsp;<spring:message code='LOGIN_ADMIN_USER'/>
                          </td>
                          <td width="1" bgcolor="#f1f3f4"></td>
                        </tr>
                        <tr>
                          <td width="1" bgcolor="#f1f3f4"></td>
                          <td bgcolor="#f1f3f4" align="left" nowrap width="1%">
                          </td>
                          <td bgcolor="#f1f3f4" height="25" align="left">
                          <span class="user_login"> biuser/biuser</span>&nbsp;<spring:message code='LOGIN_JOEUSER'/>
                          </td>
                          <td width="1" bgcolor="#f1f3f4"></td>
                        </tr>
                        <tr>
                          <td width="1" height="1"><img src="images/bottom_left.PNG"></td>
                          <td bgcolor="#f1f3f4" colspan="2">&nbsp;</td>
                          <td width="1" height="1"><img src="images/bottom_right.PNG"></td>
                        </tr>

                        <tr height="40">
                          <td></td>
                          <td colspan="2">&nbsp;</td>
                          <td></td>
                        </tr>

                        <tr>
                          <td></td>
                          <td  colspan="2">&nbsp;</td>
                          <td></td>
                        </tr>

                        <tr>
                          <td colspan="4" class="bb_line">&nbsp;</td>
                        </tr>

                    </tbody>
                </table>

        </td>
    </tr>
</table>
</td>
<td valign="top" style="padding-top:40px;" >

<!--  login section starts here -->

<table border="0" cellspacing="0">

<tr>
  <td valign="top" style="background-image: url(images/login-top-left.png); background-repeat: no-repeat;"></td>
  <td valign="top" style="background-image: url(images/login-top-middle.png); background-repeat: repeat;width:30px;" height="24">&nbsp;</td>
  <td valign="top" style="background-image: url(images/login-top-middle.png); background-repeat: repeat;width:170px;">&nbsp;</td>
  <td valign="top" style="background-image: url(images/login-top-right.png); background-repeat: no-repeat;"> </td>
</tr>

<tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                                                                                                                <td colspan=2 style="background-image: url(images/login-center.png); background-repeat: repeat;text-align:center;"><span class="fsection"><spring:message code='jsp.Login.section'/></span></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;">&nbsp;</td>
                            <td style="padding-right:5px;background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><spring:message code='jsp.Login.username'/>:</td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><input type="text" name="j_username" value="" size="20" maxlength="100" class="control"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><spring:message code='jsp.Login.password'/>:</td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><input type="password" name="j_password" value="" size="20" maxlength="100" class="control"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>

                        <c:if test="${paramValues.error != null}">
                        <c:choose>
                            <c:when test="${exception!=null}">
                              <tr><td colspan="4"><img src="images/login-center-left.png">${exception}</td></tr>
                            </c:when>
                            <c:otherwise>
                                  <tr>
                                    <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                                    <td colspan=2 align="center" class="ferror" style="background-image: url(images/login-center.png); background-repeat: repeat;font-size:10px"><spring:message code='jsp.loginError.invalidCredentials1'/><br/><spring:message code='jsp.loginError.invalidCredentials2'/>
                                    <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </c:if>
                        
                        
                        <% if ("true".equals(request.getParameter("showPasswordChange"))) { %>
                         <tr>
                          <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                          <td colspan=2 align="center" class="ferror" style="background-image: url(images/login-center.png); background-repeat: repeat;font-size:10px"><spring:message code='jsp.loginError.expiredPassword1'/><br/><spring:message code='jsp.loginError.expiredPassword2'/>
                          <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                          </td>
                         </tr>                       
                        <% } %>

                        <tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;">&nbsp;</td>
                            <td align="left" style="padding-top:5px;background-image: url(images/login-center.png); background-repeat: repeat;"><a id="showHideLocaleLink" href="#" onclick="toggleLocaleDetails();return false;" class="nonbold"><spring:message code="jsp.Login.link.showLocale"/></a></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>

                    </tbody>


                    <tbody id="localeDetails" style="display:none">
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td align="right" style="background-image: url(images/login-center.png); background-repeat: repeat;" valign="middle" style="padding-top:5px;" nowrap="true"></td>
                            <td style="text-align:left;padding-right:5px;padding-top:5px;background-image: url(images/login-center.png); background-repeat: repeat;"><spring:message code='jsp.Login.locale'/>:
                                                                                                               </td>
                                                                                                               <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                                                                                                </tr>
                                                                                                <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td align="right" style="background-image: url(images/login-center.png); background-repeat: repeat;" valign="middle" nowrap="true"></td>
                            <td style="text-align:left;padding-right:5px;background-image: url(images/login-center.png); background-repeat: repeat;">
                                <select name="userLocale" class="control" style="width:220px">
                                    <c:forEach items="${userLocales}" var="locale">
                                        <option value="${locale.code}" <c:if test="${preferredLocale == locale.code}">selected</c:if>>
                                            <spring:message code="locale.option"
                                                arguments='<%= new String[]{((UserLocale) pageContext.getAttribute("locale")).getCode(), ((UserLocale) pageContext.getAttribute("locale")).getDescription()} %>'/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </td>
                                                                                                               <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td align="right" valign="middle" style="padding-top:5px;background-image: url(images/login-center.png); background-repeat: repeat;" nowrap="true"></td>
                            <td style="text-align:left;padding-right:5px;padding-top:4px;background-image: url(images/login-center.png); background-repeat: repeat;"><spring:message code='jsp.Login.timezone'/>:</td>
                                                                                                                <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td align="right" valign="middle" style="background-image: url(images/login-center.png); background-repeat: repeat;" nowrap="true"></td>
                            <td style="text-align:left;background-image: url(images/login-center.png); background-repeat: repeat;">
                                <select name="userTimezone" class="control" style="width:220px">
                                    <c:forEach items="${userTimezones}" var="timezone">
                                        <option value="${timezone.code}" <c:if test="${preferredTimezone == timezone.code}">selected</c:if>>
                                            <spring:message code="timezone.option"
                                                arguments='<%= new String[]{((StringOption) pageContext.getAttribute("timezone")).getCode(), ((StringOption) pageContext.getAttribute("timezone")).getDescription()} %>'/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </td>
                                                                                                                <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                    </tbody>

                    <c:if test="${allowUserPasswordChange eq 'true'}">
                    <tbody>
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;">&nbsp;</td>
                            <td align="left" style="padding-top:5px;background-image: url(images/login-center.png); background-repeat: repeat;"><a id="showHideChangePasswordLink" href="#" onclick="togglePasswordDetails();return false;" class="nonbold"><spring:message code="jsp.Login.link.changePassword"/></a></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                    </tbody>
                    <tbody id="passwordDetails" style="display:none">
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;" align="right" nowrap="true"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><spring:message code='jsp.Login.link.newPassaowrd'/>:</td>
                             <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;" align="right" nowrap="true"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><input type="text" name="j_newpassword1" id="j_newpassword1" value="" size="20" maxlength="100" class="control"></td>
                             <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;" align="right" nowrap="true"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><spring:message code='jsp.Login.link.repeatNewPassword'/>:</td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                                <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;" align="right" nowrap="true"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"><input type="text" name="j_newpassword2" id="j_newpassword2"  value="" size="20" maxlength="100" class="control"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                    </tbody>
                    </c:if>
                    <tbody>
                        <tr style="height:7px">
                            <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                                                                                                                <td style="background-image: url(images/login-center.png); background-repeat: repeat;"></td>
                            <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                        </tr>
                        <tr>
                                                                                                 <td style="background-image: url(images/login-center-left.png); background-repeat: repeat;"></td>
                         <td  colspan="2" align="center" style="padding-top:10px;padding-right:15px;background-image: url(images/login-center.png); background-repeat: repeat;" nowrap="true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" name="btnsubmit" value="<spring:message code='jsp.Login.button.login'/>" style="background-color:#F1F3F4;border:solid 1px #993200;padding-top:2px;padding-bottom:2px;font-family:Verdana,Arial,sans-serif;font-size:11px">&nbsp;&nbsp;&nbsp;<input type="reset" name="btnreset" value="<spring:message code='jsp.Login.button.reset'/>" style="background-color:#F1F3F4;border:solid 1px #993200;padding-top:2px;padding-bottom:2px;font-family:Verdana,Arial,sans-serif;font-size:11px"></td>
                                     <td style="background-image: url(images/login-center-right.png); background-repeat: repeat;"></td>
                                                                                </tr>
</td>
</tr>


<tr>
  <td  style="background-image: url(images/login-bottom-left.png); background-repeat: no-repeat;" width="18">&nbsp;</td>
  <td  style="background-image: url(images/login-bottom-middle.png); background-repeat: repeat;" height="24">&nbsp;</td>
 <td  style="background-image: url(images/login-bottom-middle.png); background-repeat: repeat;"> </td>
  <td style="background-image: url(images/login-bottom-right.png); background-repeat: no-repeat;" width="18">&nbsp; </td>
</tr>


</table>
</td>
</tr>


</table>
</td>
</tr>
<tr>
<td valign="bottom" style="padding-bottom:10px;padding-right:15px;">

</td>
</tr>
</table>
<input type="hidden" name="passwordExpiredDays" value="${passwordExpirationInDays}" />
</form>

<script language="Javascript">
    document.fmLogin.j_username.focus();
</script>

<script>
    var localeShowing=false;
    var passwordShowing=false;
    var localeDetails = document.getElementById("localeDetails");
    var passwordDetails = document.getElementById("passwordDetails");
    var showHideLocaleLink = document.getElementById("showHideLocaleLink");
    var showHideChangePasswordLink = document.getElementById("showHideChangePasswordLink");

    var tBodyDisplay = "block";
  if (navigator.appName=="Netscape")
        tBodyDisplay = "table-row-group";

    function toggleLocaleDetails() {
        if(localeShowing) {
            localeShowing = false;
            localeDetails.style.display="none";
            showHideLocaleLink.style.fontWeight="normal";
            updateLocaleLink("<spring:message code='jsp.Login.link.showLocale'/>");
        } else {
            localeShowing = true;
            localeDetails.style.display=tBodyDisplay;
            showHideLocaleLink.style.fontWeight="bold";
            updateLocaleLink("<spring:message code='jsp.Login.link.hideLocale'/>");
            document.fmLogin.userLocale.focus();
        }

    }

    function togglePasswordDetails() {

        var p1 = document.getElementById("j_newpassword1");
        var p2 = document.getElementById("j_newpassword2");

        if(passwordShowing) {
            passwordShowing = false;
            passwordDetails.style.display="none";
            var textField1 = createPField("text", "j_newpassword1");
            var textField2 = createPField("text", "j_newpassword2");
            p1.parentNode.replaceChild(textField1, p1);
            p2.parentNode.replaceChild(textField2, p2);
            showHideChangePasswordLink.style.fontWeight="normal";
            updatePasswordLink("<spring:message code='jsp.Login.link.changePassword'/>");
        } else {
            passwordShowing = true;
            var passwordField1 = createPField("password", "j_newpassword1");
            var passwordField2 = createPField("password", "j_newpassword2");
            p1.parentNode.replaceChild(passwordField1, p1);
            p2.parentNode.replaceChild(passwordField2, p2);
            passwordDetails.style.display=tBodyDisplay;
            showHideChangePasswordLink.style.fontWeight="bold";
            updatePasswordLink("<spring:message code='jsp.Login.link.cancelPassword'/>");
            document.fmLogin.j_newpassword1.focus();
        }

    }


    function updateLocaleLink(newText) {
            showHideLocaleLink.innerText?showHideLocaleLink.innerText=newText:showHideLocaleLink.innerHTML=newText;
    }

    function updatePasswordLink(newText) {
            showHideChangePasswordLink.innerText?showHideChangePasswordLink.innerText=newText:showHideChangePasswordLink.innerHTML=newText;
    }

    function validatePassword() {
              var p1 = document.getElementById("j_newpassword1");
              var p2 = document.getElementById("j_newpassword2");

              if (p1 == null) {
                 return true;
              }

              if (passwordDetails.style.display == tBodyDisplay) {

                  if (p1.value.replace(/^\s+|\s+$/g, "") == "") {
                     alert("<spring:message code='jsp.Login.link.nonEmptyPassword'/>");
                     return false;
                  }

                  if (p1.value != p2.value) {
                     alert("<spring:message code='jsp.Login.link.passwordNotMatch'/>");
                     return false;
                  }
              } else {
                  p1.value = "";
                  p2.value = "";
              }
              return true;

    }

    function createPField(type, id) {
       element = document.createElement("input");
       element.setAttribute("type", type);
       element.setAttribute("name", id);
       element.setAttribute("id", id);
       element.setAttribute("value", "");
       element.setAttribute("size", "20");
       element.setAttribute("maxlength", "100");
       element.setAttribute("class", "control");
       return element;
    }


    var aboutPanel = document.getElementById('about');
    function launchAboutDlg() {
       aboutPanel.style.display="block";
       centerLayer(aboutPanel);
    }

    function hideAboutDlg() {
       aboutPanel.style.display="none";
    }

    <% if ("true".equals(request.getParameter("showPasswordChange"))) { %>
       togglePasswordDetails();
    
    <% } %>


</script>


</body>

</html>
