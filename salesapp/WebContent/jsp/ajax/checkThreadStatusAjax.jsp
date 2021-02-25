<%@page import="com.pens.util.DBConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.process.testconn.TestURLConnection"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.inf.dao.InterfaceDAO" %>

<%@page import="com.isecinc.pens.inf.helper.*" %>
<%
String threadName = (String) request.getParameter("threadName");
String status = "";
Connection conn = null;
try{ 
	conn = DBConnection.getInstance().getConnection();
	status = TestURLConnection.getStatusOfThread(conn,threadName);
	System.out.println("Chk Status Date["+(new Date())+"]thread["+threadName+"]Status["+status+"]");

}catch(Exception e){
	e.printStackTrace();
}finally{
	try {
		conn.close();
	} catch (Exception e2) {}
}
%>
<%=status%>