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
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();" style="height: 100%;">
<!-- PROGRAM HEADER -->
<jsp:include page="../program.jsp">
	<jsp:param name="function" value="SalesOrder"/>
	<jsp:param name="code" value="${orderForm.order.orderNo}"/>
</jsp:include>
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
			<html:text property="order.orderDate" maxlength="10" size="18" readonly="true" styleClass="disableText"/>
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
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Profile.PaymentMethod" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<html:select property="order.paymentMethod" disabled="true" styleClass="disableText">
				<html:options collection="paymentMethod" property="key" labelProperty="name"/>
			</html:select>
		</td>
		<td colspan="2">
			<html:hidden property="order.priceListId"/>
			<html:hidden property="order.vatCode"/>
			<html:hidden property="order.paymentTerm"/>
			<html:hidden property="order.paymentMethod"/>
		</td>
	</tr>
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
				<th><bean:message key="Tax" bundle="sysele"/></th>
				<th><bean:message key="Overall" bundle="sysele"/></th>
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
				<th><bean:message key="Order.Paid" bundle="sysele"/></th>
				<th><bean:message key="Bill.No" bundle="sysele"/></th>
				<%} %>
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
								<fmt:formatNumber pattern="#,##0.00" value="${lines1.price1}"/>
							</c:when>
							<c:otherwise>
								<fmt:formatNumber pattern="#,##0.00" value="${lines1.price}"/>
							</c:otherwise>
						</c:choose>
					<%}else{ %>
					<c:choose>
						<c:when test="${lines1.promotion=='Y'}">
							<fmt:formatNumber pattern="#,##0.00" value="0"/>
						</c:when>
						<c:otherwise>
							<fmt:formatNumber pattern="#,##0.00" value="${lines1.price1}"/>/
							<fmt:formatNumber pattern="#,##0.00" value="${lines1.price2}"/>												
						</c:otherwise>
					</c:choose>											
					<%} %>
				</td>
				<td align="right">
					<fmt:formatNumber pattern="#,##0.00" value="${lines1.lineAmount}"/>
				</td>
				<td align="right">
					<fmt:formatNumber pattern="#,##0.00" value="${lines1.discount}"/>
				</td>
				<td align="right">
					<fmt:formatNumber pattern="#,##0.00" value="${lines1.lineAmount - lines1.discount}"/>
				</td>
				<td align="right">
					<!--<fmt:formatNumber pattern="#,##0.00" value="${lines1.vatAmount1 + lines1.vatAmount2}"/>-->
					<fmt:formatNumber pattern="#,##0.00" value="${lines1.vatAmount}"/>
				</td>
				<td align="right">
					<fmt:formatNumber pattern="#,##0.00" value="${lines1.totalAmount}"/>
				</td>
				<%if(user.getType().equalsIgnoreCase(User.DD)){ %>
				<td align="center">${lines1.tripNo}</td>
				<%} %>
				<td align="center">${lines1.shippingDate}</td>
				<td align="center">${lines1.requestDate}</td>
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
		<td align="right"><bean:message key="TotalAmount" bundle="sysele"/>&nbsp;&nbsp;</td>
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
		<td align="left">
			<html:checkbox property="order.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
		</td>
		<td align="right"></td>
		<td valign="top">
			<html:checkbox property="order.payment" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Order.Paid" bundle="sysele"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td align="left">
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
		<td align="right"></td>
		<td align="right" width="20%">
			<a href="#" onclick="window.close();">
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
					
</body>
</html>