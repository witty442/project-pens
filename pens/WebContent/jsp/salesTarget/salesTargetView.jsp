<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="page"></jsp:useBean>
<%

%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salestarget.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" style="height: 100%;">
<table id="maintab" align="center" border="0" cellpadding="0" cellspacing="0" style="buttom:0;" class="body">
	<tr>
		<td bgcolor="#FFFFFF"><jsp:include page="../header.jsp"/></td>
	</tr>
	<tr id="framerow">
		<td valign="top" align="center">
			<!-- INCLUDE -->
			<jsp:include page="../menu.jsp"/>
			<jsp:include page="../program.jsp">
				<jsp:param name="system" value="MasterData"/>
				<jsp:param name="function" value="SalesTarget"/>
				<jsp:param name="code" value="${salestargetForm.salesTarget.monthLabel}-${salestargetForm.salesTarget.year}"/>
			</jsp:include>
			<!-- BODY -->
			<html:form action="/jsp/salestargetAction">
			<jsp:include page="../error.jsp"/>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
				<tr>
					<td align="right" width="30%"><bean:message key="Year" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left" width="15%">
						<html:text property="salesTarget.year" readonly="true" styleClass="disableText"/>
					</td>
					<td width="20%" align="right"><bean:message key="Month" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="salesTarget.monthLabel" readonly="true" styleClass="disableText"/>
					</td>
				</tr>
				<tr>
					<td align="right"><bean:message key="MonthTarget" bundle="sysele"/>(<bean:message key="Product.UOM"  bundle="sysele"/>)&nbsp;&nbsp;</td>
					<td align="left" colspan="3">
						<html:text property="salesTarget.targetAmountLabel" readonly="true" style="text-align: right;" styleClass="disableText"/>
					</td>
				</tr>
			</table>
			<!-- RESULT -->
			<br/>
			<c:if test="${salestargetForm.salesTargetProduct != null}">
			<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
			&nbsp;<span class="searchResult">${salestargetForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="100%">
				<tr>
					<th class="order"><bean:message key="No" bundle="sysprop"/></th>
					<th class="name"><bean:message key="Product.Code" bundle="sysele"/></th>
					<th class="name"><bean:message key="Product.Name" bundle="sysele"/></th>
					<th class="name"><bean:message key="ProductTarget" bundle="sysele"/>(<bean:message key="Product.UOM"  bundle="sysele"/>)</th>
				</tr>
				<c:forEach var="results" items="${salestargetForm.salesTargetProduct}" varStatus="rows">
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
					<td align="left">${results.product.code}</td>
					<td align="left">${results.product.name}</td>
					<td align="right">
						<fmt:formatNumber pattern="#,##0.00" value="${results.targetAmount}"/>
					</td>	
				</tr>
				</c:forEach>
			</table>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">	
				<tr>
					<td align="left" colspan="10" class="footer">&nbsp;</td>
				</tr>
			</table>
			</c:if>
			<!-- BUTTON -->
			<br/>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="right">
						<a href="javascript:search('${pageContext.request.contextPath}')">
						<input type="button" value="ปิดหน้าจอ" class="newNegBtn"/>
						<!-- <img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"> --></a>
					</td>
					<td width="20%">&nbsp;</td>
				</tr>
			</table>
			<br><br>
			<jsp:include page="../searchCriteria.jsp"></jsp:include>
			</html:form>
			<!-- BODY -->
			<!-- END -->
		</td>
	</tr>
	<tr>
		<td bgcolor="#FFFFFF"><jsp:include page="../footer.jsp"/></td>
	</tr>
</table>
</body>
</html>