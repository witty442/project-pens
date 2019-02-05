<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>
<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="barcodeForm" class="com.isecinc.pens.web.pick.BarcodeForm" scope="session" />
<%
 String mode = barcodeForm.getMode();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('openDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('closeDate'));
	 
	 <%if( !mode.equals("view")){ %>
	    addRow();
	 <%}%>
	 calcTotalRow();
}
function clearForm(path){
	var form = document.barcodeForm;
	form.action = path + "/jsp/barcodeAction.do?do=clear";
	form.submit();
	return true;
}
function newBox(path){
	var form = document.barcodeForm;
	form.action = path + "/jsp/barcodeAction.do?do=newBox";
	form.submit();
	return true;
}
function printReport(path){
	var form = document.barcodeForm;
	form.action = path + "/jsp/barcodeAction.do?do=printReport";
	form.submit();
	return true;
}
function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		var form = document.barcodeForm;
		form.action = path + "/jsp/barcodeAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}
function back(path){
	var form = document.barcodeForm;
	form.action = path + "/jsp/barcodeAction.do?do=search2&action=back";
	form.submit();
	return true;
}

var countSave = 0;
function save(path){
	//alert(countSave);
	/** Check Save duplicate */
    if(countSave > 0){
    	return false;
    }
    countSave++;
	var form = document.barcodeForm;
	var jobId =$('#jobId').val();
	var storeCode =$('#storeName').val();
	var storeNo =$('#storeNo').val();
	var subInv =$('#subInv').val();
	
	if(jobId ==""){
		alert("กรุณากรอก เลือก  รับคืนจาก");
		return false;
	}
	//validate item
	var barcode = document.getElementsByName("barcode");
	if(barcode[0].value == ""){
		alert("กรุณากรอก ใส่รายการ อย่างน้อย 1 รายการ");
		return false;
	}
	if(storeCode ==""){
		alert("ไม่พบข้อมูล รหัสร้านค้า ไม่สามารถทำงานต่อได้  ");
		return false;
	}
	if(subInv ==""){
		alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
		return false;
	}
	if(storeNo ==""){
		alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
		return false;
	}
	/**Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	form.action = path + "/jsp/barcodeAction.do?do=save";
	form.submit();
	return true;
}
function saveGrNo(path){
	var form = document.barcodeForm;
	var grNo =$('#grNo').val();
	
	if(grNo ==""){
		alert("กรุณากรอก GR No");
		return false;
	}
	form.action = path + "/jsp/barcodeAction.do?do=saveGrNo";
	form.submit();
	return true;
}

function openJobPopup(path){
    var param = "&status=<%=PickConstants.STATUS_OPEN%>";
	url = path + "/jsp/searchJobPopupAction.do?do=prepare3&action=new"+param;
	//window.open(encodeURI(url),"",
	//		   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	PopupCenterFullHeight(url,"",500);
}

function setStoreMainValue(code,desc,storeCode,storeName,storeNo,subInv,wareHouse,wareHouseDesc){
	//alert(types+":"+desc);
	var form = document.barcodeForm;
	
	if(storeNo=='' || subInv==''){
	    if(storeNo==''){
			alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
		}
		if(subInv==''){
			alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
		} 
		form.jobId.value = "";	
		form.name.value = "";	
		form.storeCode.value = '';
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
		form.wareHouse.value = "";
		form.wareHouseDesc.value = "";
	}else{
		document.getElementsByName("job.jobId")[0].value = code;
		document.getElementsByName("job.name")[0].value = desc;
		
		document.getElementsByName("job.storeCode")[0].value = storeCode;
		document.getElementsByName("job.storeName")[0].value = storeName;
		document.getElementsByName("job.storeNo")[0].value = storeNo;
		document.getElementsByName("job.subInv")[0].value = subInv;
		document.getElementsByName("job.wareHouse")[0].value = wareHouse;
		document.getElementsByName("job.wareHouseDesc")[0].value = wareHouseDesc;
	}
}

function getJobNameKeypress(e,code){
	var form = document.barcodeForm;
	if(e != null && e.keyCode == 13){
		if(code.value ==''){
			form.name.value = '';
		}else{
			getJobNameModel(code);
		}
	}
}

//Return String :jobName|StoreCode|StioreName|StoreNo|subInv
function getJobNameModel(code){
	var returnString = "";
	var form = document.barcodeForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoJobWithStoreDetail.jsp",
			data : "code=" + code.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	if(returnString !=''){
		var retArr = returnString.split("|");
		form.storeName.value = retArr[0];
		
		if(retArr[3]=='' || retArr[4]==''){
		    if(retArr[3]==''){
				alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
			}
			if(retArr[4]==''){
				alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
			} 
			form.jobId.value = "";	
			form.name.value = "";	
			form.storeCode.value = '';
			form.storeName.value = "";
			form.storeNo.value = "";
			form.subInv.value = "";
			form.wareHouse.value = "";
			form.wareHouseDesc.value = "";
		}else{
			
			form.name.value = retArr[0];	
			form.storeCode.value = retArr[1];
			form.storeName.value = retArr[2];
			form.storeNo.value = retArr[3];
			form.subInv.value = retArr[4];
			form.wareHouse.value = retArr[5];
			form.wareHouseDesc.value = retArr[6];
		}
		
	}else{
		alert("ไม่พบข้อมูล");
		
		form.jobId.value = "";	
		form.name.value = "";
		form.storeCode.value ="";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
		form.wareHouse.value = "";
		form.wareHouseDesc.value = "";
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
	    "<td class='td_text_center' width='10%'> <input type='checkbox' tabindex ='-1' name='linechk' value='0'/></td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' name='barcode' size='30'  "+
	    " onkeypress='getProductKeypress(event,this,"+lineId+")' "+
	
	    " />  </td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='materialMaster' size='25' onkeypress='getProductKeypressByMat(event,this,"+lineId+")'/></td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='groupCode' readonly class='disableText' size='30' /></td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='pensItem' readonly class='disableText' size='15' /></td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='wholePriceBF' readonly class='disableNumber' size='20' /></td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='retailPriceBF' readonly class='disableNumber' size='20'/></td>"+
	    
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
function removeRow(){
	//todo play with type
	var tbl = document.getElementById('tblProduct');
	var chk = document.getElementsByName("linechk");
	var drow;
	var bcheck=false;
	for(var i=chk.length-1;i>=0;i--){
		if(chk[i].checked){
			// alert(i);
			drow = tbl.rows[i+1];
			$(drow).remove();
			bcheck=true;
		}
	}
	if(!bcheck){alert('เลือกข้อมูลอย่างน้อย 1 รายการ');return false;}
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
	var form = document.barcodeForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcode.jsp",
			data : "itemCode=" + barcodeObj.value+"&storeCode="+form.storeCode.value,
			async: false,
			cache: false,
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
	var form = document.barcodeForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcode.jsp",
			data : "matCode="+matObj.value+"&storeCode="+form.storeCode.value,
			async: false,
			cache: false,
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
				<jsp:param name="function" value="scanBarcode"/>
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
						<html:form action="/jsp/barcodeAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						       <tr>
                                    <td> Transaction Date</td>
									<td>					
										 <html:text property="job.transactionDate" styleId="transactionDate" size="20" readonly="true" styleClass="disableText"/>
									</td>
									<td> 
									           เลขที่กล่อง <html:text property="job.boxNo" styleId="boxNo" size="20" readonly="true" styleClass="disableText"/>
									           สถานะ  <html:text property="job.statusDesc" styleId="statusDesc" size="20" readonly="true" styleClass="disableText"/>
									</td>
									<td>					
										 
									</td>
								</tr>
								<tr>
                                    <td>รับคืนจาก </td>
                                    <c:choose>
                                      <c:when test="${barcodeForm.mode == 'add'}">
									     <td >
								             <html:text property="job.jobId" styleId="jobId" size="20" 
						                               onkeypress="getJobNameKeypress(event,this)"  styleClass="\" autoComplete=\"off"/> 
						                      <font color="red">*</font>
						                     <input type="button" name="x1" value="..." onclick="openJobPopup('${pageContext.request.contextPath}')"/>
										 </td>
									     <td colspan="1" align="left">
										        <html:text property="job.name" styleId="name" size="30" readonly="true" styleClass="disableText"/>
										            บันทึกเข้าคลัง
						                     <html:text property="job.wareHouse" styleId="wareHouse" size="10" readonly="true" styleClass="disableText"/>
						                      <html:text property="job.wareHouseDesc" styleId="wareHouseDesc" size="30" readonly="true" styleClass="disableText"/>
										 </td>
									    </c:when>
										<c:otherwise>    
											<td >
								               <html:text property="job.jobId" styleId="jobId" size="20" readonly="true" styleClass="disableText"/>
											</td>
										    <td colspan="1">
										        <html:text property="job.name" styleId="name" size="30" readonly="true" styleClass="disableText"/>
										         บันทึกเข้าคลัง
						                     <html:text property="job.wareHouse" styleId="wareHouse" size="10" readonly="true" styleClass="disableText"/>
						                      <html:text property="job.wareHouseDesc" styleId="wareHouseDesc" size="30" readonly="true" styleClass="disableText"/>
										    </td>
								        </c:otherwise>
								      </c:choose>
								</tr>	
								<tr>
                                    <td> 
                                                                                                         รหัสร้านค้า    
                                    </td>
									<td colspan="3">
									   <html:text property="job.storeCode" styleId="storeCode" size="20" readonly="true" styleClass="disableText"/>
									   &nbsp;&nbsp;&nbsp;&nbsp;<html:text property="job.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="20"/>
									   Sub Inventory
						               <html:text property="job.subInv" styleId="subInv" size="10" readonly="true" styleClass="disableText"/>
						               Store No
						               <html:text property="job.storeNo" styleId="storeNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								<tr>
                                    <td> หมายเหตุ</td>
									<td colspan="3">
									  <c:if test="${barcodeForm.job.canEdit == true}">  
						                  <html:text property="job.remark" styleId="remark" size="90"  styleClass="\" autoComplete=\"off"/>
						              </c:if>
						               <c:if test="${barcodeForm.job.canEdit == false}">  
						                  <html:text property="job.remark" styleId="remark" size="90" readonly="true" styleClass="disableText"/>
						              </c:if>
						              <!-- Show Only Hisher StoreCode -->
						              <%
						              if( !mode.equalsIgnoreCase("add")){
							              if(barcodeForm.getJob().getStoreCode().startsWith(PickConstants.STORE_TYPE_HISHER_CODE)){ %>
								              &nbsp; GR NO(His&Her) 
								              <c:choose>
		                                         <c:when test="${barcodeForm.job.canEditGrNo == true}">
		                                             <html:text property="job.grNo" styleId="grNo" size="20"  styleClass="\" autoComplete=\"off"/>
		                                         </c:when>
		                                         <c:otherwise>   
		                                             <html:text property="job.grNo" styleId="grNo" size="20" readonly="true" styleClass="disableText" />
		                                         </c:otherwise>
		                                      </c:choose>
                                      <%
							              }
                                      }else{
                                      %>
                                         &nbsp; GR NO(His&Her) 
                                         <html:text property="job.grNo" styleId="grNo" size="20"  styleClass="\" autoComplete=\"off"/>
                                      <%} %>
									</td>
								</tr>	
								
						   </table>
						   
				 <!-- Table Data -->
				<c:if test="${barcodeForm.results != null}">
                  	    <c:if test="${barcodeForm.job.canEdit == true}">
	                        <div align="left">
								<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow();"/>	
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow();"/>	
							</div>
				        </c:if> 
				         		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >Barcode</th>
								<th >Wacoal Mat.</th>
								<th >Group Code</th>
								<th >PENS Item</th>
								<th >ราคาขายส่งก่อน VAT</th>
								<th >ราคาขายปลีกก่อน VAT</th>	
								<th >Status</th>						
							</tr>
							<c:forEach var="results" items="${barcodeForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										<%-- <td class="data_no">
										   <input type="text" name="lineId" value ="${results.lineId}" size="5" readonly class="disableText"/>
										</td> --%>
										<td  class='td_text_center' width='10%'>
										  <input type="checkbox" name="linechk" value="${results.lineId}"/>
										</td>
										<td class='td_text_center' width='10%'>
										    <input type="text" name="barcode" id="barcode" value ="${results.barcode}" size="30" 
											    onkeypress="getProductKeypress(event,this,${results.lineId})"
											    readonly="${results.barcodeReadonly}" class="${results.barcodeStyle}" 
											   <%--  onchange="getProductModel(this,${results.lineId})" --%>
											    />
                                        </td>
										<td class='td_text_center' width='10%'>
											<input  onkeypress="getProductKeypressByMat(event,this,${results.lineId})" type="text" name="materialMaster" value ="${results.materialMaster}" size="25"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="groupCode" value ="${results.groupCode}" size="30" readonly class="disableText"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="pensItem" value ="${results.pensItem}" size="15" readonly class="disableText"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="wholePriceBF" value ="${results.wholePriceBF}" size="20" readonly class="disableNumber"/>
										</td>
										<td class='td_text_center' width='10%'>
										   <input type="text" name="retailPriceBF" value ="${results.retailPriceBF}" size="20" readonly class="disableNumber"/>
										</td>
										<td class='td_text_center' width='10%'>${results.statusDesc}</td>
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
									     <c:if test="${barcodeForm.job.canEdit == true}">
											<a href="javascript:newBox('${pageContext.request.contextPath}')">
											  <input type="button" value="   เริ่มกล่องใหม่   " class="newPosBtnLong">
											</a>
										</c:if>
										
										 <c:if test="${barcodeForm.job.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
											 </a>
										 </c:if>
										 <c:if test="${barcodeForm.job.canEdit == false}">
											 <c:if test="${barcodeForm.job.canEditGrNo == true}">
												<a href="javascript:saveGrNo('${pageContext.request.contextPath}')">
												  <input type="button" value="  บันทึก GR NO  " class="newPosBtnLong"> 
												 </a>
											 </c:if>
										 </c:if>
										 <c:if test="${barcodeForm.job.status != 'AB'}"> <!-- AB = CANCEL -->
											<a href="javascript:printReport('${pageContext.request.contextPath}')">
											  <input type="button" value="พิมพ์ไปปะหน้ากล่อง" class="newPosBtnLong">
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
										 <c:if test="${barcodeForm.job.canCancel == true}">
											<a href="javascript:cancel('${pageContext.request.contextPath}')">
											  <input type="button" value="   ยกเลิก   " class="newPosBtnLong">
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
<!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->