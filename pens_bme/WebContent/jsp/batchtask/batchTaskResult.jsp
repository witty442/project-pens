<%@page import="com.isecinc.pens.bean.MonitorBean"%>
<%@page import="com.pens.util.Utils"%>
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
		<th> ชื่อ Process</th>
		<th> ชื่อ Process(TH)</th>
		<th> ประเภทข้อมูล</th>
		<th> ผู้สร้าง</th>
	    <th> วันที่เริ่ม</th>
		<th> วันที่สำเร็จ</th>
		<th> สถานะ</th>
		<th> Message</th>
		<th> รายละเอียด</th>
		</tr>
		<%
		 for(int i=0;i<batchTaskForm.getResults().length;i++){
			 MonitorBean item = batchTaskForm.getResults()[i];
		%>
		<tr> 
            <td width="3%"> <%=(i+1)%></td>
            <td width="4%"> <%=item.getTransactionId() %></td>
			<td width="10%"> <%=item.getName() %></td>
			<td width="20%"> <%=Utils.isNull(item.getThName()) %></td>
			<td width="3%"> <%=Utils.isNull(item.getTransactionType()) %></td>
			<td width="4%"> <%=item.getCreateUser() %></td>
			<td width="7%"> <%=item.getSubmitDateDisp()%></td>
			<td width="7%"> <%=Utils.isNull(item.getUpdateDateDisp())%></td>  
			<td width="3%"> 
			   <%if(item.getStatus()==1){ %>
			     <font color='green' size="2"><b> Success</b></font>
			   <%}else if(item.getStatus()==0){ %>
			      <font color='#2892FC' size="2"><b> Running </b></font>
			   <%}else{ %>
			      <font color='red' size="2"><b> Error</b></font> 
			   <%} %>
			</td>
			<td align="left" width="14%"><font color='red'><%=Utils.isNull(item.getErrorMsg())%> </font></td>
		    <td width="11%">
		    <!-- Case Export save all path for download after run Batch Success -->
		      <%if(Utils.isNull(item.getType()).equalsIgnoreCase("export")
		    	 && item.getStatus()==1){  %>
		         <a href="javascript:downloadFile('<%=item.getFileName()%>')">DownloadFile</a>
		      <%}else if( Utils.isNull(item.getType()).equalsIgnoreCase("import")){ %>
		          FileImport:<%=item.getFileName()%>
		      <%} %>
		    </td>
		</tr>
		<%} %>	
	</table>
</c:if>

<%
 String pageName = request.getParameter("pageName");
 //System.out.println("pageName:"+pageName);
%>

<%if("ImportOrderFromExcel".equalsIgnoreCase(pageName)){ %>
  <jsp:include page="sub/ImportOrderFromExcel_sub.jsp"></jsp:include>
<%}else if("ImportBarcodeMasterFromExcel".equalsIgnoreCase(pageName)){ %>
  <jsp:include page="sub/ImportBarcodeMasterFromExcel_sub.jsp"></jsp:include>
<%}else if("ImportOrderToOracleFromExcel".equalsIgnoreCase(pageName)){ %>
  <jsp:include page="sub/ImportOrderToOracleFromExcel_sub.jsp"></jsp:include>
<%}else{ %>
   <%if(batchTaskForm.getTaskInfo().getDispBean().isDispDetail()){ %>
     <jsp:include page="sub/AllResultBatchTask_sub.jsp"></jsp:include>
   <%} %>
<%} %>

 
					
								