<%@page import="com.isecinc.pens.web.managepath.ManagePath"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.isecinc.pens.bean.User"%>
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" id="bannerHeader">
	<tr>
		<td align="left" valign="middle" width="300px;">
			
		</td>
		<%if(session.getAttribute("user")!=null){ %>
		<td align="right" valign="top">
			<div>
				<span class="userlabel"><%=((User)session.getAttribute("user")).getName() %>/<%=((User)session.getAttribute("user")).getUserGroupName() %></span>&nbsp;&nbsp;
				<br>
				<span><a href="${pageContext.request.contextPath}/?logoff=T"><span id="Logout">&nbsp;Logout&nbsp;</span></a></span>&nbsp;&nbsp;
			</div>
		</td>
		<%
		} %>
	</tr>
</table>
