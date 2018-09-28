<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.inf.bean.MonitorItemBean"%>
<%@page import="com.isecinc.pens.inf.bean.MonitorItemResultBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="batchTaskForm" class="com.isecinc.pens.web.batchtask.BatchTaskForm" scope="session" />

<!-- Detail -->
<%
System.out.println("MonitorItem:"+batchTaskForm.getMonitorItem());
if(batchTaskForm.getMonitorItem() != null){
	 List<MonitorItemResultBean> successList = batchTaskForm.getMonitorItem().getSuccessList();
	 List<MonitorItemResultBean> failList = batchTaskForm.getMonitorItem().getFailList();
%>

	<%if(failList != null && failList.size() >0){%>
	<p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	  <th colspan="10"><font color="#921F06">จำนวน Row ที่ไม่สามารถ Import ได้   ${batchTaskForm.monitorItem.failCount} Row </font></th>
	</tr>
	<tr>
	    <th width="5%">No</th>
		<th width="5%">Line in Excel</th>
		<th width="5%">กลุ่ม</th>
		<th width="5%">รหัส Pens</th>
		<th width="10%">MATERIAL NO. / ชื่อย่อ Pens </th>
		<th width="10%">BARCODE NO</th>
		<th width="5%">Style</th>
		<th width="30%">Message</th>
	</tr>
	<%
	 String[] lineArr = null;
	 int totalQty = 0;
	 int no = 0;
	 for(int i=0;i<failList.size();i++){
		 no++;
		 MonitorItemResultBean item = failList.get(i);
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
		    <td><%=no%></td>
		    <td><%=lineArr[0] %></td>
			<td><%=lineArr[1] %></td>
			<td><%=lineArr[2] %></td>
			<td><%=lineArr[3] %></td>
			<td><%=lineArr[4] %></td>
			<td><%=lineArr[5] %></td>
			<td align="left"><font color="red"><%=lineArr[6] %></font></td>
		</tr>
		<%} %>
	</table>
  <%} %>

<% if(successList != null && successList.size() >0){ %> 
    <p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	  <th colspan="10">จำนวน Row ที่สามารถ Import ได้   ${batchTaskForm.monitorItem.successCount} Row </th>
	</tr>
	<tr>
	    <th width="5%">No</th>
		<th width="5%">Line in Excel</th>
		<th width="5%">กลุ่ม</th>
		<th width="5%">รหัส Pens</th>
		<th width="10%">MATERIAL NO. / ชื่อย่อ Pens </th>
		<th width="10%">BARCODE NO</th>
		<th width="5%">Style</th>
		<th width="30%">Message</th>
	</tr>
	<%
	 String[] lineArr = null;
	 int totalQty = 0;
	 int no = 0;
	 for(int i=0;i<successList.size();i++){
		 no++;
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
		    <td><%=no%></td>
		    <td><%=lineArr[0] %></td>
			<td><%=lineArr[1] %></td>
			<td><%=lineArr[2] %></td>
			<td><%=lineArr[3] %></td>
			<td><%=lineArr[4] %></td>
			<td><%=lineArr[5] %></td>
			<td><%=lineArr[6] %></td>
		</tr>
		<%} %>
	</table>
   <%} %>
	 
<% }%>
					
								