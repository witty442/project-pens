<!--
Product Popup for Sales Order 
 -->
<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%
//init Brand List
List<References> brandList = InitialReferences.getReferenes().get(InitialReferences.BRAND_LIST);
pageContext.setAttribute("brandList", brandList, PageContext.PAGE_SCOPE);

User user = (User) request.getSession().getAttribute("user");

boolean isVanSales = User.VAN.equalsIgnoreCase(user.getRole().getKey()); 

//List<UOM> uoms = new MUOM().lookUp(0);
String row="";
if(request.getParameter("row")!=null){
	row = request.getParameter("row");
}

SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH"));
String today = sdf.format(new Date());

//wit edit 29/07/2554   Case VanSales shippingDate = requestDate
Calendar cld = Calendar.getInstance();
if( !"VAN".equals(user.getType()) ){
   cld.add(Calendar.DAY_OF_MONTH,3); // +3 Day
}

String reqDate = new SimpleDateFormat("dd/MM/yyyy",new Locale("th","TH")).format(cld.getTime());

%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="com.isecinc.pens.model.MUOM"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	var cdoc = window.opener.document;
	$('#pricelistId').val(cdoc.getElementsByName('order.priceListId')[0].value);
	if('<%=row%>'!='')
	{
		putData(<%=row%>);
	}
	$('#pCode').focus();	

<%
if(!isVanSales){
%>
	//default Date
	if($('#shipdate').val()==''){
		$('#shipdate').val('<%=today%>');
	}
	if($('#reqdate').val()==''){
		$('#reqdate').val('<%=reqDate%>');
	}
<%}
else {
%>
//default Date
if($('#shipdate').val()==''){
	$('#shipdate').val(cdoc.getElementsByName('order.orderDate')[0].value);
}
if($('#reqdate').val()==''){
	$('#reqdate').val(cdoc.getElementsByName('order.orderDate')[0].value);
}
<% } %>

//	new Epoch('epoch_popup','th',document.getElementById('shipdate'));
//	new Epoch('epoch_popup','th',document.getElementById('reqdate'));
}

function putData(rowNo){

	//alert(rowNo);
	
	var cdoc = window.opener.document;
	var ids=cdoc.getElementsByName('lines.id')[rowNo-1];
	var productIds=cdoc.getElementsByName('lines.productId')[rowNo-1];
	var products=cdoc.getElementsByName('lines.product')[rowNo-1];
	var productLabels=cdoc.getElementsByName('lines.productLabel')[rowNo-1];
	
	var uomIds1=cdoc.getElementsByName('lines.uom1')[rowNo-1];
	var uomIds2=cdoc.getElementsByName('lines.uom2')[rowNo-1];
	var uomLabels1=cdoc.getElementsByName('lines.uomLabel1')[rowNo-1];
	var uomLabels2=cdoc.getElementsByName('lines.uomLabel2')[rowNo-1];
	var prices1=cdoc.getElementsByName('lines.price1')[rowNo-1];
	var prices2=cdoc.getElementsByName('lines.price2')[rowNo-1];
	var qtys1=cdoc.getElementsByName('lines.qty1')[rowNo-1];
	var qtys2=cdoc.getElementsByName('lines.qty2')[rowNo-1];
	var amounts1=cdoc.getElementsByName('lines.amount1')[rowNo-1];
	var amounts2=cdoc.getElementsByName('lines.amount2')[rowNo-1];
	var discs1=cdoc.getElementsByName('lines.disc1')[rowNo-1];
	var discs2=cdoc.getElementsByName('lines.disc2')[rowNo-1];
	var totals1=cdoc.getElementsByName('lines.total1')[rowNo-1];
	var totals2=cdoc.getElementsByName('lines.total2')[rowNo-1];
	
	var ships=cdoc.getElementsByName('lines.ship')[rowNo-1];
	var reqs=cdoc.getElementsByName('lines.req')[rowNo-1];
	var promos=cdoc.getElementsByName('lines.promo')[rowNo-1];


	$('#lineId').val(ids.value);
	$('#productId').val(productIds.value);
	$('#pCode').val(products.value);
	$('#pName').val(productLabels.value);
	
	$('#qty1').val(qtys1.value);
	$('#qty2').val(qtys2.value);
	$('#amount1').val(amounts1.value);
	$('#amount2').val(amounts2.value);
	$('#amount').val(eval(amounts1.value) + eval(amounts2.value));
	$('#disc1').val(discs1.value);
	$('#disc2').val(discs2.value);
	$('#disc').val(eval(discs1.value) + eval(discs2.value));
	$('#total1').val(totals1.value);
	$('#total2').val(totals2.value);
	$('#total').val(eval(totals1.value) + eval(totals2.value));
	
	$('#shipdate').val(ships.value);
	$('#reqdate').val(reqs.value);
	$('#productRow').val(rowNo);

	loadProductModel(null);

	//$('#uom').val(uomIds.value);
	//$('#price').val(prices.value);
    //alert(uomIds2.value);
    
	$('#uom1').val(uomIds1.value);
	$('#uom2').val(uomIds2.value);
	$('#price1').val(prices1.value);
	$('#price2').val(prices2.value);
}

function loadProductOnKeyPress(e){
	if(e != null && e.keyCode == 13){
		//alert("onKeyPress");
		loadProductModel(e);

		$('#qty1').focus();
	}
}
function loadProductOnblur(e){
  //alert("onblur");
  if(jQuery.trim($('#pCode').val()) !=''){
     loadProductModel(e);
  }
}
// call ajax
function loadProductModel(e){

		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/productQueryOrder.jsp",
				data : "pCode=" + $('#pCode').val()+" &pBrand=" + encodeURIComponent($('#pBrand').val()),
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					$('#productId').val(returnString.split('||')[0]);
					$('#pName').val(returnString.split('||')[1]+" "+returnString.split('||')[2]);
					var getData2 = $.ajax({
						url: "${pageContext.request.contextPath}/jsp/ajax/UOMQuery.jsp",
						data : "pId=" + $('#productId').val()
						+ "&plId=" + $('#pricelistId').val(),
						async: false,
						success: function(getData){
							var returnString = jQuery.trim(getData);
							$('#uom1').val(returnString.split('|')[0]);
							$('#uom2').val(returnString.split('|')[1]);
							$('#price1').val(returnString.split('|')[2]);
							$('#price2').val(returnString.split('|')[3]);
							$('#amount1').val(0);
							$('#amount2').val(0);
							$('#disc1').val(0);
							$('#disc2').val(0);
							$('#total1').val(0);
							$('#total2').val(0);

							$('#price1Show').val(addCommas($('#price1').val()));
							$('#price2Show').val(addCommas($('#price2').val()));
							
						}
					}).responseText;
				}
			}).responseText;
		});

		//alert("["+jQuery.trim($('#pName').val())+"]");
		if(jQuery.trim($('#pName').val()) ==''){
		      alert("��س��к������Թ������١��ͧ��еç�Ѻ�ù���Թ���");
		      $('#pCode').focus();
		 }
	
	$('#qty').focus();
	calPrice();
	if(document.getElementById('uom1').value==''){
		document.getElementById('qty1').readOnly=true;
		document.getElementById('qty1').className='disableText';
	}else{
		document.getElementById('qty1').readOnly=false;
		document.getElementById('qty1').className='';
	}
	if(document.getElementById('uom2').value==''){
		document.getElementById('qty2').readOnly=true;
		document.getElementById('qty2').className='disableText';
	}else{
		document.getElementById('qty2').readOnly=false;
		document.getElementById('qty2').className='';
	}

    
}

function calPrice(){
	
	var qty1 = $('#qty1').val();
	var qty2 = $('#qty2').val();

	if(qty1==0 && qty2==0){
		return;
	}
	
	var price1 = $('#price1').val();
	var price2 = $('#price2').val();
	var amt1 = (qty1 * price1);
	var amt2 = (qty2 * price2);
	
	$('#amount1').val(amt1);
	$('#amount2').val(amt2);
	var amt = amt1 + amt2;
	$('#amount').val(amt.toFixed(5));
	$('#amountShow').val(addCommas($('#amount').val()));

	var disc1 = $('#disc1').val();
	var disc2 = $('#disc2').val();
	var disc = eval(disc1) + eval(disc2);
	$('#disc').val(disc.toFixed(5));
	$('#discShow').val(addCommas($('#disc').val()));

	var total1 = eval(amt1) - eval(disc1);
	var total2 = eval(amt2) - eval(disc2);
	$('#total1').val(total1);
	$('#total2').val(total2);
	$('#total').val((total1 + total2).toFixed(5));
	$('#totalShow').val(addCommas($('#total').val()));
}

function addRow(){
	var qty1 = $('#qty1').val();
	var qty2 = $('#qty2').val();
	
	if(jQuery.trim($('#pCode').val()) == '' || jQuery.trim($('#pName').val()) == ''){
		alert('��س����͡�Թ���');
		$('#pCode').focus();
		return false;
	}  
	if(qty1==0 && qty2==0){
		alert('��س����ӹǹ');
		$('#qty1').focus();
		return false;
	}

	var product = new Object();
	product.productId = $('#productId').val();
	product.product = $('#pCode').val();
	product.productLabel = $('#pName').val();
	
	product.uom1 = $('#uom1').val();
	product.uom2 = $('#uom2').val();
	product.uomLabel1 = $('#uom1').val();
	product.uomLabel2 = $('#uom2').val();
	product.price1 = $('#price1').val();
	product.price2 = $('#price2').val();
	product.qty1 = $('#qty1').val();
	product.qty2 = $('#qty2').val();

	if(!checkNum($('#amount1'))){return;}
	if(!checkNum($('#amount2'))){return;}
	
	product.amount1 = $('#amount1').val();
	product.amount2 = $('#amount2').val();
	product.disc1 = $('#disc1').val();
	product.disc2 = $('#disc2').val();
	product.total1 = $('#total1').val();
	product.total2 = $('#total2').val();

	product.vat1 = $('#vat1').val();
	product.vat2 = $('#vat2').val();
	
	product.ship = $('#shipdate').val();
	product.req = $('#reqdate').val();
	product.id = $('#lineId').val();
	product.row = $('#productRow').val();

	//alert("product.row:"+product.row);
	
	if(product.row==''){
		window.opener.addProduct('${pageContext.request.contextPath}', product);
	}else{
		window.opener.setValueToProduct('${pageContext.request.contextPath}', product);
	}
	window.close();
}

function showProduct(path){
	var pBrand = $('#pBrand').val();
   // alert(pBrand);
	window.open(path + '/jsp/pop/view/productViewPopup.jsp?pBrand='+encodeURIComponent(pBrand), 'Product List', 'width=500,height=450,location=No,resizable=No');
}
function setProduct(code, name){
	$('#pCode').val(code);
	$('#pName').val(name);
	loadProductModel(null);
}

//Click on Calendar
function calculateShipReqDate(target){
	//alert($('#shipdate').val());
	//alert($('#reqdate').val());
	//alert(target.id);
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/calculateShipReqDateQuery.jsp",
			data : 
				"shipdate=" + $('#shipdate').val()+
				"&reqdate=" + $('#reqdate').val()+
				"&target="+target.id
			,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				$('#shipdate').val(returnString.split('||')[0]);
				$('#reqdate').val(returnString.split('||')[1]);
			}
		}).responseText;
	});
}
function validNumber(e){
	var r = inputNum(e);
	alert(r);
	if(r){
	   onQty1KeyPressNextTab(e);
	}
	return r;
}

function onQty1KeyPressNextTab(e){
	//inputNum(e);
	if(e != null && e.keyCode == 13){
		$('#qty2').focus();
	}
}

function onQty2KeyPressNextTab(e){
	//inputNum(e);
	if(e != null && e.keyCode == 13){
		$('#save').focus();
	}
}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="�Թ���"/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="40%"></td>
		<td></td>
	</tr>
	
	<tr>
		<td align="right">�ù��</td>
		<td align="left">
			<select id="pBrand" name="pBrand" onchange="loadProductOnblur(null);">
			  <option value=""></option>
			  <%if(brandList != null){ 
			     for(int i=0;i<brandList.size();i++){
			    	 References rb = (References)brandList.get(i);
			  %>
			     <option value="<%=rb.getCode()%>"><%=rb.getCode() %></option>
			    <%}} %>
			</select>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Product" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<input type="text" id="pCode" name="pCode"  onkeypress="loadProductOnKeyPress(event);" onblur="loadProductOnblur(null);" tabindex="1" />&nbsp;
			<a href="#" onclick="showProduct('${pageContext.request.contextPath}');" id="lookProduct">
			<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"></a>
		</td>
	</tr>
	<tr>
		<td align="right"></td>
		<td align="left"><input type="text" id="pName" name="pName" size="50" readonly="readonly" class="disableText"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Product.UOM" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="uom1" name="uom1" size="10" style="text-align: right;"  readonly="readonly" class="disableText">
			&nbsp;/&nbsp;
			<input type="text" id="uom2" name="uom2" size="10" style="text-align: right;"  readonly="readonly" class="disableText">
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Price.Unit" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="price1Show" name="price1Show" size="10" style="text-align: right;"  readonly="readonly" class="disableText" >
			&nbsp;/&nbsp;
			<input type="text" id="price2Show" name="price2Show" size="10" style="text-align: right;"  readonly="readonly" class="disableText" >
			<input type="hidden" id="price1" name="price1">
			<input type="hidden" id="price2" name="price2">
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Amount" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<input type="text" id="qty1" name="qty1" value="0"  onkeydown="return inputNum(event);" onblur="calPrice();" size="10" style="text-align: right;" tabindex="2" >
			&nbsp;/&nbsp;
			<input type="text" id="qty2" name="qty2" value="0"  onkeydown="return inputNum(event);" onblur="calPrice();" size="10" style="text-align: right;" tabindex="3">
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="TotalAmount" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="amountShow" name="amountShow" value="0.00" size="10" style="text-align: right;" readonly="readonly" class="disableText">
			<input type="hidden" id="amount1" name="amount1">
			<input type="hidden" id="amount2" name="amount2">
			<input type="hidden" id="amount" name="amount">
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Discount" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="discShow" name="discShow" value="0.00" size="10" style="text-align: right;" readonly="readonly" class="disableText">
			<input type="hidden" id="disc1" name="disc1">
			<input type="hidden" id="disc2" name="disc2">
			<input type="hidden" id="vat1" name="vat1">
			<input type="hidden" id="vat2" name="vat2">
			<input type="hidden" id="disc" name="disc">
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Overall" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" id="totalShow" name="totalShow" value="0.00" size="10" style="text-align: right;" readonly="readonly" class="disableText">
			<input type="hidden" id="total1" name="total1">
			<input type="hidden" id="total2" name="total2">
			<input type="hidden" id="total" name="total">
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Order.ShipmentDate" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<input type="text" id="shipdate" name="shipdate" size="10" readonly="readonly" onvaluechange="alert('change1');" >
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Order.RequiredDate" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			<input type="text" id="reqdate" name="reqdate" size="10" readonly="readonly" onvaluechange="alert('change2');" >
		</td>
	</tr>
	
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td align="left">
			<a href="#" onclick="addRow();">
<!--			<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" id ="save" value="�ѹ�֡" class="newPosBtn" tabindex="4">
			</a>
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="¡��ԡ" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>

<input type="hidden" id="lineId" name="lineId"/>
<input type="hidden" id="pricelistId" name="pricelistId"/>
<input type="hidden" id="productId" name="productId">
<input type="hidden" id="productRow" name="productRow">
<br/>
</body>
</html>