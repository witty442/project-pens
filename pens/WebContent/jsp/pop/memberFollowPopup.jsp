<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="memberFollowForm" class="com.isecinc.pens.web.memberfollow.MemberFollowForm" scope="request" />

<%
	int id = 0;
	if(request.getParameter("id") != null && !request.getParameter("id").equals("")){
		id = Integer.parseInt((String)request.getParameter("id"));
	}else{
		id = memberFollowForm.getMemberFollow().getCustomerId();
	}
	pageContext.setAttribute("customerId", id, PageContext.PAGE_SCOPE);	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(path){
	if($('#isFirst').val()=='N'){
		search(path);
	}
	document.getElementById('div_new_record').style.display='none';
}

function search(path){
	document.memberFollowForm.action = path + "/jsp/memberFollowAction.do?do=search&fs=Y";
	document.memberFollowForm.submit();
	return true;
}

function save(path){
	if($('#followDate').val()==''){
		alert('ข้อมูลไม่เพียงพอ');
		$('#followDate').focus();return;
	}
	if($('#followTime').val()==''){
		alert('ข้อมูลไม่เพียงพอ');
		$('#followTime').focus();return;
	}
	if($('#followDetail').val()==''){
		alert('ข้อมูลไม่เพียงพอ');
		$('#followDetail').focus();return;
	}
	
	document.memberFollowForm.action = path + "/jsp/memberFollowAction.do?do=save";
	document.memberFollowForm.submit();
	return true;
}

function addFollow(){
	document.getElementById('div_new_record').style.display='';
	//$('#div_new_record').show(300);
	new Epoch('epoch_popup','th',document.getElementById('followDate'));
}
function cancel(){
	$('#div_new_record').slideUp(300);
}
</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<!-- BODY -->
<html:form action="/jsp/memberFollowAction">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="MasterData"/>
	<jsp:param name="function" value="MemberFollow"/>
	<jsp:param name="code" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td colspan="4" align="left">&nbsp;</td>
	</tr>
</table>
<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
&nbsp;<span class="searchResult">${memberFollowForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
<table align="left" border="0" cellpadding="3" cellspacing="1" width="100%">
	<tr>
		<td colspan="4" align="left">
			&nbsp;<input type="button" value="เพิ่มรายการ" onclick="addFollow();"/>
		</td>
	</tr>			
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th width="15px;"><bean:message key="No" bundle="sysprop"/></th>
		<th class="date"><bean:message key="FollowDateTime"  bundle="sysele"/></th>
		<th class="code">ผู้ติดตาม</th>
		<th class="code"><bean:message key="FollowBy"  bundle="sysele"/></th>
		<th width="100px;"><bean:message key="IsRenew"  bundle="sysele"/></th>
		<th><bean:message key="Detail"  bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${memberFollowForm.results}" varStatus="rows">
	<c:choose>
		<c:when test="${rows.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
		<td><c:out value='${rows.index+1}'/></td>
		<td align="center">${results.followDate}&nbsp;&nbsp;${results.followTime}</td>
		<td align="left">${results.user.name}</td>
		<td align="left">${results.followByLabel}</td>
		<td align="center">
			<c:if test="${results.renewed=='Y'}">
				<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
			</c:if>
			<c:if test="${results.renewed!='Y'}">
			-
			</c:if>
		</td>
		<td align="left">${results.followDetail}</td>
	</tr>
	</c:forEach>
	<tbody>
	<tr class="lineE" id="div_new_record">
		<td>&nbsp;</td>
		<td align="left" colspan="2">
			<html:text property="memberFollow.followDate" size="15" styleId="followDate" readonly="true"/>&nbsp;
			<html:text property="memberFollow.followTime" styleId="followTime" maxlength="5" size="5" onkeypress="return inputTime(event, this);"/>
		</td>
		<td align="left" valign="middle">
			<html:select property="memberFollow.followBy">
				<html:option value="T"><bean:message key="Telephone" bundle="sysele"/></html:option>
				<html:option value="E"><bean:message key="Email" bundle="sysele"/></html:option>
				<html:option value="O"><bean:message key="Other" bundle="sysele"/></html:option>
			</html:select>
		</td>
		<td align="center" valign="middle">
			<html:checkbox property="memberFollow.renewed" value="Y"/>&nbsp;<bean:message key="IsRenew" bundle="sysele"/>
		</td>
		<td align="left" valign="middle">
			<html:textarea property="memberFollow.followDetail" styleId="followDetail" rows="2" cols="40"></html:textarea>&nbsp;
			<input type="button" value="บันทึก" onclick="save('${pageContext.request.contextPath}');"/>
			<input type="button" value="ยกเลิก" onclick="cancel();"/>
		</td>
	</tr>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td align="left" colspan="11" class="footer">&nbsp;</td>
	</tr>
</table>
<br><br>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
<html:hidden property="memberFollow.customerId" value="${customerId}"/>
<html:hidden property="isFirst" styleId="isFirst"/>
<jsp:include page="../searchCriteria.jsp"></jsp:include>
</html:form>
<!-- BODY -->
</body>
</html>