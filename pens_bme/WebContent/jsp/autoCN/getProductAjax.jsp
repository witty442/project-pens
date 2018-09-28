
<%@page import="com.isecinc.pens.web.autocn.AutoCNBean"%>
<%@page import="com.isecinc.pens.dao.AutoCNDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
String pensItem = Utils.isNull((String) request.getParameter("pensItem"));
String storeCode = Utils.isNull((String) request.getParameter("storeCode"));
String matCode = Utils.isNull((String) request.getParameter("matCode")); 

String outputText = "";
try{
	//System.out.println("itemCode:"+itemCode);
	if( !"".equals(Utils.isNull(pensItem)) ){
		
		PopupForm popupForm = new PopupForm();
		popupForm.setCodeSearch(pensItem);
		popupForm.setMatCodeSearch(matCode);
		
		AutoCNBean b = AutoCNDAO.searchProductByPensItem(popupForm,storeCode); 
		
		if(b != null ){
		    outputText = b.getItemName()+"|"+b.getUnitPrice()+"|"+b.getInventoryItemId();
		}else{
		    outputText ="";
		}
		
		//System.out.println("returnText["+outputText+"]");
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>