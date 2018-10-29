<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.web.sales.OrderForm"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
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
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" /> 
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();

String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />

<style type="text/css">
.h1{
 font-size: 20px;
 font-weight: bold;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrderProduct.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript">
function loadMe(){
	calculatePrice();	
	//new Epoch('epoch_popup','th',document.getElementById('orderDate'));
	<%if(request.getAttribute("Message") != null){%>
	  alert("<%=request.getAttribute("Message")%>");
	<%} %>
	
	//display payment method
	<%if( !Utils.isNull(orderForm.getOrder().getPaymentMethod()).equals("")){%>
	  document.getElementById("paymentMethod").value ="<%=orderForm.getOrder().getPaymentMethod()%>";
	  changePaymentMethod(document.getElementById("paymentMethod"));
	<%}else{%>
	   changePaymentMethod(document.getElementById("paymentMethod"));
	<%}%>
}

function printListOrderProductReport(path,userType){
	var customerId = document.getElementsByName("order.customerId");
   // window.open(path + "/jsp/saleOrderAction.do?do=printListOrderProductReport&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
	window.open(path + "/jsp/pop/printPopup.jsp?report_name=list_order_product&customerId="+customerId[0].value, "Print2", "width=100,height=100,location=No,resizable=No");
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
function changePaymentMethod(paymentMethod){
	if("CS" ==paymentMethod.value ){
		document.getElementById("div_credit_1").style.display = "none";
		document.getElementById("div_credit_2").style.display = "none";
		document.getElementById("div_credit_3").style.display = "none";
		document.getElementById("div_credit_4").style.display = "none";
	}else{
		document.getElementById("div_credit_1").style.display = "block";
		document.getElementById("div_credit_2").style.display = "block";
		document.getElementById("div_credit_3").style.display = "block";
		document.getElementById("div_credit_4").style.display = "block";
	}
}
/** Calc Change Amount onkeypress**/
function calcChangeAmount(e,custAmountObj){
    //alert(e.keyCode);
	if(e != null && e.keyCode == 13){
		//validate input is number 2 digit
		if( !isNum2DigitValue(custAmountObj.value)){
			alert("กรุณากรอก จำนวนเงินเป็นตัวเลขเท่านั้น");
			custAmountObj.value ="";
			custAmountObj.focus();
			return false;
		}
		
		calcChangeAmountModel(custAmountObj);
	}
}

/** Calc Change Amount onchange**/
function calcChangeAmountOnchange(custAmountObj){
	//validate input is number 2 digit
	if( !isNum2DigitValue(custAmountObj.value)){
		alert("กรุณากรอก จำนวนเงินเป็นตัวเลขเท่านั้น");
		custAmountObj.value ="";
		custAmountObj.focus();
		return false;
	}
	
	calcChangeAmountModel(custAmountObj);
}
function calcChangeAmountModel(custAmountObj){
    //alert(e.keyCode);
	if(custAmountObj.value != ""){
	  var netAmount = convetTxtObjToFloat(document.getElementById("tempNetAmount"));
	  //alert("netAmount["+document.getElementById("tempNetAmount").value+"]:"+netAmount+",custAmount:"+custAmountObj.value);
      if(Number(custAmountObj.value)< netAmount){
    	  alert("จำนวนเงินจากลูกค้า น้อยกว่า ยอดสุทธิ กรุณาใส่จำนวนเงินใหม่");
    	  document.getElementById("tempChangeAmount").value = "";
    	  custAmountObj.focus();
      }else{
	     var changeAmoutResult = Number(custAmountObj.value)-netAmount;
	     document.getElementById("tempChangeAmount").value =  addCommas(Number(changeAmoutResult).toFixed(2));
	     custAmountObj.value =Number(custAmountObj.value).toFixed(2);
      }
	}else{
		alert("กรุณาระบุจำนวนเงิน ");
		custAmountObj.focus();
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
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						   <tr>
								<td width="30%" align="right">ร้านค้าหลัก</td>
								<td width="25%" align="left">
								  <html:text property="order.customerName" styleId="order.customerName"  readonly="true" styleClass="disableText"/> 
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
									<html:text property="order.customerBillName" size="40" styleClass="\" autoComplete=\"off" tabindex="1"/>		
								</td>
							</tr>
							<tr>
								<td align="right">ที่อยู่<font color="red"></font></td>
								<td align="left" colspan="3">
									<html:text property="order.addressDesc" size="80"  styleClass="\" autoComplete=\"off" tabindex="2" />
								</td>
							</tr>
							<tr>
								<td align="right">บัตรประชาชน<font color="red"></font></td>
								<td align="left" colspan="2">
									<html:text property="order.idNo" size="20" maxlength="13" 
									onkeydown="return inputNum(event);" styleClass="\" autoComplete=\"off"  tabindex="3"/>
									&nbsp;&nbsp;&nbsp;&nbsp;
									Passport No &nbsp;
									<html:text property="order.passportNo" size="20" styleClass="\" autoComplete=\"off"  tabindex="4"/>
								    &nbsp;&nbsp;&nbsp;&nbsp;  
							</td> 
							 <td align="left">   
								         อัตราภาษี &nbsp;
									<html:text property="order.vatCode" value="7" readonly="true" size="2" styleClass="disableText"  tabindex="-1"/>
									
									<html:hidden property="order.priceListId" styleId="order.priceListId"/>
									<html:hidden property="order.paymentTerm"/>
									<html:hidden property="order.oraBillAddressID"/>
									<html:hidden property="order.oraShipAddressID"/>
								</td>
							</tr> 
							<tr>
								<td colspan="4" align="center">
								<div id="divTableProductHead" style="width: 100%; height:50px; overflow-y: scroll;">
								<table id="tblProductHead" align="left" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
									<tr>
										<th class="td_text_center" width="4%">ลำดับ</th>
										<th class="td_text_center" width="5%"><input type="checkbox" disabled name="chk_temp" class="disableCheckbox"/></th>
										<th class="td_text_center" width="20%">ชื่อสินค้า</th>
										<th class="td_text_center" width="10%">หน่วยนับ</th>
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
								<div id="divTableProduct" style="width: 100%; height: 270px; overflow-y: scroll;">
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
									
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
										<td class="td_text_center" width="4%">${lines1.lineNo}</td>
										<td class="td_text_center" width="5%"><input type="checkbox" disabled name="chk_temp" class="disableCheckbox"/></td>
										<td class="td_text" width="20%">
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
										<td class="td_text_center" width="10%">${lines1.fullUom}</td>
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
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/>
										</td>
										<td class="td_text_right" width="10%">${lines1.promotion}:
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
										<td class="td_text_center" width="5%">
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
							      <tr>
									<td class="td_text_right" width="40%" colspan="5"><span class="h1">รวมจำนวน</span></td>
									<td  class="td_text_right" width="10%">
									   <!-- <input type="text" name="totalQty" id="totalQty" size="10" 
									   class="disableBoldNumber"> -->
									  <b> <span id="totalQty" class="h1"></span></b>
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
								<!-- <td align="left" valign="top" >   
								</td> -->
								<td align="left" colspan="3">
								  <table align="center" border="0" cellpadding="3" cellspacing="1" width="70%">
								      <tr>
									      <td align="left" width="10%"></td>
									      <td align="left" width="10%"></td>
									      <td align="center" width="15%"><span id="div_credit_1" style="display:none"><b> เลขที่บัตร</b></span></td>
									      <td align="center" width="15%"><span id="div_credit_2" style="display:none"><b> หมดอายุ  MM/YY</b></span></td>
									   </tr>
									   <tr>
									      <td align="left"></td>
									      <td align="left" nowrap> <b>ชำระโดย</b> &nbsp;
									         <html:select property="order.paymentMethod" styleId="paymentMethod" onchange="changePaymentMethod(this)">
										       <html:options collection="paymentMethod" property="key" labelProperty="name"/>
									         </html:select> 
									      </td>
									      <td align="left" nowrap>
									         <span id="div_credit_3" style="display:none">
									            <html:select property="order.creditCardType" >
										           <html:option value="VISA">VISA</html:option>
										           <html:option value="MASTER">MASTER</html:option>
									            </html:select> 
									           <html:text property="order.creditCardNo" styleId="creditCardNo"  styleClass="\" autoComplete=\"off"
									           size="30" maxlength="16" onkeydown="return inputNum(event);"/>
									           <font color="red">*</font>
									         </span>
									      </td>
									      <td align="left" nowrap>
									        <span id="div_credit_4" style="display:none" align="center">
									          <%--  <html:text property="order.creditCardExpireDate" styleId="creditCardExpireDate" 
									            size="15" maxlength="5" styleClass="\" autoComplete=\"off"  /> --%>
									            
									            <html:select property="order.creditcardMonthExpire" styleId="creditcardMonthExpire">
										           <html:option value="01">01</html:option>
										           <html:option value="02">02</html:option>
										           <html:option value="03">03</html:option>
										           <html:option value="04">04</html:option>
										           <html:option value="05">05</html:option>
										           <html:option value="06">06</html:option>
										           <html:option value="07">07</html:option>
										           <html:option value="08">08</html:option>
										           <html:option value="09">09</html:option>
										           <html:option value="10">10</html:option>
										           <html:option value="11">11</html:option>
										           <html:option value="12">12</html:option>
					 				            </html:select> 
					 				            /
					 				            <html:select property="order.creditcardYearExpire" styleId="creditcardYearExpire">
										            <html:options collection="CAREDITCARD_YEAR_EXPIRE_LIST" property="key" labelProperty="name"/>
									            </html:select> 
									            <font color="red">*</font>
									        </span>
									      </td>
									    </tr>
									 <tr>
									 <td colspan="4"></td>
									 </tr>
									 <tr>
									   <td colspan="4" align="right">
									       <input type="button" value="แก้ไขสินค้า" class="newPosBtn" onclick="return backadd('${pageContext.request.contextPath}');">
											&nbsp;&nbsp;&nbsp;
											<% if(request.getAttribute("do_not_save") == null){%>
											   <input type="button" value="  บันทึก   " class="newPosBtnLong" onclick="save('${pageContext.request.contextPath}','<%=user.getType() %>')">
											&nbsp;&nbsp;&nbsp;
											<%} %>
											<input type="button" value=" ยกเลิก  " class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									   </td>
									 </tr>
								   </table>
								</td>
								<td align="left" valign="top"  >
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
									    <tr>
								          <td colspan="3" ><hr></td>
								        </tr>
									     <tr>
									       <td align="right" nowrap style="color:#00e600;"><b><font size ="4">รับเงินจากลูกค้า</font></b></td>
									       <td align="right">
									         <!--  <input type="text" id="tempCustAmount" name="tempCustAmount" size="13" 
									           class="enableBoldBigGreenText" style="text-align: right;"
                                               onkeypress="calcChangeAmount(event,this)" /> -->
                                               
                                               <html:text property="order.tempCustAmount" size="14" styleId="tempCustAmount"
                                                onkeypress="calcChangeAmount(event,this)"
                                                onchange="calcChangeAmountOnchange(this)"
                                                styleClass="enableBoldBigGreenNumber \" autoComplete=\"off"  tabindex="8"/>
									       </td>
									    </tr>
									    <tr>
									       <td align="right" nowrap style="color:#ff471a;"><b><font size ="4">เงินทอน</font></b></td>
									       <td align="right">
									          <!-- <input type="text" id="tempChangeAmount" name="tempChangeAmount" size="13" 
									          readonly="readonly" class="disableBoldBigRedText" style="text-align: right;"/> -->
									          
									           <html:text property="order.tempChangeAmount" size="14" styleId="tempChangeAmount"
                                               styleClass="disableBoldBigRedNumber" readonly="true" tabindex="-1"/>
									       </td>
									    </tr>
								   </table> 
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									
								</td>
							</tr>
						</table>
						<span title="SalesOrderPreview_">...</span>
						<!-- AUTO RECEIPT -->
						 <html:hidden property="autoReceiptFlag"/>
						<html:hidden property="autoReceipt.paymentMethod"/>
						<html:hidden property="autoReceipt.bank"/>
						<html:hidden property="autoReceipt.chequeNo"/>
						<html:hidden property="autoReceipt.chequeDate"/>
						<html:hidden property="autoReceipt.creditCardType"/>
						<html:hidden property="autoReceipt.internalBank"/>
						<!--  Can Receipt Credit (VAN)-->
						<html:hidden property="receiptCreditFlag"/>
					    <html:hidden property="custCreditLimit"/>
					
						<html:hidden property="deletedId"/>
						<html:hidden property="order.orderType"/>
						<html:hidden property="order.id"/>
						<html:hidden property="order.customerId"/>
						<html:hidden property="order.exported"/>
						<html:hidden property="order.isCash"/>
						
						<html:hidden property="order.shipAddressId"/>
						<html:hidden property="order.billAddressId"/>
						
						<html:hidden property="order.printDateTimePick"/>
						<html:hidden property="order.printCountPick"/> 
						<html:hidden property="order.docStatus" value="SV"/> 
						
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