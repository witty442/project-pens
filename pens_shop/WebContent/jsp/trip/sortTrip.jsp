<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.tablednd_0_5.js"></script>
<title></title>
<script type="text/javascript">
$(document).ready(function() {
    // Initialise the table
    $("#table-1").tableDnD();
});

function closeSort(path, type){
	document.tripForm.action = path + "/jsp/tripAction.do?do=search&rf=Y&type="+type;
	document.tripForm.submit();
	return true;
}
function saveNewSort(path){
	var length = document.getElementsByName('lineNo').length;
	var i;
	for(i=0;i<length;i++){
		document.getElementsByName('linesNo')[0].value += "," + document.getElementsByName('lineNo')[i].value;
	}

	document.tripForm.action = path + "/jsp/tripAction.do?do=saveNewSort";
	document.tripForm.submit();
	return true;
}
</script>
</head>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" id="table-1">
	<tr>
		<th class="order"><bean:message key="No"  bundle="sysprop"/></th>
		<th class="code"><bean:message key="Trip.LineNo" bundle="sysele"/></th>
		<th class="code"><bean:message key="Trip.TripDate"  bundle="sysele"/></th>
		<th class="th"><bean:message key="Trip.CustomerName"  bundle="sysele"/></th>
	</tr>
	<c:forEach var="results" items="${tripForm.results}" varStatus="rows">
	<c:choose>
		<c:when test="${rows.index %2 == 0}">
			<c:set var="tabclass" value="lineO"/>
		</c:when>
		<c:otherwise>
			<c:set var="tabclass" value="lineE"/>
		</c:otherwise>
	</c:choose>
	<tr class="<c:out value='${tabclass}'/>" id="${rows.index+1}">
		<td><c:out value='${rows.index+1}'/><html:hidden property="ids" value="${results.id}"/></td>
		<td align="center">${results.lineNo}</td>
		<td align="center">${results.tripDateFrom}</td>
		<td align="left">
			${results.customer.code}&nbsp;${results.customer.name}&nbsp;${results.customer.name2}
			<html:hidden property="customerId" value="${results.customer.id}"/>
		</td>
	</tr>
	</c:forEach>
</table>
<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
	<tr>
		<td align="left" colspan="12" class="footer">&nbsp;</td>
	</tr>
</table>
<br/>
<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
	<tr>
		<td align="right">
			<a href="#" onclick="return saveNewSort('${pageContext.request.contextPath}');">
<!--			<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
			<input type="button" value="บันทึก" class="newPosBtn">
			</a>
			<a href="#" onclick="closeSort('${pageContext.request.contextPath}','admin');">
<!--			<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
<input type="hidden" name="type" value="admin">
</html>