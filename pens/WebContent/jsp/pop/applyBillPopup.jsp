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
String recAmt = request.getParameter("rec");
String type = request.getParameter("type");
type= new String(type.getBytes("ISO8859_1"), "TIS-620");
String seed = request.getParameter("seed");
String row = request.getParameter("row");
String custId = request.getParameter("cust");

String writeOff = request.getParameter("writeOff");

User user = (User) session.getAttribute("user");

List<Order> zero = new ArrayList<Order>();

List<Order> orders = new MOrder().lookUp(user.getId(),Integer.parseInt(custId),user.getOrderType().getKey(),"in",selected);
for(Order r : orders){
	r.setCreditAmount(new MReceiptLine().calculateCreditAmount(r));
	if(r.getCreditAmount()==0)
		zero.add(r);
}
//remove zero credit
for(Order r : zero){
	//orders.remove(r);
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
<%@page import="com.isecinc.pens.model.MCreditNote"%>
<%@page import="com.isecinc.pens.bean.CreditNote"%><html>
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
	var writeOff = <%=writeOff%>;
	
	var totalCNAmount = Number(0);
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
					crds[j].value =(Number(crds[j].value).toFixed(2));
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
						paids2[i].value = allPaids.value.split('|')[j];
						crds[i].value = Number(crds[i].value) + Number(paids2[i].value);
						crds[j].value =(Number(crds[j].value).toFixed(2));
						paids2[i].readOnly=false;
						paids2[i].className='';
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
					cncrds[j].value = Number(cncrds[j].value).toFixed(2);
					tbl.rows[j+1].cells[3].innerHTML = addCommas(Number(cncrds[j].value).toFixed(2));			
				}
			}
			
			if(writeOff){
				cnchk[i].disabled = true;
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
						cnchk[i].disabled=true;
						//changeReceiptText(i);
						cnpaids2[i].value = allPaids.value.split('|')[j];
						cncrds[i].value = Number(cncrds[i].value) + Number(cnpaids2[i].value);
						cncrds[j].value = Number(cncrds[j].value).toFixed(2);
						cnpaids2[i].readOnly=false;
						cnpaids2[i].className='';
						tbl.rows[j+1].cells[3].innerHTML = addCommas(Number(cncrds[j].value).toFixed(2));
					}
					else if(b[j] == '' && !writeOff && Number(cncrds[i].value).toFixed(2)< 0){
						cnchk[i].checked=true;
						cnchk[i].disabled=true;
						//changeReceiptText(i);
						cnpaids2[i].value = Number(cncrds[i].value).toFixed(2);
						cncrds[i].value = 0
						//cncrds[j].value = Number(cncrds[j].value).toFixed(2);
						cnpaids2[i].readOnly=true;
						cnpaids2[i].className='';
						tbl.rows[i+1].cells[3].innerHTML = addCommas(Number(cncrds[i].value).toFixed(2));
					}
				}
			}
		}
		
		totalCNAmount = getTotalCNAmount();
		calculateRemain();
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
				objtxt[row].value = Number(prepaid).toFixed(2);
				prepaid=0;
			}else{
				objtxt[row].value=Number(objcrd[row].value).toFixed(2);
				prepaid-=Number(objcrd[row].value).toFixed(2);
			}
		} else {
			prepaid+= Number(objtxt[row].value).toFixed(2);
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

		var allCNPaid=0;
		var objcdrCN = document.getElementsByName('cnTotalAmount');
		var objtxtCN = document.getElementsByName('paidCNAmount');
		var objspnCN = document.getElementsByName('remainCNAmount');
		for (i = 0; i < objtxtCN.length; i++) {
			if (objtxtCN[i].value != '') {
				allCNPaid+=Number(objtxtCN[i].value);
				if(Number(objcdrCN[i].value)<0)
				{
					//neg value
					if(Number(objtxtCN[i].value)>0){
						alert("ห้ามใส่จำนวนเงินมากกว่า 0");
						objtxtCN[i].focus();
						return;
					}
					if(Number(objtxtCN[i].value)<Number(objcdrCN[i].value)){
						alert("จำนวนเงินไม่ถูกต้อง จำนวนเงินรับชำระมากกว่า จำนวนเงินค้างชำระ");
						objtxtCN[i].focus();
						return;
					}
				}else{
					//pos value
					if(Number(objtxtCN[i].value)<0){
						alert("ห้ามใส่จำนวนเงินน้อยกว่า 0");
						objtxtCN[i].focus();
						return;
					}
					if(Number(objtxtCN[i].value)>Number(objcdrCN[i].value)){
						alert("จำนวนเงินไม่ถูกต้อง จำนวนเงินรับชำระมากกว่า จำนวนเงินค้างชำระ");
						objtxtCN[i].focus();
						return;
					}
				}
				
				objspnCN[i].value = Number(objcdrCN[i].value - objtxtCN[i].value).toFixed(2);
			}
		}
		//prepaid-=allCNPaid;

		//alert(prepaid);
		prepaid+=totalCNAmount;
	}

	function addRow() {

		var allBillId='';
		var allPaid='';
		var totalPaid=Number(0);

		var limitApplyAmt=gpp;

		var objchk = document.getElementsByName('chkReceipts');
		var objtxt = document.getElementsByName('paidAmount');
		var objspn = document.getElementsByName('remainAmount');

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

				allBillId+=','+document.getElementsByName('orderId')[i].value;
				allPaid+='|'+document.getElementsByName('paidAmount')[i].value;
				totalPaid= eval(Number(totalPaid)+Number(document.getElementsByName('paidAmount')[i].value));
			}
		}

		if(totalPaid>gpp){
			//alert("จำนวนเงินไม่ถูกต้อง จำนวนเงินตัดชำระมากกว่า จำนวนเงินที่ตัดชำระได้");
			//return false;
		}
		//alert(allBillId);
		//alert(allPaid);
		
		var allCNId='';
		var allCNPaid='';
		var totalCNPaid=Number(0);

		var objchkCN = document.getElementsByName('chkCNs');
		var objtxtCN = document.getElementsByName('paidCNAmount');
		var objspnCN = document.getElementsByName('remainCNAmount');

		for (i = 0; i < objchkCN.length; i++) {
			if (objchkCN[i].checked) {
				if (objtxtCN[i].value == '' || objtxtCN[i].value == '-') {
					alert('ใส่จำนวนเงินที่ชำระ');
					objtxtCN[i].focus();
					objtxtCN[i].select();
					return false;
				}
				if (Number(objtxtCN[i].value) == 0) {
					alert('ใส่จำนวนเงินไม่ถูกต้อง');
					objtxtCN[i].focus();
					objtxtCN[i].select();
					return false;
				}

				allCNId+=','+document.getElementsByName('cnId')[i].value;
				allCNPaid+='|'+document.getElementsByName('paidCNAmount')[i].value;
				totalCNPaid= eval(Number(totalCNPaid) + Number(document.getElementsByName('paidCNAmount')[i].value));
			}
		}

		var totalApplyAmt = Number(totalPaid) + Number(totalCNPaid);
		totalApplyAmt = Number(totalApplyAmt).toFixed(2);
		
		if(totalApplyAmt > limitApplyAmt){
			alert("จำนวนเงินที่ตัดชำระมากกว่าจำนวนเงินที่รับชำระ");
			return false;
		}
		
		if(totalApplyAmt < limitApplyAmt){
			var diffAmt = limitApplyAmt-totalApplyAmt;
			if(diffAmt > 10){
				alert("จำนวนเงินที่ตัดชำระน้อยกว่าจำนวนเงินที่รับชำระเกิน 10บาท");
				return false;
			}
		}

		window.opener.fillApply('${seed}',allBillId,allPaid);
		window.opener.fillCN('${seed}',allCNId,allCNPaid);
		
		window.close();
		return true;
	}

	function changeCNText(row) {
		var objchk = document.getElementsByName('chkCNs');
		var objtxt = document.getElementsByName('paidCNAmount');
		var objspn = document.getElementsByName('remainCNAmount');
		var objcrd = document.getElementsByName('cnTotalAmount');
		
		if (objchk[row].checked) {
			//alert(prepaid)
			if(prepaid < 0){
				//return false;
			}
			objtxt[row].readOnly = false;
			objtxt[row].className = '';
			objtxt[row].focus();

			objtxt[row].value=objcrd[row].value;
			
			//calculate from prepaid 
			/*if(prepaid < Number(objcrd[row].value)){
				objtxt[row].value=prepaid;
				prepaid=0;
			}else{
				objtxt[row].value=objcrd[row].value;
				prepaid-=Number(objcrd[row].value);
			}*/
		} else {
			//prepaid+= Number(objtxt[row].value);
			objspn[row].value = '';
			objtxt[row].value = '';
			objtxt[row].readOnly = true;
			objtxt[row].className = 'disableText';
		}
		calculateRemain();
		return true;
		//alert(prepaid);
	}
	
	function getTotalCNAmount(){
		var objchk = document.getElementsByName('chkCNs');
		var objtxt = document.getElementsByName('paidCNAmount');
		
		var totalCNAmt = Number(0);
		
		for(var i=0; i < objchk.length;i++){
			if (objchk[i].checked) {
				totalCNAmt = Number(totalCNAmt) - Number(objtxt[i].value).toFixed(2);
			}
		}
		
		return totalCNAmt;
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
			<input type="hidden" id="limitApplyAmt" name="limitApplyAmt" value="<%=recAmt%>" >
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
			<td><input type="checkbox" name="chkReceipts" onclick="return changeReceiptText(${rows.index});"></td>
			<td><input type="text" name="paidAmount" onblur="calculateRemain();" onkeydown="return isNum0to9andpoint(this, event);" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
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
			<td><input type="checkbox" name="chkCNs" onclick="return changeCNText(${rowsCN.index});"></td>
			<td><input type="text" name="paidCNAmount" onblur="calculateRemain();" onkeydown="return isNum0to9andpoint(this, event);" readonly="readonly" size="10;" style="text-align: right;" class="disableText" ></td>
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
			<a href="#" onclick="return addRow();">
			<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn">
			</a>
			<a href="#" onclick="window.close();">
			<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
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