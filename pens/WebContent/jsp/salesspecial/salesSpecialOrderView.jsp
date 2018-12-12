<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="util.SessionGen"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="orderSpecialForm" class="com.isecinc.pens.web.salesspecial.OrderSpecialForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}
String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";
List<Address> address = new ArrayList<Address>();
address = new MAddress().lookUp(orderSpecialForm.getOrder().getCustomerId());
pageContext.setAttribute("addresses",address,PageContext.PAGE_SCOPE);

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);

List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

/* -- Auto Receipt --> */
List<References> banks= InitialReferences.getReferenes().get(InitialReferences.BANK);

List<References> internalBank= InitialReferences.getReferenes().get(InitialReferences.INTERNAL_BANK);
pageContext.setAttribute("internalBank",internalBank,PageContext.PAGE_SCOPE);

List<References> vanPaymentMethod = InitialReferences.getReferenes().get(InitialReferences.VAN_PAYMENT_METHOD);
pageContext.setAttribute("vanPaymentMethod",vanPaymentMethod,PageContext.PAGE_SCOPE);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	var totalAmountHaveVat = Number(document.getElementsByName("order.totalAmount")[0].value)
	-Number(document.getElementsByName("order.totalAmountNonVat")[0].value);
	
	//alert(totalAmountHaveVat);
	//TotalAmount is have vat
	document.getElementById("tempTotalAmount").value = addCommas(Number(totalAmountHaveVat).toFixed(2));
	document.getElementById("tempTotalAmountNonVat").value = addCommas(Number(Number(document.getElementsByName("order.totalAmountNonVat")[0].value)).toFixed(2));
	document.getElementById("tempNetAmount").value = addCommas(Number(document.getElementsByName("order.netAmount")[0].value).toFixed(2)); 
	
	if(Number(document.getElementsByName("order.vatAmount")[0].value)==0){
		document.getElementById("tempVatAmount").value ="-";
	}else{
		document.getElementById("tempVatAmount").value = addCommas(Number(document.getElementsByName("order.vatAmount")[0].value).toFixed(2));
	}
}

var countSaveReceiptVan = 0;
var i;
var _path;
function setNextVisit(path, visitDate, fileType){
	i=0;
	_path = path;
	document.getElementsByName('fileType')[0].value = fileType;
	document.getElementsByName('nextVisitDate')[0].value = visitDate;
	
	//window.open(path + "/jsp/saleOrderAction.do?do=printReport&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print1", "width=100,height=100,location=No,resizable=No");
	//window.open(path + "/jsp/saleOrderAction.do?do=printReport&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print2", "width=100,height=100,location=No,resizable=No");
	var param  = "report_name=printReport";
	    param += "&orderId="+document.getElementsByName('order.id')[0].value;
	    param += "&i=0";
	    param += "&visitDate="+visitDate+"&fileType="+fileType;
	
	  PopupCenter(path + "/jsp/salesspecial/pop/printPopup.jsp?"+param,"Print",700,300);
	  
	return true;
}

function setNextVisitSummary(path, visitDate, fileType){
	i=0;
	_path = path;
	document.getElementsByName('fileType')[0].value = fileType;
	document.getElementsByName('nextVisitDate')[0].value = visitDate;
	
	window.open(path + "/jsp/saleOrderSpecialAction.do?do=printReportSummary&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print1", "width=100,height=100,location=No,resizable=No");
	return true;
}

function gotoSummaryReport(path, reportType,orderNo){
 var param ="report_name=tax_invoice_summary&orderId="+document.getElementsByName('order.id')[0].value
     param += "&reportType="+reportType;
     param +="&orderNo="+orderNo;
     
     var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
     var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

     var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
     var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

     param +="&width="+(width-100);
     param +="&height="+(height-100);
     
    //window.open(path + "/jsp/pop/printPopup.jsp?"+param, "Print2", "width=100,height=100,location=No,resizable=No");
     PopupCenter(path + "/jsp/salesspecial/pop/printPopup.jsp?"+param,"Print",750,300);
}

function close(){
	window.close();
}
function printAgain(){
	i++;
	document.orderSpecialForm.action = _path + "/jsp/saleOrderSpecialAction.do?do=printReport&i="+i;
	document.orderSpecialForm.submit();
}

function stampPrint(){
	var orderId = document.getElementsByName('order.id')[0].value;
	
	var printDateTimePick = document.getElementsByName("order.printDateTimeRcp")[0];
	var printCountPick = document.getElementsByName("order.printCountRcp")[0];
	
	printCountPick.value = parseInt(printCountPick.value)+1;
	var d = new Date(); 
	var dd = d.getDate();
        dd = dd.toString().length==1?"0"+dd:dd;
	var MM = (d.getMonth()+1);
	    MM = MM.toString().length==1?"0"+MM:MM;
	var year = (d.getFullYear()+543).toString();
	var hours = (d.getHours()).toString();
	    hours = hours.toString().length==1?"0"+hours:hours;
	var minite = (d.getMinutes()).toString();
	    minite = minite.toString().length==1?"0"+minite:minite;
	    
	    //ddMMyyyyHHmm
	var currentDateStr = dd+""+MM+""+year+""+hours+""+minite;
	//alert(currentDateStr);
	printDateTimePick.value = currentDateStr;
	
	//update db
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoStampPrint.jsp",
			data : "orderId="+orderId+"&dateStr="+printDateTimePick.value +"&count="+printCountPick.value,
			async: false,
			success: function(getData){
			}
		}).responseText;
	});
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
				<jsp:param name="function" value="SalesOrderSpecial"/>
				<jsp:param name="code" value="${orderSpecialForm.order.orderNo}"/>
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
						<html:form action="/jsp/saleOrderSpecialAction">
						<jsp:include page="../error.jsp"/>
						
                       <div id="divOrderView" style="">
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<%if(User.TT.equals(user.getType())){%>
							<tr>
								<td width="30%" align="right">ระบุเลขที่ PO ลูกค้า(ถ้ามี) </td>
								<td width="25%"><html:text property="order.poNumber" size="20"  readonly="true" styleClass="disableText"/></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<%}else{ %>
							    <tr>
									<td width="30%" align="right"></td>
									<td width="25%"></td>
									<td width="15%"></td>
									<td></td>
								</tr>
							 <%} %>
							<tr>
								<td align="right"></td>
								<td align="left"></td>
								<td align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:hidden property="order.orderNo" />
									<%
									 String orderNo1 = "";
									 String orderNo2 = "";
									 if(orderSpecialForm.getOrder() != null){
										 String orderNoTemp = orderSpecialForm.getOrder().getOrderNo();
										 if(orderNoTemp.length()==12){
											orderNo1 =  orderNoTemp.substring(0,8);
											orderNo2 =  orderNoTemp.substring(8,12);
										 }
									 }
									%>
									<b><span><%=orderNo1%></span></b><span class="labelBigSize"><%=orderNo2%></span>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderDate" maxlength="10" size="18" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">
									<bean:message key="Customer" bundle="sysprop"/>&nbsp;&nbsp;
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
							<tr>
								<td colspan="4" align="center">
								<div align="left">
								  N(สินค้าปกติ)&nbsp;&nbsp;&nbsp; Y(สินค้าแถมปกติ)&nbsp;&nbsp;&nbsp; S(สินค้าแถมพิเศษ)
								</div>
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<th class="td_text_center" width="5%"><bean:message key="No" bundle="sysprop"/></th>
										<th class="td_text_center" width="15%"><bean:message key="Product.Name" bundle="sysele"/></th>
										<th class="td_text_center" width="5%"><bean:message key="Product.UOM" bundle="sysele"/></th>
										<th class="td_text_center" width="5%"><bean:message key="Quantity" bundle="sysele"/></th>
										<th class="td_text_center" width="10%"><bean:message key="Price.Unit" bundle="sysele"/></th>
										<th class="td_text_center" width="10%"><bean:message key="Total" bundle="sysele"/></th>
										<th class="td_text_center" width="10%"><bean:message key="Discount" bundle="sysele"/></th>
										<th class="td_text_center" width="10%"><bean:message key="TotalExcludeDiscount" bundle="sysele"/></th>
										<th class="td_text_center" width="5%"><bean:message key="Order.ShipmentDate" bundle="sysele"/></th>										
										<th class="td_text_center" width="5%"><bean:message key="Order.RequiredDate" bundle="sysele"/></th>
										<th class="td_text_center" width="3%">ภาษี</th>
										<th class="td_text_center" width="3%"><bean:message key="Promotion" bundle="sysele"/></th>
									</tr>
									<c:forEach var="lines1" items="${orderSpecialForm.lines}" varStatus="rows1">
									<c:choose>
										<c:when test="${rows1.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="${tabclass}">
										<td class="td_text_center" width="5%">${rows1.index + 1}</td>
										<td class="td_text" width="15%">${lines1.product.code} ${lines1.product.name}</td>
										<td class="td_text_right" width="5%">
											<c:choose>
												<c:when test="<%=orderSpecialForm.getOrder().getOrderType().equals(User.DD) %>">
													${lines1.uom.code}&nbsp;${lines1.uom1.code}
												</c:when>
												<c:otherwise>
													${lines1.fullUom}	
												</c:otherwise>
											</c:choose>
										</td>
										<td class="td_text_right" width="10%">
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
										</td>
										<td class="td_text_right" width="10%">
											<c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<fmt:formatNumber pattern="#,##0.00000" value="0"/>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>/
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price2}"/>												
												</c:otherwise>
											</c:choose>											
										
										</td>
										<td class="td_text_right" width="10%">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
										</td>
										<td class="td_text_right" width="10%">
											<fmt:formatNumber pattern="#,##0.00" value="${lines1.discount}"/>
										</td>
										<td class="td_text_right" width="10%">
											<fmt:formatNumber pattern="#,##0.00" value="${lines1.lineAmount - lines1.discount}"/>
										</td>
										
										<td class="td_text_center" width="5%">${lines1.shippingDate}</td>
										<td class="td_text_center" width="5%">${lines1.requestDate}</td>
										<td class="td_text_center" width="3%">
											<c:if test="${lines1.taxable=='Y'}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if>
										</td>
										<td class="td_text_center" width="3%">
											${lines1.promotion}
										</td>
										
									</tr>
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
								<td align="right">ยอดรวมก่อนภาษี &nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempTotalAmount" name="tempTotalAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.totalAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="Tax" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempVatAmount" name="tempVatAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.vatAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right">ยอดเงินรวมที่ไม่เสียภาษี&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempTotalAmountNonVat" name="tempTotalAmountNonVat" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.totalAmountNonVat"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="Net" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.netAmount"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td><!--<html:checkbox property="order.paymentCashNow" value="Y" disabled="true" styleClass="disableText"/> บันทึกรับเงินสดทันที -->
								 <b>ชำระโดย
									 <html:select property="order.vanPaymentMethod" styleClass="disableText" disabled="true">
										<html:options collection="vanPaymentMethod" property="key" labelProperty="name"/>
									</html:select></b> 
								</td>
								
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
							       <%if(role.equals(User.VAN)){ %>
									   <input type="button" value="ทำบันทึกตั้งกอง" class="newPosBtnLong" onclick="manageProdShow('${pageContext.request.contextPath}','<%=orderSpecialForm.getOrder().getOrderNo()%>');">
									<%} %>
									
									<%if(!isAdd.equals("N") || ((String)session.getAttribute("memberVIP")).equalsIgnoreCase("Y")){ %>
										
										<input type="button" value="สร้างรายการใหม่" class="newPosBtnLong" onclick="prepare('${pageContext.request.contextPath}','add');">
										
									<%} %>
									
								    <c:if test="${orderSpecialForm.mode=='edit'}">
										<c:if test="${orderSpecialForm.order.exported=='N'}">
											<c:if test="${orderSpecialForm.order.docStatus=='SV'}">
											<c:if test="${orderSpecialForm.order.payment=='N'}">
												
												<input type="button" value="แก้ไขรายการ" class="newPosBtnLong" onclick="prepare('${pageContext.request.contextPath}','edit','${orderSpecialForm.order.id}');">
												
											</c:if>
											</c:if>
										</c:if>
								   </c:if>
									
									<%if(role.equals(User.VAN)){ %>
										<c:if test="${orderSpecialForm.order.docStatus=='SV'}">
										     <c:if test="${orderSpecialForm.order.isCash=='Y'}">
											    <input type="button" id ="reportBtn" value="พิมพ์ ใบส่งสินค้า/ใบเสร็จรับเงินชั่วคราว" class="newPosBtn" onclick="stampPrint();gotoSummaryReport('${pageContext.request.contextPath}','copy');">
					                            <input type="button" id ="reportBtn" value="พิมพ์ ใบกำกับภาษี(จริง)" class="newPosBtn" onclick="gotoSummaryReport('${pageContext.request.contextPath}','original');">
	                                          </c:if>
	                                          
	                                          <c:if test="${orderSpecialForm.order.isCash=='N'}">
											    <input type="button" id ="reportBtn" value="พิมพ์ใบส่งของ/ใบกำกับภาษี" class="newPosBtn" onclick="stampPrint();gotoSummaryReport('${pageContext.request.contextPath}','tax','${orderSpecialForm.order.orderNo}');">
					                           
					                             <input type="button" id ="reportBtn" value="พิมพ์ใบเสร็จรับเงิน" class="newPosBtn" onclick="gotoSummaryReport('${pageContext.request.contextPath}','bill','${orderSpecialForm.order.orderNo}');">
	                                           
	                                          </c:if>
											  <input type="button" id ="reportBtn" value="พิมพ์" class="newPosBtn" onclick="gotoReport('${pageContext.request.contextPath}','<%=role %>');">
										</c:if>
									<%} %>
									
								</td>
								<td align="right">
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderSpecialForm.order.customerId}');">
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
					    <span title="SalesOrderView">...</span>
						<!--  -->
						<html:hidden property="order.payment" styleId="payment"/>
						<html:hidden property="deletedId"/>
						<html:hidden property="order.orderType"/>
						<html:hidden property="order.id"/>
						<html:hidden property="order.customerId"/>
						<html:hidden property="order.exported"/>
						<html:hidden property="order.isCash"/>
						
						<!--  Can Receipt Credit (VAN)-->
						<html:hidden property="receiptCreditFlag"/>
						<html:hidden property="custCreditLimit"/>
						
						<!-- AUTO RECEIPT -->
						<html:hidden property="autoReceiptFlag"/>
						<html:hidden property="autoReceipt.bank"/>
						<html:hidden property="autoReceipt.chequeNo"/>
						<html:hidden property="autoReceipt.chequeDate"/>
						<html:hidden property="autoReceipt.creditCardType"/>	    
						<html:hidden property="autoReceipt.internalBank"/>
						<html:hidden property="autoReceipt.paymentMethod"/>
						
						<input type="hidden" name="fileType" id="fileType"/>
						<input type="hidden" name="nextVisitDate">
						<input type="hidden" name="memberVIP" value="${memberVIP}"/>
						
						<div id="productList" style="display: none;"></div>
						<div id="ByList" style="display: none;"></div>
						
						<input type="hidden" name="orderDate" id="orderDate" value="${orderSpecialForm.order.orderDate}"/>
						
						<html:hidden property="order.printDateTimePick"/><br/>
						<html:hidden property="order.printCountPick"/><br/>
						<html:hidden property="order.printDateTimeRcp"/><br/>
						<html:hidden property="order.printCountRcp"/>
						
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<jsp:include page="../trxhist.jsp">
							<jsp:param name="module" value="<%=TrxHistory.MOD_ORDER%>"/>
							<jsp:param name="id" value="${orderSpecialForm.order.id}"/>
						</jsp:include>
					  </div>
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