
<%@page import="com.isecinc.pens.web.location.LocationControlPage"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%
String provinceId = (String)request.getParameter("provinceId");
System.out.println(provinceId);
List<PopupBean> districts = new ArrayList<PopupBean>();
try{
	if(provinceId != null && provinceId.length()>0){
		districts = LocationControlPage.searchDistrictList(provinceId);
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<%for(PopupBean u : districts){ %>
<option value="<%=u.getDistrict()%>"><%=u.getDistrictName()%></option>
<%}%>