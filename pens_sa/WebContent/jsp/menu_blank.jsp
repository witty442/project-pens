<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%
   /* for menu Count */
   int no = 0;
   int subNo = 0;	
   User user = (User)session.getAttribute("user");
   String role = user.getRoleSalesTarget(); 
%>
<ul id="nav">
	<li><a href="javascript: void(0)" class="parent" ><span>..</span></a>
		<ul>
          
		</ul>
	</li>
</ul>
   