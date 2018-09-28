
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
String groupCode = (String) request.getParameter("groupCode");
String outputText = "";
try{
	//System.out.println("itemCode:"+itemCode);
	if( !"".equals(Utils.isNull(groupCode)) ){
		PopupForm popupForm = new PopupForm();
		popupForm.setCodeSearch(groupCode);
		List<PopupForm> results = SummaryDAO.searchProductFromMstByGroupCode(popupForm);
		
		if(results != null && results.size()>0){
			PopupForm p = (PopupForm)results.get(0);
		    
		    outputText = p.getCode()+"|"+p.getPrice();
		}else{
		    outputText ="";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>