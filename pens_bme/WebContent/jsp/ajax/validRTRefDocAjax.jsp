
<%@page import="com.isecinc.pens.dao.RTDAO"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String refDoc = Utils.isNull(request.getParameter("refDoc"));
String outputText = "";
try{
	//System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(refDoc)) ){
		
		 boolean results = RTDAO.validRefDoc(refDoc); 
		 outputText = String.valueOf(results).toLowerCase();
	}else{
		 outputText ="";
	}
	
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>