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
		<td align="left">จาก วันที่นับสต็อก&nbsp; <html:text property="physicalSummary.countDateFrom" styleId="countDateFrom" readonly="true"/></td>
		<td align="left">ถึง วันที่นับสต็อก&nbsp; <html:text property="physicalSummary.countDateTo" styleId="countDateTo"/></td>
	</tr>
	<tr>
		<td align="left">จาก รหัสร้านค้า&nbsp;&nbsp; &nbsp; 
		    <html:text property="physicalSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="10" onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'pensCustNameFrom')"/>-
		    <html:text property="physicalSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText"/>
		    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
		</td>
		<td align="left">ถึง รหัสร้านค้า&nbsp;&nbsp;&nbsp;&nbsp; 
		    <html:text property="physicalSummary.pensCustCodeTo" styleId="pensCustCodeTo" size="10" onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'pensCustNameTo')"/>-
		    <html:text property="physicalSummary.pensCustNameTo" styleId="pensCustNameTo" readonly="true" styleClass="disableText"/>
		    <input type="button" name="x2" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','to','')"/>
		</td>
	</tr>
	<tr>
		<td align="left">ชื่อไฟล์ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp; &nbsp;&nbsp;    
		 <html:text property="physicalSummary.fileName" styleId="fileName"/></td>
		<td align="left"></td>
	</tr>
</table>