<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
List<References> roles = InitialReferences.getReferenes().get(InitialReferences.ROLE);
pageContext.setAttribute("roles",roles,PageContext.PAGE_SCOPE);

List<References> actives= InitialReferences.getReferenes().get(InitialReferences.ACTIVE);
pageContext.setAttribute("actives",actives,PageContext.PAGE_SCOPE);

boolean isVAN = false;
User user = null;
if(session.getAttribute("user")!=null){
	user = (User)session.getAttribute("user");
	isVAN = User.VAN.equalsIgnoreCase(user.getRole().getKey());
}
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.core.bean.References"%>
<jsp:useBean id="userForm" class="com.isecinc.pens.web.user.UserForm" scope="request" />

<%@page import="com.isecinc.pens.bean.SubInventory"%>
<%@page import="com.isecinc.pens.model.MSubInventory"%>
<%@page import="com.isecinc.pens.bean.User"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/user.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript">
function loadMe(){
	changeRoles(document.getElementById('userRole').value);
	
}
function changeRoles(val){
	if(val=='VAN'){
		document.getElementById('subInvs').style.display='';
	}else{
		document.getElementById('subInvs').style.display='none';
	}
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
				<jsp:param name="function" value="Profile"/>
				<jsp:param name="code" value="${user.code} ${user.name}"/>
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
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="28%"></td>
								<td width="20%"></td>
								<td width="15%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.Number"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.code" size="20" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="User.Category"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.category" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.Organization"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.organization" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.StartDate"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.startDate" size="15" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="User.EndDate"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.endDate" size="15" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.Name"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="user.name" size="30" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.SourceName"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.sourceName" size="30" readonly="true" styleClass="disableText"/>
								</td>
								<% if(isVAN) {%>
									<td align="right"><bean:message key="User.PDPaid"  bundle="sysele"/>&nbsp;&nbsp;</td>
									<td align="left">									
										<input type="checkbox" <c:if test="${user.pdPaid=='Y'}">checked</c:if> name="user.pdPaid" value="Y">
									</td>
								<% } %>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.IDCardNo"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.idCardNo" size="30" readonly="true" styleClass="disableText"/>
								</td>
								<% if(isVAN) {%>
									<td align="right">ส่งเงินให้ Pens&nbsp;&nbsp;</td>
									<td align="left">									
										<input type="checkbox" <c:if test="${user.moneyToPens=='Y'}">checked</c:if> name="user.moneyToPens" value="Y">
									</td>
								<% } %>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.Role"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="user.type" styleId="userRole" disabled="true" styleClass="disableText">
										<html:option value=""></html:option>
										<html:options collection="roles" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="User.UserName"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="user.userName" size="20" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="User.Password"  bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:password property="user.password" readonly="true" styleClass="disableText" size="20" maxlength="10"/>
								</td>
							</tr>
							<tr>
								<td></td><td></td>
								<td align="right"><bean:message key="User.PasswordConfirm"  bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:password property="user.confirmPassword" readonly="true" styleClass="disableText" size="20" maxlength="10"/>
								</td>
							</tr>
							<tr id="subInvs">
								<c:if test="${userForm.pdList!=null}">
								<td align="right" valign="top"><bean:message key="User.SubInv"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td colspan="2" align="left">
									<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
										<c:forEach var="results" items="${userForm.pdList}" varStatus="rows">
										  <c:if test="${results.pdCode !=''}">
											<c:choose>
												<c:when test="${rows.index %2 == 0}">
													<c:set var="tabclass" value="lineO"/>
												</c:when>
												<c:otherwise>
													<c:set var="tabclass" value="lineE"/>
												</c:otherwise>
											</c:choose>
											<tr class="<c:out value='${tabclass}'/>">
												<td align="center" width="10px">
													<input type="checkbox" name="subinvids" value="${results.pdCode}" checked="checked" disabled="disabled"/>
												</td>
												<td align="left">${results.pdCode} - ${results.pdDesc}</td>
											</tr>
										   </c:if>
										</c:forEach>
									</table>
								</td>
								</c:if>
							</tr> 
							<%-- <tr id="subInvs">
								<c:if test="${userForm.subInventories!=null}">
								<td align="right" valign="top"><bean:message key="User.SubInv"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td colspan="2" align="left">
									<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
										<c:forEach var="results" items="${userForm.subInventories}" varStatus="rows">
										<c:choose>
											<c:when test="${rows.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										<tr class="<c:out value='${tabclass}'/>">
											<td align="center" width="10px">
												<c:if test="${results.selected=='Y'}">
													<input type="checkbox" name="subinvids" value="${results.id}" checked="checked" disabled="disabled"/>
												</c:if>
												<c:if test="${results.selected=='N'}">
													<input type="checkbox" name="subinvids" value="${results.id}" disabled="disabled"/>
												</c:if>
											</td>
											<td align="left">${results.id} - ${results.description}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
								</c:if>
							</tr> --%>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<% if(isVAN) { %> 
									<a href="#" onclick="return profileSave('${pageContext.request.contextPath}');">
									<input type="button" value="บันทึก" class="newPosBtn"/>
									</a>
									<% } %>
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn"/>
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