<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%
	String districtId = (String) request.getParameter("districtId");
	String dg = "";

	System.out.println(districtId);
	
	dg = new MDistrict().getDeliveryGroup(Integer.parseInt(districtId));
	
%>
<%=dg%>