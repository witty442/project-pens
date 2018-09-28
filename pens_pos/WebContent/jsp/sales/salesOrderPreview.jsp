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

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />

<style type="text/css"></style>
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
								<td width="30%" align="right">��ҹ�����ѡ</td>
								<td width="25%" align="left"><html:text property="order.customerName" styleId="order.customerName" /> 
								   <html:hidden property="order.customerId" styleId="order.customerId" /> 
								</td>
								<td width="15%" align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><b>�������١�������Ѻ�͡���</b></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><font color="red"></font></td>
								<td align="left">
									<html:text property="order.orderDate" maxlength="10" size="15" readonly="true" styleId="orderDate" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">&nbsp;&nbsp;����-���ʡ��<font color="red"></font></td>
								<td align="left" colspan="3">
									<html:text property="order.customerBillName" size="40" styleClass="normalText"/>		
								</td>
							</tr>
							<tr>
								<td align="right">�������<font color="red"></font></td>
								<td align="left" colspan="3">
									<html:text property="order.addressDesc" size="80" styleClass="normalText"/>
								</td>
							</tr>
							<tr>
								<td align="right">�ѵû�ЪҪ�<font color="red"></font></td>
								<td align="left" colspan="2">
									<html:text property="order.idNo" size="20" styleClass="normalText"/>
									&nbsp;&nbsp;&nbsp;&nbsp;
									Passport No &nbsp;
									<html:text property="order.passportNo" size="20" styleClass="normalText"/>
								    &nbsp;&nbsp;&nbsp;&nbsp;  
							</td> 
							 <td align="left">   
								         �ѵ������ &nbsp;
								    <html:select property="order.vatCode" disabled="true" onchange="calculatePrice();" styleClass="disableText">
										<html:options collection="vatcodes" property="key" labelProperty="name"/>
									</html:select>
									
									<html:hidden property="order.priceListId" styleId="order.priceListId"/>
									<html:hidden property="order.vatCode"/>
									<html:hidden property="order.paymentTerm"/>
									<html:hidden property="order.oraBillAddressID"/>
									<html:hidden property="order.oraShipAddressID"/>
								</td>
							</tr> 
							<tr>
								<td colspan="4" align="center">
								<div id="divTableProduct" style="width: 100%; height: 200px; overflow-y: scroll;">
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<th class="order"><bean:message key="No" bundle="sysprop"/></th>
										<td style="display: none;"></td>
										<th><bean:message key="Product.Name" bundle="sysele"/></th>
										<th><bean:message key="Product.UOM" bundle="sysele"/></th>
										<th><bean:message key="Quantity" bundle="sysele"/></th>
										<th><bean:message key="Price.Unit" bundle="sysele"/></th>
										<th><bean:message key="Total" bundle="sysele"/></th>
										<th><bean:message key="Discount" bundle="sysele"/></th>
										<th><bean:message key="TotalExcludeDiscount" bundle="sysele"/></th>
										<th><bean:message key="Order.ShipmentDate" bundle="sysele"/></th>					
										<th><bean:message key="Order.RequiredDate" bundle="sysele"/></th>
										<th>����</th>
										<th><bean:message key="Promotion" bundle="sysele"/></th>
									</tr>
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
										<td style="display: none;">${lines1.lineNo}</td>
										<td align="left">
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
										</td>
										<td align="center">
									        ${lines1.fullUom}
										</td>
										<td align="right">
											
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
										<td align="right">
											
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
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount - lines1.discount}"/>
										</td>

										<td align="center">${lines1.shippingDate}</td>
										<td align="center">${lines1.requestDate}</td>
										<td align="center">
											<c:if test="${lines1.taxable=='Y'}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if>
										</td>
										<td align="center">
											<c:if test="${lines1.promotion=='Y'}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if>
										</td>
									</tr>
									</c:forEach>
								</table>
								</div>
								<hr>
								</td>
							</tr>
							<tr>
								<!-- <td align="left" valign="top" >   
								</td> -->
								<td align="left" colspan="3">
								  <table align="center" border="0" cellpadding="3" cellspacing="1" width="70%">
								      <tr>
									      <td align="left" width="10%"></td>
									      <td align="left" width="10%"></td>
									      <td align="center" width="15%"><span id="div_credit_1" style="display:none"><b> �Ţ���ѵ�</b></span></td>
									      <td align="center" width="15%"><span id="div_credit_2" style="display:none"><b> �������  MM/YY</b></span></td>
									   </tr>
									   <tr>
									      <td align="left"></td>
									      <td align="left" nowrap> <b>������</b> &nbsp;
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
									         </span>
									      </td>
									      <td align="left">
									        <span id="div_credit_4" style="display:none" align="center">
									           <html:text property="order.creditCardExpireDate" styleId="creditCardExpireDate" 
									            size="15" maxlength="5" styleClass="\" autoComplete=\"off"  />
									        </span>
									      </td>
									    </tr>
									 <tr>
									 <td colspan="4"></td>
									 </tr>
									 <tr>
									   <td colspan="4" align="right">
									       <input type="button" value="����Թ���" class="newPosBtn" onclick="return backadd('${pageContext.request.contextPath}');">
											
											<% if(request.getAttribute("do_not_save") == null){%>
											   <input type="button" value="�ѹ�֡" class="newPosBtnLong" onclick="save('${pageContext.request.contextPath}','<%=user.getType() %>')">
											<%} %>
											<input type="button" value="¡��ԡ" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									   </td>
									 </tr>
								   </table>
								</td>
								<td align="left" valign="top"  >
								   <table align="left" border="0" cellpadding="3" cellspacing="1">
									    <tr>
									      <td align="right" nowrap><b>�ʹ�����͹����</b></td>
									      <td align="left">
									        <input type="text" id="tempTotalAmount" name="tempTotalAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
										     <html:hidden property="order.totalAmount"/>
									      </td>
									    </tr>
									    <tr>
									       <td align="right" nowrap><b>����</b></td>
									       <td align="left">
									         <input type="text" id="tempVatAmount" name="tempVatAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									         <html:hidden property="order.vatAmount"/>
									       </td>
									    </tr>
									    <tr>
									       <td align="right" nowrap><b>�ʹ�����������������</b></td>
									       <td align="left">
									           <input type="text" id="tempTotalAmountNonVat" name="tempTotalAmountNonVat" readonly="readonly" class="disableText" style="text-align: right;"/>
									           <html:hidden property="order.totalAmountNonVat"/>
									       </td>
									    </tr>
									    <tr>
									       <td align="right" nowrap><b>�ʹ�ط��</b></td>
									       <td align="left">
									          <input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									           <html:hidden property="order.netAmount"/>
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
									<%-- <input type="button" value="����Թ���" class="newPosBtn" onclick="return backadd('${pageContext.request.contextPath}');">
									
									<% if(request.getAttribute("do_not_save") == null){%>
									   <input type="button" value="�ѹ�֡" class="newPosBtnLong" onclick="save('${pageContext.request.contextPath}','<%=user.getType() %>')">
									<%} %>
								
									<%if(User.VAN.equals(user.getType())){%>
									   <input type="button" value="��������Ժ�ͧ" class="newPosBtnLong" onclick="stampPrint();printListOrderProductReport('${pageContext.request.contextPath}','<%=user.getType() %>')">
									    
									<%} %>		
									<input type="button" value="¡��ԡ" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									 --%>
								</td>
							</tr>
						</table>
						<span title="SalesOrderPreview">...</span>
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