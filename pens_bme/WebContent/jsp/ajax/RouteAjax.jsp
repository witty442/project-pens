
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="com.isecinc.pens.dao.MCDAO"%>
<%@page import="com.isecinc.pens.bean.MCBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%
String mcArea = (String)request.getParameter("mcArea");
//System.out.println(refId);
List<MCBean> districts = new ArrayList<MCBean>();
List<PopupForm> districts2 = new ArrayList<PopupForm>();
try{
	if(mcArea != null && mcArea.length()>0){
		districts = MCDAO.searchRouteList(mcArea,"Y");
	}else{
		districts2 = MCDAO.searchMCRefList(new PopupForm(),"","Line-SA");
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
}
%>
<option value=""></option>
<% 
if(mcArea != null && mcArea.length()>0){
   for(MCBean u : districts){ %>
     <option value="<%=u.getMcRouteDesc()%>"><%=u.getMcRouteDesc()%></option>
<% }
  }else{
	  for(PopupForm u : districts2){ 
  %>
    <option value="<%=u.getCode()%>"><%=u.getDesc()%></option>
 <%}}%>