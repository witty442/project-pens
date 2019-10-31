<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.web.sales.OrderForm"%>
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
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}
String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

List<References> creditCardType = InitialReferences.getReferenes().get(InitialReferences.CREDITCARD_TYPE);
pageContext.setAttribute("creditCardType",creditCardType,PageContext.PAGE_SCOPE);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
.h1{
 font-size: 20px;
 font-weight: bold;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrderProduct.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>

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
	//display payment method
	document.getElementById("paymentMethod").value ="<%=orderForm.getOrder().getPaymentMethod()%>";
	changePaymentMethod(document.getElementById("paymentMethod"));
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
	
	  PopupCenter(path + "/jsp/pop/printPopup.jsp?"+param,"Print",700,300);
	  
	return true;
}

function setNextVisitSummary(path, visitDate, fileType){
	i=0;
	_path = path;
	document.getElementsByName('fileType')[0].value = fileType;
	document.getElementsByName('nextVisitDate')[0].value = visitDate;
	
	window.open(path + "/jsp/saleOrderAction.do?do=printReportSummary&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print1", "width=100,height=100,location=No,resizable=No");
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
     PopupCenter(path + "/jsp/pop/printPopup.jsp?"+param,"Print",750,300);
}

function close(){
	window.close();
}
function printAgain(){
	i++;
	document.orderForm.action = _path + "/jsp/saleOrderAction.do?do=printReport&i="+i;
	document.orderForm.submit();
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

function changePaymentMethod(paymentMethod){
	if("CR" ==paymentMethod.value ){
		document.getElementById("div_credit_1").style.display = "block";
		document.getElementById("div_credit_2").style.display = "block";
		document.getElementById("div_credit_3").style.display = "block";
		document.getElementById("div_credit_4").style.display = "block";
	}else{
		document.getElementById("div_credit_1").style.display = "none";
		document.getElementById("div_credit_2").style.display = "none";
		document.getElementById("div_credit_3").style.display = "none";
		document.getElementById("div_credit_4").style.display = "none";
	}
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<jsp:include page="../menu_header.jsp"/>
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
						
                       <div id="divOrderView" style="">
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
					      <tr>
								<td width="30%" align="right">ร้านค้าหลัก</td>
								<td width="25%" align="left">
								  <html:text property="order.customerName"  
								  readonly="true" styleId="order.customerName" styleClass="disableText"/> 
								
								   <html:hidden property="order.customerId" styleId="order.customerId" /> 
								</td>
								<td width="15%" align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><b>ข้อมูลลูกค้าสำหรับออกบิล</b></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><font color="red"></font></td>
								<td align="left">
									<html:text property="order.orderDate" maxlength="10" size="15" readonly="true" styleId="orderDate" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">&nbsp;&nbsp;ชื่อ-นามสกุล<font color="red"></font></td>
								<td align="left" colspan="3">
									<html:text property="order.customerBillName" size="40"  readonly="true" styleClass="disableText"/>		
								</td>
							</tr>
							<tr>
								<td align="right">ที่อยู่<font color="red"></font></td>
								<td align="left" colspan="3">
									<html:text property="order.addressDesc" size="80"  readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">บัตรประชาชน<font color="red"></font></td>
								<td align="left" colspan="2">
									<html:text property="order.idNo" size="20" readonly="true" styleClass="disableText"/>
									&nbsp;&nbsp;&nbsp;&nbsp;
									Passport No &nbsp;
									<html:text property="order.passportNo" size="20" readonly="true" styleClass="disableText"/>
								    &nbsp;&nbsp;&nbsp;&nbsp;  
							</td> 
							 <td align="left">   
								         อัตราภาษี &nbsp;
								    <html:text property="order.vatCode" value="7" readonly="true" size="2" styleClass="disableText"/>
									<html:hidden property="order.priceListId" styleId="order.priceListId"/>
									<html:hidden property="order.vatCode"/>
									<html:hidden property="order.paymentTerm"/>
									<html:hidden property="order.oraBillAddressID"/>
									<html:hidden property="order.oraShipAddressID"/>
								</td>
							</tr> 
							<tr>
								<td colspan="4" align="center">
								<div id="divTableProductHead" style="width: 100%; height:50px; overflow-y: scroll;">
								<table id="tblProductHead" align="left" border="0" cellpadding="1" cellspacing="1" class="tableSearch">
									<tr>
										<th class="td_text_center" width="10%">ลำดับ</th>
										<th class="td_text_center" width="25%">ชื่อสินค้า</th>
										<th class="td_text_center" width="5%">หน่วยนับ</th>
										<th class="td_text_center" width="10%">จำนวน</th>
										<th class="td_text_center" width="10%">ราคาต่อหน่วย</th>
										<th class="td_text_center" width="10%">ยอดรวม</th>
										<th class="td_text_center" width="10%">ส่วนลด</th>
										<th class="td_text_center" width="10%">ยอดรวม หลังหักส่วนลด</th>
										<th class="td_text_center" width="5%">ภาษี</th>
										<th class="td_text_center" width="5%">โปรโมชั่น</th>
									</tr>
								</table>
								</div>
								<div id="divTableProduct" style="width: 100%; height: 200px; overflow-y: scroll;">
								<table align="center" border="0" cellpadding="1" cellspacing="1" class="tableSearch">
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
										<td class="td_text_center" width="10%">${rows1.index + 1}</td>
										<td class="td_text" width="25%">${lines1.product.code} ${lines1.product.name}</td>
										<td class="td_text_center" width="5%">
											${lines1.fullUom}
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
											<c:if test="${lines1.promotion=='N'}">
											 <fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount - lines1.discount}"/>
										   </c:if>
										    <c:if test="${lines1.promotion=='S'}">
										        0.00000	
										    </c:if>
										</td>
										
										<td class="td_text_center" width="5%">
											<c:if test="${lines1.taxable=='Y'}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if>
										</td>
										<td  class="td_text_center" width="5%">
											${lines1.promotion}
										</td>
									</tr>
									</c:forEach>
								</table>
								</div>
								  <hr>
								</td>
							</tr>
							<tr><td colspan="5">
							   <table id="tblProductSummary" align="left" border="0" cellpadding="3" 
							   cellspacing="1" width="100%" bgcolor="#D3D3D3">
							      <tr >
									<td class="td_text_right" width="40%" colspan="1"><span class="h1">รวมจำนวน</span></td>
									<td  class="td_text_right" width="10%">
									   <!-- <input type="text" name="totalQty" id="totalQty" size="10" 
									   class="disableBoldNumber"> --> 
									  <b> <span id="totalQty" class="h1"><%=orderForm.getTotalQty() %></span></b>
									</td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_center" width="5%"></td>
									<td class="td_text_center" width="5%"></td>
								  </tr>
							   </table>
							</td></tr>
							<tr>
								<td align="left" valign="top" colspan="2">   
								  <table align="left" border="0" cellpadding="3" cellspacing="1" class="" width="70%">
									   <tr>
									      <td align="left" width="10%"></td>
									      <td align="left" width="10%"></td>
									      <td align="center" width="20%"><span id="div_credit_1" style="display:none"><b> เลขที่บัตร</b></span></td>
									      <td align="center" width="10%"><span id="div_credit_2" style="display:none"><b> หมดอายุ MM/YYYY</b></span></td>
									   </tr>
									   <tr>
									      <td align="left"></td>
									      <td align="left" nowrap><b>ชำระโดย</b>&nbsp;<b>
									        <html:hidden property="order.paymentMethod" styleId="paymentMethod"/>
									         <%if(orderForm.getOrder().getPaymentMethod().equalsIgnoreCase("CS")) {%>
									              <input type="text"  value="เงินสด" class="disableBoldText" readonly size="10"/>
									         <%}else if(orderForm.getOrder().getPaymentMethod().equalsIgnoreCase("ALI")) {%>
									              <input type="text"  value="AliPay" class="disableBoldText" readonly size="10"/>
									         <%}else if(orderForm.getOrder().getPaymentMethod().equalsIgnoreCase("WE")) {%>
									             <input type="text"  value="WeChat" class="disableBoldText" readonly size="10"/>
									         <%}else if(orderForm.getOrder().getPaymentMethod().equalsIgnoreCase("GOV")) {%>
									             <input type="text"  value="ชิมช็อปใช้" class="disableBoldText" readonly size="10"/>
									         <%}else if(orderForm.getOrder().getPaymentMethod().equalsIgnoreCase("CR")) {%>
									             <input type="text"  value="บัตรเครดิต" class="disableBoldText" readonly size="10"/>
									         <%} %>
									         </b>
									      </td>
									      <td align="left" nowrap>
									         <span id="div_credit_3" style="display:none">
									            <html:select property="order.creditCardType" disabled="true" styleClass="normalText">
										           <html:options collection="creditCardType" property="key" labelProperty="name"/>
									            </html:select> 
									           <html:text property="order.creditCardNo" styleId="creditCardNo"
									           readonly="true" styleClass="disableText" size="30"/>
									         </span>
									      </td>
									      <td align="center">
									        <span id="div_credit_4" style="display:none;" align="center">
									           <html:text property="order.creditCardExpireDate" styleId="creditCardExpireDate" 
									           readonly="true" styleClass="disableText" size="15"/>
									        </span>
									      </td>
									    </tr>
									    <tr>
									      <td colspan="4" ><hr></td>
									    </tr>
									   <tr>
									   <td colspan="4" align="right" nowrap>
									     <c:if test="${orderForm.mode=='edit'}">
										   <c:if test="${orderForm.order.exported=='N'}">
											 <c:if test="${orderForm.order.docStatus=='SV'}">
											   <c:if test="${orderForm.order.payment=='N'}">
												<%-- <input type="button" value="แก้ไขรายการ" class="newPosBtnLong" onclick="prepare('${pageContext.request.contextPath}','edit','${orderForm.order.id}');"> --%>
											   </c:if>
											 </c:if>
										    </c:if>
								         </c:if>
									      <c:if test="${orderForm.order.docStatus=='SV'}">
											<%--   <input type="button" id ="reportBtn" value="พิมพ์ ใบส่งสินค้า/ใบเสร็จรับเงินชั่วคราว" class="newPosBtn" onclick="stampPrint();gotoSummaryReport('${pageContext.request.contextPath}','copy');"> --%>
					                          <input type="button" id ="reportBtn" value="พิมพ์ ใบกำกับภาษี(จริง)" class="newPosBtn" onclick="gotoSummaryReport('${pageContext.request.contextPath}','original');">
	                                          &nbsp;&nbsp;&nbsp;
											 <%--  <input type="button" id ="reportBtn" value="พิมพ์ (A5)" class="newPosBtn" onclick="gotoReport('${pageContext.request.contextPath}','<%=role %>');"> --%>
										  </c:if>
										  <input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									   </td>
									 </tr>
								  </table>
								</td>
								 <td align="left" ></td> 
								<td valign="top"  >
								   <table align="left" border="0" cellpadding="3" cellspacing="1">
									    <tr>
									      <td align="right" nowrap><b>ยอดรวมก่อนภาษี</b></td>
									      <td align="right">
									        <input type="text" id="tempTotalAmount" name="tempTotalAmount" size="21" readonly="readonly" class="disableBoldText" style="text-align: right;"/>
										     <html:hidden property="order.totalAmount"/>
									      </td>
									    </tr>
									    <tr>
									       <td align="right" nowrap><b>ภาษี</b></td>
									       <td align="right">
									         <input type="text" id="tempVatAmount" name="tempVatAmount" size="21" readonly="readonly" class="disableBoldText" style="text-align: right;"/>
									         <html:hidden property="order.vatAmount"/>
									       </td>
									    </tr>
									    
									    <tr>
									       <td align="right" nowrap><b><font size ="3">ยอดสุทธิ</font></b></td>
									       <td align="right">
									          <input type="text" id="tempNetAmount" name="tempNetAmount" size="17" readonly="readonly" class="disableBoldBigBlueText" style="text-align: right;"/>
									           <html:hidden property="order.netAmount"/>
									           
									            <!-- calc vat -->
									           <input type="hidden" id="tempTotalAmountNonVat" name="tempTotalAmountNonVat"/>
									           <html:hidden property="order.totalAmountNonVat" />
									       </td>
									    </tr>
									    <%if( !"view".equalsIgnoreCase(Utils.isNull(request.getParameter("action")))) {%>
										    <tr>
									          <td colspan="3" ><hr></td>
									        </tr>
										     <tr>
										       <td align="right" nowrap style="color:#00e600;"><b><font size ="4">รับเงินจากลูกค้า</font></b></td>
										       <td align="right">            
                                                 <html:text property="order.tempCustAmount" readonly="true" size="14" styleId="tempCustAmount"
                                                   styleClass="disableBoldBigGreenNumber" />
										       </td>
										    </tr>
										    <tr>
										       <td align="right" nowrap style="color:#ff471a;"><b><font size ="4">เงินทอน</font></b></td>
										       <td align="right">
				                                    <html:text property="order.tempChangeAmount" size="14" styleId="tempChangeAmount"
                                                     styleClass="disableBoldBigRedNumber" readonly="true" tabindex="-1"/>
										       </td>
										    </tr>
									    <%} %>
								   </table> 
								</td>
							</tr>
						</table>
						<!-- BUTTON -->
						<br />
						
					    <span title="SalesOrderView">...</span>
						<!--  -->
						<html:hidden property="order.payment" styleId="payment"/>
						<html:hidden property="deletedId"/>
						<html:hidden property="order.orderType"/>
						<html:hidden property="order.id"/>
						<html:hidden property="order.customerId"/>
						<html:hidden property="order.exported"/>
						<html:hidden property="order.isCash"/>
						<html:hidden property="order.isPromotionSpecial"/>
						
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
						
						<input type="hidden" name="orderDate" id="orderDate" value="${orderForm.order.orderDate}"/>
						
						<html:hidden property="order.printDateTimePick"/><br/>
						<html:hidden property="order.printCountPick"/><br/>
						<html:hidden property="order.printDateTimeRcp"/><br/>
						<html:hidden property="order.printCountRcp"/>
						
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<jsp:include page="../trxhist.jsp">
							<jsp:param name="module" value="<%=TrxHistory.MOD_ORDER%>"/>
							<jsp:param name="id" value="${orderForm.order.id}"/>
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