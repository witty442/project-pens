<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String codes = Utils.isNull(request.getParameter("codes"));

codes = new String(codes.getBytes("ISO8859_1"), "UTF-8");

session.setAttribute("pick_report_codes", codes);
System.out.println("set Value Selected Session");
System.out.println("SetAjax codes:"+codes);
%>