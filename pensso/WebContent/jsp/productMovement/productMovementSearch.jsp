<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%

%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="inventoryTransactionForm" class="com.isecinc.pens.web.inventorytransaction.InventoryTransactionForm" scope="request" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inventorytransaction.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('movementDateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('movementDateTo'));
}
</script>
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" style="height: 100%;" onload="loadMe();">
<table id="maintab" align="center" border="0" cellpadding="0" cellspacing="0" style="buttom:0;" class="body">
	<tr>
		<td bgcolor="#FFFFFF"><jsp:include page="../header.jsp"/></td>
	</tr>
	<tr id="framerow">
		<td valign="top" align="center">
			<!-- INCLUDE -->
			<jsp:include page="../menu.jsp"/>
			<jsp:include page="../program.jsp">
				<jsp:param name="system" value="Transaction"/>
				<jsp:param name="function" value="ProductMovement"/>
				<jsp:param name="code" value=""/>
			</jsp:include>
			<!-- BODY -->
			<html:form action="/jsp/inventoryTransactionAction">
			<jsp:include page="../error.jsp"/>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="right" width="40%"><bean:message key="DateFrom" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="inventoryTransaction.movementDateFrom" styleId="movementDateFrom" readonly="true"/>
					</td>
				</tr>
				<tr>
					<td align="right"><bean:message key="DateTo" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:text property="inventoryTransaction.movementDateTo" styleId="movementDateTo" readonly="true"/>
					</td>
				</tr>
				<tr>
					<td width="40%" align="right"><bean:message key="User.Category" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:select property="inventoryTransaction.transactionType" style="width: 120px;">
							<html:option value=""></html:option>
							<html:option value="โอน">โอน</html:option>
							<html:option value="ขาย">ขาย</html:option>
						</html:select>
					</td>
				</tr>
			</table>
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="right">
						<a href="javascript:search('${pageContext.request.contextPath}')">
						<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn"></a>
						<a href="javascript:clearForm('${pageContext.request.contextPath}')">
						<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn"></a>
					</td>
					<td width="20%">&nbsp;</td>
				</tr>
			</table>
			<!-- RESULT -->
			<c:if test="${inventoryTransactionForm.results != null}">
			<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
			&nbsp;<span class="searchResult">${inventoryTransactionForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
				<tr>
					<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
					<th class="code"><bean:message key="Date"  bundle="sysele"/></th>
					<th class="code"><bean:message key="Product.Code"  bundle="sysele"/></th>
					<th class="name"><bean:message key="Product.Name"  bundle="sysele"/></th>
					<th class="th"><bean:message key="Product.UOM"  bundle="sysele"/></th>
					<th class="th"><bean:message key="User.Category"  bundle="sysele"/></th>
					<th class="th"><bean:message key="Amount"  bundle="sysele"/></th>
				</tr>
				<c:forEach var="results" items="${inventoryTransactionForm.results}" varStatus="rows">
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
					<td align="left">${results.movementDate}</td>
					<td align="left">${results.product.code}</td>
					<td align="left">${results.product.name}</td>
					<td align="center">${results.uom.name}</td>
					<td align="center">${results.transactionType}</td>
					<td align="right">${results.qty}</td>
				</tr>
				</c:forEach>
			</table>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">	
				<tr>
					<td align="left" colspan="10" class="footer">&nbsp;</td>
				</tr>
			</table>
			<br>
			</c:if>
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="right">
						<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'"><img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"></a>
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