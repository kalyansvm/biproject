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

<%@ page contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/spring" prefix="spring"%>
<html>
<head>
  <title><spring:message code='jsp.menutest.title'/></title>
</head>
<body bgcolor=white>

<spring:message code='jsp.menutest.menu'/> <br/>

<!-- there has to be a better way to pass object params... -->
<% request.setAttribute("indent", 0); %>
<% request.setAttribute("menu", com.jaspersoft.jasperserver.war.common.SiteMenu.instance().getMenu()); %>
<jsp:include page="menutestr.jsp" flush="true"/>

</body>
</html>