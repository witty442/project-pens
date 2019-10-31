<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="adjustStockForm" class="com.isecinc.pens.web.adjuststock.AdjustStockForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	 sumTotalIssueQty();
	 sumTotalReceiptQty();
	 sumTotalDiffCost();
	 
	 //move cursor to last row
	 var itemIssueDesc = document.getElementsByName("itemIssueDesc");
	 //alert(itemIssueDesc.length);
	 itemIssueDesc[itemIssueDesc.length-1].focus();
}
function clearForm(path){
	var form = document.adjustStockForm;
	form.action = path + "/jsp/adjustStockAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.adjustStockForm;
	form.action = path + "/jsp/adjustStockAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function sumTotalIssueQty(){
	var itemIssueQty = document.getElementsByName("itemIssueQty");
	var totalIssueQty = 0;
	for(var i= 0;i<itemIssueQty.length;i++){
		if(itemIssueQty[i].value != ''){
			totalIssueQty += parseInt(itemIssueQty[i].value);
		}
	}
	document.getElementsByName("totalIssueQty")[0].value = totalIssueQty;
}
function sumTotalReceiptQty(){
	var itemIssueQty = document.getElementsByName("itemReceiptQty");
	var totalIssueQty = 0;
	for(var i= 0;i<itemIssueQty.length;i++){
		if(itemIssueQty[i].value != ''){
			totalIssueQty += parseInt(itemIssueQty[i].value);
		}
	}
	document.getElementsByName("totalReceiptQty")[0].value = totalIssueQty;
}

function sumTotalDiffCost(){
	var itemIssueQty = document.getElementsByName("diffCost");
	var totalIssueQty = 0;
	for(var i= 0;i<itemIssueQty.length;i++){
		if(itemIssueQty[i].value != ''){
			totalIssueQty += parseFloat(itemIssueQty[i].value);
		}
	}
	var num = Number(totalIssueQty);
	document.getElementsByName("totalDiffCost")[0].value = num.toFixed(2);
}

function validateData(){
	var pass = true;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	if(rows.length ==1){
		return false;
	}
	
	var itemIssueDesc = document.getElementsByName("itemIssueDesc");
	var itemIssue = document.getElementsByName("itemIssue");
	
	var itemReceiptDesc = document.getElementsByName("itemReceiptDesc");
	var itemReceipt = document.getElementsByName("itemReceipt");
	
	//alert(itemIssue.length);
	
	var errorList = new Object();
	for(var i= 0;i<itemIssueDesc.length;i++){
		var rowObj = new Object();
		var lineClass ="lineE";
		if(i%2==0){
			lineClass = "lineO";
		}
		//alert(itemIssue[i].value );
		
		if(itemIssueDesc[i].value != ""){
			if( itemIssue[i].value =="" ){
			  rows[i+1].className ='lineError';
			  pass = false;
			}
		}
		if(itemReceiptDesc[i].value != "" ){
			if(itemReceipt[i].value ==""){
			  rows[i+1].className ='lineError';
			  pass = false;
			}
		}
		//no error
		if(pass){
			rows[i+1].className =lineClass;
		}
		//alert("rows["+i+"]:"+rows[i].className);
	}// for
	return pass ;
}
//deprecate
function validateItems(){
	var pass = true;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	if(rows.length ==1){
		return false;
	}
	
	var itemIssue = document.getElementsByName("itemIssue");
	var itemIssueQty = document.getElementsByName("itemIssueQty");
	var itemReceipt = document.getElementsByName("itemReceipt");
	var itemReceiptQty = document.getElementsByName("itemReceiptQty");
	
	//alert(itemIssue.length);
	
	var errorList = new Object();
	for(var i= 0;i<itemIssue.length;i++){
		var rowObj = new Object();
		var lineClass ="lineE";
		if(i%2==0){
			lineClass = "lineO";
		}
		
		//alert(itemIssue[i].value );
		
		if(itemIssue[i].value == "" && itemReceipt[i].value =="" 
			&& itemIssueQty[i].value=="" && itemIssueQty[i].value=="0"
			&& itemReceiptQty[i].value=="" && itemReceiptQty[i].value=="0"){
			
			rows[i+1].className ='lineError';
			pass = false;
		}else if(itemIssue[i].value != ""){
			if(    itemReceipt[i].value =="" 
				|| itemIssueQty[i].value=="" || itemIssueQty[i].value=="0"
				|| itemReceiptQty[i].value=="" || itemReceiptQty[i].value=="0"){
				
				rows[i+1].className ='lineError';
				pass = false;
			}else{
				rows[i+1].className =lineClass;
			}
			
		}else if(itemReceipt[i].value !="" ){
			if( itemReceiptQty[i].value=="" || itemReceiptQty[i].value=="0"){
				rows[i+1].className ='lineError';
				pass = false;
			}else{
				rows[i+1].className =lineClass;
			}
		}else{
			rows[i+1].className =lineClass;
		} //if
		
		//alert("rows["+i+"]:"+rows[i].className);
		
	}// for
	return pass ;
}

function save(path){
	var form = document.adjustStockForm;
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	if(rows.length ==1){
		alert("กรุณาเพิ่มรายการอย่างน้อย 1 รายการ");
		return false;
	}
	
	var pass = validateData();//validateItems();
	if(pass){
	   form.action = path + "/jsp/adjustStockAction.do?do=save";
	   form.submit();
	   return true;
    }else{
    	alert("ใส่ข้อมูลไม่ถูกต้อง");
    	return false;
    }
}

function exported(path){
	if(confirm("ยืนยันการ POST ")){
	   var form = document.adjustStockForm;
	   form.action = path + "/jsp/adjustStockAction.do?do=exported";
	   form.submit();
	   return true;
	}
}

function search(path){
	var form = document.adjustStockForm;
	var transactionDate =$('#transactionDate').val();
	var storeCode =$('#storeCode').val();
	if(transactionDate ==""){
		alert("กรุณากรอกวันที่  Transaction Date");
		return false;
	}
	if(storeCode ==""){
		alert("กรุณากรอก ร้านค้า");
		return false;
	}
	
	form.action = path + "/jsp/adjustStockAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function verifyData(path){
	var form = document.adjustStockForm;
	var transactionDate =$('#transactionDate').val();
	var storeCode =$('#storeCode').val();
	if(transactionDate ==""){
		alert("กรุณากรอกวันที่  Transaction Date");
		return false;
	}
	if(storeCode ==""){
		alert("กรุณากรอก ร้านค้า");
		return false;
	}
	
	form.action = path + "/jsp/adjustStockAction.do?do=verifyData&action=newsearch";
	form.submit();
	return true;
}

function openPopupProduct(path,seqNo,types){
	var param = "&page=searchPensItemByGroupPopup";
	    param += "&types="+types+"&seqNo="+seqNo;
	var url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	//window.open(encodeURI(url),"",
			  // "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",700);
}
function setProductMainValue(seqNo,types,groupCode,pensItem,price){
	//alert("seqNo:"+seqNo);
	
	if("itemIssue" == types){
		
		document.getElementsByName("itemIssueDesc")[seqNo-1].value = groupCode;
		document.getElementsByName("itemIssue")[seqNo-1].value = pensItem;
		document.getElementsByName("itemIssueUom")[seqNo-1].value = 'EA';
		document.getElementsByName("itemIssueRetailNonVat")[seqNo-1].value = price;
	}else{
		document.getElementsByName("itemReceiptDesc")[seqNo-1].value = groupCode;
		document.getElementsByName("itemReceipt")[seqNo-1].value = pensItem;
		document.getElementsByName("itemReceiptUom")[seqNo-1].value = 'EA';
		document.getElementsByName("itemReceiptRetailNonVat")[seqNo-1].value = price;
	}
} 

function getProductKeypress(e,type,groupCode,seqNo){
	
	var form = document.adjustStockForm;
	
	var itemIssue = document.getElementsByName("itemIssue");
	var itemReceipt = document.getElementsByName("itemReceipt");
	var itemIssueRetailNonVat = document.getElementsByName("itemIssueRetailNonVat");
	var itemReceiptRetailNonVat = document.getElementsByName("itemReceiptRetailNonVat");
	var itemIssueUom = document.getElementsByName("itemIssueUom");
	var itemReceiptUom = document.getElementsByName("itemReceiptUom");
	
	if(e != null && e.keyCode == 13){
		if('issue' == type){
			if(groupCode.value ==''){
				itemIssue[seqNo-1].value = '';
				itemIssueRetailNonVat[seqNo-1].value ='';
				itemIssueUom[seqNo-1].value ='';
			}else{
				getProductModel(type,groupCode,seqNo);
			}
		}else{
			if(groupCode.value ==''){
				itemReceipt[seqNo-1].value = '';
				itemReceiptRetailNonVat[seqNo-1].value ='';
				itemReceiptUom[seqNo-1].value ='';
			}else{
				getProductModel(type,groupCode,seqNo);
			}
		}
	}
}

function getProductModel(type,groupCode,seqNo){
	var itemIssue = document.getElementsByName("itemIssue");
	var itemIssueDesc = document.getElementsByName("itemIssueDesc");
	var itemIssueUom = document.getElementsByName("itemIssueUom");
	var itemIssueRetailNonVat = document.getElementsByName("itemIssueRetailNonVat");
	
	var itemReceipt = document.getElementsByName("itemReceipt");
	var itemReceiptDesc = document.getElementsByName("itemReceiptDesc");
	var itemReceiptRetailNonVat = document.getElementsByName("itemReceiptRetailNonVat");
	var itemReceiptUom = document.getElementsByName("itemReceiptUom");
	
	var returnString = "";
	var form = document.adjustStockForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoProductByGroup.jsp",
			data : "groupCode=" + groupCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	
	if('issue' == type){
		if(returnString==''){
			alert("ไม่พบข้อมูล item "+groupCode.value);
			groupCode.value ="";
			groupCode.focus();
			
			itemIssue[seqNo-1].value = '';
		    itemIssue[seqNo-1].value = '';
		    itemIssueRetailNonVat[seqNo-1].value ='';
		    itemIssueUom[seqNo-1].value ='';
		}else{
			var s = returnString.split("|");
			itemIssue[seqNo-1].value = s[0];
			itemIssueRetailNonVat[seqNo-1].value = s[1];
			itemIssueUom[seqNo-1].value ='EA';
		}
	}else{
		if(returnString==''){
           alert("ไม่พบข้อมูล item "+groupCode.value);
           groupCode.value ="";
           groupCode.focus();
			   
		   itemReceipt[seqNo-1].value = '';
		   itemReceipt[seqNo-1].value = '';
		   itemReceiptRetailNonVat[seqNo-1].value ='';
		   itemReceiptUom[seqNo-1].value ='';
		  
		}else{
		   var s = returnString.split("|");
		   itemReceipt[seqNo-1].value = s[0];
		   itemReceiptRetailNonVat[seqNo-1].value =s[1];
		   itemReceiptUom[seqNo-1].value ='EA';
		}
	}
	
	calcDiffCost(seqNo);
}

function addRow(path){
	var form = document.adjustStockForm;
	form.action = path + "/jsp/adjustStockAction.do?do=addRow";
	form.submit();
	return true;
}

function removeRow(path){
	var form = document.adjustStockForm;
	//alert(isSelectOne());
	
	if(isSelectOne()){
		form.action = path + "/jsp/adjustStockAction.do?do=removeRow";
		form.submit();
		return true;
	}
	return false;
}

function isSelectOne(){
	//todo play with type
	var chk = document.getElementsByName("linechk");
	//alert(chk.length);
	
	var bcheck=false;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	
	return true;
}

function openPopupCustomer(path,types,storeType){
    var param = "&types="+types;
        param += "&storeType="+storeType;
    
	var url = path + "/jsp/searchCustomerPopupAction.do?do=prepare2&action=new"+param;
	//window.open(encodeURI(url),"",
			   //"menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",700);
}

function setStoreMainValue(code,desc,types){
	//alert(types+":"+desc);
	if("from" == types){
		document.getElementsByName("adjustStock.storeCode")[0].value = code;
		document.getElementsByName("adjustStock.storeName")[0].value = desc;
	}
}

function getStoreNameKeypress(e,storeCode){
	var form = document.adjustStockForm;
	if(e != null && e.keyCode == 13){
		if(storeCode.value ==''){
			form.storeName.value = '';
		}else{
			getStoreNameModel(storeCode);
		}
	}
}

function getStoreNameModel(storeCode){
	var returnString = "";
	var form = document.adjustStockForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameAjax.jsp",
			data : "custCode=" + storeCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	form.storeName.value = returnString;	
}

function isNum(obj){
  if(obj.value != ""){
	var newNum = parseInt(obj.value);
	if(isNaN(newNum)){
		alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
		obj.value = "";
		obj.focus();
		return false;
	}else{return true;}
   }
  return true;
}

function calcDiffCost(seqNo){
	var diffCostObj = document.getElementsByName("diffCost");
	
	var itemIssueRetailNonVat = document.getElementsByName("itemIssueRetailNonVat");
	var itemIssueQty = document.getElementsByName("itemIssueQty");
	var itemReceiptRetailNonVat = document.getElementsByName("itemReceiptRetailNonVat");
	var itemReceiptQty = document.getElementsByName("itemReceiptQty");
	
	//alert(seqNo);
	//alert(itemIssueRetailNonVat[seqNo-1].value);
	
	var issue= parseFloat(itemIssueRetailNonVat[seqNo-1].value) * parseFloat(itemIssueQty[seqNo-1].value) ;
	var receipt = parseFloat(itemReceiptRetailNonVat[seqNo-1].value) * parseFloat(itemReceiptQty[seqNo-1].value); 
	
	var diffCost = receipt - issue;
	diffCostObj[seqNo-1].value =  diffCost.toFixed(2);
}

function checkSelect(chk1,chk2){
	for(var i=0;i<chk2.length;i++){
		chk2[i].checked = chk1.checked;
	}
	return;
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
				<jsp:param name="function" value="adjustStock"/>
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
						<html:form action="/jsp/adjustStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Transaction Date</td>
									<td >
									 <c:if test="${adjustStockForm.mode=='add'}">
										  <html:text property="adjustStock.transactionDate" styleId="transactionDate" size="20" readonly="true" />
										  <font color="red">*</font>
								     </c:if>
								     <c:if test="${adjustStockForm.mode =='edit'}">
								         <html:text property="adjustStock.transactionDate" styleId="transactionDateX" size="20" readonly="true" styleClass="disableText"/>
								     </c:if>
								     <c:if test="${adjustStockForm.mode =='view'}">
								         <html:text property="adjustStock.transactionDate" styleId="transactionDateX" size="20" readonly="true" styleClass="disableText"/>
								     </c:if>
									</td>
								</tr>
								 <tr>
                                    <td> รหัสร้านค้า</td>
									<td >
									  <c:if test="${adjustStockForm.mode=='add'}">
						                 <html:text property="adjustStock.storeCode" styleId="storeCode" size="20" 
						                  onkeypress="getStoreNameKeypress(event,this)" styleClass=" \" autoComplete=\"off"
						                  />
									     <font color="red">*</font>-
									     <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','all')"/>
									  </c:if>
									  <c:if test="${adjustStockForm.mode == 'edit'}">
									     <html:text property="adjustStock.storeCode" styleId="storeCode" size="20" 
						                  readonly="true"  styleClass="disableText"/>
									  </c:if>
									    <html:text property="adjustStock.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
									</td>
								</tr>
								 <tr>
                                    <td> รหัสบัญชี</td>
									<td >
						               <html:text property="adjustStock.bankNo" styleId="bankNo" size="30" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								 <tr>
                                    <td> ORG/Sub Inventory</td>
									<td >
						               <html:text property="adjustStock.org" styleId="org" size="5" readonly="true" styleClass="disableText"/>/
						               <html:text property="adjustStock.subInv" styleId="subInv" size="5" styleClass="disableText" />
									</td>
								</tr>
								<tr>
                                    <td> Document No</td>
									<td >
						               <html:text property="adjustStock.documentNo" styleId="documentNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td> Reference</td>
									<td >
									  <c:if test="${adjustStockForm.mode == 'edit'}">
						                 <html:text property="adjustStock.ref" styleId="ref" size="30"  styleClass=" \" autoComplete=\"off"/>
						              </c:if>
						              <c:if test="${adjustStockForm.mode == 'add'}">
						                 <html:text property="adjustStock.ref" styleId="ref" size="30"  styleClass=" \" autoComplete=\"off"/>
						              </c:if>
						              <c:if test="${adjustStockForm.mode != 'add'}">
						               <c:if test="${adjustStockForm.mode != 'edit'}">
						                   <html:text property="adjustStock.ref" styleId="ref" size="30" readonly="true" styleClass="disableText"/>
						                 </c:if>
						              </c:if>
									</td>
								</tr>
								<tr>
                                    <td> Status</td>
									<td >
						               <html:text property="adjustStock.statusDesc"  size="25" readonly="true" styleClass="disableText"/>
						               <html:hidden property="adjustStock.status" />
						               Status Message 
						               <html:text property="adjustStock.statusMessage"  size="50" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
						   </table>
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									    <c:if test="${adjustStockForm.mode=='add'}">
											<a href="javascript:verifyData('${pageContext.request.contextPath}')">
											  <input type="button" value="    ตรวจสอบข้อมูล      " class="newPosBtnLong"> 
											</a>
											
											<a href="javascript:clearForm('${pageContext.request.contextPath}')">
											  <input type="button" value="   Clear   " class="newPosBtnLong">
											</a>
										</c:if>			
										<a href="javascript:back('${pageContext.request.contextPath}')">
											  <input type="button" value="   ปิดหน้าจอ  " class="newPosBtnLong">
										</a>				
									</td>
								</tr>
							</table>
					  </div>

            <c:if test="${adjustStockForm.verify==true}">
                  <c:if test="${adjustStockForm.adjustStock.canEdit == true}">
                        <div align="left">
							<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow('${pageContext.request.contextPath}');"/>	
							<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
						</div>
				  </c:if>  			
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
									<th >Seq no</th>
									<th ><input type="checkbox" name="chkAll"
										onclick="checkSelect(this,document.getElementsByName('linechk'));" /></th>
									
									<th >Group Issue</th>
									<th >Issue Description</th>
									<th >UOM</th>
									<th >ราคาขายส่งก่อน VAT</th>
									<th >Issue Qty</th>
									<th >Group Receipt</th>
									<th >Receipt Description</th>
									<th >UOM</th>							
									<th >ราคาขายส่งก่อน VAT</th>
									<th >Receipt Qty</th>
									<th >Diff Cost</th>
							</tr>
							<c:forEach var="results" items="${adjustStockForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
							
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text">${results.seqNo}</td>
										<td class="td_text"><input type="checkbox" name="linechk" value="${results.seqNo}"/></td>
										<td class="td_text" nowrap>
										   <c:if test="${results.canEdit == true}">
											    <input type="text" name="itemIssueDesc" id="itemIssueDesc" value ="${results.itemIssueDesc}" size="10" 
											    onkeypress="getProductKeypress(event,'issue',this,${results.seqNo})"
											    onchange="getProductModel('issue',this,${results.seqNo})"
											    autocomplete="off"
											    />
											     <input  tabindex="-1" type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}',${results.seqNo},'itemIssue')"
											     class="enableNumber"/>
										   </c:if>
										   <c:if test="${results.canEdit == false}">
											    <input type="text" name="itemIssueDesc" id="itemIssueDesc" value ="${results.itemIssueDesc}" size="10" 
											     readonly class="disableNumber"/>
										   </c:if>

										</td>
										<td class="td_text"><input tabindex="-1" type="text" name="itemIssue" value ="${results.itemIssue}" size="20" readonly class="disableText"/></td>
										
										<td class="td_text"><input tabindex="-1" type="text" name="itemIssueUom" value ="${results.itemIssueUom}" size="3" readonly class="disableText"/></td>
										<td class="td_text"><input tabindex="-1" type="text" name="itemIssueRetailNonVat" value ="${results.itemIssueRetailNonVat}" size="10" readonly class="disableNumber"/></td>
										<td class="td_text">
											<c:if test="${results.canEdit == true}">
											    <input type="text" name="itemIssueQty" id="itemIssueQty" value ="${results.itemIssueQty}" size="10" onkeypress="isNum(this)" 
											     class="enableNumber" onblur="sumTotalIssueQty();calcDiffCost(${results.seqNo})"
											     autocomplete="off"/>
											</c:if>
											<c:if test="${results.canEdit == false}">
											   <input type="text" name="itemIssueQty" id="itemIssueQty" value ="${results.itemIssueQty}" size="10" readonly class="disableNumber"/>
											</c:if>
										</td>
										
										<td class="td_text"  nowrap>
										  <c:if test="${results.canEdit == true}">
											    <input type="text" name="itemReceiptDesc" id="itemReceiptDesc" value ="${results.itemReceiptDesc}" size="10"
											     onkeypress="getProductKeypress(event,'receipt',this,${results.seqNo})"
											     onchange="getProductModel('receipt',this,${results.seqNo})"
											      autocomplete="off"
											     />
											    <input  tabindex="-1" type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}',${results.seqNo},'itemReceipt')"/>
								
										 </c:if>
										 <c:if test="${results.canEdit == false}">
										      <input type="text" name="itemReceiptDesc" id="itemReceiptDesc" value ="${results.itemReceiptDesc}" size="10"
											     readonly class="disableNumber" />
										 </c:if>
										</td>
										<td class="td_text"><input  tabindex="-1" type="text" name="itemReceipt" value ="${results.itemReceipt}" size="20" readonly class="disableText"/></td>
										
										<td class="td_text"><input  tabindex="-1" type="text" name="itemReceiptUom" value ="${results.itemReceiptUom}" size="3" readonly class="disableText"/></td>
										<td class="td_text"><input  tabindex="-1" type="text" name="itemReceiptRetailNonVat" value ="${results.itemReceiptRetailNonVat}" size="10" readonly class="disableNumber"/></td>
										<td class="td_text">
										  <c:if test="${results.canEdit == true}">
										     <input type="text" name="itemReceiptQty" id="itemReceiptQty" value ="${results.itemReceiptQty}" size="10" onkeypress="isNum(this)" 
										      class="enableNumber" onblur="sumTotalReceiptQty();calcDiffCost(${results.seqNo});sumTotalDiffCost()"
										      autocomplete="off"/>
										  </c:if>
										  <c:if test="${results.canEdit == false}">
										     <input type="text" name="itemReceiptQty" id="itemReceiptQty" value ="${results.itemReceiptQty}" size="10" readonly class="disableNumber"/>
										  </c:if>
									    </td>
										  
										<td class="td_text"><input  tabindex="-1" type="text" name="diffCost" value ="${results.diffCost}" size="10" readonly class="disableNumber"/></td>
	
									</tr>
							
							  </c:forEach>
							  <tr>
									<td ></td>
									<td ></td>
									<td ></td>
									<td ></td>
									<td ></td>
									<td ></td>
									<td ><input type="text" name="totalIssueQty" class="disableNumber" size="10"/></td>
									<td ></td>
									<td ></td>
									<td ></td>							
									<td ></td>
									<td ><input type="text" name="totalReceiptQty" class="disableNumber" size="10"/></td>
									<td ><input type="text" name="totalDiffCost" class="disableNumber" size="10"/></td>
							</tr>
					</table>
				 <c:if test="${adjustStockForm.adjustStock.canEdit == true}">
                        <div align="left">
							<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow('${pageContext.request.contextPath}');"/>	
							<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
						</div>
				  </c:if>  		
								
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="left">
										    <c:if test="${adjustStockForm.adjustStock.canEdit ==true}">
												<a href="javascript:save('${pageContext.request.contextPath}')">
												  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
												</a>
										  </c:if>
										
									      <c:if test="${adjustStockForm.adjustStock.canExport ==true}">							 
												<a href="javascript:exported('${pageContext.request.contextPath}')">
												  <input type="button" value="   POST    " class="newPosBtnLong"> 
												</a>
											</c:if>
									   
																
										</td>
									</tr>
						</table>
					</div>
				</c:if>
					<!-- ************************Result ***************************************************-->
					
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
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