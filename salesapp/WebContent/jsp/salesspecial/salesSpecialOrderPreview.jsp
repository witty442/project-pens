<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.isecinc.pens.web.salesspecial.OrderSpecialForm"%>
<%@page import="util.SessionGen"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.OrgRuleBean"%>
<%@page import="com.isecinc.pens.model.MOrgRule"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="orderSpecialForm" class="com.isecinc.pens.web.salesspecial.OrderSpecialForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();

String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH"));
String today = sdf.format(new Date());

//wit edit 29/07/2554   Case VanSales shippingDate = requestDate
Calendar cld = Calendar.getInstance();
if( !"VAN".equals(user.getType()) ){
   cld.add(Calendar.DAY_OF_MONTH,3); // +3 Day
}
String reqDate = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH")).format(cld.getTime());

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

List<References> w1List = new MOrgRule().getW1RefList("","");
pageContext.setAttribute("w1List",w1List,PageContext.PAGE_SCOPE);

List<References> vanPaymentMethod = InitialReferences.getReferenes().get(InitialReferences.VAN_PAYMENT_METHOD);
pageContext.setAttribute("vanPaymentMethod",vanPaymentMethod,PageContext.PAGE_SCOPE);

//Filter Can Receipt More Cash //1:can, 0: cannot, -1:no pay prev bill
OrderSpecialForm orderSpecialFormTemp = null;
String receiptCreditFlag = "0";
if(request.getAttribute("orderSpecialForm") != null){
	orderSpecialFormTemp = (OrderSpecialForm)request.getAttribute("orderSpecialForm");
    receiptCreditFlag = Utils.isNull(orderSpecialFormTemp.getReceiptCreditFlag());
  
   System.out.println("orderSpecialFormTemp :"+orderSpecialFormTemp);
}
System.out.println("lines size:"+orderSpecialForm.getLines().size());
System.out.println("Message:"+request.getAttribute("Message"));

String saveBtnDisable = "";
String saveBtnStyle ="newPosBtn";
if(request.getAttribute("do_not_save") != null){
	saveBtnDisable = "disabled";
	//saveBtnStyle ="newPosBtnDisable";
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<style type="text/css">
.currPage{ border:1px solid #000000; padding-left:4px;padding-right:4px;padding-top:2px; }
.pageLink{padding-left:4px;padding-right:2px;padding-top:2px; }
.paging{height:18px;width:100%;padding-left:4px;padding-right:2px;padding-top:2px;}
.catalog{text-align:center;height:60px;width:25%;/*background-color:#FFCC99;*/}
.brandName{width:120px;vertical-align:top;}

table#productList thead{background:#FFE4CA;}
.qtyInput{width:50px; height:26px;text-align:right;}
table#productList tbody td{vertical-align:top;padding-left:2px;padding-right:4px;}
table#productList tbody td.number{text-align:right;}

</style>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrderSpecial.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrderProductSpecial.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/lock-scroll.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">
function loadMe(){
	calculatePrice();	
	//new Epoch('epoch_popup','th',document.getElementById('orderDate'));
	<%if(request.getAttribute("Message") != null){%>
	  alert("<%=request.getAttribute("Message")%>");
	<%} %>
}

function printListOrderProductReport(path,userType){
	var customerId = document.getElementsByName("order.customerId");
   // window.open(path + "/jsp/saleOrderAction.do?do=printListOrderProductReport&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
	window.open(path + "/jsp/salesspecial/pop/printPopup.jsp?report_name=list_order_product&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
}

function stampPrint(){
	var printDateTimePick = document.getElementsByName("order.printDateTimePick")[0];
	var printCountPick = document.getElementsByName("order.printCountPick")[0];
	
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
	    
	var currentDateStr = dd+""+MM+""+year+""+hours+""+minite;
	//alert(currentDateStr);
	printDateTimePick.value = currentDateStr;
	
}
function validateVanPaymentMethod(){
	var r = true;
	var vanPaymentMethod = document.getElementsByName("order.vanPaymentMethod")[0];
	if(vanPaymentMethod.value =='CREDIT'){
		<%if(receiptCreditFlag.equalsIgnoreCase("0")){%>
		     //alert("ร้านค้านี้ ยังไม่ได้เปิดสิทธิ์ ให้ขายเชื่อ ได้ กรุณาแจ้งทางผู้จัดการหรือซุปเปอร์ ให้ทำเรื่องเปิดสิทธิ์การขายเชื่อ");
		     
		     $("#error-dialog").dialog("open");
		     jQuery("#error-dialog-message").text("ร้านค้านี้ ยังไม่ได้เปิดสิทธิ์ ให้ขายเชื่อ ได้ กรุณาแจ้งทางผู้จัดการหรือซุปเปอร์ ให้ทำเรื่องเปิดสิทธิ์การขายเชื่อ");
		     r = false;
		<%}if(receiptCreditFlag.equalsIgnoreCase("-1")){%>
		    //alert("ร้านค้านี้ ยังมีบิลเก่า ที่ยังไม่ได้เก็บเงิน  ต้องเก็บเงินบิลเก่าก่อน จึงสามารถเปิดบิลขายเชื่อใหม่");
		    
		    $("#error-dialog").dialog("open");	    
		     jQuery("#error-dialog-message").text("ร้านค้านี้ ยังมีบิลเก่า ที่ยังไม่ได้เก็บเงิน  ต้องเก็บเงินบิลเก่าก่อน จึงสามารถเปิดบิลขายเชื่อใหม่");
	        r = false;
		<%}%>
	}
	return r;
}
function validateVanCreditLimit(){
	var r = true;
	var vanPaymentMethod = document.getElementsByName("order.vanPaymentMethod")[0];
	var custCreditLimit = document.getElementsByName("custCreditLimit")[0];
	var netAmount = document.getElementsByName("order.netAmount")[0];
	if(vanPaymentMethod.value =='CREDIT'){
		//alert("creditLimit["+custCreditLimit.value+"]:netAmount["+netAmount.value+"]") ;
		if(parseFloat(netAmount.value) > parseFloat(custCreditLimit.value)){
		   //alert("creditLimit["+parseFloat(custCreditLimit.value)+"]:netAmount["+parseFloat(netAmount.value)+"]") ;
		   $("#error-dialog").dialog("open");
		   jQuery("#error-dialog-message").text("ยอดเงินที่ขาย เกินวงเงินที่กำหนด ไม่สามารถทำรายการขายได้ วงเงินที่ขายได้ของร้านนี้ ("+custCreditLimit.value+")");
		   r = false;
		}
	}
	return r;
}

$(function(){
	 //error-dailog_message
    $('#error-dialog').dialog({
					autoOpen: false,
					modal:true,
					width: 300,
					height:80,
					title:"Error Message",
					position:'center',
					resizable: false,
					dialogClass: 'brandDialog',
					buttons: {"ปิด.": 
						function() { 
						   $(this).dialog("close"); 
					     } 
					}
				});
});
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
									<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:text property="order.orderDate" maxlength="10" size="15" readonly="true" styleId="orderDate" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">
									<bean:message key="Customer" bundle="sysprop"/>&nbsp;&nbsp;
								</td>
								<td align="left" colspan="3">
									<html:text property="order.customerName" size="80" readonly="true" styleClass="disableText"/>
								    <html:hidden property="order.customerId" styleId="order.customerId" />
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryAddress" bundle="sysele"/><font color="red">*</font></td>
								<td align="left" colspan="3">
									<html:select property="order.shipAddressId" style="width:80%" disabled="true" styleClass="disableText">
										<html:options collection="addresses" property="id" labelProperty="lineString"/>
									</html:select>
									<html:hidden property="order.shipAddressId"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryDocAddress" bundle="sysele"/><font color="red">*</font></td>
								<td align="left" colspan="3">
									<html:select property="order.billAddressId" style="width:80%" disabled="true" styleClass="disableText">
										<html:options collection="addresses" property="id" labelProperty="lineString"/>
									</html:select>
									<html:hidden property="order.billAddressId"/>
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
									
									<html:hidden property="order.priceListId" styleId="order.priceListId"/>
									<html:hidden property="order.vatCode"/>
									<html:hidden property="order.paymentTerm"/>
									<html:hidden property="order.paymentMethod"/>
								</td>
							
							<tr>
								<td colspan="4" align="center">
								<div align="left">
								  N(สินค้าปกติ)&nbsp;&nbsp;&nbsp; Y(สินค้าแถมปกติ)&nbsp;&nbsp;&nbsp; S(สินค้าแถมพิเศษ)
								</div>
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
									<tr>
										<th class="td_text_center" width="5%">ลำดับ</th>
										<th class="td_text_center" width="5%"></th>
										<th class="td_text_center" width="15%">ชื่อสินค้า</th>
										<th class="td_text_center" width="5%">หน่วยนับ</th>
										<th class="td_text_center" width="5%">จำนวน</th>
										<th class="td_text_center" width="10%">ราคาต่อหน่วย</th>
										<th class="td_text_center" width="10%">ยอดรวม</th>
										<th class="td_text_center" width="10%">ส่วนลด</th>
										<th class="td_text_center" width="10%">ยอดรวมหลังหักส่วนลด</th>
										<th class="td_text_center" width="5%">วันที่ส่งสินค้า</th>							
										<th class="td_text_center" width="5%">วันที่ต้องการสินค้า</th>
										<th class="td_text_center" width="3%">ภาษี</th>
										<th class="td_text_center" width="3%">โปรโมชั่น</th>
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
										<%-- <td style="display: none;">${lines1.lineNo}</td> --%>
										<td class="td_text_center" width="5%">
										   <c:choose>
											<c:when test="${lines1.promotion=='S'}">
										       <input type="checkbox" name="lineids" value="${lines1.id}" />
										     </c:when>
										     <c:otherwise>
										        <input type="checkbox" name="lineids" value="${lines1.id}" disabled/>
										     </c:otherwise>
										    </c:choose>
										</td>
										<td class="td_text" width="15%">
											${lines1.product.code}&nbsp;${lines1.product.name}
											<input type="hidden" name='lines.id' value='${lines1.id}'>
											<input type='hidden' name='lines.row' value='${lines1.lineNo}'>
											<input type='hidden' name='lines.productId' value='${lines1.product.id}'>
											<input type='hidden' name='lines.product' value='${lines1.product.code}'>
											<input type='hidden' name='lines.productLabel' value='${lines1.product.name}'>
											
											<input type='hidden' name='lines.uom' value='${lines1.uom.id}'>
											<input type='hidden' name='lines.uom1' value='${lines1.uom1.id}'>
											<input type='hidden' name='lines.uom2' value='${lines1.uom2.id}'>
											<input type='hidden' name='lines.uomLabel' value='${lines1.uom.code}'>
											<input type='hidden' name='lines.uomLabel1' value='${lines1.uom1.code}'>
											<input type='hidden' name='lines.uomLabel2' value='${lines1.uom2.code}'>
											<input type='hidden' name='lines.fullUom' value='${lines1.fullUom}'>
											<input type='hidden' name='lines.price' value='${lines1.price}'>
											<input type='hidden' name='lines.price1' value='${lines1.price1}'>
											<input type='hidden' name='lines.price2' value='${lines1.price2}'>
											<input type='hidden' name='lines.qty' value='${lines1.qty}'>
											<input type='hidden' name='lines.qty1' value='${lines1.qty1}'>
											<input type='hidden' name='lines.qty2' value='${lines1.qty2}'>
											<input type="hidden" name='lines.amount' value='${lines1.lineAmount}'>
											<input type="hidden" name='lines.amount1' value='${lines1.lineAmount1}'>
											<input type="hidden" name='lines.amount2' value='${lines1.lineAmount2}'>
											<input type='hidden' name='lines.disc' value='${lines1.discount}'>
											<input type='hidden' name='lines.disc1' value='${lines1.discount1}'>
											<input type='hidden' name='lines.disc2' value='${lines1.discount2}'>
											<input type='hidden' name='lines.afdisc'>
											<input type='hidden' name='lines.afdisc1'>
											<input type='hidden' name='lines.afdisc2'>
											<input type="hidden" name='lines.vat' value='${lines1.vatAmount}'>
											<input type='hidden' name='lines.vat1' value='${lines1.vatAmount1}'>
											<input type='hidden' name='lines.vat2' value='${lines1.vatAmount2}'>
											<input type='hidden' name='lines.total' value='${lines1.totalAmount}'>
											<input type='hidden' name='lines.total1' value='${lines1.totalAmount1}'>
											<input type='hidden' name='lines.total2' value='${lines1.totalAmount2}'>
											
											<input type='hidden' name='lines.ship' value='${lines1.shippingDate}'>
											<input type='hidden' name='lines.req' value='${lines1.requestDate}'>
											<input type='hidden' name='lines.promo' value='${lines1.promotion}'>
											<input type='hidden' name='lines.lineno' value='${lines1.lineNo}'>
											<input type='hidden' name='lines.tripno' value='${lines1.tripNo}'>
											<input type='hidden' name='lines.taxable' value='${lines1.taxable}'>
											<input type='hidden' name='lines.sellingPrice' value='${lines1.sellingPrice}'>
										</td>
										<td class="td_text_right" width="5%">
									        ${lines1.fullUom}
										</td>
										<td class="td_text_right" width="5%">
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
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/>
										</td>
										<td class="td_text_right" width="10%">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount - lines1.discount}"/>
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
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
									<tr>
										<td align="left" class="footer">&nbsp;
											<a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}','<%=user.getType() %>');"> 
											   <img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
											   <bean:message key="Delete" bundle="sysprop"/>
											</a>
										</td>
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
								<td align="right">ยอดรวมที่ไม่เสียภาษี&nbsp;&nbsp;</td>
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
								<td colspan="4"><hr/></td>
							</tr>
							<tr>
								<td></td>
								<%if(User.VAN.equals(user.getType())){%>
									<td class="textSpecial">
										     ชำระโดย
										 <html:select property="order.vanPaymentMethod">
											<html:options collection="vanPaymentMethod" property="key" labelProperty="name"/>
										</html:select>
								   </td>
								<%}else if(User.TT.equals(user.getType())){%>	
							       <html:hidden property="order.vanPaymentMethod"/>
								<!-- New Issue 15/05/2555 Add Bill Place -->
								<td nowrap>
									    สถานที่ออกสินค้า/พิมพ์บิล :
									    <html:select property="order.placeOfBilled"  styleClass="normalText">
											<html:options collection="w1List" property="key" labelProperty="name"/>
										</html:select>
										</td>
									 <%}else{ %>
									 <td></td>
									<%} %>
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
								<td align="right"><bean:message key="Order.No" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesOrderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="Bill.No" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.arInvoiceNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">Sales Representative&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
									<html:hidden property="order.salesRepresent.id"/>
									<html:hidden property="order.salesRepresent.code"/>
								</td>
								<td align="right"><bean:message key="Status" bundle="sysele"/><font color="red">*</font></td>
								<td valign="top">
									<html:select property="order.docStatus" disabled="true" styleClass="disableText">
										<html:options collection="docstatus" property="key" labelProperty="name"/>
									</html:select>
									<html:hidden property="order.docStatus"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<input type="button" value="แก้ไขสินค้า" class="newPosBtn" onclick="return backadd('${pageContext.request.contextPath}');">
									<!-- WIT EDIT:04/08/2554 ***************-->
<!--									<a href="#" onclick="return autoReceipt('${pageContext.request.contextPath}','<%=user.getType() %>');">-->
<!--									    <input type="button" value="บันทึก" class="newPosBtn">-->
<!--									</a>-->
									<!-- WIT EDIT:04/08/2554 ***************-->

									<!-- OLD CODE ************************ -->
									<% if(request.getAttribute("do_not_save") == null){%>
									   <input type="button" value="บันทึก" class="<%=saveBtnStyle %>" onclick="save('${pageContext.request.contextPath}','<%=user.getType() %>')" <%=saveBtnDisable %>>
									<%} %>
									<!-- OLD CODE ************************ -->
									<%if(User.VAN.equals(user.getType())){%>
									 
									   <input type="button" value="พิมพ์ใบหยิบของ" class="newPosBtnLong" onclick="stampPrint();printListOrderProductReport('${pageContext.request.contextPath}','<%=user.getType() %>')">
									   
									<%} %>		
									<input type="button" value="ยกเลิก" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderSpecialForm.order.customerId}');">
									
								</td>
							</tr>
						</table>
						<span title="SalesSpecialOrderPreview">...</span>
						<!-- AUTO RECEIPT -->
						<html:hidden property="autoReceiptFlag"/>
						<html:hidden property="autoReceipt.paymentMethod"/>
						<html:hidden property="autoReceipt.bank"/>
						<html:hidden property="autoReceipt.chequeNo"/>
						<html:hidden property="autoReceipt.chequeDate"/>
						<html:hidden property="autoReceipt.creditCardType"/>
						<html:hidden property="autoReceipt.internalBank"/>
						<!--  -->
						<!--  Can Receipt Credit (VAN)-->
						<html:hidden property="receiptCreditFlag"/>
					    <html:hidden property="custCreditLimit"/>
					
						<html:hidden property="deletedId"/>
						<html:hidden property="order.orderType"/>
						<html:hidden property="order.id"/>
						<html:hidden property="order.customerId"/>
						<html:hidden property="order.exported"/>
						<html:hidden property="order.isCash"/>
						
						<html:hidden property="order.printDateTimePick"/>
						<html:hidden property="order.printCountPick"/>
						
						<!-- ForTest -->
					 	<%-- printDateTimePick:<html:text property="order.printDateTimePick"/><br/>
						printCountPick:<html:text property="order.printCountPick"/> --%>
						
						<input type="hidden" name="memberVIP" value="${memberVIP}"/>
						<div id="productList" style="display: none;"></div>
						<div id="ByList" style="display: none;"></div>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
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
<div id="error-dialog"><div id="error-dialog-message" style="color:red;"></div></div>