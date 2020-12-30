<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.web.stockmc.StockMCDAO"%>
<%@page import="org.apache.struts.Globals"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
try{
//head parameter
List<String[]> uomList = StockMCUtils.getUomList();
User user = (User)session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName")); 
String mobile = Utils.isNull(request.getParameter("mobile")); 

//get parameter from main screen
String action = Utils.isNull(request.getParameter("action"));
String headerId = Utils.isNull(request.getParameter("headerId"));
String lineId = Utils.isNull(request.getParameter("lineId"));
String stockDate = Utils.isNull(request.getParameter("stockDate"));
String mcName = new String(Utils.isNull(request.getParameter("mcName")).getBytes("ISO8859_1"), "UTF-8");
String customerCode = Utils.isNull(request.getParameter("customerCode"));
String customerName = new String(Utils.isNull(request.getParameter("customerName")).getBytes("ISO8859_1"), "UTF-8");
String storeCode = new String(Utils.isNull(request.getParameter("storeCode")).getBytes("ISO8859_1"), "UTF-8");
String productCode = Utils.isNull(request.getParameter("productCode"));
String index = Utils.isNull(request.getParameter("index"));

System.out.println("action:"+action);
System.out.println("headerId:"+headerId);
System.out.println("lineId:"+lineId);
System.out.println("productCode:"+productCode);
System.out.println("mcNameDecodeURIComp:"+URLEncoder.encode(request.getParameter("mcName")));

StockMCBean stockMCBean = null;
int tabIndex = 0;
String[] uom = null;
if( "edit".equalsIgnoreCase(action) || "view".equalsIgnoreCase(action)){
	//Get Detail from DB
	stockMCBean= new StockMCDAO().searchStockMCDetail(headerId, productCode);
	
	//get from screen
	stockMCBean.setStockDate(stockDate);
	stockMCBean.setMcName(mcName);
	stockMCBean.setCustomerCode(customerCode);
	stockMCBean.setCustomerName(customerName);
	stockMCBean.setStoreCode(storeCode);
	stockMCBean.setProductCode(productCode);
	stockMCBean.setCanEdit("edit".equalsIgnoreCase(action)?true:false);
	
}else if( "add".equalsIgnoreCase(action) ){
	// new
	//Case back and edit agian
	//Get Detail from DB by Product
	stockMCBean= new StockMCDAO().searchStockMCDetail(headerId, productCode);
	
	stockMCBean = stockMCBean == null?new StockMCBean():stockMCBean;
	
	stockMCBean.setId(Utils.convertStrToInt(headerId));
	stockMCBean.setStockDate(stockDate);
	stockMCBean.setMcName(mcName);
	stockMCBean.setCustomerCode(customerCode);
	stockMCBean.setCustomerName(customerName);
	stockMCBean.setStoreCode(storeCode);
	stockMCBean.setProductCode(productCode);
	stockMCBean.setCanEdit(true);
	//Get product Detail
	StockMCBean productInfo = StockMCDAO.getProductMCItemInfo(stockMCBean.getCustomerCode(),stockMCBean.getProductCode());
	if(productInfo != null){
		stockMCBean.setProductName(productInfo.getProductName());
		stockMCBean.setProductPackSize(productInfo.getProductPackSize());
		stockMCBean.setProductAge(productInfo.getProductAge());
		stockMCBean.setRetailPriceBF(productInfo.getRetailPriceBF());
		stockMCBean.setBarcode(productInfo.getBarcode());
	}
	
}else if( "save".equalsIgnoreCase(action) ){
	System.out.println("saveByProduct");
	StockMCDAO dao = new StockMCDAO();
	//set parameter
	StockMCBean paraBean = new StockMCBean();
	//head
	paraBean.setId(Utils.convertStrToInt(request.getParameter("headerId")));

	paraBean.setStockDate(Utils.isNull(request.getParameter("stockDate")));
	paraBean.setMcName(Utils.isNull(request.getParameter("mcName")));
	paraBean.setCustomerCode(Utils.isNull(request.getParameter("customerCode")));
	paraBean.setCustomerName(Utils.isNull(request.getParameter("customerName")));
	paraBean.setStoreCode(Utils.isNull(request.getParameter("storeCode")));
	
	//detail
    paraBean.setLineId(Utils.convertStrToInt(request.getParameter("lineId")));
	paraBean.setProductCode(productCode);
	paraBean.setBarcode(Utils.isNull(request.getParameter("barcode")));
	paraBean.setPromotionPrice(Utils.isNull(request.getParameter("promotionPrice")));
	
	//Get product Detail
	StockMCBean productInfo = StockMCDAO.getProductMCItemInfo(paraBean.getCustomerCode(),paraBean.getProductCode());
	if(productInfo != null){
		paraBean.setProductName(productInfo.getProductName());
		paraBean.setProductPackSize(productInfo.getProductPackSize());
		paraBean.setProductAge(productInfo.getProductAge());
		paraBean.setRetailPriceBF(productInfo.getRetailPriceBF());
		paraBean.setBarcode(productInfo.getBarcode());
	}
	paraBean.setLegQty(Utils.isNull(request.getParameter("legQty")));
	paraBean.setInStoreQty(Utils.isNull(request.getParameter("inStoreQty")));
	paraBean.setBackendQty(Utils.isNull(request.getParameter("backendQty")));
	paraBean.setUom(Utils.isNull(request.getParameter("uom")));
	paraBean.setFrontendQty1(Utils.isNull(request.getParameter("frontendQty1")));
	paraBean.setUom1(Utils.isNull(request.getParameter("uom1")));
	paraBean.setExpireDate1(Utils.isNull(request.getParameter("expireDate1")));
	paraBean.setFrontendQty2(Utils.isNull(request.getParameter("frontendQty2")));
	paraBean.setUom2(Utils.isNull(request.getParameter("uom2")));
	paraBean.setExpireDate2(Utils.isNull(request.getParameter("expireDate2")));
	paraBean.setFrontendQty3(Utils.isNull(request.getParameter("frontendQty3")));
	paraBean.setUom3(Utils.isNull(request.getParameter("uom3")));
	paraBean.setExpireDate3(Utils.isNull(request.getParameter("expireDate3")));
	paraBean.setCreateUser(user.getUserName());
	paraBean.setUpdateUser(user.getUserName());
	
   	//save Mobile By Product
	paraBean = dao.saveByProduct(paraBean);
	
	//search 
	stockMCBean= dao.searchStockMCDetail(paraBean.getId()+"", paraBean.getProductCode());
	
	stockMCBean.setCanEdit(true);
	stockMCBean.setStockDate(paraBean.getStockDate());
	stockMCBean.setMcName(paraBean.getMcName());
	stockMCBean.setCustomerCode(paraBean.getCustomerCode());
	stockMCBean.setCustomerName(paraBean.getCustomerName());
	stockMCBean.setStoreCode(paraBean.getStoreCode());
	
	request.setAttribute("Message","บันทึกข้อมูลเรียบร้อย");
	
}else if( "clearForm".equalsIgnoreCase(action) ){
	stockMCBean = new StockMCBean();
	stockMCBean.setCanEdit(true);
	//head
	stockMCBean.setId(Utils.convertStrToInt(request.getParameter("id")));

	stockMCBean.setStockDate(Utils.isNull(request.getParameter("stockDate")));
	stockMCBean.setMcName(Utils.isNull(request.getParameter("mcName")));
	stockMCBean.setCustomerCode(Utils.isNull(request.getParameter("customerCode")));
	stockMCBean.setCustomerName(Utils.isNull(request.getParameter("customerName")));
	stockMCBean.setStoreCode(Utils.isNull(request.getParameter("storeCode")));
	
	//detail
    stockMCBean.setLineId(Utils.convertStrToInt(request.getParameter("lineId")));
    stockMCBean.setProductCode(Utils.isNull(request.getParameter("productCode")));
    stockMCBean.setProductName(Utils.isNull(request.getParameter("productName")));
    stockMCBean.setBarcode(Utils.isNull(request.getParameter("barcode")));
    
   //Get product Detail
  	StockMCBean productInfo = StockMCDAO.getProductMCItemInfo(stockMCBean.getCustomerCode(),stockMCBean.getProductCode());
  	if(productInfo != null){
  		stockMCBean.setProductPackSize(productInfo.getProductPackSize());
  		stockMCBean.setProductAge(productInfo.getProductAge());
  		stockMCBean.setRetailPriceBF(productInfo.getRetailPriceBF());
  	}
  	
	//clear detail form
	stockMCBean.setPromotionPrice("");
	stockMCBean.setLegQty("");
	stockMCBean.setInStoreQty("");
	stockMCBean.setBackendQty("");
	stockMCBean.setUom("");
	stockMCBean.setFrontendQty1("");
	stockMCBean.setUom1("");
	stockMCBean.setExpireDate1("");
	stockMCBean.setFrontendQty2("");
	stockMCBean.setUom2("");
	stockMCBean.setExpireDate2("");
	stockMCBean.setFrontendQty3("");
	stockMCBean.setUom3("");
	stockMCBean.setExpireDate3("");
}
%>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<%-- <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/epoch_styles.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
 --%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<!-- Calendar -->
 <link rel="StyleSheet" href="${pageContext.request.contextPath}/css/calendar/jquery.calendars.picker.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.plugin.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.plus.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai-th.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker-th.js"></script>

   
<%--  <script type="text/javascript" src="${pageContext.request.contextPath}/js/page/epoch_classes_mc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
    --%>
<!-- Bootstrap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style>
.errormsg {
	color: #FF0000;
	font-weight: bold;
	font-size: 14px;
}
</style>
<script type="text/javascript">

/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

function loadMe(){
	// set instance is blank default eng christDate
     $('#expireDate1').calendarsPicker({calendar: $.calendars.instance('','')});
     $('#expireDate2').calendarsPicker({calendar: $.calendars.instance('','')});
     $('#expireDate3').calendarsPicker({calendar: $.calendars.instance('','')});
     
    //new Epoch('epoch_popup', 'th', document.getElementById('expireDate1'));
    
     //set id to main 
     window.opener.headerId.value = <%=stockMCBean!= null?stockMCBean.getId():0%>;
}
function save(path){
	if(checkCanSave()){
		document.stockMCForm.action = path + "/jsp/stockMC/stockMCMobileDetail.jsp?action=save";
		document.stockMCForm.submit();
		return true;
	}
	return false;
}
function checkCanSave(){
	var selectOne = false;
	var itemCode = document.getElementsByName("productCode");
	//var status = document.getElementsByName("status");
	
	var mcName = document.getElementById("mcName");
	var customerCode = document.getElementById("customerCode");
	var storeCode = document.getElementById("storeCode");
	var promotionPrice = document.getElementById("promotionPrice");
	var legQty = document.getElementById("legQty");
	var inStoreQty = document.getElementById("inStoreQty");
	var backendQty = document.getElementById("backendQty");
	var frontendQty1 = document.getElementById("frontendQty1");
	var frontendQty2 = document.getElementById("frontendQty2");
	var frontendQty3 = document.getElementById("frontendQty3");
	
	if(mcName.value ==''){
		alert("กรุณาระบุ ชื่อ-นามสกุล พีซี");
		mcName.focus();
		r = false;
		return r;
	}
	if(customerCode.value ==''){
		alert("กรุณาระบุ ห้าง");
		customerCode.focus();
		r = false;
		return r;
	}
	if(storeCode.value ==''){
		alert("กรุณาระบุ สาขา");
		storeCode.focus();
		r = false;
		return r;
	}
	
	//validate input number
	if(!isNum2Digit(promotionPrice)){
		alert("กรุณาระบุ ราคาโปรโมชั้่นเป็นตัวเลขเท่านั้น");
		promotionPrice.focus();
		r = false;
	}
	if(!isNumPositive(legQty)){
		alert("กรุณาระบุ ขา เป็นตัวเลขเท่านั้น");
		legQty.focus();
		r = false;
	}
	if(!isNumPositive(inStoreQty)){
		alert("กรุณาระบุ ในระบบห้าง เป็นตัวเลขเท่านั้น");
		inStoreQty.focus();
		r = false;
	}
	if(!isNumPositive(backendQty)){
		alert("กรุณาระบุ หลังร้าน เป็นตัวเลขเท่านั้น");
		backendQty.focus();
		r = false;
	}
	if(!isNumPositive(frontendQty1)){
		alert("กรุณาระบุ หน้าร้านกลุ่มที่ 1 เป็นตัวเลขเท่านั้น");
		frontendQty1.focus();
		r = false;
	}
	if(!isNumPositive(frontendQty2)){
		alert("กรุณาระบุ หน้าร้านกลุ่มที่ 2 เป็นตัวเลขเท่านั้น");
		frontendQty2.focus();
		r = false;
	}
	if(!isNumPositive(frontendQty3)){
		alert("กรุณาระบุ หน้าร้านกลุ่มที่ 3 เป็นตัวเลขเท่านั้น");
		frontendQty3.focus();
		r = false;
	}
	return true;
}
function backsearch(path) {
   window.opener.headerId.value = document.getElementById("headerId").value;
  <%if(stockMCBean.getLineId() != 0){%>
     window.opener.div_<%=index%>.className ='stock_save';
   <%}%>
   window.close();
}
function clearForm(path){
	document.stockMCForm.action = path + "/jsp/stockMC/stockMCMobileDetail.jsp?action=clearForm";
	document.stockMCForm.submit();
	return true;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

		<!-- BODY -->
		<html:form action="/jsp/stockMCAction" method="post">
		<jsp:include page="../error.jsp"/>
		
		  <!-- Hidden -->
          <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
		  <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
		  <input type="hidden" name="TokenKey" value="<%= session.getAttribute(Globals.TRANSACTION_TOKEN_KEY) %>" >
		  <input type="hidden" name="headerId" id="headerId" value="<%=stockMCBean.getId()%>"/>
		  <input type="hidden" name="lineId" id="lineId" value="<%=stockMCBean.getLineId()%>"/>
		  <input type="hidden" name="stockDate" id="stockDate"  value="<%=stockMCBean.getStockDate()%>"/>	
          <input type="hidden" name="customerCode" id="customerCode" value="<%=stockMCBean.getCustomerCode()%>" />
		  <input type="hidden" name="mcName" id="mcName"  value="<%=stockMCBean.getMcName()%>"/>
		  <input type="hidden" name="customerName" id="customerName"  value="<%=stockMCBean.getCustomerName()%>"/>
		  <input type="hidden" name="storeCode" id="storeCode"  value="<%=stockMCBean.getStoreCode()%>"/>
		  <input type="hidden" name="productCode" id="productCode"  value="<%=stockMCBean.getProductCode()%>"/>
		  <input type="hidden" name="productName" id="productName"  value="<%=stockMCBean.getProductName()%>"/>
		  <input type="hidden" name="barcode" id="barcode"  value="<%=stockMCBean.getBarcode()%>"/>
		 <%--  headerId[<%=stockMCBean.getId()%>] --%>
		  <input type="hidden" name="index" id="index"  value="<%=index%>"/>
		   <%--  index[<%=index%>] --%>
			<!-- Head -->
					<div class="pricing-header px-3 py-3 pt-md-1 pb-md-1 mx-auto text-center">
					    <h4 class="my-0 font-weight-bold"> <u>เพิ่ม/แก้ไข เช็คสินค้าห้าง</u> </h4>
					     <h5 class="my-0 font-weight-normal">  
					             วันที่ตรวจเช็คสต๊อก :<%=stockMCBean.getStockDate() %>	
						</h5>
						<h5 class="my-0 font-weight-normal">  
						    ชื่อ-นามสกุล พีซี :<%=stockMCBean.getMcName() %>
						</h5>
						<h5 class="my-0 font-weight-normal">   
						     ห้าง  :<%=stockMCBean.getCustomerCode() %>-<%=stockMCBean.getCustomerName() %>
						     สาขา :<%=stockMCBean.getStoreCode() %>
						 </h5>
						  <p class="lead">  
							   <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
								   <%if(stockMCBean.isCanEdit()){%>
									 <a href="#" onclick="return save('${pageContext.request.contextPath}');">
									   <input type="button" value="บันทึกรายการ" class="btn btn-primary">
									 </a>	
								   <%} %>
								    &nbsp;
								  <a href="#" onclick="clearForm('${pageContext.request.contextPath}');">
									  <input type="button" value="Clear"  class="btn btn-primary">
								    </a>
							    <%} %>
							  &nbsp;
							  <a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
								  <input type="button" value="ปิดหน้าจอ"  class="btn btn-primary">
							  </a>
						  </p>
					</div>
					<!-- /Head -->
					
					<!--  Input Form  -->
					  <!-- Main group -->
					   <div class="card mb-1 shadow-sm">
					       <div class="card-header">
					         <h5 class="my-0 font-weight-normal">
					         <u>รหัสสินค้า:<%=stockMCBean.getProductCode() %> /
					         <%=stockMCBean.getBarcode() %>
					         </u></h5>
					      
					         <h5 class="my-0 font-weight-normal"><u>ชื่อสินค้า:<%=stockMCBean.getProductName() %></u> </h5>
					      </div> 
					        <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">ราคาโปรโมชั่น :</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
									<%tabIndex++; %>
									 <input type="number" size="5" name="promotionPrice" tabindex="<%=tabIndex %>" id="promotionPrice" 
									 value ="<%=Utils.isNull(stockMCBean.getPromotionPrice())%>" class="enableNumber"
									 onkeydown="return isNum(this,event);" autocomplete="off"/>
								</div>
							  </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right"> ขา:</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							       <%tabIndex++; %>
									 <input  type="number" size="5"  name="legQty" tabindex="<%=tabIndex %>" id="legQty" 
									 value ="<%=Utils.isNull(stockMCBean.getLegQty())%>" size="1"  class="enableNumber" 
									 onkeydown="return isNum0to9andpoint(this,event);" autocomplete="off"/>	 
								</div>
							  </div>
					    </div>
					  <!-- ************ -->
					   <!-- Main group -->
					   <div class="card mb-4 shadow-sm">
					   
					      <div class="card-header">
					        <h5 class="my-0 font-weight-normal">สต๊อกสินค้า </h5>
					      </div>
							<div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">ในระบบห้าง:</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							        <%tabIndex++; %>
									 <input  type="number" size="5"  name="inStoreQty" tabindex="<%=tabIndex %>" id="inStoreQty" 
									 value ="<%=Utils.isNull(stockMCBean.getInStoreQty())%>" size="1"  class="enableNumber"
									 onkeydown="return isNum0to9andpoint(this,event);" autocomplete="off"/>
								</div>
							 </div>
					        <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">หลังร้าน:</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							        <%tabIndex++; %>
									  <input  type="number" size="5"  name="backendQty" tabindex="<%=tabIndex %>" id="backendQty" 
									  value ="<%=Utils.isNull(stockMCBean.getBackendQty())%>" size="1" class="enableNumber"
									  onkeydown="return isNum0to9andpoint(this,event);" autocomplete="off"/>	  
								</div>
							</div>
					        <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">หน่วยบรรจุ:</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							        <%tabIndex++; %>
									  <select name="uom" tabindex="<%=tabIndex %>" id="uom">
										 <% for(int n=0;n<uomList.size();n++){ 
											 uom = uomList.get(n);
											 if(Utils.isNull(stockMCBean.getUom()).equalsIgnoreCase(uom[0])){
										        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
											 }else{
												out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
											 }
										 }
										 %>
									 </select>  
								</div>
					       </div> 
					    
					  <!-- ************ -->
					  <!-- Group 1 -->
					      <div class="card-header">
					        <h5 class="my-0 font-weight-normal">สต๊อกสินค้า กลุ่มหมดอายุที่ 1</h5>
					      </div>
					        <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">หน้าร้าน :</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							    <%tabIndex++; %>
									<input  type="number" size="5"
									tabindex="<%=tabIndex %>"
									value="<%=Utils.isNull(stockMCBean.getFrontendQty1())%>" name="frontendQty1" size="1"
									onkeydown="return isNum0to9andpoint(this,event);"  id="frontendQty1"
									class="enableNumber" autocomplete="off"/>
								</div>
							  </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right"> หน่วยบรรจุ :</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							       <%tabIndex++; %>
									<select name="uom1" tabindex="<%=tabIndex %>" id="uom1">
									 <% for(int n=0;n<uomList.size();n++){ 
										 uom = uomList.get(n);
										 if(Utils.isNull(stockMCBean.getUom1()).equalsIgnoreCase(uom[0])){
									        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
										 }else{
											out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
										 }
									 }
									 %>
									</select>
								</div>
							  </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">วันที่หมดอายุ:</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							      <%tabIndex++; %>
								   <input type='text' name='expireDate1' size='7' 
								   value='<%=Utils.isNull(stockMCBean.getExpireDate1())%>'
								   id="expireDate1"  readonly >
								</div>
							  </div>
					   
					    <!-- /Group1 -->
					    <!-- Group 2 -->
					    <div class="card mb-4 shadow-sm">
					      <div class="card-header">
					        <h5 class="my-0 font-weight-normal">สต๊อกสินค้า กลุ่มหมดอายุที่ 2</h5>
					      </div>
					        <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">หน้าร้าน :</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							    <%tabIndex++; %>
									<input  type="number" size="5"
									tabindex="<%=tabIndex %>"
									value="<%=Utils.isNull(stockMCBean.getFrontendQty2())%>" name="frontendQty2" size="1"
									onkeydown="return isNum0to9andpoint(this,event);"  id="frontendQty2"
									class="enableNumber" autocomplete="off"/>
								</div>
							  </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right"> หน่วยบรรจุ :</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							       <%tabIndex++; %>
									<select name="uom2" tabindex="<%=tabIndex %>" id="uom2">
									 <% for(int n=0;n<uomList.size();n++){ 
										 uom = uomList.get(n);
										 if(Utils.isNull(stockMCBean.getUom2()).equalsIgnoreCase(uom[0])){
									        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
										 }else{
											out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
										 }
									 }
									 %>
									</select>
								</div>
							  </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">วันที่หมดอายุ:</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							      <%tabIndex++; %>
								   <input type='text' name='expireDate2' size='7' 
								   value='<%=Utils.isNull(stockMCBean.getExpireDate2())%>'
								   id="expireDate2"  readonly >
								</div>
							  </div>
					    </div> 
					    <!-- /Group2 -->
					    <!-- Group 3 -->
					      <div class="card-header">
					        <h5 class="my-0 font-weight-normal">สต๊อกสินค้า กลุ่มหมดอายุที่ 3</h5>
					      </div>
					        <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">หน้าร้าน :</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							    <%tabIndex++; %>
									<input  type="number" size="5"
									tabindex="<%=tabIndex %>"
									value="<%=Utils.isNull(stockMCBean.getFrontendQty3())%>" name="frontendQty3" size="1"
									onkeydown="return isNum0to9andpoint(this,event);" id="frontendQty3"
									class="enableNumber" autocomplete="off"/>
								</div>
							  </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right"> หน่วยบรรจุ :</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							       <%tabIndex++; %>
									<select name="uom3" tabindex="<%=tabIndex %>" id="uom3">
									 <% for(int n=0;n<uomList.size();n++){ 
										 uom = uomList.get(n);
										 if(Utils.isNull(stockMCBean.getUom3()).equalsIgnoreCase(uom[0])){
									        out.println("<option selected value='"+uom[0]+"'>"+uom[1]+"</option>");
										 }else{
											out.println("<option value='"+uom[0]+"'>"+uom[1]+"</option>"); 
										 }
									 }
									 %>
									</select>
								</div>
							  </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">วันที่หมดอายุ:</div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
							      <%tabIndex++; %>
								   <input type='text' name='expireDate3' size='7' 
								   value='<%=Utils.isNull(stockMCBean.getExpireDate3())%>'
								   id="expireDate3"  readonly >
								</div>
							  </div>
					    <!-- /Group3 -->
					    
					  </div>
		             <!--  Input Form  -->
						
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								 <%if ( UserUtils.userInRole("ROLE_MC",user,new String[]{User.ADMIN, User.MC_ENTRY}) ){ %>
								   <%if(stockMCBean.isCanEdit()) {%>
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									  <input type="button" value="บันทึกรายการ" class="btn btn-primary">
									</a>	
								  <%} %>
								 
								  &nbsp;
								   <a href="#" onclick="clearForm('${pageContext.request.contextPath}');">
									  <input type="button" value="Clear"  class="btn btn-primary">
								    </a>
								    <%} %>
								   <a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									  <input type="button" value="ปิดหน้าจอ"  class="btn btn-primary">
								    </a>
								 </td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
						 <input type="hidden" name="pageName" value="<%=pageName %>"/>
						 <input type="hidden" name="mobile" value="<%=mobile %>"/>
					     <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
						 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
						<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
						</html:form>
						<!-- BODY -->
					
</body>
</html>
<%}catch(Exception e){
  e.printStackTrace();
}
	%>

