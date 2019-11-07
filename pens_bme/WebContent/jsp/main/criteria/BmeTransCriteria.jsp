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
			<td align="right" width="15%">จาก วันที่ <font color="red">*</font>
			</td>
			<td align="left" width="50%"><html:text property="onhandSummary.asOfDateFrom" styleId="asOfDateFrom" readonly="true"/>
			ถึง วันที่&nbsp;&nbsp;&nbsp; <html:text property="onhandSummary.asOfDateTo" styleId="asOfDateTo"/></td>
		</tr>
       <tr>
			<td align="right" width="15%">รหัสร้านค้า<font color="red">*</font>
			</td>
			<td align="left" width="50%"> 
			 <html:text property="onhandSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" styleClass="\" autoComplete=\"off"
			 size="20" onkeypress="getCustNameKeypress('${pageContext.request.contextPath}',event,this,'pensCustNameFrom')"/>-
			    <input type="button" name="x1" value="..." onclick="openPopupCustomerAll('${pageContext.request.contextPath}','from','<%=storeType%>')"/>
			<html:text property="onhandSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="50"/></td>
		</tr>
		<tr>
			<td align="right" width="15%">
			     Pens Item From 
			</td>
			<td align="left" width="50%">
			    <html:text property="onhandSummary.pensItemFrom" styleId="pensItemFrom"/>
			     &nbsp;
			    <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','from')"/>
			     Pens Item To&nbsp;&nbsp; <html:text property="onhandSummary.pensItemTo" styleId="pensItemTo"/>
			     <input type="button" name="x1" value="..." onclick="openPopupProduct('${pageContext.request.contextPath}','to')"/>   
			</td>
		</tr>
		<tr>
			<td align="right" width="15%">Group 
			  </td>
			<td align="left" width="50%">   
			   <html:text property="onhandSummary.group" styleId="group" />
			    &nbsp;
			    <input type="button" name="x1" value="..." onclick="openPopupGroup('${pageContext.request.contextPath}')"/>
			     <html:hidden property="onhandSummary.groupDesc" styleId="groupDesc" />
		   </td>
	   </tr>
	 </table>
