<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.Product"%>
<%@page import="com.isecinc.pens.model.MProduct"%>
<%
System.out.println("*** Get Product Stock Return ***");
User user = (User) request.getSession().getAttribute("user");
String productCode = Utils.isNull(request.getParameter("productCode"));
Product p = null;
boolean dup = false;
String outputText = "";
try{
	if(productCode != null && productCode.length()>0){
	   p = new MProduct().getStockReturnProduct(productCode,user); 
	   if(p!= null){
	      outputText = p.getId()+"|"+p.getName()+"|"+p.getUom2()+"|"+p.getUom1Pac()+"|"+p.getUom2Pac()+"|"+p.getUom1Price();
	      outputText +="|"+p.getUom1ConvRate()+"|"+p.getUom2ConvRate();
	   }
	  System.out.println("stockReturn :output="+outputText);
	}
}catch(Exception e){
	e.printStackTrace();
}
if(p != null)
%>
<%=outputText%>