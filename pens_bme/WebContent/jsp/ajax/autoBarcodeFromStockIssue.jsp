
<%@page import="com.isecinc.pens.bean.Barcode"%>
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.dao.SummaryDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.bean.Master"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String itemCode = Utils.isNull((String) request.getParameter("itemCode"));
String issueReqNo = Utils.isNull((String) request.getParameter("issueReqNo"));
String matCode = Utils.isNull((String) request.getParameter("matCode"));
String warehouse = Utils.isNull((String) request.getParameter("warehouse"));
String boxNo = Utils.isNull((String) request.getParameter("boxNo"));

String outputText = "";
try{
	//System.out.println("itemCode:"+itemCode);
	if( !issueReqNo.equals("") && !"".equals(Utils.isNull(itemCode)) || !"".equals(Utils.isNull(matCode))){
		
		PopupForm popupForm = new PopupForm();
		popupForm.setCodeSearch(itemCode);
		popupForm.setMatCodeSearch(matCode);
		
		Barcode b = GeneralDAO.searchProductByBarcodeFromStockIssue(popupForm,issueReqNo,warehouse,boxNo);  
		
		if(b != null ){
		    outputText = b.getBarcode()+"|"+b.getMaterialMaster()+"|"+b.getGroupCode()+"|"+b.getPensItem()+"|"+b.getQty();
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