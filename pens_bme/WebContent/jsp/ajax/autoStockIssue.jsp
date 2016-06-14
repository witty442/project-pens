
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
String code = Utils.isNull(request.getParameter("code"));
String warehouse = Utils.isNull(request.getParameter("warehouse"));
String status = Utils.isNull(request.getParameter("status"));
String boxNo = Utils.isNull(request.getParameter("boxNo"));
String mode = Utils.isNull(request.getParameter("mode"));
String outputText = "";
try{
	System.out.println("issueReqNo:"+code);
	System.out.println("warehouse:"+warehouse);
	
	if( !"".equals(Utils.isNull(code)) ){
		PopupForm popupForm = new PopupForm();
		popupForm.setCodeSearch(code);
		popupForm.setWareHouse(warehouse);
		popupForm.setBoxNo(boxNo); 
		
		List<PopupForm> results = GeneralDAO.searchStockIssue(popupForm,status,mode); 
		
		   //window.opener.setStoreMainValue
		      //   desc[i].value,storeCode[i].value
           	//	,storeName[i].value,wareHouse[i].value,
           	//requestor[i].value,issueReqDate[i].value
           	//	,custGroup[i].value);
		   
		if(results != null && results.size()>0){
			PopupForm p = (PopupForm)results.get(0);
		    
		    outputText = p.getStoreCode()+"|"+p.getStoreName()+"|"+p.getWareHouse()+"|"+p.getRequestor()+"|"+p.getIssueReqDate()+"|"+p.getCustGroup()+"|"+p.getRemark()+"|"+p.getTotalReqQty()+"|"+p.getTotalQty();
		}else{
		    outputText ="";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>