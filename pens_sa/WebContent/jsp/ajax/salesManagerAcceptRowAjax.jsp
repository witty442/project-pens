
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetConstants"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetDAO"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
User user = (User)session.getAttribute("user");
long id = Utils.convertStrToLong(request.getParameter("id"),0);
String outputText = "";
try{
	System.out.println("id:"+id);
	if( 0 != id ){
		boolean r = SalesTargetDAO.salesManagerAccept(user,id);
		if(r){
		  outputText = SalesTargetConstants.STATUS_FINISH;
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>