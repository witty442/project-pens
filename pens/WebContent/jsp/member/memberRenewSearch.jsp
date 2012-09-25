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

%>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/memberrenew.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('expiredDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('expiredDateTo'));
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
				<jsp:param name="function" value="Renew"/>
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
						<html:form action="/jsp/memberRenewAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td colspan="4" align="left">&nbsp;</td>
							</tr>
							<tr>
								<td align="right" width="33%"><bean:message key="SysConf.MemberAgingPeriod"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" width="20%">
									<html:text property="memberRenew.alertPeriod" size="10"/>
								</td>
								<td width="10%" align="right"><bean:message key="Member.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="memberRenew.member.code" size="15"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Member.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="memberRenew.member.name"/></td>
								<td align="right"><bean:message key="Member.Surname" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="memberRenew.member.name2"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="ExpireDateFrom" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="memberRenew.expiredDateFrom" styleId="expiredDateFrom" size="15" readonly="true"/></td>
								<td align="right"><bean:message key="To" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="memberRenew.expiredDateTo" styleId="expiredDateTo" size="15" readonly="true"/></td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="javascript:search('${pageContext.request.contextPath}','search')">
									<!--<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ค้นหา" class="newPosBtn">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<!--<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<!-- RESULT -->
						<c:if test="${memberRenewForm.members != null}">
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
						&nbsp;<span class="searchResult">${memberRenewForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No" bundle="sysprop"/></th>
								<th class="code"><bean:message key="Member.Code" bundle="sysele"/></th>
								<th class="name"><bean:message key="Member.Name" bundle="sysele"/> <bean:message key="Member.Surname" bundle="sysele"/></th>
								<th class="name"><bean:message key="Member.ExpireDate" bundle="sysele"/></th>
								<th class="status"><bean:message key="DaysBeforeExpire" bundle="sysele"/></th>
								<th class="code"><bean:message key="SysConf.MemberType" bundle="sysele"/></th>
								<th class="code"><bean:message key="Other.MemberStatus" bundle="sysele"/></th>
								<th class="status"><bean:message key="Status" bundle="sysele"/></th>
								<th class="status"><bean:message key="View" bundle="sysprop"/></th>
								<th class="status"><bean:message key="Renew" bundle="sysprop"/></th>
								<th class="status"><bean:message key="MemberFollow" bundle="sysprop"/></th>
							</tr>	
						<c:forEach var="results" items="${memberRenewForm.members}" varStatus="rows">
							<c:choose>
								<c:when test="${rows.index %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
								<c:choose>
									<c:when test="${results.expired==true}">
										<td width="36px;"><font color="red"><c:out value='${rows.index+1}'/></font></td>
										<td align="left" width="81px;"><font color="red">${results.code}</font></td>
										<td align="left" width="111px;"><font color="red">${results.name}&nbsp;${results.name2}</font></td>
										<td align="center" width="115px;"><font color="red">${results.expiredDate}</font></td>
										<td align="center" width="52px;"><font color="red">${results.daysBeforeExpire}</font></td>
										<td align="center" width="82px;"><font color="red">${results.memberTypeLabel}</font></td>
										<td align="center" width="82px;"><font color="red">${results.memberLevelLabel}</font></td>
										<td align="center" width="51px;"><font color="red">${results.activeLabel}</font></td>
									</c:when>
									<c:otherwise>
										<td width="36px;"><c:out value='${rows.index+1}'/></td>
										<td align="left" width="81px;">${results.code}</td>
										<td align="left" width="111px;">${results.name}&nbsp;${results.name2}</td>
										<td align="center" width="115px;">${results.expiredDate}</td>
										<td align="center" width="52px;">${results.daysBeforeExpire}</td>
										<td align="center" width="82px;">${results.memberTypeLabel}</td>
										<td align="center" width="82px;">${results.memberLevelLabel}</td>
										<td align="center" width="51px;">${results.activeLabel}</td>
									</c:otherwise>
								</c:choose>
								<td align="center" width="48px;">
									<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','renew','${results.id}','search');">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								</td>
								<td align="center" width="53px;">
									<a href="#" onclick="javascript:open_renew('${pageContext.request.contextPath}','${results.id}', 'search');">
									<img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
								</td>
								<td align="center">
									<a href="#" onclick="javascript:open_renew_follow('${pageContext.request.contextPath}','${results.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
								</td>
							</tr>
						</c:forEach>
							<tr>
								<td align="left" colspan="11" class="footer">&nbsp;</td>
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
						<input type="hidden" name="type" value="search"/>
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