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
String recAmt = request.getParameter("rec");
String type = request.getParameter("type");
type= new String(type.getBytes("ISO8859_1"), "TIS-620");
String seed = request.getParameter("seed");
String row = request.getParameter("row");
String custId = request.getParameter("cust");
String prepaid = request.getParameter("prepaid");

User user = (User) session.getAttribute("user");

List<Order> zero = new ArrayList<Order>();

List<Order> orders = new ArrayList<Order>();

if(prepaid.equalsIgnoreCase("Y")){
	orders = new MOrder().lookUpPrepaid(user.getId(),Integer.parseInt(custId),user.getOrderType().getKey(),"in",selected);
}else{
	orders = new MOrder().lookUp(user.getId(),Integer.parseInt(custId),user.getOrderType().getKey(),"in",selected);
}

//remove zero credit
for(Order r : zero){
	orders.remove(r);
}

pageContext.setAttribute("orders",orders,PageContext.PAGE_SCOPE);
pageContext.setAttribute("seed",seed,PageContext.PAGE_SCOPE);


//CreditNote...
String cns = request.getParameter("cns");
List<CreditNote> creditNotes = new MCreditNote().lookUpForPaid(cns,custId);
pageContext.setAttribute("creditnotes",creditNotes,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@page import="com.isecinc.pens.model.MReceiptLine"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.isecinc.pens.bean.CreditNote"%>
<%@page import="com.isecinc.pens.model.MCreditNote"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
	var prepaid=0;
	var gpp=0;
	function loadMe() {
		prepaid = Number(<%=recAmt%>);
		gpp=prepaid;
		//alert(prepaid);

		//get paid at opener
		var orders = window.opener.document.getElementsByName('bill.orderId');
		var netamts=window.opener.document.getElementsByName('bill.netAmt');
		var creditamts=window.opener.document.getElementsByName('bill.creditAmt');
		var paidamts = window.opener.document.getElementsByName('bill.paidAmt');

		
		var allBills = window.opener.document.getElementsByName('pb.allBillId')[<%=row%>-1];
		var allPaids = window.opener.document.getElementsByName('pb.allPaid')[<%=row%>-1];
		var b;

		//local variable
		var tbl =document.getElementById('tblBillToApply');
		var bills = document.getElementsByName('orderId');
		var nets = document.getElementsByName('netAmount');
		var crds = document.getElementsByName('creditAmount');
		var paids2 = document.getElementsByName('paidAmount');
		var remains = document.getElementsByName('remainAmount');
		var chk = document.getElementsByName('chkReceipts');

		for(i=0;i<orders.length;i++){
			//alert(orders[i].value);
			//alert(paids[i].value);
			for(j=0;j<bills.length;j++){
				if(orders[i].value==bills[j].value){
					//crds[j].value = Number(nets[j].value)-Number(paids[i].value);
					crds[j].value = Number(creditamts[i].value) - Number(paidamts[i].value);
					tbl.rows[j+1].cells[3].innerHTML = addCommas(Number(crds[j].value).toFixed(2));			
				}
			}
		}

		//alert(allBills);
		//alert(allPaids);
		if(Trim(allBills).value!=''){
			b = allBills.value.split(',');
			//alert(b);
			for(i=0;i<bills.length;i++){
				for(j=0;j<b.length;j++){
					if(bills[i].value==b[j]){
						chk[i].checked=true;
						//changeReceiptText(i);
						paids2[i].value =  allPaids.value.split('|')[j];
						crds[i].value = Number(crds[i].value)+Number(paids2[i].value);
						tbl.rows[j+1].cells[3].innerHTML = addCommas(Number(crds[j].value).toFixed(2));
					}					
				}
			}
		}

		//get cn at opener
		var cns = window.opener.document.getElementsByName('cn.cnId');
		var cnnetamts=window.opener.document.getElementsByName('cn.totalAmount');
		var cncreditamts=window.opener.document.getElementsByName('cn.creditAmt');
		var cnpaidamts = window.opener.document.getElementsByName('cn.paidAmt');

		//local variable
		var tbl =document.getElementById('tblCNtoPay');
		var cnbills = document.getElementsByName('cnId');
		var cnnets = document.getElementsByName('cnTotalAmount');
		var cncrds = document.getElementsByName('cnCreditAmount');
		var cnpaids2 = document.getElementsByName('paidCNAmount');
		var cnremains = document.getElementsByName('remainCNAmount');
		var cnchk = document.getElementsByName('chkCNs');

		for(i=0;i<cns.length;i++){
			//alert(orders[i].value);
			//alert(paids[i].value);
			for(j=0;j<cnbills.length;j++){
				if(cns[i].value==cnbills[j].value){
					//crds[j].value = Number(nets[j].value)-Number(paids[i].value);
					cncrds[j].value = Number(cncreditamts[i].value) - Number(cnpaidamts[i].value);
					tbl.rows[j+1].cells[3].innerHTML = addCommas(Number(cncrds[j].value).toFixed(2));			
				}
			}
		}

		allBills = window.opener.document.getElementsByName('pb.allCNId')[<%=row%>-1];
		allPaids = window.opener.document.getElementsByName('pb.allCNPaid')[<%=row%>-1];
		
		if(Trim(allBills).value!=''){
			b = allBills.value.split(',');
			//alert(b);
			for(i=0;i<cns.length;i++){
				for(j=0;j<b.length;j++){
					if(cnbills[i].value==b[j]){
						cnchk[i].checked=true;
						//changeReceiptText(i);
						cnpaids2[i].value = allPaids.value.split('|')[j];
						cncrds[i].value = Number(cncrds[i].value)+Number(cnpaids2[i].value);
						tbl.rows[j+1].cells[3].innerHTML = addCommas(Number(cncrds[j].value).toFixed(2));
					}					
				}
			}
		}
		
		calculateRemain();
	}

	function calculateRemain() {
		var allPaid=0;
		var objcdr = document.getElementsByName('creditAmount');
		var objtxt = document.getElementsByName('paidAmount');
		var objspn = document.getElementsByName('remainAmount');
		for (i = 0; i < objtxt.length; i++) {
			if (objtxt[i].value != '') {
				allPaid+=Number(objtxt[i].value);
				objspn[i].value = Number(objcdr[i].value - objtxt[i].value).toFixed(2);

				objtxt[i].value = addCommas(Number(objtxt[i].value).toFixed(2));
				objspn[i].value = addCommas(Number(objspn[i].value).toFixed(2));
			}
		}
		prepaid = gpp-allPaid;

		var allCNPaid=0;
		var objcdrCN = document.getElementsByName('cnTotalAmount');
		var objtxtCN = document.getElementsByName('paidCNAmount');
		var objspnCN = document.getElementsByName('remainCNAmount');
		for (i = 0; i < objtxtCN.length; i++) {
			if (objtxtCN[i].value != '') {
				allCNPaid+=Number(objtxtCN[i].value);
				objspnCN[i].value = Number(objcdrCN[i].value - objtxtCN[i].value).toFixed(2);

				objtxtCN[i].value = addCommas(Number(objtxtCN[i].value).toFixed(2));
				objspnCN[i].value = addCommas(Number(objspnCN[i].value).toFixed(2));
			}
		}
	}

</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="ตัดชำระ"/>
</jsp:include>
<table align="center" border="0" cellpadding="0" cellspacing="1" width="100%">
	<tr>
		<td width="20px;"></td>
		<td>
			ประเภท = <font color="red"><b><%=type %></b></font>
			ยอดเงินที่ตัดชำระได้ = <font color="red"><b><%=new DecimalFormat("#,##0.00").format(new Double(recAmt)) %></b></font>
		</td>
	</tr>
</table>
&nbsp;&nbsp;ใบแจ้งหนี้
<table id="tblBillToApply" align="center" border="0" cellpadding="0" cellspacing="1" width="100%" class="result">
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
				<input type="hidden" name="salesOrderNo" value="${results.salesOrderNo}">
				<input type="hidden" name="netAmount" value="${results.netAmount}">
				<input type="hidden" name="creditAmount" value="${results.creditAmount}">
			</td>
			<td>
				${results.salesOrderNo}
			</td>
			<td>
				<fmt:formatNumber pattern="#,##0.00" value="${results.netAmount}"/>
			</td>
			<td>
				<fmt:formatNumber pattern="#,##0.00" value="${results.creditAmount}"/>
			</td>
			<td><input type="checkbox" name="chkReceipts" disabled="disabled"></td>
			<td><input type="text" name="paidAmount" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
			<td><input type="text" name="remainAmount" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
		</tr>
	</c:forEach>
	<tr>
		<td align="left" colspan="7" class="footer">&nbsp;</td>
	</tr>
</table>	
&nbsp;&nbsp;ใบลดหนี้
<table id="tblCNtoPay" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th>เลขที่ใบลดหนี้</th>
		<th><bean:message key="Bill.No"  bundle="sysele"/></th>
		<th class="costprice"><bean:message key="TotalAmount"  bundle="sysele"/></th>
		<th><bean:message key="Order.Behindhand" bundle="sysele"/></th>
		<th><bean:message key="Order.Payment" bundle="sysele"/></th>
		<th><bean:message key="Amount" bundle="sysele"/></th>
		<th><bean:message key="Product.Balance" bundle="sysele"/></th>
	</tr>
	<c:forEach var="resultsCN" items="${creditnotes}" varStatus="rowsCN">
		<c:choose>
			<c:when test="${rowsCN.index %2 == 0}">
				<c:set var="tabclass" value="lineO"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE"/>
			</c:otherwise>
		</c:choose>
		<tr class="${tabclass}">
			<td align="center">
				${resultsCN.creditNoteNo}
				<input type="hidden" name='cnId' value='${resultsCN.id}' >
				<input type='hidden' name='cnNo' value='${resultsCN.creditNoteNo}' >
				<input type='hidden' name='cnInvoiceNo' value='${resultsCN.arInvoiceNo}'>
				<input type='hidden' name='cnTotalAmount' value='${resultsCN.totalAmount}'>
				<input type="hidden" name="cnCreditAmount" value="${results.creditAmount}">
			</td>
			<td align="center">${resultsCN.arInvoiceNo}</td>
			<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${resultsCN.totalAmount}"/></td>
			<td align="right"><fmt:formatNumber pattern="#,##0.00" value="${resultsCN.creditAmount}"/></td>
			<td><input type="checkbox" name="chkCNs" disabled="disabled"></td>
			<td><input type="text" name="paidCNAmount" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
			<td><input type="text" name="remainCNAmount" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
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
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<input type="hidden" id="seedId" name="seedId" value="${seed}">
<input type="hidden" id="billId" name="billId">
<input type="hidden" id="addressRow" name="addressRow">
<br/>
</body>
</html>