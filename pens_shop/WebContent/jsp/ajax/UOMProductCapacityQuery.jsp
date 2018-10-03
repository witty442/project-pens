
<%@page import="com.isecinc.pens.model.MUOMConversion"%>
<%@page import="com.isecinc.pens.bean.UOMConversion"%>
<%@page import="com.isecinc.pens.bean.Product"%><%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%@page import="util.DBCPConnectionProvider"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.DecimalFormat"%>
<%
String pID =  (String)request.getParameter("pId");
String uom1 = (String)request.getParameter("uom1");
String uom2 = (String)request.getParameter("uom2");

String capacityStr = "";
try{
	if(!"".equals(pID) && !"".equals(uom1)){
		int productId = Integer.parseInt(pID);
		double capacity = new MUOMConversion().getCapacity(productId, uom1, uom2);
		capacityStr = capacity+"";
	}
}catch(Exception e){
	e.printStackTrace();
}
%>

<%=capacityStr%>