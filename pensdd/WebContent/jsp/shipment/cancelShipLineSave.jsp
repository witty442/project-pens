<%@page import="util.DBCPConnectionProvider"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.model.MShipmentConfirm"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
User user = (User) session.getAttribute("user");

String lineId = request.getParameter("lineId");
String isCancel = request.getParameter("isCancel");
String shipDate = request.getParameter("confirmDate");

String saveResult = "";

List lineL = new ArrayList<Integer>();
if(!StringUtils.isEmpty(lineId)){
	lineL.add(Integer.valueOf(lineId));
	MShipmentConfirm shipConfirm = MShipmentConfirm.getInstance();

	if("Y".equals(isCancel)){
		saveResult = shipConfirm.cancelLines(lineL,user);
	}
	else{
		saveResult = shipConfirm.saveConfirmDate(Integer.valueOf(lineId),shipDate);
	}
}
else{
	saveResult = "No Line ID";
}
%>
<%=saveResult%>