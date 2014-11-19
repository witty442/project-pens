<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">

<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/pick_confFinish.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript">

function loadMe(_path){
	
}

function print(path){
	var form = document.tempForm;
	form.action = path + "/jsp/printGroupCodeBoxAction.do?do=print";
	form.submit();
	return true;
}

function isNum(obj){
	  if(obj.value != ""){
		var newNum = parseInt(obj.value);
		if(isNaN(newNum)){
			alert('ให้กรอกได้เฉพาะตัวเลขเท่านั้น');
			obj.value = "";
			obj.focus();
			return false;
		}else{return true;}
	   }
	  return true;
	}

</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/printGroupCodeBoxAction">
   
	<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="tableSearch">
		    <tr>
				<th >Group Code</th>
				<th >จำนวนแผ่นที่ต้องการพิมพ์</th>
			</tr>
			<c:forEach var="results" items="${sessionScope.results}" varStatus="rows">
				<c:choose>
					<c:when test="${rows.index %2 == 0}">
						<c:set var="tabclass" value="lineO"/>
					</c:when>
					<c:otherwise>
						<c:set var="tabclass" value="lineE"/>
					</c:otherwise>
				</c:choose>
					<tr class="<c:out value='${tabclass}'/>">
						
						<td class="data_no"> ${results.groupCode}
						<input type="hidden" name="groupCode" value ="${results.groupCode}" />
						</td>
						<td class="data_qty" align="center">
						   <input type="text" name="qty" value ="" size="20"  class="" onchange="isNum(this)"/>
						</td>
					</tr>
			  </c:forEach>
	</table>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
		      <a href="javascript:print('${pageContext.request.contextPath}')">
				<input type="button" value="   พิมพ์     " class="newPosBtnLong"> 
			 </a>
			<a href="#" onclick="window.close();">
			<input type="button" value="ยกเลิก" class="newNegBtn">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>