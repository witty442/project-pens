<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "reportAllForm");
String pageName = Utils.isNull(request.getParameter("pageName"));
%>
<%if("ProjectCReport".equalsIgnoreCase(pageName)){%>
  <jsp:include page="page/projectCReport.jsp" flush="true"/> 
<%} %>
						