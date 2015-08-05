<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%
 String wareHouse = request.getParameter("wareHouse");

if("W1".equals(wareHouse)){
%>
	<option value="SCANNING">SCANNING</option>
	<option value="ONHAND">ONHAND</option>
	<option value="RETURN">RETURN</option>
	<option value="ALL">ALL</option>
<%}else if("W2".equals(wareHouse)){ %>
    <option value="SCANNING">SCANNING</option>
	<option value="ONHAND">ONHAND</option>
	<option value="FINISHING">FINISHING</option>
	<option value="FINISHGOODS">FINISH GOODS</option>
	<option value="ALL">ALL</option>
<%}else if("W3".equals(wareHouse)){ %>
    <option value="SCANNING">SCANNING</option>
	<option value="ONHAND">ONHAND</option>
	<option value="ISSUED">ISSUED</option>
	<option value="ALL">ALL</option>
<%}else if("W4".equals(wareHouse)){ %>
    <option value="SCANNING">SCANNING</option>
	<option value="ONHAND">ONHAND</option>
	<option value="ISSUED">ISSUED</option>
	<option value="ALL">ALL</option>
<%}%>