<%@page import="com.pens.gps.GPSReader"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%

GPSReader dao = new GPSReader();
try{
	  /** Clear Temp Data **/
	   new GPSReader().boforeStartClearData();
	  /** Start Thread **/
	   GPSReader.startProcess();
}catch(Exception e){
	e.printStackTrace();
}finally{
	
}
%>
