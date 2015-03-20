
<%@page import="util.BahtText"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String totalQty = (String)request.getParameter("totalQty");
%>
<%out.print(new BahtText(totalQty).toString());%>