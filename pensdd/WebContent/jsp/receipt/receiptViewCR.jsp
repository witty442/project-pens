<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
	User user = (User)session.getAttribute("user");
%>

<%@page import="com.isecinc.pens.bean.User"%><jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<div align="left">&nbsp;&nbsp;���˹��</div>
<table id="tblBill" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
		<%if(!user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
		<th class="code"><bean:message key="Bill.No"  bundle="sysele"/></th>
		<th class="code"><bean:message key="Order.No"  bundle="sysele"/></th>
		<%} %>
		<%if(user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
		<th class="code"><bean:message key="Order.No"  bundle="sysele"/></th>
		<%} %>
		<th><bean:message key="Description" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="TotalAmount"  bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Order.Behindhand" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="AmountReceived" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Product.Balance" bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${receiptForm.lines}" varStatus="rows">
	<c:choose>
		<c:when test="${rows.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="<c:out value='${tabclass}'/>">
		<td align="center">${rows.index+1}</td>
		<%if(!user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
		<td align="center">${results.arInvoiceNo}</td>
		<td align="center">${results.salesOrderNo}</td>
		<%} %>
		<%if(user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
		<td align="center">${results.order.orderNo}</td>
		<%} %>
		<td align="left">
			${results.description}
			<input type="hidden" name='bill.id' value='${results.id}'>
			<input type='hidden' name='bill.orderId' value='${results.order.id}' >
			<input type='hidden' name='bill.invoiceNo' value='${results.arInvoiceNo}'>
			<input type='hidden' name='bill.salesOrderNo' value='${results.salesOrderNo}'>
			<input type='hidden' name='bill.netAmt' value='${results.invoiceAmount}'>
			<input type='hidden' name='bill.creditAmt' value='${results.creditAmount}'>
			<input type='hidden' name='bill.paidAmt' value='${results.paidAmount}'>
			<input type='hidden' name='bill.remainAmt' value='${results.remainAmount}'>
		</td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.invoiceAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.creditAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.paidAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.remainAmount}"/></td>
	</tr>
	</c:forEach>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<%if(!user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
		<td align="left" colspan="8" class="footer">&nbsp;</td>
		<%}else{ %>
		<td align="left" colspan="6" class="footer">&nbsp;</td>
		<%} %>
	</tr>
</table>
