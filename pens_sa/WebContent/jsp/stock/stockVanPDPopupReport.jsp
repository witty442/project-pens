<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<jsp:useBean id="stockForm" class="com.isecinc.pens.web.stock.StockForm" scope="session" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockForm");
String pageName = Utils.isNull(request.getParameter("pageName"));
if(Utils.isNull(pageName).equals("")){
	pageName = stockForm.getPageName();
}
System.out.println("pageName:"+pageName);
%>

  <%if(StockConstants.PAGE_STOCK_CLOSE_VAN.equalsIgnoreCase(pageName)){ %>
      <jsp:include page="page/stockCloseVanPopupPage.jsp" flush="true" /> 
  <%}else if(StockConstants.PAGE_STOCK_CLOSEPD_VAN.equalsIgnoreCase(pageName)){ %>
      <jsp:include page="page/stockPDVanPopupPage.jsp" flush="true" />   
  <% } %>
			