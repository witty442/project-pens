<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<tr><td style="height: 5px;"></td></tr>
<tr><td>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th class="order"><bean:message key="No" bundle="sysprop"/></th>
		<th class="code">Modifier No.</th>
		<th>Level</th>
		<th>Modifier Type</th>
		<th>Volume Type</th>
		<th class="status">Operators</th>
		<th class="status">Value From</th>
		<th class="status">Value To</th>
		<th class="status">แสดง</th>
	</tr>
	<tr class="lineO">
		<td>1</td>
		<td align="left">889875</td>
		<td align="left">Line</td>
		<td align="left">Discount</td>
		<td align="left"></td>
		<td align="left"></td>
		<td align="left">1</td>
		<td align="left">10</td>
		<td align="center">
			<a href="javascript:settype(2);">
			<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
		</td>
	</tr>
	<tr class="lineE">
		<td>2</td>
		<td align="left">009887</td>
		<td align="left">Line</td>
		<td align="left">Price Break</td>
		<td align="left"></td>
		<td align="left"></td>
		<td align="left">1</td>
		<td align="left">12</td>
		<td align="center">
			<a href="javascript:settype(3);">
			<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
		</td>
	</tr>
	<tr class="lineO">
		<td>3</td>
		<td align="left">112456</td>
		<td align="left">Line</td>
		<td align="left">Promotional Good</td>
		<td align="left"></td>
		<td align="left"></td>
		<td align="left">1</td>
		<td align="left">6</td>
		<td align="center">
			<a href="javascript:javascript:settype(4);">
			<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
		</td>
	</tr>
	<tr>
		<td align="left" colspan="14" class="footer">&nbsp;</td>
	</tr>
</table>

