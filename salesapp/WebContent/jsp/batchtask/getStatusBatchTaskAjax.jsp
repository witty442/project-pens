<%@page import="com.isecinc.pens.web.batchtask.BatchTaskDAO"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.*"%>
<%
String id = (String) request.getParameter("id");
 
String whereCause = "";
String status = "";
java.sql.Connection conn= null;
try{
	conn = DBConnection.getInstance().getConnection();
	status = new BatchTaskDAO().findMonitorStatus(conn,id);
	
	String s = "";
	if(Utils.isNull(status).equals("") || Utils.isNull(status).equals("0")){
		s = "Running";
	}else{
		s ="Success";
	}
	//System.out.println("Check Status DateTime["+(new Date())+"]TransId["+id+"]Status["+s+"]statisId["+status+"]");
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(conn != null){
		conn.close();conn=null;
	}
}
%>
<%=status%>