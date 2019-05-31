<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetUtils"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String salesChannelNo = (String)request.getParameter("salesChannelNo");
//System.out.println("salesChannelNo:"+salesChannelNo);
List<PopupBean> dataList= null;
try{
	if( !Utils.isNull(salesChannelNo).equals("")){
		dataList = SalesTargetUtils.searchCustCatNoMTList(salesChannelNo);
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
if(dataList != null && dataList.size()>1){
%>
<option value=""></option>
<%} %>
<%for(PopupBean u : dataList){ %>
<option value="<%=u.getCustCatNo()%>"><%=u.getCustCatDesc()%></option>
<%}%>