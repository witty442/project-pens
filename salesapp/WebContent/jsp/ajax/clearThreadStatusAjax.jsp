<%@page import="com.isecinc.pens.process.testconn.TestURLConnection"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.inf.dao.InterfaceDAO" %>

<%@page import="com.isecinc.pens.inf.helper.*" %>
<%
String threadName = (String) request.getParameter("threadName");
try{
	System.out.println("Clear Status DateTime["+(new Date())+"]threadName["+threadName+"]Status[]");
	TestURLConnection.processThread(TestURLConnection.THREAD_PENSA, "");
}catch(Exception e){
	e.printStackTrace();
}
%>