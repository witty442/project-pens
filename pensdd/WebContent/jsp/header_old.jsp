<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String system="";
String function="";
String code="";
system=(request.getParameter("system")==null?"":request.getParameter("system"));
function=(request.getParameter("function")==null?"":request.getParameter("function"));
code=(request.getParameter("code")==null?"":request.getParameter("code"));
if(system.length()>0) system = SystemProperties.getCaption(system,Locale.getDefault());
if(function.length()>0) function = SystemProperties.getCaption(function,Locale.getDefault());
if(code.equals("null")) code = SystemProperties.getCaption(SystemProperties.CREATE_NEW_RECORD,Locale.getDefault());
%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<div class="headerlabel">
<table border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr height="40px;">
		<td align="left">
			PENS &#169;
		</td>
		<td align="right" valign="bottom">
			<div><span class="userlabel">welcome : <%=((User)session.getAttribute("user")).getName() %>&nbsp; type : <%=((User)session.getAttribute("user")).getType() %>&nbsp;&nbsp;</span></div>
			<div>
				&nbsp;
				<a href="${pageContext.request.contextPath}/jsp/mainpage.jsp"><span class="menuheader">&nbsp;Menu&nbsp;</span></a>
				<!-- <a href="${pageContext.request.contextPath}/jsp/userAction.do?do=profile"><span class="menuheader">&nbsp;Profile&nbsp;</span></a> -->
				<a href="#" onclick="changeURL('${pageContext.request.contextPath}/jsp/administer/profile.jsp');"><span class="menuheader">&nbsp;Profile&nbsp;</span></a>
				<a href="${pageContext.request.contextPath}/?logoff=T"><span class="menuheader">&nbsp;Logout&nbsp;</span></a>
				&nbsp;
			</div>
		</td>
	</tr>
</table>
</div>