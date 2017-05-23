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
     <table  border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
		 <tr>
               <td width="30%"></td>
			<td align="left">
			จาก วันที่ขาย&nbsp;&nbsp;&nbsp; 
			<html:text property="transactionSummary.salesDateFrom" styleId="salesDateFrom" readonly="true"/>
			ถึง วันที่ขาย&nbsp;&nbsp;&nbsp; <html:text property="transactionSummary.salesDateTo" styleId="salesDateTo"/>
			</td>
		</tr>
		<tr>
		    <td width="30%"></td>
			<td align="left">รหัสร้านค้า
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="transactionSummary.pensCustCodeFrom" styleId="pensCustCodeFrom" size="20" onkeypress="getCustNameKeypress(event,this,'pensCustNameFrom')"/>-
			    <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','lotus')"/>
			    <html:text property="transactionSummary.pensCustNameFrom" styleId="pensCustNameFrom" readonly="true" styleClass="disableText" size="40"/>
			</td>
		</tr>
		<tr>
		    <td width="30%"></td>
			<td align="left">ชื่อไฟล์ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;
			    <html:text property="transactionSummary.fileName" styleId="fileName"/>
			 </td>
		</tr>
	</table>