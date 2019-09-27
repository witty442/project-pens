<%@page import="com.isecinc.pens.web.stockmc.StockMCDAO"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
User user = (User) request.getSession().getAttribute("user");
String productCode = Utils.isNull(request.getParameter("productCode"));
String barcode = Utils.isNull(request.getParameter("barcode"));
String customerCode = Utils.isNull(request.getParameter("customerCode"));
StockMCBean p = null;
boolean dup = false;
String outputText = "";
try{
	if( !Utils.isNull(productCode).equals("") || !Utils.isNull(barcode).equals("")){
		
		p = StockMCDAO.getProductMCItem(customerCode, productCode,barcode); 
		if(p!= null){
			outputText  = p.getProductCode()+"|";
			outputText += p.getProductName()+"|"+p.getProductPackSize()+"|";
			outputText += /*p.getProductExpireDate()*/""+"|"+p.getRetailPriceBF()+"|";
			outputText +=p.getBarcode()+"|"+p.getExpireDate1();
		}
		System.out.println("outputText:"+outputText);
	}
}catch(Exception e){
	e.printStackTrace();
}
if(p != null)
%>
<%=outputText%>