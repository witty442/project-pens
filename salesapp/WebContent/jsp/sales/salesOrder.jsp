<!-- HTML 5 -->
<!-- <!DOCTYPE html> -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.web.sales.OrderForm"%>
<%@page import="com.pens.util.CustomerReceiptFilterUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
  // cld.add(Calendar.DAY_OF_MONTH,3); // +3 Day
  
  //wit edit 04/09/2561
  cld.add(Calendar.DAY_OF_MONTH,2); // +2 Day
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

System.out.println("custAddr size:"+custAddr.size());

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

List<References> vanPaymentMethod = InitialReferences.getReferenes().get(InitialReferences.VAN_PAYMENT_METHOD);
pageContext.setAttribute("vanPaymentMethod",vanPaymentMethod,PageContext.PAGE_SCOPE);

//Filter Can Receipt More Cash //1:can, 0: cannot, -1:no pay prev bill
OrderForm orderFrom = null;
String receiptCreditFlag = "0";
if(request.getAttribute("orderForm") != null){
  orderFrom = (OrderForm)request.getAttribute("orderForm");
  receiptCreditFlag = Utils.isNull(orderFrom.getReceiptCreditFlag());
}

System.out.println("receiptCreditFlag:"+receiptCreditFlag);
boolean debugMode = session.getAttribute("debug_mode")!= null?((Boolean)session.getAttribute("debug_mode")):false;
System.out.println("debugMode:"+debugMode);
%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
 <meta http-equiv="X-UA-Compatible" content="IE=edge" />
 <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
 <meta name="description" content="" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<%-- <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
 --%>
<%-- <link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
 --%>
<style type="text/css">

.currPage{ border:1px solid #000000; padding-left:4px;padding-right:4px;padding-top:2px; }
.pageLink{padding-left:4px;padding-right:2px;padding-top:2px; }
.paging{height:18px;width:100%;padding-left:4px;padding-right:2px;padding-top:2px;}
.catalog{text-align:center;height:60px;width:25%;/*background-color:#FFCC99;*/}
.brandName{width:120px;vertical-align:top;}

table#productList thead{background:#FFE4CA;}
.qtyInput{width:50px; height:26px;text-align:right;}
table#productList tbody td{vertical-align:top;padding-left:2px;padding-right:4px;}
table#productList tbody td.number{text-align:right;}


</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>

<!-- Include Bootstrap Resource  -->
<jsp:include page="../resourceBootstrap.jsp"  flush="true"/>
<!-- /Include Bootstrap Resource -->

<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pages/salesOrderProduct.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
 
 <style>
 
 .modal-dialog-full-width {
        width: 100% !important;
        height: 100% !important;
        margin: 0 !important;
        padding: 0 !important;
        max-width:none !important;
    }

    .modal-content-full-width  {
        height: auto !important;
        min-height: 100% !important;
        border-radius: 0 !important;
        background-color: #ececec !important 
    }

    .modal-header-full-width  {
        border-bottom: 1px solid #9ea2a2 !important;
    }

    .modal-footer-full-width  {
        border-top: 1px solid #9ea2a2 !important;
    }
 </style>
<script type="text/javascript">
//clear cach
$.ajaxSetup({cache: false});

function openProductCategory(){
	  // CAll AJAX PAGE
	  loadProductCategory(0);

	  //open modal
	  //lock screen
	  $("#brand-dialog").modal({backdrop: 'static', keyboard: true,show:true}) ; 
	  
      //$("#brand-dialog").modal('show') ; 
}

var currPage = 0;
function loadProductCategory(page){
	currPage = page;
	var custId = document.getElementById("order.customerId").value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/productCatQuery.jsp",
			data : "pageId=" +page+"&custId="+custId,
			async: false,
			success: function(getData){
				var htmlText = jQuery.trim(getData);
				
				//alert(htmlText);
				document.getElementById('brand-dialog-content').innerHTML = htmlText;
			}
		}).responseText;
	});
}

function loadProductsByBrand(brandCode){
	var brandCodeVal = brandCode;
	if(brandCode.value == '[object HTMLSelectElement]'){
		brandCodeVal=brandCode.value;
	}
	//alert("brandCode:"+brandCode+","+brandCode.value+","+brandCodeVal);
	
	if(brandCodeVal != ""){
		var orderDate = document.getElementById("orderDate").value;
		var pricelistId = document.getElementById("order.priceListId").value;
		var custId = document.getElementById("order.customerId").value;
		
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/productByBrand.jsp",
				data : "brandCode=" +brandCodeVal+"&orderDate="+orderDate+"&pricelistId="+pricelistId+"&custId=" +custId ,
				async: false,
				success: function(getData){
					var htmlText = jQuery.trim(getData);
					document.getElementById('product-dialog-content').innerHTML = htmlText;
				}
			}).responseText;
		});
		
		//open product modal
	    $("#product-dialog").modal({backdrop: 'static', keyboard: true,show:true}) ; 
	}
}
function imgError(source){
    source.src = '${pageContext.request.contextPath}/images/img_not_found.jpg';
    source.onerror = "";
    return true;
}
function loadMe(){
	calculatePrice();	
	//new Epoch('epoch_popup','th',document.getElementById('orderDate'));
}

function validateVanPaymentMethod(){
	var r = true;
	var vanPaymentMethod = document.getElementsByName("order.vanPaymentMethod")[0];
	if(vanPaymentMethod.value =='CREDIT'){
		<%if(receiptCreditFlag.equalsIgnoreCase("0")){%>
		     //alert("ร้านค้านี้ ยังไม่ได้เปิดสิทธิ์ ให้ขายเชื่อ ได้ กรุณาแจ้งทางผู้จัดการหรือซุปเปอร์ ให้ทำเรื่องเปิดสิทธิ์การขายเชื่อ");
			 
			 $("#error-dialog").dialog("open");
		     jQuery("#error-dialog-message").text("ร้านค้านี้ ยังไม่ได้เปิดสิทธิ์ ให้ขายเชื่อ ได้ กรุณาแจ้งทางผู้จัดการหรือซุปเปอร์ ให้ทำเรื่องเปิดสิทธิ์การขายเชื่อ");
		     r = false;
		<%}if(receiptCreditFlag.equalsIgnoreCase("-1")){%>
		    //alert("ร้านค้านี้ ยังมีบิลเก่า ที่ยังไม่ได้เก็บเงิน  ต้องเก็บเงินบิลเก่าก่อน จึงสามารถเปิดบิลขายเชื่อใหม่");
		   
		    $("#error-dialog").dialog("open");	    
		    jQuery("#error-dialog-message").text("ร้านค้านี้ ยังมีบิลเก่า ที่ยังไม่ได้เก็บเงิน  ต้องเก็บเงินบิลเก่าก่อน จึงสามารถเปิดบิลขายเชื่อใหม่");
	        r = false;
		<%}%>
	}
	return r;
}
function validateVanCreditLimit(){
	var r = true;
	var vanPaymentMethod = document.getElementsByName("order.vanPaymentMethod")[0];
	var custCreditLimit = document.getElementsByName("custCreditLimit")[0];
	var netAmount = document.getElementsByName("order.netAmount")[0];
	if(vanPaymentMethod.value =='CREDIT'){
		//alert("creditLimit["+custCreditLimit.value+"]:netAmount["+netAmount.value+"]") ;
		if(parseFloat(netAmount.value) > parseFloat(custCreditLimit.value)){
		   //alert("creditLimit["+parseFloat(custCreditLimit.value)+"]:netAmount["+parseFloat(netAmount.value)+"]") ;
		   $("#error-dialog").dialog("open");
		   jQuery("#error-dialog-message").text("ยอดเงินที่ขาย เกินวงเงินที่กำหนด ไม่สามารถทำรายการขายได้ วงเงินที่ขายได้ของร้านนี้ ("+custCreditLimit.value+")");
		   r = false;
		}
	}
	return r;
}
//calc line price by product line
function linePrice(rowNo,price1,price2){
	var qty1 = document.getElementsByName("qty1")[rowNo].value;
	var qty2 = document.getElementsByName("qty2")[rowNo].value;
	 
	var totalAmtText = document.getElementsByName("totalLineAmtT")[rowNo];
	var totalAmt = document.getElementsByName("totalLineAmt")[rowNo];

	if(qty1 == '' && qty2 == ''){
		totalAmt.value = "";	
		totalAmtText.innerHTML = "";
		return ;
	}
	
	if(qty1 == null)
		qty1 = 0;

	if(qty2 == null)
		qty2 = 0;

	var amt1 = (qty1 * price1);
	var amt2 = (qty2 * price2);

	var amt = amt1 + amt2;
	totalAmt.value = (amt.toFixed(5));	
	totalAmtText.innerHTML = addCommas(amt.toFixed(2));
}
</script>
</head>
<body  class="sb-nav-fixed" onload="loadMe();">

   <!-- Include Header Mobile  -->
     <jsp:include page="../header.jsp"  flush="true"/>
    <!-- /Include Header Mobile -->
       
     <!-- Nav Text -->
     <jsp:include page="../program.jsp">
		  <jsp:param name="function" value="SalesOrder"/>
		  <jsp:param name="code" value="${orderForm.order.orderNo}"/>
	 </jsp:include>
	   
	<!-- Content -->
	<html:form action="/jsp/saleOrderAction">
	<jsp:include page="../error.jsp"/>
		
			<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
			<%if(User.TT.equals(user.getType())){%>
				<tr>
					<td width="30%" align="right">ระบุเลขที่ PO ลูกค้า(ถ้ามี) </td>
					<td width="25%"><html:text property="order.poNumber" size="20"/></td>
					<td width="15%"></td>
					<td></td>
				</tr>
			<%}else{ %>
			    <tr>
					<td width="30%" align="right"></td>
					<td width="25%"></td>
					<td width="15%"></td>
					<td></td>
				</tr>
			<%} %>
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
						<html:text property="order.orderDate" maxlength="10" size="15" readonly="true" styleId="orderDate" styleClass="disableText"/>
					</td>
				</tr>
				<tr>
					<td align="right">
						<bean:message key="Customer" bundle="sysele"/>&nbsp;&nbsp;
					</td>
					<td align="left" colspan="3">
						<html:text property="order.customerName" size="80" readonly="true" styleClass="disableText"/>
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
					<td colspan="4" align="center">
					<div align="left">
						<%-- &nbsp;&nbsp;
						<input type="button" class="newPosBtn" value="เพิ่มสินค้ารายตัว" onclick="open_product('${pageContext.request.contextPath}');"/> --%>
						&nbsp;&nbsp; 
						<input type="button" class="btn btn-info btn-lg" value="เลือกสินค้าตามแบรนด์" onclick="openProductCategory();" />

					</div>
					<div class="table-responsive">
					<table id="tblProduct" class="table table-bordered table-striped table-light">
					    <thead class="thead-dark">
						<tr>
							<th class="order">ลำดับ</th>
							<th class="checkBox">
							   <input type="checkbox" name="chkAll"
								onclick="checkSelect(this,document.getElementsByName('lineids'));" />
							</th>
							<th>ชื่อสินค้า</th>
							<th>หน่วยนับ</th>
							<th>จำนวน</th>
							<th>ราคาต่อหน่วย</th>
							<th>ยอดรวม</th>
							<th>ส่วนลด</th>
							<th>ยอดรวมหลังหักส่วนลด</th>
							<th>ภาษี</th>
							<th>โปรโมชั่น</th>
						</tr>
					    </thead>
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
							<td align="center"><input type="checkbox" name="lineids" value="${lines1.id}" /></td>
							
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
								<input type='hidden' name='lines.taxable' value='${lines1.taxable}'>
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
							<td align="right">
								
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
							<td align="right">
								<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount}"/>
							</td>
							<td align="right">
								<fmt:formatNumber pattern="#,##0.00000" value="${lines1.discount}"/>
							</td>
						
							<td align="right">
								<fmt:formatNumber pattern="#,##0.00000" value="${lines1.lineAmount - lines1.discount}"/>
							</td>
							<td align="center">
								<c:if test="${lines1.taxable=='Y'}">
									   <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">								
								</c:if>
							</td>
							<td align="center">
									<c:if test="${lines1.promotion=='N'}">
								         <%if(debugMode==true) {%>
										   <%-- <a href="#" onclick="open_product('${pageContext.request.contextPath}',${rows1.index+1});">
										   <img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>  --%>
										 <%} %>
									</c:if>
							</td>
						</tr>
						</c:forEach>
					</table>
					</div>
					<table align="left" border="0" cellpadding="3" cellspacing="1" class="result">
						<tr>
							<td align="left" class="footer">&nbsp;
								<a href="#" onclick="javascript:deleteProduct('${pageContext.request.contextPath}','<%=user.getType() %>');"> 
								   <img border=0 src="${pageContext.request.contextPath}/icons/doc_inactive.gif">&nbsp;
								   <bean:message key="Delete" bundle="sysprop"/>
								</a>
							</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td></td><td></td>
					<td align="right">ยอดรวมก่อนภาษี &nbsp;&nbsp;</td>
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
					<td align="right">ยอดรวมที่ไม่เสียภาษี&nbsp;&nbsp;</td>
					<td align="left">
						<input type="text" id="tempTotalAmountNonVat" name="tempTotalAmountNonVat" readonly="readonly" class="disableText" style="text-align: right;"/>
						<html:hidden property="order.totalAmountNonVat"/>
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
					<td class="textSpecial">
					<%if(User.VAN.equals(user.getType())){%>  
					     ชำระโดย
					    <html:select property="order.vanPaymentMethod">
							<html:options collection="vanPaymentMethod" property="key" labelProperty="name"/>
						</html:select>
				    <%}else{ %>
				        <html:hidden property="order.vanPaymentMethod"/>
				    <%} %>
					</td>
					<td></td>
					<td valign="top">
						<html:checkbox property="order.payment" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Order.Paid" bundle="sysele"/>
						<html:checkbox property="order.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
					    <html:checkbox property="order.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
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
						   <input type="button" value="บันทึก" class="newPosBtn" onclick="return autoReceipt('${pageContext.request.contextPath}','<%=user.getType() %>');">
						<%} %>
						<input type="button" value="ยกเลิก" class="newNegBtn" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
					</td>
				</tr>
			</table>
	
			<span title="SalesOrder">...</span>
			<span title="<%=Utils.isNull(request.getSession().getAttribute("PREV_STEP_ORDER_ACTION")) %>">...</span>
			
			<!-- Hidden Field Fix Value -->
			<html:hidden property="order.salesOrderNo" />
			<html:hidden property="order.arInvoiceNo"/>
			<html:hidden property="order.docStatus" />
			<html:hidden property="order.salesRepresent.name"/>
			<html:hidden property="order.salesRepresent.id"/>
			<html:hidden property="order.salesRepresent.code"/>
			<html:hidden property="order.salesOrderNo" />
			<html:hidden property="order.arInvoiceNo"/>
			
			<html:hidden property="order.priceListId" styleId="order.priceListId"/>
			<html:hidden property="order.vatCode"/>
			<html:hidden property="order.paymentTerm"/>
			<html:hidden property="order.paymentMethod"/>
			<html:hidden property="order.oraBillAddressID"/>
			<html:hidden property="order.oraShipAddressID"/>
			
			<!-- AUTO RECEIPT -->
			<html:hidden property="autoReceiptFlag"/>
			<html:hidden property="autoReceipt.paymentMethod"/>
			<html:hidden property="autoReceipt.bank"/>
			<html:hidden property="autoReceipt.chequeNo"/>
			<html:hidden property="autoReceipt.chequeDate"/>
			<html:hidden property="autoReceipt.creditCardType"/>
			<html:hidden property="autoReceipt.internalBank"/>
			
			<!-- Hidden -->
			<html:hidden property="deletedId"/>
			<html:hidden property="order.orderType"/>
			<html:hidden property="order.id"/>
			<html:hidden property="order.roundTrip"/>
			<html:hidden property="order.customerId"/>
			<html:hidden property="order.exported" value="N"/>
			<html:hidden property="order.isCash" value="N"/>
		    <html:hidden property="order.custGroup"/>
				
			<input type="hidden" name="memberVIP" value="${memberVIP}"/>
			
			<!--  Can Receipt Credit (VAN)-->
			<html:hidden property="receiptCreditFlag"/>
		    <html:hidden property="custCreditLimit"/>
				
			<!-- Case Check Item W1,W2 -->
			<html:hidden property="order.placeOfBilled"/>
			
		    <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
			<div id="productList" style="display: none;"></div>
			<div id="ByList" style="display: none;"></div>
			
	</html:form>
	<!-- /Content -->

	<!-- Include Footer Mobile  -->
    <jsp:include page="../footer.jsp" flush="true"/>
    <!-- /Include Footer Mobile -->	
    
  <!-- Product-Content -->
     <div  class="modal fade" id="brand-dialog" role="dialog" >
       <div class="modal-dialog-full-width modal-dialog modal-fluid">
	      <div class="modal-content-full-width modal-content">
	        <div class=" modal-header-full-width  modal-header text-center">
	           <button type="button" class="btn btn-default" data-dismiss="modal" onclick='addProductToSalesOrder()'>OK</button>
	           <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	           
	           <button type="button" class="close" data-dismiss="modal">&times;</button>
	        </div>
	        <div class="modal-body">
	            <div id="brand-dialog-content">
	               Content By Code
	            </div>
	        </div>
	        <div class="modal-footer-full-width  modal-footer text-center">
	          <button type="button" class="btn btn-default" data-dismiss="modal" onclick='addProductToSalesOrder()'>OK</button>
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        </div>
	      </div>
	    </div>
    </div> 
   
    <!-- Product-Content -->
     <div  class="modal fade" id="product-dialog" role="dialog" >
       <div class="modal-dialog-full-width modal-dialog modal-fluid">
	      <div class="modal-content-full-width modal-content">
	        <div class=" modal-header-full-width  modal-header text-center">
	           <button type="button" class="btn btn-default" data-dismiss="modal" onclick='addProductToBasket()'>OK</button>
	           <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        </div>
	        <div class="modal-body">
	            <div id="product-dialog-content">
	               Content By Code
	            </div>
	        </div>
	        <div class="modal-footer-full-width  modal-footer text-center">
	          <button type="button" class="btn btn-default" data-dismiss="modal" onclick='addProductToBasket()'>OK</button>
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        </div>
	      </div>
	    </div>
    </div> 
    
    <div id="error-dialog">
	  <div id="error-dialog-message" style="color:red;">
	  </div>
	</div>

</body>
</html>

 <!-- Control Save Lock Screen -->
 <!-- Bug in modal -->
 <jsp:include page="../controlSaveLockScreen.jsp"/> 
<!-- Control Save Lock Screen -->

