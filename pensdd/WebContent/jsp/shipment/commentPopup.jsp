<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String comment = request.getParameter("comment");
String idx = request.getParameter("idx");

pageContext.setAttribute("comment",comment,PageContext.PAGE_SCOPE);
pageContext.setAttribute("idx",idx,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.core.bean.Messages"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
function loadMe(){
	var commentValue = window.opener.document.getElementsByName("confirms[${idx}].comment")[0].value;
	$('#comment').val(commentValue);
	$('#comment').focus();
}

function saveComment(){
	comment = document.getElementById("comment");
	window.opener.setComment('${idx}',comment.value);
	window.close();
}
</script>
</head>
<body onload="loadMe();">
<textarea cols="30" rows="5" id="comment" name="comment" >${comment}</textarea>

<br>
<input type="button" value="บันทึก" onclick="saveComment();">
<input type="button" value="ปิดหน้าจอ" onclick="window.close()">
</body>
</html>