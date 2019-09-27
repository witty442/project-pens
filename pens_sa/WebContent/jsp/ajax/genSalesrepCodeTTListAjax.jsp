<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetTTUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
User user = (User) request.getSession().getAttribute("user");
String salesChannelNo = (String)request.getParameter("salesChannelNo");
String custCatNo = (String)request.getParameter("custCatNo");
String salesZone = (String)request.getParameter("salesZone");
System.out.println("salesChannelNo:"+salesChannelNo);
System.out.println("custCatNo:"+custCatNo);
System.out.println("salesZone:"+salesZone);

List<PopupBean> dataList= null;
try{
   dataList = SalesTargetTTUtils.searchSalesrepListTT(user,salesChannelNo,custCatNo,salesZone);
}catch(Exception e){ 
	e.printStackTrace();
}finally{
}
if(dataList != null && dataList.size()>0){
%>
 <option value=""></option>
   <%for(PopupBean u : dataList){ %>
     <option value="<%=u.getSalesrepCode()%>"><%=u.getSalesrepCode()%></option>
   <%}%>
<%}else{%>
<option value=""></option>
<%}%>