<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.inf.dao.InterfaceDAO" %>

<%@page import="com.isecinc.pens.inf.helper.*" %>
<%@page import="com.isecinc.pens.inf.bean.*" %>
<%
String id = (String) request.getParameter("id");

String whereCause = "";
String status = "";
InterfaceDAO dao = new InterfaceDAO();
java.sql.Connection conn= null;
try{
	conn = DBConnection.getInstance().getConnection();
	status = dao.findMonitorStatus(conn,id);
	
	String s = "";
	if(Utils.isNull(status).equals("") || Utils.isNull(status).equals("0")){
		s = "Running";
	}else{
		s ="Success";
	}
	System.out.println("***Check Status Date["+(new Date())+"]TransId["+id+"]Status["+status+"]Msg["+s+"]***");
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();conn=null;
	}
}
%>
<%=status%>