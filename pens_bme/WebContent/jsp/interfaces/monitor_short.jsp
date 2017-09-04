<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />

 <c:if test="${interfacesForm.results != null}">
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
    <tr>
      <th width="10%">Trans ID</th>
      <th width="20%">Task Name</th>
      <th width="20%">Submit Date</th>
      <th width="10%">ʶҹ� </th>
      <th width="40%">Message </th>
    </tr>
	<c:forEach var="results" items="${interfacesForm.results}" varStatus="rows">
	        <c:choose>
			<c:when test="${results.status == -1}">
				<c:set var="tabclass" value="errorLine"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineO"/>
			</c:otherwise>
		    </c:choose>
			<tr>
			    <td><span class="<c:out value='${tabclass}'/>">${results.transactionId}</span></td>
				<td><span class="<c:out value='${tabclass}'/>"> ${results.name}</span></td> 
				<td><span class="<c:out value='${tabclass}'/>"> <fmt:formatDate value="${results.submitDate}" pattern="dd-MM-yyyy HH:ss"></fmt:formatDate ></span></td>
				<td><span class="<c:out value='${tabclass}'/>">${results.statusDesc}</span></td>
				<td align="left"><span class="<c:out value='${tabclass}'/>">${results.errorMsg}</span></td>
			</tr>
	</c:forEach>
	</table>
</c:if>

 
					
								