<%@page import="com.isecinc.pens.web.sales.OrderForm"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}
String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";
List<Address> address = new ArrayList<Address>();
address = new MAddress().lookUp(orderForm.getOrder().getCustomerId());
pageContext.setAttribute("addresses",address,PageContext.PAGE_SCOPE);

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);

List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

/* -- Auto Receipt --> */
List<References> banks= InitialReferences.getReferenes().get(InitialReferences.BANK);

List<References> internalBank= InitialReferences.getReferenes().get(InitialReferences.INTERNAL_BANK);
pageContext.setAttribute("internalBank",internalBank,PageContext.PAGE_SCOPE);

/** Allow Van Credit **/
boolean vanAllowCredit = false;
if(request.getAttribute("orderForm") != null){
  System.out.println("canReceiptCredit:"+orderForm.getCanReceiptCredit());
  if("Y".equalsIgnoreCase(orderForm.getCanReceiptCredit())){
	  vanAllowCredit = true;
  }
}

String canReceiptMoreCash = "N";
if(request.getAttribute("orderForm") != null){
  canReceiptMoreCash = orderForm.getCanReceiptMoreCash();
}
System.out.println("canReceiptMoreCash:"+canReceiptMoreCash);

String canAirpay = "N";
if(request.getAttribute("orderForm") != null){
	canAirpay = orderForm.getCanAirpay();
}
System.out.println("canAirpay:"+canAirpay);
/* -- Auto Receipt --> */
%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">

function loadMe(){
	document.getElementById("tempTotalAmount").value = addCommas(Number(document.getElementsByName("order.totalAmount")[0].value).toFixed(2));
	document.getElementById("tempVatAmount").value = addCommas(Number(document.getElementsByName("order.vatAmount")[0].value).toFixed(2));
	document.getElementById("tempNetAmount").value = addCommas(Number(document.getElementsByName("order.netAmount")[0].value).toFixed(2)); 

	//WIT EDIT :20110804
	<%if( ("true").equals(util.ConvertNullUtil.convertToString(request.getAttribute("popup_autoreceipt")))){ %>
	    //alert('��سҷ���¡�úѹ�֡�Ѻ�Թ');
	    //autoReceipt('${pageContext.request.contextPath}','<%=user.getType() %>');
	  	
	    $('#divOrderView').hide();
	    
	    loadAutoReceipt();
	<%}else{ %>
	    $('#divOrderView').show();
	<%} %>
}

var i;
var _path;
function setNextVisit(path, visitDate, fileType){
	i=0;
	_path = path;
	document.getElementsByName('fileType')[0].value = fileType;
	document.getElementsByName('nextVisitDate')[0].value = visitDate;
	
	window.open(path + "/jsp/saleOrderAction.do?do=printReport&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print1", "width=100,height=100,location=No,resizable=No");
	window.open(path + "/jsp/saleOrderAction.do?do=printReport&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print2", "width=100,height=100,location=No,resizable=No");
	
	return true;
}

function setNextVisitSummary(path, visitDate, fileType){
	i=0;
	_path = path;
	document.getElementsByName('fileType')[0].value = fileType;
	document.getElementsByName('nextVisitDate')[0].value = visitDate;
	
	window.open(path + "/jsp/saleOrderAction.do?do=printReportSummary&i="+(i++)+"&id="+document.getElementsByName('order.id')[0].value+"&visitDate="+visitDate+"&fileType="+fileType, "Print1", "width=100,height=100,location=No,resizable=No");
	
	return true;
}

function gotoSummaryReport(path, reportType){
 window.open(path + "/jsp/pop/printPopup.jsp?report_name=tax_invoice_summary&orderId="+document.getElementsByName('order.id')[0].value+"&reportType="+reportType, "Print2", "width=100,height=100,location=No,resizable=No");
}

function close(){
	window.close();
}

function printAgain(){
	i++;
	document.orderForm.action = _path + "/jsp/saleOrderAction.do?do=printReport&i="+i;
	document.orderForm.submit();
}


// *****************Auto Receipt ********************************************************* 

var rowseed=2;
var isPDPaid = <%=user.isPDPaid()%> 

function loadAutoReceipt(){
	
	//check payment term payment method
	//CS - CS ->> default check box
	pt = document.getElementsByName('order.paymentTerm')[0].value;
	pm = document.getElementsByName('order.paymentMethod')[0].value;
	
	//alert("pt["+pt+"]pm["+pm+"]");
	
	if(pt=='IM'&&pm=='CS'){
		//default radio CS
		document.getElementsByName('autoReceipt.paymentType')[0].checked=true;
		changePayType('CS');
	}else if(pt=='IM'&&pm=='CH'){
		//default radio CH
		document.getElementsByName('autoReceipt.paymentType')[1].checked=true;
		changePayType('CH');
	}else if(pm=='AP'){
		//default radio CH
		document.getElementsByName('autoReceipt.paymentType')[4].checked=true;
		changePayType('AP');
	}else{
		//Wit Edit 26/07/2556 display Credit Pay
		//document.getElementsByName('autoReceipt.paymentType')[3].checked=true;
		//changePayType('CR');
		
		document.getElementsByName('autoReceipt.paymentType')[0].checked=true;
		changePayType('CS');
	}
}

function changePayType(type){
	if(type=='CS'){
		//new line with CS one line
		showPayBy(true,type);
	}else if(type=='CH'){
		//new line with CH one line
		showPayBy(true,type);	
	}else if(type=='AP'){
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
		new Epoch('epoch_popup','th',document.getElementById('bys.chqDate_'+rowseed));
	}else if(type=='AP'){
		//new line with CH one line
		$('#divAddBtn').hide();
		$('#paymentMethod').val('AP');
		change_payment();
		removeRow();
		new Epoch('epoch_popup','th',document.getElementById('bys.chqDate_'+rowseed));
	}else if(type=='MIX'){
		//new line & ready for multiline
		$('#divAddBtn').show();
		$('#paymentMethod').val('CS');
		change_payment();
		removeRow();
	}
}

function change_payment(){
	var method=document.getElementsByName('bys.paymentMethod');
	//var recAmt=document.getElementsByName('receiptAmount');
	var bank=document.getElementsByName('bys.bank');
	var chqNo=document.getElementsByName('bys.chequeNo');
	var chqDate=document.getElementsByName('bys.chequeDate');
	var cct=document.getElementsByName('bys.creditCardType');
	var wof=document.getElementsByName('bys.writeOff');
	
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
                
				//alert(chqDate[i]);
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
			
		}else if(val=='AP'){
			bank[i].value='';
			bank[i].disabled=true;
			bank[i].className='disableText';

			chqNo[i].value='';
			chqNo[i].readOnly=false;
			chqNo[i].className='';

			chqDate[i].value='';
			chqDate[i].readOnly=true;
			chqDate[i].disabled=true;
			chqDate[i].className='disableText';

			cct[i].value='';
			cct[i].disabled=true;
			cct[i].className='disableText';

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

function saveAutoReceiptVan(path){
	var auto='N';
	var amount = document.getElementsByName('order.netAmount')[0].value;
	var billId = document.getElementsByName('order.id')[0].value;
	
	var orderDateObj = thaiDateToChristDate(document.getElementsByName('orderDate')[0].value);

	//enable 
	$("#autoReceipt.internalBank").removeAttr("disabled"); 
	if(!document.getElementsByName('autoReceipt.paymentType')[3].checked) {
		//order.iscash = 'Y'
		
		if($('#autoReceipt.internalBank').val()==''){
			alert('�������Žҡ�Թ��Һѭ��');
			$('#autoReceipt.internalBank').focus();
			return false;
		}

		var totalPaid=0;
		//alert('auto');

		var method=document.getElementsByName('bys.paymentMethod');
		var recAmt=document.getElementsByName('bys.receiptAmount');
		var bank=document.getElementsByName('bys.bank');
		var chqNo=document.getElementsByName('bys.chequeNo');
		var chqDate=document.getElementsByName('bys.chequeDate');
		var cct=document.getElementsByName('bys.creditCardType');
		var paid=document.getElementsByName('bys.paidAmount');

		for(i=0;i<method.length;i++){
			if(Trim(recAmt[i].value)==''||Number(recAmt[i].value)==0){
				//alert('��سҡ�͡���������ú��ǹ');
				//recAmt[i].focus();
				//return false;
			}
			if(method[i].value=='AP'){

				if(chqNo[i].value==''){
					alert('��س��кآ����� �Ţ������������� ���ú��ǹ');
					chqNo[i].focus();
					return false;
				}
			}
			if(method[i].value=='CH'){
				if(bank[i].value==''){
					alert('��س��кآ��������ú��ǹ');
					bank[i].focus();
					return false;
				}

				if(chqNo[i].value==''){
					alert('��س��кآ��������ú��ǹ');
					chqNo[i].focus();
					return false;
				}

				if(chqDate[i].value==''){
					alert('��س��кآ��������ú��ǹ');
					chqDate[i].focus();
					return false;
				}else{
					var chqDateObj = thaiDateToChristDate(chqDate[i].value);
					//alert("chDate["+chqDateObj+"]orderDate["+orderDateObj+"]");
					if(chqDateObj <= orderDateObj){
						if( confirm("�ѹ���˹���礵�ͧ�ҡ�����ѹ����Ѻ�Թ �׹�ѹ���з���¡�õ���")){
						}else{
						   chqDate[i].focus();
						   return false;
						}
					}
				}
			}
			if(method[i].value=='CR'){
				if(bank[i].value==''){
					alert('��س��кآ��������ú��ǹ');
					bank[i].focus();
					return false;
				}

				if(chqNo[i].value==''){
					alert('��س��кآ��������ú��ǹ');
					chqNo[i].focus();
					return false;
				}

				if(cct[i].value==''){
					alert('��س��кآ��������ú��ǹ');
					cct[i].focus();
					return false;
				}
			}

			if(Trim(paid[i].value)==''||Number(paid[i].value)==0){
				alert('��سҡ�͡���������ú��ǹ');
				paid[i].focus();
				return false;
			}

			recAmt[i].value = Number(paid[i].value);
			totalPaid+=Number(paid[i].value);
		}
		//alert(totalPaid);
		
		if(totalPaid != amount ){
			alert("�ӹǹ�Թ�Ѵ���з��������١��ͧ");
			recAmt[0].focus();
			return false;
		}
		auto = 'Y';
		createBysList();
		document.getElementsByName('order.isCash')[0].value=auto;
		document.getElementsByName('autoReceiptFlag')[0].value=auto;
		
		//window.opener.save(path);
	}else if(document.getElementsByName('autoReceipt.paymentType')[3].checked && isPDPaid) // Case Credit Sales for VAN Sales PD Paid
	{
		//alert("case2:");
		
		if($('#autoReceipt.internalBank').val()==''){
			alert('�������Žҡ�Թ��Һѭ��');
			$('#autoReceipt.internalBank').focus();
			return false;
		}

		var divlines = document.getElementById('ByList');

		var method='CS';
		var recAmt=amount;
		var bank='';
		var chqNo='';
		var chqDate='';
		var cct='';
		var paid=amount;
		var lf='N';
		var allbills=billId;
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
		document.getElementsByName('order.isCash')[0].value='N';
		document.getElementsByName('autoReceiptFlag')[0].value='Y';
	}

	 
	if(auto=='Y'){
		createAutoReceipt(path,"saveAutoReceipt");
	}else{
		createAutoReceipt(path,"");
	}
	
	return true;
}


/** Create Bill Lazy List */
function createBysList(){
	var amount = document.getElementsByName('order.netAmount')[0].value;
	var billId = document.getElementsByName('order.id')[0].value;
	
	var divlines = document.getElementById('ByList');

	var method=document.getElementsByName('bys.paymentMethod');
	var recAmt=document.getElementsByName('bys.receiptAmount');
	var bank=document.getElementsByName('bys.bank');
	var chqNo=document.getElementsByName('bys.chequeNo');
	var chqDate=document.getElementsByName('bys.chequeDate');
	var cct=document.getElementsByName('bys.creditCardType');
	var paid=document.getElementsByName('bys.paidAmount');
	var lf=document.getElementsByName('bys.writeOff');
	var allbills= billId;
	
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
				url: "${pageContext.request.contextPath}/jsp/ajax/autoReceiptVanQuery.jsp",
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

	new Epoch('epoch_popup','th',document.getElementById('bys.chqDate_'+rowseed));
	
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
	if(!bcheck){alert('���͡���������ҧ���� 1 ��¡��');return false;}
	chk = document.getElementsByName("recpbyids");
	for(i=0;i<chk.length;i++){
		tbl.rows[i+2].cells[0].innerHTML=(i+2);
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

// *********************************************************************************

function stampPrint(){
	var orderId = document.getElementsByName('order.id')[0].value;
	
	var printDateTimePick = document.getElementsByName("order.printDateTimeRcp")[0];
	var printCountPick = document.getElementsByName("order.printCountRcp")[0];
	
	printCountPick.value = parseInt(printCountPick.value)+1;
	var d = new Date(); 
	var dd = d.getDate();
        dd = dd.toString().length==1?"0"+dd:dd;
	var MM = (d.getMonth()+1);
	    MM = MM.toString().length==1?"0"+MM:MM;
	var year = (d.getFullYear()+543).toString();
	var hours = (d.getHours()).toString();
	    hours = hours.toString().length==1?"0"+hours:hours;
	var minite = (d.getMinutes()).toString();
	    minite = minite.toString().length==1?"0"+minite:minite;
	    
	    //ddMMyyyyHHmm
	var currentDateStr = dd+""+MM+""+year+""+hours+""+minite;
	//alert(currentDateStr);
	printDateTimePick.value = currentDateStr;
	
	//update db
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoStampPrint.jsp",
			data : "orderId="+orderId+"&dateStr="+printDateTimePick.value +"&count="+printCountPick.value,
			async: false,
			success: function(getData){
			}
		}).responseText;
	});
	
}

</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="SalesOrder"/>
				<jsp:param name="code" value="${orderForm.order.orderNo}"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/saleOrderAction">
						<jsp:include page="../error.jsp"/>
						
						
   <!-- ****************** Case Van Create Auto Receipt **********************************************************************************************-->
						    <%if( ("true").equals(util.ConvertNullUtil.convertToString(request.getAttribute("popup_autoreceipt")))){ %>
						     <table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
								<tr>
									<td colspan="4">
									
									<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
									     <tr>
											<td colspan="2">
												&nbsp;&nbsp;�ѹ����Ѻ�Թ = 
												<font color="red"><b>${orderForm.order.orderDate}</b></font>
											</td>
										</tr>
										<tr>
											<td colspan="2">
												&nbsp;&nbsp;<bean:message key="Receipt.AmountToPaid" bundle="sysele"/> = 
												<font color="red"><b> <fmt:formatNumber pattern="#,##0.00" value="${orderForm.order.netAmount}"/></b></font>
											</td>
										</tr>
										<tr>
											<td align="right" width="45%">&nbsp;&nbsp;</td>
											<td align="left">
											    <html:radio property="autoReceipt.paymentType" value="CS" onclick="changePayType(this.value);"/> �Ѻ�Թʴ�ѹ��<br>
	 										    <html:radio property="autoReceipt.paymentType" value="CH" onclick="changePayType(this.value);"/> �Ѻ��<br>
											    <html:radio property="autoReceipt.paymentType" value="MIX" onclick="changePayType(this.value);"/> ����Ẻ���<br>
											    <!-- Wit edit 26/07/2556  -->
											    <%if(vanAllowCredit){%>
											      <html:radio property="autoReceipt.paymentType" value="CR" onclick="changePayType(this.value);" /> �Թ����<br> 	
											    <%}else{ %>
											      <html:radio property="autoReceipt.paymentType" value="CR" onclick="changePayType(this.value);" disabled="true"/> <font color="#FFFFFF">�Թ���� </font><br> 							
											    <%} %>		
											    <%if("Y".equalsIgnoreCase(canAirpay)){%>
											      <html:radio property="autoReceipt.paymentType" value="AP" onclick="changePayType(this.value);"/> ���м�ҹ������� (Air Pay)<br>				
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
											   <html:select property="autoReceipt.internalBank" styleId="autoReceipt.internalBank" styleClass="disableText">
										           <html:options collection="internalBank" property="key" labelProperty="name"/>
									            </html:select>
											</td>
										</tr>
										<tr><td>&nbsp;</td></tr>
										<tr>
											<td align="left" id="divAddBtn" colspan="2">
												&nbsp;&nbsp;<input type="button" value="������������ê����Թ" onclick="addRow('${pageContext.request.contextPath}');"/>
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
														<th>�Ţ�����/�����Ţ�ѵ��ôԵ/�Ţ�������������</th>
														<th><bean:message key="Check.Date" bundle="sysele"/></th>
														<th><bean:message key="CreditCardType" bundle="sysele"/></th>
														<th><bean:message key="Receipt.Paid" bundle="sysele"/></th>
														<th>�������</th>
													</tr>
													<tr class="lineO">
														<td align="center">${rows2.index+1}</td>
														<td align="center">&nbsp;</td>
														<td align="center">
															<select id="paymentMethod" name="bys.paymentMethod" onchange="change_payment();">
																<%for(References r : paymentMethod){ %>
																<option value="<%=r.getKey() %>"><%=r.getName() %></option>
																<%} %>
															</select>
														</td>
														<td align="center" style="display: none;">
															<input type="text" name="bys.receiptAmount" value="${orderForm.order.netAmount}" size="10" maxlength="20" readonly="readonly" class="disableText" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
														</td>
														<td align="left">
															<select name="bys.bank">
																<option value=""></option>
																<%for(References r : banks){ %>
																<option value="<%=r.getKey() %>"><%=r.getName() %></option>
																<%} %>
															</select>
														</td>
														<td align="center">
														<input type="text" name="bys.chequeNo" size="20" maxlength="20"/>
														</td>
														<td align="center"><input type="text" name="bys.chequeDate" id="bys.chqDate_1" size="15" readonly="readonly" maxlength="10"/></td>
														<td align="center">
															<select name="bys.creditCardType">
																<option value=""></option>
																<option value="VISA">VISA</option>
																<option value="MASTERCARD">Master Card</option>
															</select>
														</td>
														<td align="center">
															<input type="text" name="bys.paidAmount" value="${orderForm.order.netAmount}" size="10" maxlength="20" style="text-align: right;" onkeydown="return isNum0to9andpoint(this, event);"/>
														</td>
														<td align="center">
															<input type="checkbox" name="bys.writeOff" value="Y"/>
														</td>
													</tr>
												</table>
												<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="100%">
													<tr>
														<td align="left" class="footer">&nbsp;
															<a href="javascript:deleteRecpBy('${pageContext.request.contextPath}');"> 
															<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif"> ź��¡��</a>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
									<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
										<tr>
											<td align="center" colspan="2">								
												<input type="button" value="�ѹ�֡�Ѻ�Թ" class="newPosBtn" onclick="return saveAutoReceiptVan('${pageContext.request.contextPath}');"/>
											</td>
										</tr>
									</table>
									</td>
								</tr>
						</table>
						
						<% } %>
<!-- **************************************************************************************************************************************************** -->							
				<div id="divOrderView" style="">
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="30%"></td>
								<td width="25%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left"></td>
								<td align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderDate" maxlength="10" size="18" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right">
									<bean:message key="Customer" bundle="sysprop"/>&nbsp;&nbsp;
								</td>
								<td align="left" colspan="3">
									<html:text property="order.customerName" size="80" readonly="true" styleClass="disableText"/>
									<html:hidden property="order.customerId"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryAddress" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:select property="order.shipAddressId" style="width:80%" disabled="true" styleClass="disableText">
										<html:options collection="addresses" property="id" labelProperty="lineString"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryDocAddress" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:select property="order.billAddressId" style="width:80%" disabled="true" styleClass="disableText">
										<html:options collection="addresses" property="id" labelProperty="lineString"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Profile.CreditTerm" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="order.paymentTerm" disabled="true" styleClass="disableText">
										<html:options collection="paymentTerm" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Profile.TaxRate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="order.vatCode" disabled="true" onchange="calculatePrice();" styleClass="disableText">
										<html:options collection="vatcodes" property="key" labelProperty="name"/>
									</html:select>
									
									<html:hidden property="order.priceListId"/>
									<html:hidden property="order.vatCode"/>
									<html:hidden property="order.paymentTerm"/>
									<html:hidden property="order.paymentMethod"/>
								</td>
							</tr>
							
							<tr>
								<td colspan="4" align="center">
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<th class="order"><bean:message key="No" bundle="sysprop"/></th>
										<th><bean:message key="Product.Name" bundle="sysele"/></th>
										<th><bean:message key="Product.UOM" bundle="sysele"/></th>
										<th><bean:message key="Quantity" bundle="sysele"/></th>
										<th><bean:message key="Price.Unit" bundle="sysele"/></th>
										<th><bean:message key="Total" bundle="sysele"/></th>
										<th><bean:message key="Discount" bundle="sysele"/></th>
										<th><bean:message key="TotalExcludeDiscount" bundle="sysele"/></th>
										<th><bean:message key="Order.ShipmentDate" bundle="sysele"/></th>										
										<th><bean:message key="Order.RequiredDate" bundle="sysele"/></th>
										<th><bean:message key="Promotion" bundle="sysele"/></th>

									</tr>
									<c:forEach var="lines1" items="${orderForm.lines}" varStatus="rows1">
									<c:choose>
										<c:when test="${rows1.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="${tabclass}">
										<td>${rows1.index + 1}</td>
										<td align="left">${lines1.product.code} ${lines1.product.name}</td>
										<td align="center">
											<c:choose>
												<c:when test="<%=orderForm.getOrder().getOrderType().equals(User.DD) %>">
													${lines1.uom.code}&nbsp;${lines1.uom1.code}
												</c:when>
												<c:otherwise>
													${lines1.fullUom}	
												</c:otherwise>
											</c:choose>
										</td>
										<td align="right">
											
											<c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<c:choose>
														<c:when test="${(lines1.product.uom.id==lines1.uom1.id && lines1.product.uom.id==lines1.uom2.id) || (lines1.product.uom.id!=lines1.uom1.id && lines1.product.uom.id!=lines1.uom2.id)}">
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty1 + lines1.qty2}"/>
														</c:when>
														<c:otherwise>
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
													<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>												
												</c:otherwise>
											</c:choose>
											
										</td>
										<td align="right">
											
											<c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<fmt:formatNumber pattern="#,##0.00000" value="0"/>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>/
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price2}"/>												
												</c:otherwise>
											</c:choose>											
										
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00" value="${lines1.discount}"/>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00" value="${lines1.lineAmount - lines1.discount}"/>
										</td>
										
										<td align="center">${lines1.shippingDate}</td>
										<td align="center">${lines1.requestDate}</td>
										
										<td align="center">
											<c:if test="${lines1.promotion=='Y'}">
												<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
											</c:if>
										</td>
										
										
									</tr>
									</c:forEach>
								</table>
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<td align="left" class="footer"></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TotalAmount" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempTotalAmount" name="tempTotalAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.totalAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="Tax" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempVatAmount" name="tempVatAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.vatAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="Net" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.netAmount"/>
								</td>
							</tr>
							<tr>
								<td colspan="4"><hr/></td>
							</tr>
							<tr>
								<td></td>
								<td><!--<html:checkbox property="order.paymentCashNow" value="Y" disabled="true" styleClass="disableText"/> �ѹ�֡�Ѻ�Թʴ�ѹ�� --></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.payment" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Order.Paid" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesOrderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="Bill.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.arInvoiceNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.SalesRepresent"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
									<html:hidden property="order.salesRepresent.id"/>
									<html:hidden property="order.salesRepresent.code"/>
								</td>
								<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td valign="top">
									<html:select property="order.docStatus" disabled="true" styleClass="disableText">
										<html:options collection="docstatus" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
						</table>
						<!-- BUTTON -->
						<br />
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right" width="10%"></td>
								<td align="left">
							  
									<%if(!isAdd.equals("N") || ((String)session.getAttribute("memberVIP")).equalsIgnoreCase("Y")){ %>
										
										<input type="button" value="���ҧ��¡������" class="newPosBtnLong" onclick="prepare('${pageContext.request.contextPath}','add');">
										
									<%} %>
									
								    <c:if test="${orderForm.mode=='edit'}">
										<c:if test="${orderForm.order.exported=='N'}">
											<c:if test="${orderForm.order.docStatus=='SV'}">
											<c:if test="${orderForm.order.payment=='N'}">
												
												<input type="button" value="�����¡��" class="newPosBtnLong" onclick="prepare('${pageContext.request.contextPath}','edit','${orderForm.order.id}');">
												
											</c:if>
											</c:if>
										</c:if>
								   </c:if>
									
									<%if(role.equals(User.VAN)){ %>
									<c:if test="${orderForm.order.docStatus=='SV'}">
									
									     <c:if test="${orderForm.order.isCash=='Y'}">
										    <input type="button" id ="reportBtn" value="����� ����Թ���/������Ѻ�Թ���Ǥ���" class="newPosBtn" onclick="stampPrint();gotoSummaryReport('${pageContext.request.contextPath}','copy');">
				                            <input type="button" id ="reportBtn" value="����� 㺡ӡѺ����(��ԧ)" class="newPosBtn" onclick="gotoSummaryReport('${pageContext.request.contextPath}','original');">
                                          </c:if>
                                          
                                          <c:if test="${orderForm.order.isCash=='N'}">
										    <input type="button" id ="reportBtn" value="�������觢ͧ/㺡ӡѺ����" class="newPosBtn" onclick="stampPrint();gotoSummaryReport('${pageContext.request.contextPath}','tax');">
				                            <input type="button" id ="reportBtn" value="�����������Ѻ�Թ" class="newPosBtn" onclick="gotoSummaryReport('${pageContext.request.contextPath}','bill');">
                                          </c:if>
                                          
										  <input type="button" id ="reportBtn" value="�����" class="newPosBtn" onclick="gotoReport('${pageContext.request.contextPath}','<%=role %>');">
										
									</c:if>
									<%} %>
									
								  <%if(role.equals(User.VAN)){ %>
								     <c:if test="${orderForm.mode=='edit'}">
										<c:if test="${orderForm.order.payment=='N'}">
											<c:if test="${orderForm.order.docStatus=='SV'}">
											
											    <input type="button" value="�ѹ�֡�Ѻ�Թ" class="newPosBtn" onclick="autoReceiptNew('${pageContext.request.contextPath}','<%=role %>','<%=canReceiptMoreCash%>');">
											</c:if>
										</c:if>
									   </c:if>
									<%} %>
								</td>
								<td align="right">
									
									<input type="button" value="�Դ˹�Ҩ�" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						
						<!--  -->
						<html:hidden property="order.payment" styleId="payment"/>
						<html:hidden property="deletedId"/>
						<html:hidden property="order.orderType"/>
						<html:hidden property="order.id"/>
						<html:hidden property="order.customerId"/>
						<html:hidden property="order.exported"/>
						<html:hidden property="order.isCash"/>
						
						<!--  Can Receipt Credit (VAN)-->
						<html:hidden property="canReceiptMoreCash"/>
						<html:hidden property="canReceiptCredit"/>
						<html:hidden property="canAirpay"/>
						
						<!-- AUTO RECEIPT -->
						<html:hidden property="autoReceiptFlag"/>
						<html:hidden property="autoReceipt.bank"/>
						<html:hidden property="autoReceipt.chequeNo"/>
						<html:hidden property="autoReceipt.chequeDate"/>
						<html:hidden property="autoReceipt.creditCardType"/>	    
						<html:hidden property="autoReceipt.internalBank"/>
						<html:hidden property="autoReceipt.paymentMethod"/>
						
						<input type="hidden" name="fileType" id="fileType"/>
						<input type="hidden" name="nextVisitDate">
						<input type="hidden" name="memberVIP" value="${memberVIP}"/>
						
						<div id="productList" style="display: none;"></div>
						<div id="ByList" style="display: none;"></div>
						
						<input type="hidden" name="orderDate" id="orderDate" value="${orderForm.order.orderDate}"/>
						
						<html:hidden property="order.printDateTimePick"/><br/>
						<html:hidden property="order.printCountPick"/><br/>
						<html:hidden property="order.printDateTimeRcp"/><br/>
						<html:hidden property="order.printCountRcp"/>
						
						<!-- ForTest -->
					   <%--  printDateTimePick:<html:text property="order.printDateTimePick"/><br/>
						printCountPick:<html:text property="order.printCountPick"/><br/>
						printDateTimeRcp:<html:text property="order.printDateTimeRcp"/><br/>
						printCountRcp:<html:text property="order.printCountRcp"/> --%>
						
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<jsp:include page="../trxhist.jsp">
							<jsp:param name="module" value="<%=TrxHistory.MOD_ORDER%>"/>
							<jsp:param name="id" value="${orderForm.order.id}"/>
						</jsp:include>
						
					</div>

						</html:form>
						<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>