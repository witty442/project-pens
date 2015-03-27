
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.dao.PayDAO"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String deptId = Utils.isNull((String)request.getParameter("deptId"));
System.out.println("deptId:"+deptId);

List<PopupForm> sectionList = new ArrayList<PopupForm>();
try{
	sectionList = PayDAO.searchSectionList(deptId,"","equals");
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<%for(PopupForm u : sectionList){ %>
<option value="<%=u.getCode()%>"><%=u.getDesc()%></option>
<%}%>