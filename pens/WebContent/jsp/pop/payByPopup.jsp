<!--
Product Popup for Sales Order 
 -->
<%@page import="util.SessionGen"%>
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

String seed = String.valueOf(Calendar.getInstance().getTimeInMillis());
pageContext.setAttribute("seed",seed,PageContext.PAGE_SCOPE);

User user = (User) session.getAttribute("user");
%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.bean.User"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	change_payment(document.getElementsByName('paymentMethod')[0].value);
	$("#receiptAmount").focus();
}

function change_payment(val){
	if(val=='CH' || val=='CR'){
		document.getElementById('div_cheque1').style.display='';
		document.getElementById('div_cheque2').style.display='';
		if(val=='CH'){
			document.getElementById('div_cheque3').style.display='';
			document.getElementById('div_cheque4').style.display='none';
			new Epoch('epoch_popup','th',document.getElementById('chequeDate'));
		}
		if(val=='CR'){
			document.getElementById('div_cheque3').style.display='none';
			document.getElementById('div_cheque4').style.display='';
		}
		document.getElementById('div_cash').style.display='none';
	}else if(val=='CS'){
		document.getElementById('div_cash').style.display='';
		new Epoch('epoch_popup','th',document.getElementById('transCashDate'));
		
		document.getElementById('div_cheque1').style.display='none';
		document.getElementById('div_cheque2').style.display='none';
		document.getElementById('div_cheque3').style.display='none';
		document.getElementById('div_cheque4').style.display='none';

		//WitEdit :17/06/2555 Case :pay by cash add transfer date (save in field cheque_date(t_receipt_by))
		
	}
}

function save(path){
	var auto="N";
	var pay = "";
	var bank="";
	var chq ="";
	var chqd = "";
	var cct = "";

	var recamt = $("#receiptAmount").val();
	//if(recamt=='' || Number(recamt)==0) Comment Out Because 
	if(recamt=='')
	{
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$("#receiptAmount").focus();
		return false;
	}

	
	pay = $("#paymentMethod").val();
	if(pay=='PD'){
		bank="";
		chq ="";
		chqd = "";
		cct = "";
	}else if(pay=='CS'){
		bank="";
		chq ="";
		chqd = "";
		cct = "";
		
		chqd = $("#transCashDate").val();
		if(chqd=='')
		{
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$("#transCashDate").focus();
			return false;
		}
		
	}else if(pay=='CH'){
		bank=$("#bank").val();
		chq =$("#chequeNo").val();
		chqd = $("#chequeDate").val();

		cct = "";

		if(bank=='')
		{
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$("#bank").focus();
			return false;
		}

		if(chq=='')
		{
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$("#chequeNo").focus();
			return false;
		}

		if(chqd=='')
		{
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$("#chequeDate").focus();
			return false;
		}
		
	}else{
		bank=$("#bank").val();
		chq =$("#chequeNo").val();
		chqd = "";
		cct = $("#creditCardType").val();

		if(bank=='')
		{
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$("#bank").focus();
			return false;
		}

		if(chq=='')
		{
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$("#chequeNo").focus();
			return false;
		}

		if(cct=='')
		{
			alert('กรุณาระบุข้อมูลให้ครบถ้วน');
			$("#creditCardType").focus();
			return false;
		}
	}

	var payBy = new Object();
	payBy.method = pay;
	payBy.methodName =  document.getElementById('paymentMethod').options[document.getElementById('paymentMethod').selectedIndex].text;
	payBy.bank = bank;
	payBy.bankName = document.getElementById('bank').options[document.getElementById('bank').selectedIndex].text;
	payBy.chqNo = chq;
	payBy.chqDate = chqd;
	payBy.creditType = cct;
	if(cct!='')
		payBy.creditTypeName =  document.getElementById('creditCardType').options[document.getElementById('creditCardType').selectedIndex].text;
	else
		payBy.creditTypeName='';
		
	payBy.recAmount = recamt;
	payBy.seedId = $("#seedId").val();
	
	window.opener.addReceiptBy(path,payBy);
	window.close();
}

</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value="PayBy"/>
	<jsp:param name="code" value=""/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="45%"></td>
		<td></td>
	</tr>
	<tr id="div_cheque0">
		<td align="right"><bean:message key="Profile.PaymentMethod" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<select id="paymentMethod" name="paymentMethod" onchange="change_payment(this.value);">
				<%for(References r : payment){ %>
					<%if(user.getType().equalsIgnoreCase(User.TT)){ %>
					<%if(!r.getKey().equalsIgnoreCase("CR")){ %>
						<option value="<%=r.getKey() %>"><%=r.getName() %></option>
					<%} %>
					<%}else{ %>
						<option value="<%=r.getKey() %>"><%=r.getName() %></option>
					<%} %>
				<%} %>
			</select>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Receipt.Amount" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<input type="text" id="receiptAmount" name="receiptAmount" size="15" maxlength="20" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
		</td>
	</tr>
	<tr id="div_cash" style="display: none;">
		<td align="right">วันที่โอนเงิน <font color="red">*</font></td>
		<td align="left">
			<input type="text" id="transCashDate" name="transCashDate" size="15" readonly="readonly" maxlength="10"/>
		</td>
	</tr>
	
	<tr id="div_cheque1" style="display: none;">
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
	<tr id="div_cheque2" style="display: none;">
		<td align="right"><bean:message key="Check.No" bundle="sysele"/>/<bean:message key="CreditCardNo" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="chequeNo" name="chequeNo" size="25" maxlength="20"/>
		</td>
	</tr>
	<tr id="div_cheque3" style="display: none;">
		<td align="right"><bean:message key="Check.Date" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="chequeDate" name="chequeDate" size="15" readonly="readonly" maxlength="10"/>
		</td>
	</tr>
	<tr id="div_cheque4" style="display: none;">
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
	<tr>
		<td align="center" colspan="2">
			<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn" onclick="save('${pageContext.request.contextPath}');">

			<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn" onclick="window.close();">
		</td>
	</tr>
</table>
<input type="hidden" id="seedId" name="seedId" value="${seed}">
<br/>
</body>
</html>