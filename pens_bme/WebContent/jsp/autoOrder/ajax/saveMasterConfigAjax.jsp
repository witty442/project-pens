<%@page import="com.isecinc.pens.web.autoorder.AutoOrderDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String refCode = Utils.isNull(request.getParameter("refCode"));
String custGroup = Utils.isNull(request.getParameter("custGroup"));
String inputQty = Utils.isNull(request.getParameter("inputQty"));
String outputText = "";//pass
try{
	System.out.println("refCode:"+refCode+",custGroup:"+custGroup+",inputQty:"+inputQty);
	if( !"".equals(Utils.isNull(custGroup)) && !inputQty.equals("") ){
		 AutoOrderDAO.updateMasterConfig(refCode,custGroup,inputQty);
	}//if
}catch(Exception e){
	e.printStackTrace();
	outputText="-1";
}
%>
<%=outputText %>