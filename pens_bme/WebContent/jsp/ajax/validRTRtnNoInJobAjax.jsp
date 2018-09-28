<%@page import="com.isecinc.pens.dao.JobDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String rtnNo = Utils.isNull(request.getParameter("rtnNo"));
String outputText = "";
try{
	//System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(rtnNo)) ){
		
		 boolean results = JobDAO.validRtnNoInJob(rtnNo); 
		 outputText = String.valueOf(results).toLowerCase();
	}else{
		 outputText ="";
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>