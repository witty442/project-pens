<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

<jsp:useBean id="summaryForm" class="com.isecinc.pens.web.summary.SummaryForm" scope="session" />
<%
String totalPage = "";
String currentPage = "";
if(summaryForm.getResults() != null){
	 totalPage = String.valueOf(Utils.calcTotalPage(summaryForm.getResults().size(),50));
	 
	String queryStr= request.getQueryString();
	if(queryStr.indexOf("d-") != -1){
		queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
		System.out.println("queryStr:"+queryStr);
		currentPage = request.getParameter(queryStr);
	}
}
/** Case ToatlPage ==1 set currentPage=1 ***/
if(totalPage.equals("1")){
	currentPage ="1";
}
System.out.println("totalPage:"+totalPage);
System.out.println("currentPage:"+currentPage);
%>
<html>
<head>
<script>
</script>
</head>
     <c:if test="${summaryForm.page == 'reportEndDateLotus'}">
     <c:if test="${summaryForm.results != null}">

		<c:if test="${summaryForm.summaryType == 'PensItem'}">
		<br/>
			<display:table style="width:100%;" id="item" name="sessionScope.summaryForm.results" defaultsort="0"  defaultorder="descending" class="resultDisp"
			    requestURI="#" sort="list" pagesize="50">	
			    
			    <display:column  title="รหัสสาขา" property="storeCode"  sortable="false" class="td_text" style="width:5%"/>
			    <display:column  title="ชื่อสาขา" property="storeName"  sortable="false" class="td_text" style="width:10%"/>
			    <display:column  title="Pens Item" property="pensItem"  sortable="false" class="td_text"  style="width:5%"/>	 
			    <display:column  title="Group" property="group"  sortable="false" class="td_text"  style="width:6%"/>	
			    <display:column  title="Begining Qty" property="beginingQty"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_number"  style="width:8%"/>
			    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Adjust QTY " property="adjustQty"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Stock Short QTY " property="stockShortQty"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Price List " property="retailPriceBF"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Amount " property="onhandAmt"  sortable="false" class="td_number"  style="width:8%"/>	
			</display:table>
			 <%if(currentPage.equals(totalPage)){ %>
				<table width="100%" class="resultDisp">		
				    <tr>
				      <td width="5%">&nbsp;</td>
				      <td width="10%">&nbsp;</td>
				      <td width="5%">&nbsp;</td>
				      <td width="6%"><b>Total</b></td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="beginingQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleInQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleReturnQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleOutQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="adjustQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="stockShortQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandQty"/> </td>
				      <td class="td_number_bold" width="8%">&nbsp;</td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandAmt"/> </td>
				    </tr>
				</table>
			 <%} %>
			</c:if>
			
			<c:if test="${summaryForm.summaryType == 'GroupCode'}">
		    <br/>
			<display:table style="width:100%;" id="item" name="sessionScope.summaryForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
			    requestURI="#" sort="list" pagesize="50">	
			    
			    <display:column  title="รหัสสาขา" property="storeCode"  sortable="false" class="td_text"  style="width:8%"/> 
			    <display:column  title="ชื่อสาขา" property="storeName"  sortable="false" class="td_text"  style="width:12%"/> 
			    <display:column  title="Group" property="group"  sortable="false" class="td_text" style="width:8%"/>	
			    <display:column  title="Begining Qty" property="beginingQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Sale In Qty" property="saleInQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Sale Return Qty" property="saleReturnQty"  sortable="false" class="td_number" style="width:8%"/>
			    <display:column  title="Sale Out Qty" property="saleOutQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Adjust QTY " property="adjustQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Stock Short QTY " property="stockShortQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Onhand QTY " property="onhandQty"  sortable="false" class="td_number" style="width:8%"/>	
			    <display:column  title="Price List " property="retailPriceBF"  sortable="false" class="td_number"  style="width:8%"/>	
			    <display:column  title="Amount " property="onhandAmt"  sortable="false" class="td_number"  style="width:8%"/>	
			</display:table>
			<%if(currentPage.equals(totalPage)){ %>
				<table width="100%" class="resultDisp">		
				    <tr>
				      <td width="8%">&nbsp;</td>
				      <td width="12%">&nbsp;</td>
				      <td width="8%"><b>Total</b></td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="beginingQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleInQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleReturnQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="saleOutQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="adjustQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="stockShortQty"/> </td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandQty"/> </td>
				      <td class="td_number_bold" width="8%">&nbsp;</td>
				      <td class="td_number_bold" width="8%"><bean:write name="summary" property="onhandAmt"/> </td>
				    </tr>
				</table>
			 <%} %>
			</c:if>
	   </c:if>
 </c:if>
</body>
</html>