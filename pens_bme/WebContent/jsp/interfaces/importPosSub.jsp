<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.MonitorItemBean"%>
<%@page import="com.isecinc.pens.bean.MonitorItemResultBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:useBean id="interfacesForm" class="com.isecinc.pens.web.interfaces.InterfacesForm" scope="request" />

<!-- Monitor Task  -->
 <jsp:include page="monitor_short.jsp"></jsp:include>

<!-- Detail -->
<%
//Order no,Customer no	,Order Date	,Subinv	,Reference	,Sale code	,Pens Item	,Unit selling price,Qty
if(interfacesForm.getMonitorItemBeanResult() != null){
	 List<MonitorItemResultBean> successList = interfacesForm.getMonitorItemBeanResult().getSuccessList();
	 List<MonitorItemResultBean> failList = interfacesForm.getMonitorItemBeanResult().getFailList();
	 
	 if(successList != null && successList.size() >0){
%>
    <p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	  <th colspan="10">จำนวนรายการที่สามารถ Import ได้   ${interfacesForm.monitorItemBeanResult.successCount} รายการ </th>
	</tr>
	<tr>
		<th width="5%">Line in Excel</th>
		<th width="5%">Order no</th>
		<th width="10%">Customer no</th>
		<th width="10%">Order Date</th>
		<th width="10%">Subinv</th>
		<th width="10%">Reference</th>
		<th width="10%">Sale code</th>
		<th width="10%">Pens Item</th>
		<th width="10%">Unit selling price</th>
		<th width="10%">Qty</th>
	</tr>
	<%
	 String[] lineArr = null;
	 int totalQty = 0;
	 for(int i=0;i<successList.size();i++){
		 MonitorItemResultBean item = successList.get(i);
		 lineArr = item.getMsg().split("\\,");
		 totalQty += Utils.convertStrToInt(Utils.convertDoubleStrToStr(lineArr[9] ));
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
		    <td><%=lineArr[0] %></td>
			<td><%=lineArr[1] %></td>
			<td><%=lineArr[2] %></td>
			<td><%=lineArr[3] %></td>
			<td><%=lineArr[4] %></td>
			<td><%=lineArr[5] %></td>
			<td><%=lineArr[6] %></td>
			<td><%=lineArr[7] %></td>
			<td><%=lineArr[8] %></td>
			<td><%=Utils.convertDoubleToStrNoDigit(lineArr[9]) %></td>
		</tr>
		<%} %>
		<tr class="hilight_text">
		    <td colspan="9" align="right">Total Qty</td>
			<td><%=totalQty%></td>
		</tr>
	</table>
   <%} %>
	
	<%if(failList != null && failList.size() >0){%>
	<p></p>
	<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
	  <th colspan="10">จำนวนรายการที่ไม่่สามารถ Import ได้   ${interfacesForm.monitorItemBeanResult.failCount} รายการ </th>
	</tr>
	<tr>
		<th width="5%">Line in Excel</th>
		<th width="5%">Order no</th>
		<th width="10%">Customer no</th>
		<th width="10%">Order Date</th>
		<th width="10%">Subinv</th>
		<th width="10%">Reference</th>
		<th width="10%">Sale code</th>
		<th width="10%">Pens Item</th>
		<th width="10%">Unit selling price</th>
		<th width="10%">Qty</th>
	</tr>
	<%
	 String[] lineArr = null;
	 int totalQty = 0;
	 for(int i=0;i<failList.size();i++){
		 MonitorItemResultBean item = failList.get(i);
		 lineArr = item.getMsg().split("\\,");
		 
		 totalQty += Utils.convertStrToInt(Utils.convertDoubleStrToStr(lineArr[9] ));
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
		    <td><%=lineArr[0] %></td>
			<td><%=lineArr[1] %></td>
			<td><%=lineArr[2] %></td>
			<td><%=lineArr[3] %></td>
			<td><%=lineArr[4] %></td>
			<td><%=lineArr[5] %></td>
			<td><%=lineArr[6] %></td>
			<td><%=lineArr[7] %></td>
			<td><%=lineArr[8] %></td>
			<td><%=lineArr[9] %></td>
		</tr>
		<%} %>
		<tr class="hilight_text">
		    <td colspan="9" align="right">Total Qty</td>
			<td><%=totalQty%></td>
		</tr>
	</table>
	 <%} %>
	 
<% }%>
					
								