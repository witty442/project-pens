<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MRole"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.*" %>
<%
String type = (String) request.getParameter("type");
String roleName = (String) request.getParameter("roleName");
boolean returnText = false;
try{
	returnText = MRole.isRoleNameExist(roleName);
	System.out.println("returnText:"+returnText);
}catch(Exception e){
	e.printStackTrace();
}finally{
	
}
%>
<%=returnText%>