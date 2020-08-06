
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.PopupDAO"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%

String custCode = (String) request.getParameter("custCode");
String outputText = "";
try{

	System.out.println("custCode:"+custCode);
	
	if( !"".equals(Utils.isNull(custCode)) ){
		//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		PopupBean c = new PopupBean();
		c.setCodeSearch(custCode);
		
	    List<PopupBean> result = PopupDAO.searchCustomerMaster(c, "equals");
		if(result != null && result.size() >0){
			PopupBean r = result.get(0);
		    outputText = r.getDesc();
		}else{
		   outputText ="";
		} 
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>