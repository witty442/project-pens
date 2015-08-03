
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.NSDAO"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String channelId = (String)request.getParameter("channelId");
//System.out.println(refId);
List<PopupForm> districts = new ArrayList<PopupForm>();
try{
	if(channelId != null && channelId.length()>0){
		districts = NSDAO.searchProvinceList(channelId,"Y");
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<%for(PopupForm u : districts){ %>
<option value="<%=u.getCode()%>"><%=u.getDesc()%></option>
<%}%>