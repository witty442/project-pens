<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.isecinc.pens.web.stockvan.StockVanBean"%>
<%@page import="java.util.List"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<jsp:useBean id="stockVanForm" class="com.isecinc.pens.web.stockvan.StockVanForm" scope="session" />
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
  if(request.getSession().getAttribute("RESULT_DATA") != null){
	  out.print(((StringBuffer)request.getSession().getAttribute("RESULT_DATA")).toString());
  }
%>
	