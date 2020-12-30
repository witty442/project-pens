<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.isecinc.pens.bean.SalesrepBean"%>
<%@page import="com.isecinc.pens.bean.OrderNissin"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<jsp:useBean id="orderNissinForm" class="com.isecinc.pens.web.ordernissin.OrderNissinForm" scope="request" />
<%
OrderNissin order = orderNissinForm.getBean();
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String fromPage = Utils.isNull(request.getParameter("fromPage"));

List<SalesrepBean> salesrepCodeListAll = new ArrayList<SalesrepBean>();
salesrepCodeListAll.add(new SalesrepBean());
salesrepCodeListAll.addAll(order.getSalesrepCodeList());
pageContext.setAttribute("salesrepCodeList", salesrepCodeListAll, PageContext.PAGE_SCOPE);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}

-->
 
.currPage{ border:1px solid #000000; padding-left:4px;padding-right:4px;padding-top:2px; }
.pageLink{padding-left:4px;padding-right:2px;padding-top:2px; }
.paging{height:18px;width:100%;}
.catalog{text-align:center;height:60px;width:25%;/*background-color:#FFCC99;*/}
.brandName{width:120px;vertical-align:top;}

table#productList thead{background:#FFE4CA;}
.qtyInput{width:50px; height:26px;text-align:right;}
table#productList tbody td{vertical-align:top;padding-left:2px;padding-right:4px;}
table#productList tbody td.number{text-align:right;}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/lock-scroll.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/orderNissin.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />

<script type="text/javascript">
//clear cach
$.ajaxSetup({cache: false});

function loadMe(){
}

function openProductCategory(){
	  // CAll AJAX PAGE
	  loadProductCat(0);
	  
	  $(document).ready(function() {
	    $("#brand-dialog").dialog("open");	    
	  });
}

var currPage = 0;
function loadProductCat(page){
	var path = document.getElementById("path").value;
	//alert("loadProductCat");
	currPage = page;
	var custId = 9;//document.getElementById("order.customerId").value;
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/orderNissin/nissinProductSubBrandQuery.jsp",
			data : "pageId=" +page+"&custId="+custId,
			async: false,
			success: function(getData){
				var htmlText = jQuery.trim(getData);
				document.getElementById('brand-dialog').innerHTML = htmlText;
			}
		}).responseText;
	});
}

function loadProductsBySubBrandObj(subBrandCodeObj){
	loadProductsBySubBrand(subBrandCodeObj.value);
}
function loadProductsBySubBrand(subBrandCode){
	//alert("brandCode:"+brandCode.value);
	var path = document.getElementById("path").value;
	//alert("subBrandCode:"+subBrandCode);
	
	if(subBrandCode != ""){
		var orderDate ="";// document.getElementById("orderDate").value;
		var pricelistId = 0;
		var custId = 0;//document.getElementById("order.customerId").value;
		
		$(function(){
			var getData = $.ajax({
				url: path+"/jsp/orderNissin/nissinProductBySubBrand.jsp",
				data : "subBrandCode=" +subBrandCode+"&orderDate="+orderDate+"&pricelistId="+pricelistId+"&custId=" +custId ,
				async: false,
				success: function(getData){
					var htmlText = jQuery.trim(getData);
					//alert(htmlText);
					document.getElementById('selectProduct').innerHTML = htmlText;
				}
			}).responseText;
		});
		
		$(document).ready(function() {
		    $("#selectProduct").dialog("open");
		});
	}
}

//set Cannot Dragg
$.ui.dialog.prototype._makeDraggable = function() { 
    this.uiDialog.draggable({
        containment: true
    });
};

function brandDialogClose(){
	$('#brand-dialog').dialog("close"); 
	unlockScreen();
}

function productDialogClose(){
	$('#selectProduct').dialog("close"); 	
	unlockScreen();
}

$(function(){
	var screen_height= $(window).height();
	var screen_width = $(window).width();
	
	//alert(screen.height+":"+screen.width);

	    $('#brand-dialog').dialog({
						autoOpen: false,
						modal:true,
						width: screen_width-10,
						height:screen_height-20,
						title:"เลือกกลุ่มสินค้า",
						position:'center',
						resizable: false,
						dialogClass: 'brandDialog',
						buttons: {
							 "OK.":  function() { addProductToSalesOrder(); } , 
							"Cancel.": 
								function() { 
								   $(this).dialog("close"); 
								   unlockScreen();
							     } 
							}
					});
	    
	   var btns = " <div align='center'>"
	               +" <input onclick='addProductToSalesOrder()' class ='newPosBtn' type='button' name='ok' value='           OK.           ' />"
		           +" <input onclick='brandDialogClose()' class ='newPosBtn' type='button' name='ok' value='        Cancel.        ' />"
	               +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
	               +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>";
	    
	    $(".brandDialog").children(".ui-dialog-titlebar").append(btns);

    
	  $('#selectProduct').dialog({
			autoOpen: false,
			width: screen_width-10,
			height:screen_height-20,
			modal:true,
			resizable: false,
			dialogClass: 'productDialog',
			position:'fixed',
			title:"กำหนดรายการสินค้าที่ต้องการ",
			buttons: {
				"OK.": function() {  addProductToBasket(); }, 
				"Cancel.": function() { 
					$(this).dialog("close");
				}
			}
		});
			
	  var btns = " <div align='center'>"
          +" <input onclick='addProductToBasket()' class ='newPosBtn' type='button' name='ok' value='           OK.           ' />"
          +" <input onclick='productDialogClose()' class ='newPosBtn' type='button' name='ok' value='        Cancel.        ' />"
          +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>";

      $(".productDialog").children(".ui-dialog-titlebar").append(btns);
      
      //error-dailog_message
	    $('#error-dialog').dialog({
						autoOpen: false,
						modal:true,
						width: 300,
						height:80,
						title:"Error Message",
						position:'center',
						resizable: false,
						dialogClass: 'brandDialog',
						buttons: {"ปิด.": 
								function() { 
								   $(this).dialog("close"); 
								   unlockScreen();
							     } 
							}
					});
});

</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../headerSP.jsp"/></td>
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
	    	<%if("customerNissin".equalsIgnoreCase(fromPage)){ %>
		      	<jsp:include page="../program.jsp">
					<jsp:param name="function" value="OrderNissin"/>
					<jsp:param name="code" value=""/>
				</jsp:include>
			<%}else{ %>
			    <%if ( UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS_PENS}) ){%>
				   <jsp:include page="../program.jsp">
						<jsp:param name="function" value="OrderNissinPens"/>
						<jsp:param name="code" value=""/>
					</jsp:include>
					
				<%}else{ %>
				   <jsp:include page="../program.jsp">
						<jsp:param name="function" value="OrderNissin"/>
						<jsp:param name="code" value=""/>
					</jsp:include>
					
				<%} %>
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
						<html:form action="/jsp/orderNissinAction">
						<jsp:include page="../error.jsp"/>
		
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="4" align="center">
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
								   <tr>
										<td align="right" width="20%">Order ID</td>
										<td align="left"  width="15%">
										   <html:text property="bean.id" styleId="orderId" size="15" readonly="true" styleClass="disableText"/>
										</td>
										<td align="right"  width="15%">
										      วันที่บันทึกรายการ
										</td>
										<td align="left"  width="15%">
										     <html:text property="bean.orderDate" styleId="orderDate" size="15" readonly="true" styleClass="disableText"/>
										</td>
										<td align="right"  width="15%" nowrap>สถานะ</td>
										<td align="left"  width="20%">
										     <html:text property="bean.docStatus" styleId="docStatus" size="15" readonly="true" styleClass="disableText"/>
										</td>
									</tr>
									 <tr>
										<td align="right" width="20%">ชื่อร้านค้า</td>
										<td align="left"  width="15%" colspan="3">
										   <html:text property="bean.customerNis.name" styleId="customerName" size="80" readonly="true" styleClass="disableText"/>
										</td>
										<td align="right"  width="10%">
										      Ref Cust ID
										</td>
										<td align="left"  width="15%">
										     <html:text property="bean.customerNis.id" styleId="customerId" size="15" readonly="true" styleClass="disableText"/>
										</td>
									</tr>
									 <tr>
										<td align="right" width="20%">เลขที่/ซอย</td>
										<td align="left"  width="15%">
										   <html:text property="bean.customerNis.addressLine1" styleId="addressLine1" size="15" readonly="true" styleClass="disableText"/>
										</td>
										<td align="right"  width="15%">
										      ถนน
										</td>
										<td align="left"  width="10%">
										     <html:text property="bean.customerNis.addressLine2" styleId="addressLine2" size="15" readonly="true" styleClass="disableText"/>
										</td>
										<td align="right"  width="15%" nowrap>แขวง/ตำบล</td>
										<td align="left"  width="20%">
										     <html:text property="bean.customerNis.addressLine3" styleId="addressLine3" size="15" readonly="true" styleClass="disableText"/>
										</td>
									</tr>
									<tr>
										<td align="right" width="20%">จังหวัด</td>
										<td align="left"  width="15%">
										   <html:text property="bean.customerNis.provinceName" styleId="provinceName" size="15" readonly="true" styleClass="disableText"/>
										   
										</td>
										<td align="right"  width="10%">
										     อำเภอ
										</td>
										<td align="left"  width="15%">
										     <html:text property="bean.customerNis.districtName" styleId="districtName" size="15" readonly="true" styleClass="disableText"/>
										     <html:hidden property="bean.customerNis.id"/>
										     <html:hidden property="bean.customerNis.provinceId"/>
										     <html:hidden property="bean.customerNis.districtId"/>
										</td>
										<td align="right"  width="15%" nowrap></td>
										<td align="left"  width="20%"></td>
									</tr>
									<tr>
										<td align="right" width="20%">Note หมายเหตุเพิ่มเติม</td>
										<td align="left"  width="35%" colspan="3">
										   <%if(UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS}) &&  orderNissinForm.getBean().isCanEdit()){%>
										      <html:text property="bean.remark" styleId="remark" size="80"  styleClass="\" autoComplete=\"off"  />
										   <%}else{ %>
										       <html:text property="bean.remark" styleId="remark" size="80"  styleClass="disableText" readonly="true" />
										   <%} %>
										</td>
										<td align="right"  width="15%" nowrap>ผู้บันทึก Order</td>
										<td align="left"  width="20%"><html:text property="bean.nisCreateUser" styleId="nisCreateUser" size="15" readonly="true" styleClass="disableText"/></td>
									</tr>
									
									<!-- For Pens Key Invoice  -->
									<%if(UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS_PENS,user.NIS_VIEW})
										|| "VAN".equalsIgnoreCase(role)){
									%>
									    <tr>
										   <td align="right" colspan="6"><hr></td>
										</tr>
										 <%if(  !Utils.isNull(orderNissinForm.getBean().getSalesrepCode()).equals("")){%>
												<tr>
												   <td align="left" colspan="6">
												   <font color="green" size="3"><b>บันทึก Sales Code ไปแล้ว  
												           วันที่ทำรายการ :<bean:write name="orderNissinForm" property="bean.pensActionDate"/>
												     </b></font>
												   </td>
												</tr>
										  <%}else{%>
											    <tr>
												   <td align="left" colspan="6">
												   <font color="red" size="3"><b>ยังไม่บันทึก Sales Code</b></font>
												   </td>
												</tr>
										 <% } %>
										<tr>
										 <td align="right" width="20%">Sales Code<font color="red">*</font></td>
										 <td align="left"  width="15%">
										   <%if(order.getSalesrepCodeList().size()>1){ %>
										      <html:select property="bean.salesrepCode" styleId="salesrepCode" >
										          <html:options collection="salesrepCodeList" property="code" labelProperty="code"/>
										      </html:select>
										   <%}else{ %>
											   <html:text property="bean.salesrepCode" styleId="salesrepCode" 
											    size="15"  styleClass="<%=order.getReadonlyStyle() %>" readonly="<%=order.getReadonlyText() %>"
											    onblur="getAutoOnblur(event,this,'SALESREP_SA')"/>
										   <%} %>
										   <html:hidden property="bean.salesrepId" styleId="salesrepId"/>
										 </td>
										 <td align="right"  width="10%">
										   Order No (4 ตัว)
										 </td>
										 <td align="left"  width="15%" nowrap>
										     <html:text property="bean.orderNo" styleId="orderNo" size="10" 
										      styleClass="<%=order.getReadonlyOrderNoStyle() %>" readonly="<%=order.getReadonlyOrderNoText() %>"/>
										 </td>
										 <td align="right"  width="35%" colspan="2" nowrap>
										  Invoice No<font color="red"></font>
										    <html:text property="bean.invoiceNo" styleId="invoiceNo" size="15" 
										     styleClass="<%=order.getReadonlyStyle() %>" readonly="<%=order.getReadonlyText() %>"/>
										     <input type="button" name="btn1" value="Check Invoice" 
										    onclick="getAutoOnclick('INVOICE_SA')" class="newPosBtnLong"/>
										    &nbsp;&nbsp;&nbsp;
										   Invoice Date  <html:text property="bean.invoiceDate" styleId="invoiceDate" size="10" readonly="true" styleClass="disableText"/>
									
										 </td>
										 
									  </tr>
										<tr>
										 <td align="right" width="20%">รหัสร้านค้า</td>
										 <td align="left"  width="15%">
										   <html:text property="bean.oraCustomerCode" styleId="oraCustomerCode" size="15" readonly="true" styleClass="disableText"/>
										   <html:hidden property="bean.oraCustomerId" styleId="oraCustomerId"/>
										 </td>
										 <td align="right"  width="10%">
										    ชื่อร้านค้า
										 </td>
										 <td align="left"  width="65%" colspan="3">
										     <html:text property="bean.oraCustomerName" styleId="oraCustomerName" size="50" readonly="true" styleClass="disableText"/>
										 </td>
									  </tr>
									  <tr>
										 <td align="right" width="20%" nowrap>เปลี่ยนสถานะเป็น PENDING</td>
										 <td align="left"  width="15%">
										   <html:select property="bean.pendingStatus" disabled="<%=order.getReadonlyText() %>">
										     <html:option value=""></html:option>
										     <html:option value="PENDING">PENDING</html:option>
										   </html:select>
										 </td>
										 <td align="left"  width="75%" colspan="4">
										         สาเหตุ
										     <html:text property="bean.reason" styleId="reason" size="70" 
										      styleClass="<%=order.getReadonlyStyle() %>" readonly="<%=order.getReadonlyText() %>"/>
										 </td>
									  </tr>
									   <tr>
									     <td align="right" width="20%" nowrap valign="top">PENS Note</td>
										 <td align="left"  width="80%" colspan="5">
										     <html:textarea property="bean.pensNote" cols="100" rows="5"
										     styleClass="normalText" >
										     </html:textarea>
										 </td>
									  </tr>
									<%}else{ %>
									 <!-- NISSIN Sales Display -->
									    <tr>
										   <td align="right" colspan="6"><hr></td>
										</tr>
										
										<tr>
										 <td align="right" width="20%">Sales Code</td>
										 <td align="left"  width="15%">
											   <html:text property="bean.salesrepCode" styleId="salesrepCode" 
											    size="15"  styleClass="disableText" readonly="true"/>
										  
										   <html:hidden property="bean.salesrepId" styleId="salesrepId"/>
										 </td>
										 <td align="left"  width="60%" colspan="4" nowrap>
										   Invoice Date  <html:text property="bean.invoiceDate" styleId="invoiceDate" size="10" readonly="true" styleClass="disableText"/>
									
										 </td>
									  <tr>
										<td align="right" width="20%" nowrap>เปลี่ยนสถานะเป็น PENDING</td>
										 <td align="left"  width="15%">
										   <html:select property="bean.pendingStatus" disabled="true">
										     <html:option value=""></html:option>
										     <html:option value="PENDING">PENDING</html:option>
										   </html:select>
										 </td>
										 <td align="left"  width="75%" colspan="4">
										        สาเหตุ
										     <html:text property="bean.reason" styleId="reason" size="70" 
										      styleClass="disableText" readonly="true"/>
										 </td>
									  </tr>
									 <tr>
									   <td align="right" width="20%" nowrap valign="top">PENS Note</td>
										 <td align="left"  width="80%" colspan="5">
										     <html:textarea property="bean.pensNote" cols="100" rows="5"
										     styleClass="disableText" readonly="true">
										     </html:textarea>
										 </td>
									  </tr>
									<%} %>
									</table>
							     </td>
							</tr>
							<tr>
								<td colspan="4" align="center">
								<div align="left" style="margin-left:13px;">
								    <c:if test="${orderNissinForm.bean.canEdit =='true'}">
								       <%if(UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS})){%>
								        <input type="button" value="เลือกสินค้า " class="newPosBtn" onclick="openProductCategory();" />
								       <%} %>
								     </c:if>
								</div>
								
								<!--  Results  -->
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
									<tr>
										<th rowspan="2">No.</th>
										<th rowspan="2"><input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" /></th>
										<th rowspan="2">รหัสสินค้า</th>
										<th rowspan="2">ชื่อสินค้า</th>
										<th colspan="2">จำนวนที่สั่งซื้อ</th>
									</tr>
									<tr>
									  <th >จำนวน</th>
									  <th >หน่วยบรรจุ</th>
									</tr>
									<c:forEach var="lines1" items="${orderNissinForm.lines}" varStatus="rows1">
									<c:choose>
										<c:when test="${rows1.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="${tabclass}">
										<td class ="td_text_center" width="5%">${rows1.index + 1}</td>
										<td class ="td_text_center" width="5%">
										   <input type="checkbox" name="lineids" value="${lines1.id}" />
										</td>
										<td class ="td_text_center" width="7%">${lines1.product.code}</td>
										<td class ="td_text" width="20%">
										     ${lines1.product.name}
											<input type="hidden" name='lines.id' value='${lines1.id}'>
											<input type='hidden' name='lines.lineNo' value='${lines1.lineNo}'>
											<input type='hidden' name='lines.row' value='${lines1.lineNo}'>
											<input type='hidden' name='lines.productId' value='${lines1.product.id}'>
											<input type='hidden' name='lines.product' value='${lines1.product.code}'>
											<input type='hidden' name='lines.productLabel' value='${lines1.product.name}'>
											
											<input type='hidden' name='lines.uom' value='${lines1.uom.id}'>
											<input type='hidden' name='lines.uom1' value='${lines1.uom1.id}'>
											<input type='hidden' name='lines.uom2' value='${lines1.uom2.id}'>
											<input type='hidden' name='lines.uomLabel' value='${lines1.uom.code}'>
											<input type='hidden' name='lines.uomLabel1' value='${lines1.uom1.code}'>
											<input type='hidden' name='lines.uomLabel2' value='${lines1.uom2.code}'>
											<input type='hidden' name='lines.fullUom' value='${lines1.fullUom}'>
											<input type='hidden' name='lines.price' value='${lines1.price}'>
											<input type='hidden' name='lines.price1' value='${lines1.price1}'>
											<input type='hidden' name='lines.price2' value='${lines1.price2}'>
											
											<input type='hidden' name='lines.qty' value='${lines1.qty}'>
											<input type='hidden' name='lines.qty1' value='${lines1.qty1}'>
											<input type='hidden' name='lines.qty2' value='${lines1.qty2}'>
											
											<input type="hidden" name='lines.amount' value='${lines1.lineAmount}'>
											<input type="hidden" name='lines.amount1' value='${lines1.lineAmount1}'>
											<input type="hidden" name='lines.amount2' value='${lines1.lineAmount2}'>
											<input type='hidden' name='lines.disc' value='${lines1.discount}'>
											<input type='hidden' name='lines.disc1' value='${lines1.discount1}'>
											<input type='hidden' name='lines.disc2' value='${lines1.discount2}'>
											
											<input type='hidden' name='lines.afdisc'>
											<input type='hidden' name='lines.afdisc1'>
											<input type='hidden' name='lines.afdisc2'>
											<input type="hidden" name='lines.vat' value='${lines1.vatAmount}'>
											<input type='hidden' name='lines.vat1' value='${lines1.vatAmount1}'>
											<input type='hidden' name='lines.vat2' value='${lines1.vatAmount2}'>
											<input type='hidden' name='lines.total' value='${lines1.totalAmount}'>
											<input type='hidden' name='lines.total1' value='${lines1.totalAmount1}'>
											<input type='hidden' name='lines.total2' value='${lines1.totalAmount2}'>
											<input type='hidden' name='lines.ship' value='${lines1.shippingDate}'>
											
											<input type='hidden' name='lines.req' value='${lines1.requestDate}'>
											<input type='hidden' name='lines.promo' value='${lines1.promotion}'>
											<input type='hidden' name='lines.lineno' value='${lines1.lineNo}'>
											<input type='hidden' name='lines.tripno' value='${lines1.tripNo}'>
											<input type='hidden' name='lines.taxable' value='${lines1.taxable}'>
										</td>
										<td class="td_text_center" width="10%">
										   <fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>
										    /
										    <fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>	
										</td>
                                        <td class="td_text_center" width="10%">
										    ${lines1.uom1.code}	/ ${lines1.uom2.code}	
										</td>
									</tr>
								   </c:forEach>
								  
								</table>
								<!--  Results -->

								<!-- Delete Button -->
								  <%if ( UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS}) ){%>
									<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
										<tr>
											<td align="left" class="footer">&nbsp;	
											   <c:if test="${orderNissinForm.bean.canEdit =='true'}">
												  <a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}','<%=user.getType() %>');"> 
												  <img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
												  <bean:message key="Delete" bundle="sysprop"/></a>
											  </c:if>
											</td>
										</tr>
									</table>
								<%} %>
								</td>
							</tr>
				
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								   <c:if test="${orderNissinForm.bean.canEdit =='true'}">
								   
								     <%if(UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS}) ){%>
									    <a href="#" onclick="return saveByNissin('${pageContext.request.contextPath}');">
										  <input type="button" value="บันทึกรายการ" class="newPosBtnLong">
										 </a>
								     <%}else if(UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS_PENS}) ){%>
										<a href="#" onclick="return saveByPens('${pageContext.request.contextPath}');">
										  <input type="button" value="บันทึกรายการ" class="newPosBtnLong">
										</a>
									<!-- Van Input 4 digit last OrderNo -->
									  <%}else if("VAN".equalsIgnoreCase(role)){ %>	
									    <a href="#" onclick="return saveByPens('${pageContext.request.contextPath}');">
										  <input type="button" value="บันทึกรายการ" class="newPosBtnLong">
										 </a>
									 <%} %>
								   </c:if>	
								    <a href="#" onclick="backSearch('${pageContext.request.contextPath}');">
									  <input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong">
								    </a>
								</td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
						<html:hidden property="bean.priceListId" styleId="bean.priceListId"/>
						<html:hidden property="deletedId"/>
						<html:hidden property="lineNoDeleteArray"/>
						<html:hidden property="fromPage" styleId="fromPage"/>
						
						<div id="productList" style="display: none;"></div>
						<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
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

<div id="brand-dialog"></div>
<div id="selectProduct" >No Product To Display!</div>
<div id="error-dialog"><div id="error-dialog-message" style="color:red;"></div></div>

 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->