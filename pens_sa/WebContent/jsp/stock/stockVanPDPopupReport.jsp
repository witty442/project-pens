<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="util.Utils"%>
<%@page import="util.SIdUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockForm" class="com.isecinc.pens.web.stock.StockForm" scope="session" />
<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getRoleSalesTarget();
String pageName = Utils.isNull(request.getParameter("pageName"));
if(pageName.equals("")){
	pageName = stockForm.getPageName();
}
String pageNameTemp = pageName;
if(StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){ 
	pageNameTemp = "ReportStockCredit";
}
System.out.println("pageNameTemp:"+pageNameTemp);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">

		<!-- Hidden Field -->
	    <html:hidden property="pageName" value="<%=pageName %>"/>

			<!-- BODY -->
			<html:form action="/jsp/stockAction">
			<jsp:include page="../error.jsp"/>

			<div align="center">
			    <font size='2'><b><bean:message key="<%=pageNameTemp%>" bundle="sysprop"/></b></font>
			    <br/><br/>
			    <% if(StockConstants.PAGE_STOCK_CLOSE_VAN.equalsIgnoreCase(pageName)){ %>
			        <jsp:include page="criteria/stockCloseVanCriteria.jsp" flush="true"/> 
			    <% }else if(StockConstants.PAGE_STOCK_CLOSEPD_VAN.equalsIgnoreCase(pageName)){ %>
			        <jsp:include page="criteria/stockPDVanCriteria.jsp" flush="true"/> 
			    <% } %>
		    </div>
		</html:form>
</html>

 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
<!-- Control Save Lock Screen -->
