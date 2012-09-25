<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%
String uCode = (String) request.getParameter("uCode");
String whereCause = "";
String uId = "";
String uName = "";
User[] results = null;

try{
	if(uCode != null && !uCode.equals("")){
		whereCause += " AND CODE = '" + uCode + "'";
		whereCause += " AND ISACTIVE = 'Y'";
		whereCause += " ORDER BY USER_ID ";
		
		results = new MUser().search(whereCause);
		for(User u : results){
			uId = String.valueOf(u.getId());
			uName = u.getName();
		}
	}
}catch(Exception e){
	e.printStackTrace();
}
%>
<%=uId%>||<%=uName%>