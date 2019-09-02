<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.bean.User"%>

<jsp:useBean id="manageProfileSearchForm" class="com.isecinc.pens.web.profilesearch.ManageProfileSearchForm" scope="request" /> 
<%
User user = (User)session.getAttribute("user");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript">
function loadMe(path){
	<%if(request.getAttribute("save_success") != null){%>
	   var profileId = document.getElementById("profileId");
	   window.opener.changeProfile(path,profileId);
	<%}%>
}
function saveProfile(path){
    document.manageProfileSearchForm.action = path + "/jsp/manageProfileSearchAction.do?do=save&action=save";
	document.manageProfileSearchForm.submit();
}
function addProfile(path){
	 var profileName = prompt("เพิ่ม รูปแบบการค้นหาใหม่ กรุณาระบุ ชื่อ รูปแบบการค้นหา", "Profile");
	 if (profileName != null) {
	     document.getElementById("profileName").value =profileName;
	 }
    document.manageProfileSearchForm.action = path + "/jsp/manageProfileSearchAction.do?do=save&action=add";
	document.manageProfileSearchForm.submit();
	
}
function changeProfile(){
	var profileId = document.getElementById("profileId");
	var profileName = profileId.options[profileId.selectedIndex].text;
	document.getElementById("profileName").value =profileName;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe('${pageContext.request.contextPath}');MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">

	<!-- BODY -->
	<html:form action="/jsp/manageProfileSearchAction">
	<jsp:include page="../error.jsp"/>
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	    <tr>
			<td width="100%" colspan="2" align="center">
			<font size="2"><b>
			  <%--   <bean:message key="ManageProfileSearch" bundle="sysprop"/> --%>
			          เพิ่ม/แก้ไข  รูปแบบการค้นหาที่ใช้ประจำ
			  </b>
			</font>
			</td>
		</tr>
		<tr>
			<td width="30%">&nbsp;</td>
			<td width="70%"></td>
			
		</tr>
		<tr>
			<td align="right"><b>รูปแบบการค้นหา:</b></td>
			<td align="left">
			   <html:select property="bean.profileId" styleId="profileId" onchange="changeProfile()" styleClass="txt_style" >
					 <html:options collection="profileList" property="key" labelProperty="name"/>
			   </html:select>
			   (เลือกรายการที่ต้องการเปลี่ยนชื่อ)
			</td>
		</tr>
		<tr>
			<td align="right" nowrap><b>ชื่อ รูปแบบการค้นหา:</b></td>
			<td align="left">
				<html:text property="bean.profileName" size="60" styleId="profileName"  styleClass="\" autoComplete=\"off"
				maxlength="58" />
			</td>
		</tr>
	</table>
	<br>
	<!-- BUTTON -->
	<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
		<tr>
			<td align="center">
				<a href="#" onclick="return saveProfile('${pageContext.request.contextPath}');">
				  <input type="button" value="  บันทึก   " class="newPosBtn"/>
				</a>
				 
				<a href="#" onclick="window.close()">
				<input type="button" value="ปิดหน้าจอ" class="newNegBtn"/>
				</a>
				&nbsp;&nbsp;
				  <a href="#" onclick="return addProfile('${pageContext.request.contextPath}');">
				  <input type="button" value="เพิ่มรายการใหม่" class="newPosBtn"/>
				</a>
			</td>
		</tr>
	</table>

	<jsp:include page="../searchCriteria.jsp"></jsp:include>
	</html:form>
	<!-- BODY -->
				
</body>
</html>