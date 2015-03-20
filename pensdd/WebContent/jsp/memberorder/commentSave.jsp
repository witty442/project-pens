
<%@page import="util.DBCPConnectionProvider"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.model.MMemberTripComment"%>
<%@page import="com.isecinc.pens.bean.MemberTripComment"%>
<%@page import="com.isecinc.pens.bean.User"%><%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
User user = (User) session.getAttribute("user");
String tripId=request.getParameter("tripId");
String tripNo=request.getParameter("tripNo");
String orderId=request.getParameter("orderId");
String comment=request.getParameter("comment");
comment = new String(comment.getBytes("ISO8859_1"), "UTF-8");

boolean saveResult=false;

MemberTripComment tripComment=null;
if(tripId.equalsIgnoreCase("0")){
	//new Trip Comment
	tripComment=new MemberTripComment();
	tripComment.setOrderId(Integer.parseInt(orderId));
	tripComment.setTripNo(Integer.parseInt(tripNo));
	tripComment.setTripComment(comment);
}else{
	tripComment = new MMemberTripComment().find(tripId);
	tripComment.setTripComment(comment);
}
Connection conn = null;
try{
	conn = new DBCPConnectionProvider().getConnection(conn);
	conn.setAutoCommit(false);
	new MMemberTripComment().save(tripComment,user.getId(),conn);
	conn.commit();
	saveResult=true;
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{
		conn.setAutoCommit(true);
	}catch(Exception e){}
	try{
		conn.close();
	}catch(Exception e){}
}
%>
<%if(saveResult){ %>
<%=saveResult%>
<%}%>