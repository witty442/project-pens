<!--
Recommented By Popup for Member 
 -->
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
	String Id = (String) request.getParameter("id");
	pageContext.setAttribute("memberId", Id, PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="com.isecinc.pens.model.MUOM"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
function loadMe(){
	searchMember(null);
}
function searchMember(e){
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/view/recommentedViewQuery.jsp",
				data : "mId=" + encodeURIComponent($('#memberId').val()),
				success: function(getData){
					var returnString = jQuery.trim(getData);
					document.getElementById('results').innerHTML = returnString;
				}
			}).responseText;
		});
	}
}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value="Recommend"/>
	<jsp:param name="code" value=""/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="35%"></td>
	</tr>
	<tr>
		<td colspan="3">
			<div id="results" style="height:280px; overflow-x: hidden; overflow-y: scroll;"></div>
		</td>
	</tr>
</table>
<br/><br/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="60%">&nbsp;</td>
		<td align="left">
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<br/>
<input type="hidden" id="memberId" value="${memberId}"/>
</body>
</html>