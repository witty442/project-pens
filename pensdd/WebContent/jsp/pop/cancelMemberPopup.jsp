<!--
Cancel Member Popup for Member 
 -->
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.UOM"%>
<%@page import="com.isecinc.pens.model.MUOM"%>
<jsp:useBean id="memberForm" class="com.isecinc.pens.web.member.MemberForm" scope="request" />
<%
	String Id = (String) request.getParameter("id");
	pageContext.setAttribute("memberId", Id, PageContext.PAGE_SCOPE);
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
<script type="text/javascript" language="javascript">
function loadMe(){
	if(<%=memberForm.getMember().getId()!=0%>){
		document.getElementById('cancelReason').disabled = true;
	}
}

// call ajax
function cancelMember(path){
	if($('#cancelReason').val()==''){
		alert('กรุณาระบุข้อมูลให้ครบถ้วน');
		$('#cancelReason').focus();
		return;
	}

	if(!confirm('คุณต้องการยกเลิกสมาชิก?')){
		return;
	}
	document.memberForm.action = path + "/jsp/memberAction.do?do=cancelMember";
	document.memberForm.submit();
}

function checkMaxlength(objText, length){
	if(objText.value.length > length){
		return false;
	}else{
		return true;
	}
}

function closePopup(path){
	window.opener.search(path);
	window.close();
}
</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<html:form action="/jsp/memberAction">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="ยกเลิกสมาชิก"/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="35%">&nbsp;</td>
		<td></td>
	</tr>
	<tr valign="top">
		<td align="right"><bean:message key="Other.CancelReason" bundle="sysele"/><font color="red">*</font></td>
		<td align="left" valign="top">
			<html:textarea property="member.cancelReason" styleId="cancelReason" rows="5" cols="40" onkeypress="return checkMaxlength(this, 255);"></html:textarea>			
		</td>
	</tr>
</table>
<br/><br/>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td width="50%">&nbsp;</td>
		<td align="left">
			<%if(memberForm.getMember().getId()==0){%>
				<a href="#" onclick="cancelMember('${pageContext.request.contextPath}');">
<!--				<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
				<input type="button" value="บันทึก" class="newPosBtn">
				</a>
				<a href="#" onclick='window.close();'>
<!--				<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
				<input type="button" value="ยกเลิก" class="newNegBtn">
				</a>
			<%}else{ %>
				<a href="#" onclick="closePopup('${pageContext.request.contextPath}');">
<!--				<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
				<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
				</a>
			<%} %>
		</td>
	</tr>
</table>
<html:hidden property="member.id" value='${memberId}'/>
<jsp:include page="../searchCriteria.jsp"></jsp:include>
</html:form>
</body>
</html>