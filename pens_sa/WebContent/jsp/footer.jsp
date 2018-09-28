
<%@page import="com.isecinc.pens.report.salesanalyst.helper.EnvProperties"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="27px;"><img src="${pageContext.request.contextPath}/images2/footer_left.png" border="0"/></td>
		<td width="44px;"><img src="${pageContext.request.contextPath}/images2/footer_left2.png" border="0"/></td>
        <td background="${pageContext.request.contextPath}/images2/footer01.png">
        	Application Version
			
			<a href ="javascript:window.open('<%=request.getContextPath()%>/jsp/adminConsole/adminConsole.jsp','','width=800px,height=600px')" title="Configuration">
			  <font color="red"><b><bean:message bundle="sysprop" key="AppVersion"/></b></font>
			</a>
			  Type:[<b><%=EnvProperties.getInstance().getProperty("product.type")%></b>]
		</td>
        <td width="60px"><img src="${pageContext.request.contextPath}/images2/footer_right2.png" border="0"/></td>
        <td width="31px;"><img src="${pageContext.request.contextPath}/images2/footer_right.png" border="0"/></td>
	</tr>
</table>