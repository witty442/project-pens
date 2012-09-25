<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%
	String first_search = request.getSession().getAttribute("first_search") != null ? (String)request.getSession().getAttribute("first_search") : "";
%>
<jsp:useBean id="memberRenewForm" class="com.isecinc.pens.web.memberrenew.MemberRenewForm" scope="request" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title></title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/memberrenew.js"></script>
<script type="text/javascript">
function loadMe(path){
	if(<%=memberRenewForm.getMembers() == null && !first_search.equals("Y")%>){
		search(path, 'show');
	}
}

</script>
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" style="height: 100%;" onload="loadMe('${pageContext.request.contextPath}');">
<!-- RESULT -->
<html:form action="/jsp/memberRenewAction">
<jsp:include page="../error.jsp"/>
<c:if test="${memberRenewForm.members != null}">
<fieldset><legend><bean:message key="MembersExpire" bundle="sysele"/></legend>
<br/>
<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
&nbsp;<span class="searchResult">${memberRenewForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop"/></th>
		<th class="code"><bean:message key="Member.Code" bundle="sysele"/></th>
		<th class="th"><bean:message key="Member.Name" bundle="sysele"/>-<bean:message key="Member.Surname" bundle="sysele"/></th>
		<th class="th"><bean:message key="Member.ExpireDate" bundle="sysele"/></th>
		<th class="th"><bean:message key="DaysBeforeExpire" bundle="sysele"/></th>
		<th class="th"><bean:message key="SysConf.MemberType" bundle="sysele"/></th>
		<th class="th"><bean:message key="Other.MemberStatus" bundle="sysele"/></th>
		<th class="status"><bean:message key="Status" bundle="sysele"/></th>
		<th class="status"><bean:message key="View" bundle="sysprop"/></th>
		<th class="status"><bean:message key="Renew" bundle="sysprop"/></th>
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
					<td><font color="red"><c:out value='${rows.index+1}'/></font></td>
					<td align="left"><font color="red">${results.code}</font></td>
					<td align="left"><font color="red">${results.name}&nbsp;${results.name2}</font></td>
					<td align="center"><font color="red">${results.expiredDate}</font></td>
					<td align="center"><font color="red">${results.daysBeforeExpire}</font></td>
					<td align="center"><font color="red">${results.memberTypeLabel}</font></td>
					<td align="center"><font color="red">${results.memberLevelLabel}</font></td>
					<td align="center"><font color="red">${results.activeLabel}</font></td>
				</c:when>
				<c:otherwise>
					<td><c:out value='${rows.index+1}'/></td>
					<td align="left">${results.code}</td>
					<td align="left">${results.name}&nbsp;${results.name2}</td>
					<td align="center">${results.expiredDate}</td>
					<td align="center">${results.daysBeforeExpire}</td>
					<td align="center">${results.memberTypeLabel}</td>
					<td align="center">${results.memberLevelLabel}</td>
					<td align="center">${results.activeLabel}</td>
				</c:otherwise>
			</c:choose>
			<td align="center">
				<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','renew','${results.id}');">
				<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
			</td>
			<td align="center">
				<a href="#" onclick="javascript:open_renew('${pageContext.request.contextPath}','${results.id}','show');">
				<img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
			</td>
		</tr>
	</c:forEach>
	<tr>
		<td align="left" colspan="10" class="footer">&nbsp;</td>
	</tr>
</table>
<br/>
</fieldset>
</c:if>
<br><br>
<input type="hidden" name="type" value="show"/>
<%request.getSession().removeAttribute("first_search"); %>
<jsp:include page="../searchCriteria.jsp"></jsp:include>
</html:form>
<!-- BODY -->
<!-- END -->
</body>
</html>