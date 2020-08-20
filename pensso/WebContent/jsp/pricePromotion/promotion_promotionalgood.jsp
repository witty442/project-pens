<%@page import="com.pens.util.SIdUtils"%>
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
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css">
</head>
<body topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<br>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th><bean:message key="Modifier.AttrItem" bundle="sysele"/></th>
		<th><bean:message key="Modifier.AttrItemVal" bundle="sysele"/></th>
		<th class="code"><bean:message key="Modifier.BenefitUOM" bundle="sysele"/></th>
		<th class="costprice"><bean:message key="Modifier.BenefitQty" bundle="sysele"/></th>
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
			<td align="center">${results.attr.productAttribute}</td>
			<td align="left">${results.attr.attributeValueLabel}</td>
			<td align="center">${results.benefitUOM.name}</td>
			<td align="right"><fmt:formatNumber pattern="#,##0.0" value="${results.benefitQty}"/></td>
		</tr>
	</c:forEach>	
	<tr>
		<td align="left" colspan="4" class="footer">&nbsp;</td>
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
