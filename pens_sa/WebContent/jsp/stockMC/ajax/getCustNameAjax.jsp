
<%@page import="com.isecinc.pens.web.popup.PopupDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String customerCode = Utils.isNull(request.getParameter("customerCode"));
String outputText = "";
try{
	System.out.println("customerCode:"+customerCode);
	if( !"".equals(Utils.isNull(customerCode)) ){
	
		//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		PopupForm c = new PopupForm();
		c.setCodeSearch(customerCode);
		
	    List<PopupForm> dataList = PopupDAO.searchCustomerStockMC(c);
	    if(dataList != null && dataList.size()>0){
		   outputText = Utils.isNull(dataList.get(0).getDesc());
		   System.out.println("outputText:"+outputText);
	    }
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>