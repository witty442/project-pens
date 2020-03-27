<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "importAllForm");
String pageName = Utils.isNull(request.getParameter("pageName"));
%>
<%if("importMasterOrderREP".equalsIgnoreCase(pageName)){%>
  <jsp:include page="page/importMasterOrderREP.jsp" flush="true"/> 
<%}else if("ImportExcelPICG899ToG07".equalsIgnoreCase(pageName)){%>
  <jsp:include page="page/importExcelPICG899ToG07.jsp" flush="true"/> 
<%}else if("ImportFileSwitchItemAdjustStock".equalsIgnoreCase(pageName)){%>
  <jsp:include page="page/importFileSwitchItemAdjustStock.jsp" flush="true"/> 
<%} %>
						