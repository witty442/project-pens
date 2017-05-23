<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.isecinc.pens.SystemProperties"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css">
<script language = "javascript" src = "${pageContext.request.contextPath}/js/javascript.js"></script>
</head>
<body bottommargin="0" leftmargin="0" topmargin="0" rightmargin="0">
<br/>
<table align="center" border="0" cellpadding="3" cellspacing="0" class="tabDetail">
	<tr class="tabHeader">
		<td colspan="4">Product Movement Information (In)</td>
	</tr>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
	<tr>
		<td align="right"></td>
		<td align="left"></td>
		<td align="right"><bean:message key="Date" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" maxlength="10" size="15"/>
			<img border=0 src="${pageContext.request.contextPath}/icons/cld.gif" align="absmiddle">
		</td>	
	</tr>
	<tr>
		<td align="right"><bean:message key="Product.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left">
			<input type="text" readonly="readonly" class="disableText">
			<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle">	
		</td>
		<td align="right"><bean:message key="Product.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td align="left"><input type="text" readonly="readonly" class="disableText"></td>
	</tr>
	<tr>
		<td align="right"><bean:message key="NumStocksInVan" bundle="sysele"/></td>
		<td align="left"><input type="text" readonly="readonly" class="disableText"></td>
		<td align="right">&nbsp;&nbsp;</td>
		<td align="left"></td>
	</tr>
	<tr>
		<td width="30%" align="right"><bean:message key="WarehouseMovementFrom" bundle="sysele"/>&nbsp;&nbsp;</td>
		<td width="20%" align="left">
			<select style="width: 150px; height: 22px;">
				<option>ทั้งหมด</option>
			</select>
		</td>
		<td width="10%" align="right">&nbsp;</td>
		<td align="left">&nbsp;</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="Quantity" bundle="sysele"/></td>
		<td align="left" colspan="3"><input type="text"></td>
	</tr>
</table>
<br/>
<div id="button" style="width: 100%;">
<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
	<tr>
		<td align="right">
			<input type="button" value="Save" class="newPosBtn">
			<input type="button" value="Cancel" class="newNegBtn" onclick="window.location='productMovementSearch.jsp'">
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</div>
</body>
</html>