<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />

 <c:if test="${interfacesForm.monitorItemList != null}">
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<th width="10%">No</th>
		<th width="10%">FileName</th>
		<th width="10%">Status</th>
		<th width="10%">Total</th>
		<th width="10%">Success</th>
		<th width="10%">Fail</th>
		<th width="50%">Message</th>
	</tr>
	<c:forEach var="results" items="${interfacesForm.monitorItemList}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO" />
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE" />
			</c:otherwise>
		</c:choose>
		 <c:choose>
			<c:when test="${results.status == -1}">
				<c:set var="tabclass" value="errorLine"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineO"/>
			</c:otherwise>
		    </c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td><span class="<c:out value='${tabclass}'/>">${results.row}</span></td>
			<td><span class="<c:out value='${tabclass}'/>">${results.fileName}</span></td>
			<td align="center"><span class="<c:out value='${tabclass}'/>">${results.statusDesc}</span></td>
			<td><span class="<c:out value='${tabclass}'/>">${results.dataCount}</span></td>
			<td><span class="<c:out value='${tabclass}'/>">${results.successCount}</span></td>
			<td><span class="<c:out value='${tabclass}'/>">${results.failCount}</span></td>
			<td align="left"><span class="<c:out value='${tabclass}'/>">${results.errorMsg}</span></td>
			
		</tr>
	</c:forEach>
	</table>
</c:if>

					
								