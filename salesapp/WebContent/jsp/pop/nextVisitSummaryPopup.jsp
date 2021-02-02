<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
	String role = (String)request.getParameter("role");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">

<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('nextVisitDate'));
}

function gotoReport(path){
	
	if(document.getElementsByName('nextVisitDate')[0].value==''){
		alert('กรุณาใส่ข้อมูลให้ครบถ้วน');
		document.getElementsByName('nextVisitDate')[0].focus();
		return;
	}
	if(!compareCurrentDate(document.getElementsByName('nextVisitDate')[0])){return;}
	
	var nextVisitDate = document.getElementsByName('nextVisitDate')[0].value;
	var fileType = '';
	if(document.getElementsByName('fileType')[0].checked){
		fileType = document.getElementsByName('fileType')[0].value;
	}else{
		fileType = document.getElementsByName('fileType')[1].value;
	}
	window.opener.setNextVisitSummary(path, nextVisitDate, fileType);
	window.close();
}

</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/saleOrderAction">
<input type="hidden" name="load" value="">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="Transaction"/>
	<jsp:param name="function" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">

	<tr>
		<td align="right" width="35%"><bean:message key="NextVisit" bundle="sysele"/></td>
		<td align="center" width="1%"><font color="red">*</font></td>
		<td align="left"><input type="text" name="nextVisitDate" id="nextVisitDate" readonly="readonly" size="15"/></td>
	</tr>
	
	<tr>
		<td align="right"><bean:message key="ReportFormat" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left" colspan="2">
		   <%--  <input type="radio" name="fileType" value="PDF" checked="checked">&nbsp;<bean:message key="PDF" bundle="sysele"/>&nbsp;&nbsp; --%>
			<input type="radio" name="fileType" value="PRINTER" checked="checked">&nbsp;<bean:message key="Printer" bundle="sysele"/>
		</td>
	</tr>
</table>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
			<a href="#" onclick="return gotoReport('${pageContext.request.contextPath}');">
			<input type="button" value="พิมพ์" class="newPosBtn">
			</a>
			<a href="#" onclick="window.close();">
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>