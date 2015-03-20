<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%
	String role = ((User)session.getAttribute("user")).getType();
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="com.isecinc.pens.bean.User"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/login.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript">
</script>
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" style="height: 100%;">
<table id="maintab" align="center" border="1" cellpadding="0" cellspacing="0" style="buttom:0;" class="body">
	<tr height="111px;">
		<td bgcolor="#FFFFFF"><jsp:include page="../header.jsp"/></td>
	</tr>
	<tr id="framerow">
		<td valign="top" align="center">
			<jsp:include page="menu.jsp"/>
			<%if(role.equals(User.DD)){ %>
				<br><br><br>
				<jsp:include page='../jsp/member/memberRenewView.jsp'/>
			<%} %>
		</td>
	</tr>
	<tr height="111px;">
		<td bgcolor="#FFFFFF"><jsp:include page="../footer.jsp"/></td>
	</tr>
</table>
</body>
</html>