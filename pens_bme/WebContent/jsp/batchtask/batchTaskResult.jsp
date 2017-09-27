<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="batchTaskForm" class="com.isecinc.pens.web.batchtask.BatchTaskForm" scope="session" />

<c:if test="${batchTaskForm.results != null}">
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
        <th> No.</th>
        <th> เลขที่รายการ</th>
		<th> ประเภท</th>
		<th> ประเภทข้อมูล</th>
		<th> ผู้สร้าง</th>
		<th> สถานะ</th>
		<th> วันที่ทำรายการล่าสุด</th>
		<th> Message</th>
		<th> รายละเอียด</th>
		</tr>
		<c:forEach var="results" items="${batchTaskForm.results}" varStatus="rows">
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO"/>
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE"/>
			</c:otherwise>
		</c:choose>
		
		<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
		       
                <td> <c:out value='${rows.index+1}'/></td>
                <td> ${results.transactionId}</td>
				<td> ${results.type}</td>
				<td> ${results.transactionType}</td>
				<td> ${results.createUser}</td>
				<td> 
				  <c:choose>
					<c:when test="${results.status == 1}">
						<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
					</c:when>
					<c:otherwise>
						<img border=0 src="${pageContext.request.contextPath}/icons/uncheck.gif">
					</c:otherwise>
				   </c:choose>
				</td>
				<td> ${results.submitDateDisp}</td>
				<td align="left"> ${results.errorMsg}</td>
			    <td></td>
		</tr>
		</c:forEach>
	</table>
</c:if>

<%
 String pageName = request.getParameter("pageName");
 //System.out.println("pageName:"+pageName);
%>

<%if("ImportOrderFromExcel".equalsIgnoreCase(pageName)){ %>
  <jsp:include page="sub/ImportOrderFromExcel_sub.jsp"></jsp:include>
<%} %>

 
					
								