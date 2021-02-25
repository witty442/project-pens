<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<%
%>
<div align="left">&nbsp;&nbsp;„∫≈¥Àπ’È</div>
<div class="table-responsive">
  <table class="table table-bordered table-striped table-light"
     id="tblCN" width="100%" cellspacing="0">
    <thead class="thead-dark">
	<tr>
		<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
		<th>‡≈¢∑’Ë„∫≈¥Àπ’È</th>
		<th><bean:message key="Bill.No"  bundle="sysele"/></th>
		<th class="costprice"><bean:message key="TotalAmount"  bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Order.Behindhand" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="AmountReceived" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Product.Balance" bundle="sysele"/></th>
	</tr>
	</thead>
	<c:forEach var="resultsCN" items="${receiptForm.cns}" varStatus="rowsCN">
	<c:choose>
		<c:when test="${rowsCN.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="${tabclass}">
		<td>${rowsCN.index+1}</td>
		<td align="center">
			${resultsCN.creditNote.creditNoteNo}
			<input type='hidden' name='cn.id' value='${resultsCN.id}'>
			<input type='hidden' name='cn.cnId' value='${resultsCN.creditNote.id}' >
			<input type='hidden' name='cn.cnNo' value='${resultsCN.creditNote.creditNoteNo}' >
			<input type='hidden' name='cn.cnInvoiceNo' value='${resultsCN.creditNote.arInvoiceNo}'>
			<input type='hidden' name='cn.totalAmount' value='${resultsCN.creditNote.totalAmount}'>
			<input type='hidden' name='cn.creditAmt' value='${resultsCN.creditAmount}'>
			<input type='hidden' name='cn.paidAmt' value='${resultsCN.paidAmount}'>
			<input type='hidden' name='cn.remainAmt' value='${resultsCN.remainAmount}'>
		</td>
		<td align="center">${resultsCN.creditNote.arInvoiceNo}</td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${resultsCN.creditNote.totalAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${resultsCN.creditAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${resultsCN.paidAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${resultsCN.remainAmount}"/></td>
	</tr>
	</c:forEach>							
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td align="left" colspan="8" class="footer">&nbsp;</td>
	</tr>
</table>
</div>
