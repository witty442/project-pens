<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.model.MOrder"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
int orderId = Utils.convertStrToInt(request.getParameter("orderId"));
String dateStr = request.getParameter("dateStr");
int count = Utils.convertStrToInt(request.getParameter("count"));

	new MOrder().updatePrintRcpStamp(orderId, dateStr, count);
%>
