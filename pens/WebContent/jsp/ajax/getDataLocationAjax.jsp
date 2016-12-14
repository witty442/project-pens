<%@page import="com.pens.gps.GPSReader"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.inf.helper.*" %>
<%@page import="com.isecinc.pens.inf.bean.*" %>
<%
String status = "";
GPSReader dao = new GPSReader();
try{
	status = dao.getLocationDB(); 
	System.out.println("return str["+status+"]");
	
	//Test
	//status ="13.672494|100.542054";
}catch(Exception e){
	e.printStackTrace();
}finally{
	
}
%>
<%=status%>