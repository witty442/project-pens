<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%
 String wareHouse = request.getParameter("wareHouse");
 String status = request.getParameter("status");

if("W1".equals(wareHouse) || "W3".equals(wareHouse) || "W4".equals(wareHouse)  || ( "W2".equals(wareHouse) && ( "SCANNING".equals(status) || "ONHAND".equals(status)) || "FINISHING".equals(status) || "FINISHGOODS".equals(status) ) ){
%>
	<option value="Detail">Detail</option>
	<option value="SummaryByBox">Summary ตามกล่อง</option>
	<option value="SummaryByPensItem">Summary ตาม PensItem</option>
<%}else{ %>
   <option value="Detail">Detail</option>
   <option value="SummaryByPensItem">Summary ตาม PensItem</option>
<%}%>