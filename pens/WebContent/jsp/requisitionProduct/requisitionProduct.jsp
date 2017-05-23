<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.bean.RecFor"%>
<%@page import="com.isecinc.pens.model.MRecFor"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.bean.PD"%>
<%@page import="com.isecinc.pens.model.MPD"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="requisitionProductderForm" class="com.isecinc.pens.web.requisitionProduct.RequisitionProductForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String userName = user.getUserName();

List<RecFor> reasonList = MRecFor.getReasonList();
pageContext.setAttribute("reasonList",reasonList,PageContext.PAGE_SCOPE);

%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tablesorter.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/requisitionProduct.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">

function loadMe(){
	//alert("loadMe");
	calculatePrice();	
	new Epoch('epoch_popup','th',document.getElementById('requestDate'));
}

function openProductCategory(){
	  // CAll AJAX PAGE
	  loadProductCat(0);
	  
	  lockScreen();
	  
	  $(document).ready(function() {
	    $("#brand-dialog").dialog("open");	    
	  });
}

var currPage = 0;
function loadProductCat(page){
	//alert("loadProductCat");
	currPage = page;
	var custId = 0;//document.getElementById("order.customerId").value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/requisitionProduct/requisitionProductProductCatQuery.jsp",
			data : "pageId=" +page+"&custId="+custId,
			async: false,
			success: function(getData){
				var htmlText = jQuery.trim(getData);
				document.getElementById('brand-dialog').innerHTML = htmlText;
			}
		}).responseText;
	});
}

function loadProducts(brandCode){
	//alert("branchCode:"+brandCode);
	
	var orderDate ="";// document.getElementById("orderDate").value;
	var pricelistId = document.getElementById("requisitionProduct.priceListId").value;
	var custId = 0;//document.getElementById("order.customerId").value;
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/requisitionProduct/requisitionProductProductByBrand.jsp",
			data : "brandCode=" +brandCode+"&orderDate="+orderDate+"&pricelistId="+pricelistId+"&custId=" +custId ,
			async: false,
			success: function(getData){
				var htmlText = jQuery.trim(getData);
				document.getElementById('selectProduct').innerHTML = htmlText;
			}
		}).responseText;
	});
	
	$(document).ready(function() {
	    $("#selectProduct").dialog("open");
	});
}

function lockScreen() {
	$('#div_body').hide();
	
	 /*  $.blockUI({ 
		overlayCSS: { backgroundColor: '#00f' },
	    message: ''
	});   */
	  
	 // disable_scroll();
}

function unlockScreen() {
	// $.unblockUI({backgroundColor: '#00f' }); 
	 //enable_scroll();
	$('#div_body').show();
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
	
	 $('#brand-dialog').dialog({
			autoOpen: false,
			width: screen_width-10,
			height:screen_height-20,
			title:"เลือกกลุ่มสินค้า",
			position:'center',
			dialogClass: 'brandDialog',
			resizable: false,
			buttons: {
				"OK": function() { addProductToSalesOrder(); }, 
				"Cancel": function() { 
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
			title:"กำหนดรายการสินค้าที่ต้องการ",
			dialogClass: 'productDialog',
			buttons: {
				"OK": function() { addProductToBasket(); }, 
				"Cancel": function() { 
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
});

function addProductToSalesOrder(){
	//alert("addProductToSalesOrder!");
	unlockScreen();
	
	var data = '';
	var custId = 0;//document.getElementById("order.customerId").value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/requisitionProduct/requisitionProductAddProductToSO.jsp",
			contentType: "application/json; charset=utf-8",
			data : "custId=" +custId,
			async: false,
			success: function(getData){
				data = jQuery.trim(getData);
				//alert(data);
			}
		}).responseText;
	});

	var products = [];
	products = eval(data);
	if(products != null){
		for(var i=0; i < products.length ; i++){
			//var p = new Object();
			var product = new Object();
			//product.lineNo = products[i].lineNo;
			
			product.productId = products[i].productId;
			product.product = products[i].productCode;
	//		alert(products[i].productName);
			product.productLabel = decodeURIComponent(escapeParameter(products[i].productName));
			//alert(products[i].productName);
			
			product.uom1 = products[i].uom1;
			product.uom2 = products[i].uom2;
			product.uomLabel1 = products[i].uom1;
			product.uomLabel2 = products[i].uom2;
			product.price1 = products[i].price1;
			product.price2 = products[i].price2;
			product.qty1 = products[i].qty1;
			product.qty2 = products[i].qty2;
			
			product.amount1 = products[i].amount1;
			product.amount2 = products[i].amount2;
			product.disc1 = 0;
			product.disc2 = 0;
			product.total1 = products[i].amount1;
			product.total2 = products[i].amount2;
	
			product.vat1 = "";
			product.vat2 = "";
			
			product.ship = "<%//reqDate%>";
			product.req = "<%//today%>";
			product.id = "";
			product.row = "";
			
			addProduct('${pageContext.request.contextPath}', product);
		}
	}
	
	$(document).ready(function() {
	    $("#brand-dialog").dialog("close");
	});
}

function addProductToBasket(){
	var productIds = document.getElementsByName("productId");
	var productNames = document.getElementsByName("productName");
	var productCodes = document.getElementsByName("productCode");
	var uom1s = document.getElementsByName("uom1");
	var uom2s = document.getElementsByName("uom2");
	var qty1s = document.getElementsByName("qty1");
	var qty2s = document.getElementsByName("qty2");
	var price1s = document.getElementsByName("price1");
	var price2s = document.getElementsByName("price2");
	var lineAmts = document.getElementsByName("totalLineAmt");
	var custId = 0;// document.getElementById("order.customerId").value;

	var categoryCode = document.getElementById("categoryCode").value;
	var selected = false;

	for(var i =0;i < lineAmts.length; i++){
		if(!selected && Number(lineAmts[i].value) > 0 )
			selected = true;
		
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/requisitionProduct/requisitionProductAddProductToBasket.jsp",
				data : "custId=" +custId
					  +"&pId="+productIds[i].value
					  +"&pCode="+productCodes[i].value 
					  +"&pName="+escapeParameter(productNames[i].value)
					  +"&uom1="+uom1s[i].value
					  +"&uom2="+uom2s[i].value
					  +"&price1="+price1s[i].value
					  +"&price2="+price2s[i].value
					  +"&qty1="+qty1s[i].value
					  +"&qty2="+qty2s[i].value
					  +"&lineAmt="+lineAmts[i].value,
				async: false,
				success: function(getData){
					var status = jQuery.trim(getData);
					if(status != "")
						alert(status);
				}
			}).responseText;
		});
	}

	// Set Background 
	if(selected){
		$("#"+categoryCode).css('background-color', '#FFFF99');
	}
	else{
		$("#"+categoryCode).css('background-color', '');
	}

	$(document).ready(function() {
	    $("#selectProduct").dialog("close");
	}); 	
}

function escapeParameter(param){
	return param.replace("%","%25");
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
				<jsp:param name="function" value="RequisitionProduct"/>
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/requisitionProductAction">
						<jsp:include page="../error.jsp"/>
		
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							
							<tr>
								<td colspan="4" align="center">
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
								   <tr>
										<td align="right" width="20%">วันที่ปัจจุบัน</td>
										<td align="left"  width="10%">
										   <html:text property="requisitionProduct.currentDate" size="15" readonly="true" styleClass="disableText"/>
										</td>
										<td align="right"  width="23%">
										         เลขที่เอกสาร&nbsp;&nbsp;
										</td>
										<td align="left"  width="27%" nowrap> 
										   <html:text property="requisitionProduct.requestNumber" size="20"   readonly="true" styleClass="disableText"/>
										</td>
										
									</tr>
									<tr>
										<td align="right" width="20%"> 
										      วันที่ทำรายการ<font color="red">*</font>
										</td>
										<td align="left"  width="10%">
										  <html:text property="requisitionProduct.requestDate" maxlength="10" size="15" readonly="true" styleId="requestDate" disabled="${requisitionProductForm.requisitionProduct.requestDateDisabled}"/>
										</td>
										<td align="right"  width="23%">
										       หน่วยรถ
										</td>
										<td align="left"  width="27%" nowrap>
											<html:text property="requisitionProduct.salesCode" size="15"  readonly="true"  styleClass="disableText"/> 
										</td>
										
									</tr>
									<tr>
										<td align="right"  width="20%">
											  สถานะ
										</td>
										<td align="left"  width="10%">
										     <html:text property="requisitionProduct.statusLabel" size="30" readonly="true"  styleId="statusLabel" styleClass="disableText"/>
										</td>
										<td align="right"  width="23%" >
										      สถานะการส่งข้อมูล
										</td>
										<td align="left"  width="27%" >
										  <html:text property="requisitionProduct.exportedLabel" size="18" readonly="true"  styleId="exportedLabel" styleClass="disableText"/>
										</td>	
									</tr>
									
									 <tr>
										<td align="right"  width="20%">
										สินค้าเบิกเพื่อ
										</td>
										<td align="left"  width="10%"> 
										     <html:select property="requisitionProduct.reasonCode">
										     <html:options collection="reasonList" property="lookupCode" labelProperty="description"/>
									       </html:select>
										</td>
										<td align="right"  width="22%" >เหตุผลในการยกเลิก </td>
										<td align="left"  width="28%" >
										   <html:text property="requisitionProduct.cancelReason" size="30" readonly="true"  styleId="cancelReason" styleClass="disableText"/>
										</td>
									</tr>
									
									<tr>
										<td align="right"  width="20%">หมายเหตุ </td>
										<td align="left"  width="10%"> 
										  <html:text property="requisitionProduct.remark" size="30"  styleId="remark" />
										</td>
										<td align="right"  width="22%" ></td>
										<td align="left"  width="28%" >
										  
										</td>
									</tr>
									</table>
							     </td>
							  </tr>
							  
							<tr>
								<td colspan="4" align="center">
								<div align="left" style="margin-left:13px;">
								   <c:if test="${requisitionProductForm.requisitionProduct.canEdit =='true'}">
								     <input type="button" class="newPosBtn" value="เลือกสินค้าใหม่ " onclick="openProductCategory();" />
								      &nbsp;&nbsp;
								    	<input type="button" class="newPosBtn" value="เลือกสินค้าของแถม" onclick="open_product_premium('${pageContext.request.contextPath}');"/>
								   </c:if>
								</div>
								
								<!--  Results  -->
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="moveOrder">
									<tr>
										<th class="moveOrder.no">No.</th>
										<th class="moveOrder.chk"><input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" /></th>
										<th class="moveOrder.productName">รหัสสินค้า</th>
										<th class="moveOrder.uom">หน่วยนับ</th>
										<th class="moveOrder.qty">จำนวน</th>
										<th class="moveOrder.amount">ยอดเงิน</th>
										<th class="moveOrder.edit">แก้ไข</th>
									</tr>
									<c:forEach var="lines1" items="${requisitionProductForm.lines}" varStatus="rows1">
									<c:choose>
										<c:when test="${rows1.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="${tabclass}">
										<td align="center" class = "moveOrder.no">${rows1.index + 1}</td>
										<td align="center" class = "moveOrder.chk"><input type="checkbox" name="lineids" value="${lines1.id}" /></td>
										<td align="left" class ="moveOrder.productName">
										${lines1.product.code}&nbsp;${lines1.product.name}
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
											<input type="hidden" name='lines.amount' value='${lines1.amount}'>
											<input type="hidden" name='lines.amount1' value='${lines1.amount1}'>
											<input type="hidden" name='lines.amount2' value='${lines1.amount2}'>
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
											
										</td>
										<td align="right" class="moveOrder.uom">
										    ${lines1.fullUom}	
										</td>
										<td align="right" class="moveOrder.qty">
										   <c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<c:choose>
														<c:when test="${(lines1.product.uom.id==lines1.uom1.id && lines1.product.uom.id==lines1.uom2.id) || (lines1.product.uom.id!=lines1.uom1.id && lines1.product.uom.id!=lines1.uom2.id)}">
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty1 + lines1.qty2}"/>
														</c:when>
														<c:otherwise>
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
															<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>/
													<fmt:formatNumber pattern="#,##0" value="${lines1.qty2}"/>												
												</c:otherwise>
											</c:choose>
										</td>

										<td align="right" class="moveOrder.amount"><fmt:formatNumber pattern="#,##0.00" value="${lines1.totalAmount}"/></td>
										<td align="right" class="moveOrder.edit">
											<c:if test="${moveOrderForm.moveOrder.showSaveBtn =='true'}">
												   <c:if test="${moveOrderForm.moveOrder.canEdit =='true'}">
													<a href="#" onclick="open_product('${pageContext.request.contextPath}','${rows1.index+1}');">
			                                           <img border=0 src='${pageContext.request.contextPath}/icons/doc_edit.gif'>
			                                        </a>
			                                      </c:if>
		                                     </c:if>
										</td>
									</tr>
								   </c:forEach>
								  
								</table>
								<!--  Results -->

								<!-- Delete Button -->
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<td align="left" class="footer">&nbsp;	
										   <c:if test="${requisitionProductForm.requisitionProduct.canEdit =='true'}">
											<a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}','<%=user.getType() %>');"> 
											<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
											<bean:message key="Delete" bundle="sysprop"/></a>
										  </c:if>
										</td>
									</tr>
								</table>
								
								<!-- Total Sum -->
								<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%" >
								    <tr>
								  	    <td  width="73%" >&nbsp;</td>
										<td  align="right">&nbsp;</td>
										<td  align="left">&nbsp;</td>
										<td class="status">&nbsp;</td>
									</tr>
								   <tr>
									    <td  width="50%" >&nbsp;</td>
										<td  align="right" width="23%"><b>ยอดรวมทั้งสิ้น</b></td>
										<td  align="left">
									         <html:text property="requisitionProduct.totalAmount"  size="20"  styleId="totalAmount" styleClass="disableText" style="text-align: right;"/>
										</td>
										<td class="status">&nbsp;</td>
									</tr>
								</table>
							    <!--  Total Sum -->
								</td>
							</tr>
				
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								<c:if test="${requisitionProductForm.requisitionProduct.showSaveBtn =='true'}">
								   <c:if test="${requisitionProductForm.requisitionProduct.canEdit =='true'}">
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									  <input type="button" value="บันทึกรายการ" class="newPosBtnLong">
									</a>	
								   </c:if>	
								</c:if>		
								
                                <c:if test="${requisitionProductForm.requisitionProduct.showCancelBtn =='true'}">
                                   <c:if test="${requisitionProductForm.requisitionProduct.canEdit =='true'}">
										<a href="#" onclick="cancelRequisitionProduct('${pageContext.request.contextPath}');"">
										  <input type="button" value="ยกเลิกรายการ"  class="newPosBtnLong">
									    </a>
								   </c:if>
								</c:if>
								
								<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									  <input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong">
								    </a>
								</td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
						<html:hidden property="requisitionProduct.priceListId" styleId="requisitionProduct.priceListId"/>
						<html:hidden property="deletedId"/>
						<html:hidden property="lineNoDeleteArray"/>
			
						<div id="productList" style="display: none;"></div>
						
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
<Script>
	<%if("true".equals(Utils.isNull(request.getAttribute("popupPrint")))){ %>
	   popupPrint('${pageContext.request.contextPath}','${requisitionProductForm.requisitionProduct.requestNumber}');
	<%} %>
</Script>
<div id="brand-dialog">No Product Catalog To Display!</div>
<div id="selectProduct">No Product To Display!</div>