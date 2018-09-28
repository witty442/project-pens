<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String selected = request.getParameter("selected");
if(selected==null)selected="";
String custId = request.getParameter("cust");

User user = (User) session.getAttribute("user");

List<Order> zero = new ArrayList<Order>();

List<Order> orders = new MOrder().lookUp(user.getId(),Integer.parseInt(custId) ,user.getOrderType().getKey(),"not in" ,selected);
for(Order r : orders){
	r.setCreditAmount(new MReceiptLine().calculateCreditAmount(r));
	if(r.getCreditAmount()==0)
		zero.add(r);
}
//remove zero credit
for(Order r : zero){
	orders.remove(r);
}

pageContext.setAttribute("orders",orders,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@page import="com.isecinc.pens.model.MReceiptLine"%>
<%@page import="java.util.ArrayList"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
	var prepaid=0;
	var gpp=0;
	function loadMe() {
		prepaid = Number(window.opener.document.getElementById('receiptAmount').value);
		gpp=prepaid;
		//alert(prepaid);
	}

	function changeReceiptText(row) {
		var objchk = document.getElementsByName('chkReceipts');
		var objtxt = document.getElementsByName('paidAmount');
		var objspn = document.getElementsByName('remainAmount');
		var objcrd = document.getElementsByName('creditAmount');
		if (objchk[row].checked) {
			//alert(prepaid)
			if(prepaid < 0){
				return false;
			}
			objtxt[row].readOnly = false;
			objtxt[row].className = '';
			objtxt[row].focus();
			//calculate from prepaid 
			if(prepaid < Number(objcrd[row].value)){
				objtxt[row].value=prepaid;
				prepaid=0;
			}else{
				objtxt[row].value=objcrd[row].value;
				prepaid-=Number(objcrd[row].value);
			}
		} else {
			prepaid+= Number(objtxt[row].value);
			objspn[row].value = '';
			objtxt[row].value = '';
			objtxt[row].readOnly = true;
			objtxt[row].className = 'disableText';
		}
		calculateRemain();
		return true;
		//alert(prepaid);
	}

	function calculateRemain() {
		var allPaid=0;
		var objcdr = document.getElementsByName('creditAmount');
		var objtxt = document.getElementsByName('paidAmount');
		var objspn = document.getElementsByName('remainAmount');
		for (i = 0; i < objtxt.length; i++) {
			if (objtxt[i].value != '') {
				allPaid+=Number(objtxt[i].value);
				if(Number(objtxt[i].value)>Number(objcdr[i].value)){
					alert("จำนวนเงินไม่ถูกต้อง จำนวนเงินรับชำระมากกว่า จำนวนเงินค้างชำระ");
					objtxt[i].focus();
					return;
				}
				objspn[i].value = Number(objcdr[i].value - objtxt[i].value).toFixed(2);
			}
		}
		prepaid = gpp-allPaid;
	}

	function addRow() {

		var retArry = new Array();
		var bill;

		var objchk = document.getElementsByName('chkReceipts');
		var objtxt = document.getElementsByName('paidAmount');
		var objspn = document.getElementsByName('remainAmount');

		var idx = 0;
		for (i = 0; i < objchk.length; i++) {
			if (objchk[i].checked) {
				if (objtxt[i].value == '') {
					alert('ใส่จำนวนเงินที่ชำระ');
					objtxt[i].focus();
					objtxt[i].select();
					return false;
				}
				if (Number(objspn[i].value) < 0 || Number(objtxt[i].value) == 0) {
					alert('ใส่จำนวนเงินไม่ถูกต้อง');
					objtxt[i].focus();
					objtxt[i].select();
					return false;
				}
				
				bill = new Object();
				bill.rows=0;
				bill.orderId = document.getElementsByName('orderId')[i].value;
				bill.invoiceNo = document.getElementsByName('arInvoiceNo')[i].value;
				bill.salesOrderNo = document.getElementsByName('salesOrderNo')[i].value;
				bill.netAmount = document.getElementsByName('netAmount')[i].value;
				bill.creditAmount = document.getElementsByName('creditAmount')[i].value;
				bill.paidAmount = document.getElementsByName('paidAmount')[i].value;
				bill.remainAmount = document.getElementsByName('remainAmount')[i].value;

				retArry[idx]=bill;
				idx++;
			}
		}
		//alert(retArry);
		window.opener.addBill('${pageContext.request.contextPath}',retArry);
		window.close();
	}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="ใบแจ้งหนี้"/>
</jsp:include>
<table align="center" border="0" cellpadding="0" cellspacing="1" width="100%" class="result">
	<tr>
		<th><bean:message key="Bill.No" bundle="sysele"/></th>
		<th><bean:message key="Order.No" bundle="sysele"/></th>
		<th><bean:message key="TotalAmount" bundle="sysele"/></th>
		<th><bean:message key="Order.Behindhand" bundle="sysele"/></th>
		<th><bean:message key="Order.Payment" bundle="sysele"/></th>
		<th><bean:message key="Amount" bundle="sysele"/></th>
		<th><bean:message key="Product.Balance" bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${orders}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE"/>
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td>
				${results.arInvoiceNo}
				<input type="hidden" name="arInvoiceNo" value="${results.arInvoiceNo}">
				<input type="hidden" name="orderId" value="${results.id}">
			</td>
			<td>
				${results.salesOrderNo}
				<input type="hidden" name="salesOrderNo" value="${results.salesOrderNo}">
			</td>
			<td>
				<fmt:formatNumber pattern="#,##0.00" value="${results.netAmount}"/>
				<input type="hidden" name="netAmount" value="${results.netAmount}">
			</td>
			<td>
				<fmt:formatNumber pattern="#,##0.00" value="${results.creditAmount}"/>
				<input type="hidden" name="creditAmount" value="${results.creditAmount}">
			</td>
			<td><input type="checkbox" name="chkReceipts" onclick="return changeReceiptText(${rows.index});"></td>
			<td><input type="text" name="paidAmount" onblur="calculateRemain();" onkeydown="return isNum0to9andpoint(this, event);" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
			<td><input type="text" name="remainAmount" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
		</tr>
	</c:forEach>
	<tr>
		<td align="left" colspan="7" class="footer">&nbsp;</td>
	</tr>
</table>	
<br>
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td align="center">
			<input type="button" value="บันทึก" class="newPosBtn" onclick="addRow();">

			<input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="window.close();">
		</td>
	</tr>
</table>
<input type="hidden" id="billId" name="billId">
<input type="hidden" id="addressRow" name="addressRow">
<br/>
</body>
</html>