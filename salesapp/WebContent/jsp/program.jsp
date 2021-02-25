<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String system="";
String function="";
String code="";
system=(request.getParameter("system")==null?"":request.getParameter("system"));
function=(request.getParameter("function")==null?"":request.getParameter("function"));
code=(request.getParameter("code")==null?"":request.getParameter("code"));

//System.out.println(system);
//System.out.println(function);
//System.out.println(code);


if(system.length()>0) system = SystemProperties.getCaption(system,Locale.getDefault());
if(function.length()>0) function = SystemProperties.getCaption(function,Locale.getDefault());
if(code.equals("null")) code = SystemProperties.getCaption(SystemProperties.CREATE_NEW_RECORD,Locale.getDefault());
%>

<%-- <h1 class="mt-4"><%=function.length()>0? function:"" %><%=code.length()>0? " > " + code : "" %></h1> --%>
   <ol class="breadcrumb mb-4">
      <!-- <li class="breadcrumb-item"><a href="index.html">Dashboard</a></li> -->
      <li class="breadcrumb-item active"><%=function.length()>0? function:"" %><%=code.length()>0? " > " + code : "" %></li>
  </ol> 

