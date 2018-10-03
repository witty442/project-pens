<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.ProductCategory"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%
String segment = (String)request.getParameter("segment");
String psegmentId = (String)request.getParameter("psegmentId");

//System.out.println(segment+":"+psegmentId);

List<ProductCategory> cats = new ArrayList<ProductCategory>();
try{
	if(psegmentId.length()>0)
	cats = new MProductCategory().lookUp(Integer.parseInt(segment),Integer.parseInt(psegmentId));
}catch(Exception e){
	e.printStackTrace();
}finally{	
}
%>
<option value=""></option>
<%for(ProductCategory c : cats){ %>
<option value="<%=c.getId()%>"><%=c.getName()%></option>
<%}%>