<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.web.sales.OrderForm"%>
<%@page import="util.CustomerReceiptFilterUtils"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MAddress"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
String path = request.getContextPath();
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String tripNo = "";
int ind = 0;

boolean isVanSales = User.VAN.equalsIgnoreCase(user.getRole().getKey());
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH"));
String today = sdf.format(new Date());

//wit edit 29/07/2554   Case VanSales shippingDate = requestDate
Calendar cld = Calendar.getInstance();
String reqDate = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH")).format(cld.getTime());

String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}
List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tablesorter.css" />
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
.paging{height:18px;width:100%;padding-left:4px;padding-right:2px;padding-top:2px;}
.catalog{text-align:center;height:60px;width:25%;/*background-color:#FFCC99;*/}
.brandName{width:120px;vertical-align:top;}

table#productList thead{background:#FFE4CA;}
.qtyInput{width:50px; height:26px;text-align:right;}
table#productList tbody td{vertical-align:top;padding-left:2px;padding-right:4px;}
table#productList tbody td.number{text-align:right;}
.h1{
 font-size: 20px;
 font-weight: bold;
}
.bigRadio{ width: 1.5em; height: 1.5em; }

.sale_normal{
  background-color: #00ff40;
  font-weight: bold;
}
.sale_special{
  background-color: #f2f20d	;
  font-weight: bold;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrderProduct.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/lock-scroll.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.scannerdetection.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>

<!-- Test new version jquery -->
<!-- <script type="text/javascript" src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script type="text/javascript" src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
 -->
<script type="text/javascript">
//clear cach
$.ajaxSetup({cache: false});

onkeydown = function(e){
	//alert(e.keyCode+":"+e.type);
	//F1 =112
	//F2 =113
	//F3 =114
	//F4 =115
	//Enter =13
	//Spacebar =32
	  if(e.keyCode == 113){//F2
	     // e.preventDefault();
	     //your saving code
	     setProductType('N')//default sale normal
	     document.getElementById("barcode").focus();
	  }else if(e.keyCode == 115){//F4
		  setProductType('S')//default sale normal
		  document.getElementById("barcode").focus();
	  }else if(e.keyCode == 32){//Spacebar
		 // presave();
	  }
}

/** Auto Detect Barcode Scanner **/
$(document).scannerDetection({  
  //https://github.com/kabachello/jQuery-Scanner-Detection

	timeBeforeScanTest: 200, // wait for the next character for upto 200ms
	avgTimeByChar: 40, // it's not a barcode if a character takes longer than 100ms
	preventDefault: false,

	endChar: [13],
	onComplete: function(barcode, qty){
        validScan = true;
    	$('#barcode').val (barcode);
    	getProductKeypressByBarcodeModel('<%=path%>',$('#barcode').val());
    } // main callback function	,
	,
	onError: function(string, qty) {
	}
});

function presave() {
	if(!createProductList()){return false;}
	document.orderForm.action = "<%=path%>/jsp/saleOrderAction.do?do=preSave";
	document.orderForm.submit();
	return true;
}

function loadMe(){
	//new Epoch('epoch_popup','th',document.getElementById('orderDate'));
	calculatePrice();
	
	setProductType('N')//default sale normal
}

function setProductType(curProductType){
	if(curProductType =='N'){//normal
		document.getElementById("sale_normal").className='sale_normal';
		document.getElementById("sale_special").className='';
		
		var value = 'N';
		$("input[name=productType][value=" + value + "]").attr('checked', 'checked');
	}else{
		//special
		document.getElementById("sale_normal").className='';
		document.getElementById("sale_special").className='sale_special';
		
		var value = 'S';
		$("input[name=productType][value=" + value + "]").attr('checked', 'checked');
	}
}

/** Get Product By Barcode **/
/* 	contentType: "application/json; charset=utf-8", */
function getProductKeypressByBarcode(path,e,barcodeObj){
	var custId = document.getElementById("order.customerId").value;
	var inputQty = document.getElementById("inputQty").value;
	if(e != null && e.keyCode == 13){
		if(barcodeObj.value ==''){
			//no input
			alert("กรุณาระบุ บาร์โค๊ด");
			barcodeObj.focus();
		}else{
			getProductKeypressByBarcodeModel(path,barcodeObj.value)
		}//if
	}
}
function getProductKeypressByBarcodeModel(path,barcode){
	var pricelistId = document.getElementById("order.priceListId").value;
	var custId = document.getElementById("order.customerId").value;
	var inputQty = document.getElementById("inputQty").value;
	var barcodeObj = document.getElementById("barcode");
	var productType = $('input[name=productType]:checked').val();//promotion
	//alert("productType:"+productType);
	
	var result = '';
    var getData = $.ajax({
 		url: path+"/jsp/sales/ajax/getProductByBarcodeAjax.jsp",
		data : "custId="+custId+"&barcode="+barcode+"&pricelistId="+pricelistId+"&inputQty="+inputQty,
		async: false,
		cache: false,
		contentType: "application/json; charset=utf-8", 
		success: function(getData){
			result = jQuery.trim(getData);
			//alert(result);
		}
	 }).responseText; 
    
	if(result ==''){
		alert("ไม่พบข้อมูลสินค้า ");
		barcodeObj.value ="";
		barcodeObj.focus();
	}else{
		setProductType('N')//default sale normal
		
		barcodeObj.value ="";
		barcodeObj.focus();
		//get Product Info By Old method
		var products = [];
		products = eval(result);
		//alert(products);//alert(products.length);//alert(products[0].productId);
		
		//Load data from screen to product screen
		if(products != null){
			for(var i=0; i < products.length ; i++){
				var product = new Object();
				product.productId = products[i].productId;
				product.product = products[i].productCode;
				product.productLabel = decodeURIComponent(escapeParameter(products[i].productName));
				
				product.uom1 = products[i].uom1;
				product.uom2 = products[i].uom2;
				product.uomLabel1 = products[i].uom1;
				product.uomLabel2 = products[i].uom2;
				product.price1 = products[i].price1;
				product.price2 = products[i].price2;
				product.priceAfDiscount = products[i].priceAfDiscount;
				
				//alert(products[i].qty1);
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
				
				product.ship = "<%=reqDate%>";
				product.req = "<%=today%>";
				product.id = "";
				product.row = "";
				
				product.taxable = products[i].taxable;
				product.promotion = productType;
				product.productNonBme = products[i].productNonBme;//add 02/06/2563
				
				addProduct('${pageContext.request.contextPath}', product);
				
				
			}//if
		}//for
		updateScroll();
	}//if
}
/** Scroll to buttom after acsn barcode success */
function updateScroll(){
     var element = document.getElementById("divTableProduct");
     element.scrollTop = element.scrollHeight;
}

function escapeParameter(param){
	return param.replace("%","%25");
}
function openPopupSearch(path,pageName,mutiple){
	var form = document.orderForm;
	var param  = "&pageName="+pageName+"&mutiple="+mutiple;
	    param += "&hideAll=true";
	if("ItemBarcode" == pageName){
		
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.orderForm;
	if("ItemBarcode" == pageName){
		//alert(code+","+desc);
		//form.brand.value = code;
		getProductKeypressByBarcodeModel('${pageContext.request.contextPath}',code);
	}
}
function addInputQty(sign){
	var inputQty = document.getElementById("inputQty");
	if(sign == 1){
	  inputQty.value = parseInt(inputQty.value)+(sign*1);
	}else if(sign == -1){
		if(inputQty.value >1){
			 inputQty.value = parseInt(inputQty.value)+(sign*1);
		}
	}
	document.getElementById("barcode").focus();
}
/** Clear auto Complete */
var pageForm = document.getElementById("orderForm"); 
if(pageForm != null){ 
  pageForm.setAttribute("autocomplete","off"); 
} 

</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" 
onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')"
style="height: 100%;">
<div id="div_body">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    		<jsp:include page="../menu_header.jsp"/>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="SalesOrder"/>
				<jsp:param name="code" value="${orderForm.order.orderNo}"/>
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
						<html:form action="/jsp/saleOrderAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="5" align="center">
								<!-- **** Table Product **** -->
								<div id="divTableProductHead" style="width: 100%; height:50px; overflow-y: scroll;">
								<table id="tblProductHead" align="left" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
									<tr>
										<th class="td_text_center" width="5%">ลำดับ</th>
										<th class="td_text_center" width="5%">
										   <input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" />
										</th>
										<th class="td_text_center" width="25%">ชื่อสินค้า</th>
										<th class="td_text_center" width="5%">หน่วยนับ</th>
										<th class="td_text_center" width="10%">จำนวน</th>
										<th class="td_text_center" width="10%">ราคาต่อหน่วย</th>
										<th class="td_text_center" width="10%">ยอดรวม</th>
										<th class="td_text_center" width="10%">ส่วนลด</th>
										<th class="td_text_center" width="10%">ยอดรวม หลังหักส่วนลด</th>
										<th class="td_text_center" width="5%">ภาษี</th>
										<th class="td_text_center" width="5%">โปรโมชั่น</th>
									</tr>
								</table>
								</div>
								<div id="divTableProduct" style="width: 100%; height:380px; overflow-y: scroll;">
								<table id="tblProduct" align="left" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
									
									<c:forEach var="lines1" items="${orderForm.lines}" varStatus="rows1">
									<c:choose>
										<c:when test="${rows1.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="${tabclass}">
										<td  class="td_text_center" width="5%">${rows1.index + 1}</td>
										<td  class="td_text_center" width="5%"><input type="checkbox" name="lineids" value="${lines1.id}" /></td>
										
										<td class="td_text" width="25%">
											${lines1.product.code}&nbsp;${lines1.product.name}
											<input type="hidden" name='lines.id' value='${lines1.id}'>
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
											<input type='hidden' name='lines.priceAfDiscount' value='${lines1.priceAfDiscount}'>
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
											
											<input type='hidden' name='lines.sellingPrice' value='${lines1.sellingPrice}'>
											<input type='hidden' name='lines.modifierLineId' value='${lines1.modifierLineId}'>
											<input type='hidden' name='lines.productNonBme' value='${lines1.productNonBme}'>
										</td>
										<td class="td_text_center" width="5%">
											<c:choose>
												<c:when test="<%=orderForm.getOrder().getOrderType().equals(User.DD) %>">
													${lines1.uom.code}&nbsp;${lines1.uom1.code}
												</c:when>
												<c:otherwise>
													${lines1.fullUom}	
												</c:otherwise>
											</c:choose>
										</td>
										<td  class="td_text_right" width="10%">
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
										<td  class="td_text_right" width="10%">
											<c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<fmt:formatNumber pattern="#,##0.00000" value="0"/>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>/
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price2}"/>												
												</c:otherwise>
											</c:choose>											
										
										</td>
										<td class="td_text_right" width="10%">
										    <!-- ยอดรวม -->
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
										</td>
										<td class="td_text_right" width="10%">
										    <!-- ส่วนลด -->
											<%-- <fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/> --%>
										</td>
										<td class="td_text_right" width="10%">
										    <!-- ยอดรวม หลังหักส่วนลด -->
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount - lines1.discount}"/>
										</td>
										<td  class="td_text_center" width="5%">
										   <!-- Taxable -->
											<c:if test="${lines1.taxable=='Y'}">
												   <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">								
											</c:if>
										</td>
										<td  class="td_text_center" width="5%">
										  <!-- Promotion -->
										  ${lines1.promotion}
										</td>
									</tr>
									</c:forEach>
								</table>
								</div>
								<hr>
								<!-- **** Table Product **** -->
							 </td>
							</tr>
							<tr><td colspan="5">
							   <table id="tblProductSummary" align="left" border="0" cellpadding="3" 
							   cellspacing="1" width="100%" bgcolor="#D3D3D3">
							      <tr>
							        <td width="10%">
							           <a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}','<%=user.getType() %>');">  
									     <input type="button" value="ลบรายการ" class="newPosBtnLong">
								       </a>
							        </td>
									<td class="td_text_right" width="30%" colspan="4"><span class="h1">รวมจำนวน</span></td>
									<td  class="td_text_right" width="10%">
									   <!-- <input type="text" name="totalQty" id="totalQty" size="10" 
									   class="disableBoldNumber"> -->
									  <b> <span id="totalQty" class="h1"></span></b>
									</td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_right" width="10%"></td>
									<td class="td_text_center" width="5%"></td>
									<td class="td_text_center" width="5%"></td>
								  </tr>
							   </table>
							</td></tr>
							<tr>
							   <td valign="top"></td>
								<td valign="top"  colspan="2">
								    <table align="left" border="0" cellpadding="3" cellspacing="1">
								    <tr>
								      <td align="center"><b>สแกนบาร์โค๊ด</b></td>
								      <td align="center"><b>จำนวน</b></td>
								      <td align="center"></td>
								    </tr>
									<tr>
										<td align="center" nowrap valign="top">&nbsp;
											 <input type="text" id="barcode" name="barcode" class="inputTextSpecial" size="16"
											 autoComplete="off" 
											 onkeypress="getProductKeypressByBarcode('${pageContext.request.contextPath}',event,this)"
											 value ="" autofocus  tabindex="-1"/> 
											 <input type="button" value="..."  class="newNegBtn"
											  onclick ="openPopupSearch('${pageContext.request.contextPath}','ItemBarcode','false')" />
										</td> 
										<td align="center" nowrap valign="top">
										     &nbsp;&nbsp;
										     <input type="button" value="-"onclick ="addInputQty(-1)" class="newNegBtn"/>
											 <input type="text" class="inputNumSpecial" autoComplete="off"  id="inputQty" name="inputQty" size="3" value="1"
											  onkeydown="return inputNum(event);"/> 
											 <input type="button" value="+"onclick ="addInputQty(1)" class="newNegBtn"/>
										</td>
										<td align="center" nowrap valign="bottom">
										    <input type="radio" class="bigRadio" name="productType" id="productType" value="N" checked onchange="setProductType('N')">
										    <span id="sale_normal" class="sale_normal"> <b>ขายปกติ(F2) </b></span>
										    <input type="radio" class="bigRadio" name="productType" id="productType" value="S" onchange="setProductType('S')">
										    <span id="sale_special"> <b>แถมพิเศษ(F4)</b></span>
										</td>
									 </tr>
									  <tr>
									     <td colspan="3" align="right"><hr/> </td>
									  </tr>
									  <tr>
									   <td colspan="3" align="right">
									     <input type="button" value="คำนวณโปรโมชั่น" tabindex="-1" onclick="return presave('${pageContext.request.contextPath}');" class="newPosBtnLong">
									    &nbsp;&nbsp;&nbsp;
									     <input type="button" value="ยกเลิก" tabindex="-1" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									   </td>
									 </tr> 
								   </table>
								</td>
								<td align="left">
								   <%-- <table align="left" border="0" cellpadding="3" cellspacing="1">
								   <tr>
									   <td colspan="2" align="center">
									   
									   </td>
									 </tr>
									 <tr>
									   <td colspan="2" align="center">
									    <input type="button" value="คำนวณโปรโมชั่น" tabindex="-1" onclick="return presave('${pageContext.request.contextPath}');" class="newPosBtnLong">
									    <input type="button" value="ยกเลิก" tabindex="-1" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									   </td>
									 </tr>
								   </table>  --%>
								</td>
								<td align="left" valign="top">
								  <table align="left" border="0" cellpadding="3" cellspacing="1">
									    <tr>
									      <td align="right"><b>ยอดรวมก่อนภาษี</b></td>
									      <td align="right">
									        <input type="text" id="tempTotalAmount" name="tempTotalAmount" size="21" readonly="readonly" class="disableBoldText" style="text-align: right;" tabindex="-1"/>
										     <html:hidden property="order.totalAmount"/>
									      </td>
									    </tr>
									    <tr>
									       <td align="right"><b>ภาษี</b></td>
									       <td align="right">
									         <input type="text" id="tempVatAmount" name="tempVatAmount" size="21" readonly="readonly" class="disableBoldText" style="text-align: right;" tabindex="-1"/>
									         <html:hidden property="order.vatAmount"/>
									        </td>
									    </tr>
									    <tr>
									       <td align="right"><b><font size ="3">ยอดสุทธิ</font></b></td>
									       <td align="right">
									          <input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" 
									          class="disableBoldBigBlueText" 
									          style="text-align: right;" tabindex="-1" size="17"/>
									           <html:hidden property="order.netAmount"/>
									           
									           <!-- calc vat -->
									           <input type="hidden" id="tempTotalAmountNonVat" name="tempTotalAmountNonVat"/>
									           <html:hidden property="order.totalAmountNonVat" />
									       </td>
									    </tr>
								   </table>
								</td>
							</tr>
						</table>
						<br>
						<span title="SalesOrder">...</span>
						<!-- AUTO RECEIPT -->
						<html:hidden property="autoReceiptFlag"/>
						<html:hidden property="autoReceipt.paymentMethod"/>
						<html:hidden property="autoReceipt.bank"/>
						<html:hidden property="autoReceipt.chequeNo"/>
						<html:hidden property="autoReceipt.chequeDate"/>
						<html:hidden property="autoReceipt.creditCardType"/>
						<html:hidden property="autoReceipt.internalBank"/>
						
						<!--  -->
						<html:hidden property="deletedId"/>
						<html:hidden property="order.orderType"/>
						<html:hidden property="order.id"/>
						<html:hidden property="order.roundTrip"/>
						<html:hidden property="order.customerId"/>
						<html:hidden property="order.customerName"/>
						<html:hidden property="order.exported" value="N"/>
						<html:hidden property="order.isCash" value="N"/>
					   
					    <html:hidden property="order.shipAddressId" value="0"/>
						<html:hidden property="order.billAddressId" value="0"/>
							  
						<input type="hidden" name="memberVIP" value="${memberVIP}"/>
						
						<!--  Can Receipt Credit (VAN)-->
						<html:hidden property="receiptCreditFlag"/>
					    <html:hidden property="custCreditLimit"/>
					    
					    <html:hidden property="order.customerBillName"/>
					    <html:hidden property="order.addressDesc"/>
					    <html:hidden property="order.idNo"/>
					    <html:hidden property="order.passportNo"/>
					    
					    <html:hidden property="order.paymentMethod"/>
					    <html:hidden property="order.creditCardType"/>
					    <html:hidden property="order.creditCardNo"/>
					    <html:hidden property="order.creditCardExpireDate"/>
                        <html:hidden property="order.creditcardMonthExpire"/>
					    <html:hidden property="order.creditcardYearExpire"/>
					    
					    <html:hidden property="order.priceListId" styleId="order.priceListId"/>
						<html:hidden property="order.vatCode" value="7"/>
						<html:hidden property="order.customerId" styleId="order.customerId" /> 
						<html:hidden property="order.paymentTerm"/>
						<html:hidden property="order.oraBillAddressID"/>
						<html:hidden property="order.oraShipAddressID"/>
						<html:hidden property="order.orderNo"/>
						<html:hidden property="order.orderDate" styleId="orderDate" />
						
						<!-- Case Check Item W1,W2 -->
						<html:hidden property="order.placeOfBilled"/>
						
						<div id="productList" style="display: none;"></div>
						<div id="ByList" style="display: none;"></div>
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
</div>
</body>
</html>
<div id="error-dialog"><div id="error-dialog-message" style="color:red;"></div></div>
