<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
try{
int tabIndex = 0;
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
function gotoStockMCMStep3Select(customerCode,storeCode,customerName,storeName){
	document.getElementById("customerCode").value =customerCode;
	document.getElementById("storeCode").value=storeCode;
	document.getElementById("customerName").value =customerName;
	document.getElementById("storeName").value=storeName;
	gotoStockMCMStep3();
}
//NextStep
function gotoStockMCMStep3(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var customerCode  = document.getElementById("customerCode");
	var storeCode  = document.getElementById("storeCode");
	if(customerCode.value !="" && storeCode.value != ""){
	  form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep3&action=new";
	  form.submit();
	}else{
		if(customerCode.value == "" && storeCode.value ==""){
		   alert("กรุณาระบุ ห้าง ,สาขา ");
		   customerCode.focus();
		   return false;
		}else{
			if(customerCode.value == ""){
				alert("กรุณาระบุ ห้าง ");
				customerCode.focus();
				return false;
			}
			if(storeCode.value == ""){
				alert("กรุณาระบุ สาขา");
				storeCode.focus();
				return false;
			}
		}
	}
}
function gotoStockMCMStep1(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep1&action=new";
	form.submit();
} 
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();">

  <!-- Include Header Mobile  -->
  <jsp:include page="../templates/headerM.jsp"/>
  <!-- /Include Header Mobile -->
         
	<!-- BODY -->
	<html:form action="/jsp/stockMCAction">
	<jsp:include page="../error.jsp"/>

		 <span title="StockMCMStep2">...</span>
		 <div class="card mb-1 shadow-sm">
		       <div class="card-header bg-info"><b>บันทึกสต๊อกห้าง</b></div>
		       <div class="card-header">
		                         วันที่นับสต๊อก: ${stockMCForm.bean.stockDate}
			      <html:hidden property="bean.stockDate" styleId="stockDate" />
		      </div> 
		      <div class="card-header">
		          <input type="button" name="x1" value="   เลือกห้าง   " class="btn btn-primary"
						 onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC','true')"/>
		      </div>
		      <div class="card-header">
		           <html:text property="bean.customerCode" styleId="customerCode" size="3" readonly="true"
					     styleClass="disableText"/>-
				   <html:text property="bean.customerName" styleId="customerName" readonly="true"
					     styleClass="disableText" />
		      </div>
		      <div class="card-header">
		           <input type="button" name="x2" value="   เลือกสาขา   " class="btn btn-primary"
						  onclick="openPopup('${pageContext.request.contextPath}','BranchStockMC','true')"/>
		      </div>
		      <div class="card-header">
		           <html:text property="bean.storeCode" styleId="storeCode" size="3" readonly="true"
					     styleClass="disableText"/>-
				   <html:text property="bean.storeName" styleId="storeName"  readonly="true"
					     styleClass="disableText" />
		      </div>
		      <div class="card-header">
		           <input type="button" name="backBT" value=" ย้อนกลับ   " class="btn btn-primary"
					      onclick="gotoStockMCMStep1()"/> 
				   <input type="button" name="nextBT" value=" หน้าถัดไป   " class="btn btn-success"
					      onclick="gotoStockMCMStep3()"/>
		      </div>
		       <div class="card-header">
		      </div>
		</div>
  <%
	if(1==1 && stockMCForm.getRouteList() != null && stockMCForm.getRouteList().size() >0){ %>
      <div class="table-responsive">
			 <table id="tblProduct" class="table table-hover table-fixed">
			    <thead>
			      <tr>
			         <th class="bg-info">ชื่อเส้นทาง </th>	
			         <th class="bg-info">ห้าง/สาขา</th>	
				 </tr>
				</thead>
			  <!-- /Head Table -->
			  <!-- Row Table -->
			   <tbody>
			  <%
				 List<StockMCBean> results = stockMCForm.getRouteList();
				 String tabclass = "";
				 StockMCBean b = null;
				 for(int i=0;i<results.size();i++){
					b = results.get(i);
					tabclass=i%2==0?"bg-light":"";
					%>
				<tr class="<%=tabclass%>">
				  <td  width="30%" nowrap>
				        <div>
				          <%=b.getRouteName()%>               
				        </div>
					 </td>
			       <td  width="30%" nowrap>
				       <div>
				           <a  href="#" 
				             onclick ="gotoStockMCMStep3Select('<%=b.getCustomerCode()%>','<%=b.getStoreCode()%>'
				             ,'<%=b.getCustomerName()%>','<%=b.getStoreName()%>')">
				             
				              <%=b.getCustomerCode()%>-<%=b.getCustomerName()%><br/> 
				              / <%=b.getStoreCode()%>-<%=b.getStoreName()%>
				          </a>
				        </div>
					 </td>
				</tr>				
			 <%} %>	
			<!-- /Row Table -->
			 </tbody>
		   </table>
		</div>
	  <%} %>		
	  <!-- Hidden Field -->
	  <input type="hidden" name="pageName" value="<%=pageName %>"/>
	  <input type="hidden" name="mobile" value="<%=mobile %>"/>
      <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
	  <input type="hidden" id="nextStep" name="nextStep"/>
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

