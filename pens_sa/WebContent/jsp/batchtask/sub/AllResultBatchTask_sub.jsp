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
	 String columnHeadStrArr = batchTaskForm.getMonitorItem().getColumnHeadStrArr();
	 List<MonitorItemResultBean> successList = batchTaskForm.getMonitorItem().getSuccessList();
	 List<MonitorItemResultBean> failList = batchTaskForm.getMonitorItem().getFailList();
%>
   <%
   /** case no insert result because data too many show successCount ,failCount from monitor */
   if( (failList==null ||(failList != null && failList.size()==0) )
		   && (successList==null || (successList != null && successList.size()==0))){
   %>
   <p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" >
	<tr>
	  <th ><font color="#921F06">จำนวน Row ที่ไม่สามารถ Import ได้   ${batchTaskForm.monitorItem.failCount} Row </font></th>
	</tr>
	<tr>
	  <th >จำนวน Row ที่สามารถ Import ได้   ${batchTaskForm.monitorItem.successCount} Row </th>
	</tr>
	</table>
   <%} %>
   <!-------------------------------------------------------------->
   
	<%if(failList != null && failList.size() >0){
		//Gen Column Head Table
	  if( !Utils.isNull(columnHeadStrArr).equals("")){
		String[] columnHeadArr = columnHeadStrArr.split("\\|");
	%>
	<p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	  <th colspan="<%=columnHeadArr.length%>"><font color="#921F06">จำนวน Row ที่ไม่สามารถ Import ได้   ${batchTaskForm.monitorItem.failCount} Row </font></th>
	</tr>
	<tr>
		<% 
			for(int c=0;c<columnHeadArr.length;c++){
		%>
		  <th width="5%"><%=columnHeadArr[c]%></th>
		<% }} %>
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
			      <td align="left" width="10%"><font color="red"><%=lineArr[c] %></font></td>
			    <%}else{ %>
				   <td><%=lineArr[c] %></td>
				<%} %>
			<% }%>
		</tr>
		<%} %>
	</table>
  <%} %>

<% if(successList != null && successList.size() >0){ 
	//Gen Column Head Table
    if( !Utils.isNull(columnHeadStrArr).equals("")){
	   String[] columnHeadArr = columnHeadStrArr.split("\\|");
%> 
    <p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	  <th colspan="<%=columnHeadArr.length%>">จำนวน Row ที่สามารถ Import ได้   ${batchTaskForm.monitorItem.successCount} Row </th>
	</tr>
	<tr>
		<% 
			for(int c=0;c<columnHeadArr.length;c++){
		%>
		  <th width="5%"><%=columnHeadArr[c]%></th>
		<% }} %>
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
		         <%if(c==lineArr.length-1){ %>
		          <!-- Show Message Error -->
			      <td align="left" width="10%"><%=lineArr[c] %></td>
			    <%}else{ %>
				   <td><%=lineArr[c] %></td>
				<%} %>
			<%}%>
		</tr>
		<%} %>
	</table>
   <%} %>
	 
<% }%>
					
								