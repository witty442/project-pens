<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.dao.PickStockGroupDAO"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>

<%
String returnMsg ="Save Data Success";
User user = (User) request.getSession().getAttribute("user");
String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
String totalBox = Utils.isNull(request.getParameter("totalBox"));

System.out.println("issueReqNo:"+issueReqNo);
System.out.println("totalBox:"+totalBox);
try{
 PickStockGroupDAO.updateTotalBox(issueReqNo,totalBox,user);
}catch(Exception e){
	e.printStackTrace();
	returnMsg ="Cannot Save ..Please try again";
}
%>
<%=returnMsg%>