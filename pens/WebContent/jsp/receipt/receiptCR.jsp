<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<div align="left">&nbsp;&nbsp;<input type="button" value="เพิ่มใบแจ้งหนี้" onclick="open_bill('${pageContext.request.contextPath}', 0);"/></div>
<table id="tblBill" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
		<th class="checkBox">
			<input type="checkbox" name="chkAll" onclick="checkSelect(this,document.getElementsByName('billids'));" />
		</th>
		<th class="name"><bean:message key="Bill.No"  bundle="sysele"/></th>
		<th class="name"><bean:message key="Order.No"  bundle="sysele"/></th>
		<th class="costprice"><bean:message key="TotalAmount"  bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Order.Behindhand" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="AmountReceived" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Product.Balance" bundle="sysele"/></th>
		<!-- <th class="status"><bean:message key="Edit" bundle="sysprop"/></th> -->
	</tr>
	<c:forEach var="results" items="${receiptForm.lines}" varStatus="rows1">
	<c:choose>
		<c:when test="${rows1.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="${tabclass}">
		<td>${results.lineNo}</td>
		<td align="center"><input type="checkbox" name="billids" value="${results.id}"/></td>
		<td align="center">
			${results.arInvoiceNo}
			<input type="hidden" name='bill.id' value='${results.id}'>
			<input type='hidden' name='bill.orderId' value='${results.order.id}' >
			<input type='hidden' name='bill.invoiceNo' value='${results.arInvoiceNo}'>
			<input type='hidden' name='bill.salesOrderNo' value='${results.salesOrderNo}'>
			<input type='hidden' name='bill.netAmt' value='${results.invoiceAmount}'>
			<input type='hidden' name='bill.creditAmt' value='${results.creditAmount}'>
			<input type='hidden' name='bill.paidAmt' value='${results.paidAmount}'>
			<input type='hidden' name='bill.remainAmt' value='${results.remainAmount}'>
		</td>
		<td align="center">${results.salesOrderNo}</td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.invoiceAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.creditAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.paidAmount}"/></td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${results.remainAmount}"/></td>
		<!-- 
		<td align="center">
			<a href="#" onclick="editBill('${pageContext.request.contextPath}','${rows1.index+1}');">
			<img border=0 src='${pageContext.request.contextPath}/icons/doc_edit.gif' align="absmiddle"></a>
		</td>
		 -->
	</tr>
	</c:forEach>							
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td align="left" colspan="8" class="footer">&nbsp;
			<a href="javascript:deleteBill('${pageContext.request.contextPath}');"> 
			<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif"> ลบรายการ</a>
		</td>
	</tr>
</table>
								