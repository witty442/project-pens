
<%@page import="com.pens.util.SessionUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
try{
    out.println(SessionUtils.listSessionAttribute(request));
}catch(Exception e){
	e.printStackTrace();
}
%>
