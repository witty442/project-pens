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
		<c:if test="${importForm.loadOnhandTVDirectListErrorSize > 0}">
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
				<tr>
					<th colspan="9"  align="left"> <font color="red">จำนวนรายการที่ไม่สามารถ import ได้  : ${importForm.loadOnhandTVDirectListErrorSize} รายการ </font> </th>
				</tr>
				<tr>
				    <th width="5%">Row</th>
					<th width="8%">materialMaster</th>
					<th width="8%">barcode </th>
					<th width="10%">onhandQty </th>
					<th width="10%">wholePriceBF </th>
					<th width="10%">retailPriceBF </th>
					<th width="10%">item </th>
					<th width="15%">itemDesc </th>
					<th width="24%">Message </th>
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
						<td align="left">${results.onhandSummary.materialMaster}</td>
						<td align="left">${results.onhandSummary.barcode}</td>
						<td align="left">${results.onhandSummary.onhandQty}</td>
						<td align="left">${results.onhandSummary.wholePriceBF}</td>
						<td align="left">${results.onhandSummary.retailPriceBF}</td>
						<td align="left">${results.onhandSummary.item}</td>
						<td align="left">${results.onhandSummary.itemDesc}</td>
						<td align="left">
						  <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
						    ${rows2.index}:${results2.message}<br/>
						 </c:forEach>
						</td>
					</tr>
				</c:forEach>
				</table>
		</c:if>
		
         <c:if test="${importForm.loadOnhandTVDirectListSuccessSize > 0}">
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
			<tr>
				<th colspan="9"  align="left">จำนวนรายการที่สามารถ import ได้  : ${importForm.loadOnhandTVDirectListSuccessSize} รายการ  </th>
			</tr>
			<tr>
			    <th width="5%">Row</th>
				<th width="8%">materialMaster</th>
				<th width="8%">barcode </th>
				<th width="10%">onhandQty </th>
				<th width="10%">wholePriceBF </th>
				<th width="10%">retailPriceBF </th>
				<th width="10%">item </th>
				<th width="15%">itemDesc </th>
				<th width="24%">Message </th>
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
					<td align="left">${results.onhandSummary.materialMaster}</td>
					<td align="left">${results.onhandSummary.barcode}</td>
					<td align="left">${results.onhandSummary.onhandQty}</td>
					<td align="left">${results.onhandSummary.wholePriceBF}</td>
					<td align="left">${results.onhandSummary.retailPriceBF}</td>
					<td align="left">${results.onhandSummary.item}</td>
					<td align="left">${results.onhandSummary.itemDesc}</td>
					<td align="left">
					 <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
					    ${rows2.index}:${results2.message}<br/>
					 </c:forEach>
					</td>
				</tr>
			</c:forEach>
			</table>
		</c:if>
						
</body>
</html>