
<%@page import="java.text.DecimalFormat"%><%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.model.MProductPrice"%>
<%@page import="com.isecinc.pens.bean.ProductPrice"%>
<%@page import="util.DBCPConnectionProvider"%>
<%
String pID = (String)request.getParameter("pId");
String uomID = (String)request.getParameter("uomId");
String pricelistID = (String)request.getParameter("plId");

List<ProductPrice> results = null;
double price = 0;

try{
	
	if(pID != null && pID.length() > 0){
		results = new MProductPrice().getCurrentPrice(pID,pricelistID,uomID);
		for(ProductPrice p : results){
			price = p.getPrice();
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=new DecimalFormat("###0.00").format(price)%>