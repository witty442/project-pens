<%@page import="com.pens.util.DBConnection"%>
<%@page import="com.isecinc.pens.thread.ControlSubThread"%>
<%@page import="org.apache.commons.httpclient.protocol.ControllerThreadSocketFactory"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String threadName = (String) request.getParameter("threadName");
String status = "";
Connection conn = null;
try{
	conn = DBConnection.getInstance().getConnection();
	status = ControlSubThread.getStatusOfThread(conn,threadName);
	System.out.println("Check Status DateTime["+(new Date())+"]threadName["+threadName+"]Status["+status+"]");

}catch(Exception e){
	e.printStackTrace();
}finally{
	try {
		conn.close();
	} catch (Exception e2) {}
}
%>
<%=status%>