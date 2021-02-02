<%@page import="com.isecinc.pens.web.stockmc.StockMCUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
try{
int tabIndex = 0;
StockMCBean bean = stockMCForm.getBean();
User user = (User)session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName")); 
String mobile = Utils.isNull(request.getParameter("mobile")); 
String action = Utils.isNull(request.getParameter("action")); 
StockMCBean stockMCBean = stockMCForm.getBean();
String fromPage = Utils.isNull(request.getParameter("fromPage")); 
if(fromPage.equals("")){
	fromPage =stockMCForm.getFromPage();
}

List<String[]> reasonNList = StockMCUtils.getReasonNList();
List<String[]> reasonDList = StockMCUtils.getReasonDList();

System.out.println("fromPage:"+fromPage);

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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_mobile_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<!-- Calendar -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMCMobile.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 

<!-- Calendar -->
<!-- Year =christDate(MC ONLY) -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/calendar/jquery.calendars.picker.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.plugin.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.plus.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker.js"></script> 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.thai-th.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar/jquery.calendars.picker-th.js"></script>

<!-- Bootstrap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css?v=<%=SIdUtils.getInstance().getIdSession()%>">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style>
</style>
<script type="text/javascript">

/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

function loadMe(){
	  $('#expireDate1').calendarsPicker({calendar: $.calendars.instance('','')});
	  $('#expireDate2').calendarsPicker({calendar: $.calendars.instance('','')});
	  $('#dateInStore').calendarsPicker({calendar: $.calendars.instance('','')});
	  //display div input by item_check
	  switchDivItemCheck();
	  
	  
	  document.getElementById("dateInStore").readonly = true;
	  document.getElementById("expireDate1").readonly = true;
	  document.getElementById("expireDate2").readonly = true;
	  
	  document.getElementById("note").value = "<%=Utils.isNull(stockMCBean.getNote())%>";
}
function switchDivItemCheck(){
	var itemCheck = document.getElementsByName("bean.itemCheck");
	var div_y = document.getElementById("div_y");
	var div_n = document.getElementById("div_n");
	var div_d = document.getElementById("div_d"); 
	//alert(itemCheck[0].checked+":"+itemCheck[1].checked+":"+itemCheck[2].checked);
	//alert(itemCheck[0].checked);
	if(itemCheck[0].checked){ //Y
		div_y.style.display = 'block'; 
		div_n.style.display = 'none'; 
		div_d.style.display = 'none'; 
	}else if(itemCheck[1].checked){ //N
		div_y.style.display = 'none'; 
		div_n.style.display = 'block'; 
		div_d.style.display = 'none'; 
	}else if(itemCheck[2].checked){ //D
		div_y.style.display = 'none'; 
		div_n.style.display = 'none'; 
		div_d.style.display = 'block'; 
	}
}
//back to step 4
function gotoStockMCMStep4(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	
	<%if(fromPage.equalsIgnoreCase("stockMCMSearchDetail")){%>
	    var param  = "&headerId="+document.getElementById("headerId").value;
	    form.action = path + "/jsp/stockMCAction.do?do=viewSearchDetail"+param;
	<%}else {%>
	    form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep4&action=back";
	<%}%>
	form.submit();
}
function saveByItemStockMC(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	//validate
	var itemCheck = document.getElementsByName("bean.itemCheck");
	if( !itemCheck[0].checked && !itemCheck[1].checked && !itemCheck[2].checked){
		alert("กรุณาระบุ ประเภทข้อมูล มี(YES)/ไม่มี(NO)/ไม่ได้นำสินค้าเข้ามาจำหน่าย(Deranged)");
		itemCheck[0].focus();
		return false;
	}
	
	if(itemCheck[1].checked){ //N
	   var reasonNId = document.getElementById("reasonNId");
	   if(reasonNId.value ==""){
		   alert("กรุณาระบุเหตุผล");
		   reasonNId.focus();
		   return false;
	   }
	}else if(itemCheck[2].checked){ //D
		 var reasonDId = document.getElementById("reasonDId");
		  if(reasonDId.value ==""){
			   alert("กรุณาระบุเหตุผล");
			   reasonDId.focus();
			   return false;
		  }
	}
	
	form.action = path + "/jsp/stockMCAction.do?do=saveByItemStockMC";
	form.submit();
}
function saveImage(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var imageFile = document.getElementById("imageFile");
	if(validateImageFile(imageFile)){
	   form.action = path + "/jsp/stockMCAction.do?do=saveImage&forward=stockMCMStep5";
	   form.submit();
	}return false;
}
function validateImageFile(input) {
  var file;
  //validate file type
   var extension = '';
    if(input.value != '' && input.value.indexOf('.') > 0){
       extension = input.value.substring(input.value.lastIndexOf('.') + 1).toLowerCase();
    }
    if(input.value != '' && (extension == 'png' 
    || extension == 'jpeg') || extension == 'jpg' || extension == 'bmp'  ){
    }else{
       alert('กรุณาเลือกไฟล์รูปภาพเท่านั้น นามสกุล  png ,jpeg ,jpg ,bmp เท่านั้น');
       input.value = "";
       return false;
    }
    return true;
}
function openImageFile(){
	var path = document.getElementById("path").value;
	var imageFileName = document.getElementById("imageFileName").value;
	var url =path+"/jsp/stockMC/showImage.jsp?fileName="+imageFileName;
	PopupCenter(url,"",600,600);
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();">

  <!-- Include Header Mobile  -->
  <jsp:include page="../templates/headerM.jsp"/>
  <!-- /Include Header Mobile -->
         
	<!-- BODY -->
	<html:form action="/jsp/stockMCAction" method="post" enctype="multipart/form-data">
	<jsp:include page="../error.jsp"/>
	
		<span title="StockMCMStep5">...</span>
		
		 <!-- Head Table -->
		      <!-- Hidden field -->
		       <html:hidden property="bean.id" styleId="headerId"/>
		       <html:hidden property="bean.stockDate" styleId="stockDate" />
		       <html:hidden property="bean.customerCode" styleId="customerCode" />
			   <html:hidden property="bean.customerName" styleId="customerName" />
			   <html:hidden property="bean.storeCode" styleId="storeCode"/>
			   <html:hidden property="bean.storeName" styleId="storeName"/>
		       <html:hidden property="bean.brand" styleId="brand" />
			   <html:hidden property="bean.brandName" styleId="brandName"/>
			   <html:hidden property="bean.lineId" styleId="lineId"/>
			   <html:hidden property="bean.productCode" styleId="productCode"/>
			   <html:hidden property="fromPage"/>
                <div class="card mb-1 shadow-sm">
				     <div class="card-header">วันที่นับสต๊อก:${stockMCForm.bean.stockDate}</div>
					 <div class="card-header">
					                  ห้าง:${stockMCForm.bean.customerCode}-${stockMCForm.bean.customerName}
				     </div>
				     <div class="card-header">
				                           สาขา:${stockMCForm.bean.storeCode}-${stockMCForm.bean.storeName}
				     </div>
					 <div class="card-header">
					                 แบรนด์:${stockMCForm.bean.brand}-${stockMCForm.bean.brandName} 
				     </div>
				     <div class="card mb-4 shadow-sm">
					       <p class="lead">
					                     เพิ่ม/แก้ไข รูปภาพ :<html:file property="imageFile" styleId="imageFile" accept="image/*;capture=camera"/> 
					       </p> 
					       
					       <% if( !Utils.isNull(bean.getImageFileName()).equals("")){ %>
					            <input type="hidden" name="imageFileName" id="imageFileName" value="<%=bean.getImageFileName()%>"/>
					            <p class="lead">
					               <a href="javascript:openImageFile()">
					                 <img loading="lazy" id="imageFileDisp" width="80" height="50"
								     src="${pageContext.request.contextPath}/photoServlet?pageName=stockMC&fileName=<%=bean.getImageFileName()%>" />
								   </a>
								</p>
					       <%} %>
					       <div class="card-header">
						      <input type="button" name="saveImageBT" value="บันทึกรูปภาพ " class="btn btn-primary"
								 onclick="saveImage()"/>
						  </div>
		            </div>
					 <div class="card-header">
				            <input type="button" name="backBT" value=" ย้อนกลับ   " class="btn btn-primary"
							    onclick="gotoStockMCMStep4()"/>
							    
							<a href="javascript:window.location='${pageContext.request.contextPath}/jsp/stockMC/stockMCMStep1.jsp'">
					          <input type="button" value="กลับหน้าหลัก " class="btn btn-primary"> 
				            </a>
				     </div>
			   </div>
			   
		          <!--  Input Form  -->
					  <!-- Select Item Check -->
					   <div class="card mb-1 shadow-sm">
					       <div class="card-header">
					         <h5 class="my-0 font-weight-normal">
					                        สินค้า: ${stockMCForm.bean.productName} /
					            Barcode: ${stockMCForm.bean.barcode} 
					         </h5>
					      </div> 
					        <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">
							       <html:radio property="bean.itemCheck" styleId="itemCheck" value="Y" onchange="switchDivItemCheck()"></html:radio>
							    </div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
									มี(YES)
								</div>
							 </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">
							       <html:radio property="bean.itemCheck" styleId="itemCheck" value="N" onchange="switchDivItemCheck()"></html:radio>
							    </div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
									ไม่มี(NO)
								</div>
							 </div>
							  <div class="row mb-1">
							    <div class="col-4 themed-grid-col-detail" align="right">
							       <html:radio property="bean.itemCheck" styleId="itemCheck" value="D" onchange="switchDivItemCheck()"></html:radio>
							    </div>
							    <div class="col-6 themed-grid-col-detail" align="left"> 
									ไม่ได้นำสินค้าเข้ามาจำหน่าย(Deranged)
								</div>
							 </div>
					   </div>
					   
					   <!-- *** 1: YES *******************************-->
					   <div class="card mb-4 shadow-sm" id="div_y" style="display: none;">
					        <%-- <div class="input-group">
							    <div class="input-group-prepend">
							      <span class="input-group-text">ขา ที่ต้องมี  :</span>
							    </div>
								<input  size="5"  name="masterLegQty"  id="masterLegQty" readonly="true" 
								  value ="<%=Utils.isNull(stockMCBean.getMasterLegQty())%>" size="1"  
								  class="form-control" />	 
							  </div> --%>
							  
					       <div class="input-group">
							    <div class="input-group-prepend">
							      <span class="input-group-text">ขา ที่มีอยู่ :</span>
							    </div>
							     <%tabIndex++; %>
								 <input  type="number" size="5"  name="legQty" tabindex="<%=tabIndex %>" id="legQty" 
									 value ="<%=Utils.isNull(stockMCBean.getLegQty())%>" size="1"  
									 onkeydown="return isNum0to9andpoint(this,event);" 
									 autocomplete="off" class="form-control"/>	 
							  </div>
							  
							 <div class="input-group">
							    <div class="input-group-prepend">
							      <span class="input-group-text">จำนวน :</span>
							    </div>
							       <%tabIndex++; %>
									<input  type="number" size="5"
									tabindex="<%=tabIndex %>"
									value="<%=Utils.isNull(stockMCBean.getFrontendQty1())%>" name="frontendQty1" size="1"
									onkeydown="return isNum0to9andpoint(this,event);"  id="frontendQty1"
									 autocomplete="off" class="form-control"/>
									 
									<div class="input-group-prepend">
							          <span class="input-group-text">
							            <%=Utils.isNull(stockMCBean.getUom()) %>.
									  </span>
							       </div>
							 </div>
							 
					        <div class="input-group">
							    <div class="input-group-prepend">
							      <span class="input-group-text">วันที่หมดอายุ(1) :</span>
							    </div>
							       <%tabIndex++; %>
								    <input type='text' name='expireDate1' size='7' 
								     value='<%=Utils.isNull(stockMCBean.getExpireDate1())%>'
								     id="expireDate1"  class="form-control" autoComplete="off">	
							</div>
							
					         <div class="input-group">
							    <div class="input-group-prepend">
							      <span class="input-group-text">จำนวน :</span>
							    </div>
							       <%tabIndex++; %>
									<input  type="number" size="2"
									tabindex="<%=tabIndex %>"
									value="<%=Utils.isNull(stockMCBean.getFrontendQty2())%>" name="frontendQty2" 
									onkeydown="return isNum0to9andpoint(this,event);"  id="frontendQty2"
									autocomplete="off"  class="form-control"/>
									
									<div class="input-group-prepend">
							         <span class="input-group-text">
							            <%=Utils.isNull(stockMCBean.getUom()) %>.
									 </span>
							       </div>
							 </div>
					         <div class="input-group">
							    <div class="input-group-prepend">
							      <span class="input-group-text">วันที่หมดอายุ(2) :</span>
							    </div> 
							       <%tabIndex++; %>
								    <input type='text' name='expireDate2' size='7' 
								     value='<%=Utils.isNull(stockMCBean.getExpireDate2())%>'
								     id="expireDate2"  class="form-control" autoComplete="off">	
							</div>
					  </div>
					  <!-- /*** 1: YES *******************************-->
					  
					  <!-- *** 2: NO *******************************-->
					   <div class="card mb-4 shadow-sm" id="div_n" style="display: none;">
					      <div class="input-group">
							  <div class="input-group-prepend">
							    <span class="input-group-text"><font color="red">*</font>เหตุผล  :</span>
							  </div>
								<%tabIndex++; %>
								<select name="reasonNId" tabindex="<%=tabIndex %>" id="reasonNId" class="form-control">
								 <% for(int n=0;n<reasonNList.size();n++){ 
									 String[] reasonN = reasonNList.get(n);
									 if(Utils.isNull(stockMCBean.getReasonNId()).equalsIgnoreCase(reasonN[0])){
								        out.println("<option selected value='"+reasonN[0]+"'>"+reasonN[1]+"</option>");
									 }else{
										out.println("<option value='"+reasonN[0]+"'>"+reasonN[1]+"</option>"); 
									 }
								 }
								 %>
								</select>
						    </div>
						    <div class="input-group">
							      <div class="input-group-prepend">
								    <span class="input-group-text">วันที่สินค้าเข้า  :</span>
								  </div>
								  <input type='text' name='dateInStore' size='7' class="form-control" 
									     value='<%=Utils.isNull(stockMCBean.getDateInStore())%>'
									     id="dateInStore" autoComplete="off"> 
						     </div>
						     <div class="input-group">
							     <div class="input-group-prepend">
								    <span class="input-group-text">จำนวน  :</span>
								 </div>
							     <%tabIndex++; %>
								 <input  type="number" size="5"  name="dateInStoreQty" tabindex="<%=tabIndex %>" id="dateInStoreQty" 
								 value ="<%=Utils.isNull(stockMCBean.getDateInStoreQty())%>" size="1"  class="form-control" 
								 onkeydown="return isNum0to9andpoint(this,event);" autocomplete="off"/>	
							 
					      </div>
					  </div>
					  <!-- /*** 2: NO *******************************-->
					  
					   <!-- *** 3: D *******************************-->
					   <div class="card mb-4 shadow-sm" id="div_d" style="display: none;">
						   <div class="input-group">
							  <div class="input-group-prepend">
							    <span class="input-group-text"><font color="red">*</font>เหตุผล:</span>
							  </div>
							   <%tabIndex++; %>
								<select name="reasonDId" tabindex="<%=tabIndex %>" id="reasonDId" class="form-control">
								 <% for(int n=0;n<reasonDList.size();n++){ 
									 String[] reasonD = reasonDList.get(n);
									 if(Utils.isNull(stockMCBean.getReasonDId()).equalsIgnoreCase(reasonD[0])){
								        out.println("<option selected value='"+reasonD[0]+"'>"+reasonD[1]+"</option>");
									 }else{
										out.println("<option value='"+reasonD[0]+"'>"+reasonD[1]+"</option>"); 
									 }
								 }
								 %>
								</select>
						  </div>
					  </div>
					  <!-- /*** 3: D *******************************-->
				
					<div class="input-group">
					  <div class="input-group-prepend">
					    <span class="input-group-text">Note   :</span>
					  </div>
					  <textarea class="form-control" id="note" name="note" rows="2">
					    
					  </textarea>
					</div>
					<%
					 System.out.println("Note["+Utils.isNull(stockMCBean.getNote())+"]");
					%>
			    <%if(stockMCBean.isCanEdit()) {%>
				     <div class="card mb-1 shadow-sm">
					     <div class="card-header">
							  <input type="button" name="saveBT" value="บันทึกข้อมูล และไปที่หน้าร้านค้า" class="btn btn-primary"
							  onclick="saveByItemStockMC()"/>
					     </div>
			             <div class="card-header">
					          <input type="button" name="backBT" value=" ไม่บันทึก กลับไปที่หน้า รายการสินค้า  " class="btn btn-primary"
							   onclick="gotoStockMCMStep4()"/>
					     </div>
			        </div>
		       <%} %>
	<!-- Hidden Field -->
	 <input type="hidden" name="pageName" value="<%=pageName %>"/>
	 <input type="hidden" name="mobile" value="<%=mobile %>"/>
     <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
	
	</html:form>
	<!-- BODY -->
	
	 <!-- Include Footer Mobile  -->
     <jsp:include page="../templates/footerM.jsp"/>
     <!-- /Include Footer Mobile -->	
					
</body>
</html>
<%}catch(Exception e){
  e.printStackTrace();
}
%>

