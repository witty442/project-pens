<%@page import="com.isecinc.pens.bean.MonitorItemResultBean"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.bean.MonitorItemBean"%>
<%@page import="com.isecinc.pens.web.summary.SummaryAction"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />
 <%--  <%if(interfacesForm.getMonitorItemBeanResult().getFailCount()>0 || interfacesForm.getMonitorItemBeanResult().getSuccessCount()>0){ %>
   <div align="center"><font size="3"><b>Result import</b></font></div>
  <%} %> --%>
  
  <c:if test="${interfacesForm.monitorItemBeanResult.failCount >0}">
                                                    
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result_error">
	 <tr>
		<th colspan="7"  align="left"><font color="#F5B8B8">จำนวนรายการที่ไม่สามารถ Import ได้   ${interfacesForm.monitorItemBeanResult.failCount} รายการ </font> </th>
	</tr> 
	<tr>
		<th width="5%">Row In Excel</th>
		<th width="10%">Sales Date</th>
		<th width="5%">Store No</th>
		<th width="25%">Item Wacoal</th>
		<th width="30%">Description</th>
		<th width="5%">QTY</th>
		<th width="25%">Message </th>
	</tr>
	<%
	List<MonitorItemResultBean> list = interfacesForm.getMonitorItemBeanResult().getFailList();

	for(int i=0;i<list.size();i++){
		MonitorItemResultBean resultBean = list.get(i);
		if(!"".equals(resultBean.getMsg())){
			String[] ms = resultBean.getMsg().split("\\,");
			String tabclass  ="lineE";
			if(i%2==0){
				tabclass  ="lineO";
			}
	%>
		<tr class="<%=tabclass%>">
			<td><%=ms[0] %></td>
			<td align="left"><%=ms[1] %></td>
			<td align="left"><%=ms[2] %></td>
			<td align="left"><%=ms[3] %></td>
			<td align="left"><%=ms[4] %></td>
			<td align="left"><%=ms[5] %></td>
			<td align="left"><%=ms[6] %></td>
		</tr>
	<%} }%>
	</table>
</c:if>
<p></p>
 <c:if test="${interfacesForm.monitorItemBeanResult.successCount >0}">
	
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result_success">
	 <tr>
		<th colspan="7"  align="left"><font color="">จำนวนรายการที่สามารถ Import ได้   ${interfacesForm.monitorItemBeanResult.successCount} รายการ </font> </th>
	</tr> 
	<tr>
		<th width="5%">Row In Excel</th>
		<th width="10%">Sales Date</th>
		<th width="5%">Store No</th>
		<th width="25%">Store Name</th>
		<th width="30%">Description</th>
		<th width="5%">QTY</th>
		<th width="25%">Message </th>
	</tr>
	<%
	List<MonitorItemResultBean> list = interfacesForm.getMonitorItemBeanResult().getSuccessList();
	for(int i=0;i<list.size();i++){
		MonitorItemResultBean resultBean = list.get(i);
		if(!"".equals(resultBean.getMsg())){
			String[] ms = resultBean.getMsg().split("\\,");
			//System.out.println("ms length:"+ms.length);
			String tabclass  ="lineE";
			if(i%2==0){
				tabclass  ="lineO";
			}
	%>
		<tr class="<%=tabclass%>">
			<td><%=ms[0] %></td>
			<td align="left"><%=ms[1] %></td>
			<td align="left"><%=ms[2] %></td>
			<td align="left"><%=ms[3] %></td>
			<td align="left"><%=ms[4] %></td>
			<td align="left"><%=ms[5] %></td>
			<td align="left"><%=ms[6] %></td>
		</tr>
	<%} }%>
	</table>
</c:if>
					
								