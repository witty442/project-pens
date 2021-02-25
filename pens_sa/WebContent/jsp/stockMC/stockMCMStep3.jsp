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
function gotoStockMCMStep2(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var fromPage = document.getElementById("fromPage").value;
	
	if("stockMCMSearch"==fromPage){
		form.action = path + "/jsp/stockMCAction.do?do=searchHead&action=back";
	}else{
	    form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep2&action=back";
	}
	form.submit();
}
//NextStep
function gotoStockMCMStep4(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var brand  = document.getElementById("brand");
	if(brand.value !=""){
	   form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep4&action=new";
	   form.submit();
	}else{
		alert("กรุณาระบุ แบรนด์");
		brand.focus();
		return false;
	}
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
	
		<span title="StockMCMStep3">...</span>
         <div class="card mb-1 shadow-sm">
                <div class="card-header bg-info"><b>บันทึกสต๊อกห้าง</b></div>
                <div class="card-header">วันที่นับสต๊อก: ${stockMCForm.bean.stockDate}
			         <html:hidden property="bean.stockDate" styleId="stockDate" />
			    </div>
                <div class="card-header">ห้าง: ${stockMCForm.bean.customerCode}-${stockMCForm.bean.customerName}
			         <html:hidden property="bean.customerCode" styleId="customerCode" />
				     <html:hidden property="bean.customerName" styleId="customerName" />
			    </div>
			     <div class="card-header">สาขา: ${stockMCForm.bean.storeCode}-${stockMCForm.bean.storeName}
				     <html:hidden property="bean.storeCode" styleId="storeCode"/>
					 <html:hidden property="bean.storeName" styleId="storeName"/>
			    </div>
			   <div class="card-header">
                   <input type="button" name="x1" value="   เลือกแบรนด์   " class="btn btn-primary"
						    onclick="openPopup('${pageContext.request.contextPath}','BrandStockMC','true')"/>
			    </div>
			    <div class="card-header">
			         <html:text property="bean.brand" styleId="brand" size="3" readonly="true"
					     styleClass="disableText"/>
				     <html:text property="bean.brandName" styleId="brandName"  readonly="true"
					  styleClass="disableText" />
			    </div>
	   
			    <p></p> 
			    <div class="card-header">
			       <input type="button" name="backBT" value=" ย้อนกลับ   " class="btn btn-primary"
						    onclick="gotoStockMCMStep2()"/>
                   <input type="button" name="nextBT" value=" หน้าถัดไป   " class="btn btn-success"
						    onclick="gotoStockMCMStep4()"/>
			    </div>
			    
		       <div class="card-header">
				    <a href="javascript:window.location='${pageContext.request.contextPath}/jsp/stockMC/stockMCMStep1.jsp'">
					    <input type="button" value="กลับหน้าหลัก " class="btn btn-primary"> 
				    </a>
			   </div>
		</div>     
		
	<!-- Hidden Field -->
	 <input type="hidden" name="pageName" value="<%=pageName %>"/>
	 <input type="hidden" name="mobile" value="<%=mobile %>"/>
     <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
	 <html:hidden property="fromPage" styleId="fromPage"/>
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

