<%@page import="com.isecinc.pens.web.salestarget.SalesTargetTTDAO"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
User user = (User)session.getAttribute("user");
String salesZone = Utils.isNull(request.getParameter("salesZone"));
String brand = Utils.isNull(request.getParameter("brand"));
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
String period = Utils.isNull(request.getParameter("period"));
String startDate = Utils.isNull(request.getParameter("startDate"));
String outputText = "";
try{
	System.out.println("brand:"+brand+",startDate="+startDate);
	if( !Utils.isNull(brand).equals("")){
		boolean r = SalesTargetTTDAO.salesManagerAccept(user, salesZone, brand, custCatNo, period, startDate);
		if(r){
		  outputText = SalesTargetConstants.STATUS_FINISH;
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>