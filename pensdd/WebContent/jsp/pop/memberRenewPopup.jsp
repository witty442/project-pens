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
<jsp:useBean id="memberRenewForm" class="com.isecinc.pens.web.memberrenew.MemberRenewForm" scope="request" />

<%
String id = (String)request.getParameter("id");
pageContext.setAttribute("type", request.getParameter("type"), PageContext.PAGE_SCOPE);

String isPrepare = request.getSession().getAttribute("isPrepare") != null ? (String) request.getSession().getAttribute("isPrepare") : "";
String save_success = request.getSession().getAttribute("save_success") != null ? (String) request.getSession().getAttribute("save_success") : "";

List<References> membertypes = InitialReferences.getReferenes().get(InitialReferences.MEMBER_TYPE);
pageContext.setAttribute("membertypes", membertypes, PageContext.PAGE_SCOPE);
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
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(path){
	if(<%=!isPrepare.equals("Y")%>){
		loadMember(path);
	}
	if(<%=save_success.equals("Y")%>){
		save_success(path, document.getElementsByName('type')[0].value);
	}
	new Epoch('epoch_popup', 'th', document.getElementById('renewedDate'));
}

function loadMember(path){
	var id = '<%=id%>';
	document.memberRenewForm.action = path + "/jsp/memberRenewAction.do?do=prepare&id="+id;
	document.memberRenewForm.submit();
	return true;
}

function save(path){
	document.memberRenewForm.action = path + "/jsp/memberRenewAction.do?do=save";
	document.memberRenewForm.submit();
	return true;
}

function save_success(path, type){
	alert('บันทึกข้อมูลเรียบร้อยแล้ว');
	window.opener.search(path, type);
	window.close();
}

</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<!-- BODY -->
<html:form action="/jsp/memberRenewAction">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="MasterData"/>
	<jsp:param name="function" value="Renew"/>
	<jsp:param name="code" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td colspan="4" align="left"><%-- &nbsp;<html:text property="memberRenew.id" size="15" readonly="true" styleClass="disableText"/> --%></td>
	</tr>
	<tr>
		<td align="right" width="25%">&nbsp;&nbsp;</td>
		<td align="right" colspan="2"><bean:message key="Member.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left"><html:text property="memberRenew.member.code" size="15" readonly="true" styleClass="disableText"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Member.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left" width="20%"><html:text property="memberRenew.member.name" readonly="true" styleClass="disableText"/></td>
		<td align="right" width="15%"><bean:message key="Member.Surname" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left"><html:text property="memberRenew.member.name2" readonly="true" styleClass="disableText"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="LastRenewDate" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<html:text property="memberRenew.appliedDate" readonly="true" styleClass="disableText" size="15"/>			
		</td>
		<td align="right"><bean:message key="Member.ExpireDate" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left"><html:text property="memberRenew.expiredDate" size="15" readonly="true" styleClass="disableText"/></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="RenewedAgain" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<html:select property="memberRenew.memberType">
				<html:options collection="membertypes" property="key" labelProperty="name" />
			</html:select>
		</td>
		<td align="right"><bean:message key="RenewDate" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<html:text property="memberRenew.renewedDate" styleId="renewedDate" size="15" readonly="true"/>
		</td>
	</tr>
</table>
<br><br>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
			<a href="#" onclick="return save('${pageContext.request.contextPath}');">
<!--			<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn">
			</a>
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
<%request.getSession().removeAttribute("isPrepare"); %>
<html:hidden property="memberRenew.member.id"/>
<input type="hidden" name="type" value="${type}"/>
<jsp:include page="../searchCriteria.jsp"></jsp:include>
</html:form>
<!-- BODY -->
</body>
</html>