<%@page import="com.isecinc.pens.web.promotion.PromotionUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String salesChannelNo = Utils.isNull(request.getParameter("salesChannelNo"));
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
System.out.println("salesChannelNo:"+salesChannelNo);
System.out.println("custCatNo:"+custCatNo);
List<PopupBean> dataList= null;
try{
	//if( !Utils.isNull(salesChannelNo).equals("")){
		dataList = PromotionUtils.searchSalesrepListAll(salesChannelNo,custCatNo);
	//}
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