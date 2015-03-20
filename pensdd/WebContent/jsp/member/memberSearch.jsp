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
<%
List<References> types = InitialReferences.getReferenes().get(InitialReferences.MEMBER_TYPE);
pageContext.setAttribute("types", types, PageContext.PAGE_SCOPE);

List<References> levels = InitialReferences.getReferenes().get(InitialReferences.MEMBER_STATUS);
pageContext.setAttribute("levels", levels, PageContext.PAGE_SCOPE);

List<References> actives = InitialReferences.getReferenes().get(InitialReferences.ACTIVE);
pageContext.setAttribute("actives",actives,PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="memberForm" class="com.isecinc.pens.web.member.MemberForm" scope="request" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/member.js"></script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="MemberInfo"/>
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
						<html:form action="/jsp/memberAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td colspan="2" align="left">
									<a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
									<img border=0 src="${pageContext.request.contextPath}/icons/user_add.gif" align="absmiddle">&nbsp;
									<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
								</td>
							</tr>
							<tr>
								<td width="45%" align="right"><bean:message key="Member.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="member.code" styleId="memberCode" onkeypress="searchKeypress(event,'${pageContext.request.contextPath}')"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Member.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="member.name"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Member.Surname" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="member.name2"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Member.PIDNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="member.personIDNo"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Contact.Phone" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="contacts[0].phone"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Contact.Mobile" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="contacts[0].mobile"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Condition.MemberType" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:select property="member.memberType" style="width: 120px;">
										<html:option value=""></html:option>
										<html:options collection="types" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Other.MemberStatus" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:select property="member.memberLevel" style="width: 120px;">
										<html:option value=""></html:option>
										<html:options collection="levels" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:select property="member.isActive" style="width: 120px;">
										<html:option value=""></html:option>
										<html:option value="Y"><bean:message key="Active" bundle="sysprop" /></html:option>
										<html:option value="C"><bean:message key="Cancel" bundle="sysprop" /></html:option>
									</html:select>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<!--a href="javascript:search('${pageContext.request.contextPath}')">
									<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">>
									</a-->
									<input type="button" value="ค้นหา" class="newPosBtn" onclick="javascript:search('${pageContext.request.contextPath}')">
									<!--a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">
									</a-->
									<input type="button" value="Clear" class="newNegBtn" onclick="javascript:clearForm('${pageContext.request.contextPath}')">
								</td>
							</tr>
						</table>
						<!-- RESULT -->
						<c:if test="${memberForm.results != null}">
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						 <logic:present name="Message_more_max_disp" scope="request">
								${Message_more_max_disp}
						 </logic:present>
						&nbsp;<span class="searchResult">${memberForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/>
						  <logic:present name="Message_more_max_disp2" scope="request">
								${Message_more_max_disp2}
						 </logic:present>
						</div>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No" bundle="sysprop"/></th>
								<th class="code"><bean:message key="Member.Code" bundle="sysele"/></th>
								<th class="name"><bean:message key="Member.Name" bundle="sysele"/>-<bean:message key="Member.Surname" bundle="sysele"/></th>
								<th class="code"><bean:message key="SysConf.MemberType" bundle="sysele"/></th>
								<th class="code"><bean:message key="Other.MemberStatus" bundle="sysele"/></th>
								<th class="status"><bean:message key="Exported" bundle="sysele"/></th>
								<th class="status"><bean:message key="Interfaces" bundle="sysele"/></th>
								<th class="status"><bean:message key="Status" bundle="sysele"/></th>
								<th class="status"><bean:message key="Edit" bundle="sysprop"/></th>
								<th class="status"><bean:message key="View" bundle="sysprop"/></th>
								<th class="status"><bean:message key="Recommend" bundle="sysprop"/></th>
							</tr>	
						<c:forEach var="results" items="${memberForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
								<td width="37px;"><c:out value='${rows.index+1}'/></td>
								<td align="left" width="90px;">${results.code}</td>
								<td align="left" width="133px;">${results.name}&nbsp;${results.name2}</td>
								<td align="center" width="91px;">${results.memberTypeLabel}</td>
								<td align="center" width="91px;">${results.memberLevelLabel}</td>
								<td align="center" width="53px;">
									<c:if test="${results.exported=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:if>
								</td>
								<td align="center" width="53px;">
									<c:if test="${results.interfaces=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
									</c:if>
								</td>
								<td align="center" width="53px;">${results.activeLabel}</td>
								<td align="center">
									<c:if test="${results.isActive=='Y'}">
										<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${results.id}');">
										<img border=0 src="${pageContext.request.contextPath}/icons/user_edit.gif"></a>
									</c:if>
								</td>
								<td align="center" width="52px;">
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								</td>
								<td align="center">
									<a href="#" onclick="javascript:recommented_member('${pageContext.request.contextPath}','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								</td>
							</tr>
						</c:forEach>
							<tr>
								<td align="left" class="footer" colspan="11">&nbsp;</td>
							</tr>
						</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
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