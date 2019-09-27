<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.isecinc.pens.web.stock.StockConstants"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.report.salesanalyst.SAConstants"%>

<%
  User user = (User)session.getAttribute("user");
 // System.out.println("Role:"+user.getRole().getKey());
  
%>
<%if ( Utils.isNull(user.getRoleVanSales()).equalsIgnoreCase(User.VANSALES)){ %>
   <jsp:include page="menu_vansales.jsp" flush="true"/>
<%}else{%>
   <jsp:include page="menu_pens.jsp" flush="true"/>
<%}%>
   