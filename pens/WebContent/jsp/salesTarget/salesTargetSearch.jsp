<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%
List<String> years = new MSalesTarget().getYears();
pageContext.setAttribute("years",years,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="salestargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="request" />

<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.model.MSalesTarget"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salestarget.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" style="height: 100%;">
<table id="maintab" align="center" border="0" cellpadding="0" cellspacing="0" style="buttom:0;" class="body">
	<tr>
		<td bgcolor="#FFFFFF"><jsp:include page="../header.jsp"/></td>
	</tr>
	<tr id="framerow">
		<td valign="top" align="center">
			<!-- INCLUDE -->
			<jsp:include page="../menu.jsp"/>
			<jsp:include page="../program.jsp">
				<jsp:param name="system" value="MasterData"/>
				<jsp:param name="function" value="SalesTarget"/>
				<jsp:param name="code" value=""/>
			</jsp:include>
			<!-- BODY -->
			<html:form action="/jsp/salestargetAction">
			<jsp:include page="../error.jsp"/>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="right" width="45%"><bean:message key="Year" bundle="sysele"/>&nbsp;&nbsp;</td>
					<td align="left">
						<html:select property="salesTarget.year">
							<%for(String s : years) {%>
							<html:option value="<%=s %>"><%=s%></html:option>
							<%} %>
						</html:select>
					</td>
				</tr>
			</table>
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="center">
						<a href="javascript:search('${pageContext.request.contextPath}')">
<!--						<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
						<input type="button" value="ค้นหา" class="newPosBtn">
						</a>
						<a href="javascript:clearForm('${pageContext.request.contextPath}')">
<!--						<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
						<input type="button" value="Clear" class="newNegBtn">
						</a>
					</td>
				</tr>
			</table>
			<!-- RESULT -->
			<br/>
			<c:if test="${salestargetForm.results != null}">
			<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
			&nbsp;<span class="searchResult">${salestargetForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
			<table align="center" border="0" cellpadding="3" cellspacing="1"
					class="result" width="30%">
				<tr>
					<th class="order"><bean:message key="No" bundle="sysprop"/></th>
					<th class="name"><bean:message key="Month" bundle="sysele"/></th>
					<th class="name"><bean:message key="MonthTarget" bundle="sysele"/>(<bean:message key="Product.UOM"  bundle="sysele"/>)</th>
					<th class="status"><bean:message key="View" bundle="sysprop"/></th>
				</tr>
				<c:forEach var="results" items="${salestargetForm.results}" varStatus="rows">
				<c:choose>
					<c:when test="${rows.index %2 == 0}">
						<c:set var="tabclass" value="lineO"/>
					</c:when>
					<c:otherwise>
						<c:set var="tabclass" value="lineE"/>
					</c:otherwise>
				</c:choose>
				<tr class="<c:out value='${tabclass}'/>">
					<td><c:out value='${rows.index+1}'/></td>
					<c:if test="${results.month == 'JAN'}">
						<td align="center"><bean:message key="JAN" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'FEB'}">
						<td align="center"><bean:message key="FEB" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'MAR'}">
						<td align="center"><bean:message key="MAR" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'APR'}">
						<td align="center"><bean:message key="APR" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'MAY'}">
						<td align="center"><bean:message key="MAY" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'JUN'}">
						<td align="center"><bean:message key="JUN" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'JUL'}">
						<td align="center"><bean:message key="JUL" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'AUG'}">
						<td align="center"><bean:message key="AUG" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'SEP'}">
						<td align="center"><bean:message key="SEP" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'OCT'}">
						<td align="center"><bean:message key="OCT" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'NOV'}">
						<td align="center"><bean:message key="NOV" bundle="sysele"/></td>
					</c:if>
					<c:if test="${results.month == 'DEC'}">
						<td align="center"><bean:message key="DEC" bundle="sysele"/></td>
					</c:if>
					<td align="right">
						<fmt:formatNumber pattern="#,##0.00" value="${results.targetAmount}"/>
					</td>	
					<td align="center">
						<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','${results.id}');">
						<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
					</td>
				</tr>
				</c:forEach>
			</table>
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">	
				<tr>
					<td align="left" colspan="10" class="footer">&nbsp;</td>
				</tr>
			</table>
			</c:if>
			<br>
			<!-- BUTTON -->
			<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
				<tr>
					<td align="right">			

						<input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
					</td>
					<td width="20%">&nbsp;</td>
				</tr>
			</table>
			<br>
			<jsp:include page="../searchCriteria.jsp"></jsp:include>
			</html:form>
			<!-- BODY -->
			<!-- END -->
		</td>
	</tr>
	<tr>
		<td bgcolor="#FFFFFF"><jsp:include page="../footer.jsp"/></td>
	</tr>
</table>
</body>
</html>