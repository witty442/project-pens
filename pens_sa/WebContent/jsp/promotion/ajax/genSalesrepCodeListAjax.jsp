<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.promotion.PromotionUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String salesChannelNo = Utils.isNull(request.getParameter("salesChannelNo"));
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
String salesZone = Utils.isNull(request.getParameter("salesZone"));
System.out.println("salesChannelNo:"+salesChannelNo);
System.out.println("custCatNo:"+custCatNo);
List<PopupBean> dataList= null;
String salesrepCode = "";
User user = (User)session.getAttribute("user");
try{
	//Case Sales Login filter show only salesrepCode 
	if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
		salesrepCode = user.getUserName().toUpperCase();
	}
	dataList = PromotionUtils.searchSalesrepListAll(salesChannelNo,custCatNo,salesZone,salesrepCode);
	
if(dataList != null){ 
	if(dataList.size()>1){
	%>
	<option value=""></option>
	<%} %>
	<%for(PopupBean u : dataList){ %>
	<option value="<%=u.getSalesrepCode()%>"><%=u.getSalesrepCode()%></option>
	<%}//for
 }//if
}catch(Exception e){ 
	e.printStackTrace();
}finally{
}
%>