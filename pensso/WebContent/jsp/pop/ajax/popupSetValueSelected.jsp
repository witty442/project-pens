<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String codes = Utils.isNull(request.getParameter("codes"));
String keys = Utils.isNull(request.getParameter("keys"));
String descs = Utils.isNull(request.getParameter("descs"));
String descs2 = Utils.isNull(request.getParameter("descs2"));
String descs3 = Utils.isNull(request.getParameter("descs3"));

codes = new String(codes.getBytes("ISO8859_1"), "UTF-8");
keys = new String(keys.getBytes("ISO8859_1"), "UTF-8");
descs = new String(descs.getBytes("ISO8859_1"), "UTF-8");
descs2 = new String(descs2.getBytes("ISO8859_1"), "UTF-8");
descs3 = new String(descs3.getBytes("ISO8859_1"), "UTF-8");

session.setAttribute("codes", codes);
session.setAttribute("keys", keys);
session.setAttribute("descs", descs);
session.setAttribute("descs2", descs2);
session.setAttribute("descs3", descs3);

System.out.println("set Value Selected Session");

System.out.println("SetAjax codes:"+codes);
System.out.println("SetAjax descs2:"+descs2);
System.out.println("Result SetAjax descs:"+(String)session.getAttribute("descs2"));
%>