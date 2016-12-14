<%@page import="com.pens.gps.GPSReader"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.inf.helper.*" %>
<%@page import="com.isecinc.pens.inf.bean.*" %>
<%
String status = "";
String s  ="";
GPSReader dao = new GPSReader();
//Error -1 = ErrorNotFoundGPSDevice
//Error -2 = ErrorNotFoundLocation
try{
	status = dao.getLocationDB(); 
	 if(Utils.isNull(status).equals("")){
		s ="0";
	}else{
		  s ="1";
	} 
	 
	 //System.out.println("status len:"+status.split("\\|").length);

	 if(status.split("\\|").length >2){
		 String ss[] = status.split("\\|");
		 //System.out.println("error:"+Utils.isNull(ss[2]));
		 if( !Utils.isNull(ss[2]).equals("") && Utils.isNull(ss[2]).equalsIgnoreCase("ErrorNotFoundGPSDevice") ){
			 s ="-1"; 
		 }else  if(!Utils.isNull(ss[2]).equals("") && Utils.isNull(ss[2]).equalsIgnoreCase("ErrorNotFoundLocation") ){
			 s ="-2";  
		 }
	 }
	 
	System.out.println("Check Status DateTime["+(new Date())+"]Status["+s+"]");
}catch(Exception e){
	e.printStackTrace();
}finally{
	
}
%>
<%=s%>