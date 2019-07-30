
<%@page import="com.isecinc.pens.thread.ControlSubThread"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String threadName = (String) request.getParameter("threadName");
try{
	System.out.println("Clear Status DateTime["+(new Date())+"]threadName["+threadName+"]Status[]");
	ControlSubThread.processThread(threadName, "");
}catch(Exception e){
	e.printStackTrace();
}
%>