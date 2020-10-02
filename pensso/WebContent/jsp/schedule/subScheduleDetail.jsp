<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%
 //get d-xxx-d parameter d-49489-p=16
String queryStr= request.getQueryString();
if(queryStr.indexOf("d-") != -1){
	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
	System.out.println("queryStr:"+queryStr);
}
%>
<html>
<head>
</head>
<%if(session.getAttribute("dataList") != null){ %>
	<display:table style="width:100%;" id="item" name="sessionScope.dataList" defaultsort="0"  defaultorder="descending" class="resultDisp"
	    requestURI="#" sort="list" pagesize="50">	
	    
	    <display:column  title="No." property="id"  sortable="false" class="td_text_center" style="width:3%"/>
	     <display:column  title="Program ID" property="programId"  sortable="false" class="td_text" style="width:7%"/>
	    <display:column  title="Task Name" property="name"  sortable="false" class="td_text" style="width:10%;white-space: nowrap"/>
	    <display:column  title="Status" property="status"  sortable="false" class="td_text_center" style="width:6%"/>
	    <display:column  title="Type" property="type"  sortable="false" class="td_text_center" style="width:5%"/>	
	    <display:column  title="Batch Date" property="batchDateTime"  sortable="false" class="td_text_center" style="width:10%"/>	
	    <display:column  title="Last Run Date" property="lastRunDate"  sortable="false" class="td_text_center" style="width:10%"/>
	    <display:column  title="Next Run Date" property="nextRunDate"  sortable="false" class="td_text_center" style="width:10%"/>	
	    <display:column  title="Source Path" property="sourcePath"  sortable="false" class="td_text_center" style="width:10%"/>	
	    <display:column  title="Destination  Path" property="destPath"  sortable="false" class="td_text_center" style="width:10%"/>	
	    <display:column  title="File Name" property="fileName"  sortable="false" class="td_text_center" style="width:10%"/>	
	    <display:column  title="Size Of File" property="sizeOfFile"  sortable="false" class="td_text_center" style="width:5%"/>	
	    <display:column  title="Tatal Record" property="noOfRecord"  sortable="false" class="td_text_center" style="width:5%"/>	
	    
	    <display:column  title="TransactionId" property="transactionId"  sortable="false" class="td_text_center" style="width:5%"/>	
	     <display:column  title="ViewDetail" sortable="false" class="td_text_center" style="width:5%"> 
	            <html:button property="button" value="View" onclick="submitViewDetail('${pageContext.request.contextPath}',${item.transactionId});"/>
	    </display:column>
	    
	    <display:column  title="Delete" sortable="false" class="td_text_center" style="width:5%"> 
	            <html:button property="button" value="Delete" onclick="submitDelete('${pageContext.request.contextPath}',${item.no});"/>
	    </display:column>
	
    </display:table>
<%} %>

</body>
</html>