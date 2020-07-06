<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.dao.SalesrepDAO"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String salesChannelNo = Utils.isNull(request.getParameter("salesChannelNo"));
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
String salesZone = Utils.isNull(request.getParameter("salesZone"));
String salesrepCode = Utils.isNull(request.getParameter("salesrepCode"));
String roleNameChk = Utils.isNull(request.getParameter("roleNameChk"));
String pageName = Utils.isNull(request.getParameter("pageName")); //for spacification by pageName

System.out.println("salesChannelNo:"+salesChannelNo);
System.out.println("custCatNo:"+custCatNo);
System.out.println("salesZone:"+salesZone);
List<PopupBean> dataList= null;
User user = (User)session.getAttribute("user");
String selected ="";
try{
	dataList = SalesrepDAO.searchSalesrepListAll(pageName,salesChannelNo,custCatNo,salesZone,user,roleNameChk); 
	
if(dataList != null){
	if(dataList.size()>1){
	%>
	<option value=""></option>
	<%} %>
	<%for(PopupBean u : dataList){ 
		selected = u.getSalesrepCode().equalsIgnoreCase(salesrepCode)?"selected":"";
	%>
	   <option <%=selected %> value="<%=u.getSalesrepCode()%>"><%=u.getSalesrepCode()%></option>
	<%}//for
 }//if
}catch(Exception e){ 
	e.printStackTrace();
}finally{
}
%>