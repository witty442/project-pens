<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%
String refId = (String)request.getParameter("refId");
//System.out.println(refId);
List<References> provinces = new ArrayList<References>();
try{
	if(refId != null && refId.length()>0){
		provinces = new ImportDAO().getStoreListByStoreType(refId);
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<select name="storeCodeTemp">
<%for(References u : provinces){ %>
<option value="<%=u.getKey()%>"><%=u.getName()%></option>
<%}%>
</select>