<%@page import="com.isecinc.pens.web.prodshow.ProdShowUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String salesChannelNo = Utils.isNull(request.getParameter("salesChannelNo"));
String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
String salesZone = Utils.isNull(request.getParameter("salesZone"));
System.out.println("salesChannelNo:"+salesChannelNo);
System.out.println("custCatNo:"+custCatNo);
System.out.println("salesZone:"+salesZone);
List<PopupBean> dataList= null;
try{
	//if( !Utils.isNull(salesChannelNo).equals("")){
		dataList = ProdShowUtils.searchSalesrepListAll(salesChannelNo,custCatNo,salesZone);
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