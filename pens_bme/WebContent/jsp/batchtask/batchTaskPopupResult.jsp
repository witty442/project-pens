<%@page import="com.isecinc.pens.bean.MonitorItemBean"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.pens.util.Utils"%>
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
 if(batchTaskForm.getResults() != null){
     System.out.println("Page BatchResults: batchTaskForm["+batchTaskForm+"]results length["+batchTaskForm.getResults().length+"]");
 }
 MonitorItemBean monitorItemBean = batchTaskForm.getMonitorItem();
%>

<%if(batchTaskForm.getResults() != null && batchTaskForm.getResults().length >0){ %>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" >
		<tr>
        <th> No.</th>
        <th> �Ţ�����¡��</th>
		<th> ���� Process</th>
		<th> ���� Process(TH)</th>
		<th> ������������</th>
		<th> ������ҧ</th>
		<th> �ѹ��������</th>
		<th> �ѹ��������</th>
		<th> ʶҹ�</th>
		<th> Message</th>
		<th> ��������´</th>
		</tr>
		<%
		 for(int i=0;i<batchTaskForm.getResults().length;i++){
			 MonitorBean item = batchTaskForm.getResults()[i];
		%>
		<tr class="" id="">
		       
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
				      <font color='#2892FC' size="2"><b>  Running </b></font>
				   <%}else{ %>
				      <font color='red' size="2"><b> Error</b></font> 
				   <%} %>
				</td>
				<td align="left" width="14%"><font color='red'><%=Utils.isNull(item.getErrorMsg())%></font> </td>
			    <td width="11%">
			    <!-- Case Export save all path for download after run Batch Success -->
			      <%if(Utils.isNull(item.getType()).equalsIgnoreCase("export")
			    	 && item.getStatus()==1){ %>
			         <a href="javascript:downloadFile('<%=item.getFileName()%>')">DownloadFile</a>
			       <%}else if( Utils.isNull(item.getType()).equalsIgnoreCase("import")){ %>
		              FileImport:<%=item.getFileName()%>
		           <%} %>
			    </td>
		</tr>
		<%} %>	
	</table>
<%} %>	

  <!-- Result Batch Task Result Import-->
  <%
  System.out.println("batchTaskName:"+batchTaskForm.getTaskInfo().getDescription()+":"+batchTaskForm.getTaskInfo().getDispBean().isDispDetail()) ;
  %>
  
  <%if(batchTaskForm.getTaskInfo().getDispBean().isDispDetail()){ %>
     <jsp:include page="sub/AllResultBatchTaskPopup_sub.jsp"></jsp:include>
  <%} %>
<%} %>	
								