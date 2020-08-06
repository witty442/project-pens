<%@page import="util.SessionUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="budsAllForm" class="com.isecinc.pens.web.buds.BudsAllForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "budsAllForm");

String pageName = Utils.isNull(request.getParameter("pageName"));

if(Utils.isNull(pageName).equals("")){
   pageName = budsAllForm.getPageName();
}

%>
<%if("ConfPickingSearch".equalsIgnoreCase(pageName)){%>
     <jsp:include page="page/confPickingSearchPage.jsp" flush="true"/>  
<%}else if("ConfPicking".equalsIgnoreCase(pageName)){%>
     <jsp:include page="page/confPickingPage.jsp" flush="true"/>  
<%}else if("ControlPickingSearch".equalsIgnoreCase(pageName)){%>
     <jsp:include page="page/controlPickingSearchPage.jsp" flush="true"/>  
<%}else if("OrderEDI".equalsIgnoreCase(pageName)){%>
     <jsp:include page="page/orderEDIPage.jsp" flush="true"/>   
<% } %>

						