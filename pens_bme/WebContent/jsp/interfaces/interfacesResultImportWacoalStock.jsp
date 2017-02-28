<%@page import="com.isecinc.pens.inf.bean.MonitorItemResultBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />


 <c:if test="${interfacesForm.monitorItemList == null}">
 <c:if test="${interfacesForm.results != null}">
	<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>
	&nbsp;<span class="searchResult">${interfacesForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records"  bundle="sysprop"/></div>
	
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
        <th> No.</th>
        <th> Task ID</th>
		<th> ประเภท</th>
		<th> ประเภทข้อมูล</th>
		<th> ผู้สร้าง</th>
		<th> สถานะ</th>
		<th> วันที่ทำรายการล่าสุด</th>
		<th> จำนวนไฟล์</th>
		<th> Message</th>
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
				<td>Import Wacoal Stock</td>
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
				
		</tr>
		</c:forEach>
	</table>
</c:if>
 </c:if>
 
 <c:if test="${interfacesForm.monitorItemList != null}">
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	    <th width="5%">Task Id</th>
	    <th width="5%">Monitor ID</th>
		<th width="5%">No</th>
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
		  <td><span class="<c:out value='${tabclass}'/>">${results.transactionId}</span></td>
		    <td><span class="<c:out value='${tabclass}'/>">${results.monitorId}</span></td>
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

 <c:if test="${interfacesForm.monitorItemBeanResult.successCount > 0}">
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	 <tr>
		<th colspan="8"  align="left">จำนวนรายการที่สามารถ Import ได้   ${interfacesForm.monitorItemBeanResult.successCount} รายการ</th>
	</tr> 
	<tr>
		<th width="5%">Line in Excel</th>
		<th width="5%">Branch Id</th>
		<th width="25%">Branch name</th>
		<th width="8%">Check Date</th>
		<th width="8%">Item</th>
		<th width="8%">Price No Vat</th>
		<th width="5%">Check Qty</th>
		<th width="10%">File Name</th>
		
	</tr>
	<%
	//chkstk_200170125.xls|399|6000P011|บริษัท เอก-ชัย ดีสทริบิวชั่น ซิสเทม จำกัด(สาขาที่ 01747)(THE WALK นครสวรรค์)|16/01/2017|WU1982|121.50|23.00
	 List<MonitorItemResultBean> successList = interfacesForm.getMonitorItemBeanResult().getSuccessList();
	 String[] lineArr = null;
	 for(int i=0;i<successList.size();i++){
		 MonitorItemResultBean item = successList.get(i);
		 lineArr = item.getMsg().split("\\|");
	%>
		<c:choose>
			<c:when test="${rows.index %2 == 0}">
				<c:set var="tabclass" value="lineO" />
			</c:when>
			<c:otherwise>
				<c:set var="tabclass" value="lineE" />
			</c:otherwise>
		</c:choose>
		<tr class="<c:out value='${tabclass}'/>">
			<td><%=lineArr[1] %></td>
			<td><%=lineArr[2] %></td>
			<td><%=lineArr[3] %></td>
			<td><%=lineArr[4] %></td>
			<td><%=lineArr[5] %></td>
			<td><%=lineArr[6] %></td>
			<td><%=lineArr[7] %></td>
			<td><%=lineArr[0] %></td>
		</tr>
		<%} %>
		
	</table>
</c:if> 
					

					
								