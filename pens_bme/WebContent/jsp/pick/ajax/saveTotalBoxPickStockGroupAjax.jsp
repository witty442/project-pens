<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.dao.PickStockGroupDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String returnMsg ="Save Data Success";
User user = (User) request.getSession().getAttribute("user");
String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
String totalBox = Utils.isNull(request.getParameter("totalBox"));
String forwarder = Utils.isNull(request.getParameter("forwarder"));
forwarder = new String(forwarder.getBytes("ISO8859_1"), "UTF-8");
String deliveryDate = Utils.isNull(request.getParameter("deliveryDate"));

System.out.println("issueReqNo:"+issueReqNo);
System.out.println("totalBox:"+totalBox);
System.out.println("forwarder:"+forwarder);
System.out.println("deliveryDate:"+deliveryDate);
try{
    PickStockGroupDAO.updateTotalBox(user,issueReqNo,totalBox,forwarder,deliveryDate);
}catch(Exception e){
	e.printStackTrace();
	returnMsg ="Cannot Save ..Please try again";
}
%>
<%=returnMsg%>