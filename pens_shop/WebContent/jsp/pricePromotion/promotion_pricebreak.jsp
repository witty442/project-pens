<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
String lineId = request.getParameter("lineId");
MModifierLine mLine = new MModifierLine();
ModifierLine line = mLine.find(lineId);
//System.out.println(line);
pageContext.setAttribute("lines",line,PageContext.PAGE_SCOPE);
pageContext.setAttribute("linesRelate",line.getRelatedModifier(),PageContext.PAGE_SCOPE);

%>

<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.model.MModifierLine"%>
<%@page import="com.isecinc.pens.bean.ModifierLine"%><head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />

</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<br>
<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
	<tr>
		<td width="50%" align="right"><bean:message key="Modifier.BreakLineType" bundle="sysele"/>&nbsp;:&nbsp;</td>
		<td align="left"><b>≈¥√“§“</b></td>
	</tr>
</table>
<br/>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearchNoWidth">
	<tr>
		<th class="td_text_center"><bean:message key="No" bundle="sysprop"/></th>
		<th class="td_text_center" width="10%">Modifier Line Id</th>
		<th class="td_text_center"><bean:message key="Modifier.ValFrom" bundle="sysele"/></th>
		<th class="td_text_center"><bean:message key="Modifier.ValTo" bundle="sysele"/></th>
		<th><bean:message key="Modifier.AppMethod" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Modifier.Vals" bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${linesRelate}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE"/>
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td  class="td_text_center" width="10%"><c:out value='${rows.index + 1}'/></td>
			<td  class="td_text_center" width="10%">${results.id}</td>
			<td  class="td_number" width="10%"><fmt:formatNumber pattern="#,##0.00" value="${results.attr.valueFrom}"/></td>
			<td  class="td_number" width="10%">
				<c:if test="${results.attr.valueTo<999999}">
					<fmt:formatNumber pattern="#,##0.00" value="${results.attr.valueTo}"/>
				</c:if>
			</td>
			<td  class="td_text_center" width="10%">${results.applicationMethod}</td>
			<td  class="td_number" width="10%"><fmt:formatNumber pattern="#,##0.00" value="${results.values}"/></td>
		</tr>
	</c:forEach>	
	<tr>
		<td align="left" colspan="6" class="footer">&nbsp;</td>
	</tr>
</table>
<br>
<table align="center" border="0" cellpadding="3" cellspacing="1" width="100%">
	<tr>
		<td width="80%" align="right">
			<a href="#" onclick="window.close();">
			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"></a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</body>
