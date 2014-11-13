<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%>
<%
String refId = (String)request.getParameter("refId");
//System.out.println(refId);
List<District> districts = new ArrayList<District>();
try{
	if(refId != null && refId.length()>0){
		districts = new MDistrict().lookUp(Integer.parseInt(refId));
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<%for(District u : districts){ %>
<option value="<%=u.getId()%>"><%=u.getName()%></option>
<%}%>