<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<jsp:useBean id="generalForm" class="com.isecinc.pens.web.general.GeneralForm" scope="request" />
<%
String pageName = Utils.isNull(request.getParameter("pageName"));
%>
<%if("GenBarcode".equalsIgnoreCase(pageName)){%>
  <jsp:include page="page/genBarcode.jsp" flush="true"/> 
<%}%>