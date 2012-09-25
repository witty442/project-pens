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
<%@page import="com.isecinc.pens.web.user.UserForm"%>
<jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="request" />
<%@page import="com.isecinc.pens.model.MGroupRole"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/user.js"></script>

<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('user.startDate'));
	new Epoch('epoch_popup','th',document.getElementById('user.endDate'));
}

</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  		<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="User"/>
				<jsp:param name="code" value="<%=userForm.getUser().getCode() %>"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/userAction">
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							
							<tr>
								<td align="right"><bean:message key="User.Name"  bundle="sysele"/><font color="red">*</font>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="user.name" size="30"  />
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.UserName"  bundle="sysele"/><font color="red">*</font>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.userName" size="20"  />
								</td>
								<td align="right"><bean:message key="User.Password"  bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:password property="user.password" size="20" maxlength="10"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.Group"  bundle="sysele"/><font color="red">*</font></td>
								<td valign="top">
								       <html:select property="user.userGroupId">
									         <html:options collection="groupList" property="key" labelProperty="name"/>
							            </html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.StartDate"  bundle="sysele"/><font color="red">*</font>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.startDate" size="15"  styleId="user.startDate" readonly="true"/>
								</td>
								<td align="right"><bean:message key="User.EndDate"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.endDate" size="15" styleId="user.endDate" readonly="true" />
								</td>
							</tr>
							
							
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="save('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
									<a href="#" onclick="addNewUser('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
									<input type="button" value="เพิ่มผู้ใช้ใหม่" class="newPosBtn">
									</a>
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<html:hidden property="user.id"/>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						</html:form>
						<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>