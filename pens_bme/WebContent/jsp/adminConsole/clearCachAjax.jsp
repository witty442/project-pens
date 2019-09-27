<%@page import="com.pens.util.EnvProperties"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%

try{
	//AppEnvProperties.getInstance().reload();
	EnvProperties.getInstance().reload();
	
	System.out.println("Reload Cach");
}catch(Exception e){
	e.printStackTrace();
}
%>