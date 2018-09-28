
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.dao.RTDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String asOfDate = Utils.isNull(request.getParameter("asOfDate"));
String storeCode = Utils.isNull(request.getParameter("storeCode"));
String outputText = "";
try{
	//System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(storeCode)) ){
		 outputText = GeneralDAO.validAsOfDate_EndDateStockLotus(asOfDate,storeCode); 
	}else{
		 outputText ="";
	}
	
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>