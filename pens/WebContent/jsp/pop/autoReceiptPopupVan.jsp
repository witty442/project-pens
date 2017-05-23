<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.web.sales.OrderForm"%>
<%@page import="java.util.Date"%>
<%@page import="util.DateToolsUtil"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="java.text.DecimalFormat"%><html>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

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
/* if(request.getParameter("amount") == null){
	amount= Utils.isNull(request.getAttribute("amount"));
} */
String billId= request.getParameter("billId");
/* if(request.getParameter("billId") ==null){
	billId= Utils.isNull(request.getAttribute("billId"));
} */

String receiptDate = DateToolsUtil.convertToString(new Date());

List<References> internalBank= InitialReferences.getReferenes().get(InitialReferences.INTERNAL_BANK);
pageContext.setAttribute("internalBank",internalBank,PageContext.PAGE_SCOPE);
User user = (User)session.getAttribute("user");


/** Allow Van Credit **/
   boolean vanAllowMore = false;
  String canReceiptMoreCash = request.getParameter("canReceiptMoreCash");
  System.out.println("canReceiptMoreCash:"+canReceiptMoreCash);
  if("Y".equalsIgnoreCase(canReceiptMoreCash)){
	  vanAllowMore = true;
  }
 
%>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

var rowseed=2;
var isPDPaid = <%=user.isPDPaid()%> 

function loadMe(){
	//check payment term payment method
	//CS - CS ->> default check box
	pt = window.opener.document.getElementsByName('order.paymentTerm')[0].value;
	pm = window.opener.document.getElementsByName('order.paymentMethod')[0].value;
	//alert("pt["+pt+"]pm["+pm+"]");
	if(pt=='IM'&&pm=='CS'){
		//default radio CS
		document.getElementsByName('payType')[0].checked=true;
		changePayType('CS');
	}else if(pt=='IM'&&pm=='CH'){
		//default radio CH
		document.getElementsByName('payType')[1].checked=true;
		changePayType('CH');
	}else{
		document.getElementsByName('payType')[3].checked=true;
		changePayType('CR');
	}
	
	<%if(vanAllowMore==false){%>//Cash Only
	   document.getElementsByName('payType')[0].checked=true;
	   changePayType('CS');
	<%}%>
}

function change_payment(){
	var method=document.getElementsByName('paymentMethod');
	//var recAmt=document.getElementsByName('receiptAmount');
	var bank=document.getElementsByName('bank');
	var chqNo=document.getElementsByName('chequeNo');
	var chqDate=document.getElementsByName('chequeDate');
	var cct=document.getElementsByName('creditCardType');
	var wof=document.getElementsByName('writeOff');
	
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

				new Epoch('epoch_popup','th',chqDate[i]);
			}
			if(val=='CR'){
				chqDate[i].readOnly=true;
				chqDate[i].disabled=true;
				chqDate[i].className='disableText';

				cct[i].disabled=false;
				cct[i].className='';
			}
			wof[i].checked=false;
			wof[i].disabled=true;
			wof[i].className='disableText';
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

			wof[i].disabled=false;
			wof[i].className='';
		}
	}
	
}

function save(path){
	var auto='N';
	var receiptDate = new Date();
	var orderDateObj = thaiDateToChristDate(document.getElementsByName('orderDate')[0].value);
	
	if(!document.getElementsByName('payType')[3].checked)
	{
		//order.iscash = 'Y'
		if($('#internalBank').val()==''){
			alert('ใส่ข้อมูลฝากเงินเข้าบัญชี');
			$('#internalBank').focus();
			return false;
		}

		window.opener.document.getElementsByName('autoReceipt.internalBank')[0].value=$('#internalBank').val();
		
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
			if(Trim(recAmt[i].value)==''||Number(recAmt[i].value)==0){
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
				}else{
					var chqDateObj = thaiDateToChristDate(chqDate[i].value);
					//alert("chDate["+chqDateObj+"]orderDate["+orderDateObj+"]");
					if(chqDateObj <= orderDateObj){
						if( confirm("วันที่หน้าเช็คต้องมากกว่าวันที่รับเงิน ยืนยันที่จะทำรายการต่อไป")){
						}else{
						   chqDate[i].focus();
						   return false;
						}
					}
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
			alert("จำนวนเงินตัดชำระทั้งหมดไม่ถูกต้อง");
			recAmt[0].focus();
			return false;
		}
		auto = 'Y';
		createBysList();
		window.opener.document.getElementsByName('order.isCash')[0].value=auto;
		window.opener.document.getElementsByName('autoReceiptFlag')[0].value=auto;
		
		//window.opener.save(path);
	}
	else if(document.getElementsByName('payType')[3].checked && isPDPaid) // Case Credit Sales for VAN Sales PD Paid
	{
		if($('#internalBank').val()==''){
			alert('ใส่ข้อมูลฝากเงินเข้าบัญชี');
			$('#internalBank').focus();
			return false;
		}

		window.opener.document.getElementsByName('autoReceipt.internalBank')[0].value=$('#internalBank').val();

		var divlines = window.opener.document.getElementById('ByList');

		var method='CS';
		var recAmt=<%=amount%>;
		var bank='';
		var chqNo='';
		var chqDate='';
		var cct='';
		var paid=<%=amount%>;
		var lf='N';
		var allbills='<%=billId%>';
		var i = 0;
		
		var inputLabel="";
		
		divlines.innerHTML="";

		inputLabel="";
		inputLabel+="<input type='text' name='bys["+i+"].id' value='0'>";
		inputLabel+="<input type='text' name='bys["+i+"].paymentMethod' value='"+method+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].creditCardType' value='"+cct+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].bank' value='"+bank+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].chequeNo' value='"+chqNo+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].chequeDate' value='"+chqDate+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].receiptAmount' value='"+recAmt+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].seedId' value=''>";
		inputLabel+="<input type='text' name='bys["+i+"].allBillId' value='"+allbills+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].allPaid' value='"+paid+"'>";
		inputLabel+="<input type='text' name='bys["+i+"].writeOff' value='N'>";
		
		inputLabel+="<hr/>";
		divlines.innerHTML += inputLabel;

		auto = 'Y';
		window.opener.document.getElementsByName('order.isCash')[0].value='N';
		window.opener.document.getElementsByName('autoReceiptFlag')[0].value='Y';
		//window.opener.createAutoReceipt(path);
	}

	if(auto=='Y'){
		window.opener.createAutoReceipt(path,'saveAutoReceipt');
	}else{
		window.opener.createAutoReceipt(path,'');
	}
	window.close();
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
	var lf=document.getElementsByName('writeOff');
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
		if(lf[i].checked){
			inputLabel+="<input type='text' name='bys["+i+"].writeOff' value='Y'>";
		}else{
			inputLabel+="<input type='text' name='bys["+i+"].writeOff' value='N'>";
		}
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

function changePayType(type){
	if(type=='CS'){
		//new line with CS one line
		showPayBy(true,type);
	}else if(type=='CH'){
		//new line with CH one line
		showPayBy(true,type);	
	}else if(type=='MIX'){
		//new line & ready for multiline
		showPayBy(true,type);
	}else {
		//hide line...
		showPayBy(false,type);
	}
}

function showPayBy(bshow,type){
	if(bshow)
		$('#tblPayBy').show();
	else
		$('#tblPayBy').hide();
	//...
	if(type=='CS'){
		//new line with CS one line
		$('#divAddBtn').hide();
		$('#paymentMethod').val('CS');
		change_payment();
		removeRow();
		
	}else if(type=='CH'){
		//new line with CH one line
		$('#divAddBtn').hide();
		$('#paymentMethod').val('CH');
		change_payment();
		removeRow();
		new Epoch('epoch_popup','th',document.getElementById('chqDate_1'));
	}else if(type=='MIX'){
		//new line & ready for multiline
		$('#divAddBtn').show();
		$('#paymentMethod').val('CS');
		change_payment();
		removeRow();
	}
}

function removeRow(){
	var chk = document.getElementsByName("recpbyids");
	var haveRow=false;
	for(i=chk.length-1;i>=0;i--){
		chk[i].checked = true;
		haveRow = true;
	}
	if(haveRow)
		deleteRecpBy('');
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
     <input type="hidden" name="orderDate" id="orderDate" value="<%=receiptDate%>"/>
     <tr>
		<td colspan="2">
			&nbsp;&nbsp;วันทีรับเงิน = <font color="red"><b><%=receiptDate%></b></font>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			&nbsp;&nbsp;<bean:message key="Receipt.AmountToPaid" bundle="sysele"/> = <font color="red"><b><%=new DecimalFormat("#,##0.00").format(new Double(amount)) %></b></font>
		</td>
	</tr>
	<tr>
		<td align="right" width="45%">&nbsp;&nbsp;</td>
		<td align="left">
			<input type="radio" name="payType" value="CS" onclick="changePayType(this.value);"/>รับเงินสดทันที<br>
			
			
		 <!-- Wit edit 26/07/2556  -->
		    <%if(vanAllowMore){%>
		       <input type="radio" name="payType" value="CH" onclick="changePayType(this.value);"/>สดรับเช็ค<br>
			   <input type="radio" name="payType" value="MIX" onclick="changePayType(this.value);"/>ชำระแบบผสม<br>
		       <input type="radio" name="payType" value="CR" onclick="changePayType(this.value);"/>เงินเชื่อ<br>
		      
		    <%}else{ %>
		       <input type="radio" name="payType" value="CH" onclick="" disabled/>สดรับเช็ค<br>
			   <input type="radio" name="payType" value="MIX" onclick="" disabled/>ชำระแบบผสม<br>
		       <input type="radio" name="payType" value="CR" onclick="" disabled/> ...<br> 							
		    <%} %>			

		</td>
	</tr>
</table>
<table id="tblPayBy" align="center" border="0" cellpadding="0" cellspacing="0" width="100%">	
	<tr>
		<td align="right" width="45%">
			<bean:message key="InternalBank" bundle="sysele"/><font color="red">*</font>
		</td>
		<td align="left">
			<select id="internalBank" name="internalBank">
				<%for(References r : internalBank){ %>
				<option value="<%=r.getKey() %>"><%=r.getName() %></option>
				<%} %>
			</select>
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td align="left" id="divAddBtn" colspan="2">
			&nbsp;&nbsp;<input type="button" value="เพิ่มประเภทการชำระเงิน" onclick="addRow('${pageContext.request.contextPath}');"/>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="2">
			<table id="tblRecpBy" align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="100%">
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
					<th>เซลล์จ่าย</th>
				</tr>
				<tr class="lineO">
					<td align="center">${rows2.index+1}</td>
					<td align="center">&nbsp;</td>
					<td align="center">
						<select id="paymentMethod" name="paymentMethod" onchange="change_payment();">
							<%for(References r : payment){ %>
							<option value="<%=r.getKey() %>"><%=r.getName() %></option>
							<%} %>
						</select>
					</td>
					<td align="center" style="display: none;">
						<input type="text" name="receiptAmount" value="<%=amount %>" size="10" maxlength="20" readonly="readonly" class="disableText" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
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
					<td align="center">
						<input type="checkbox" name="writeOff" value="Y"/>
					</td>
				</tr>
			</table>
			<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="100%">
				<tr>
					<td align="left" class="footer">&nbsp;
						<a href="javascript:deleteRecpBy('${pageContext.request.contextPath}');"> 
						<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif"> ลบรายการ</a>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="center" colspan="2">
			<a href="#" onclick="return save('${pageContext.request.contextPath}');">
			<input type="button" value="บันทึก" class="newPosBtn"/>
			</a>
		</td>
	</tr>
</table>

<input type="hidden" id="lineId" name="lineId"/>
<input type="hidden" id="pricelistId" name="pricelistId"/>
<input type="hidden" id="productId" name="productId">
<input type="hidden" id="productRow" name="productRow">
<br/>
</body>
</html>