<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
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

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<div class="programlabel">
<%=function.length()>0? function:"" %><%=code.length()>0? " > " + code : "" %>
<%//=system %><%//=function.length()>0? " > " + function : "" %><%//=code.length()>0? " > " + code : "" %>
</div>
