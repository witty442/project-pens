<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.dao.ProvinceDAO"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String refId = (String)request.getParameter("refId");
String notInProvinceId = Utils.isNull(request.getParameter("notInProvinceId"));
//System.out.println(refId);
List<Province> provinces = new ArrayList<Province>();
User user = (User) request.getSession(true).getAttribute("user");
try{
	if(refId != null && refId.length()>0){
		provinces = new ProvinceDAO().lookUp(user,Integer.parseInt(refId),notInProvinceId);
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