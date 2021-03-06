<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
String lineId = request.getParameter("lineId");
MModifierLine mLine = new MModifierLine();
ModifierLine line = mLine.find(lineId);
//System.out.println(line);
pageContext.setAttribute("lines",line,PageContext.PAGE_SCOPE);

%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="com.isecinc.pens.model.MModifierLine"%>
<%@page import="com.isecinc.pens.bean.ModifierLine"%><head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<br>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th><bean:message key="Modifier.AppMethod" bundle="sysele"/></th>
		<th><bean:message key="Modifier.Vals" bundle="sysele"/></th>
	</tr>
	<tr class="lineO">
		<td align="center">${lines.applicationMethod}</td>
		<td align="right">${lines.values}</td>
	</tr>
	<tr>
		<td align="left" colspan="2" class="footer">&nbsp;</td>
	</tr>
</table>
<br>
<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
	<tr>
		<td width="80%" align="right">
			<a href="#" onclick="window.close();">
			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"></a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</body>