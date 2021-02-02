<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockReturnForm" class="com.isecinc.pens.web.stockreturn.StockReturnForm" scope="session" />

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession() %>"></script>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript">
<%
String requestNumber = Utils.isNull(request.getParameter("requestNumber"));
String printReport1 = Utils.isNull(request.getParameter("printReport1"));
String printReport2 = Utils.isNull(request.getAttribute("printReport2"));
String fileType = Utils.isNull(request.getParameter("fileType"));

String msg = "";
System.out.println("printReport1:"+printReport1);
System.out.println("printReport2:"+printReport2);
User user = ((User)session.getAttribute("user"));
%>
function loadMe(_path){
	
<% if(printReport1.equalsIgnoreCase("printReport1")){ %>
	  printReport1();
	  // setTimeout(function(){printReport2();},700);   
<%}%>
}
function printReport1(){
	   var param  = "do=printReport&reportType=1";
	   param += "&requestNumber=<%=requestNumber%>";
	   param += "&fileType=<%=fileType%>";
	 //  alert("printReport2");
	   
	   document.stockReturnForm.action =  "${pageContext.request.contextPath}/jsp/stockReturnAction.do?"+param;
	   document.stockReturnForm.submit(); 
}
	
function printReport2(){
   var param  = "do=printReport&reportType=2";
   param += "&requestNumber=<%=requestNumber%>";
   param += "&fileType=<%=fileType%>";
   //alert("printReport2");
   
   document.stockReturnForm.action =  "${pageContext.request.contextPath}/jsp/stockReturnAction.do?"+param;
   document.stockReturnForm.submit(); 
} 
</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/stockReturnAction">
<input type="hidden" name="load" value="">
<input type="hidden" name="requestNumber" value="<%=requestNumber%>">
<input type="hidden" name="fileType" value="<%=fileType%>">
<input type="hidden" name="printReport2" value="<%=printReport2%>">
<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="Transaction"/>
	<jsp:param name="function" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="center">
			<!-- Display Error msg -->
			<font color="red" size="2">
			   <%out.println(Utils.isNull(request.getAttribute("ERROR_MSG"))); %>
			</font>
			         กำลังพิมพ์ รายงาน .........
			<br/><br/><br/><br/>
			<a href="#" onclick="window.close();">
			  <input type="button" value="   ปิดหน้าจอ     " class="newPosBtnLong">
			</a>
		</td>
		<td width="20%">&nbsp;</td>
	</tr>
</table>
</html:form>
<!-- BODY -->
</body>
</html>