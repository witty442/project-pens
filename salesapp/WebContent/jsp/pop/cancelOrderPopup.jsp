<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<%@page import="com.isecinc.pens.SystemProperties"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>

<%
 String orderNo = request.getParameter("orderNo");
 String id = request.getParameter("id");
 String payment = request.getParameter("payment");
 
 String actionCancel = "";
 if(request.getAttribute("actionCancelOrder") != null){
	 actionCancel = (String)request.getAttribute("actionCancelOrder");
 }
 
%>
<script type="text/javascript">
 <% if("success".equals(actionCancel)){ %>
	    //alert("success");
	    window.opener.search('${pageContext.request.contextPath}');
		window.close();
 <% } %>
</script>
</head>
<body>
 <html:form action="/jsp/saleOrderAction">
   <jsp:include page="../error.jsp" />
   <html:hidden property="order.orderNo" value="<%=orderNo %>"/>
   <html:hidden property="order.id" value="<%=id %>"/>
   <html:hidden property="order.payment" value="<%=payment%>"/>
   
   <table align="left" border="0" cellpadding="3" cellspacing="0" width="100%">
		<tr>
			<td align="left">
			<bean:message key="DocumentNo" bundle="sysele" />&nbsp;&nbsp;
			<html:text property="order.orderNo" value="<%=orderNo %>"/></td>
		</tr>
		<tr>
			<td align="left">�˵ؼ�㹡��¡��ԡ <font color="red">*</font>&nbsp;&nbsp;
			   <html:textarea property="order.reason" rows="2" cols="40"></html:textarea>
			</td>
		</tr>
	</table>
	<table align="left" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
		<tr>
			<td align="center">
				<a href="javascript:cancelOrder('${pageContext.request.contextPath}');">
				  <input type="button" value="¡��ԡ��¡�â��" ></a> 
				<a href="javascript:window.close();">
				  <input type="button" value="�Դ" ></a>
			</td>
		</tr>
	</table>
 </html:form>
</body>
</html>