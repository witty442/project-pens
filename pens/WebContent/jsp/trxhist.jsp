<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
String module = request.getParameter("module");
String recordId = request.getParameter("id");

pageContext.setAttribute("trxmodule",module,PageContext.PAGE_SCOPE);
pageContext.setAttribute("trxrecord",recordId,PageContext.PAGE_SCOPE);

%>

<%@page import="com.isecinc.pens.SystemProperties"%><head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">
function trxhistOpen(path){
	if(document.getElementById('histPanel').innerHTML!=''){
		document.getElementById('histPanel').innerHTML='';
		return;
	}
	
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/TrxHistQuery.jsp",
			data : "module=" + $('#trxModule').val()
			+ "&recordId=" + $('#trxRecord').val(),
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				document.getElementById('histPanel').innerHTML = returnString;
			}
		}).responseText;
	});
}
</script>
</head>
<div align="left" style="width: 80%;" class="programlabel">
<bean:message key="TrxHist" bundle="sysprop"/>
<a href="javascript:trxhistOpen('${pageContext.request.contextPath}');">
<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"></a>
</div>
<div id="histPanel" align="left" style="width: 80%;"></div>
<input type="hidden" id="trxModule" name="trxModule" value="${trxmodule}"/>
<input type="hidden" id="trxRecord" name="trxRecord" value="${trxrecord}"/>
