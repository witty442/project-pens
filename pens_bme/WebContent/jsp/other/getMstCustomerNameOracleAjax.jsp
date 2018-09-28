
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String custCode = Utils.isNull(request.getParameter("custCode"));
String outputText = "";
try{
	System.out.println("custCode:"+custCode);
	if( !"".equals(Utils.isNull(custCode)) ){
		//condCode = new String(condCode.getBytes("ISO8859_1"), "UTF-8");
		 outputText = GeneralDAO.getMstCustomerNameOracle(custCode);
		 System.out.println("custName:"+outputText);
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>