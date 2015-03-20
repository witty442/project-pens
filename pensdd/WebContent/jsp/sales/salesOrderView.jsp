<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}
String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";
List<Address> address = new ArrayList<Address>();
address = new MAddress().lookUp(orderForm.getOrder().getCustomerId());
pageContext.setAttribute("addresses",address,PageContext.PAGE_SCOPE);

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);

List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
function loadMe(){
	document.getElementById("tempTotalAmount").value = addCommas(Number(document.getElementsByName("order.totalAmount")[0].value).toFixed(2));
	document.getElementById("tempVatAmount").value = addCommas(Number(document.getElementsByName("order.vatAmount")[0].value).toFixed(2));
	document.getElementById("tempNetAmount").value = addCommas(Number(document.getElementsByName("order.netAmount")[0].value).toFixed(2));

	//WIT EDIT :20110804

	<%if( ("true").equals(util.ConvertNullUtil.convertToString(request.getAttribute("popup_autoreceipt")))){ %>
	    alert('กรุณาทำรายการบันทึกรับเงิน');
	    autoReceipt('${pageContext.request.contextPath}','<%=user.getType() %>');
	<%} %>
	
}


var i;
var _path;
function setNextVisit(path, visitDate, fileType){
	i=0;
	_path = path;
	document.getElementsByName('fileType')[0].value = fileType;
	document.getElementsByName('nextVisitDate')[0].value = visitDate;
	//document.orderForm.action = path + "/jsp/saleOrderAction.do?do=printReport&i="+i;
	//document.orderForm.submit();

	window.open(path + "/jsp/saleOrderAction.do?do=printReport&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print1", "width=100,height=100,location=No,resizable=No");
	window.open(path + "/jsp/saleOrderAction.do?do=printReport&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print2", "width=100,height=100,location=No,resizable=No");
	//setTimeout("printAgain()", 2000);
	return true;
}

function printAgain(){
	var pay = jQuery.trim(document.getElementById('payment').value);
	i++;
	//if(pay=='N'){
		document.orderForm.action = _path + "/jsp/saleOrderAction.do?do=printReport&i="+i;
		document.orderForm.submit();
	//}
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="SalesOrder"/>
				<jsp:param name="code" value="${orderForm.order.orderNo}"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/saleOrderAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="30%"></td>
								<td width="25%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left"></td>
								<td align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.created" maxlength="10" size="18" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">
									<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
									<bean:message key="Customer" bundle="sysprop"/>&nbsp;&nbsp;
									<%}else{ %>
									<bean:message key="Member" bundle="sysprop"/>&nbsp;&nbsp;
									<%} %>
								</td>
								<td align="left" colspan="3">
									<html:text property="order.customerName" size="80" readonly="true" styleClass="disableText"/>
									<html:hidden property="order.customerId"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryAddress" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:select property="order.shipAddressId" style="width:80%" disabled="true" styleClass="disableText">
										<html:options collection="addresses" property="id" labelProperty="lineString"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryDocAddress" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:select property="order.billAddressId" style="width:80%" disabled="true" styleClass="disableText">
										<html:options collection="addresses" property="id" labelProperty="lineString"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Profile.CreditTerm" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="order.paymentTerm" disabled="true" styleClass="disableText">
										<html:options collection="paymentTerm" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Profile.TaxRate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="order.vatCode" disabled="true" onchange="calculatePrice();" styleClass="disableText">
										<html:options collection="vatcodes" property="key" labelProperty="name"/>
									</html:select>
									
									<html:hidden property="order.priceListId"/>
									<html:hidden property="order.vatCode"/>
									<html:hidden property="order.paymentTerm"/>
									<html:hidden property="order.paymentMethod"/>
								</td>
							</tr>
<!--							<tr>-->
<!--								<td align="right"><bean:message key="Profile.PaymentMethod" bundle="sysele"/>&nbsp;&nbsp;</td>-->
<!--								<td align="left">-->
<!--									<html:select property="order.paymentMethod" disabled="true" styleClass="disableText">-->
<!--										<html:options collection="paymentMethod" property="key" labelProperty="name"/>-->
<!--									</html:select>-->
<!--								</td>-->
<!--								<td colspan="2">-->
<!--									-->
<!--								</td>-->
<!--							</tr>-->
							<%if(role.equalsIgnoreCase(User.DD)) {%>
							<tr>
								<td align="right"><bean:message key="Condition.ShipmentDay" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="order.shippingDay" disabled="true" styleClass="disableText">
										<html:option value="Mon"><bean:message key="Monday" bundle="sysele" /></html:option>
										<html:option value="Tue"><bean:message key="Tueday" bundle="sysele" /></html:option>
										<html:option value="Wed"><bean:message key="Wednesday" bundle="sysele" /></html:option>
										<html:option value="Thu"><bean:message key="Thursday" bundle="sysele" /></html:option>
										<html:option value="Fri"><bean:message key="Friday" bundle="sysele" /></html:option>
										<html:option value="Sat"><bean:message key="Saturday" bundle="sysele" /></html:option>
									</html:select>
								</td>
								<td align="right"><bean:message key="Condition.ShipmentTime" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.shippingTime" size="5" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<%} %>
							<tr>
								<td colspan="4" align="center">
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<th class="order"><bean:message key="No" bundle="sysprop"/></th>
										<th><bean:message key="Product.Name" bundle="sysele"/></th>
										<th><bean:message key="Product.UOM" bundle="sysele"/></th>
										<th><bean:message key="Quantity" bundle="sysele"/></th>
										<th><bean:message key="Price.Unit" bundle="sysele"/></th>
										<th><bean:message key="Total" bundle="sysele"/></th>
										<th><bean:message key="Discount" bundle="sysele"/></th>
										<th><bean:message key="TotalExcludeDiscount" bundle="sysele"/></th>
<!--										<th><bean:message key="Tax" bundle="sysele"/></th>-->
<!--										<th><bean:message key="Overall" bundle="sysele"/></th>-->
										<%if(user.getType().equalsIgnoreCase(User.DD)){ %>
										<th><bean:message key="Member.Time" bundle="sysele"/></th>
										<%} %>
										<th><bean:message key="Order.ShipmentDate" bundle="sysele"/></th>
										<%if(user.getType().equalsIgnoreCase(User.DD)){ %>
										<th><bean:message key="Order.ReceiveDate" bundle="sysele"/></th>
										<%} %>
										<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
										<th><bean:message key="Order.RequiredDate" bundle="sysele"/></th>
										<%} %>
										<%if(!user.getType().equals(User.DD)){ %>
										<th><bean:message key="Promotion" bundle="sysele"/></th>
										<%} %>
										<%if(user.getType().equals(User.DD)){ %>
										<th>เก็บเงิน</th>
										<th>วิธีการชำระเงิน</th>
										<th>ส่งของ</th>
										<th>Export</th>
										<th>Promotion</th>
										<th><bean:message key="Order.Paid" bundle="sysele"/></th>
										<th><bean:message key="Bill.No" bundle="sysele"/></th>
										<%} %>
									</tr>
									<c:set var="totalNeedBillAmt" value="0" />
									<c:forEach var="lines1" items="${orderForm.lines}" varStatus="rows1">
									<c:choose>
										<c:when test="${rows1.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="${tabclass}">
										<td>${rows1.index + 1}</td>
										<td align="left">${lines1.product.code} ${lines1.product.name}</td>
										<td align="center">
											<c:choose>
												<c:when test="<%=orderForm.getOrder().getOrderType().equals(User.DD) %>">
													${lines1.uom.code}&nbsp;${lines1.uom1.code}
												</c:when>
												<c:otherwise>
													${lines1.fullUom}	
												</c:otherwise>
											</c:choose>
										</td>
										<td align="right">
											<%if(user.getType().equals(User.DD)){ %>
												<c:choose>
													<c:when test="${lines1.qty==0}">
														<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber pattern="#,##0" value="${lines1.qty}"/>
													</c:otherwise>
												</c:choose>
											<%}else{ %>
											<c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<c:choose>
														<c:when test="${(lines1.product.uom.id==lines1.uom1.id && lines1.product.uom.id==lines1.uom2.id) || (lines1.product.uom.id!=lines1.uom1.id && lines1.product.uom.id!=lines1.uom2.id)}">
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty1 + lines1.qty2}"/>
														</c:when>
														<c:otherwise>
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
													<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>												
												</c:otherwise>
											</c:choose>
											<%} %>
										</td>
										<td align="right">
											<%if(user.getType().equals(User.DD)){ %>
												<c:choose>
													<c:when test="${lines1.price==0}">
														<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price}"/>
													</c:otherwise>
												</c:choose>
											<%}else{ %>
											<c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<fmt:formatNumber pattern="#,##0.00000" value="0"/>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>/
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price2}"/>												
												</c:otherwise>
											</c:choose>											
											<%} %>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00" value="${lines1.lineAmount - lines1.discount}"/>
										</td>
<!--										<td align="right">-->
											
<!--											<fmt:formatNumber pattern="#,##0.00" value="${lines1.vatAmount}"/>-->
<!--										</td>-->
<!--										<td align="right">-->
<!--											<fmt:formatNumber pattern="#,##0.00" value="${lines1.totalAmount}"/>-->
<!--										</td>-->
										<%if(user.getType().equalsIgnoreCase(User.DD)){ %>
										<td align="center">${lines1.tripNo}</td>
										<%} %>
										<td align="center">${lines1.shippingDate}</td>
										<td align="center">${lines1.requestDate}</td>
										<td align="center">

										<fmt:formatNumber pattern="#,##0.00" value="${lines1.needBill}"/>
										</td>
										
										<td align="center">
																			
										<c:choose>
										
												<c:when test="${lines1.paymentMethod == 'CS'}">
														เงินสด
												</c:when>
												<c:when test="${lines1.paymentMethod == 'CR'}">
														บัตรเครดิต
												</c:when>
												<c:when test="${lines1.paymentMethod == 'CH'}">
														เช็ค
												</c:when>
												<c:otherwise>
														
												</c:otherwise>
											</c:choose>
										</td>
										
										<td align="center">									
										<c:choose>
												<c:when test="${lines1.shipment == 'Y'}">
													<img src="${pageContext.request.contextPath}/icons/check.gif" align="absmiddle"/>
												</c:when>
												
											</c:choose>
										</td>
										
										<td align="center">									
										<c:choose>
												<c:when test="${lines1.exported == 'Y'}">
													<img src="${pageContext.request.contextPath}/icons/check.gif" align="absmiddle"/>
												</c:when>
												
											</c:choose>
										</td>
										
										
										
										

										<td align="center">
										
										
										<c:choose>
												<c:when test="${lines1.promotion1 == 'Y'}">
													<img src="${pageContext.request.contextPath}/icons/check.gif" align="absmiddle"/>
												</c:when>
												
											</c:choose>
											
										</td>
										
										<%if(!user.getType().equals(User.DD)){ %>
										<td align="center">
											<c:if test="${lines1.promotion=='Y'}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if>
										</td>
										<%} %>
										<%if(user.getType().equals(User.DD)){ %>
										<td align="center">
											<c:if test="${lines1.payment=='Y'}">
											<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if></td>
										<td align="center">${lines1.arInvoiceNo}</td>
										<%} %>
									</tr>
									<c:set var="totalNeedBillAmt" value="${totalNeedBillAmt+lines1.needBill}" />
									</c:forEach>
								</table>
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<td align="left" class="footer"></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right">ยอดสุทธิรวม<span id="totalAmt" style="display: none;"><bean:message key="TotalAmount" bundle="sysele"/></span>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempTotalNeedNill" name="tempTotalNeedNill" readonly="readonly" class="disableText" style="text-align: right;" value="<fmt:formatNumber type="number" pattern="#,###,##0.00" value="${totalNeedBillAmt}" />" />
									
									<input type="text" id="tempTotalAmount" name="tempTotalAmount" readonly="readonly" class="disableText" style="text-align: right;display:none;"/>
									<html:hidden property="order.totalAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><span id="taxAmt" style="display: none;"><bean:message key="Tax" bundle="sysele"/></span>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempVatAmount" name="tempVatAmount" readonly="readonly" class="disableText" style="text-align: right;display:none;"/>
									<html:hidden property="order.vatAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><span id="netAmt" style="display: none;"><bean:message key="Net" bundle="sysele"/></span>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" class="disableText" style="text-align: right;display:none;"/>
									<html:hidden property="order.netAmount"/>
								</td>
							</tr>
							<tr>
								<td colspan="4"><hr/></td>
							</tr>
							<tr>
								<td></td>
								<td><!--<html:checkbox property="order.paymentCashNow" value="Y" disabled="true" styleClass="disableText"/> บันทึกรับเงินสดทันที --></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.payment" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Order.Paid" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesOrderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="Bill.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.arInvoiceNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.SalesRepresent"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
									<html:hidden property="order.salesRepresent.id"/>
									<html:hidden property="order.salesRepresent.code"/>
								</td>
								<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td valign="top">
									<html:select property="order.docStatus" disabled="true" styleClass="disableText">
										<html:options collection="docstatus" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
						</table>
						<!-- BUTTON -->
						<br />
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right" width="10%"></td>
								<td align="left">
									<%if(!isAdd.equals("N") || ((String)session.getAttribute("memberVIP")).equalsIgnoreCase("Y")){ %>
									<a href="#" onclick="prepare('${pageContext.request.contextPath}','add');">
									<!--<img src="${pageContext.request.contextPath}/images/b_new.gif" border="1" class="newPicBtn">-->
									<input type="button" value="สร้างรายการใหม่" class="newPosBtnLong">
									</a>
									<%} %>
									<c:if test="${orderForm.order.exported=='N'}">
										<c:if test="${orderForm.order.docStatus=='SV'}">
										<c:if test="${orderForm.order.payment=='N'}">
											<!-- a href="#" onclick="prepare('${pageContext.request.contextPath}','edit','${orderForm.order.id}');">
											<!--<img src="${pageContext.request.contextPath}/images/b_edit.gif" border="1" class="newPicBtn">
											<input type="button" value="แก้ไขรายการ" class="newPosBtnLong">
											</a>-->
										</c:if>
										</c:if>
									</c:if>
									<%if(role.equals(User.VAN) || role.equals(User.DD)){ %>
									<c:if test="${orderForm.order.docStatus=='SV'}">
										<a href="#" onclick="gotoReport('${pageContext.request.contextPath}','<%=role %>');">
										<!--<img src="${pageContext.request.contextPath}/images/b_printrc.gif" border="1" class="newPicBtn">-->
										<input type="button" value="พิมพ์" class="newPosBtn">
										</a>
									</c:if>
									<%} %>
									<%if(role.equals(User.VAN)){ %>
									<c:if test="${orderForm.order.payment=='N'}">
										<c:if test="${orderForm.order.docStatus=='SV'}">
										<a href="#" onclick="autoReceipt('${pageContext.request.contextPath}','<%=role %>');">
										<input type="button" value="บันทึกรับเงิน" class="newPosBtn"></a>
										</c:if>
									</c:if>
									<%} %>
								</td>
								<td align="right">
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<!-- AUTO RECEIPT -->
						<html:hidden property="autoReceiptFlag"/>
						<html:hidden property="autoReceipt.paymentMethod"/>
						<html:hidden property="autoReceipt.bank"/>
						<html:hidden property="autoReceipt.chequeNo"/>
						<html:hidden property="autoReceipt.chequeDate"/>
						<html:hidden property="autoReceipt.creditCardType"/>
						<html:hidden property="autoReceipt.internalBank"/>
						
						<!--  -->
						<html:hidden property="order.payment" styleId="payment"/>
						<html:hidden property="deletedId"/>
						<html:hidden property="order.orderType"/>
						<html:hidden property="order.id"/>
						<html:hidden property="order.customerId"/>
						<html:hidden property="order.exported"/>
						<html:hidden property="order.isCash"/>
						<input type="hidden" name="fileType" id="fileType"/>
						<input type="hidden" name="nextVisitDate">
						<input type="hidden" name="memberVIP" value="${memberVIP}"/>
						<div id="productList" style="display: none;"></div>
						<div id="ByList" style="display: none;"></div>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<jsp:include page="../trxhist.jsp">
							<jsp:param name="module" value="<%=TrxHistory.MOD_ORDER%>"/>
							<jsp:param name="id" value="${orderForm.order.id}"/>
						</jsp:include>
						</html:form>
						<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>