
<%@page import="com.isecinc.pens.dao.DistrictDAO"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%
String provinceId = (String)request.getParameter("refId");
//System.out.println(refId);
List<District> districts = new ArrayList<District>();
try{
	if(provinceId != null && provinceId.length()>0){
		districts = new DistrictDAO().lookUp(provinceId);
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