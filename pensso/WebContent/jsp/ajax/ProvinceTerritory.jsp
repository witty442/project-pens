<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%>
<%
String refId = (String)request.getParameter("refId");
//System.out.println(refId);
List<Province> provinces = new ArrayList<Province>();
try{
	if(refId != null && refId.length()>0){
		provinces = new MProvince().lookUp(Integer.parseInt(refId));
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<%for(Province u : provinces){ 
 //System.out.println("provinceId:"+u.getId());
%>
<option value="<%=u.getId()%>"><%=u.getName()%></option>
<%}%>