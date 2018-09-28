<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="mttForm" class="com.isecinc.pens.web.mtt.MTTForm" scope="session" />

<%
 String mode = mttForm.getMode();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('saleDate'));
	 
	 <%if( !mode.equals("view")){ %>
	    addRow();
	 <%}%>
	 calcTotalRow();
}
function clearForm(path){
	var form = document.mttForm;
	form.action = path + "/jsp/mttAction.do?do=clear";
	form.submit();
	return true;
}
function printReport(path){
	var form = document.mttForm;
	form.action = path + "/jsp/mttAction.do?do=printReport";
	form.submit();
	return true;
}

function cancel(path){
	if(confirm("กรุณายืนยัน การลบข้อมูลขายทั้งเอกสาร")){
		var form = document.mttForm;
		form.action = path + "/jsp/mttAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function back(path){
	var form = document.mttForm;
	form.action = path + "/jsp/mttAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function save(path){
	var form = document.mttForm;
	var storeCode =$('#storeName').val();
	var saleDate =$('#saleDate').val();
	var custGroup =$('#custGroup').val();

	//validate item
	var barcode = document.getElementsByName("barcode");
	if(barcode[0].value == ""){
		alert("กรุณากรอก ใส่รายการ อย่างน้อย 1 รายการ");
		return false;
	}
	if(custGroup =="" ){
		alert("กรุณากรอกข้อมูลกลุ่มร้านค้า ");
		return false;
	}
	if(saleDate =="" ){
		alert("กรุณากรอก วันที่ขาย ");
		return false;
	}
	if(storeCode =="" ){
		alert("กรุณากรอก รหัสร้านค้า ");
		return false;
	}
	
	form.action = path + "/jsp/mttAction.do?do=save";
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
	var form = document.mttForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.mttForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
	
	if(storeNo=='' || subInv==''){
		if(storeNo==''){
			alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
		}
		if(subInv==''){
			alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
		}
		form.storeCode.value = '';
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}else{
	   form.storeNo.value = storeNo;
	   form.subInv.value = subInv;
	}
	
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.mttForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}else{
		  getCustName(custCode,fieldName);
		}
	}
}

function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.mttForm;
	var storeGroup = form.custGroup.value;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameWithSubInvAjax.jsp",
				data : "custCode=" + custCode.value+"&storeGroup="+storeGroup,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		
		if("storeCode" == fieldName){
			if(returnString !=''){
				var retArr = returnString.split("|");
				form.storeName.value = retArr[0];
				
				if(retArr[1]=='' || retArr[2]==''){
					if(retArr[1]==''){
						alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
					}
					if(retArr[2]==''){
						alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
					}
					form.storeCode.value = '';
					form.storeName.value = "";
					form.storeNo.value = "";
					form.subInv.value = "";
				}else{
					form.storeNo.value = retArr[1];
					form.subInv.value = retArr[2];
				}
				
			}else{
				alert("ไม่พบข้อมูล");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}
}

function resetStore(){
	var form = document.mttForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}
}

function addRow(){
	// $('#myTable tr').length;
	var rows = $('#tblProduct tr').length-1;
	var className = 'lineO';
	if(rows%2 !=0){
		className = 'lineE';
	}

	var lineId = rows+1;
	
	//alert("lineId["+lineId+"]");
	
	var rowData ="<tr class='"+className+"'>"+
	
	   // "<td class='data_no'> <input type='text' tabindex ='-1' name='no' size='5' readonly class='disableText' value='"+lineId+"'/></td>"+
	    "<td class='data_linechk'> <input type='checkbox' tabindex ='-1' name='linechk' value='0'/>"+
	    "  <input type='hidden' tabindex ='-1' name='lineId' />"+
	    "</td>"+
	    "<td class='data_barcode'> <input type='text' name='barcode' size='30'  "+
	    " onkeypress='getProductKeypress(event,this,"+lineId+")' "+
	  //  " onchange='getProductModel(this,"+lineId+")' "+
	    " />  </td>"+
	    "<td class='data_materialMaster'> <input type='text' tabindex ='-1' name='materialMaster' size='30' onkeypress='getProductKeypressByMat(event,this,"+lineId+")'/></td>"+
	    "<td class='data_groupCode'> <input type='text' tabindex ='-1' name='groupCode' readonly class='disableText' size='30' /></td>"+
	    "<td class='data_pensItem'> <input type='text' tabindex ='-1' name='pensItem' readonly class='disableText' size='20' /></td>"+
	    
	    "<td class='data_retailPriceBF'>"+ 
	    "<input type='text' tabindex ='-1' name='retailPriceBF' readonly class='disableNumber' size='20'/></td>"+
	    "<input type='hidden' tabindex ='-1' name='wholePriceBF' readonly class='disableNumber' size='20' />"+
	    "<td class='data_pensItem'> "+
	    "  <input type='text' tabindex ='-1' id='statusDesc' name='statusDesc' value='NEW' readonly class='disableText' size='20' />"+
	    "  <input type='hidden' tabindex ='-1' id='status' name='status' value='N' readonly class='disableText' size='20' />"+
	    "</td>"+
	    "</tr>";

    $('#tblProduct').append(rowData);
    //set focus default
    var barcode = document.getElementsByName("barcode");
    barcode[lineId-1].focus();
}


function calcTotalRow(){
	var rows = $('#tblProduct tr').length-1;//1,head row ,2 blank row
	var barcodeLastRow = document.getElementsByName("barcode")[rows-1]; //alert(barcodeLastRow.value);
	if(barcodeLastRow.value ==''){
		rows = rows-1;// blank row
	}
	//Calc Row
    var totalRow = document.getElementsByName("totalRow");
    totalRow[0].value = rows;
}

function removeRow(path){
	if(confirm("ยืนยันการลบ barcode นี้")){
		//todo play with type
		var tbl = document.getElementById('tblProduct');
		var chk = document.getElementsByName("linechk");
		var bcheck=false;
		var lineId = "";
		for(var i=chk.length-1;i>=0;i--){
			if(chk[i].checked){
				bcheck=true;
				lineId = chk[i].value;
				
				drow = tbl.rows[i+1];
				
				//alert("lineId:"+lineId);
				if(lineId=="" || lineId=="0"){
					removeRowBlank(drow);
				}else{
					removeRowUpdate(path,drow,i);
				}
			}
		}
		if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
	}
	return false;
}
	
function removeRowUpdate(path,drow,index){
	//todo play with type	
	drow.style.display ='none';
	//set  staus = AB
	//alert(index);
	document.getElementsByName("status")[index].value ='AB';
	document.getElementsByName("statusDesc")[index].value ='Cancel';
	
	//update db
	save(path);
}
	
function removeRowBlank(drow){
	//todo play with type
	$(drow).remove();
}
	
function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
}


function getProductKeypress(e,barcodeObj,lineId){
	//materialMaster groupCode pensItem wholePriceBF retailPriceBF
	//alert(barcode.value);
	
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	var wholePriceBF = document.getElementsByName("wholePriceBF");
	var retailPriceBF = document.getElementsByName("retailPriceBF");
	
	var storeCode =$('#storeName').val();
	if(storeCode =="" ){
		alert("กรุณากรอก รหัสร้านค้า ");
		return false;
	}
	
	if(e != null && e.keyCode == 13){
	
		if(barcodeObj.value ==''){
			
			materialMaster[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
			wholePriceBF[lineId-1].value = '';
			retailPriceBF[lineId-1].value = '';
		}else{
			var found = getProductModel(barcodeObj,lineId);
			if(found){
				calcTotalRow();
				
				//Add New Row Auto
				addRow();
				
				barcode[lineId].focus();
				
				//Set Prev row readonly 
				barcode[lineId-1].className ="disableText";
				barcode[lineId-1].readOnly = true;
			}
		}
	}
}

function getProductModel(barcodeObj,lineId){
	var found = false;
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	var wholePriceBF = document.getElementsByName("wholePriceBF");
	var retailPriceBF = document.getElementsByName("retailPriceBF");
	
	var returnString = "";
	var form = document.mttForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcode.jsp",
			data : "itemCode=" + barcodeObj.value+"&storeCode="+form.storeCode.value,
			async: false,
			cache: true,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	   // alert("x:"+returnString);
	    
		if(returnString==''){
			alert("ไม่พบข้อมูลสินค้า  "+barcodeObj.value);
			barcodeObj.focus();
			
			barcode[lineId-1].value = '';
			materialMaster[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
			wholePriceBF[lineId-1].value = '';
			retailPriceBF[lineId-1].value = '';
		}else{
			var s = returnString.split("|");
			
			barcode[lineId-1].value = s[0];
			materialMaster[lineId-1].value = s[1];
			groupCode[lineId-1].value = s[2];
			pensItem[lineId-1].value = s[3];
			wholePriceBF[lineId-1].value = s[4];
			retailPriceBF[lineId-1].value = s[5];
			
			found = true;
		}
	return found;
}

function getProductKeypressByMat(e,matObj,lineId){
	//materialMaster groupCode pensItem wholePriceBF retailPriceBF
	//alert(barcode.value);
	
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	var wholePriceBF = document.getElementsByName("wholePriceBF");
	var retailPriceBF = document.getElementsByName("retailPriceBF");
	
	if(e != null && e.keyCode == 13){
	
		if(matObj.value ==''){
			
			barcode[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
			wholePriceBF[lineId-1].value = '';
			retailPriceBF[lineId-1].value = '';
		}else{
			var found = getProductModelByMat(matObj,lineId);
			if(found){
				calcTotalRow();
				
				//Add New Row Auto
				addRow();
				
				barcode[lineId].focus();
				
				//Set Prev row readonly 
				materialMaster[lineId-1].className ="disableText";
				materialMaster[lineId-1].readOnly = true;
			}
		}
	}
}

function getProductModelByMat(matObj,lineId){
	var found = false;
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	var wholePriceBF = document.getElementsByName("wholePriceBF");
	var retailPriceBF = document.getElementsByName("retailPriceBF");
	
	var returnString = "";
	var form = document.mttForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcode.jsp",
			data : "matCode="+matObj.value+"&storeCode="+form.storeCode.value,
			async: false,
			cache: true,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	   // alert("x:"+returnString);
	    
		if(returnString==''){
			alert("ไม่พบข้อมูลสินค้า  "+matObj.value);
			matObj.focus();
			
			barcode[lineId-1].value = '';
			materialMaster[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
			wholePriceBF[lineId-1].value = '';
			retailPriceBF[lineId-1].value = '';
		}else{
			var s = returnString.split("|");
			
			barcode[lineId-1].value = s[0];
			materialMaster[lineId-1].value = s[1];
			groupCode[lineId-1].value = s[2];
			pensItem[lineId-1].value = s[3];
			wholePriceBF[lineId-1].value = s[4];
			retailPriceBF[lineId-1].value = s[5];
			
			found = true;
		}
	return found;
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
				<jsp:param name="function" value="mtt"/>
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
						<html:form action="/jsp/mttAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						     <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Sale Date<font color="red">*</font></td>
									<td>					
									   <html:text property="bean.saleDate" styleId="saleDate" size="20"/>
									   Doc No
									    <html:text property="bean.docNo" styleId="docNo" size="20" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า <font color="red">*</font></td>
									<td>		
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
								</tr>
								<tr>
									<td >รหัสร้านค้า<font color="red">*</font>
									</td>
									<td align="left"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									
									  <html:hidden property="bean.subInv" styleId="subInv"/>
									  <html:hidden property="bean.storeNo" styleId="storeNo"/>
									</td>
								</tr>
								<tr>
                                    <td> หมายเหตุ<font color="red"></font></td>
									<td>		
										  <html:text property="bean.remark" styleId="remark" size="40"/>
									</td>
								</tr>
						   </table>
						   
				 <!-- Table Data -->
				<c:if test="${mttForm.results != null}">
	                     <!-- <div align="left">
							<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow();"/>	
							<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow();"/>	
					    </div> -->
				       	
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >Barcode</th>
								<th >Wacoal Mat.</th>
								<th >Group Code</th>
								<th >PENS Item</th>
								<!-- <th >ราคาขายส่งก่อน VAT</th> -->
								<th >ราคาขายปลีกก่อน VAT</th>	
								<th >Status</th>						
							</tr>
							<c:forEach var="results" items="${mttForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<td class="td_text_center" nowrap width="10%">
										  <input type="checkbox" name="linechk" value="${results.lineId}"/>
										  <input type="hidden" name="lineId" value="${results.lineId}" />
										</td>
										<td class="td_text_center" width="10%">
										    <input type="text" name="barcode" id="barcode" value ="${results.barcode}" size="30" 
											    onkeypress="getProductKeypress(event,this,${results.lineId})"
											    readonly="${results.barcodeReadonly}" class="${results.barcodeStyle}" 
											   <%--  onchange="getProductModel(this,${results.lineId})" --%>
											    />
                                        </td>
										<td class="td_text_center" width="10%">
											<input  onkeypress="getProductKeypressByMat(event,this,${results.lineId})" type="text" name="materialMaster" value ="${results.materialMaster}" size="25"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="groupCode" value ="${results.groupCode}" size="30" readonly class="disableText"/>
										</td>
										<td class="td_text_center" width="10%">
										   <input type="text" name="pensItem" value ="${results.pensItem}" size="20" readonly class="disableText"/>
										</td>
										<%-- <td class="data_wholePriceBF">
										   <input type="text" name="wholePriceBF" value ="${results.wholePriceBF}" size="20" readonly class="disableNumber"/>
										</td> --%>
										<td class="td_text_center" width="10%">
										  
										   <input type="text" name="retailPriceBF" value ="${results.retailPriceBF}" size="20" readonly class="disableNumber"/>
										   <input type="hidden" name="wholePriceBF" value ="${results.wholePriceBF}" size="20" readonly class="disableNumber"/>
										</td>
										<td class="td_text_center" width="10%">
										<input type="text" name="statusDesc" id="statusDesc" value ="${results.statusDesc}" size="20" readonly class="disableText"/>
										<input type="hidden" name="status" id="status" value ="${results.status}" size="20" readonly class="disableText"/>
										</td>
									</tr>
							
							  </c:forEach>
					</table>
						 <div align="left">
							<font size="3"> <b>สรุปยอดจำนวนตัว <input type="text" name ="totalRow" class="disableNumber" value="" readonly/></b>	</font>
							 
						</div>
				</c:if>
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									     <%-- <c:if test="${mttForm.bean.canEdit == true}">
											<a href="javascript:newBox('${pageContext.request.contextPath}')">
											  <input type="button" value="   เริ่มกล่องใหม่   " class="newPosBtnLong">
											</a>
										</c:if> --%>
										
									    <c:if test="${mttForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
												  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
											 </a>
										</c:if>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>	
															
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>	
										</td>
										<td align="right">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										 <c:if test="${mttForm.bean.canCancel == true}">
										 
											<a href="javascript:removeRow('${pageContext.request.contextPath}')">
											  <input type="button" value="   ลบรายการ   " class="newPosBtnLong">
											</a>
											
											<a href="javascript:cancel('${pageContext.request.contextPath}')">
											  <input type="button" value="   ลบทั้งเอกสาร " class="newPosBtnLong">
											</a>
											
										</c:if>
									</td>
								</tr>
							</table>
					  </div>
					<!-- ************************Result ***************************************************-->
					
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