<%@page import="com.isecinc.pens.inf.helper.SessionIdUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionIdUtils.getInstance().getIdSession()%>"></script>
<%
  User user = (User)session.getAttribute("user");
 // System.out.println("Role:"+user.getRole().getKey());
  
%>
<%if ( Utils.userInRole(user,new String[]{User.WACOAL}) ){ %>
   <jsp:include page="menu_wacoal.jsp" flush="true"/>
<%}else{%>
   <jsp:include page="menu_pens.jsp" flush="true"/>
<%}%>