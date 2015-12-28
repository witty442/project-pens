<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />

 <c:if test="${interfacesForm.monitorItemBeanResult.failCount > 0}">
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	 <tr>
		<th colspan="3"  align="left"><font color="red">จำนวนรายการที่ไม่่สามารถ Generate ได้   ${interfacesForm.monitorItemBeanResult.failCount} รายการ </font> </th>
	</tr> 
	<tr>
		<th width="10%">No</th>
		<th width="50%">Message</th>
		<th width="10%">Status</th>
	</tr>
	<c:forEach var="results" items="${interfacesForm.monitorItemBeanResult.failList}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO" />
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE" />
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td>${results.row}</td>
			<td align="left">${results.msg}</td>
			<td align="left">${results.status}</td>
		</tr>
	</c:forEach>
	</table>
</c:if>

 <c:if test="${interfacesForm.monitorItemBeanResult.successCount > 0}">
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	 <tr>
		<th colspan="3"  align="left">จำนวนรายการที่สามารถ Generate ได้   ${interfacesForm.monitorItemBeanResult.successCount} รายการ</th>
	</tr> 
	<tr>
		<th width="10%">No</th>
		<th width="50%">Message</th>
		<th width="10%">Status</th>
	</tr>
	<c:forEach var="results" items="${interfacesForm.monitorItemBeanResult.successList}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO" />
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE" />
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td>${results.row}</td>
			<td align="left">${results.msg}</td>
			<td align="left">${results.status}</td>
		</tr>
	</c:forEach>
	</table>
</c:if>
					
								