<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.ProductBarcodeBean"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.dao.ProductBarcodeDAO"%>
<%@page import="java.util.List"%>
<%
ProductBarcodeBean productBarcode = null;
String result = "";
String json ="";
try{
	String barcode = Utils.isNull((String) request.getParameter("barcode"));
	String inputQty = Utils.isNull((String) request.getParameter("inputQty"));
	String custId = request.getParameter("custId");
	String priceListId = Utils.isNull((String) request.getParameter("pricelistId"));
	User user = ((User)session.getAttribute("user"));
	
	System.out.println("custId:"+custId+",barcode:"+barcode+",inputQty:"+inputQty);
	//Get productCode By barcode
	productBarcode = ProductBarcodeDAO.getProductCodeByBarcode(barcode,priceListId,user,inputQty);
	if(productBarcode != null){
		json = productBarcode.getJson();
	}
}catch(Exception e){
	e.printStackTrace();
}
%> 
<%//=productBarcode.getProductCatalog().getProductName()%>
<%=json %>

