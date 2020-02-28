<%@page import="com.isecinc.pens.web.autoorder.AutoOrderDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
String orderDate = Utils.isNull(request.getParameter("orderDate"));
String outputText = "";//pass
try{
	System.out.println("orderDate:"+orderDate);
	if( !"".equals(Utils.isNull(orderDate)) ){
		//check other store not confirm (status is null)
		 String storeNotConfirm =AutoOrderDAO.storeOrdeRepNoConfirm(orderDate);
		 if( !Utils.isNull(storeNotConfirm).equals("")){
			 outputText = storeNotConfirm;
		 }
	}//if
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>