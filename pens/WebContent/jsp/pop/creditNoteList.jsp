<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%
String invoiceNo = request.getParameter("invoiceNo");

User user = (User) session.getAttribute("user");
List<CreditNote> creditNoteL = new ArrayList<CreditNote>();

creditNoteL = new MCreditNote().lookUp(invoiceNo);
pageContext.setAttribute("creditNoteL",creditNoteL,PageContext.PAGE_SCOPE);
%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.CreditNote"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MCreditNote"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.model.MReceiptCN"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop"
		key="<%=SystemProperties.PROJECT_NAME %>" /></title>
<link rel="StyleSheet"
	href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet"
	href="${pageContext.request.contextPath}/css/webstyle.css"
	type="text/css" />
<style type="text/css">
<!--
.style1 {
	color: #004a80
}
-->
</style>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery.js"></script>
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0"
	class="popbody">
	<jsp:include page="../program.jsp">
		<jsp:param name="system" value="" />
		<jsp:param name="function" value="" />
		<jsp:param name="code" value="" />
	</jsp:include>
	<table align="center" border="0" cellpadding="0" cellspacing="1"
		width="100%" class="result">
		<tr>
			<th><bean:message key="CreditNote.No" bundle="sysele" /></th>
			<th><bean:message key="CreditNote.Date" bundle="sysele" /></th>
			<th><bean:message key="CreditNote.Amount" bundle="sysele" /></th>
		</tr>
		<c:forEach var="results" items="${creditNoteL}" varStatus="rows">
			<c:choose>
				<c:when test="${rows.index %2 == 0}">
					<c:set var="tabclass" value="lineO" />
				</c:when>
				<c:otherwise>
					<c:set var="tabclass" value="lineE" />
				</c:otherwise>
			</c:choose>
			<tr class="<c:out value='${tabclass}'/>">
				<td align="center">${results.creditNoteNo}</td>
				<td>${results.documentDate}</td>
				<td><fmt:formatNumber pattern="#,##0.00"
						value="${results.totalAmount}" />
				</td>
			</tr>
		</c:forEach>
	</table>
	<table align="center" border="0" cellpadding="10" cellspacing="1"
		width="100%">
		<tr>
			<td>&nbsp;</td>
			<td align="right"><a href="#" onclick="window.close();"> <input
					type="button" value="»Ô´" class="newNegBtn"> </a>
			</td>
		</tr>
	</table>
</body>
</html>