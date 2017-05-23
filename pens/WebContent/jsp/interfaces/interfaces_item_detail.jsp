<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>

</head>
<body>
	<!-- BODY -->
	<div align="left" class="recordfound">&nbsp;&nbsp&nbsp;ข้อมูล Export</div>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" width="600">
		<tr>
			<th width="46">ลำดับ</th>
			<th width="154">รหัสลูกค้า</th>
			<th width="158">รายชื่อลูกค้า</th>
			<th width="95">
				<c:choose>
					<c:when test="${interfacesForm.monitorBean.monitorItemBean.tableName == 't_order'}">
						ORDER NO
					</c:when>
					<c:when test="${interfacesForm.monitorBean.monitorItemBean.tableName == 't_receipt'}">
						RECEIPT NO
					</c:when>
					<c:when test="${interfacesForm.monitorBean.monitorItemBean.tableName == 't_visit'}">
						VISIT CODE
					</c:when>
					<c:otherwise>
					    
					</c:otherwise>
				</c:choose>
			</th>
			<th width="111">Amount</th>
		</tr>
		<c:forEach var="results" items="${interfacesForm.resultsItemDetail}" varStatus="rows">
			<c:choose>
				<c:when test="${rows.index %2 == 0}">
					<c:set var="tabclass" value="lineO"/>
				</c:when>
				<c:otherwise>
					<c:set var="tabclass" value="lineE"/>
				</c:otherwise>
			</c:choose>
			
			<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
                <td> <c:out value='${rows.index+1}'/></td>
				<td align="center"> ${results.customerCode}</td>
				<td align="left"> ${results.customerName}</td>
				<td align="center"> ${results.code}</td>
				<td align="right"> ${results.amountStr}</td>
		   </tr>
              </c:forEach>
	</table>
	<table align="center" border="0" cellpadding="3" cellspacing="0" class="result">	
		<tr>
			<td align="left" colspan="10" class="footer">&nbsp;</td>
		</tr>
	</table>
	<br>
	<!-- BUTTON -->
	<table align="center" border="0" cellpadding="3" cellspacing="0" width="600">
		<tr>
			<td align="right">
				<a href="#" onclick="window.close()"><img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"></a>
			</td>
			<td width="20%">&nbsp;</td>
		</tr>
	</table>
	<br><br>
	<!-- BODY -->
			
</body>
</html>