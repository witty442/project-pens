<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%-- <%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%> --%>

<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />


<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	<%if("lotus".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
	<%}else if("bigc".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
    <%}else if("tops".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
	<%} else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
        new Epoch('epoch_popup', 'th', document.getElementById('countDateFrom'));
        new Epoch('epoch_popup', 'th', document.getElementById('countDateTo'));
    <%} else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
        new Epoch('epoch_popup', 'th', document.getElementById('asOfDate'));
    <%}else if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDate'));
	<%}else if("onhandBigC".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('salesDate'));
	<%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
	    new Epoch('epoch_popup', 'th', document.getElementById('asOfDateFrom'));
	    new Epoch('epoch_popup', 'th', document.getElementById('asOfDateTo'));
	 <%} %>
	 
}

function search(path){
	var form = document.summaryForm;
	<%if("lotus".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	  // var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom =="" &&fileName ==""){
		   alert("กรุณากรอดข้อมูลในการค้นหาอย่างน้อยหนึ่งรายการ");
		   return false;
	   }
   <%}else if("bigc".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   //var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom ==""  &&fileName ==""){
		   alert("กรุณากรอดข้อมูลในการค้นหาอย่างน้อยหนึ่งรายการ");
		   return false;
	   }
   <%}else if("tops".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.salesDateFrom.value;
	   var salesDateTo = form.salesDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   //var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom ==""  &&fileName ==""){
		   alert("กรุณากรอดข้อมูลในการค้นหาอย่างน้อยหนึ่งรายการ");
		   return false;
	   } 
	<%}else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var salesDateFrom = form.countDateFrom.value;
	   var salesDateTo = form.countDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   var pensCustCodeTo = form.pensCustCodeTo.value;
	   var fileName = form.fileName.value;
	   
	   if(salesDateFrom =="" && salesDateTo =="" && pensCustCodeFrom =="" && pensCustCodeTo =="" &&fileName ==""){
		   alert("กรุณากรอดข้อมูลในการค้นหาอย่างน้อยหนึ่งรายการ");
		   return false;
	   }

	<%}else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var asOfDateFrom = form.asOfDate.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom =="" && pensCustCodeFrom ==""){
		   alert("กรุณากรอดข้อมูลในการค้นหาอย่างน้อยหนึ่งรายการ");
		   return false;
	   }
	   
   <%}else if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var asOfDateFrom = form.salesDate.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom ==""){ 
		   alert("กรุณากรอกข้อมูลวันที่ As Of");
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("กรุณากรอกข้อมูลรหัสร้านค้า");
		   return false;
	   } 
	    
	<%}else if("onhandBigC".equalsIgnoreCase(request.getParameter("page"))) {%>
		   var asOfDateFrom = form.salesDate.value;
		   var pensCustCodeFrom = form.pensCustCodeFrom.value;
		   
		   if(asOfDateFrom ==""){ 
			   alert("กรุณากรอกข้อมูลวันที่ As Of");
			   return false;
		   }
		    if(pensCustCodeFrom ==""){ 
			   alert("กรุณากรอกข้อมูลรหัสร้านค้า");
			   return false;
		   }    
	   
	 <%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
	   var asOfDateFrom = form.asOfDateFrom.value;
	   var asOfDateTo = form.asOfDateTo.value;
	   var pensCustCodeFrom = form.pensCustCodeFrom.value;
	   
	   if(asOfDateFrom ==""){ 
		   alert("กรุณากรอกข้อมูลวันที่ขาย From");
		   return false;
	   }
	   if(asOfDateTo ==""){ 
		   alert("กรุณากรอกข้อมูลวันที่ขาย To");
		   return false;
	   }
	    if(pensCustCodeFrom ==""){ 
		   alert("กรุณากรอกข้อมูลรหัสร้านค้า");
		   return false;
	   } 
	   
	   
	<%}%>
	
	form.action = path + "/jsp/summaryAction.do?do=search&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}

function exportExcel(path){
	var form = document.summaryForm;
	form.action = path + "/jsp/summaryAction.do?do=export&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.summaryForm;
	form.action = path + "/jsp/summaryAction.do?do=prepare&action=new&page=<%=request.getParameter("page")%>";
	form.submit();
	return true;
}

function openPopupCustomer(path,types,storeType){
    var param = "&types="+types;
        param += "&storeType="+storeType;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare2&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function openPopupCustomerAll(path,types,storeType){
    var param = "&types="+types;
        param += "&storeType="+storeType;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function openPopupGroup(path){
    var param = "";
	url = path + "/jsp/searchGroupPopupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function openPopupProduct(path,types){
	var param = "&types="+types;
	url = path + "/jsp/searchProductPopupAction.do?do=prepare&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,types){
	var form = document.summaryForm;
	//alert(form);
	if("from" == types){
		form.pensCustCodeFrom.value = code;
		form.pensCustNameFrom.value = desc;
	}else{
		form.pensCustCodeTo.value = code;
		form.pensCustNameTo.value = desc;
	}
} 

function setGroupMainValue(code,desc,types){
	var form = document.summaryForm;
	form.group.value = code;
	form.groupDesc.value = desc;
}

function setProductMainValue(code,desc,types){
	var form = document.summaryForm;
	//alert(form);
	if("from" == types){
		form.pensItemFrom.value = code;
	}else{
		form.pensItemTo.value = code;
	}
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.summaryForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("pensCustNameFrom" == fieldName){
				form.pensCustNameFrom.value = '';
			}
			if("pensCustNameTo" ==fieldName){
				form.pensCustNameTo.value = '';
			}
		}else{
		  getCustName(custCode,fieldName);
		}
	}
}

function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.summaryForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameAjax.jsp",
			data : "custCode=" + custCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
	if("pensCustNameFrom" == fieldName){
		form.pensCustNameFrom.value = returnString;
	}
	if("pensCustNameTo" ==fieldName){
		form.pensCustNameTo.value = returnString;
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
	    	<%if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromWacoal"/>
				</jsp:include>
			<%}else if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandLotus"/>
				</jsp:include>
		    <%}else if("onhandBigC".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandBigC"/>
				</jsp:include>
			<%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEOnhandLotusPeriod"/>
				</jsp:include>
			<%}else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromPhysical"/>
				</jsp:include>
			<%}else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEDiffStock"/>
				</jsp:include>
			<%}else if("lotus".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromLotus"/>
				</jsp:include>
			<%}else if("bigc".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromBigC"/>
				</jsp:include>
			<%}else if("tops".equalsIgnoreCase(request.getParameter("page"))){ %>
			   <jsp:include page="../program.jsp">
					<jsp:param name="function" value="SummaryBMEFromTops"/>
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
						<html:form action="/jsp/summaryAction">
						<jsp:include page="../error.jsp"/>
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="65%">
						<%if("lotus".equalsIgnoreCase(request.getParameter("page"))) {%>
								 <tr>
									<td align="left">จาก วันที่ขาย&nbsp;&nbsp;&nbsp; <html:text property="transactionSummary.salesDateFrom" styleId="salesDateFrom" readonly="true"/>
									ถึง วันที่ขาย&nbsp;&nbsp;&nbsp; <html:text property="transactionSummary.salesDateTo" styleId="salesDateTo"/></td>
								</tr>
								<tr>
									<td align="left">รหัสร้านค้า
									    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="transactionSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
									    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','lotus')"/>
									    <html:text property="transactionSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="40"/>
									</td>
									<td align="left">
									</td>
								</tr>
								<tr>
									<td align="left">ชื่อไฟล์ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;
									    <html:text property="transactionSummary.fileName" styleId="fileName"/></td>
									
								</tr>
						<%}else if("bigc".equalsIgnoreCase(request.getParameter("page"))) {%>
								 <tr>
									<td align="left">จาก วันที่ขาย&nbsp;&nbsp;&nbsp; <html:text property="transactionSummary.salesDateFrom" styleId="salesDateFrom" readonly="true"/>
									ถึง วันที่ขาย&nbsp;&nbsp;&nbsp;<html:text property="transactionSummary.salesDateTo" styleId="salesDateTo"/></td>
								</tr>
								<tr>
									<td align="left">รหัสร้านค้า
									    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="transactionSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
									    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','bigc')"/>
									    <html:text property="transactionSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="40"/>
									</td>
								</tr>
								<tr>
									<td align="left">ชื่อไฟล์ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;
									    <html:text property="transactionSummary.fileName" styleId="fileName"/></td>
									<td align="left"></td>
								</tr>
						<%}else if("tops".equalsIgnoreCase(request.getParameter("page"))) {%>
								 <tr>
									<td align="left">จาก วันที่ขาย&nbsp;&nbsp;&nbsp; <html:text property="transactionSummary.salesDateFrom" styleId="salesDateFrom" readonly="true"/>
									ถึง วันที่ขาย&nbsp;&nbsp;&nbsp;<html:text property="transactionSummary.salesDateTo" styleId="salesDateTo"/></td>
								</tr>
								<tr>
									<td align="left">รหัสร้านค้า
									    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="transactionSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
									    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','tops')"/>
									    <html:text property="transactionSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="40"/>
									</td>
								</tr>
								<tr>
									<td align="left">ชื่อไฟล์ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;
									    <html:text property="transactionSummary.fileName" styleId="fileName"/></td>
									<td align="left"></td>
								</tr>
						<%}else if("physical".equalsIgnoreCase(request.getParameter("page"))) {%>
								 <tr>
									<td align="left">จาก วันที่นับสต็อก&nbsp; <html:text property="physicalSummary.countDateFrom" styleId="countDateFrom" readonly="true"/></td>
									<td align="left">ถึง วันที่นับสต็อก&nbsp; <html:text property="physicalSummary.countDateTo" styleId="countDateTo"/></td>
								</tr>
								<tr>
									<td align="left">จาก รหัสร้านค้า&nbsp;&nbsp; &nbsp; 
									    <html:text property="physicalSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="10" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
									    <html:text property="physicalSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText"/>
									    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									</td>
									<td align="left">ถึง รหัสร้านค้า&nbsp;&nbsp;&nbsp;&nbsp; 
									    <html:text property="physicalSummary.pensCustCodeTo" styleId="pensCustCodeTo" size="10" onkeypress="getCustNameKeypress(event,this,'pensCustNameTo')"/>-
									    <html:text property="physicalSummary.pensCustNameTo" styleId="pensCustNameTo" readonly="true" styleClass="disableText"/>
									    <input type="button" name="x2" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','to','')"/>
									</td>
								</tr>
								<tr>
									<td align="left">ชื่อไฟล์ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp; &nbsp;&nbsp;    
									 <html:text property="physicalSummary.fileName" styleId="fileName"/></td>
									<td align="left"></td>
								</tr>
						<%}else if("diff_stock".equalsIgnoreCase(request.getParameter("page"))) {%>
								 
								<tr>
									<td align="right" width="25%">รหัสร้านค้า</td>
									<td align="left" width="40%">
									    <html:text property="diffStockSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="10" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
									    <html:text property="diffStockSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText"/>
									    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
									</td>
								</tr>
								<tr>
									<td align="right" width="25%">As Of Date</td>
									<td align="left" width="40%"><html:text property="diffStockSummary.asOfDate" styleId="asOfDate" readonly="true"/></td>
								</tr>
								<tr>
									<td align="right" width="25%">Only have qty</td>
									<td align="left" width="40%"><html:checkbox property="diffStockSummary.haveQty" /></td>
								</tr>
						<%}else if("onhand".equalsIgnoreCase(request.getParameter("page"))) {%>
						       <tr>
									<td align="left" width="30%">
									     จาก รหัสสินค้า&nbsp;&nbsp; <html:text property="onhandSummary.itemCodeFrom" styleId="itemCodeFrom"/>
									</td>
									<td align="left" width="40%">
									          ถึง รหัสสินค้า&nbsp;&nbsp; <html:text property="onhandSummary.itemCodeTo" styleId="itemCodeTo"/>
									</td>
								</tr>
								<tr>
									<td align="left" width="30%">
									     Pens Item From&nbsp;&nbsp; <html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom"/>
									     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>
									</td>
									<td align="left" width="40%">
									     Pens Item To&nbsp;&nbsp; <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo"/>
									     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>    
									</td>
								</tr>
								<tr>
									<td align="left" width="30%">Group &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    <html:text property="onhandSummary.group" styleId="group" size="25"/>
									    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
									     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
									  </td>
									<td align="left" width="40%">สถานะ
									   &nbsp;
									   <html:select property="onhandSummary.status">
										<html:option value="SUCCESS">SUCCESS</html:option>
										<html:option value="ERROR">ERROR</html:option>
								     </html:select>
								     Display Zero Stock <html:checkbox property="onhandSummary.dispZeroStock" />
									</td>
								</tr>
								
						<%}else if("onhandLotus".equalsIgnoreCase(request.getParameter("page"))
								|| "onhandBigC".equalsIgnoreCase(request.getParameter("page"))) {
								
							    String storeType ="lotus";
								if("onhandBigC".equalsIgnoreCase(request.getParameter("page"))){
									storeType="bigc";
								}
								%>
						
						        <tr>
									<td align="right">จาก วันที่ขาย (As Of)<font color="red">*</font> <html:text property="onhandSummary.salesDate" styleId="salesDate" readonly="true"/>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
									<td align="left" width="30%">&nbsp;</td>
								</tr>
						       <tr>
									<td align="right">รหัสร้านค้า<font color="red">*</font>
									    <html:text property="onhandSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
									    <input type="button" name="x1" value="..." onclick="openPopupCustomerAll('${pageContext.request.contextPath}','from','<%=storeType%>')"/>
									</td>
									<td align="left" width="30%"> <html:text property="onhandSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="30"/></td>
								</tr>
								<tr>
									<td align="right" width="30%">
									     Pens Item From<html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom"/>
									     &nbsp;
									    <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>
									</td>
									<td align="left" width="30%">
									     Pens Item To&nbsp;&nbsp; <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo"/>
									     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>   
									</td>
								</tr>
								<tr>
									<td align="right" width="30%">Group &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    <html:text property="onhandSummary.group" styleId="group" />
									    &nbsp;
									    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
									     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
									     
									  </td>
									<td align="left" width="30%">&nbsp;</td>
							   </tr>
							   
						<%}else if("onhandLotusPeriod".equalsIgnoreCase(request.getParameter("page"))) {%>
						
						        <tr>
									<td align="right">จาก วันที่ขาย <font color="red">*</font> 
									<html:text property="onhandSummary.asOfDateFrom" styleId="asOfDateFrom" readonly="true"/>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
									<td align="left" width="30%">ถึง วันที่ขาย <font color="red">*</font> 
									<html:text property="onhandSummary.asOfDateTo" styleId="asOfDateTo" readonly="true"/>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
								</tr>
						       <tr>
									<td align="right">รหัสร้านค้า<font color="red">*</font>
									    <html:text property="onhandSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
									    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','lotus')"/>
									</td>
									<td align="left" width="30%"> <html:text property="onhandSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="30"/></td>
								</tr>
								<tr>
									<td align="right" width="30%">
									     Pens Item From<html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom"/>
									     &nbsp;
									    <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>
									</td>
									<td align="left" width="30%">
									     Pens Item To&nbsp;&nbsp; <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo"/>
									     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>   
									</td>
								</tr>
								<tr>
									<td align="right" width="30%">Group &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    <html:text property="onhandSummary.group" styleId="group" />
									    &nbsp;
									    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
									     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
									  </td>
									<td align="left" width="30%">&nbsp;</td>
							   </tr>
						<%} %>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtn"> 
								</a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtn">
								</a>
							</td>
						</tr>
					</table>
					<!-- RESULT -->
				    
			        <c:if test="${summaryForm.onhandSummaryResults != null}">
				        <table align="Left" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="left">
									<b>Data As Of Date :  ${summaryForm.onhandSummary.asOfDate} </b>
								</td>
							</tr>
							<tr>
								<td align="left">
									<b>File Name :${summaryForm.onhandSummary.fileName}</b>
								</td>
							</tr>
						</table>
						<br/>
						<br/>
							<display:table id="item" name="sessionScope.summaryForm.onhandSummaryResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
							    
							    <display:column  title="Group" property="group"  sortable="false" class="group"/>
							    <display:column  title="Item" property="item"  sortable="false" class="item"/>
							    <display:column  title="Description" property="itemDesc"  sortable="false" class="itemDesc"/>	
							    <display:column  title="On-Hand" property="onhandQty"  sortable="false" class="onhandQty"/>	
							    <display:column  title="ราคาขายส่งก่อน VAT" property="wholePriceBF"  sortable="false" class="wholePriceBF"/>	
							    <display:column  title="ราคาขายปลีกก่อน VAT " property="retailPriceBF"  sortable="false" class="retailPriceBF"/>			
							    <display:column  title="Barcode" property="barcode"  sortable="false" class="barcode"/>	
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="pensItem"/>
							    <display:column  title=" Material Master" property="materialMaster"  sortable="false" class="materialMaster"/>	
							    <display:column  title=" Status" property="status"  sortable="false" class="status"/>	
							    <display:column  title=" Message" property="message"  sortable="false" class="message"/>			
							</display:table>
                    </c:if>
                    <c:if test="${summaryForm.onhandSummaryLotusResults != null}">

						<br/>
							<display:table id="item" name="sessionScope.summaryForm.onhandSummaryLotusResults" defaultsort="0" defaultorder="descending" width="100%" class="resultDisp"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="lotus_storeCode"/>
							    <display:column  title="PensItem" property="pensItem"  sortable="false" class="lotus_pensItem"/>
							    <display:column  title="Group" property="group"  sortable="false" class="lotus_group"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="lotus_saleInQty"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="lotus_saleReturnQty"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="lotus_saleOutQty"/>	
							    <display:column  title="Adjust" property="adjustQty"  sortable="false" class="lotus_adjustQty"/>	
							    <display:column  title="Stock short" property="stockShortQty"  sortable="false" class="lotus_stockShortQty"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="lotus_onhandQty"/>	
							    				
							</display:table>
                    </c:if>
                    
                     <c:if test="${summaryForm.onhandBigCResults != null}">

						<br/>
							<display:table id="item" name="sessionScope.summaryForm.onhandBigCResults" defaultsort="0" defaultorder="descending" width="100%" class="resultDisp"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="bigc_storeCode"/>
							    <display:column  title="Sub Inv" property="subInv"  sortable="false" class="bigc_subInv"/>
							    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false" class="bigc_storeName"/>
							    <display:column  title="Group" property="group"  sortable="false" class="bigc_group"/>	
							    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="bigc_pensItem"/>	
							    <display:column  title="Transfer In Qty" property="transInQty"  sortable="false" class="bigc_transInQty"/>	
							    <display:column  title="Return Qty" property="saleReturnQty"  sortable="false" class="bigc_saleReturnQty"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="bigc_saleOutQty"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="bigc_onhandQty"/>	
							    				
							</display:table>
                    </c:if>
                    
                    <c:if test="${summaryForm.onhandSummaryLotusPeriodResults != null}">

						<br/>
							<display:table id="item" name="sessionScope.summaryForm.onhandSummaryLotusPeriodResults" defaultsort="0" defaultorder="descending" width="100%" class="resultDisp"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="false" class="lotus_storeCode"/>
							    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="false" class="lotus_storeName"/>
							    <display:column  title="Group" property="group"  sortable="false" class="lotus_group"/>	
							    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="lotus_saleInQty"/>	
							    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="lotus_saleReturnQty"/>
							    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="lotus_saleOutQty"/>	
							    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="lotus_onhandQty"/>	
							    				
							</display:table>
                    </c:if>
                    
                     <c:if test="${summaryForm.lotusSummaryResults != null}">
						
							<display:table id="item" name="sessionScope.summaryForm.lotusSummaryResults" defaultsort="0" defaultorder="descending"  class="resultTrans"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="30">	
							    
							    <display:column  title="Sales Date" property="salesDate"  sortable="false" class="salesDate"/>
							    <display:column  title="Pens Cust Code" property="pensCustCode"  sortable="false" class="pensCustCode"/>	
							    <display:column  title="Pens Cust Desc" property="pensCustDesc"  sortable="false" class="pensCustDesc"/>	
							    <display:column  title="Store No" property="storeNo"  sortable="false" class="storeNo"/>	
							    <display:column  title="Store Name " property="storeName"  sortable="false" class="storeName"/>			
							    <display:column  title="STYLE NO" property="styleNo"  sortable="false" class="styleNo"/>	
							    <display:column  title="DESCRIPTION" property="description"  sortable="false" class="description"/>	
							    
							    <display:column  title="QTY" property="qty"  sortable="false" class="qty"/>
							    <display:column  title="Pens Group" property="pensGroup"  sortable="false" class="pensGroup"/>	
							    <display:column  title="Pens Group Type" property="pensGroupType"  sortable="false" class="pensGroupType"/>	
							    <display:column  title="Sales Year" property="salesYear"  sortable="false" class="salesYear"/>	
							    <display:column  title="Sales Month" property="salesMonth"  sortable="false" class="salesMonth"/>			
							    <display:column  title="file Name" property="fileName"  sortable="false" class="fileName"/>	
							    <display:column  title="Vendor" property="vendor"  sortable="false" class="vendor"/>
							    <display:column  title="Name" property="name" nowrap="true" sortable="false" class="name"/>	
							    <display:column  title="AP Type" property="apType"  sortable="false" class="apType"/>	
							    <display:column  title="LEASE VENDOR TYPE" property="leaseVendorType"  sortable="false" class="leaseVendorType"/>			
							    <display:column  title="COL" property="col"  sortable="false" class="col"/>	
							    <display:column  title="Size Type" property="sizeType"  sortable="false" class="sizeType"/>	
							    			
							    <display:column  title="SIZE" property="sizes"  sortable="false" class="sizes"/>	
							    <display:column  title="GROSS SALES" property="grossSales"  sortable="false" class="grossSales"/>	
							    <display:column  title="RETURN AMT" property="returnAmt"  sortable="false" class="returnAmt"/>			
							    <display:column  title="NET SALES INCL VAT" property="netSalesInclVat"  sortable="false" class="netSalesInclVat"/>	
							    <display:column  title="VAT AMT" property="vatAmt"  sortable="false" class="vatAmt"/>		
			
							    <display:column  title="NET SALES EXC VAT" property="netSalesExcVat"  sortable="false" class="netSalesExcVat"/>	
							    <display:column  title="GP AMOUNT" property="gpAmount"  sortable="false" class="gpAmount"/>			
							    <display:column  title="VAT ON GP AMOUNT" property="vatOnGpAmount"  sortable="false" class="vatOnGpAmount"/>	
							    <display:column  title="GP AMOUNT INCL VAT" property="gpAmountInclVat"  sortable="false" class="gpAmountInclVat"/>		
			
							    <display:column  title="AP AMOUNT" property="apAmount"  sortable="false" class="apAmount"/>	
							    <display:column  title="TOTAL VAT AMT" property="totalVatAmt"  sortable="false" class="totalVatAmt"/>	
							    <display:column  title="AP AMOUNT INCL VAT" property="apAmountInclVat"  sortable="false" class="apAmountInclVat"/>			
							    <display:column  title="Create date" property="createDate"  sortable="false" class="createDate"/>	
							    <display:column  title="Create by" property="createUser"  sortable="false" class="createUser"/>	
							    	
							</display:table>
                    </c:if>
                    <c:if test="${summaryForm.topsSummaryResults != null}">
						        
						
							<display:table id="item" name="sessionScope.summaryForm.topsSummaryResults" defaultsort="0" defaultorder="descending"  class="resultTrans"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="30">	
							    
							    <display:column  title="Sales Date" property="salesDate" width="10%" sortable="false" class="tops_salesDate"/>
							    <display:column  title="Pens Cust Code" nowrap="false" property="pensCustCode" width="10%" sortable="false" class="tops_pensCustCode"/>	
							    <display:column  title="Pens Cust Desc" property="pensCustDesc" width="20%" sortable="false" class="tops_pensCustDesc"/>	
							    <display:column  title="Pens Group" property="pensGroup" width="10" sortable="false" class="tops_pensGroup"/>	
							    <display:column  title="Pens Group Type" property="pensGroupType"  sortable="false" class="tops_pensGroupType"/>	
                                <display:column  title="Pens Item" property="pensItem"  sortable="false" class="tops_pensItem"/>	
							    <display:column  title="QTY" property="qty"  sortable="false" class="tops_qty"/>
							    <display:column  title="Item" property="item"  sortable="false" class="tops_item"/>	
							    <display:column  title="Item Desc" property="itemDesc"  sortable="false" class="tops_itemDesc"/>	
							    <display:column  title="Branch Name" property="branchName"  sortable="false" class="tops_branchName"/>	
							    <display:column  title="Group No" property="groupNo"  sortable="false" class="tops_groupNo"/>
							    <display:column  title="Group Name" property="groupName"  sortable="false" class="tops_groupName"/>
							    <display:column  title="DEPT" property="dept"  sortable="false" class="tops_dept"/>
							    <display:column  title="Dept Name" property="deptName"  sortable="false" class="tops_deptName"/>
							    <display:column  title="Unit Cost" property="unitCost"  sortable="false" class="tops_unitCost"/>
							    <display:column  title="Retail Price" property="retailPrice"  sortable="false" class="tops_retailPrice"/>
							    <display:column  title="GP PERCENT" property="gpPercent"  sortable="false" class="tops_gpPercent"/>		
							    <display:column  title="NET SALES INCL VAT" property="netSalesInclVat"  sortable="false" class="tops_netSalesInclVat"/>
							    <display:column  title="NET SALES EXC VAT" property="netSalesExcVat"  sortable="false" class="tops_netSalesExcVat"/>
							    <display:column  title="GP AMOUNT" property="gpAmount"  sortable="false" class="tops_gpAmount"/>		
							    <display:column  title="GROSS SALES" property="grossSales"  sortable="false" class="tops_grossSales"/>	
							   	<display:column  title="Discount" property="discount"  sortable="false" class="tops_discount"/>
							   	<display:column  title="CUS RETURN" property="cusReturn"  sortable="false" class="tops_cusReturn"/>
							   	<display:column  title="DISCOUNT CUS RETURN" property="discountCusReturn"  sortable="false" class="tops_discountCusReturn"/>
							   	<display:column  title="NET CUS RETURN" property="netCusReturn"  sortable="false" class="tops_netCusReturn"/>
							   	<display:column  title="COGS" property="cogs"  sortable="false" class="tops_cogs"/>
		
							    <display:column  title="Sales Year" property="salesYear"  sortable="false" class="tops_salesYear"/>	
							    <display:column  title="Sales Month" property="salesMonth"  sortable="false" class="tops_salesMonth"/>			
							    <display:column  title="file Name" property="fileName"  sortable="false" class="tops_fileName"/>				
							    <display:column  title="Create date" property="createDate"  sortable="false" class="tops_createDate"/>	
							    <display:column  title="Create by" property="createUser"  sortable="false" class="tops_createUser"/>	
							    	
							</display:table>
                    </c:if>
                    
                     <c:if test="${summaryForm.bigcSummaryResults != null}">
						
							<display:table id="item" name="sessionScope.summaryForm.bigcSummaryResults" defaultsort="0" defaultorder="descending" class="resultTrans"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="30">	
							    
							    <display:column  title="Sales Date" property="salesDate"  sortable="false" class="salesDate"/>
							    <display:column  title="Pens Cust Code" property="pensCustCode"  sortable="false" class="pensCustCode"/>	
							    <display:column  title="Pens Cust Desc" property="pensCustDesc"  sortable="false" class="pensCustDesc"/>	
							    <display:column  title="Store No" property="storeNo"  sortable="false" class="storeNo"/>	
							    <display:column  title="Store Name " property="storeName"  sortable="false" class="storeName"/>			
							    <display:column  title="STYLE NO" property="styleNo"  sortable="false" class="styleNo"/>	
							    <display:column  title="DESCRIPTION" property="description"  sortable="false" class="description"/>	
							    
							    <display:column  title="QTY" property="qty"  sortable="false" class="qty"/>
							    <display:column  title="Whole Price BF" property="wholePriceBF"  sortable="false" class="totalWholePriceBF"/>	
							    <display:column  title="Retail Price BF" property="retailPriceBF"  sortable="false" class="totalWholePriceBF"/>	
							    <display:column  title="TOTAL Whole Price BF" property="totalWholePriceBF"  sortable="false" class="totalWholePriceBF"/>	
							    <display:column  title="Pens Group" property="pensGroup"  sortable="false" class="pensGroup"/>	
							    <display:column  title="Pens Group Type" property="pensGroupType"  sortable="false" class="pensGroupType"/>	
							    <display:column  title="Sales Year" property="salesYear"  sortable="false" class="salesYear"/>	
							    <display:column  title="Sales Month" property="salesMonth"  sortable="false" class="salesMonth"/>			
							    <display:column  title="file Name" property="fileName"  sortable="false" class="fileName"/>	
							    <display:column  title="Vendor" property="vendor"  sortable="false" class="vendor"/>
							    <display:column  title="Name" property="name" nowrap="true" sortable="false" class="name"/>	

							    <display:column  title="GP Percent" property="gpPercent"  sortable="false" class="gpPercent"/>	
							   
							    <display:column  title="Create date" property="createDate"  sortable="false" class="createDate"/>	
							    <display:column  title="Create by" property="createUser"  sortable="false" class="createUser"/>	
							    	
							</display:table>
                    </c:if>
                    
                     <c:if test="${summaryForm.physicalSummaryResults != null}">
						<br/>
						<br/>
							<display:table id="item" name="sessionScope.summaryForm.physicalSummaryResults" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="30">	
							    
							    <display:column  title="Item" property="item"  sortable="false" class="phy_item"/>
							    <display:column  title="Barcode" property="barcode"  sortable="false" class="phy_barcode"/>	
							    <display:column  title="Cust Code" property="pensCustCode"  sortable="false" class="phy_custCode"/>
							    <display:column  title="Cust Name" property="pensCustName"  sortable="false" class="phy_custName"/>		
							    <display:column  title="Count Date" property="countDate"  sortable="false" class="phy_countDate"/>	
							    <display:column  title="File Name " property="fileName"  sortable="false" class="phy_fileName"/>			
							    <display:column  title="Create Date" property="createDate"  sortable="false" class="phy_createDate"/>	
					
							</display:table>
                    </c:if>
                    
                      <c:if test="${summaryForm.diffStockSummaryLists != null}">
						<br/>
						<br/>
							<display:table id="item" name="sessionScope.summaryForm.diffStockSummaryLists" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="../jsp/summaryAction.do?do=search" sort="list" pagesize="50">	
							    
							    <display:column  title="รหัสร้านค้า" property="custCode"  sortable="false" class="d_custCode"/>
							    <display:column  title="Item" property="item"  sortable="false" class="d_item"/>
							    <display:column  title="Description" property="description"  sortable="false" class="d_description"/>
							    <display:column  title="Order Consign" property="orderConsign"  sortable="false" class="d_orderConsign"/>		
							    <display:column  title="Order From Lotus" property="orderFromLotus"  sortable="false" class="d_orderFromLotus"/>	
							    <display:column  title="Data From Physical " property="dataFromPhysical"  sortable="false" class="d_dataFromPhysical"/>			
							    <display:column  title="Adjust" property="adjust"  sortable="false" class="d_adjust"/>	
					            <display:column  title="Diff" property="diff"  sortable="false" class="d_diff"/>	
							</display:table>
                    </c:if>
                    
					<jsp:include page="../searchCriteria.jsp"></jsp:include>
					
					<!-- hidden field -->
					<input type="hidden" name="page" value="<%=request.getParameter("page") %>"/>
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