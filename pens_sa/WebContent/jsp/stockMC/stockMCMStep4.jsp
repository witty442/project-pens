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
	
}
//back
function gotoStockMCMStep3(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep3&action=back";
	form.submit();
}
//next
function gotoStockMCMStep5(lineId,productCode,productName,barcode){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	//set selected lineId
	document.getElementById("selectLineId").value = lineId;
	document.getElementById("selectProductCode").value = productCode;
	document.getElementById("selectProductName").value = productName;
	document.getElementById("selectBarcode").value = barcode;
	
	form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep5&action=new";
	form.submit();
}
function saveImage(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var imageFile = document.getElementById("imageFile");
	if(validateImageFile(imageFile)){
	   form.action = path + "/jsp/stockMCAction.do?do=saveImage&forward=stockMCMStep4";
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
	
		<span title="StockMCMStep4">...</span>
	      <!-- Hidden field -->
	       <html:hidden property="bean.id" styleId="headerId"/>
	       <html:hidden property="bean.stockDate" styleId="stockDate" />
	       <html:hidden property="bean.customerCode" styleId="customerCode" />
		   <html:hidden property="bean.customerName" styleId="customerName" />
		   <html:hidden property="bean.storeCode" styleId="storeCode"/>
		   <html:hidden property="bean.storeName" styleId="storeName"/>
	       <html:hidden property="bean.brand" styleId="brand" />
		   <html:hidden property="bean.brandName" styleId="brandName"/>
			
		   <!-- for use check selectRow action -->
		   <input type="hidden" name="selectLineId" id="selectLineId"/>
		   <input type="hidden" name="selectProductCode" id="selectProductCode"/>
		   <input type="hidden" name="selectProductName" id="selectProductName"/>
		   <input type="hidden" name="selectBarcode" id="selectBarcode"/>
				
				<div class="card mb-1 shadow-sm">
				    <div class="card-header">วันที่นับสต๊อก: ${stockMCForm.bean.stockDate}</div>
				    <div class="card-header">ห้าง: ${stockMCForm.bean.customerCode}-${stockMCForm.bean.customerName}</div>
			        <div class="card-header">สาขา: ${stockMCForm.bean.storeCode}-${stockMCForm.bean.storeName}</div>
				    <div class="card-header">แบรนด์: ${stockMCForm.bean.brand}-${stockMCForm.bean.brandName}</div>
			    
					<div class="card-header">
				        <input type="button" name="backBT" value=" ย้อนกลับ   " class="btn btn-primary"
							   onclick="gotoStockMCMStep3()"/>
						<a href="javascript:window.location='${pageContext.request.contextPath}/jsp/stockMC/stockMCMStep1.jsp'">
					    <input type="button" value="กลับหน้าหลัก " class="btn btn-primary"> 
				    </a>
				    </div>
				</div>
				
				<div class="card mb-4 shadow-sm">
			       <p class="lead">
			                      เพิ่ม/แก้ไข รูปภาพ :<html:file property="imageFile" styleId="imageFile" accept="image/*;capture=camera"/> 
			       </p> 
			       
			       <% if( !Utils.isNull(bean.getImageFileName()).equals("")){ %>
			            <input type="hidden" name="imageFileName" id="imageFileName" value="<%=bean.getImageFileName()%>"/>
			            <p class="lead">
			               <a href="javascript:openImageFile()">
			                 <img id="imageFileDisp" width="80" height="50"
						     src="${pageContext.request.contextPath}/photoServlet?pageName=stockMC&fileName=<%=bean.getImageFileName()%>" />
						   </a>
						</p>
			       <%} %>
			       <div class="card-header">
				      <input type="button" name="saveImageBT" value="บันทึกรูปภาพ" class="btn btn-primary"
						 onclick="saveImage()"/>
				  </div>
		        </div>
		        
			    <div class="table-responsive">
				 <table id="tblProduct" class="table table-hover table-fixed">
				    <thead>
				      <tr>
				         <th class="bg-info">รายละเอียดสินค้า</th>	
					 </tr>
					</thead>
				  <!-- /Head Table -->
				  <!-- Row Table -->
				   <tbody>
				  <%
					if(1==1 && stockMCForm.getResults() != null && stockMCForm.getResults().size() >0){
					 List<StockMCBean> results = stockMCForm.getResults();
					 String tabclass = "";StockMCBean b = null;
					 String tabClassSave = "";
					 for(int i=0;i<results.size();i++){
						b = results.get(i);
						tabclass=i%2==0?"bg-light":"";
						tabClassSave=b.getLineId() != 0?"text-success":"";
						%>
					<tr class="<%=tabclass%>">
				       <td  width="30%" nowrap>
					          <div id="div_<%=i %>" class="stock_save" >
					           <a class="<%=tabClassSave%>" href="#" onclick ="gotoStockMCMStep5('<%=b.getLineId() %>','<%=b.getProductCode()%>'
					             ,'<%=b.getProductName()%>','<%=b.getBarcode()%>')">
					             
					              <%=b.getProductName()%><br/> 
					              Barcode: <%=b.getBarcode()%>
					             <%--  lineId:<%=b.getLineId() %> --%>
					            </a>
					          </div>
				           
					         <input type="hidden" name="productName" id="productName" value ="<%=b.getProductName()%>" size="20" readonly class="disableText"/>	 
						     <input type="hidden" name="barcode" id="barcode" value ="<%=b.getBarcode()%>" /> 
							 <input type="hidden" name="productCode" id="productCode" value ="<%=b.getProductCode()%>" /> 
							 <input type="hidden" name="lineId" id="lineId" value ="<%=b.getLineId()%>"/>
							 <input type="hidden" name="productPackSize" id="productPackSize" value ="<%=b.getProductPackSize()%>" />		 
							 <input type="hidden" name="productAge" id="productAge" value ="<%=b.getProductAge()%>" />	
					         <input type="hidden" name="retailPriceBF" id="retailPriceBF" value ="<%=b.getRetailPriceBF()%>"/> 
						 </td>
					</tr>				
				 <%}} %>	
				<!-- /Row Table -->
				 </tbody>
			   </table>
			</div>
			
		  <p></p>
          <div class="container">
		       
			    <div class="col-xs-12">
			        <input type="button" name="backBT" value=" ย้อนกลับ   " class="btn btn-primary"
						    onclick="gotoStockMCMStep3()"/>
			    </div>
		</div>     
		
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

