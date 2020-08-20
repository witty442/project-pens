<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
//Id = order_id
String id = (String)request.getParameter("id");
User user = (User) request.getSession(true).getAttribute("user");

List<ReceiptLine> rls = new MReceiptLine().lookUpOutstanding(Integer.parseInt(id),user);
pageContext.setAttribute("rls",rls,PageContext.PAGE_SCOPE);

%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.ReceiptLine"%>
<%@page import="com.isecinc.pens.model.MReceiptLine"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
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
<html:form action="/jsp/tempAction">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value="InvoiceOutstanding"/>
	<jsp:param name="code" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%" class="result">
	<tr>
		<th><bean:message key="No" bundle="sysprop"/></th>
		<th><bean:message key="DocumentNo" bundle="sysele"/></th>
		<th><bean:message key="Order.No" bundle="sysele"/></th>
		<th><bean:message key="Bill.No" bundle="sysele"/></th>
		<th><bean:message key="TotalAmount"  bundle="sysele"/></th>
		<th><bean:message key="Order.Paid" bundle="sysele"/></th>
		<th><bean:message key="Order.Behindhand" bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${rls}" varStatus="rows">
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
			<td align="center">${results.order.orderNo}</td>
			<td align="center">${results.order.salesOrderNo}</td>
			<td align="center">${results.order.arInvoiceNo}</td>
			<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.invoiceAmount}"/></td>
			<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.paidAmount}"/></td>
			<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.creditAmount}"/></td>
		</tr>
	</c:forEach>
	<tr>
		<td align="left" colspan="7" class="footer">&nbsp;</td>
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