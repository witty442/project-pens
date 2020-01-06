
<%@page import="com.isecinc.pens.web.reportall.page.ProjectCReportAction"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%
String provinceName = (String)request.getParameter("provinceName");
provinceName =new String(provinceName.getBytes("ISO8859_1"), "UTF-8");
System.out.println(provinceName);
List<PopupBean> districts = new ArrayList<PopupBean>();
try{
	if(provinceName != null && provinceName.length()>0){
		districts = new ProjectCReportAction().searchAmphorList(provinceName);
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