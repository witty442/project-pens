<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MRole"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.*" %>
<%
String type = (String) request.getParameter("type");
String roleColumnAccess = (String) request.getParameter("roleColumnAccess");
String returnText = "";
try{
	returnText = MRole.getRoleDataAccess(type,roleColumnAccess);
	System.out.println("returnText:"+returnText);
}catch(Exception e){
	e.printStackTrace();
}finally{
	
}
%>
<%=returnText%>