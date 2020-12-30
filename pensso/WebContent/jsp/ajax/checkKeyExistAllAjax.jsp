<%@page import="com.isecinc.pens.web.checkkeyexist.CheckKeyExistAllAction"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String outputText = "";
/** param :keyCheck           **/
/** return true:key exist     **/
/** return false:key no exist **/	
try{
	outputText = new CheckKeyExistAllAction().checkKeyExist(request); 
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>