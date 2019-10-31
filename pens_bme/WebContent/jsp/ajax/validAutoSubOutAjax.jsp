<%@page import="com.isecinc.pens.dao.AutoSubOutDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String refNo = Utils.isNull(request.getParameter("refNo"));
String outputText = "true";//pass
try{
	System.out.println("refNo:"+refNo);
	if( !"".equals(Utils.isNull(refNo)) ){
		 boolean exits = AutoSubOutDAO.isAutoSubTransOutExist(refNo); 
		 if(exits){
		   outputText = "false";//fail is Exist
		 }
	}else{
		 outputText ="";
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>