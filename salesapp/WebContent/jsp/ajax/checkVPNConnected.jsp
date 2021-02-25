<%@page import="com.pens.util.EnvProperties"%>
<%@page import="com.isecinc.pens.process.testconn.TestURLConnection"%>
<%@page import="com.isecinc.pens.inf.manager.batchwork.URLTestConnectionWorker"%>
<%@page import="com.isecinc.pens.inf.exception.FTPException"%>
<%@page import="com.isecinc.pens.inf.manager.FTPManager"%>
<%@page import="java.net.InetAddress"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String output = "false";
String url = Utils.isNull(request.getParameter("url"));
EnvProperties env = EnvProperties.getInstance();
try{
	//Case no url test connection
	if(Utils.isNull(url).equals("")){
		String currentIP =InetAddress.getLocalHost().getHostAddress();
		//case Server Test contextPath = pens_sa_test
		 if("192.168.202.8xx".equals(currentIP)){ //On Witty dev
			url = "http://localhost:8080/pens_sa"; //on localhost
		 }else{
			 if("production".equalsIgnoreCase(env.getProperty("config.type"))){
			    //production
		        url = "http://"+EnvProperties.getInstance().getProperty("ftp.ip.server")+":8081/pens_sa";
			 }else{
				//production context pens_sa_test
				url = "http://192.168.37.185:8081/pens_sa_test";
			 }
		 }//if 
	}//if
	
	//Mark status to running
	TestURLConnection.processThread(TestURLConnection.THREAD_PENSA, "running");
	
	//Run Thread to text  url connection
	new URLTestConnectionWorker(TestURLConnection.THREAD_PENSA,url).run();
	/************************************************************/

}catch(Exception e){
	e.printStackTrace();
}finally{}
%>
<%=output%>