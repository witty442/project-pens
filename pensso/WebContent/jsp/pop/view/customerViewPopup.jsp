<!--
Customer Popup 
 -->
<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
	pageContext.setAttribute("uId", (String)request.getParameter("uId"), PageContext.PAGE_SCOPE);
	pageContext.setAttribute("main", (String)request.getParameter("main"), PageContext.PAGE_SCOPE);
	pageContext.setAttribute("id", (String)request.getParameter("id"), PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="com.isecinc.pens.model.MUOM"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
function loadMe(){
	searchCustomer(null);
}
function searchCustomer(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/view/customerViewQuery.jsp",
				data : "cName=" + encodeURIComponent($('#customerName').val()) + "&uId=" + $('#uId').val() + "&main="+ $('#main').val()+"&id="+$('#id').val(),
				success: function(getData){
					var returnString = jQuery.trim(getData);
					document.getElementById('results').innerHTML = returnString;
				}
			}).responseText;
		});
	}
}

function selectCustomer(code, name, name2){
	window.opener.setMainCustomer(code, name + ' ' + name2);
	window.close();
}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="35%">&nbsp;</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Customer.Name" bundle="sysele"/><bean:message key="Customer" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left" width="30%">
			<input type="text" id="customerName" name="customerName" size="15" onkeypress="searchCustomer(event)"/>
		</td>
		<td align="left">
			<input type="button" value="Search" onclick="searchCustomer(null);"/>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<div id="results" style="height:220px; overflow-x: hidden; overflow-y: scroll;"></div>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td colspan="2">&nbsp;</td>
		<td align="left">
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<input type="hidden" id="uId" value="${uId}"/>
<input type="hidden" id="main" value="${main}"/>
<input type="hidden" id="id" value="${id}"/>
<br/>
</body>
</html>