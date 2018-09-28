
<%@page import="com.isecinc.pens.bean.StoreBean"%>
<%@page import="com.isecinc.pens.dao.StockLimitDAO"%>
<%@page import="com.isecinc.pens.bean.Barcode"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.dao.SummaryDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String orderDate = Utils.isNull((String) request.getParameter("orderDate"));
System.out.println("orderDate:"+orderDate);
String outputText = "";
try{
	String barcodeInPage = Utils.isNull(session.getAttribute("barcodeInPage"));
	List<StoreBean> storeList = (List)session.getAttribute("storeList");
	if( !barcodeInPage.equals("")){
		StockLimitDAO.deleteOrderInPage(orderDate, barcodeInPage, storeList);
	}
		
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>