
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String mcArea = (String)request.getParameter("mcArea");
//System.out.println(refId);
List<MCBean> districts = new ArrayList<MCBean>();
try{
	if(mcArea != null && mcArea.length()>0){
		districts = MCDAO.searchRouteList(mcArea,"Y");
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<%for(MCBean u : districts){ %>
<option value="<%=u.getMcRoute()%>"><%=u.getMcRouteDesc()%></option>
<%}%>