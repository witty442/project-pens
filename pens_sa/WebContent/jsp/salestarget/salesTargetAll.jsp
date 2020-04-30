<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "salesTargetForm");
String pageName = Utils.isNull(request.getParameter("pageName"));
if(Utils.isNull(pageName).equals("")){
	pageName = salesTargetForm.getPageName();
}
%>
<%if(SalesTargetConstants.PAGE_SALES_TARGET_PD.equalsIgnoreCase(pageName)){%>
     <jsp:include page="page/salesTargetPDPage.jsp" flush="true"/> 
<%}else if("".equalsIgnoreCase(pageName)){ %>
    
<% } %>
