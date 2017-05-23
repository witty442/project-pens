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
		<td align="right">จาก วันที่ขาย <font color="red">*</font> &nbsp;
		<html:text property="onhandSummary.asOfDateFrom" styleId="asOfDateFrom" readonly="true"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="left" width="30%">ถึง วันที่ขาย <font color="red">*</font> 
		<html:text property="onhandSummary.asOfDateTo" styleId="asOfDateTo" readonly="true"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	</tr>
      <tr>
		<td align="right">รหัสร้านค้า<font color="red">*</font>
		    <html:text property="onhandSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
		    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','lotus')"/>
		</td>
		<td align="left" width="30%"> <html:text property="onhandSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="30"/></td>
	</tr>
	<tr>
		<td align="right" width="30%">
		     Pens Item From<html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom"/>
		     &nbsp;
		    <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>
		</td>
		<td align="left" width="30%">
		     Pens Item To&nbsp;&nbsp; <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo"/>
		     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>   
		</td>
	</tr>
	<tr>
		<td align="right" width="30%">Group &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    <html:text property="onhandSummary.group" styleId="group" />
		    &nbsp;
		    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
		     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
		  </td>
		<td align="left" width="30%">&nbsp;</td>
   </tr>
 </table>
