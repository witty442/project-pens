
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>

<%
String delSQL = (String)request.getParameter("delSQL");
       delSQL = new String(delSQL.getBytes("ISO8859_1"), "UTF-8");
String returnStr = "0";//noting
try{
	System.out.println("delSQL:"+delSQL);
	
	if( !"".equals(Utils.isNull(delSQL)) ){
		returnStr  = SQLHelper.excUpdateOneSql(delSQL)+"";
	}
}catch(Exception e){
	e.printStackTrace();
	returnStr = "-1";//delete fail
}
%>
<%=returnStr %>
