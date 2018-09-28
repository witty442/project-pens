<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="java.net.InetAddress"%>
<%@page import="com.isecinc.pens.inf.helper.Constants"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<%
User user = (User)session.getAttribute("user");
%>
<ul id="nav">
<%if ( Utils.userInRole(user,new String[]{User.ADMIN}) ){%>
  	 <li>
  	   <a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/schedule.do?do=prepare&action=new';"><span><bean:message bundle="sysprop" key="scheduler"/></span></a>
	 </li>
	 <li>
		<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/searchTask.do?do=prepare&action=new';"><span><bean:message bundle="sysprop" key="searchTask"/></span></a>
	</li>
<%} %>

</ul>
   