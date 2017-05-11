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
<body>
		<c:if test="${importForm.imported == true}">
             <table align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
				<tr>
					<th colspan="7"  align="left">�ӹǹ��¡�÷�����  ${importForm.totalSize} ��¡��</th>
				</tr>
			</table>
		</c:if>
						
						<!-- ************************* Transaction From Lotus**************************************** -->
						<c:if test="${importForm.summaryLotusErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left"><font color="red">�ӹǹ��¡�÷���������ö import ��   ${importForm.summaryLotusErrorSize} ��¡�� </font></th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
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
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
                        <c:if test="${importForm.summaryLotusSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left">�ӹǹ��¡�÷������ö import ��   ${importForm.summaryLotusSuccessSize} ��¡��</th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
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
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<!-- ************************* Transaction From Lotus**************************************** -->
						
						<!-- ************************* Transaction From BigC**************************************** -->
						<c:if test="${importForm.summaryBigCErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left"><font color="red">�ӹǹ��¡�÷���������ö import ��   ${importForm.summaryBigCErrorSize} ��¡�� </font></th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
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
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
                        <c:if test="${importForm.summaryBigCSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="7"  align="left">�ӹǹ��¡�÷������ö import ��   ${importForm.summaryBigCSuccessSize} ��¡��</th>
							</tr>
							<tr>
								<th width="5%">Row In Excel</th>
								<th width="10%">Sales Date</th>
								<th width="5%">Store No</th>
								<th width="25%">Store Name</th>
								<th width="30%">Description</th>
								<th width="5%">QTY</th>
								<th width="25%">Message </th>
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
									<td align="center">${results.salesDate}</td>
									<td align="left">${results.storeNo}</td>
									<td align="left">${results.storeName}</td>
									<td align="left">${results.description}</td>
									<td align="right">${results.qty}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<!-- ************************* Transaction From BigC**************************************** -->
						
						<!-- ************************* Physical From ******************************************* -->
						<c:if test="${importForm.summaryPhyListErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left"> <font color="red">�ӹǹ��¡�÷���������ö import ��  : ${importForm.summaryPhyListErrorSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Message </th>
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
									<td align="left">${results.description}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<c:if test="${importForm.summaryPhyListSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left">�ӹǹ��¡�÷������ö import ��  : ${importForm.summaryPhyListSuccessSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Error Message </th>
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
									<td align="left">${results.description}</td>
									<td align="left">${results.message}</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<!-- ************************* Physical From ******************************************* -->
						
						
						<!-- ************************* Wacoal and friday From ******************************************* -->
						<c:if test="${importForm.summaryWacoalListErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="9"  align="left"> <font color="red">�ӹǹ��¡�÷���������ö import ��  : ${importForm.summaryWacoalListErrorSize} ��¡�� </font> </th>
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
						
						<c:if test="${importForm.summaryWacoalListSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="9"  align="left">�ӹǹ��¡�÷������ö import ��  : ${importForm.summaryWacoalListSuccessSize} ��¡��  </th>
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
						
						<!-- ************************* Wacoal From ******************************************* -->
	
	                   <!-- ************************* O SHOPPING From ******************************************* -->
						<c:if test="${importForm.shoppingListErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="9"  align="left"> <font color="red">�ӹǹ��¡�÷���������ö import ��  : ${importForm.shoppingListErrorSize} ��¡�� </font> </th>
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
						
						<c:if test="${importForm.shoppingListSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="9"  align="left">�ӹǹ��¡�÷������ö import ��  : ${importForm.shoppingListSuccessSize} ��¡��  </th>
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
						
						<!-- ************************* OSHOPPING From ******************************************* -->
						
	                   <!-- ************************* Return Wacoal  From ******************************************* -->
						<c:if test="${importForm.summaryReturnWacoalListErrorSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left"> <font color="red">�ӹǹ��¡�÷���������ö import ��  : ${importForm.summaryReturnWacoalListErrorSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Message </th>
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
									<td align="left">
									 <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
									    ${rows2.index}:${results2.message}<br/>
									 </c:forEach>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<c:if test="${importForm.summaryReturnWacoalListSuccessSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left">�ӹǹ��¡�÷������ö import ��  : ${importForm.summaryReturnWacoalListSuccessSize} ��¡�� </font> </th>
							</tr>
							<tr>
							    <th width="5%">Row In Excel</th>
								<th width="30%">Description</th>
								<th width="25%">Error Message </th>
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
									<td align="left">
									 <c:forEach var="results2" items="${results.errorMsgList}" varStatus="rows2">
									    ${rows2.index}:${results2.message}<br/>
									 </c:forEach>
									</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<!-- ************************* Return Wacoal From ******************************************* -->
		
						
</body>
</html>