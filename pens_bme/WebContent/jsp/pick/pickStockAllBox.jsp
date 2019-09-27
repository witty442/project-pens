<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="pickStockForm" class="com.isecinc.pens.web.pick.PickStockForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "pickStockForm");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	sumTotal();
}
function clearForm(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=exportExcel";
	form.submit();
	return true;
}
function exportBarcodeToExcel(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=exportBarcodeToExcel";
	form.submit();
	return true;
}
function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		var form = document.pickStockForm;
		form.action = path + "/jsp/pickStockAction.do?do=cancelAllBox";
		form.submit();
		return true;
	}
	return false;
}
function confirmAction(path){
	var form = document.pickStockForm;
	if(confirm("ยันยันการ Confirm ข้อมูล")){
		 /**Control Save Lock Screen **/
		 startControlSaveLockScreen();
		
		 form.action = path + "/jsp/pickStockAction.do?do=confirmAllBox";
		 form.submit();
		 return true;
	}
	return false;
}
function cancelIssueAction(path){
	var form = document.pickStockForm;
	if(confirm("ยันยันการ Cancel Issue รายการนี้")){
		
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/pickStockAction.do?do=cancelIssueAllBoxAction";
		form.submit();
		return true;
	}
	return false;
}
function search(path){
	var form = document.pickStockForm;
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
	
	form.action = path + "/jsp/pickStockAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function save(path){
	var form = document.pickStockForm;
	var pickUser =$('#pickUser').val();
	var storeCode =$('#storeName').val();
	var storeNo =$('#storeNo').val();
	var subInv =$('#subInv').val();
	var custGroup =$('#custGroup').val();
	
	if(custGroup ==""){
		alert("กรุณากรอก กลุ่มร้านค้า");
		return false;
	}
	
	if(pickUser ==""){
		alert("กรุณากรอก ผู้เบิก");
		return false;
	}
	if(storeCode ==""){
		alert("กรุณากรอก รหัสร้านค้า");
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
	if( !checkOneSelected()){
		alert("กรุณาเลือกรายการอย่างน้อย 1 รายการ");
		return false;
	}
	
	if(confirm("ยันยันการบันทึกข้อมูล")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
	   form.action = path + "/jsp/pickStockAction.do?do=saveBox";
	   form.submit();
	   return true;
	}
	return false;
}

function checkAll(chkObj){
	var chk = document.getElementsByName("linechk");
	for(var i=0;i<chk.length;i++){
		chk[i].checked = chkObj.checked;
	}
	
	sumTotal();
}
function checkOneSelected(){
	var chk = document.getElementsByName("linechk");
	var chkOne = false;
	for(var i=0;i<chk.length;i++){
		 if(chk[i].checked){
			 chkOne = true;
			 break;
		 }
	}
	return chkOne;
}

function sumTotal(chkObj){
	var chk = document.getElementsByName("linechk");
	var lineId = document.getElementsByName("lineId");
	var qty = document.getElementsByName("qty");
	
	var totalBoxTemp =0;
	var totalQtyTemp =0;
	for(var i=0;i<chk.length;i++){
		 if(chk[i].checked){
			 totalBoxTemp = totalBoxTemp+1;
			 totalQtyTemp += convetTxtObjToFloat(qty[i]);
			 
			 lineId[i].value = ""+(i+1);
		 } else{
			 lineId[i].value = "";
		 }
	}
	//alert(totalBoxTemp+","+totalQtyTemp);
	
	 $('#totalBox').val(totalBoxTemp);
	 $('#totalQty').val(totalQtyTemp);
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

function openPopupCustomer(path,types,storeType){
	var form = document.pickStockForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.pickStockForm;
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
	var form = document.pickStockForm;
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
	var form = document.pickStockForm;
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
	var form = document.pickStockForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}
}

function printStampBoxNoReport(path){
	var form = document.pickStockForm;
	form.action = path + "/jsp/pickStockAction.do?do=printStampBoxNoReport";
	form.submit();
	return true;
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
				<jsp:param name="function" value="pickStock"/>
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
						<html:form action="/jsp/pickStockAction">
						<jsp:include page="../error.jsp"/>

						    <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                    <td colspan="3" align="center"><font size="3"><b>เบิกทั้งกล่อง</b></font></td>
								</tr>
						       <tr>
                                    <td>Issue request Date</td>
                                     <td>
                                       <html:text property="bean.issueReqDate" styleId="issueReqDate" size="20"  readonly="true" styleClass="disableText"/>
                                     </td>
									<td align="right">Issue request No </td>
									<td align="left">
									 <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20"  readonly="true" styleClass="disableText"/>	  
									</td>
								</tr>
								 <tr>
                                    <td> Issue request status</td>
                                      <td>
                                        <html:text property="bean.issueReqStatusDesc" styleId="issueReqStatusDesc" size="20" readonly="true" styleClass="disableText"/>
                                     </td>
									<td align="right">	 ผู้เบิก  </td>
									<td align="left">
									  <html:text property="bean.pickUser" styleId="pickUser" size="20" /><font color="red">*</font>	  
									</td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า  <font color="red">*</font></td>		
								    <td>
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
						           </td>
								<td></td><td></td>
								</tr>
								<tr>
									<td >รหัสร้านค้า<font color="red">*</font>
									</td>
									<td align="left" colspan="2"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									</td>
								</tr>
								<tr>
                                    <td> Sub Inventory</td>
									<td colspan="2">
						               <html:text property="bean.subInv" styleId="subInv" size="10" readonly="true" styleClass="disableText"/>
						               Store No <html:text property="bean.storeNo" styleId="storeNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								
								<tr>
                                    <td > Confrim Issue Date</td>
                                    <td>
						               <html:text property="bean.confirmIssueDate" styleId="confirmIssueDate" size="20" readonly="true" styleClass="disableText"/>
									</td>
									<td></td><td></td>
								</tr>
								<tr>
                                    <td > Invoice No</td>
                                    <td>
						               <html:text property="bean.invoiceNo" styleId="invoiceNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
									<td></td><td></td>
								</tr>
								<tr>
                                    <td > หมายเหตุ </td>
                                    <td colspan="2"> 
                                      <html:text property="bean.remark" styleId="remark" size="60" />
                                      </td>
								</tr>	
						   </table>
					    </div>
					  
                     <!-- Table Content -->					
						<c:if test="${pickStockForm.results != null}">
				
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width=60%>
								    <tr>
									<!-- 	<th >No</th> -->
										<th ><!-- <input type="checkbox" name="chkAll" onclick="checkAll(this)"/> --></th>
										<th >เลขที่กล่อง</th>
										<th >Qty</th>
										<th >รับคืนจาก</th>
													
									</tr>
									<c:forEach var="results" items="${pickStockForm.results}" varStatus="rows">
										<c:choose>
											<c:when test="${rows.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										
											<tr class="<c:out value='${tabclass}'/>">
												
												<td class="td_text_center" width="10%">
													<c:choose>
														<c:when test="${results.selected == 'true'}">
															 <input type="checkbox" name="linechk"  onclick="sumTotal()" checked/>		
														</c:when>
														<c:otherwise>
															 <input type="checkbox" name="linechk"  onclick="sumTotal()"/>		
														</c:otherwise>
													</c:choose>
										
												 							  
												  <input type="hidden" name="lineId" value="${results.lineId}" />
										
												</td>
												<td class="td_text_center" width="10%">${results.boxNo}
													<input type="hidden" name="boxNo" value ="${results.boxNo}" size="40" readonly class="disableText"/>
													
												</td>
												<td class="td_text_center" width="10%">${results.qty}
												   <input type="hidden" name="qty" value ="${results.qty}" size="20" readonly class="disableText"/>
												</td>
												<td class="td_text" width="20%">${results.jobId}&nbsp;${results.jobName}
												   <input type="hidden" name="jobId" value ="${results.jobId}" size="20" readonly class="disableText"/>
												</td>
											</tr>
									  </c:forEach>
							</table>
							 <div align="left">
							    <font size="2">
								 รวมจำนวนกล่อง    &nbsp; :&nbsp;<input type="text" size="10" id ="totalBox" name ="bean.totalBox" class="disableNumber" value="" readonly/> กล่อง
							  </font>
							</div>
							 <div align="left">
							   <font size="2">
								 รวมจำนวนที่จะคืน : <input type="text" size="10" id ="totalQty" name ="bean.totalQty" class="disableNumber" value="" readonly/> ชิ้น
							  </font>
							</div>
						</c:if>
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="left">
					 
					                      <c:if test="${pickStockForm.bean.issueReqStatus == 'I'}">
											<%--  <a href="javascript:cancelIssueAction('${pageContext.request.contextPath}')"> --%>
											   <input type="button" value="    Cancel Issue     " class="disablePosBtnLong" disabled> 
											 <!-- </a> -->  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										   </c:if>	
										 
									      <c:if test="${pickStockForm.bean.canConfirm == true}">
											<a href="javascript:confirmAction('${pageContext.request.contextPath}')">
											  <input type="button" value="ยืนยัน issue" class="newPosBtnLong"> 
											 </a>
										 </c:if>	
									
										<c:if test="${pickStockForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
												<input type="button" value="บันทึก request issue" class="newPosBtnLong"> 
											</a>
										</c:if>
										 <c:if test="${pickStockForm.bean.canCancel == true}">
											<%--  <a href="javascript:cancel('${pageContext.request.contextPath}')"> --%>
											   <input type="button" value="    ยกเลิก     " disabled class="disablePosBtnLong"> 
											<!--  </a>   -->
										 </c:if>
										   <a href="javascript:printStampBoxNoReport('${pageContext.request.contextPath}')">
											  <input type="button" value="พิมพ์ไปปะหน้ากล่อง" class="newPosBtnLong">
											</a> 
											
										  <c:if test="${pickStockForm.bean.issueReqStatus == 'I'}">
											 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="    Export     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>		
										  <c:if test="${pickStockForm.bean.issueReqStatus == 'O'}">
											 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="    Export     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>		 
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>	
											&nbsp;
											<c:if test="${pickStockForm.bean.issueReqStatus == 'I'}">
											   <a href="javascript:exportBarcodeToExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="Export Barcode To Excel" class="newPosBtnLong"> 
											   </a>  
										 </c:if>		
										  <c:if test="${pickStockForm.bean.issueReqStatus == 'O'}">
											 <a href="javascript:exportBarcodeToExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="Export Barcode To Excel" class="newPosBtnLong"> 
											 </a> 
										 </c:if>							
										</td>
									</tr>
						</table>
					</div>
		
					<!-- ************************Result ***************************************************-->
					
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