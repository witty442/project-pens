<%@page import="com.isecinc.pens.web.autoorder.AutoOrderDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String refCode = Utils.isNull(request.getParameter("refCode"));
String custGroup = Utils.isNull(request.getParameter("custGroup"));
String outputText = "";//pass
try{
	System.out.println("custGroup:"+custGroup+",refCode:"+refCode);
	if( !"".equals(Utils.isNull(custGroup)) && !refCode.equals("") ){
		outputText = AutoOrderDAO.getMasterConfig(refCode, custGroup); 
	}//if
}catch(Exception e){
	e.printStackTrace();
	outputText="";
}
%>
<%=outputText %>