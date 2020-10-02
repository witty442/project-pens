<%@page import="com.isecinc.pens.web.shop.ShopAction"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "shopForm");
String pageName = Utils.isNull(request.getParameter("pageName"));
System.out.println("PageName:"+pageName);
%>
<%if(ShopAction.P_MAYA_SALEOUT.equalsIgnoreCase(pageName)) {%>
    <jsp:include page="page/saleOutMaya.jsp" flush="true"/> 
<%}else if(ShopAction.P_MAYA_STOCK_ONHAND.equalsIgnoreCase(pageName)) {%>
    <jsp:include page="page/stockOnhandMaya.jsp"  flush="true"/> 
<%}else if(ShopAction.P_SHOP_BILL_DETAIL.equalsIgnoreCase(pageName)) {%>
    <jsp:include page="page/shopBill.jsp" flush="true"/> 
<%}else if(ShopAction.P_TM_SALEOUT.equalsIgnoreCase(pageName)) {%>
     <jsp:include page="page/saleOutTM.jsp" flush="true"/> 0
<%}else if(ShopAction.P_TM_STOCK_ONHAND.equalsIgnoreCase(pageName)) {%>
     <jsp:include page="page/stockOnhandTM.jsp"  flush="true"/> 
<%}if(ShopAction.P_CH_SALEOUT.equalsIgnoreCase(pageName)) {%>
     <jsp:include page="page/saleOutCH.jsp" flush="true"/> 
<%}else if(ShopAction.P_CH_STOCK_ONHAND.equalsIgnoreCase(pageName)) {%>
     <jsp:include page="page/stockOnhandCH.jsp" flush="true"/> 
<%}if(ShopAction.P_SP_SALEOUT.equalsIgnoreCase(pageName)) {%>
     <jsp:include page="page/saleOutSP.jsp" flush="true"/> 
<%}else if(ShopAction.P_SP_STOCK_ONHAND.equalsIgnoreCase(pageName)) {%>
     <jsp:include page="page/stockOnhandSP.jsp" flush="true"/> 
<%} %>
						