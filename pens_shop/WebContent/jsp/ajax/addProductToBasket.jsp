<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.web.sales.bean.ProductCatalog"%>
<%@page import="com.isecinc.pens.web.sales.bean.Basket"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%
String custId = request.getParameter("custId");
String productId = request.getParameter("pId");
String productCode = request.getParameter("pCode");
String productName = request.getParameter("pName");
System.out.println(productName);

String uom1 = request.getParameter("uom1");
String uom2 = request.getParameter("uom2");
String price1 = request.getParameter("price1");
String price2 = request.getParameter("price2");
String qty1 = request.getParameter("qty1");
String qty2 = request.getParameter("qty2");
String linenetAmt = request.getParameter("lineAmt");
String taxable = request.getParameter("taxable");

Basket basket = (Basket)session.getAttribute(custId);
if(basket == null ){
	basket = new Basket();	
}

ProductCatalog item = new ProductCatalog();
item.setProductId(Integer.valueOf(productId));
item.setProductCode(productCode);
item.setProductName(productName);
item.setUom1(uom1);
item.setUom2(uom2);
item.setPrice1(Double.valueOf(price1));
item.setPrice2(Double.valueOf(price2));
item.setTaxable(taxable);
if(StringUtils.isEmpty(qty1.trim()))
	qty1 = "0";
item.setQty1(Integer.parseInt(qty1));

if(StringUtils.isEmpty(qty2.trim()))
	qty2 = "0";
item.setQty2(Integer.parseInt(qty2));

if(StringUtils.isEmpty(linenetAmt.trim()))
	linenetAmt = "0";
item.setLineNetAmt(Double.valueOf(linenetAmt));

basket.adjustItem(item);

session.setAttribute(custId,basket);
%>