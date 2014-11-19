
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
String status = Utils.isNull(request.getParameter("status"));
String outputText = "";
try{
	System.out.println("code:"+code);
	if( !"".equals(Utils.isNull(code)) ){
		PopupForm popupForm = new PopupForm();
		popupForm.setCodeSearch(code);
		List<PopupForm> results = GeneralDAO.searchPickJob(popupForm,status); 
		
		if(results != null && results.size()>0){
			PopupForm p = (PopupForm)results.get(0);
		    
		    outputText = p.getDesc();
		}else{
		    outputText ="";
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>