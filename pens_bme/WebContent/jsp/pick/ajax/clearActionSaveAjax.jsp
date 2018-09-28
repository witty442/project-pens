<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.pens.util.*"%>
<%
  session.removeAttribute("ACTION_SAVE");
%>
<%=Utils.isNull(session.getAttribute("ACTION_SAVE"))%>