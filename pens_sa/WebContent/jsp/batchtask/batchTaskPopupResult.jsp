<%@page import="com.isecinc.pens.web.batchtask.BatchTaskForm"%>
<%@page import="com.isecinc.pens.bean.MonitorBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
if(session.getAttribute("BATCH_TASK_RESULT") != null){
 BatchTaskForm batchTaskForm = (BatchTaskForm)session.getAttribute("BATCH_TASK_RESULT");
 System.out.println("Page BatchResults: batchTaskForm["+batchTaskForm+"]results lenth["+batchTaskForm.getResults().length+"]");
%>

<%if(batchTaskForm.getResults() != null && batchTaskForm.getResults().length >0){ %>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
		<tr>
        <th> No.</th>
        <th> เลขที่รายการ</th>
		<th> ชื่อ Process</th>
		<th> ประเภทข้อมูล</th>
		<th> ผู้สร้าง</th>
		<th> สถานะ</th>
		<th> วันที่ทำรายการล่าสุด</th>
		<th> Message</th>
		<th> รายละเอียด</th>
		</tr>
		<%
		 for(int i=0;i<batchTaskForm.getResults().length;i++){
			 MonitorBean item = batchTaskForm.getResults()[i];
		%>
		<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
		       
                <td> <%=(i+1)%></td>
                <td> <%=item.getTransactionId() %></td>
				<td> <%=item.getName() %></td>
				<td> <%=item.getTransactionType() %></td>
				<td> <%=item.getCreateUser() %></td>
				<td> 
				   <%if(item.getStatus()==1){ %>
				       <img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
				   <%}else{ %>
				       <img border=0 src="${pageContext.request.contextPath}/icons/uncheck.gif">
				   <%} %>
				</td>
				<td> <%=item.getSubmitDateDisp()%></td>
				<td align="left"><%=item.getErrorMsg()%> </td>
			    <td></td>
		</tr>
		<%} %>	
	</table>
<%} %>	

   <!-- Result Batch Task -->
    <jsp:include page="sub/AllResultBatchTaskPopup_sub.jsp"></jsp:include>
 
<%} %>	
								