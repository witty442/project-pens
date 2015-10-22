<%@page import="com.isecinc.pens.bean.ReqPickStock"%>
<%@page import="com.isecinc.pens.dao.constants.PickConstants"%>
<%@page import="com.isecinc.pens.bean.PickStock"%>
<%@page import="com.isecinc.pens.web.order.OrderAction"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.bean.Order"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
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
<jsp:useBean id="reqPickStockForm" class="com.isecinc.pens.web.pick.ReqPickStockForm" scope="session" />
<%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/req_pick_stock.css" type="text/css" />

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
	// new Epoch('epoch_popup', 'th', document.getElementById('issueReqDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('needDate'));
	 
	 <%if(session.getAttribute("results") != null) {%>
	    sumQty();
	  <%}%>
}
function clearForm(path){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=clear";
	form.submit();
	return true;
}
function back(path){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=prepare2&action=back";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.reqPickStockForm;
	form.action = path + "/jsp/reqPickStockAction.do?do=exportExcel";
	form.submit();
	return true;
}

function save(path){
	var form = document.reqPickStockForm;
	var requestor =$('#requestor').val();
	var storeCode =$('#storeName').val();
	var storeNo =$('#storeNo').val();
	var subInv =$('#subInv').val();
	var customerGroup =$('#customerGroup').val();
	
	if(customerGroup ==""){
		alert("กรุณากรอก กลุ่มร้านค้า");
		return false;
	}
	
	if(requestor ==""){
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
	if(confirm("ยันยันการบันทึกข้อมูล")){
	   form.action = path + "/jsp/reqPickStockAction.do?do=save";
	   form.submit();
	   return true;
	}
	return false;
}

function cancel(path){
	if(confirm("ยืนยันการยกเลิกรายการนี้")){
		var form = document.reqPickStockForm;
		form.action = path + "/jsp/reqPickStockAction.do?do=cancel";
		form.submit();
		return true;
	}
	return false;
}

function confirmAction(path){
	var form = document.reqPickStockForm;
	if(confirm("ยันยันการ Confirm ข้อมูล")){
		 form.action = path + "/jsp/reqPickStockAction.do?do=confirmAction";
		 form.submit();
		 return true;
	}
	return false;
}


function search(path){
	var form = document.reqPickStockForm;
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
	
	form.action = path + "/jsp/reqPickStockAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}

function gotoPage(path,pageNumber){
	var form = document.reqPickStockForm;
   // form.action = path + "/jsp/reqPickStockAction.do?do=search&pageNumber="+pageNumber+"&prevPageNumber="+form.pageNumber.value+"&totalQtyCurPage="+document.getElementsByName("totalQtyCurPage")[0].value;
	form.action = path + "/jsp/reqPickStockAction.do?do=search&pageNumber="+pageNumber;
	
   form.submit();
    return true;
}



function openPopupProduct(path,seqNo,types){
	var param = "&types="+types+"&seqNo="+seqNo;
	url = path + "/jsp/searchProductPopupAction.do?do=prepare2&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
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
	var onhandQtyObj = document.getElementsByName("onhandQty");
	//alert(onhandQtyObj[row].value);
	if(r){
		//validate Onhand Qty
		var onhandQty = parseInt(onhandQtyObj[row].value);
		var currQty = parseInt(obj.value);
		if(currQty > onhandQty){
			alert("จำนวนรวม QTY("+currQty+") มีมากว่า  Onhand QTY("+onhandQty+")");
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
	var qtyObj = document.getElementsByName("qty");
	var totalQtyNotInCurPage = 0;
	if(document.getElementsByName("totalQtyNotInCurPage")[0].value !=""){
		totalQtyNotInCurPage = parseInt(document.getElementsByName("totalQtyNotInCurPage")[0].value);
	}
	
	//alert(totalQtyNotCurPage);
	var sumCurPageQty = 0;
	for(var i=0;i<qtyObj.length;i++){
		if(qtyObj[i].value != '')
			sumCurPageQty = sumCurPageQty + parseInt(qtyObj[i].value);
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

function openPopupCustomer(path,types,storeType){
	var form = document.reqPickStockForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){

	var form = document.reqPickStockForm;
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
	var form = document.reqPickStockForm;
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
	var form = document.reqPickStockForm;
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
	var form = document.reqPickStockForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
		form.storeNo.value = "";
		form.subInv.value = "";
	}
}

  function showRow(id) {
    var row = document.getElementById(id);
    row.style.display = '';
  }

  function hideRow(id) {
    var row = document.getElementById(id);
    row.style.display = 'none';
  }
  
  function showDiv(id) {
	 id.style.display = 'block';
  }

  function hideDiv(id) {
	 id.style.display = 'none';
  }
  
  if (typeof String.prototype.startsWith != 'function') {
	  // see below for better implementation!
	  String.prototype.startsWith = function (str){
	    return this.indexOf(str) == 0;
	  };
  }
  
//id tr = groupCode_no 'ME1101_1'
function switchView(groupCode,show) {
	//alert("Hello World!".startsWith("He"));
    var rows = document.getElementById("tblProduct").rows
    for(var i = 0; i < rows.length; ++i) {
       var tr = rows.item(i);
       //alert(tr.id+":"+(tr.id).toString().startsWith(groupCode));
       if((tr.id).toString().startsWith(groupCode)){
    	   if(show){
    		   if((tr.id).toString() != groupCode+"_0")
    		      showRow(tr.id);
    	   }else{
    		   if((tr.id).toString() != groupCode+"_0")
    		      hideRow(tr.id);
    	   }
    	  
       } 
    }
    
    //switchImage
    if(show){
    	//alert(document.getElementById(groupCode+"_0_aShow"));
    	//alert(document.getElementById(groupCode+"_0_aHide"));
    	
    	hideDiv(document.getElementById(groupCode+"_0_aShow"));
    	showDiv(document.getElementById(groupCode+"_0_aHide")); 
    }else{
    	showDiv(document.getElementById(groupCode+"_0_aShow"));
    	hideDiv(document.getElementById(groupCode+"_0_aHide")); 
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
				<jsp:param name="function" value="reqPickStock"/>
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
						<html:form action="/jsp/reqPickStockAction">
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
									<td align="right"> ผู้เบิก  <font color="red">*</font></td>
									<td align="left">
									  <html:text property="bean.requestor" styleId="requestor" size="20" /><font color="red">*</font>	  
									</td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า  <font color="red">*</font></td>		
								    <td>
										 <html:select property="bean.custGroup" styleId="custGroup" onchange="resetStore()">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
						           </td>
								   <td align="right">
								     วันที่รับของ <font color="red">*</font>
								   </td>
								   <td> 
								      <html:text property="bean.needDate" styleId="needDate" size="20"  readonly="true" styleClass=""/>
								   </td>
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
                                    <td > หมายเหตุ </td>
                                    <td colspan="2"> 
                                      <html:text property="bean.remark" styleId="remark" size="60" />
                                      </td>
									
								</tr>	
						   </table>
						   
					  </div>

				  <!-- Page -->
				    <% 
					    int start = 0;
					    int end = 0;
					    int pageNumber = 1;
					  
				    %>
					<% if(session.getAttribute("totalPage") != null){ 
					
					   int totalPage = ((Integer)session.getAttribute("totalPage")).intValue();
					   int totalRow = ((Integer)session.getAttribute("totalRow")).intValue();
					   int pageSize = PickConstants.REQ_PICK_PAGE_SIZE;
					   if(Utils.isNull(request.getAttribute("action")).equalsIgnoreCase("newsearch")){
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
					
				  <%if(session.getAttribute("results") != null) {%>
						<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
						       <tr>
						            <th >แสดงเพิ่มเติม</th>
				                    <th >GroupCode</th>
				                    <th >PensItem</th>
									<th >MaterialMaster</th>
									<th >Barcode</th>
									<th >Onhand-Qty</th>
									<th >Qty ที่จะเบิก</th>
							</tr>
							<%
							List<ReqPickStock> resultList =(List<ReqPickStock>) session.getAttribute("results");
							int tabindex = 0;
							int no= start;
							String titleDisp ="";
							for(int i=0;i<resultList.size();i++){
							   ReqPickStock o = (ReqPickStock) resultList.get(i);
							   System.out.println("getLineItemStyle:"+o.getLineItemStyle());
							   
							   String classStyle = (i%2==0)?"lineO":"lineE";
							%>
							      <tr  id="<%=o.getLineItemId()%>" 
							        <%if( !"lineGroupBy".equals(o.getLineItemStyle())){%>
							            style="display:none"
							        <%}
							        	if(o.isLineErrorStock()){
							         %>
							        	   style="display:"  class="lineError"
							        <%   }else{ %>
							              class="<%=o.getLineItemStyle()%>"
							        <% } %>
							       > 
							            <td class="data_view">
							            <%if(o.getLineItemStyle().equals("lineGroupBy")) {%>
							                <div  id="<%=o.getLineItemId()+"_aShow"%>">
								                <a href="javascript:switchView('<%=o.getGroupCode()%>',true)">
								                   <img src="${pageContext.request.contextPath}/icons/icon_add.png" height="16" width="16"/>
								                </a> 
							                </div>
							                <div id="<%=o.getLineItemId()+"_aHide"%>" style="display:none">
								                 <a href="javascript:switchView('<%=o.getGroupCode()%>',false)">
								                   <img src="${pageContext.request.contextPath}/icons/icon_sub.png" height="16" width="16"/>
								                </a> 
							                </div>
							            <% } %>
							            </td>
							            <td class="data_groupCode"><%=o.getGroupCode()%></td>
							            <td class="data_pensItem"> <%=o.getPensItem() %> </td>
										<td class="data_materialMaster"><%=o.getMaterialMaster() %></td>
										<td class="data_barcode"><%=o.getBarcode() %></td>
										<td class="data_onhandQty">
										    <input tabindex="-1" type="hidden" name="onhandQty" value ="<%=o.getOnhandQty()%>"/>
										    <%=o.getOnhandQty()%>
										</td>
										<td class="data_qty">
										  <c:if test="${reqPickStockForm.bean.canEdit == true}">
											  <input tabindex="1" type="text" name="qty" value ="<%=Utils.isNull(o.getQty()) %>" size="20"  class="enableNumber"
											   onkeypress="chkQtyKeypress(this,event,<%=i%>)"
							                   onchange="validateQty(this,<%=i%>)"
											  />
										  </c:if>
										  
										  <c:if test="${reqPickStockForm.bean.canEdit == false}"><%=Utils.isNull(o.getQty())  %></c:if>
										</td>
								</tr>
							<%} %>
					</table>
					
					
					<div align="right">
						<table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="right">	 <span class="pagelinks">รวมทั้งสิ้น :
								<html:text property="bean.totalQty" styleId="totalQty" size="30" styleClass="disableNumber"/>
								<br/>
								<input type="hidden" name="totalQtyNotInCurPage" id="totalQtyNotInCurPage" value=""/>
								<input type="hidden" name = "curPageQty" id="curPageQty"/>
								</span>			
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
					 
									      <c:if test="${reqPickStockForm.bean.canConfirm == true}">
											<a href="javascript:confirmAction('${pageContext.request.contextPath}')">
											  <input type="button" value="ยืนยัน issue" class="newPosBtnLong"> 
											 </a>
										 </c:if>	
										 
										  <c:if test="${reqPickStockForm.bean.canEdit == true}">
											<a href="javascript:save('${pageContext.request.contextPath}')">
											  <input type="button" value="บันทึก request issue" class="newPosBtnLong"> 
											 </a>
										 </c:if>
										 
										 <c:if test="${reqPickStockForm.bean.canCancel == true}">
											 <a href="javascript:cancel('${pageContext.request.contextPath}')">
											   <input type="button" value="    ยกเลิก     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>
										 
										 <c:if test="${reqPickStockForm.bean.status == 'I'}">
											 <a href="javascript:exportExcel('${pageContext.request.contextPath}')">
											   <input type="button" value="    Export     " class="newPosBtnLong"> 
											 </a>  
										 </c:if>	
										 <c:if test="${reqPickStockForm.bean.status == 'O'}">
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