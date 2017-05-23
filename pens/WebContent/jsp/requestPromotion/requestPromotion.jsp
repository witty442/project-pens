<%@page import="util.SessionGen"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="util.NumberToolsUtil"%>
<%@page import="com.isecinc.pens.bean.RequestPromotionLine"%>
<%@page import="com.isecinc.pens.bean.RequestPromotionCost"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
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
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.Address"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.core.bean.References"%>

<jsp:useBean id="requestPromotionForm" class="com.isecinc.pens.web.reqPromotion.RequestPromotionForm" scope="session" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String userName = user.getUserName();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/requestPromotion.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">

function loadMe(){
	//alert("loadMe");
	
	new Epoch('epoch_popup','th',document.getElementById('promotionStartDate'));
	new Epoch('epoch_popup','th',document.getElementById('promotionEndDate'));
	
	//calprice
	calPrice('newCtn');
	calPrice('newAmount');
	calPrice('stockCtn');
	calPrice('stockQty');
	calPrice('borrowCtn');
	calPrice('borrowQty');
	calPrice('borrowAmount');
}

function escapeParameter(param){
	return param.replace("%","%25");
}

function loadProductOnKeyPress(e,productCodeObj,index){
	if(e != null && e.keyCode == 13){
		//alert("onKeyPress");
		loadProductModel(e,productCodeObj,index);
	}
}

function loadProductOnblur(e,productCodeObj,index){
  //alert("onblur");
  if(productCodeObj.value !=''){
     loadProductModel(e,productCodeObj,index);
  }
}

// call ajax
function loadProductModel(e,productCodeObj,index){
	var pricelistId = document.getElementsByName("requestPromotion.priceListId")[0];//$("#pricelistId");
	//alert(pricelistId.value);
	
	//alert(pricelistId);
	var productId = document.getElementsByName('productId')[index]; 
	var productName = document.getElementsByName('productName')[index];
	var uom1 = document.getElementsByName('uom1')[index];
	var uom2 = document.getElementsByName('uom2')[index];
	var price1 = document.getElementsByName('price1')[index];
	var price2 = document.getElementsByName('price2')[index];
	
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/productQueryOrder.jsp",
				data : "pCode=" + productCodeObj.value +" &pBrand=",
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					productId.value = returnString.split('||')[0];
					productName.value = returnString.split('||')[1]+" "+returnString.split('||')[2];
					
					var getData2 = $.ajax({
						url: "${pageContext.request.contextPath}/jsp/ajax/UOMAll_PremiumQuery.jsp",
						data : "pId=" + productId.value+ "&plId=" + pricelistId.value,
						async: false,
						success: function(getData){
							var returnString = jQuery.trim(getData);
							uom1.value = returnString.split('|')[0];
							uom2.value = returnString.split('|')[1];
							price1.value = returnString.split('|')[2];
							price2.value = returnString.split('|')[3];
							
							//$('#price1Show').val(addCommas($('#price1').val()));
							//$('#price2Show').val(addCommas($('#price2').val()));
							
							var getData3 = $.ajax({
								url: "${pageContext.request.contextPath}/jsp/ajax/UOMProductCapacityQuery.jsp",
								data : "pId=" + productId.value+ "&uom1=" +uom1.value+ "&uom2=" + uom2.value,
								async: false,
								success: function(getData){
									var returnString = jQuery.trim(getData);
									//$('#pacQty2').val(returnString);
				
								}
							}).responseText;
						}
					}).responseText;
				}
			}).responseText;
		});

		
	//calPrice();
}

function openPopupInvoice(path,index,userId){
	var form = document.requestPromotionForm;
	var productCodeObj = document.getElementsByName("productCode")[index];
	
	if(productCodeObj.value != '' && $("#customerCode").val() != ''){
	    var param  = "&index="+index;
	        param += "&productCode="+productCodeObj.value;
	        param += "&userId="+userId;
	        param += "&customerCode="+$("#customerCode").val();
	        //alert(param);
		url = path + "/jsp/popupAction.do?do=prepare&page=INVOICE&action=new"+param;
		window.open(encodeURI(url),"",
				   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
	}else{
		if($("#customerCode").val() == ''){
		  alert("กรุณาระบุรหัส ร้านค้าก่อน");
		  $("#customerCode").focus();
		}
		if(productCodeObj.value == ''){	
		  alert("กรุณาระบุรหัสสินค้าก่อน ");
		  productCodeObj.focus();
		}
	}
}
function setInvoiceValue(code,desc,index){
	var form = document.requestPromotionForm;
	//alert(form);
	document.getElementsByName("invoiceNo")[index].value = code;
}

function openPopupBrand(path){
	var form = document.requestPromotionForm;
	
    var param = "";
	url = path + "/jsp/popupAction.do?do=prepare&page=BRAND&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}
function setBrandValue(code,desc){
	var form = document.requestPromotionForm;
	//alert(form);
	
	$("#productCatagory").val(code);
	$("#productCatagoryDesc").val(desc);
}


function openPopupCustomer(path){
	var form = document.requestPromotionForm;
	
    var param = "";
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare2&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc){
	var form = document.requestPromotionForm;
	//alert(form);
	
	$("#customerCode").val(code);
	$("#customerName").val(desc);
} 

function getCustNameKeypress(e,custCode){
	var form = document.requestPromotionForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			$("#customerCode").val('');
			$("#customerName").val('');
		}else{
		  getCustName(custCode);
		}
	}
}

function getCustNameOnblur(e,custCode){
	var form = document.requestPromotionForm;
	if(e != null ){
		if(custCode.value ==''){
			$("#customerCode").val('');
			$("#customerName").val('');
		}else{
		  getCustName(custCode);
		}
	}
}

function getCustName(custCode){
	var returnString = "";
	var form = document.requestPromotionForm;

	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameAjax.jsp",
			data : "custCode=" + custCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	
		if(returnString !=''){
			var retArr = returnString.split("|");
			$("#customerName").val(retArr[0]);
		}else{
			alert("ไม่พบข้อมูล");
			$("#customerCode").val('');
			$("#customerName").val('');
		}
		
}

function calPrice(fieldName){
	
	    var total = 0;
		for(var i=0;i<7;i++){
			var r = document.getElementsByName(fieldName)[i].value;
			//alert(r);
			if(r !=""){
			  r = r.replace(/\,/g,''); //alert(r);
			  total = total + parseFloat(r);
			}
		}
	
		if(total != 0){
			if(fieldName=='newCtn'){
				document.getElementsByName('totalNewCtn')[0].value = addCommas(Number(total));
			}
			if(fieldName=='newAmount'){
				document.getElementsByName('totalNewAmount')[0].value =  addCommas(Number(toFixed(total,2)).toFixed(2));
			}
			if(fieldName=='stockCtn'){
				document.getElementsByName('totalStockCtn')[0].value = addCommas(Number(total));
			}
			if(fieldName=='stockQty'){
				document.getElementsByName('totalStockQty')[0].value = addCommas(Number(total));
			}
			if(fieldName=='borrowCtn'){
				document.getElementsByName('totalBorrowCtn')[0].value = addCommas(Number(total));
			}
			if(fieldName=='borrowQty'){
				document.getElementsByName('totalBorrowQty')[0].value = addCommas(Number(total));
			}
			if(fieldName=='borrowAmount'){
				document.getElementsByName('totalBorrowAmount')[0].value = addCommas(Number(toFixed(total,2)).toFixed(2));
			}
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
				<jsp:param name="function" value="RequestPromotion"/>
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
						<html:form action="/jsp/requestPromotionAction">
						<jsp:include page="../error.jsp"/>
		
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							
							<tr>
								<td colspan="4" align="center">
								   <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
								   <tr>
										<td align="right">วันที่ทำรายการ</td>
										<td align="left"  colspan="1" nowrap>
										   <html:hidden property="requestPromotion.priceListId" styleId="priceListId"/>
										   <html:text property="requestPromotion.requestDate" size="15" readonly="true" styleClass="disableText"/>
										    &nbsp; &nbsp; &nbsp; &nbsp;
										      พนักงานขาย  <html:text property="requestPromotion.user.code" size="10" readonly="true" styleClass="disableText"/>
										       <html:text property="requestPromotion.user.name" size="15" readonly="true" styleClass="disableText"/>
										</td>
									
										<td align="left" >
										 &nbsp; &nbsp; &nbsp; &nbsp;
										    ภาคการขาย  <html:text property="requestPromotion.territory" size="15" readonly="true" styleClass="disableText"/> 
										 </td>
									</tr>
									<tr>
										<td align="right" >แบรนด์</td>
										<td align="left"  nowrap colspan="1">
										  <html:text property="requestPromotion.productCatagory" styleId= "productCatagory" size="4" readonly="true" styleClass="disableText"/>-
										  <html:text property="requestPromotion.productCatagoryDesc" styleId= "productCatagoryDesc" size="35"  styleClass="disableText" readonly="true" />
										  <input type="button" name="bt2" value="..." onclick="openPopupBrand('${pageContext.request.contextPath}')"/>
										</td>
										
										<td align="left" nowrap>
										    <html:radio property="requestPromotion.productType" styleId="productType" value="P">โปรโมชั่น</html:radio>     
										    <html:radio property="requestPromotion.productType" styleId="productType" value="C">เคลียร์สต็อก</html:radio>
										     &nbsp; &nbsp; &nbsp; &nbsp;
											เลขที่เอกสาร <html:text property="requestPromotion.requestNo" size="15" readonly="true" styleClass="disableText"/>
										</td>
									</tr>
									<tr>
										<td align="right" >รหัสร้านค้า <font color="red">*</font></td>
										<td align="left" nowrap colspan="1">
										  <html:text property="requestPromotion.customerCode" size="10"  styleId="customerCode" 
										  onkeypress="getCustNameKeypress(event,this)"
										  onblur="getCustNameOnblur(event,this)"/>
										  <input type="button" name="bt1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}')"/>
										     <html:text property="requestPromotion.customerName" styleId="customerName" size="35" styleClass="disableText"/>
										</td>
										
										<td align="left"  nowrap>
											โทรศัพท์ร้านค้า &nbsp;<html:text property="requestPromotion.phone" size="15" />
										</td>
									</tr>
									<tr>
										<td align="right">ชื่อรายการ <font color="red">*</font></td>
										<td align="left"  colspan="2" width="10%">
										  <html:text property="requestPromotion.name" styleId="name" size="60"  />
										</td>
								   </tr>
								   <tr>
										<td align="right">ระยะเวลา <font color="red">*</font></td>
										<td align="left" >
										  <html:text property="requestPromotion.promotionStartDate" styleId="promotionStartDate" size="10" readonly="true"/>
										</td>
										 <td align="left" >ถึง <font color="red">*</font>
										      <html:text property="requestPromotion.promotionEndDate" styleId="promotionEndDate" size="10" readonly="true"/>
										</td>
										<td align="left"  nowrap>
											
										</td>
									</tr>
									<tr>
										<td align="right">หมายเหตุ</td>
										<td align="left"  colspan="2">
										  <html:text property="requestPromotion.remark" size="60" />
										</td>
								   </tr>
								   </table>
									
							     </td>
							  </tr>

						</table>
						<!-- Cost Table -->
						 <table align="center" width="50%" border="0" cellpadding="1" cellspacing="1" class="resultByManual">
							<tr >
								 <th align="center">รายละเอียด </th>
							     <th align="center">ค่าใช้จ่าย </th>
							</tr>
							<%
							  int tabIndex =1; 
							  for(int i=0;i<5;i++){
								 String lineNo = "";
								 String costDetail =  "";
								 String costAmount = "";
								 
								 Map<String, RequestPromotionCost> costMap = new HashMap<String, RequestPromotionCost>();
							     if(session.getAttribute("costTableMap") != null){
							    	costMap =(Map)session.getAttribute("costTableMap");
							    	
							    	RequestPromotionCost c = costMap.get((i+1)+"");
							    	if(c != null){
							    		lineNo = c.getLineNo()+"";
							    		costDetail = c.getCostDetail();
							    	    costAmount = NumberToolsUtil.decimalFormat(c.getCostAmount(), NumberToolsUtil.format_current_2_disgit);
							    	}else{
							    		lineNo = (i+1)+"";
							    		costAmount = "";
							    	}
							     }else{
							    	 lineNo = (i+1)+""; 
							     }
							  
							%>
							<tr class="lineO">
								 <td align="left">
								    <input type="hidden" name="lineCost" value="<%=lineNo%>"/>
								    <input type="text" name="costDetail" size="50" value="<%=costDetail%>" tabindex="<% tabIndex++; out.print(tabIndex);%>"/>
							     </td>
							    
							     <td align="center">
								    <input type="text" name="costAmount" size="10" value="<%=costAmount%>" onkeydown="return isNum0to9andpoint(this,event);" class="numberText" tabindex="<% tabIndex++; out.print(tabIndex);%>"/>
							     </td>
							</tr>
							<%} %>
						</table> 
						
						<br/>
						
						<!-- Detail -->
								<table align="center" width="100%" border="0" cellpadding="1"
									cellspacing="1" class="resultByManual">
									<tr>
										<th colspan="5"></th>
										<th nowrap colspan="5">ของแถมหน้าร้านที่ใช้จัดรายการ</th>
										<th></th>
									</tr>
									<tr>
										<th nowrap rowspan="2">รหัสสินค้า</th>
										<th nowrap colspan="2" rowspan="2">สินค้าที่จัดรายการ</th>
										<th nowrap colspan="2">สั่งใหม่</th>
										
										<th nowrap colspan="2">สต๊อกของแถม</th>
										<th nowrap colspan="3">ยืมของแถม</th>
										<th></th>
									</tr>
									<tr>
										<th nowrap>หีบ</th>
										<th nowrap>บาท</th>
										
										<th nowrap>หีบ</th>
										<th nowrap>ชิ้น</th>
										<th nowrap>หีบ</th>
										<th nowrap>ชิ้น</th>
										<th nowrap>บาท</th>
										<th>อ้างอิงเลขที่ Invoice</th>
									</tr>
									<% tabIndex =20;  
							 for(int i=0;i<7;i++){
								 String lineNo = "";
								 String productCode =  "";
								 String productId = "";
								 String uom1 = "";
								 String uom2 = "";
								 String price1 = "";
								 String price2 = "";
								 String productName = "";
								 String newCtn = "";
								 String newAmount = "";
								 String stockCtn = "";
								 String stockQty = "";
								 String borrowCtn = "";
								 String borrowQty = "";
								 String borrowAmount = "";
								 String invoiceNo = "";
								 
								 Map<String, RequestPromotionLine> lineMap = new HashMap<String, RequestPromotionLine>();
							     if(session.getAttribute("lineTableMap") != null){
							    	 lineMap =(Map)session.getAttribute("lineTableMap");
							    	
							    	RequestPromotionLine c = lineMap.get((i+1)+"");
							    	if(c != null){
							    		lineNo = c.getLineNo()+"";
							    		productCode =  Utils.isNull(c.getProductCode());
										productId = Utils.isNull(c.getProductId());
										uom1 = Utils.isNull(c.getUom1());
										uom2 = Utils.isNull(c.getUom2());
										price1 = Utils.isNull(c.getPrice1());
										price2 = Utils.isNull(c.getPrice2());
										productName =Utils.isNull(c.getProductName());
										
										newCtn = NumberToolsUtil.decimalFormat(c.getNewCtn(), NumberToolsUtil.format_current_no_disgit); 
										newAmount =  NumberToolsUtil.decimalFormat(c.getNewAmount(), NumberToolsUtil.format_current_2_disgit); 
										
										stockCtn = NumberToolsUtil.decimalFormat(c.getStockCtn(), NumberToolsUtil.format_current_no_disgit); 
										stockQty = NumberToolsUtil.decimalFormat(c.getStockQty(), NumberToolsUtil.format_current_no_disgit); 
										borrowCtn = NumberToolsUtil.decimalFormat(c.getBorrowCtn(), NumberToolsUtil.format_current_no_disgit); 
										borrowQty = NumberToolsUtil.decimalFormat(c.getBorrowQty(), NumberToolsUtil.format_current_no_disgit); 
									
										borrowAmount =  NumberToolsUtil.decimalFormat(c.getBorrowAmount(), NumberToolsUtil.format_current_2_disgit);
										invoiceNo = Utils.isNull(c.getInvoiceNo()); 
							    	}else{
							    		lineNo = (i+1)+"";
							    	}
							     }else{
							    	 lineNo = (i+1)+"";
							     }
							   
							%>
									<tr class="lineE">
										<td nowrap><input type="hidden" name="lineNo"
											value="<%=lineNo%>" /> <input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											name="productCode" value="<%=productCode%>" size="8"
											onkeypress="loadProductOnKeyPress(event,this,<%=i %>);"
											onblur="loadProductOnblur(null,this,<%=i%>);" /> <input
											type="hidden" name="productId" id="productId"
											value="<%=productId%>"> <input type="hidden"
											name="uom1" id="uom1" value="<%=uom1%>"> <input
											type="hidden" name="uom2" id="uom2" value="<%=uom2%>">
											<input type="hidden" name="price1" id="price1"
											value="<%=price1%>"> <input type="hidden"
											name="price2" id="price2" value="<%=price2%>"></td>
										<td nowrap colspan="2"><input type="text"
											id="productName" name="productName" size="45" readOnly
											class="disableText" value="<%=productName%>" /></td>
										<td nowrap><input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=newCtn%>" name="newCtn" size="8"
											onkeydown="return inputNum(event);"
											onblur="calPrice('newCtn');" class="numberText" /></td>
										<td nowrap><input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=newAmount%>" name="newAmount" size="8"
											onkeydown="return isNum0to9andpoint(this,event);"
											onblur="calPrice('newAmount');" class="numberText" /></td>
										
										<td nowrap><input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=stockCtn%>" name="stockCtn" size="8"
											onkeydown="return inputNum(event);"
											onblur="calPrice('stockCtn');" class="numberText" /></td>
										<td nowrap><input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=stockQty%>" name="stockQty" size="8"
											onkeydown="return inputNum(event);"
											onblur="calPrice('stockQty');" class="numberText" /></td>
										<td nowrap><input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=borrowCtn%>" name="borrowCtn" size="8"
											onkeydown="return inputNum(event);"
											onblur="calPrice('borrowCtn');" class="numberText" /></td>
										<td nowrap><input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=borrowQty%>" name="borrowQty" size="8"
											onkeydown="return inputNum(event);"
											onblur="calPrice('borrowQty');" class="numberText" /></td>
										<td nowrap><input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=borrowAmount%>" name="borrowAmount" size="8"
											onkeydown="return isNum0to9andpoint(this,event);"
											onblur="calPrice('borrowAmount');" class="numberText" />
										</td>
										<td>
										  <input type="text"
											tabindex="<% tabIndex++; out.print(tabIndex);%>"
											value="<%=invoiceNo%>" name="invoiceNo" size="20" readonly/>
											 <input type="button" name="bt3" value="..." onclick="openPopupInvoice('${pageContext.request.contextPath}',<%=i%>,'<%=user.getId()%>')"/>
										</td>
									</tr>
									<%} %>
									<tr class="lineE">

										<td nowrap colspan="3">รวม</td>
										<td nowrap><input type="text" name="totalNewCtn"
											size="8" class="disableNumberText" /></td>
										<td nowrap><input type="text" name="totalNewAmount"
											size="8" class="disableNumberText" /></td>
										
										<td nowrap><input type="text" name="totalStockCtn"
											size="8" class="disableNumberText" /></td>
										<td nowrap><input type="text" name="totalStockQty"
											size="8" class="disableNumberText" /></td>
										<td nowrap><input type="text" name="totalBorrowCtn"
											size="8" class="disableNumberText" /></td>
										<td nowrap><input type="text" name="totalBorrowQty"
											size="8" class="disableNumberText" /></td>
										<td nowrap><input type="text" name="totalBorrowAmount"
											size="8" class="disableNumberText" /></td>
                                       <td></td>
									</tr>
								</table>

								<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
							
							        <c:if test="${requestPromotionForm.requestPromotion.canCancel =='true'}">
										  <input type="button" value="ยกเลิกรายการ"  class="newPosBtnLong" onclick="cancelRequestPromotion('${pageContext.request.contextPath}');">
								   </c:if>
								  
	
	                              <c:if test="${requestPromotionForm.requestPromotion.canGenFile =='true'}">
										  <input type="button" value="Gen File" class="newPosBtnLong" onclick="printRequestPromotion('${pageContext.request.contextPath}');">
							      </c:if>
                             
                                  <c:if test="${requestPromotionForm.requestPromotion.canEdit =='true'}">
										<input type="button" value="บันทึกรายการ" class="newPosBtnLong" onclick="return save('${pageContext.request.contextPath}');">	
								   </c:if>	
							
								<%if(session.getAttribute("shutcut_customerId") != null){ %>  
								  
								    <input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong" onclick="backToCusotmer('${pageContext.request.contextPath}','<%=(String)session.getAttribute("shutcut_customerId")%>','<%=(String)session.getAttribute("fromPage")%>');">
								<%}else{ %>
								   
								     <input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong" onclick="backsearch('${pageContext.request.contextPath}','');">
								<%} %>
								
								</td>
							</tr>
						</table>
						
						<!-- Hidden Field -->		
						
						
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
