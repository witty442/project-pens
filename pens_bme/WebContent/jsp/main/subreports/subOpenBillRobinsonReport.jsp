<%@page import="com.isecinc.pens.web.summary.SummaryForm"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
//System.out.println("subOpenBillRobinsonReport :"+request.getAttribute("RESULTS"));
SummaryForm summaryForm =(SummaryForm)session.getAttribute("summaryForm");
 if(summaryForm.getDataHTML() != null){
	 out.println(summaryForm.getDataHTML().toString());
 }
%>
