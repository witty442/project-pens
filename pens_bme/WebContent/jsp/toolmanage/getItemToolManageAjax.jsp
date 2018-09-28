<%@page import="com.isecinc.pens.web.toolmanage.ToolManageBean"%>
<%@page import="com.isecinc.pens.dao.ToolManageDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%
String item = Utils.isNull((String) request.getParameter("item"));

String outputText = "";
try{
	//System.out.println("itemCode:"+itemCode);
	if( !"".equals(Utils.isNull(item)) ){
		
		PopupForm popupForm = new PopupForm();
		popupForm.setCodeSearch(item);
		ToolManageBean b = ToolManageDAO.searchItemMaster(popupForm); 
		
		if(b != null ){
		    outputText = b.getItemName();
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