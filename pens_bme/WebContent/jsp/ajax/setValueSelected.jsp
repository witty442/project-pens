<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String codes = Utils.isNull(request.getParameter("codes"));
String keys = Utils.isNull(request.getParameter("keys"));
String descs = Utils.isNull(request.getParameter("descs"));

codes = new String(codes.getBytes("ISO8859_1"), "UTF-8");
keys = new String(keys.getBytes("ISO8859_1"), "UTF-8");
descs = new String(descs.getBytes("ISO8859_1"), "UTF-8");

session.setAttribute("codes", codes);
session.setAttribute("keys", keys);
session.setAttribute("descs", descs);

System.out.println("set Value Selected Session");

System.out.println("SetAjax codes:"+codes);
System.out.println("SetAjax descs:"+descs);
System.out.println("Result SetAjax descs:"+(String)session.getAttribute("descs"));
%>