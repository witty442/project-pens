<%@page import="com.isecinc.pens.web.autokeypress.AutoKeypressAction"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String outputText = "";
try{
	System.out.println("PageName:"+request.getParameter("pageName"));
	outputText = new AutoKeypressAction().search(request);
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>