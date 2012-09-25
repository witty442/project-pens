<!--
Product Popup for Sales Order 
 -->
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
List<References> payment= InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
List<References> banks= InitialReferences.getReferenes().get(InitialReferences.BANK);

String amount= request.getParameter("amount");
String billId= request.getParameter("billId");
%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="java.text.DecimalFormat"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

var rowseed=2;

function loadMe(){
	change_payment(document.getElementsByName('paymentMethod')[0].value);
	changeAuto(document.getElementsByName('autoReceipt')[0].checked);
}

function changeAuto(val){
	if(val){
		document.getElementById('div_cheque0').style.display='';
		new Epoch('epoch_popup','th',document.getElementById('chqDate_1'));
	}else{
		document.getElementById('div_cheque0').style.display='none';
	}
}

function change_payment(){
	var method=document.getElementsByName('paymentMethod');
	//var recAmt=document.getElementsByName('receiptAmount');
	var bank=document.getElementsByName('bank');
	var chqNo=document.getElementsByName('chequeNo');
	var chqDate=document.getElementsByName('chequeDate');
	var cct=document.getElementsByName('creditCardType');
	
	
	var val;
	for(i=0;i<method.length;i++){
		val = method[i].value;
		if(val=='CH' || val=='CR'){
			bank[i].disabled=false;
			bank[i].className='';

			chqNo[i].readOnly=false;
			chqNo[i].className='';
			if(val=='CH'){
				chqDate[i].disabled=false;
				chqDate[i].readOnly=false;
				chqDate[i].className='';

				cct[i].disabled=true;
				cct[i].className='disableText';
			}
			if(val=='CR'){
				chqDate[i].readOnly=true;
				chqDate[i].disabled=true;
				chqDate[i].className='disableText';

				cct[i].disabled=false;
				cct[i].className='';
			}
		}else if(val=='CS'){
			bank[i].value='';
			bank[i].disabled=true;
			bank[i].className='disableText';

			chqNo[i].value='';
			chqNo[i].readOnly=true;
			chqNo[i].className='disableText';

			chqDate[i].value='';
			chqDate[i].readOnly=true;
			chqDate[i].disabled=true;
			chqDate[i].className='disableText';

			cct[i].value='';
			cct[i].disabled=true;
			cct[i].className='disableText';
		}
	}
	
}

function save(path){

	var auto='N';
	if(document.getElementById('autoReceipt').checked)
	{
		var totalPaid=0;
		//alert('auto');

		var method=document.getElementsByName('paymentMethod');
		var recAmt=document.getElementsByName('receiptAmount');
		var bank=document.getElementsByName('bank');
		var chqNo=document.getElementsByName('chequeNo');
		var chqDate=document.getElementsByName('chequeDate');
		var cct=document.getElementsByName('creditCardType');
		var paid=document.getElementsByName('paidAmount');

		for(i=0;i<method.length;i++){
			if(Trim(recAmt[i].value)==''||Number(recAmt[i].value)==0)
			{
				//alert('กรุณากรอกข้อมูลให้ครบถ้วน');
				//recAmt[i].focus();
				//return false;
			}

			if(method[i].value=='CH'){
				if(bank[i].value=='')
				{
					alert('กรุณาระบุข้อมูลให้ครบถ้วน');
					bank[i].focus();
					return false;
				}

				if(chqNo[i].value=='')
				{
					alert('กรุณาระบุข้อมูลให้ครบถ้วน');
					chqNo[i].focus();
					return false;
				}

				if(chqDate[i].value=='')
				{
					alert('กรุณาระบุข้อมูลให้ครบถ้วน');
					chqDate[i].focus();
					return false;
				}
			}
			if(method[i].value=='CR'){
				if(bank[i].value=='')
				{
					alert('กรุณาระบุข้อมูลให้ครบถ้วน');
					bank[i].focus();
					return false;
				}

				if(chqNo[i].value=='')
				{
					alert('กรุณาระบุข้อมูลให้ครบถ้วน');
					chqNo[i].focus();
					return false;
				}

				if(cct[i].value=='')
				{
					alert('กรุณาระบุข้อมูลให้ครบถ้วน');
					cct[i].focus();
					return false;
				}
			}

			if(Trim(paid[i].value)==''||Number(paid[i].value)==0)
			{
				alert('กรุณากรอกข้อมูลให้ครบถ้วน');
				paid[i].focus();
				return false;
			}

			recAmt[i].value = Number(paid[i].value);
			
			/*if(Number(recAmt[i].value) != Number(paid[i].value))
			{
				alert("จำนวนเงินไม่เท่ากัน");
				paid[i].focus();
				return false;
			}*/
			
			totalPaid+=Number(paid[i].value);
		}
		//alert(totalPaid);
		if(totalPaid != <%=amount%>)
		{
		//	alert("จำนวนเงินตัดชำระทั้งหมดไม่ถูกต้อง");
		//	recAmt[0].focus();
		//	return false;
		}
		auto = 'Y';
		window.opener.document.getElementsByName('autoReceiptFlag')[0].value=auto;
		createBysList();
	}

	window.opener.save(path);
	window.close();

	/*var auto="N";
	var pay = "";
	var bank="";
	var chq ="";
	var chqd = "";
	var cct = "";
	if(document.getElementsByName('autoReceipt')[0].checked){
		auto="Y";
		pay = $("#paymentMethod").val();
		if(pay=='CS'){
			bank="";
			chq ="";
			chqd = "";
			cct = "";
		}else if(pay=='CH'){
			bank=$("#bank").val();
			chq =$("#chequeNo").val();
			chqd = $("#chequeDate").val();
			cct = "";
		}else{
			bank=$("#bank").val();
			chq =$("#chequeNo").val();
			chqd = "";
			cct = $("#creditCardType").val();;
		}
	}

	
	
	window.opener.document.getElementsByName('autoReceiptFlag')[0].value=auto;
	window.opener.document.getElementsByName('autoReceipt.paymentMethod')[0].value=pay;
	window.opener.document.getElementsByName('autoReceipt.bank')[0].value=bank;
	window.opener.document.getElementsByName('autoReceipt.chequeNo')[0].value=chq;
	window.opener.document.getElementsByName('autoReceipt.chequeDate')[0].value=chqd;
	window.opener.document.getElementsByName('autoReceipt.creditCardType')[0].value=cct;


	window.opener.save(path);
	window.close();*/
	return true;
}


/** Create Bill Lazy List */
function createBysList(){
	var divlines = window.opener.document.getElementById('ByList');

	var method=document.getElementsByName('paymentMethod');
	var recAmt=document.getElementsByName('receiptAmount');
	var bank=document.getElementsByName('bank');
	var chqNo=document.getElementsByName('chequeNo');
	var chqDate=document.getElementsByName('chequeDate');
	var cct=document.getElementsByName('creditCardType');
	var paid=document.getElementsByName('paidAmount');
	var allbills='<%=billId%>';
	
	var inputLabel="";
	
	divlines.innerHTML="";
	for(i=0;i<method.length;i++){
		inputLabel="";
		inputLabel+="<input type='text' name='bys["+i+"].id' value='0'>";
		inputLabel+="<input type='text' name='bys["+i+"].paymentMethod' value='"+method[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].creditCardType' value='"+cct[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].bank' value='"+bank[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].chequeNo' value='"+chqNo[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].chequeDate' value='"+chqDate[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].receiptAmount' value='"+recAmt[i].value+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].seedId' value=''>";
		inputLabel+="<input type='text' name='bys["+i+"].allBillId' value='"+allbills+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].allPaid' value='"+paid[i].value+"'>";
		
		inputLabel+="<hr/>";
		divlines.innerHTML += inputLabel;
		
	}

	return true;

}

function addRow(path){
	var jQtable = $('#tblRecpBy');

	jQtable.each(function() {
	var $table = $(this);
	// Number of td's in the last table row
		var n = $('tr', this).length;
		// n++;
		// alert(n);
		var className = "lineO";
		if (n % 2 == 0)
			className = "lineE";
		

		var tds = '<tr class=' + className + '>';

		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/autoReceiptQuery.jsp",
				data : "row="+n+"&rowseed="+rowseed,
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					tds+=(returnString);
				}
			}).responseText;
		});
		tds += '</tr>';
		if ($('tbody', this).length > 0) {
			$('tbody', this).append(tds);
		} else {
			$(this).append(tds);
		}
	});

	new Epoch('epoch_popup','th',document.getElementById('chqDate_'+rowseed));
	
	rowseed++;
	
	change_payment();
}

function deleteRecpBy(path){
	var tbl = document.getElementById('tblRecpBy');
	var chk = document.getElementsByName("recpbyids");
	
	var drow;
	var bcheck=false;
	for(i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			drow = tbl.rows[i+2];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	chk = document.getElementsByName("recpbyids");
	for(i=0;i<chk.length;i++){
		tbl.rows[i+2].cells[0].innerHTML=(i+2);
	}
}


</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value="AutoCreateReceipt"/>
	<jsp:param name="code" value=""/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td align="right" width="45%">&nbsp;&nbsp;</td>
		<td align="left">
			<input type="checkbox" id="autoReceipt" name="autoReceipt" value="Y" onclick="changeAuto(this.checked);">
			<bean:message key="AutoCreateReceipt" bundle="sysprop"/>
		</td>
	</tr>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">	
	<tr>
		<td colspan="2" id="div_cheque0">
			<div align="left">
				&nbsp;&nbsp;<input type="button" value="เพิ่มประเภทการชำระเงิน" onclick="addRow('${pageContext.request.contextPath}');"/>
				&nbsp;&nbsp;<bean:message key="Receipt.AmountToPaid" bundle="sysele"/> = <font color="red"><b><%=new DecimalFormat("#,##0.00").format(new Double(amount)) %></b></font>
			</div>
			
			<div style="width: 100%;">
			<table id="tblRecpBy" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
				<tr>
					<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
					<th class="checkBox">
						<input type="checkbox" name="chkByAll" onclick="checkSelect(this,document.getElementsByName('recpbyids'));" />
					</th>
					<th><bean:message key="Profile.PaymentMethod" bundle="sysele"/></th>
					<th style="display: none;"><bean:message key="Receipt.Amount" bundle="sysele"/></th>
					<th><bean:message key="Bank" bundle="sysele"/> </th>
					<th><bean:message key="Check.No" bundle="sysele"/>/<bean:message key="CreditCardNo" bundle="sysele"/></th>
					<th><bean:message key="Check.Date" bundle="sysele"/></th>
					<th><bean:message key="CreditCardType" bundle="sysele"/></th>
					<th><bean:message key="Receipt.Paid" bundle="sysele"/></th>
				</tr>
				<tr class="lineO">
					<td align="center">${rows2.index+1}</td>
					<td align="center">&nbsp;</td>
					<td align="center">
						<select name="paymentMethod" onchange="change_payment();">
							<%for(References r : payment){ %>
							<option value="<%=r.getKey() %>"><%=r.getName() %></option>
							<%} %>
						</select>
					</td>
					<td align="center" style="display: none;">
						<input type="text" name="receiptAmount" value="<%=amount %>" readonly="readonly" class="disableText" size="10" maxlength="20" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
					</td>
					<td align="left">
						<select name="bank">
							<option value=""></option>
							<%for(References r : banks){ %>
							<option value="<%=r.getKey() %>"><%=r.getName() %></option>
							<%} %>
						</select>
					</td>
					<td align="center"><input type="text" name="chequeNo" size="20" maxlength="20"/></td>
					<td align="center"><input type="text" name="chequeDate" id="chqDate_1" size="15" readonly="readonly" maxlength="10"/></td>
					<td align="center">
						<select name="creditCardType">
							<option value=""></option>
							<option value="VISA">VISA</option>
							<option value="MASTERCARD">Master Card</option>
						</select>
					</td>
					<td align="center">
						<input type="text" name="paidAmount" value="<%=amount %>" size="10" maxlength="20" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
					</td>
				</tr>
			</table>
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
				<tr>
					<td align="left" class="footer">&nbsp;
						<a href="javascript:deleteRecpBy('${pageContext.request.contextPath}');"> 
						<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif"> ลบรายการ</a>
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="2">
			<a href="#" onclick="return save('${pageContext.request.contextPath}');">
			<input type="button" value="บันทึก" class="newPosBtn"/>
			<!-- <img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn"> --></a>
		</td>
	</tr>
	<!-- 
	<tr id="div_cheque0">
		<td align="right"><bean:message key="Profile.PaymentMethod" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<select id="paymentMethod" name="paymentMethod" onchange="change_payment(this.value);">
				<%for(References r : payment){ %>
				<option value="<%=r.getKey() %>"><%=r.getName() %></option>
				<%} %>
			</select>
		</td>
	</tr>
	<tr id="div_cheque1">
		<td align="right"><bean:message key="Bank" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<select name="bank" id="bank">
				<option value=""></option>
				<%for(References r : banks){ %>
				<option value="<%=r.getKey() %>"><%=r.getName() %></option>
				<%} %>
			</select>
		</td>
	</tr>
	<tr id="div_cheque2">
		<td align="right"><bean:message key="Check.No" bundle="sysele"/>/<bean:message key="CreditCardNo" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="chequeNo" name="chequeNo" size="25" maxlength="20"/>
		</td>
	</tr>
	<tr id="div_cheque3">
		<td align="right"><bean:message key="Check.Date" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="chequeDate" name="chequeDate" size="15" readonly="readonly" maxlength="10"/>
		</td>
	</tr>
	<tr id="div_cheque4">
		<td align="right"><bean:message key="CreditCardType" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<select name="creditCardType" id="creditCardType">
				<option value=""></option>
				<option value="VISA">VISA</option>
				<option value="MASTERCARD">Master Card</option>
			</select>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	 -->
	
</table>

<input type="hidden" id="lineId" name="lineId"/>
<input type="hidden" id="pricelistId" name="pricelistId"/>
<input type="hidden" id="productId" name="productId">
<input type="hidden" id="productRow" name="productRow">
<br/>
</body>
</html>