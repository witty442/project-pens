
<%@page import="com.isecinc.pens.thread.ControlSubThreadWorker"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.thread.ControlSubThread"%>
<%@page import="java.net.InetAddress"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String output = "false";
String threadName = Utils.isNull(request.getParameter("threadName"));
String userName = Utils.isNull(request.getParameter("userName"));
try{
	//Mark status to running
	ControlSubThread.processThread(threadName, "running");
	
	//Run Thread to text  url connection
	new ControlSubThreadWorker(threadName,"").run();
	/************************************************************/

}catch(Exception e){
	e.printStackTrace();
}finally{}
%>
<%=output%>