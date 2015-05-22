<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="mttForm" class="com.isecinc.pens.web.mtt.MTTForm"  scope="session" />

<%
 String mode = mttForm.getMode();
 int totalRowPerPage = 1;
System.out.println("mode:"+mode);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>Scan Barcode</title>
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
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('docDate'));
	 
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
function exportExcel(path){
	var form = document.mttForm;
	form.action = path + "/jsp/mttAction.do?do=exportExcel";
	form.submit();
	return true;
}

function closeJob(path){
	var form = document.mttForm;
	if(confirm("คุณต้องยืนยันว่า ต้องการเสร็จงานนี้แล้ว")){
		form.action = path + "/jsp/mttAction.do?do=closeJob";
		form.submit();
		return true;
	}
	return false;
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
	var saleDate =$('#docDate').val();
	var custGroup =$('#custGroup').val();

	if(custGroup =="" ){
		alert("กรุณากรอกข้อมูลกลุ่มร้านค้า ");
		return false;
	}
	if(saleDate =="" ){
		alert("กรุณากรอก วันที่");
		return false;
	}
	if(storeCode =="" ){
		alert("กรุณากรอก รหัสร้านค้า ");
		return false;
	}
	
	//validate item
	var barcode = document.getElementsByName("barcode");
	if(barcode[0].value == ""){
		alert("กรุณากรอก ใส่รายการ อย่างน้อย 1 รายการ");
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
	    "<td class='td_text_center' width='5%'> <input type='checkbox' tabindex ='-1' name='linechk' value='0'/>"+
	   // "  <input type='hidden' tabindex ='-1' name='lineId' />"+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> "+
	    "  <input type='text' name='barcode' size='25' onkeypress='getProductKeypress(event,this,"+lineId+")' autocomplete='off' />"+
	    "</td>"+
	    "<td class='td_text_center' width='10%'> <input type='text' tabindex ='-1' name='materialMaster' class='normalTextCenter'  size='20' onkeypress='getProductKeypressByMat(event,this,"+lineId+")'/></td>"+
	    "<td class='td_text_center' width='5%'> <input type='text' tabindex ='-1' name='groupCode' readonly class='disableTextCenter' size='15' /></td>"+
	    "<td class='td_text_center' width='5%'> <input type='text' tabindex ='-1' name='pensItem' readonly class='disableTextCenter' size='15' /> </td>"+
	    "</tr>";
   
    $('#tblProduct tr:last').after(rowData);
    
    //set focus default
    var barcode = document.getElementsByName("barcode");
    barcode[lineId-1].focus();
    
    //clear garbage collection
    rowData.length = 0;
 
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
    
    return rows;
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
		
		calcTotalRow();
	}
	return false;
}
	
function removeRowUpdate(path,drow,index){
	//todo play with type	
	drow.style.display ='none';
	
	document.getElementsByName("lineId")[index].value ='-1';
	
	//update delete line db
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

function getProductKeypressOnTop(e,barcodeObj){
	var rows = $('#tblProduct tr').length-1;
	//alert(rows);
	var lineId = rows;
	
	getProductKeypress(e,barcodeObj,lineId);
	
}

function getProductKeypress(e,barcodeObj,lineId){
	//materialMaster groupCode pensItem wholePriceBF retailPriceBF
	//alert(barcode.value);
	var totalRowPerPage = <%=totalRowPerPage%>;
	
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	var totalRows = 0;
	
	//alert("keyCode:"+e.keyCode);
	if(e != null && e.keyCode == 13){
		if(barcodeObj.value ==''){
			materialMaster[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
		
		}else{
			 
			var found = getProductModel(barcodeObj,lineId);

			if(found ){
				totalRows = calcTotalRow();
				
				//Set Prev row readonly 
				barcode[lineId-1].className ="disableText";
				barcode[lineId-1].readOnly = true;

			} 
		}
	}
	
	//clear garbage collection
	barcode.length=0;
	materialMaster.length=0;
	groupCode.length=0;
	pensItem.length=0;
}

function getProductModel(barcodeObj,lineId){
	var found = false;
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	
	var returnString = "";
	var form = document.mttForm;
	 $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcode.jsp",
			data : "itemCode=" + barcodeObj.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			  
			  if(returnString==''){
					alert("ไม่พบข้อมูลสินค้า  "+barcodeObj.value);
					barcodeObj.focus();
					
					barcode[lineId-1].value = '';
					materialMaster[lineId-1].value = '';
					groupCode[lineId-1].value = '';
					pensItem[lineId-1].value = '';
					
				}else{
					var s = returnString.split("|");
					
					//barcode[lineId-1].value = s[0];
					materialMaster[lineId-1].value = s[1];
					groupCode[lineId-1].value = s[2];
					pensItem[lineId-1].value = s[3];
					
					found = true;
					barcode[lineId-1].readonly = true;
					barcode[lineId-1].className ="disableText";
					
					addRow();
		            barcode[lineId].focus();
				}
			}
		}).responseText;
	 
	//clear garbage collection
	barcode.length=0;
	materialMaster.length=0;
	groupCode.length=0;
	pensItem.length=0;
	returnString.length =0;
	
	return found;
}

function getProductKeypressByMat(e,matObj,lineId){
	//materialMaster groupCode pensItem wholePriceBF retailPriceBF
	//alert(barcode.value);
	
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	
	if(e != null && e.keyCode == 13){
	
		if(matObj.value ==''){
			
			barcode[lineId-1].value = '';
			groupCode[lineId-1].value = '';
			pensItem[lineId-1].value = '';
		
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
	//clear garbage collection
	barcode.length=0;
	materialMaster.length=0;
	groupCode.length=0;
	pensItem.length=0;

}

function getProductModelByMat(matObj,lineId){
	var found = false;
	var barcode = document.getElementsByName("barcode");
	var materialMaster = document.getElementsByName("materialMaster");
	var groupCode = document.getElementsByName("groupCode");
	var pensItem = document.getElementsByName("pensItem");
	
	var returnString = "";
	var form = document.mttForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoBarcode.jsp",
			data : "matCode="+matObj.value,
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
			
		}else{
			var s = returnString.split("|");
			
			barcode[lineId-1].value = s[0];
			materialMaster[lineId-1].value = s[1];
			groupCode[lineId-1].value = s[2];
			pensItem[lineId-1].value = s[3];
			
			found = true;
		}
		
	//clear garbage collection
	barcode.length=0;
	materialMaster.length=0;
	groupCode.length=0;
	pensItem.length=0;
	returnString =null;
	
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
				<jsp:param name="function" value="Scan"/>
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
                                    <td> Doc Date<font color="red">*</font></td>
									<td>					
									   <html:text property="bean.docDate" styleId="docDate" size="20"/>
									   Doc No
									    <html:text property="bean.docNo" styleId="docNo" size="40" styleClass="disableText"/>
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
						   </table>
						   
				 <!-- Table Data -->
				
					   <%if( !mode.equals("view")){ %>
		                      <div align="left">
								<input type="button" class="newPosBtn" value="เพิ่มรายการ" onclick="addRow();"/>	
								<input type="button" class="newPosBtn" value="ลบรายการ" onclick="removeRow('${pageContext.request.contextPath}');"/>	
						    </div>
				       	<%} %>
												  
						<table id="tblProduct" align="center" border="0" cellpadding="1" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >Barcode</th>
								<th >Wacoal Mat.</th>
								<th >Group Code</th>
								<th >PENS Item</th>
												
							</tr>
							<%if(mttForm.getResults() != null && mttForm.getResults().size() > 0 ){ %>
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
											<td class="td_text_center"  width="5%" nowrap>
											  <input type="checkbox" name="linechk" value="${results.lineId}"/>
											</td>
											
											<td class="td_text_center"  width="10%">
											    <input type="text" name="barcode" id="barcode" value ="${results.barcode}" size="25" 
												    onkeydown="getProductKeypress(event,this,${results.lineId})"
												    readonly="${results.barcodeReadonly}" class="${results.barcodeStyle}" 
												   <%--  onchange="getProductModel(this,${results.lineId})" --%>
												    />
	                                        </td>
											<td class="td_text_center"  width="10%">
												<input  onkeydown="getProductKeypressByMat(event,this,${results.lineId})" type="text" 
	                                                    name="materialMaster" value ="${results.materialMaster}" size="20"
	                                                    readonly="${results.barcodeReadonly}" class="${results.barcodeStyle}" />
											</td>
											<td class="td_text_center"  width="10%">
											   <input type="text" name="groupCode" value ="${results.groupCode}" size="15" readonly class="disableTextCenter"/>
											</td>
											<td class="td_text_center"  width="10%">                                                     
											   <input type="text" name="pensItem" value ="${results.pensItem}" size="15" readonly class="disableTextCenter"/>
											</td>
										</tr>
								
								  </c:forEach>
						    <% } %> 
				 
					</table>
						 <div align="left">
							<font size="2"> <b>สรุปยอดจำนวนตัว <input type="text" name ="totalRow" class="disableTextCenter" value="" readonly size="20"/></b>	</font>
							 
						</div>
						<!-- <textarea rows="50" cols="20"></textarea> -->
			
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
									  
									    <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
										   <input type="button" value=" ExportToExcel " class="newPosBtnLong"> 
									     </a>
										
										
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
										<c:if test="${mttForm.bean.canClose == true}"> 
											<a href="javascript:closeJob('${pageContext.request.contextPath}')">
											  <input type="button" value="   ปิดงาน   " class="newPosBtnLong">
											</a>	
										</c:if>
										
										 <%--<c:if test="${mttForm.bean.canCancel == true}"> 
											 <a href="javascript:removeRow('${pageContext.request.contextPath}')">
											  <input type="button" value="   ลบรายการ   " class="newPosBtnLong">
											</a>
											
											<a href="javascript:cancel('${pageContext.request.contextPath}')">
											  <input type="button" value="   ลบทั้งเอกสาร " class="newPosBtnLong">
											</a> 
											
										</c:if>--%>
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