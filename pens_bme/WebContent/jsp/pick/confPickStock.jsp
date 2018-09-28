<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.isecinc.pens.bean.ReqPickStock"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.*"%>
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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<jsp:useBean id="confPickStockForm" class="com.isecinc.pens.web.pick.ConfPickStockForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionIdUtils.getInstance().getIdSession() %>" type="text/css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	<%if(confPickStockForm.getBean().isCanEditDeliveryDate()){%>
	   new Epoch('epoch_popup','th',document.getElementById('deliveryDate'));
	<%}%>
}

function print(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=print";
	form.submit();
	return true;
}
function printMini(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=printMini";
	form.submit();
	return true;
}
function printBillMini(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=printBillMini";
	form.submit();
	return true;
}
function printByGroupCode(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=printByGroupCode";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function cancelAction(path){
	if(confirm("ยืนยันยกเลิก Request นี้")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		var form = document.confPickStockForm;
		form.action = path + "/jsp/confPickStockAction.do?do=cancelAction";
		form.submit();
		return true;
	}
	return false;
}

function exportExcel(path){
	var form = document.confPickStockForm;
	form.action = path + "/jsp/confPickStockAction.do?do=exportToExcel";
	form.submit();
	return true;
}

function confirmPick(path){
	var form = document.confPickStockForm;
	//validate delivery date
	var deliveryDate =$('#deliveryDate').val();
	var totalCtn =$('#totalCtn').val();
	
	if(deliveryDate ==""){
		alert("กรุณากรอกวันที่  พร้อมจัดส่ง");
		return false;
	}
	if(totalCtn ==""){
		alert("กรุณากรอก รวมจำนวนหีย");
		return false;
	}
	
	if(confirm("กรุณายันยัน เบิกข้อมูลจากคลัง")){
	   /**Control Save Lock Screen **/
	   startControlSaveLockScreen();
		
	   form.action = path + "/jsp/confPickStockAction.do?do=confirmAction";
	   form.submit();
	   return true;
	}
	return false;
}

function saveDeliveryDate(path){
	var form = document.confPickStockForm;
	
	//validate delivery date
	var deliveryDate =$('#deliveryDate').val();
	var totalCtn =$('#totalCtn').val();
	
	if(deliveryDate ==""){
		alert("กรุณากรอกวันที่  พร้อมจัดส่ง");
		return false;
	}
	if(totalCtn ==""){
		alert("กรุณากรอก รวมจำนวนหีย");
		return false;
	}
	
	if(confirm("กรุณายันยัน บันทึกข้อมูล วันที่จัดส่ง ")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
	   form.action = path + "/jsp/confPickStockAction.do?do=saveDeliveryDateAction";
	   form.submit();
	   return true;
	}
	return false;
}

function savePick(path){
	var form = document.confPickStockForm;
	if(confirm("กรุณายันยัน เบิกข้อมูลจากคลัง")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
			
	   form.action = path + "/jsp/confPickStockAction.do?do=saveAction";
	   form.submit();
	   return true;
	}
	return false;
}

function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		/**Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		var form = document.confPickStockForm;
		form.action = path + "/jsp/confPickStockAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function confirmAction(path){
	var form = document.confPickStockForm;
	if(confirm("ยันยันการ Confirm ข้อมูล")){
		 /**Control Save Lock Screen **/
		 startControlSaveLockScreen();
		
		 form.action = path + "/jsp/confPickStockAction.do?do=confirmAction";
		 form.submit();
		 return true;
	}
	return false;
}

function search(path){
	var form = document.confPickStockForm;
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
	
	form.action = path + "/jsp/confPickStockAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function gotoPage(path,pageNumber){
	var form = document.confPickStockForm;
   // form.action = path + "/jsp/confPickStockAction.do?do=search&pageNumber="+pageNumber+"&prevPageNumber="+form.pageNumber.value+"&totalQtyCurPage="+document.getElementsByName("totalQtyCurPage")[0].value;
	form.action = path + "/jsp/confPickStockAction.do?do=search&pageNumber="+pageNumber;
	
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

function chkQtyKeypress(obj,e,row){
	//alert(obj.value);
	if(e != null && e.keyCode == 13){
		return validateQty(obj,row);
	}
}

function validateQty(obj,row){
	var table = document.getElementById('tblProduct');
	var rows = table.getElementsByTagName("tr"); 
	
	var r = isNum(obj);
	var reqQtyObj = document.getElementsByName("reqQty");
	
	if(r){
		//validate Onhand Qty
		var reqQty = parseInt(reqQtyObj[row].value);
		var issueQty = parseInt(obj.value);
		if(issueQty > reqQty){
			alert("จำนวนรวม  QTY ที่เบิกได้จริง ("+issueQty+") มีมากว่า  QTY ที่จะเบิก("+reqQty+")");
			//rows[row+1].className ="lineError";
			obj.value = "";
			obj.focus();
			return false;
		}
		
		sumQty();
	}
	return true;
}

function sumQty(){
	var qtyObj = document.getElementsByName("issueQty");
	var totalQtyNotInCurPage = 0;
	if(document.getElementsByName("totalQtyNotInCurPage")[0].value !=""){
		totalQtyNotInCurPage = parseInt(document.getElementsByName("totalQtyNotInCurPage")[0].value);
	}
	
	//alert(totalQtyNotCurPage);
	var sumCurPageQty = 0;
	var qtyInt = 0;
	for(var i=0;i<qtyObj.length;i++){
		if(qtyObj[i].value == '' || qtyObj[i].value == '0'){
			qtyInt = 0;
		}else{
			qtyInt =parseInt(qtyObj[i].value);
		}
		sumCurPageQty = sumCurPageQty + qtyInt;
	}
	//cur Page
	document.getElementsByName("curPageQty")[0].value = sumCurPageQty;
	//total All show
	//alert(totalQtyNotInCurPage +","+ sumCurPageQty);
	
	document.getElementsByName("bean.totalQty")[0].value = totalQtyNotInCurPage + sumCurPageQty ;
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/pick/setSumQtyByPageAjax.jsp",
			data : "pageNumber=" + document.getElementsByName("pageNumber")[0].value +
			       "&curPageQty=" + document.getElementsByName("curPageQty")[0].value +
			       "&totalQtyNotInCurPage=" + document.getElementsByName("totalQtyNotInCurPage")[0].value,
			//async: false,
			cache: true,
			success: function(){
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
				<jsp:param name="function" value="confPickStock"/>
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
						<html:form action="/jsp/confPickStockAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						        <tr>
                                  <td colspan="3" align="center"><font size="3"><b></b></font></td>
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
                                        <html:text property="bean.statusDesc" styleId="statusDesc" size="20" readonly="true" styleClass="disableText"/>
                                     </td>
									<td align="right"> ผู้เบิก</td>
									<td align="left">
									  <html:text property="bean.requestor" styleId="requestor" size="20" readonly="true" styleClass="disableText"/>  
									</td>
								</tr>
								<tr>
	                                    <td> กลุ่มร้านค้า </td>		
									    <td>
											 <html:text property="bean.custGroup" styleId="custGroup" size="20" readonly="true" styleClass="disableText"/>	
							           </td>
									   <td align="right"></td>
									   <td></td>
								</tr>
								<tr>
									<td >รหัสร้านค้า
									</td>
									<td align="left" colspan="3"> 
									  <html:text property="bean.storeCode" styleId="storeCode" size="20" readonly="true" styleClass="disableText"/>-
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="60"/>
									</td>
								</tr>
								<tr>
                                    <td> Sub Inventory</td>
									<td colspan="3">
						               <html:text property="bean.subInv" styleId="subInv" size="10" readonly="true" styleClass="disableText"/>
						               Store No <html:text property="bean.storeNo" styleId="storeNo" size="20" readonly="true" styleClass="disableText"/>
									
									 Warehouse
									      <html:select property="bean.wareHouse" styleId="wareHouse" disabled="true" styleClass="disableText">
											<html:options collection="wareHouseList2" property="key" labelProperty="name"/>
									    </html:select>
									</td>
								</tr>	
								<tr>
                                    <td > หมายเหตุ </td>
                                    <td colspan="3"> 
                                      <html:text property="bean.remark" styleId="remark" size="60" readonly="true" styleClass="disableText"/>
                                     </td>
								</tr>	
								  <c:if test="${confPickStockForm.bean.canEditDeliveryDate == true}">
									<tr>
	                                    <td> วันที่พร้อมจัดส่ง <font color="red">*</font></td>
										<td colspan="3">
							               <html:text property="bean.deliveryDate" styleId="deliveryDate" size="10" />
							              
										 รวมจำนวนหีบ  <font color="red">*</font>
										      <html:text property="bean.totalCtn" styleId="totalCtn" size="15" onblur="isNum(this)" onchange="isNum(this)"/>
										</td>
									</tr>	
								</c:if>
								<c:if test="${confPickStockForm.bean.canEditDeliveryDate == false}">
									<tr>
	                                    <td> วันที่พร้อมจัดส่ง <font color="red"></font></td>
										<td colspan="3">
							               <html:text property="bean.deliveryDate" styleId="deliveryDate" size="10" readonly="true" styleClass="disableText"/>
							              
										 รวมจำนวนหีบ  <font color="red"></font>
										      <html:text property="bean.totalCtn" styleId="totalCtn" size="15"  readonly="true" styleClass="disableText"/>
										&nbsp;&nbsp;
										Invoice No &nbsp;<html:text property="bean.invoiceNo" styleId="invoiceNo" size="20" readonly="true" styleClass="disableText"/>
							              
										</td>
									</tr>	
								</c:if>
						   </table>
					  </div>

				 <% 
					    int start = 0;
					    int end = 0;
					    int pageNumber = 1;
					  
				  %>

					<%if(session.getAttribute("results") != null) {%>
					 <!-- Page -->
					<% if(session.getAttribute("totalPage") != null){ 
					
					   int totalPage = ((Integer)session.getAttribute("totalPage")).intValue();
					   int totalRow = ((Integer)session.getAttribute("totalRow")).intValue();
					   int pageSize = PickConstants.CONF_PICK_PAGE_SIZE;
					   if(confPickStockForm.getBean().isNewSearch()){
						  pageNumber = 1;
					   }else{
					      pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
					   }
					   
					   start = ((pageNumber-1)*pageSize)+1;
					   end = (pageNumber * pageSize);
					   if(end > totalRow){
						   end = totalRow;
					   }
					%>
					   
					<div align="left">
					   <span class="pagebanner">รายการทั้งหมด  <%=totalRow %> รายการ, แสดงรายการที่  <%=start %> ถึง  <%=end %>.</span>
					   
					   <span class="pagelinks">
						หน้าที่ 
						 <% 
							 for(int r=0;r<totalPage;r++){
								 if(pageNumber ==(r+1)){
							 %>
			 				   <strong><%=(r+1) %></strong>
							 <%}else{ %>
							 
							    <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
							    
						 <% }} %>				
						</span>
					</div>
					<%} %>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth" width="100%">
						     <tr>					            
				                    <th >GroupCode</th>
				                    <th >PensItem</th>
				                    <th >Material Master</th>
				                    <th >Barcode</th>
									<th >Qty ที่จะเบิก</th>
									<th >Qty ที่เบิกได้จริง</th>
							</tr>
							<%
							List<ReqPickStock> resultList =(List<ReqPickStock>) session.getAttribute("results");
							int tabindex = 0;
							int no= start;
							String titleDisp ="";
							//int index = 0;
							
							for(int i=0;i<resultList.size();i++){
							   ReqPickStock o = (ReqPickStock) resultList.get(i);
							   //System.out.println("issueQty:"+o.getIssueQty());
							   
							   String classStyle = (i%2==0)?"lineO":"lineE";
							   if( !"".equals(Utils.isNull(o.getLineItemStyle()))){
								   classStyle = o.getLineItemStyle();
							   }
							%>
							    <tr id="<%=o.getLineItemId()%>" class="<%=classStyle%>"> 
						            <td class="td_text_center" width="10%"><%=o.getGroupCode()%>
						              <input tabindex="-1" type="hidden" name="groupCode" value ="<%=o.getOnhandQty()%>"/>
						            </td>
						            <td class="td_text_center" width="10%"> <%=o.getPensItem() %> </td>
									<td class="td_text_center" width="20%"> <%=o.getMaterialMaster() %> </td>
									<td class="td_text_center" width="20%"> <%=o.getBarcode() %> </td>

									<td class="td_text_right" width="20%">
										  <input tabindex="-1" type="text" name="reqQty" value ="<%=Utils.isNull(o.getQty()) %>" size="25"  
										    class="disableNumber" readonly
										   />		 
									</td>
									<td class="td_text_right" width="20%">
									  <c:choose>
									     <c:when test="${confPickStockForm.bean.canEdit == true}">
										      <input tabindex="1" type="text" name="issueQty" value ="<%=Utils.isNull(o.getIssueQty()) %>" size="25"  
											    class="disableNumber" readonly
											    onkeypress="chkQtyKeypress(this,event,<%=i%>)"
							                    onchange="validateQty(this,<%=i%>)"/>	
									     </c:when>
									     <c:otherwise>
										       <input tabindex="-1" type="text" name="issueQty" value ="<%=Utils.isNull(o.getIssueQty()) %>" size="25"  
											    class="disableNumber" readonly/>
									     </c:otherwise>
									  </c:choose>
									</td>
							  </tr>
							<% 
							//index++;
							} %>
						</table>
					
					
					<div align="right">
						<table  border="0" cellpadding="3" cellspacing="0" class="tableSearchNoWidth" width="100%">
							<tr>
								<td align="right" width="80%"><b>รวมทั้งสิ้น :  
								   <html:text property="bean.totalReqQty" styleId="totalReqQty" size="20" styleClass="disableNumberBold" readonly="true"/>
								  </b>
								</td>
								
								<td align="right" width="20%">
								   <html:text property="bean.totalQty" styleId="totalQty" size="20" styleClass="disableNumberBold" readonly="true"/>
								 
								   <!-- totalQtyNotInCurPage: --><input type="hidden" name="totalQtyNotInCurPage" id="totalQtyNotInCurPage" value="${confPickStockForm.bean.totalQtyNotInCurPage}"/>
								   <!-- curPageQty: --><input type="hidden" name = "curPageQty" id="curPageQty"/>	
								</td>
							</tr>
						</table>
					</div>	
				<%} %>		
				
					<!-- BUTTON ACTION-->
					<div align="center">
						<table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
								 <c:if test="${confPickStockForm.bean.canCancel == true}">
									<a href="javascript:cancelAction('${pageContext.request.contextPath}')">
									  <input type="button" value=" ยกเลิก Request นี้" class="newPosBtnLong"> 
									</a>
								 </c:if> 
								 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
								 <c:if test="${confPickStockForm.bean.canPrint == true}">
								    <a href="javascript:printMini('${pageContext.request.contextPath}')">
									  <input type="button" value=" พิมพ์ ใบเบิกสินค้าแบบย่อ " class="newPosBtnLong"> 
									</a>
									
									<a href="javascript:print('${pageContext.request.contextPath}')">
									  <input type="button" value=" พิมพ์ ใบเบิกสินค้า  " class="newPosBtnLong"> 
									</a>
								 </c:if>
								  <c:if test="${confPickStockForm.bean.canPrint == true}">
									<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="  Export  " class="newPosBtnLong"> 
									</a>
								 </c:if>
								  <c:if test="${confPickStockForm.bean.canEdit == true}">
									<a href="javascript:savePick('${pageContext.request.contextPath}')">
									  <input type="button" value=" บันทึก    " class="newPosBtnLong">
									 </a>
								 </c:if>
								 
								   <c:if test="${confPickStockForm.bean.canConfirm == false}">
									  <c:if test="${confPickStockForm.bean.canEditDeliveryDate == true}">
										<a href="javascript:saveDeliveryDate('${pageContext.request.contextPath}')">
										  <input type="button" value=" บันทึก(ข้อมูลวันที่จัดส่ง)" class="newPosBtnLong"> 
										 </a>
									 </c:if>
								  </c:if>
								  
								  <c:if test="${confPickStockForm.bean.canConfirm == true}">
									<a href="javascript:confirmPick('${pageContext.request.contextPath}')">
									  <input type="button" value=" ยืนยัน    " class="newPosBtnLong"> 
									 </a>
								 </c:if>	
								<a href="javascript:back('${pageContext.request.contextPath}','','add')">
								  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
								</a>						
								&nbsp;
								 <a href="javascript:printBillMini('${pageContext.request.contextPath}')">
									  <input type="button" value=" พิมพ์ ใบเดินบิล" class="newPosBtnLong"> 
									</a>
								</td>
							</tr>
						</table>
					</div>
		
					<!-- ************************Result ***************************************************-->
					
					<!-- hidden field -->
					<input type="hidden" name="pageNumber" id="pageNumber" value="<%=pageNumber%>"/>
					
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