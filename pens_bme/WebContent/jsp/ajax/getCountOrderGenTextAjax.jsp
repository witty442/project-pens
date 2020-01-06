<%@page import="com.isecinc.pens.dao.OrderDAO"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="java.util.List"%>
<%
String orderDate = Utils.isNull((String) request.getParameter("orderDate"));
System.out.println("orderDate:"+orderDate);
String outputText = "";
try{
	if( !orderDate.equals("")){
		outputText = OrderDAO.getCountGenOrderTextHis(orderDate)+"";
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=outputText %>