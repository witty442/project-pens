<%@page import="com.isecinc.pens.dao.SAEmpDAO"%>
<%@page import="com.isecinc.pens.bean.SADamageBean"%>
<%@page import="com.isecinc.pens.bean.SAEmpBean"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="saDamageForm" class="com.isecinc.pens.web.sa.SADamageForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
String mode = saDamageForm.getMode();
SADamageBean bean = saDamageForm.getBean();

//PayTypeList
List<PopupForm> payTypeList = new ArrayList();
PopupForm ref2 = new PopupForm("",""); 
payTypeList.add(ref2);
payTypeList.addAll(SAEmpDAO.getMasterListByRefCode(new PopupForm(),"","SApaytype"));
//session.setAttribute("payTypeList",billTypeList2);

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}

.day {
  width: 14%;
}
.holiday {
  width: 14%;
  background-color: #F78181;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	<%if("add".equals(mode)){%>
	     new Epoch('epoch_popup', 'th', document.getElementById('checkStockDate'));
	<%}else{%>
	     new Epoch('epoch_popup', 'th', document.getElementById('checkStockDate'));
	 <%}%>
	 
	   sumTotal();
	   //init payDate
	   var rows = $('#tblProduct tr').length-1;
	   if(rows ==0){
		   
		   <%if( !mode.equals("view")){ %>
		     addRow();
		     rows++;
	      <%}%> 
	   }
	   //init paydate on  table row
	   for(var i=0;i<rows;i++){
		   new Epoch('epoch_popup', 'th', document.getElementsByName('payDate')[i]);
	   }
		   
	  /** Disable empId **/ 
	   if(document.getElementsByName('bean.type')[0].checked){ //BME
			document.getElementById('empId').className= "disableText";
			document.getElementById('empId').readOnly= true;
			document.getElementById('getempId').style.display='none';
	   }else{
		   //WACOAL
		   document.getElementById('empId').className= "normalText";
		   document.getElementById('empId').readOnly= false;
		   document.getElementById('getempId').style.display='block';
	
	   }
}

function addRow(){
	// $('#myTable tr').length;
	var rows = $('#tblProduct tr').length-1;
	var className = 'lineO';
	if(rows%2 !=0){
		className = 'lineE';
	}

	var lineId = getTotalRow()+1;
	//alert("lineId["+lineId+"]");
	
	var rowData ="<tr class='"+className+"'>"+
	    "<td class='td_text_center' width='10%'>  <input type='hidden' name='status' id='status'/>  <input type='hidden' name='ids' id='ids'/>"+
	    "<input type='checkbox' tabindex ='-1' name='linechk' value='0'/></td>"+
	    "<td class='td_text_center' width='15%'> <input type='text' name='lineId' readonly id='lineId' size='3' value='"+lineId+"' class='disableText tabindex='' readonly> </td>"+
	    "<td class='td_text_center' width='15%'> "+
	    "<select name='payType' id='payType' tabindex=''>"+
		    <%if(payTypeList != null && payTypeList.size() >0){ 
		  	  String selected = "";
		      for(int i=0;i<payTypeList.size();i++){
		      	PopupForm p = payTypeList.get(i);
		    %>
			  "<option value='<%=p.getCode()%>'><%=p.getDesc()%></option>"+
			  <%}} %>
	    "</select>"+
	    "</td>"+
	    "<td class='td_text_center' width='15%'> <input type='text' readonly name='payDate' id='payDate' size='30' tabindex=''> </td>"+
	    "<td class='td_text_center' width='15%'> <input type='text' name='payAmt' id='payAmt' size='30' tabindex='' onblur='isNum2Digit(this);sumTotal();' class='enableNumber'></td>"+

	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    var payType = document.getElementsByName("payType");
    payType[lineId-1].focus();
    
    //init Calendar
    //alert(getTotalRowAll());
     new Epoch('epoch_popup', 'th', document.getElementsByName('payDate')[getTotalRowAll()-1]);
	 
}

 function removeRow(path){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("linechk");
	var drow;
	var bcheck=false;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			drow = tbl.rows[i+1];
			//$(drow).remove();
			bcheck=true;
			
			removeRowByIndex(path,drow,i);
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	//rearrang lineId
	var lineId = document.getElementsByName("lineId");
	var status = document.getElementsByName("status");
	var no = 0;
	for(var i=0;i<lineId.length;i++){
		//alert(status[i].value );
		if(status[i].value != 'AB'){
		   no++;
		   lineId[i].value =no;
		}
	}
} 

function getTotalRow(){
	var lineId = document.getElementsByName("lineId");
	var status = document.getElementsByName("status");
	var totalRow = 0;
	for(var i=0;i<lineId.length;i++){
		//alert(status[i].value );
		if(status[i].value != 'AB'){
			totalRow++;
		}
	}
	return totalRow;
}
function getTotalRowAll(){
	var lineId = document.getElementsByName("lineId");
	var status = document.getElementsByName("status");
	var totalRow = 0;
	for(var i=0;i<lineId.length;i++){
		//alert(status[i].value );
		totalRow++;
	}
	return totalRow;
}

function removeRowByIndex(path,drow,index){
    //alert(drow);
	//todo play with type	
	drow.style.display ='none';
	//set  staus = AB 
	//alert(index);
	document.getElementsByName("status")[index].value ='AB';
}

function sumTotal(){
	var amount = document.getElementsByName("payAmt");
	var totalQtyTemp = 0;
	for(var i=0;i<amount.length;i++){
		if(amount[i].value != ""){
		   var amountTemp = amount[i].value;
		   amountTemp = amountTemp.replace(/\,/g,''); //alert(r);
		   totalQtyTemp += parseFloat(amountTemp);	
		}
	}
	var t = addCommas(Number(toFixed(totalQtyTemp,2)).toFixed(2));
	$('#totalQty').val(t);
	 
}

function setStyletextField(){
	document.getElementById('empId').className= "disableText";
	document.getElementById('empId').readOnly= true;
	document.getElementById('startDate').className= "disableText";
}

function clearForm(path){
	var form = document.saDamageForm;
	form.action = path + "/jsp/saDamageAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.saDamageForm;
	form.action = path + "/jsp/saDamageAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.saDamageForm;
	if( $('#invRefWal').val()==""){
		alert("กรุณาระบุ  Invoice No / Ref Wacoal");
		$('#invRefWal').focus();
		return false;
	}
	if( $('#empId').val()==""){
		alert("กรุณาระบุ  Employee ID");
		$('#empId').focus();
		return false;
	}
	//valid table data
    var table = document.getElementById('tblProduct');
    var rows = table.getElementsByTagName("tr").length-1; 
	var rowsObj = table.getElementsByTagName("tr"); 
	//alert("rows:"+rows);
	var payType = document.getElementsByName("payType");
	var payDate = document.getElementsByName("payDate");
	var payAmt = document.getElementsByName("payAmt");
	var status = document.getElementsByName("status");
	var pass = true;

	for(var r=0;r<rows;r++){
		if(status[r].value != 'AB'){
			var lineClass ="lineE";
			if(r%2==0){
				lineClass = "lineO";
			}
			//alert(payType[r].value);
			if(payType[r].value =="" || payDate[r].value=="" || payAmt[r].value ==""){
				rowsObj[r+1].className ='lineError';
				pass = false;
			}else{
				rowsObj[r+1].className = lineClass;
			}
		}
	}
	//alert("pass:"+pass);
	if(pass == false){
		alert("ข้อมูล ไม่ถูกต้อง");
		return false;
	}
	
	//Validate TotalQty not over TotalDamage
	var totalQty = 	Number($('#totalQty').val().replace(/[^0-9\.]+/g,""));
	var totalDamage = 	Number($('#totalDamage').val().replace(/[^0-9\.]+/g,""));
	var diff = Math.abs(totalDamage-totalQty);
	//alert("totalQty["+totalQty+"]totalDamage["+totalDamage+"]diff["+diff+"]");
	if(diff > 0.5){
	   alert("ยอดรวมต่างกันได้ ไม่เกิน 0.50 ");
	   return false;
	}
	
	if(confirm("ยืนยันการบันทึกข้อมูล")){
		form.action = path + "/jsp/saDamageAction.do?do=save&action=newsearch";
		form.submit();
		return true;
	}
	return false;
}

function getInvRefWalKeypress(e,invRefWalObj){
	var form = document.saDamageForm;
	if(e != null && e.keyCode == 13){
		getInvRefWal(invRefWalObj);
	}
}
//Case Wacoal Only
function getInvRefWalOnblur(e,invRefWalObj){
	var form = document.saDamageForm;
	if(document.getElementsByName('bean.type')[1].checked){
		  getInvRefWal(invRefWalObj);
	 }
}

function resetInvRefWal(){
	var form = document.saDamageForm;
	form.invRefwal.value = '';
	form.oracleRefId.value = '';
	form.oracleRefName.value = '';
	form.totalDamage.value = '';
	
	if(document.getElementsByName('bean.type')[1].checked){//WCOAL
		form.totalDamage.className = 'normalText';
		form.totalDamage.readOnly = false;
		
	    document.getElementById('empId').className= "normalText";
		document.getElementById('empId').readOnly= false;
		document.getElementById('getempId').style.display='block';
	}else{
		//BME
		document.getElementById('empId').className= "disableText";
		document.getElementById('empId').readOnly= true;
		document.getElementById('getempId').style.display='none';

	}
}

function getInvRefWal(invRefWalObj){
	var returnString = "";
	var form = document.saDamageForm;
	
	//validate check bme or wacoal
	if(document.getElementsByName('bean.type')[0].checked==false
		&& document.getElementsByName('bean.type')[1].checked ==false){
		alert("กรุณาเลือกประเภท B'ME หรือ  WACOAL");
		return false;
	}
	
	//BME
	if(document.getElementsByName('bean.type')[0].checked){
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/sa/saGetInvoiceAjax.jsp",
				data : "invRefWal=" + invRefWalObj.value,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		//alert(returnString);
		
		if(returnString !=''){
			if("ERROR_FOUND_IN_DB" ==returnString ){
				alert("INV/Ref Wacoal นี้ ได้เคยมีการบันทึกไปแล้ว กรุณาตรวจสอบใหม่");
				invRefWalObj.focus();
				invRefWalObj.value ="";
				
				form.oracleRefId.value = '';
				form.oracleRefName.value = '';
			    form.totalDamage.value = '';
			}else{
				var retArr = returnString.split("|");
				form.oracleRefId.value = retArr[0];
				form.oracleRefName.value = retArr[1];
			    form.totalDamage.value = retArr[2];
			    
			    if(retArr[3] !=""){
			    
				    //Get Emp 
				    form.empId.value = retArr[3];
				    form.name.value = retArr[4];
					form.surname.value = retArr[5];
				    form.branch.value = retArr[6];
				    form.groupStore.value = retArr[7];
				    
				    form.fullName.value = form.name.value+" "+form.surname.value;
			    }else{
			    	alert("ไม่พบข้อมูลพนักงานที่ตรงกับรหัสร้านค้าใน Invoice กรุณาตรวจสอบข้อมูลพนักงาน SA");
			    	invRefWalObj.focus();
					invRefWalObj.value ="";
					
					form.oracleRefId.value = '';
					form.oracleRefName.value = '';
				    form.totalDamage.value = '';
				    
				    form.empId.value = '';
				    form.name.value = '';
					form.surname.value = '';
				    form.branch.value = '';
				    form.groupStore.value = '';
				    form.fullName.value = '';
			    }
			}

		}else{
			alert("ไม่พบข้อมูล Invoice นี้ ใน Sale Analysis กรุณาตรวจสอบก่อน");
			invRefWalObj.focus();
			invRefWalObj.value ="";
			
			form.oracleRefId.value = '';
			form.oracleRefName.value = '';
		    form.totalDamage.value = '';
		    
		    form.empId.value = '';
		    form.name.value = '';
			form.surname.value = '';
		    form.branch.value = '';
		    form.groupStore.value = '';
		    form.fullName.value = '';
		}
	}else{
		//WACOAL
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/sa/saGetInvDamageAjax.jsp",
			data : "invRefWal=" + invRefWalObj.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
		//alert(returnString);
		
		if(returnString !=''){
			alert("Ref Wacoal นี้ ได้เคยมีการบันทึกไปแล้ว กรุณาตรวจสอบใหม่");
			invRefWalObj.focus();
			invRefWalObj.value ="";

		}
	}
}

function getEmpKeypress(e,empId){
	var form = document.saDamageForm;
	if(e != null && e.keyCode == 13){
		getEmp(empId);
	}
}

function getEmp(empIdObj){
	var returnString = "";
	var form = document.saDamageForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/sa/saGetEmpAjax.jsp",
			data : "empId=" + empIdObj.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
//	alert(returnString);
	
	if(returnString !=''){
		var retArr = returnString.split("|");
		form.name.value = retArr[0];
		form.surname.value = retArr[1];
	    form.branch.value = retArr[2];
	    form.groupStore.value = retArr[3];
	    form.fullName.value = retArr[0]+" "+retArr[1];
	}else{
		alert("ไม่พบข้อมูล Employee กรุณาตรวจสอบก่อน");
		empIdObj.focus();
		empIdObj.value ="";
		
		form.name.value = "";
		form.surname.value ="";
	    form.branch.value = "";
	    form.groupStore.value = "";
	    form.fullName.value = "";
	}
}
function isNum(obj){
	//alert("isNum");
	  if(obj.value != ""){
		if(isNaN(obj.value)){
			alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
			obj.value = "";
			obj.focus();
			return false;
		}else{return true;}
	   }
	  return true;
	}

function openPopupEmp(path,seqNo,types){
	var param = "&page=searchEmpPopup";
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}
function setEmpMainValue(empId,name,surname,branch,groupStore){
	var form = document.saDamageForm;
	form.empId.value = empId;
	form.name.value = name;
	form.surname.value =surname;
    form.branch.value = branch;
    form.groupStore.value = groupStore;
    form.fullName.value = name +" "+surname;
	
} 
</script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerMC.jsp"/></td>
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
				<jsp:param name="function" value="saDamage"/>
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
						<html:form action="/jsp/saDamageAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
									<td colspan="6" align="center">	
									   <font size="3" ><b>
										 <%if("add".equals(saDamageForm.getMode())){ %>
									                                เพิ่มข้อมูล
										 <%}else{ %>
									                            แก้ไขข้อมูล
										 <%} %>
										 </b>
										 </font>	
									</td>
								</tr>
								<tr>
                                    <td colspan="4" align="center">
                                    <%if("add".equals(saDamageForm.getMode())){ %>
	                                     <html:radio property="bean.type" styleId="type" value="BME" onclick="resetInvRefWal()"/><font size="3">B'ME</font>
	                                      &nbsp; &nbsp; &nbsp; &nbsp; 
	                                     <html:radio property="bean.type" styleId="type" value="WACOAL" onclick="resetInvRefWal()"/><font size="3">WACOAL</font>
                                     <%}else{ %>
                                        <html:radio property="bean.type" styleId="type" value="BME" onclick="resetInvRefWal()" disabled="true"/><font size="3">B'ME</font>
	                                      &nbsp; &nbsp; &nbsp; &nbsp; 
	                                     <html:radio property="bean.type" styleId="type" value="WACOAL" onclick="resetInvRefWal()" disabled="true"/><font size="3">WACOAL</font>
	                                   <%} %>
                                     </td>
								</tr>
								<tr>
                                    <td  align="right">Invoice No / Ref Wacoal<font color="red">*</font></td>
									<td colspan="3">		
									<%if("add".equals(saDamageForm.getMode())){ %>
										  <html:text property="bean.invRefwal" styleId="invRefwal" size="30" onkeypress="getInvRefWalKeypress(event,this)"
										   onblur="getInvRefWalOnblur(event,this)"> </html:text>
										<%}else{ %>
										  <html:text property="bean.invRefwal" styleId="invRefwal" size="30" styleClass="disableText" readonly="true"> </html:text>
										<%} %>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									วันที่บันทึก <font color="red"></font>
											
										  <html:text property="bean.tranDate" styleId="tranDate" size="30" styleClass="disableText" readonly="true"> </html:text>
									</td>
								</tr>
								<tr>
                                    <td  align="right">รหัสร้านค้า<font color="red"></font></td>
									<td>		
								       <html:text property="bean.oracleRefId" styleId="oracleRefId" size="20" styleClass="disableText" readonly="true"> </html:text>
								    </td>
								    <td> </td>
									 <td>
									  <html:text property="bean.oracleRefName" styleId="oracleRefName" size="30" styleClass="disableText" readonly="true"> </html:text>
								    	 ห้าง / สาขา
									     <html:text property="bean.groupStore" styleId="groupStore" size="10" styleClass="disableText" /> /
										  <html:text property="bean.branch" styleId="branch" size="30" styleClass="disableText" />
									</td>
								</tr>
								<tr>
                                    <td align="right">Employee ID<font color="red">*</font></td>
									<td nowrap> 
										   <html:text property="bean.empId" styleId="empId" size="20" maxlength="6"  onkeypress="getEmpKeypress(event,this)" readonly="true" styleClass="disableText"/>  
									</td>
									<td>  
										  <input  tabindex="-1" type="button" id="getempId" name="getempId" value="..." onclick="openPopupEmp('${pageContext.request.contextPath}')" />
									</td>
									<td align="left">
										   <html:text property="bean.fullName" styleId="fullName" size="40" styleClass="disableText" > </html:text>
										    <html:hidden property="bean.name" styleId="name"  />
										   <html:hidden property="bean.surname" styleId="surname"  />
									   
									 </td>
								</tr>
								<tr>
                                    <td  align="right">วันที่เข้าตรวจนับ<font color="red"></font></td>
									<td colspan="3">		
										 <html:text property="bean.checkStockDate" styleId="checkStockDate" size="30" readonly="true"> </html:text>
									ค่าความเสียหาย<font color="red"></font>
										  <html:text property="bean.totalDamage" styleId="totalDamage" size="30" readonly="true" styleClass="disableText" onblur="isNum2Digit(this);"> </html:text>
									</td>
								</tr>
								
								<tr>
                                    <td  align="right">Remark<font color="red"></font></td>
									<td colspan="3">		
										 <html:text property="bean.remark" styleId="remark" size="60"> </html:text>
									</td>
								</tr>
						   </table>
						   
						    <!-- Items -->
						  <c:if test="${saDamageForm.bean.canEdit == true}">
	                        <div align="left" style="padding-left: 200px;">
								<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow();"/>	
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
							</div>
				           </c:if> 
						   <table id="tblProduct" align="center" width="75%" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth">
						       <tr> 
						            <th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
						            <th>ลำดับที่</th>
						            <th>ประเภทการหักเงิน</th>
									<th>วันที่ส่งเงิน(-)</th>
									<th>จำนวนเงิน(บาท)</th>
							   </tr>
							<% 
							String tabclass ="lineE";
							
							List<SADamageBean> items = saDamageForm.getBean() !=null?saDamageForm.getBean().getItems():null;
							if(items != null)
							   System.out.println("Screen items size:"+items.size());
							int tabindex = 1;
							int no = 0;
							if(items != null && items.size() >0){
								for(int n=0;n<items.size();n++){
									SADamageBean item = item = items.get(n);
									if(n%2==0){
										tabclass="lineO";
									}
									%>
									<tr class="<%=tabclass%>">
									    <td class="td_text_center"  width="10%">
										  <input type="checkbox" name="linechk" value="<%=item.getLineId() %>"/>
										  <input type="hidden" name="status" id="status" value=""/>
										   <input type="hidden" name="ids" value="<%=item.getId() %>"/>
										</td>
									     <td class="td_text_center" width="15%">
										    <input type="text" name="lineId" readonly id="lineId" value="<%=item.getLineId() %>" size="3" class="disableText" tabindex="<%out.print(tabindex);tabindex++;%>">
										</td>
										<td class="td_text_center" width="15%" >
											<select name="payType" id="payType" tabindex="<%out.print(tabindex);tabindex++;%>">
						                      <%if(payTypeList != null && payTypeList.size() >0){ 
						                    	  String selected = "";
						                        for(int i=0;i<payTypeList.size();i++){
						                        	PopupForm p = payTypeList.get(i);
						                        	selected = item.getPayType().equals(p.getCode())?"selected":"";
						                      %>
											  <option value="<%=p.getCode()%>" <%=selected%>><%=p.getDesc()%></option>
											  <%}} %>
											</select>
										</td>
										<td class="td_text_center" width="15%">
										   <input type="text" name="payDate" readonly id="payDate" size="30"  value="<%=item.getPayDate() %>" tabindex="<%out.print(tabindex);tabindex++;%>">
										</td>
										<td class="td_text_center" width="15%"> 
										   <input type="text" name="payAmt" id="payAmt" size="30" value="<%=item.getPayAmt() %>" tabindex="<%out.print(tabindex);tabindex++;%>"
										    onblur="isNum2Digit(this);sumTotal();"  class="enableNumber">
										</td>
									</tr>
							<%} }//for  %>
							
					</table>
					<table align="center" width="75%" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth">
					        <tr>
					            <td class="td_text_center" width="10%"></td>
							    <td class="td_text_center" width="15%"></td>
                             	<td class="td_text_center" width="15%"></td>
								<td class="td_text_right" width="15%"> <b>ยอดรวม</b></td>
								<td class="td_text_center" width="15%"> &nbsp;&nbsp;
								   <input type="text" name="totalQty" id="totalQty" size="22" class="disableNumberBold" readonly>
								</td>
							</tr>
					</table>
						   <!-- Items -->
						   <p></p>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
							
										<a href="javascript:save('${pageContext.request.contextPath}')">
										  <input type="button" value="    บันทึก   " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>							
									</td>
								</tr>
							</table>
					  </div>
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
					<!-- hidden field -->
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