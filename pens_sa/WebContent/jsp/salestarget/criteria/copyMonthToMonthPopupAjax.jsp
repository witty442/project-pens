
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetCopyNMonth"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
/* param  = "fromPeriod="+$('#fromPeriod').val();
param += "&fromStartDate="+$('#fromStartDate').val();
param += "&toPeriod="+$('#toPeriod').val();
param += "&toStartDate="+$('#toStartDate').val(); */

String fromPeriod = Utils.isNull(request.getParameter("fromPeriod"));
String fromStartDate = Utils.isNull(request.getParameter("fromStartDate"));
String toPeriod = Utils.isNull(request.getParameter("toPeriod"));
String toStartDate = Utils.isNull(request.getParameter("toStartDate"));
String returnStr = "";
try{
	returnStr = SalesTargetCopyNMonth.copy(fromPeriod, fromStartDate, toPeriod, toStartDate);
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=returnStr %>

