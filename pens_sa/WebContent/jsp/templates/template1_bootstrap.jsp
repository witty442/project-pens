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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/stockMC.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script> 

<!-- Bootstrap -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap/bootstrap-4.5.2.min.css?v=<%=SIdUtils.getInstance().getIdSession()%>">
<script src="${pageContext.request.contextPath}/js/bootstrap/bootstrap-4.5.2.min.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap/grid.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />


<script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/js/all.min.js" crossorigin="anonymous"></script>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/bootstrap_styles.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

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
	form.action = path + "/jsp/stockMCAction.do?do=stockMCMStep2&action=new";
	form.submit();
}

function gotoStockMCMSearch(){
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/stockMCAction.do?do=prepareSearch&action=new&pageName=STOCKMC";
	form.submit();
	return true;
}
</script>
</head>
<body onload="loadMe();" class="sb-nav-fixed">

  <!-- BODY -->
  <html:form action="/jsp/stockMCAction">
  <jsp:include page="../error.jsp"/>
			
	   <!-- Include Header Mobile  -->
       <jsp:include page="../templates/headerMT.jsp"/>
       <!-- /Include Header Mobile -->
       
			 <!-- Content -->
			 <div id="layoutSidenav_content">
                <main>
                    <div class="container-fluid">
                        <h1 class="mt-4">test </h1>
                        <table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
							   <a href="#" onclick="gotoStockMCMStep2();">
								  <input type="button" value="เริ่มกรอกข้อมูลสต๊อก"  class="btn btn-primary">
							    </a>
							 </td>
						</tr>
						<tr>
							<td align="center">
							   <a href="#" onclick="gotoStockMCMSearch();">
								  <input type="button" value="       ดูประวัติ            "  class="btn btn-primary">
							    </a>
							 </td>
						</tr>
					</table>
                    </div>
                </main>
                
                <!-- Hidden Field -->
				 <input type="hidden" name="pageName" value="<%=pageName %>"/>
				 <input type="hidden" name="mobile" value="<%=mobile %>"/>
			     <input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
	
                 <!-- Include Footer Mobile  -->
		       <jsp:include page="../templates/footerMT.jsp"/>
		     <!-- /Include Footer Mobile -->	
		     
      </html:form>
      <!-- BODY -->
</body>
</html>
<%}catch(Exception e){
  e.printStackTrace();
}
	%>

