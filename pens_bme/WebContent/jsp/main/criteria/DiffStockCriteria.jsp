<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%> 
<%
String storeType = "";
String hideAll = "";
%>
<table  border="0" cellpadding="3" cellspacing="0" class="body" width="65%">
	<tr>
		<td align="right" width="25%">รหัสร้านค้า</td>
		<td align="left" width="40%">
		    <html:text property="diffStockSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="10" onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'pensCustNameFrom')"/>-
		    <html:text property="diffStockSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText"/>
		    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
		</td>
	</tr>
	<tr>
		<td align="right" width="25%">As Of Date</td>
		<td align="left" width="40%"><html:text property="diffStockSummary.asOfDate" styleId="asOfDate" readonly="true"/></td>
	</tr>
	<tr>
		<td align="right" width="25%">Only have qty</td>
		<td align="left" width="40%"><html:checkbox property="diffStockSummary.haveQty" /></td>
	</tr>
</table>