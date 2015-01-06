<%@page import="com.isecinc.pens.bean.MoveWarehouse"%>
<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="com.isecinc.pens.bean.ReqPickStock"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

<jsp:useBean id="moveWarehouseForm" class="com.isecinc.pens.web.pick.MoveWarehouseForm" scope="session" />
<%
if(session.getAttribute("wareHouseList") == null){
	List<References> wareHouseList = new ArrayList();
	References ref1 = new References("","");
	wareHouseList.add(ref1);
	wareHouseList.addAll(JobDAO.getWareHouseList());
	
	session.setAttribute("wareHouseList",wareHouseList);
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/conf_pick_stock.css" type="text/css" />

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
	 new Epoch('epoch_popup', 'th', document.getElementById('openDate'));
	 sumTotal();
}

function clearForm(path){
	var form = document.moveWarehouseForm;
	form.action = path + "/jsp/moveWarehouseAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.moveWarehouseForm;
	form.action = path + "/jsp/moveWarehouseAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function search(path){
	var form = document.moveWarehouseForm;

	if(jobId ==""){
		alert("กรุณากรอก  ข้อมูลรับคืน ที่จะทำการย้าย Warehouse");
		$('#jobId').focus();
		return false;
	}
	
	form.action = path + "/jsp/moveWarehouseAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function save(path){
	var form = document.moveWarehouseForm;
	var openDate =$('#openDate').val();
	var warehouseFrom =$('#warehouseFrom').val();
	var warehouseTo =$('#warehouseTo').val();
	var jobId =$('#jobId').val();
	var newJobName =$('#newJobName').val();
	
	if(openDate ==""){
		alert("กรุณากรอก openDate");
		$('#openDate').focus();
		return false;
	}
	if(warehouseFrom ==""){
		alert("กรุณากรอก Warehouse From");
		$('#warehouseFrom').focus();
		return false;
	}
	
	if(warehouseTo ==""){
		alert("กรุณากรอก Warehouse To");
		$('#warehouseTo').focus();
		return false;
	}
	
	if(jobId ==""){
		alert("กรุณากรอก  รับคืนจาก");
		$('#jobId').focus();
		return false;
	}
	if(newJobName ==""){
		alert("กรุณากรอก  JobName");
		$('#newJobName').focus();
		return false;
	}
	
	if(warehouseFrom == warehouseTo){
		alert("ไม่สามารถย้ายเข้าคลังเดียวกันได้");
		$('#warehouseTo').focus();
		return false;
	}
	if( !checkOneSelected()){
		alert("กรุณาเลือกข้อมูลอย่างน้อย 1 รายการ");
		return false;
	}
	if(checkOpenDate()==false){
		alert("วันที่ Open Date ต้องน้อยกว่า หรือเท่ากับวันที่ Close Date");
		return false;
	}
	
	form.action = path + "/jsp/moveWarehouseAction.do?do=save";
	form.submit();
	return true;
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

function openJobPopup(path){
    var param = "&status=C";
	url = path + "/jsp/searchJobPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeCode,storeName,storeNo,subInv,wareHouse,wareHouseDesc){
	//alert(types+":"+desc);
	var form = document.moveWarehouseForm;
	
	if(storeNo=='' || subInv==''){
	    if(storeNo==''){
			alert("ไม่พบข้อมูล Store no  ไม่สามารถทำงานต่อได้");
		}
		if(subInv==''){
			alert("ไม่พบข้อมูล Sub Inventory  ไม่สามารถทำงานต่อได้");
		} 
		form.jobId.value = "";	
		form.jobName.value = "";	
		form.storeCode.value = '';
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
		form.warehouseFrom.value = "";
		form.warehouseFromDesc.value = "";
	}else{
		document.getElementsByName("bean.jobId")[0].value = code;
		document.getElementsByName("bean.jobName")[0].value = desc;
		
		document.getElementsByName("bean.storeCode")[0].value = storeCode;
		document.getElementsByName("bean.storeName")[0].value = storeName;
		document.getElementsByName("bean.storeNo")[0].value = storeNo;
		document.getElementsByName("bean.subInv")[0].value = subInv;
		document.getElementsByName("bean.warehouseFrom")[0].value = wareHouse;
		document.getElementsByName("bean.warehouseFromDesc")[0].value = wareHouseDesc;
	}
}

function getJobNameKeypress(e,code){
	var form = document.moveWarehouseForm;
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
	var form = document.moveWarehouseForm;
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
			form.jobName.value = "";	
			form.storeCode.value = '';
			form.storeName.value = "";
			form.storeNo.value = "";
			form.subInv.value = "";
			form.warehouseFrom.value = "";
			form.warehouseFromDesc.value = "";
		}else{
			
			form.jobName.value = retArr[0];	
			form.storeCode.value = retArr[1];
			form.storeName.value = retArr[2];
			form.storeNo.value = retArr[3];
			form.subInv.value = retArr[4];
			form.warehouseFrom.value = retArr[5];
			form.warehouseFromDesc.value = retArr[6];
		}
		
	}else{
		alert("ไม่พบข้อมูล");
		
		form.jobId.value = "";	
		form.jobName.value = "";
		form.storeCode.value ="";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
		form.warehouseFrom.value = "";
		form.warehouseFromDesc.value = "";
	}
	
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
			 totalQtyTemp += parseInt(qty[i].value);
			 
			 lineId[i].value = ""+(i+1);
		 } else{
			 lineId[i].value = "";
		 }
	}
	//alert(totalBoxTemp+","+totalQtyTemp);
	
	 $('#totalBox').val(totalBoxTemp);
	 $('#totalQty').val(totalQtyTemp);
}

function checkOpenDate(){
	var openDate =$('#openDate').val();
	var closeDate =$('#closeDate').val();
	
	if(openDate=='' || closeDate==''){return true;}
	openDate = openDate.split("/");
	opentime = new Date(openDate[2],openDate[1]-1,openDate[0]);

	closeDate = closeDate.split("/");
	closetime = new Date(closeDate[2],closeDate[1]-1,closeDate[0]);
	if((opentime-closetime) > 0){
		return false;
	}else{
		return true;
	}
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
				<jsp:param name="function" value="moveWarehouse"/>
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
						<html:form action="/jsp/moveWarehouseAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                  <td colspan="3" align="center"><font size="3"><b></b></font></td>
							   </tr>			
								<tr>
                                    <td align="right"> รับคืนจาก  <font color="red">*</font></td>
									<td colspan="3">
						               <html:text property="bean.jobId" styleId="jobId" size="20"  onkeypress="getJobNameKeypress(event,this)"/> 
						                <input type="button" name="x1" value="..." onclick="openJobPopup('${pageContext.request.contextPath}')"/>
						                <html:text property="bean.jobName" styleId="jobName" size="30" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								<tr>
									<td align="right">รหัสร้านค้า
									</td>
									<td align="left" colspan="3"> 
									 <html:text property="bean.storeCode" styleId="storeCode" size="20" readonly="true" styleClass="disableText"/>-
									<html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									</td>
								</tr>
								<tr>
                                    <td align="right"> Sub Inventory</td>
									<td colspan="3">
						               <html:text property="bean.subInv" styleId="subInv" size="20" readonly="true" styleClass="disableText"/>
									 Store No
						               <html:text property="bean.storeNo" styleId="storeNo" size="20" readonly="true" styleClass="disableText"/>
									</td>
								</tr>	
								 <tr>
                                    <td align="right"> Move From Warehouse</td>
                                      <td>
                                        <html:text property="bean.warehouseFrom" styleId="warehouseFrom" size="3" readonly="true" styleClass="disableText"/>-								
									    <html:text property="bean.warehouseFromDesc" styleId="warehouseFromDesc" size="30" readonly="true" styleClass="disableText" />	
                                     </td>
									<td align="left">
									      <a href="javascript:search('${pageContext.request.contextPath}')">
										     <input type="button" value=" แสดงข้อมูล   " class="newPosBtnLong"> 
										 </a>	
									</td>
									<td align="left"></td>
								</tr>     
								<tr>
									<td align="right">Move To Warehouse <font color="red">*</font></td>
									<td align="left" colspan="3">
									  <html:select property="bean.warehouseTo" styleId="warehouseTo" >
											<html:options collection="wareHouseList" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>     
								  <tr>
                                    <td align="right">Open Date   <font color="red">*</font></td>
                                     <td colspan="3">
                                       <html:text property="bean.openDate" styleId="openDate" size="20"  styleClass="" onchange="checkOpenDate()"/>
									Close Date 
									 <html:text property="bean.closeDate" styleId="closeDate" size="20" readonly="true" styleClass="disableText"/>	  
									</td>
								</tr>		
								<tr>
                                    <td align="right"> New Job Id</td>
									<td colspan="3">
						               <html:text property="bean.newJobId" styleId="newJobId" size="20" readonly="true" styleClass="disableText"/>
									    New Job Name
						                 <html:text property="bean.newJobName" styleId="newJobName" size="30" /><font color="red">*</font>
									</td>
								</tr>	  
								<tr>
                                    <td align="right"> หมายเหตุ</td>
									<td colspan="3">
										<html:text property="bean.remark" styleId="remark" size="80" />
						              
									</td>
								</tr>	
						   </table>
					  </div>
					  
						  <!-- BUTTON ACTION-->
						<div align="center">
							<table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
														
									</td>
								</tr>
							</table>
						</div>
					  
				       <!-- Table Data -->
				<c:if test="${moveWarehouseForm.results != null}">
		
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						    <tr>
							<!-- 	<th >No</th> -->
								<th ><input type="checkbox" name="chkAll" onclick="checkAll(this)"/></th>
								<th >เลขที่กล่อง</th>
								<th >Qty</th>		
							</tr>
							<c:forEach var="results" items="${moveWarehouseForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO"/>
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE"/>
									</c:otherwise>
								</c:choose>
								
									<tr class="<c:out value='${tabclass}'/>">
										
										<td class="data_chk">
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
										<td class="data_boxNo" align="center">${results.boxNo}
											<input type="hidden" name="boxNo" value ="${results.boxNo}" size="40" readonly class="disableText"/>
											
										</td>
										<td class="data_qty" align="center">${results.qty}
										   <input type="hidden" name="qty" value ="${results.qty}" size="20" readonly class="disableText"/>
										</td>
									</tr>
							  </c:forEach>
					</table>
					     <br/>
						 <div align="left">
							<b>รวมจำนวนกล่อง  </b> : <input type="text" size="10" id ="totalBox" name ="bean.totalBox" class="disableNumber" value="" readonly/> กล่อง
							|<b> รวมจำนวนชิ้น </b> : <input type="text" size="10" id ="totalQty" name ="bean.totalQty" class="disableNumber" value="" readonly/> ชิ้น
						</div>
						 <div align="left">
							
						</div>
						
				</c:if>
				
					<div align="center">
						   <!-- Table Data -->
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="center">
									   
										 <c:if test="${moveWarehouseForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก      " class="newPosBtnLong"> 
											 </a>
										 </c:if>	
									     
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
											  <input type="button" value="    Clear      " class="newPosBtnLong"> 
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