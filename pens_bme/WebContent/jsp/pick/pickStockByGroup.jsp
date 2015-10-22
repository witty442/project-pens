<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
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
<jsp:useBean id="pickStockGroupForm" class="com.isecinc.pens.web.pick.PickStockForm" scope="session" />
<%
String pageName = pickStockGroupForm.getBean().getPage();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />

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
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	sumTotal();
	 <%if(pickStockGroupForm.getBean().isCanConfirm() ){%>
	  sumTotalIssue();
	 <%}%>
}

function search(path){
	var form = document.pickStockGroupForm;
    var groupCodeFrom =$('#groupCodeFrom').val();
    var groupCodeTo =$('#groupCodeTo').val();
    
	if(groupCodeFrom ==""){
		alert("กรุณากรอก group Code From ");
		return false;
	}
	if(groupCodeTo ==""){
		alert("กรุณากรอก group Code To ");
		return false;
	}
	
	form.action = path + "/jsp/pickStockGroupAction.do?do=search";
	form.submit();
	return true;
}

function clear(path){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.pickStockGroupForm;
	form.action = path + "/jsp/pickStockGroupAction.do?do=exportExcel";
	form.submit();
	return true;
}

function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		var form = document.pickStockGroupForm;
		form.action = path + "/jsp/pickStockGroupAction.do?do=cancelAction";
		form.submit();
		return true;
	}
	return false;
}

function confirmAction(path){
	var form = document.pickStockGroupForm;
	if(confirm("ยันยันการ Confirm ข้อมูล")){
		 form.action = path + "/jsp/pickStockGroupAction.do?do=confirmAction";
		 form.submit();
		 return true;
	}
	return false;
}

function completeAction(path){
	var form = document.pickStockGroupForm;
	if(confirm("ยันยันการ Complete ข้อมูล")){
		 form.action = path + "/jsp/pickStockGroupAction.do?do=completeAction";
		 form.submit();
		 return true;
	}
	return false;
}

function cancelIssueAction(path){
	var form = document.pickStockGroupForm;
	if(confirm("ยันยันการ Cancel Issue รายการนี้")){
		 form.action = path + "/jsp/pickStockGroupAction.do?do=cancelIssueAction";
		 form.submit();
		 return true;
	}
	return false;
}

function save(path){
	var form = document.pickStockGroupForm;
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
	/* if( !checkOneSelected()){
		alert("กรุณาเลือกรายการอย่างน้อย 1 รายการ");
		return false;
	}  */
	
	if(confirm("ยันยันการบันทึกข้อมูล")){
	   form.action = path + "/jsp/pickStockGroupAction.do?do=saveByGroup";
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
	var qty = document.getElementsByName("qty");
	var chkOne = false;
	for(var i=0;i<qty.length;i++){
		 if(qty[i].value != ''){
			 chkOne = true;
			 break;
		 }
	}
	return chkOne;
}

function sumTotal(){
	//var chk = document.getElementsByName("linechk");
	var qty = document.getElementsByName("qty");
	
	//var totalBoxTemp =0;
	var totalQtyTemp =0;
	for(var i=0;i<qty.length;i++){
	   if(qty[i].value != '')
		 totalQtyTemp += parseInt(qty[i].value);

	}
	//alert(totalBoxTemp+","+totalQtyTemp);
	
	// $('#totalBox').val(totalBoxTemp);
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
	var form = document.pickStockGroupForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
        param += "&methodName=pickStockGroup";
        
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValuePickStockGroup(code,desc,storeNo,subInv,types){

	var form = document.pickStockGroupForm;
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
	var form = document.pickStockGroupForm;
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
	var form = document.pickStockGroupForm;
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
	var form = document.pickStockGroupForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}
}

function openJobPopup(path){
    var param = "";
	url = path + "/jsp/searchJobPopupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc){
	//alert(types+":"+desc);
	document.getElementsByName("bean.jobId")[0].value = code;
	document.getElementsByName("bean.jobName")[0].value = desc;
	
}

function getJobNameKeypress(e,code){
	var form = document.pickStockGroupForm;
	if(e != null && e.keyCode == 13){
		if(code.value ==''){
			form.name.value = '';
		}else{
			getJobNameModel(code);
		}
	}
}
function getJobNameModel(code){
	var returnString = "";
	var form = document.pickStockGroupForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/autoJob.jsp",
			data : "code=" + code.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;

	if(returnString !=''){
		form.jobName.value = returnString
	}else{
		alert("ไม่พบข้อมูล");
		form.jobId.value = "";	
		form.jobName.value = "";
	}
	
}
function chkQtyKeypress(obj,e,row){
	//alert(obj.value);
	if(e != null && e.keyCode == 13){
		return validateQty(obj,row);
	}
}

function validateQty(obj,row){
	var onhandQtyObj = document.getElementsByName("onhandQty");
	
	//validate Onhand Qty
	var onhandQty = parseInt(onhandQtyObj[row].value);
	var currQty = parseInt(obj.value);
	var valid = isNum(obj);
	if(valid){
		if(currQty > onhandQty){
			alert("จำนวนรวม QTY("+currQty+") มีมากว่า  Onhand QTY("+onhandQty+")");
			//rows[row+1].className ="lineError";
			obj.value = "";
			obj.focus();
			return false;
		}
	    sumTotal();
	}
}

function chkIssueQtyKeypress(obj,e,row){
	//alert(obj.value);
	if(e != null && e.keyCode == 13){
		return validateIssueQty(obj,row);
	}
}

function validateIssueQty(obj,row){
	var qtyObj = document.getElementsByName("qty");
	
	//validate Onhand Qty
	var qty = parseInt(qtyObj[row].value);
	var issueQty = parseInt(obj.value);
	var valid = isNum(obj);
	if(valid){
		if(issueQty > qty){
			alert("จำนวน QTY ที่เบิกได้จริง("+issueQty+") มากว่า  QTY ที่จะเบิก("+qty+")");
			//rows[row+1].className ="lineError";
			obj.value = "";
			obj.focus();
			return false;
		}
	  sumTotalIssue();
     }
}

function sumTotalIssue(){
	var qty = document.getElementsByName("issueQty");
	var totalQtyTemp =0;
	for(var i=0;i<qty.length;i++){
	   if(qty[i].value != '')
		 totalQtyTemp += parseInt(qty[i].value);

	}
	// $('#totalBox').val(totalBoxTemp);
	 $('#totalIssueQty').val(totalQtyTemp);
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
	    
	     <%if("complete".equalsIgnoreCase(pageName)){ %>
	      	     <jsp:include page="../program.jsp">
				   <jsp:param name="function" value="pickStockGroupComplete"/>
			     </jsp:include>
			 <%}else{ %>
				 <jsp:include page="../program.jsp">
				   <jsp:param name="function" value="pickStockGroup"/>
			     </jsp:include>
			 <%} %>
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
						<html:form action="/jsp/pickStockGroupAction">
						<jsp:include page="../error.jsp"/>

						    <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                    <td colspan="3" align="center"><font size="3"><b>เบิกเป็นรุ่น</b></font></td>
								</tr>
								<%
								if( pickStockGroupForm.getBean().isCanConfirm() ==false){
								  if(  PickConstants.STATUS_OPEN.equals(pickStockGroupForm.getBean().getIssueReqStatus())
									    || "".equals(Utils.isNull(pickStockGroupForm.getBean().getIssueReqStatus())) ){
										
										%>
									<tr>
	                                    <td colspan="3"><hr> </td>
	                                </tr>
									<tr>
	                                    <td>รับคืนจาก </td>
										<td colspan="2">
							                <html:text property="bean.jobId" styleId="jobId" size="20" 
							                  onkeypress="getJobNameKeypress(event,this)"
							                  />					    
										     <input type="button" name="x1" value="..." onclick="openJobPopup('${pageContext.request.contextPath}')"/>
										    <html:text property="bean.jobName" styleId="jobName" readonly="true" styleClass="disableText" size="50"/>
										</td>
									</tr>	
									<tr>
	                                    <td>จากเลขที่กล่อง</td>
	                                     <td colspan="2">
	                                       <html:text property="bean.boxNoFrom" styleId="boxNoFrom" size="20" />
	                                                                                                                                        ถึงเลขที่กล่อง
	                                  	<html:text property="bean.boxNoTo" styleId="boxNoTo" size="20" />	  
										</td>
									</tr>
									<tr>
	                                    <td>Group Code From <font color="red">*</font></td>
	                                     <td colspan="2">
	                                       <html:text property="bean.groupCodeFrom" styleId="groupCodeFrom" size="20" />
	                                       Group Code To <font color="red">*</font>
	                                  	<html:text property="bean.groupCodeTo" styleId="groupCodeTo" size="20" />	  
										</td>
									</tr>
									<tr>
	                                    <td>เรียงตามค่าของ</td>
	                                     <td colspan="2">
	                                       <html:select property="bean.orderBy" styleId="orderBy"> 
	                                         <html:option value="groupCode">Group Code</html:option>  
	                                         <html:option value="box">Box No</html:option>  
	                                       </html:select>                                                                              	  
										</td>
									</tr>
									<tr>
	                                    <td></td>
	                                     <td colspan="2">
	                                      <a href="javascript:search('${pageContext.request.contextPath}')">
											 <input type="button" value="ค้นหาข้อมูล" class="newPosBtnLong"> 
										  </a>
										   <a href="javascript:clear('${pageContext.request.contextPath}')">
											 <input type="button" value=" Clear " class="newPosBtnLong"> 
										  </a>
										</td>
									</tr>
								<%}} %>
								<tr>
	                                <td colspan="3"><hr> </td>
	                            </tr>
						       <tr>
                                    <td>Issue request Date</td>
                                     <td colspan="2">
                                       <html:text property="bean.issueReqDate" styleId="issueReqDate" size="20"  readonly="true" styleClass="disableText"/>
                                   Issue request No 
									
									 <html:text property="bean.issueReqNo" styleId="issueReqNo" size="20"  readonly="true" styleClass="disableText"/>	  
									</td>
								</tr>
								 <tr>
                                    <td> Issue request status</td>
                                      <td colspan="2">
                                        <html:text property="bean.issueReqStatusDesc" styleId="issueReqStatusDesc" size="20" readonly="true" styleClass="disableText"/>
                                     	 ผู้เบิก  
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
                                    <td > Confirm Issue Date</td>
                                    <td>
						               <html:text property="bean.confirmIssueDate" styleId="confirmIssueDate" size="20" readonly="true" styleClass="disableText"/>
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
						<c:if test="${pickStockGroupForm.results != null}">
				
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="90%">
								    <tr>
										
										<th >เลขที่กล่อง</th>
										<th >Group Code</th>
										 <%if(   PickConstants.STATUS_OPEN.equals(pickStockGroupForm.getBean().getIssueReqStatus())
									          || "".equals(Utils.isNull(pickStockGroupForm.getBean().getIssueReqStatus())) ){%>
										    <th >Onhand Qty</th>
										 <% } %>
										 
									   <%if( PickConstants.STATUS_ISSUED.equals(pickStockGroupForm.getBean().getIssueReqStatus()) ){%>
										   <th >Qty ที่เบิกได้จริง</th>
									   <%}else{ %>
									       <th >Qty ที่จะเบิก</th>
									   <%} %>
										<c:if test="${pickStockGroupForm.bean.canConfirm == true}">
										    <th >Qty ที่เบิกได้จริง</th>
									    </c:if>
										<th >รับคืนจาก</th>
													
									</tr>
									<% 
									List<PickStock> resultList = pickStockGroupForm.getResults();
									int tabindex = 1;
									String classStyle = "";
									for(int i=0;i<resultList.size();i++){
										PickStock o = (PickStock) resultList.get(i);
										classStyle = (i%2==0)?"lineO":"lineE";
									    tabindex++;
							        %>
											<tr class="<%=classStyle%>">
												<td class="td_text_center" width="10%">
												 <input type="hidden" name="lineId" value="<%=o.getLineId() %>" />
													 <input type="text" name="boxNo" value ="<%=o.getBoxNo() %>" size="20" readonly class="disableText" tabindex="-1"/>
												
												</td>
												<td class="td_text_center" width="10%">
													 <input type="text" name="groupCode" value ="<%=o.getGroupCode() %>" size="20" readonly class="disableText" tabindex="-1"/>
													
												</td>
												<%if(   PickConstants.STATUS_OPEN.equals(pickStockGroupForm.getBean().getIssueReqStatus())
									                 || "".equals(Utils.isNull(pickStockGroupForm.getBean().getIssueReqStatus())) ){%>
													<td class="data2_qty" align="center"  width="10%">
													  <input type="text" name="onhandQty" value ="<%=o.getOnhandQty() %>" size="20" readonly class="disableNumber" tabindex="-1"/> 
													</td>
												<%} %>
												<td class="td_number" width="10%">
													<%if( pickStockGroupForm.getBean().isCanConfirm() ==false){%>
													    <%if(   PickConstants.STATUS_OPEN.equals(pickStockGroupForm.getBean().getIssueReqStatus())
										                     || "".equals(Utils.isNull(pickStockGroupForm.getBean().getIssueReqStatus())) 
										                     ){%>
										                     
														    <input type="text" name="qty" value ="<%=o.getQty()%>" size="20" 
														     onkeypress="chkQtyKeypress(this,event,<%=i%>)"
									                         onchange="validateQty(this,<%=i%>)" class="enableNumber" tabindex="<%=tabindex%>"/> 
								                        <% }else{ %>
								                          
								                             <input type="text" name="qty" value ="<%=o.getQty()%>" size="20" class="disableNumber" tabindex="-1" readonly/> 
								                         <%} %>
								                     <%}else{ %>
								                          <input type="text" name="qty" value ="<%=o.getQty()%>" size="20" class="disableNumber" tabindex="-1" readonly/> 
								                     <%} %>
							                           <input type="hidden" name="orgQty" value ="<%=o.getOrgQty()%>"/>
												</td>
												<c:if test="${pickStockGroupForm.bean.canConfirm == true}">
													<td  class="td_number"  width="10%">
													    <input type="text" name="issueQty" value ="<%=o.getIssueQty()%>" size="20" 
													     onkeypress="chkIssueQtyKeypress(this,event,<%=i%>)"
								                         onchange="validateIssueQty(this,<%=i%>)" class="enableNumber"
								                         tabindex="<%=tabindex%>"/> 
													</td>
												</c:if>
												
												<td class="td_text" width="25%"> <%out.print(o.getJobId()+" "+o.getJobName());%>
												    <input type="hidden" name="jobId" value ="<%=o.getJobId() %>" size="20" readonly class="disableText"/>
												</td>
												
											</tr>
									
									  <%
									    }//for
									  %>
									  <tr>
									   <%if(   PickConstants.STATUS_OPEN.equals(pickStockGroupForm.getBean().getIssueReqStatus())
									         || "".equals(Utils.isNull(pickStockGroupForm.getBean().getIssueReqStatus())) ){%>
									        <td colspan="3" align="right">
									     <%}else{ %>
									         <td colspan="2" align="right">
									     <%} %>
									     <b> รวมจำนวน :</b>
									     </td>
									     <c:choose>
										    <c:when test="${pickStockGroupForm.bean.canConfirm == true}">
										        <td align="left" ><input type="text" size="20" id ="totalQty" name ="bean.totalQty" class="disableNumber" value="" readonly/></td>
										        <td align="left" colspan="2" ><input type="text" size="20" id ="totalIssueQty" name ="bean.totalQty" class="disableNumber" value="" readonly/>  <b>ชิ้น</b></td>
										    </c:when>
										    <c:otherwise>
										       <td align="left" colspan="2"   width="10%">&nbsp;&nbsp;&nbsp;<input type="text" size="20" id ="totalQty" name ="bean.totalIssueQty" class="disableNumber" value="" readonly/>  <b>ชิ้น</b></td>
										    </c:otherwise>
										</c:choose>
									  </tr>
									  
							</table>
							
								 <div align="left">
									 
								</div>
								
						</c:if>


					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
									<tr>
										<td align="left">
					 
					                      <c:if test="${pickStockGroupForm.bean.issueReqStatus == 'I'}">
											<%--  <a href="javascript:cancelIssueAction('${pageContext.request.contextPath}')"> --%>
											   <input type="button" value="    Cancel Issue     " class="" disabled> 
											 <!-- </a> -->  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										   </c:if>	
										   
										 <c:if test="${pickStockGroupForm.bean.canComplete == true}">
											<a href="javascript:completeAction('${pageContext.request.contextPath}')">
											  <input type="button" value="Pick Complete" class="newPosBtnLong"> 
											 </a>
										 </c:if>	
										 
									      <c:if test="${pickStockGroupForm.bean.canConfirm == true}">
											<a href="javascript:confirmAction('${pageContext.request.contextPath}')">
											  <input type="button" value="ยืนยัน issue" class="newPosBtnLong"> 
											 </a>
										 </c:if>	
									
										<c:if test="${pickStockGroupForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
												<input type="button" value="บันทึก request issue" class="newPosBtnLong"> 
											</a>
										</c:if>
										 <c:if test="${pickStockGroupForm.bean.canCancel == true}">
											<%--  <a href="javascript:cancel('${pageContext.request.contextPath}')"> --%>
											   <input type="button" value="    ยกเลิก     " class="" disabled> 
											 <!-- </a> -->  
										 </c:if>
										 
										  <c:if test="${pickStockGroupForm.bean.issueReqStatus == 'I'}">
											 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="    Export     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>		
										  <c:if test="${pickStockGroupForm.bean.issueReqStatus == 'O'}">
											 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="    Export     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>	
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>	
																
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