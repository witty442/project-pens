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
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String tripNo = "";
int ind = 0;

boolean isVanSales = User.VAN.equalsIgnoreCase(user.getRole().getKey());
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH"));
String today = sdf.format(new Date());

//wit edit 29/07/2554   Case VanSales shippingDate = requestDate
Calendar cld = Calendar.getInstance();
if( !"VAN".equals(user.getType()) ){
   cld.add(Calendar.DAY_OF_MONTH,3); // +3 Day
}

String reqDate = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH")).format(cld.getTime());

String action = (String)request.getParameter("action");
if(action == null){
	action = "";
}

List<Address> custAddr = new ArrayList<Address>();
custAddr = new MAddress().lookUp(orderForm.getOrder().getCustomerId());

List<Address> billAddr = new ArrayList<Address>();
List<Address> shipAddr = new ArrayList<Address>();

for(Address address:custAddr){
	if("Y".equals(address.getIsActive()))
	{
		if("B".equalsIgnoreCase(address.getPurpose()))
			billAddr.add(address);
		else if("S".equalsIgnoreCase(address.getPurpose()))
			shipAddr.add(address);
	}
}

pageContext.setAttribute("billAddr",billAddr,PageContext.PAGE_SCOPE);
pageContext.setAttribute("shipAddr",shipAddr,PageContext.PAGE_SCOPE);

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes",vatcodes,PageContext.PAGE_SCOPE);

List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);
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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
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
.catalog{text-align:center;/*background-color:#FFCC99;*/}
.brandName{width:120px;vertical-align:top;}

table#productList thead{background:#FFE4CA;}
.qtyInput{width:35px; text-align:right;}
table#productList tbody td{vertical-align:top;padding-left:2px;padding-right:4px;}
table#productList tbody td.number{text-align:right;}

/*
thead.fixedHeader tr {
	position: relative
}

html>body thead.fixedHeader tr {
	display: block
}

html>body tbody.scrollContent {
	display: block;
	height: 210px;
	overflow: auto;
	width: 100%
}
 */
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tablesorter.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>

<script type="text/javascript">
//clear cach
$.ajaxSetup({cache: false});

function openProductCategory(){
	  // CAll AJAX PAGE
	  loadProductCat(0);
	  
	  $(document).ready(function() {
	    $("#brand-dialog").dialog("open");	    
	  });
}

var currPage = 0;
function loadProductCat(page){
	currPage = page;
	var custId = document.getElementById("order.customerId").value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/productCatQuery.jsp",
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
	var orderDate = document.getElementById("orderDate").value;
	var pricelistId = document.getElementById("order.priceListId").value;
	var custId = document.getElementById("order.customerId").value;
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/productByBrand.jsp",
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

function loadMe(){
	calculatePrice();	
	new Epoch('epoch_popup','th',document.getElementById('orderDate'));
}

$(function(){
	  $('#brand-dialog').dialog({
						autoOpen: false,
						width: 550,
						height:425,
						title:"เลือกกลุ่มสินค้า",
						buttons: {
							"OK": function() { addProductToSalesOrder(); }, 
							"Cancel": function() { 
								$(this).dialog("close"); 
							}
						}
					});

	  $('#selectProduct').dialog({
			autoOpen: false,
			width: 770,
			height : 380,
			modal:true,
			title:"กำหนดรายการสินค้าที่ต้องการ",
			buttons: {
				"OK": function() { addProductToBasket(); }, 
				"Cancel": function() { 
					$(this).dialog("close");
				}
			}
		});
});

function addProductToSalesOrder(){
	//alert("addProductToSalesOrder!");
	var data = '';
	var custId = document.getElementById("order.customerId").value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/addProductToSO.jsp",
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
	//alert(products);
	//alert(products.length);
	//alert(products[0].amount1);
	for(var i=0; i < products.length ; i++){
		//var p = new Object();
		var product = new Object();
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
		
		product.ship = "<%=reqDate%>";
		product.req = "<%=today%>";
		product.id = "";
		product.row = "";
		
		addProduct('${pageContext.request.contextPath}', product);
	}

	$(document).ready(function() {
	    $("#brand-dialog").dialog("close");
	});
}

function addProductToBasket(){
	//alert("addProductToBasket");
	
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
	var custId = document.getElementById("order.customerId").value;

	var categoryCode = document.getElementById("categoryCode").value;
	var selected = false;

	for(var i =0;i < lineAmts.length; i++){
		if(!selected && Number(lineAmts[i].value) > 0 )
			selected = true;
		
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/addProductToBasket.jsp",
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
								<td width="30%"></td>
								<td width="25%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left"></td>
								<td align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:text property="order.orderDate" maxlength="10" size="15" readonly="true" styleId="orderDate" />
								</td>
							</tr>
							<tr>
								<td align="right">
									<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
									<bean:message key="Customer" bundle="sysele"/>&nbsp;&nbsp;
									<%}else{ %>
									<bean:message key="Member" bundle="sysele"/>&nbsp;&nbsp;
									<%} %>
								</td>
								<td align="left" colspan="3">
									<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
									<html:text property="order.customerName" size="80" readonly="true" styleClass="disableText"/>
									<%}else{ %>
									<html:text property="order.customerName" size="80"  />
									<%} %>
									<html:hidden property="order.customerId" styleId="order.customerId" />
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryAddress" bundle="sysele"/><font color="red">*</font></td>
								<td align="left" colspan="3">
									<html:select property="order.shipAddressId" style="width:80%">
										<html:options collection="shipAddr" property="id" labelProperty="lineString"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.DeliveryDocAddress" bundle="sysele"/><font color="red">*</font></td>
								<td align="left" colspan="3">
									<html:select property="order.billAddressId" style="width:80%">
										<html:options collection="billAddr" property="id" labelProperty="lineString"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Profile.CreditTerm" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="order.paymentTerm" disabled="true" styleClass="disableText">
										<html:options collection="paymentTerm" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Profile.TaxRate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="order.vatCode" disabled="true" onchange="calculatePrice();" styleClass="disableText">
										<html:options collection="vatcodes" property="key" labelProperty="name"/>
									</html:select>
									
									<html:hidden property="order.priceListId" styleId="order.priceListId"/>
									<html:hidden property="order.vatCode"/>
									<html:hidden property="order.paymentTerm"/>
									<html:hidden property="order.paymentMethod"/>
									<html:hidden property="order.oraBillAddressID"/>
									<html:hidden property="order.oraShipAddressID"/>
								</td>
							<%if(role.equalsIgnoreCase(User.DD)) {%>
							<tr>
								<td align="right"><bean:message key="Condition.ShipmentDay" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:hidden property="order.shippingDay" />
									<html:select property="order.shippingDay" disabled="true" styleClass="disableText">
										<html:option value="Mon"><bean:message key="Monday" bundle="sysele" /></html:option>
										<html:option value="Tue"><bean:message key="Tueday" bundle="sysele" /></html:option>
										<html:option value="Wed"><bean:message key="Wednesday" bundle="sysele" /></html:option>
										<html:option value="Thu"><bean:message key="Thursday" bundle="sysele" /></html:option>
										<html:option value="Fri"><bean:message key="Friday" bundle="sysele" /></html:option>
										<html:option value="Sat"><bean:message key="Saturday" bundle="sysele" /></html:option>
									</html:select>
								</td>
								<td align="right"><bean:message key="Condition.ShipmentTime" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.shippingTime" size="5" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<%} %>
							<tr>
								<td colspan="4" align="center">
								<div align="left">
									&nbsp;&nbsp;
									<%if(!role.equalsIgnoreCase(User.DD)) {%>
									<input type="button" value="เพิ่มสินค้า" onclick="open_product('${pageContext.request.contextPath}');"/>
									&nbsp;&nbsp;
									<input type="button" value="เลือกสินค้าใหม่ " onclick="openProductCategory();" />
									<%} %>
									<%if(role.equalsIgnoreCase(User.DD)) {%>
										<c:if test="${memberVIP=='Y'}">
										<input type="button" value="เพิ่มสินค้า" onclick="open_product('${pageContext.request.contextPath}');"/>
										</c:if>
										<c:if test="${memberVIP=='N'}">
										<input type="button" value="สร้างรายการอัตโนมัติ" onclick="add_auto_transaction('${pageContext.request.contextPath}', 'find', $('#order.customer.id').val());"/>
										</c:if>
									<%} %>
								</div>
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<th class="order"><bean:message key="No" bundle="sysprop"/></th>
										<%if(!role.equalsIgnoreCase(User.DD)) {%>
										<th class="checkBox"><input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" /></th>
										<%}else{ %>
										<c:if test="${memberVIP=='N'}">
										<th class="checkBox" style="display: none;"><input type="checkbox" name="chkAll"
											onclick="checkSelect(this,document.getElementsByName('lineids'));" /></th>
										</c:if>
										<c:if test="${memberVIP=='Y'}">
										<th class="checkBox">
											<input type="checkbox" name="chkAll" onclick="checkSelect(this,document.getElementsByName('lineids'));" />
										</th>
										</c:if>
										<%} %>
										<th class="name"><bean:message key="Product.Name" bundle="sysele"/></th>
										<th><bean:message key="Product.UOM" bundle="sysele"/></th>
										<th><bean:message key="Quantity" bundle="sysele"/></th>
										<th class="costprice"><bean:message key="Price.Unit" bundle="sysele"/></th>
										<th><bean:message key="Total" bundle="sysele"/></th>
										<th class="costprice"><bean:message key="Discount" bundle="sysele"/></th>
										<th class="costprice"><bean:message key="TotalExcludeDiscount" bundle="sysele"/></th>
<!--										<th class="costprice"><bean:message key="Tax" bundle="sysele"/></th>-->
<!--										<th class="costprice"><bean:message key="Overall" bundle="sysele"/></th>-->
										<%if(user.getType().equalsIgnoreCase(User.DD)){ %>
										<th><bean:message key="Member.Time" bundle="sysele"/></th>
										<%} %>
										<th><bean:message key="Order.ShipmentDate" bundle="sysele"/></th>
										<%if(user.getType().equalsIgnoreCase(User.DD)){ %>
										<th><bean:message key="Order.ReceiveDate" bundle="sysele"/></th>
										<%} %>
										<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
										<th><bean:message key="Order.RequiredDate" bundle="sysele"/></th>
										<%} %>
										<th class="status"><bean:message key="Edit" bundle="sysprop"/></th>
									</tr>
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
										<td>${rows1.index + 1}</td>
										<%if(!role.equalsIgnoreCase(User.DD)) {%>
											<td align="center"><input type="checkbox" name="lineids" value="${lines1.id}" /></td>
										<%}else{ %>
											<c:if test="${memberVIP=='N'}">
											<td align="center" style="display: none;"><input type="checkbox" name="lineids" value="${lines1.id}" /></td>
											</c:if>
											<c:if test="${memberVIP=='Y'}">
											<td align="center"><input type="checkbox" name="lineids" value="${lines1.id}" /></td>
											</c:if>
										<%} %>
										<td align="left">
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
										</td>
										<td align="center">
											<c:choose>
												<c:when test="<%=orderForm.getOrder().getOrderType().equals(User.DD) %>">
													${lines1.uom.code}&nbsp;${lines1.uom1.code}
												</c:when>
												<c:otherwise>
													${lines1.fullUom}	
												</c:otherwise>
											</c:choose>
										</td>
										<td align="right">
											<%if(user.getType().equals(User.DD)){ %>
												<c:choose>
													<c:when test="${lines1.qty==0}">
														<fmt:formatNumber pattern="#,##0" value="${lines1.qty1}"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber pattern="#,##0" value="${lines1.qty}"/>
													</c:otherwise>
												</c:choose>
											<%}else{ %>
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
											<%} %>
										</td>
										<td align="right">
											<%if(user.getType().equals(User.DD)){ %>
												<c:choose>
													<c:when test="${lines1.price==0}">
														<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price}"/>
													</c:otherwise>
												</c:choose>
											<%}else{ %>
											<c:choose>
												<c:when test="${lines1.promotion=='Y'}">
													<fmt:formatNumber pattern="#,##0.00000" value="0"/>
												</c:when>
												<c:otherwise>
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price1}"/>/
													<fmt:formatNumber pattern="#,##0.00000" value="${lines1.price2}"/>												
												</c:otherwise>
											</c:choose>											
											<%} %>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
										</td>
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/>
										</td>
									
										<td align="right">
											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount - lines1.discount}"/>
										</td>
<!--										<td align="right">-->
<!--											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.vatAmount}"/>-->
<!--										</td>-->
<!--										<td align="right">-->
<!--											<fmt:formatNumber pattern="#,##0.00000" value="${lines1.totalAmount}"/>-->
<!--										</td>-->
										
										<%if(user.getType().equalsIgnoreCase(User.DD)){ %>
										<td align="center">${lines1.tripNo}</td>
										<%} %>
										<td align="center">${lines1.shippingDate}</td>
										<td align="center">${lines1.requestDate}</td>
										<td align="center">
											<%if(((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
											<c:if test="${memberVIP=='Y'}">
												<c:if test="${lines1.promotion=='N'}">
													<a href="#" onclick="open_product('${pageContext.request.contextPath}',${rows1.index+1});">
													<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
												</c:if>
											</c:if>
											<c:if test="${memberVIP=='N'}">
												<%if(!tripNo.equals(orderForm.getLines().get(ind).getTripNo()+"")){ %>
													<a href="#" onclick="edit_shipdate('${pageContext.request.contextPath}','${lines1.shippingDate}','${rows1.index + 1}');">
													<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
													<%tripNo = orderForm.getLines().get(ind).getTripNo()+""; %>	
												<% }ind++;%>
											</c:if>
											<%} else {%>
												<c:if test="${lines1.promotion=='N'}">
													<a href="#" onclick="open_product('${pageContext.request.contextPath}',${rows1.index+1});">
													<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
												</c:if>
											<%} %>
										</td>
									</tr>
									</c:forEach>
								</table>
								<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
									<tr>
										<td align="left" class="footer">&nbsp;
											<%if(!role.equalsIgnoreCase(User.DD)) {%>
												<a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}','<%=user.getType() %>');"> 
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
												<bean:message key="Delete" bundle="sysprop"/></a>
											<%} %>
											<%if(role.equalsIgnoreCase(User.DD)) {%>
											<c:if test="${memberVIP=='Y'}">
												<a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}','<%=user.getType() %>');"> 
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
												<bean:message key="Delete" bundle="sysprop"/></a>
											</c:if>
											<%} %>
										</td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="TotalAmount" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempTotalAmount" name="tempTotalAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.totalAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right">
									<bean:message key="Tax" bundle="sysele"/>
									&nbsp;&nbsp;
									</td>
								<td align="left">
									<input type="text" id="tempVatAmount" name="tempVatAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.vatAmount"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="Net" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="tempNetAmount" name="tempNetAmount" readonly="readonly" class="disableText" style="text-align: right;"/>
									<html:hidden property="order.netAmount"/>
								</td>
							</tr>
							<tr>
								<td colspan="4"><hr/></td>
							</tr>
							<tr>
								<td></td>
								<td class="textSpecial"><%if(User.VAN.equals(user.getType())){%><html:checkbox property="order.paymentCashNow"/> บันทึกรับเงินสดทันที <%} %></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.payment" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Order.Paid" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td valign="top">
									<html:checkbox property="order.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesOrderNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="Bill.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.arInvoiceNo" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Order.SalesRepresent"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
									<html:hidden property="order.salesRepresent.id"/>
									<html:hidden property="order.salesRepresent.code"/>
								</td>
								<td align="right"><bean:message key="Status" bundle="sysele"/><font color="red">*</font></td>
								<td valign="top">
									<html:select property="order.docStatus">
										<html:options collection="docstatus" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<%if(!user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
									<input type="button" value="คำนวณโปรโมชั่น" onclick="return presave('${pageContext.request.contextPath}');" class="newPosBtnLong">
									<%} %>
									<%if(user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
									<a href="#" onclick="return autoReceipt('${pageContext.request.contextPath}','<%=user.getType() %>');">
									<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
									<%} %>
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
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
						<html:hidden property="order.exported" value="N"/>
						<html:hidden property="order.isCash" value="N"/>
						<input type="hidden" name="memberVIP" value="${memberVIP}"/>
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
</body>
</html>
<div id="brand-dialog">No Product Catalog To Display!</div>
<div id="selectProduct">No Product To Display!</div>