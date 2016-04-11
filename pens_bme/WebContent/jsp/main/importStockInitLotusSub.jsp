<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<jsp:useBean id="importForm" class="com.isecinc.pens.web.imports.ImportForm" scope="session" />
<%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script>

</script>
</head>
	<c:if test="${importForm.loadStockInitListSuccessSize > 0}">
	   <table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	       <tr>
			    <td align="left"><font size="3">Count Date :${importForm.countDate}</font></td>
			</tr>
			 <tr>
				<td align="left"><font size="3">Store Code :${importForm.storeCode} -${importForm.storeName}</font></td>
			</tr>
			<tr>
				<td align="left"><font size="3">Total QTY:${importForm.totalQty} </font></td>
			</tr>
		</table>
	</c:if>
		
		<c:if test="${importForm.loadStockInitListErrorSize > 0}">
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
			<tr>
				<th colspan="7"  align="left"><font color="red">จำนวนรายการที่ไม่สามารถ import ได้   ${importForm.loadStockInitListErrorSize} รายการ </font></th>
			</tr>
			<tr>
				<th width="5%">Row In Excel</th>
				<th width="8%">Group Code</th>
				<th width="10%">MaterialMaster</th>
				<th width="10%">Barcode</th>
				<th width="8%">Pens Item</th>
				<th width="5%">QTY</th>
				<th width="15%">Message </th>
			</tr>
			<c:forEach var="results" items="${importForm.summaryErrorList}" varStatus="rows">
				<c:choose>
					<c:when test="${rows.index %2 == 0}">
						<c:set var="tabclass" value="lineO" />
					</c:when>
					<c:otherwise>
						<c:set var="tabclass" value="lineE" />
					</c:otherwise>
				</c:choose>
				<tr class="<c:out value='${tabclass}'/>">
					<td>${results.row}</td>
					<td align="left">${results.groupCode}</td>
					<td align="left">${results.materialMaster}</td>
					<td align="left">${results.barcode}</td>
					<td align="left">${results.pensItem}</td>
					<td align="right">${results.qty}</td>
					<td align="left">${results.message}</td>
				</tr>
			</c:forEach>
			</table>
		</c:if>
		
         <c:if test="${importForm.loadStockInitListSuccessSize > 0}">
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
			<tr>
				<th colspan="7"  align="left">จำนวนรายการที่สามารถ import ได้   ${importForm.loadStockInitListSuccessSize} รายการ</th>
			</tr>
			<tr>
				<th width="5%">Row In Excel</th>
				<th width="8%">Group Code</th>
				<th width="10%">MaterialMaster</th>
				<th width="10%">Barcode</th>
				<th width="8%">Pens Item</th>
				<th width="5%">QTY</th>
				<th width="15%">Message </th>
			</tr>
			<c:forEach var="results" items="${importForm.summarySuccessList}" varStatus="rows">
				<c:choose>
					<c:when test="${rows.index %2 == 0}">
						<c:set var="tabclass" value="lineO" />
					</c:when>
					<c:otherwise>
						<c:set var="tabclass" value="lineE" />
					</c:otherwise>
				</c:choose>
				<tr class="<c:out value='${tabclass}'/>">
					<td>${results.row}</td>
					<td align="left">${results.groupCode}</td>
					<td align="left">${results.materialMaster}</td>
					<td align="left">${results.barcode}</td>
					<td align="left">${results.pensItem}</td>
					<td align="right">${results.qty}</td>
					<td align="left">${results.message}</td>
				</tr>
			</c:forEach>
			</table>
		</c:if>
						
</body>
</html>