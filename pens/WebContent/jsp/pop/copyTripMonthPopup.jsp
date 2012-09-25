<!--
Copy Trip 
 -->
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/jscal2.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jscal2.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" language="javascript">
function loadMe(){
	
}

// call ajax
function processCopy(){
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/copyTripMonthQuery.jsp",
			data : "monthFrom=" + $('#monthFrom').val() + "&yearFrom=" + $('#yearFrom').val()+ "&monthTo=" + $('#monthTo').val()+ "&yearTo=" + $('#yearTo').val(),
			success: function(getData){
				var returnString = jQuery.trim(getData);
				document.getElementById('msg').innerHTML = returnString;
			}
		}).responseText;
	});
}

<%
java.util.List yearTripList = com.isecinc.pens.model.MTrip.getYearTripList();
%>

</script>
</head>
<body onload="loadMe();" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" class="popbody">
<jsp:include page="../program.jsp">
	<jsp:param name="system" value=""/>
	<jsp:param name="function" value=""/>
	<jsp:param name="code" value="คัดลอกทริปทั้งเดือน"/>
</jsp:include>
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td colspan="2" align="center"><span id="msg" style="color: #ff0000;"></span></td>
	</tr>
	<tr>
		<td align="right" width="40%"><bean:message key="CopyFrom" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			เดือน
			<select id="monthFrom">
			    <option value="01">01</option>
			    <option value="02">02</option>
			    <option value="03">03</option>
			    <option value="04">04</option>
			    <option value="05">05</option>
			    <option value="06">06</option>
			    <option value="07">07</option>
			    <option value="08">08</option>
			    <option value="09">09</option>
			    <option value="10">10</option>
			    <option value="11">11</option>
			    <option value="12">12</option>
           </select>
                                ปี 
               <select  id="yearFrom">
                <%if(yearTripList != null && yearTripList.size() > 0){ 
                   for(int i=0;i<yearTripList.size();i++){
                	   String year = (String)yearTripList.get(i);
                %>
			        <option value="<%=year%>"><%=year%></option> 
			    <% }} %>
           </select>
		</td>
	</tr>
	<tr>
		<td align="right"><bean:message key="CopyTo" bundle="sysele"/><font color="red">*</font></td>
		<td align="left">
			เดือน
			<select  id="monthTo">
			    <option value="01">01</option>
			    <option value="02">02</option>
			    <option value="03">03</option>
			    <option value="04">04</option>
			    <option value="05">05</option>
			    <option value="06">06</option>
			    <option value="07">07</option>
			    <option value="08">08</option>
			    <option value="09">09</option>
			    <option value="10">10</option>
			    <option value="11">11</option>
			    <option value="12">12</option>
           </select>
                                ปี 
               <select  id="yearTo">
                <%if(yearTripList != null && yearTripList.size() > 0){ 
                   for(int i=0;i<yearTripList.size();i++){
                	   String year = (String)yearTripList.get(i);
                %>
			        <option value="<%=year%>"><%=year%></option> 
			    <% }} %>
           </select>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td>&nbsp;</td>
		<td align="left">
			<input type="button" value="Process" onclick="processCopy();" class="newPosBtn"/>
			<a href="#" onclick="window.close();">
<!--			<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
			</a>
		</td>
	</tr>
</table>
<br/>
</body>
</html>