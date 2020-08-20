<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
User user = (User) session.getAttribute("user");
%>

<%@page import="com.isecinc.pens.bean.User"%><jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<div align="left">&nbsp;&nbsp;ประเภทการชำระเงิน</div>
<table id="tblRecpBy" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
		<th><bean:message key="Profile.PaymentMethod" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Receipt.Amount" bundle="sysele"/></th>
		<th class="name"><bean:message key="Bank" bundle="sysele"/> </th>
		<th>เลขที่เช็ค/หมายเลขบัตรเครดิต/เลขที่ชำระแอร์เพย์</th>
		<th> วันที่หน้าเช็ค/วันที่โอนเงิน </th>
		<th class="costprice"><bean:message key="CreditCardType" bundle="sysele"/></th>
		<th>เซลล์จ่าย</th>
		<%if(!user.getType().equalsIgnoreCase(User.DD)){ %>
		<th class="status"><bean:message key="Receipt.Paid" bundle="sysele"/></th>
		<%} %>
	</tr>
	<c:forEach var="bys" items="${receiptForm.bys}" varStatus="rows2">
	<c:choose>
		<c:when test="${rows2.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="<c:out value='${tabclass}'/>">
		<td align="center">${rows2.index+1}</td>
		<td align="center">
			${bys.paymentMethodName}
			<input type='hidden' name='pb.id' value='${bys.id}'>
			<input type='hidden' name='pb.method' value='${bys.paymentMethod}'>
			<input type='hidden' name='pb.methodName' value='${bys.paymentMethodName}'>
			<input type='hidden' name='pb.recAmount' value='${bys.receiptAmount}'>
			<input type='hidden' name='pb.bank' value='${bys.bank}'>
			<input type='hidden' name='pb.bankName' value='${bys.bankName}'>
			<input type='hidden' name='pb.chqNo' value='${bys.chequeNo}'>
			<input type='hidden' name='pb.chqDate' value='${bys.chequeDate}'>
			<input type='hidden' name='pb.creditType' value='${bys.creditCardType}'>
			<input type='hidden' name='pb.creditTypeName' value='${bys.creditTypeName}'>
			<input type="hidden" name='pb.seedId' value="${bys.seedId}"/>
			<input type="hidden" name='pb.allBillId' value="${bys.allBillId}"/>
			<input type="hidden" name='pb.allPaid' value="${bys.allPaid}"/>
			<input type="hidden" name='pb.allCNId' value="${bys.allCNId}"/>
			<input type="hidden" name='pb.allCNPaid' value="${bys.allCNPaid}"/>
		</td>
		<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${bys.receiptAmount}"/></td>
		<td align="left">${bys.bankName}</td>
		<td align="left">${bys.chequeNo}</td>
		<td align="center">${bys.chequeDate}</td>
		<td align="center">${bys.creditCardType}</td>
		<td align="center">
			<c:if test="${bys.writeOff=='Y'}">
			<input type="checkbox" name="pb.writeOff" value="Y" checked="checked" disabled="disabled" style="disableText"/>
			</c:if>
			<c:if test="${bys.writeOff!='Y'}">
			<input type="checkbox" name="pb.writeOff" value="Y" disabled="disabled" style="disableText"/>
			</c:if>
		</td>
		<%if(!user.getType().equalsIgnoreCase(User.DD)){ %>
		<td align="center">
			<a href="#" onclick="applyBillView('${pageContext.request.contextPath}',${rows2.index+1},'${bys.paymentMethodName}',${bys.receiptAmount},'${bys.seedId}');">
			<img border=0 src='${pageContext.request.contextPath}/icons/lookup.gif'  align='absmiddle'></a>
		</td>
		<%} %>
	</tr>
	</c:forEach>							
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td align="left" class="footer">&nbsp;</td>
	</tr>
</table>