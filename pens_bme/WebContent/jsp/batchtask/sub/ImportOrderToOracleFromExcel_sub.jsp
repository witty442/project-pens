<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.MonitorItemBean"%>
<%@page import="com.isecinc.pens.bean.MonitorItemResultBean"%>
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
	  <th colspan="15"><font color="#921F06">จำนวน Row ที่ไม่สามารถ Import ได้   ${batchTaskForm.monitorItem.failCount} Row </font></th>
	</tr>
	<tr>
	    <th width="5%">No</th>
		<th width="5%">Line in Excel</th>
		<th width="5%">Order Number</th>
		<th width="5%">Ordered Date</th>
		<th width="5%">Customer Number</th>
		<th width="5%">Ship to Location</th>
		<th width="5%">Customer PO</th>
		<th width="5%">Item</th>
		<th width="5%">Qty</th>
		<th width="5%">Sale Code</th>
		<th width="5%">Sub Inventory</th>
		<th width="5%">Order Type</th>
		<th width="5%">ขนส่งโดย</th>
		<th width="5%">Status</th>
		<th width="20%">Message</th>
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
		    <%for(int c=0;c<lineArr.length;c++){ %>
		        <%if(c==lineArr.length-1){ %>
		          <!-- Show Message Error -->
			      <td align="left"><font color="red"><%=lineArr[c] %></font></td>
			    <%}else{ %>
				   <td><%=lineArr[c] %></td>
				<%} %>
			<% }%>
		</tr>
		<%} %>
	</table>
  <%} %>

<% if(successList != null && successList.size() >0){ %> 
    <p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	  <th colspan="15">จำนวน Row ที่สามารถ Import ได้   ${batchTaskForm.monitorItem.successCount} Row </th>
	</tr>
	<tr>
	  <th width="5%">No</th>
		<th width="5%">Line in Excel</th>
		<th width="5%">Order Number</th>
		<th width="5%">Ordered Date</th>
		<th width="5%">Customer Number</th>
		<th width="5%">Ship to Location</th>
		<th width="5%">Customer PO</th>
		<th width="5%">Item</th>
		<th width="5%">Qty</th>
		<th width="5%">Sale Code</th>
		<th width="5%">Sub Inventory</th>
		<th width="5%">Order Type</th>
		<th width="5%">ขนส่งโดย</th>
		<th width="5%">Status</th>
		<th width="20%">Message</th>
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
		    <%for(int c=0;c<lineArr.length;c++){ %>
		        <td><%=lineArr[c] %></td>
			<%}%>
		</tr>
		<%} %>
	</table>
   <%} %>
	 
<% }%>
					
								