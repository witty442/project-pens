<%@page import="com.isecinc.pens.web.moveorder.MoveOrderUtils"%>
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
try{
	//if( !Utils.isNull(salesChannelNo).equals("")){
		dataList = MoveOrderUtils.searchSalesrepListAll(salesChannelNo,custCatNo,salesZone);
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