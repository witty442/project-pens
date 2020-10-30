
<%@page import="com.pens.util.EnvProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%
User user = (User)session.getAttribute("user");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/footer_left.png" border="0"/></td>
		<td width="44px;"><img src="${pageContext.request.contextPath}/images2/footer_left2.png" border="0"/></td>
        <td background="${pageContext.request.contextPath}/images2/footer01.png">
        	Application Version
			
			<a href ="javascript:PopupCenterFull('<%=request.getContextPath()%>/jsp/adminConsole/adminConsole.jsp','')" title="Configuration">
			  <font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
			</a>
			  Type:[<b><%=EnvProperties.getInstance().getProperty("product.type")%></b>]
			  
			  <%if(user.getUserName().equalsIgnoreCase("admin")) {%>
			   <a href ="javascript:PopupCenterFullHeight('<%=request.getContextPath()%>/jsp/administer/pageVisit.jsp','',500)" title="Configuration">
			    : <B>PageVisit</B>
			   </a>
			  <%} %>
		</td>
        <td width="60px"><img src="${pageContext.request.contextPath}/images2/footer_right2.png" border="0"/></td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/footer_right.png" border="0"/></td>
	</tr>
</table>