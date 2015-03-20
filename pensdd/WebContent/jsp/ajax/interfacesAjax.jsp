<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.inf.dao.InterfaceDAO" %>

<%@page import="com.isecinc.pens.inf.helper.*" %>
<%@page import="com.isecinc.pens.inf.bean.*" %>
<%
String id = (String) request.getParameter("id");
String transaction_count = (String) request.getParameter("transaction_count");
String whereCause = "";
String status = "";
InterfaceDAO dao = new InterfaceDAO();
java.sql.Connection conn= null;
try{
	conn = DBConnection.getInstance().getConnection();
	status = dao.findMonitorStatus(conn,id,transaction_count);
	System.out.println("id :"+id+",status:"+status);
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();conn=null;
	}
}
%>
<%=status%>