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

/** list detail of product **/
function gotoStockMCMStep5(headerId,lineId,productCode,productName,barcode,brand,brandName){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	//set selected lineId
	document.getElementById("selectHeaderId").value = headerId;
	document.getElementById("selectLineId").value = lineId;
	document.getElementById("selectProductCode").value = productCode;
	document.getElementById("selectProductName").value = productName;
	document.getElementById("selectBarcode").value = barcode;
	document.getElementById("selectBrand").value = brand;
	document.getElementById("selectBrandName").value = brandName;
	form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep5&fromPage=stockMCMSearchDetail";
	form.submit();
}

function gotoStockMCMSearch(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	
	form.action = path + "/jsp/stockMCAction.do?do=searchHead&action=back&pageName=STOCKMC";
	form.submit();
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();" style="height: 100%;">

            <!-- Include Header Mobile  -->
            <jsp:include page="../templates/headerM.jsp"/>
            <!-- /Include Header Mobile -->
            
			<!-- BODY -->
			<html:form action="/jsp/stockMCAction">
			<jsp:include page="../error.jsp"/>
			
			
			<!-- Hidden -->
			 <input type="hidden" name="selectHeaderId" id="selectHeaderId"/>
             <input type="hidden" name="selectLineId" id="selectLineId"/>
			 <input type="hidden" name="selectProductCode" id="selectProductCode"/>
			 <input type="hidden" name="selectProductName" id="selectProductName"/>
			 <input type="hidden" name="selectBarcode" id="selectBarcode"/>
			 <input type="hidden" name="selectBrand" id="selectBrand"/>
			 <input type="hidden" name="selectBrandName" id="selectBrandName"/>
             <input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
               <!-- Head -->
               <div class="card mb-1 shadow-sm">
                 <div class="card-header bg-info"><b>ประวัติบันทึกสต๊อกห้าง</b></div>
			     <div class="card-header">วันที่นับสต๊อก: ${stockMCForm.bean.stockDate}</div>
			     <div class="card-header">ห้าง: ${stockMCForm.bean.customerCode}-${stockMCForm.bean.customerName}</div>
		         <div class="card-header">สาขา: ${stockMCForm.bean.storeCode}-${stockMCForm.bean.storeName}</div>
			   
			     
				 <div class="card-header">
			        <input type="button" name="backBT" value=" ย้อนกลับ   " class="btn btn-primary"
						   onclick="gotoStockMCMSearch()"/>
			     </div>
			   </div>
			        
		        <!-- Head Table -->
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
				       <td  width="100%" nowrap >
					        <div id="div_<%=i %>" > 
					            <a class='<%=tabClassSave%>' href="javascript:gotoStockMCMStep5(<%=b.getId() %>,'<%=b.getLineId() %>'
					             ,'<%=b.getProductCode()%>','<%=b.getProductName()%>','<%=b.getBarcode()%>'
					             ,'<%=b.getBrand()%>','<%=b.getBrandName()%>')">
					             
					            <%=b.getProductCode()%>-<%=b.getProductName()%> <br/>/ Barcode: <%=b.getBarcode()%>
					            </a>
					        </div>
						</td>
					</tr>				
				 <%}} %>	
				<!-- /Row Table -->
				 </tbody>
			   </table>
			</div>
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

