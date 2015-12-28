<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />

 <c:if test="${interfacesForm.results != null}">
	<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
	&nbsp;<span class="searchResult">${interfacesForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
	
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
        <th> No.</th>
        <th> �Ţ�����¡��</th>
		<th> ������</th>
		<th> ������������</th>
		<th> ������ҧ</th>
		<th> ʶҹ�</th>
		<th> �ѹ������¡������ش</th>
		<th> 
		   <c:choose>
			 <c:when test="${interfacesForm.criteria.monitorBean.type == 'IMPORT'}">
					�ӹǹ�������� / �ӹǹ��������
			 </c:when>
			 <c:otherwise>
					�ӹǹ��� 
			  </c:otherwise>
		   </c:choose>
		</th>
<!--				<th> Remark</th>-->
		<th> Message</th>
		<th> ��������´</th>
		</tr>
		<c:forEach var="results" items="${interfacesForm.results}" varStatus="rows">
		
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
				<td>
				<c:choose>
					<c:when test="${results.transactionType == 'MASTER'}">
						�����ž�鹰ҹ
					</c:when>
					<c:when test="${results.transactionType == 'TRANSACTION'}">
						������ Transaction
					</c:when>
					<c:when test="${results.transactionType == 'WEB-MEMBER'}">
						������ WEB MEMBER
					</c:when>
					<c:when test="${results.transactionType == 'BME'}">
						BME
					</c:when>
					<c:otherwise>
						Update Transaction Sales
					</c:otherwise>
				</c:choose>
				</td>
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
				<td> ${results.submitDate}</td>
				<td>
				<c:choose>
						<c:when test="${results.type == 'IMPORT'}">
							${results.successCount}/ ${results.fileCount}
						</c:when>
						<c:otherwise>
							${results.fileCount}
						</c:otherwise>
					</c:choose>
				</td>
				<td align="left"> ${results.errorMsg}</td>
				<td> 
				     <a href="#" onclick="javascript:searchDetail('${pageContext.request.contextPath}','admin','${results.monitorId}');">
			          <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
				</td>
		</tr>
		</c:forEach>
	</table>
</c:if>

 
					
								