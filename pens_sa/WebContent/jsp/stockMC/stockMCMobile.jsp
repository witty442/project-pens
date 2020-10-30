<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
try{
int tabIndex = 0;
String[] uom = null;
if(stockMCForm.getResults() != null){
	tabIndex = stockMCForm.getResults().size()*2;
}

List<String[]> uomList = StockMCUtils.getUomList();
User user = (User)session.getAttribute("user");
String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
	int screenW = new Double(screenWidth).intValue();
	if(screenW <=800){
		screenW = 800;
	}
	//screenWidth = ""+(screenW-50);
}
String pageName = Utils.isNull(request.getParameter("pageName")); 
String mobile = Utils.isNull(request.getParameter("mobile")); 
String action = Utils.isNull(request.getParameter("action")); 
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 

<!-- Bootstrap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style>
.stock_save{
   color: green;
}
.stock_no_save{
  color: blue;
}
</style>
<script type="text/javascript">

/** disable back button alway **/
window.location.hash="no-back-button";
window.location.hash="Again-No-back-button";//again because google chrome don't insert first hash into history
window.onhashchange=function(){window.location.hash="no-back-button";}

function loadMe(){
	
}
function backsearch(path) {
    document.stockMCForm.action = path + "/jsp/stockMCAction.do?do=searchHead&action=back";
	document.stockMCForm.submit();
}
function clearForm(path){
	var form = document.stockMCForm;
    var param ="&action=add";
	
	form.action = path + "/jsp/stockMCAction.do?do=clearForm"+param;
	form.submit();
	return true;
}
function loadItem(path){
	var form = document.stockMCForm;
    var param ="";
	if(form.customerCode.value ==''){
		alert("กรุณาระบุห้าง ");
		form.customerCode.focus();
		return false;
	}
	form.action = path + "/jsp/stockMCAction.do?do=loadItemMobile"+param;
	form.submit();
	return true;
}
function openStockMCMobile(action,lineId,productCode,index){
   var param = "action="+action+"&headerId="+document.getElementById("headerId").value+"&lineId="+lineId;
	   param +="&stockDate="+document.getElementById("stockDate").value;
	   param +="&mcName="+document.getElementById("mcName").value;
	   param +="&customerCode="+document.getElementById("customerCode").value;
	   param +="&customerName="+document.getElementById("customerName").value;
	   param +="&storeCode="+document.getElementById("storeCode").value;
	   param +="&productCode="+productCode+"&index="+index;
	   //alert(param);
	 var url = "${pageContext.request.contextPath}/jsp/stockMC/stockMCMobileDetail.jsp?"+param;
	 
	 PopupCenterFull(encodeURI(url),"Add Detail Mobile");
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

			<!-- BODY -->
			<html:form action="/jsp/stockMCAction">
			<jsp:include page="../error.jsp"/>
			
			<!-- Hidden -->
             <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
			 <input type="hidden" id="tabIndex" name="tabIndex" value="<%=tabIndex%>"/>
			 <input type="hidden" name="TokenKey" value="<%= session.getAttribute(Globals.TRANSACTION_TOKEN_KEY) %>" >
             <html:hidden property="bean.id" styleId="headerId"/>
                   
                   <!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							   <a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
								  <input type="button" value="ปิดหน้าจอ"  class="btn btn-primary">
							    </a>
							 </td>
						</tr>
					</table>
                   <!-- Head -->
                   <div class="container">
                      <h5><u><b>เพิ่ม/แก้ไข เช็คสินค้าห้าง</b></u></h5>
					  <div class="form-group row">
						   <div class="col-xs-2">
			                   <label for="stockDate"> วันที่ตรวจนับสต๊อก :</label>
							       <html:text property="bean.stockDate" styleId="stockDate" 
				                   readonly="true" styleClass="disableText" />	
						    </div>
						    <div class="col-xs-12">
						      <%if("add".equalsIgnoreCase(action)){ %>
			                        <label for="mcName">  ชื่อ-นามสกุล พีซี <font color="red">*</font>:</label>
							        <html:text property="bean.mcName" styleId="mcName"  styleClass="\" autoComplete=\"off"/>
							  <%}else{ %>
							       <label for="mcName">  ชื่อ-นามสกุล พีซี :</label>
							        <html:text property="bean.mcName" styleId="mcName" readonly="true"  styleClass="disableText \" autoComplete=\"off"/>
							  <%} %>
						    </div>
						    <div class="col-xs-6">
						      <%if("add".equalsIgnoreCase(action)){ %>
			                      <label for="customerCode"> ห้าง<font color="red">*</font>  :</label>
							        <html:text property="bean.customerCode" styleId="customerCode" 
									    styleClass=" \" autoComplete=\"off"
										onkeypress="getCustNameKeypress(event,this)"/>
									<input type="button" name="x1" value="..." class="btn btn-primary"
									    onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>
							   <%}else{ %>
							       <label for="customerCode"> ห้าง:</label>
							        <html:text property="bean.customerCode" styleId="customerCode" 
									    styleClass="disableText \" autoComplete=\"off" />
							   <%} %>
						    </div>
						    <div class="col-xs-12">
			                   <label for="customerName"> ชื่อห้าง :</label>
							   <html:text property="bean.customerName" styleId="customerName"  readonly="true"
								  styleClass="disableText" size="25"/>
						    </div>
						   <div class="col-xs-12">
						      <%if("add".equalsIgnoreCase(action)){ %>
			                        <label for="customerName">  สาขา<font color="red">*</font>:</label>
							        <html:text property="bean.storeCode" styleId="storeCode"  styleClass="\" autoComplete=\"off"/>
							  <%}else{ %>
							       <label for="customerName">  สาขา:</label>
							        <html:text property="bean.storeCode" styleId="storeCode" readonly="true"  styleClass="disableText \" autoComplete=\"off"/>
							  <%} %>
						    </div>
					 </div>
				</div>
		
			
			      <!-- /Head -->
					<c:if test="${stockMCForm.bean.canEdit =='true'}">
				       <c:if test="${stockMCForm.mode =='add'}">
					   <div class="card mb-4 shadow-sm">
					       <div class="card-header">
					           <input type="button" class="btn btn-primary" value="แสดงรายการสินค้า" onclick="loadItem('${pageContext.request.contextPath}');"/>
					      </div> 
				        </div>
			         </c:if>
			        </c:if>
			        
				    <div class="card mb-4 shadow-sm">
				       <p class="lead">
				                   รายการสินค้า (กดที่ชื่อสินค้า เพื่อทำการ บันทึกหรือ ดู สต๊อกสินค้า)
				      </p> 
			        </div>
				  <!--  Results  -->
			
		          <!-- Head Table -->
				  <div class="table-responsive">
				   <table id="tblProduct" class="table table-hover table-fixed">
				      <thead>
				       <tr>
				            <th class="bg-info">รายละเอียดสินค้า</th>	
				            <th class="bg-info">บรรจุ</th>
							<th class="bg-info">อายุสินค้า</th>
							<th class="bg-info">ราคาปลีก</th>
					   </tr>
					  </thead>
				  <!-- /Head Table -->
				  <!-- Row Table -->
				   <tbody>
				  <%
					if(1==1 && stockMCForm.getResults() != null && stockMCForm.getResults().size() >0){
					 List<StockMCBean> results = stockMCForm.getResults();
					 String tabclass = "";StockMCBean b = null;
					 for(int i=0;i<results.size();i++){
						b = results.get(i);
					
						if(i%2==0){ 
							tabclass="bg-light";
						}else{
							tabclass ="";
						}
						%>
					<tr class="<%=tabclass%>">
				       <td  width="30%" nowrap>
							<%if(b.getLineId() != 0){ %>
					          <div id="div_<%=i %>" class="stock_save" onclick ="openStockMCMobile('edit','<%=b.getLineId() %>','<%=b.getProductCode()%>','<%=i%>')">
					              <%=b.getProductCode()%> / <%=b.getBarcode()%><br/><%=b.getProductName()%>
					          </div>
				             <%}else{ %>
				                <div id="div_<%=i %>" class="stock_no_save" onclick ="openStockMCMobile('add','<%=b.getLineId() %>','<%=b.getProductCode()%>','<%=i%>')">
					              <%=b.getProductCode()%> / <%=b.getBarcode()%><br/><%=b.getProductName()%>
					            </div>
				             <%} %>
				           
					         <input type="hidden" name="productName" id="productName" value ="<%=b.getProductName()%>" size="20" readonly class="disableText"/>	 
						     <input type="hidden" name="barcode" id="barcode" value ="<%=b.getBarcode()%>" size="11" 
								    class="disableText"  tabindex="<%=tabIndex%>" autoComplete="off" readonly  
							 /> 
							 <input type="hidden" name="productCode" id="productCode" value ="<%=b.getProductCode()%>" /> 
							 <input type="hidden" name="lineId" id="lineId" value ="<%=b.getLineId()%>"/>
						 </td>
						 <td  width="5%">
						    <%=b.getProductPackSize()%>
							<input type="hidden" name="productPackSize" id="productPackSize" value ="<%=b.getProductPackSize()%>" size="4" readonly class="disableNumber"/>		 
						 </td>
						 <td  width="5%">
						    <%=b.getProductAge()%>
							 <input type="hidden" name="productAge" id="productAge" value ="<%=b.getProductAge()%>" size="3" readonly class="disableText"/>	  		 
						 </td>
						 <td  width="5%">
							<%=b.getRetailPriceBF()%>
					        <input type="hidden" name="retailPriceBF" id="retailPriceBF" value ="<%=b.getRetailPriceBF()%>" 
							 size="3" readonly class="disableNumber"/> 
						 </td>
						
					</tr>				
				 <%}} %>	
				<!-- /Row Table -->
				 </tbody>
			   </table>
			</div>
			 
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="center">
					   <a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
						  <input type="button" value="ปิดหน้าจอ"  class="btn btn-primary">
					    </a>
					 </td>
				</tr>
			</table>
			
	<!-- Hidden Field -->
	 <html:hidden property="bean.id"/>
	 <html:hidden property="bean.lineIdDeletes" styleId="lineIdDeletes"/>
	 <input type="hidden" name="pageName" value="<%=pageName %>"/>
	 <input type="hidden" name="mobile" value="<%=mobile %>"/>
     <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
	<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
	</html:form>
	<!-- BODY -->
					
</body>
</html>
<%}catch(Exception e){
  e.printStackTrace();
}
	%>

