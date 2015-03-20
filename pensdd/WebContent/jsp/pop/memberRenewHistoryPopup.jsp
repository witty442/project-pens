<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
String id = (String)request.getParameter("id");

List<MemberRenew> renews = new MMemberRenew().lookUp(Integer.parseInt(id));
pageContext.setAttribute("renews",renews,PageContext.PAGE_SCOPE);

%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.MemberRenew"%>
<%@page import="com.isecinc.pens.model.MMemberRenew"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(path){
}

</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<!-- BODY -->
<html:form action="/jsp/memberRenewAction">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="MasterData"/>
	<jsp:param name="function" value="RenewHistory"/>
	<jsp:param name="code" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%" class="result">
	<tr>
		<th><bean:message key="No" bundle="sysprop"/></th>
		<th><bean:message key="LastRenewDate" bundle="sysele"/></th>
		<th><bean:message key="Member.ExpireDate" bundle="sysele"/></th>
		<th><bean:message key="RenewDate" bundle="sysele"/></th>
		<th><bean:message key="RenewedAgain" bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${renews}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE"/>
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td><c:out value='${rows.index+1}'/></td>
			<td>${results.appliedDate}</td>
			<td>${results.expiredDate}</td>
			<td>${results.renewedDate}</td>
			<td>${results.memberTypeLabel}</td>
		</tr>
	</c:forEach>
	<tr>
		<td align="left" colspan="5" class="footer">&nbsp;</td>
	</tr>
</table>
<br><br>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>