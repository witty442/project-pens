<%@page import="com.pens.util.EnvProperties"%>
<%@page import="com.pens.util.*"%>
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

<%
User user = (User) session.getAttribute("user");
EnvProperties env = EnvProperties.getInstance();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">

<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/default.css" type="text/css">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/newstyle.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<script type="text/javascript">

function loadMe(_path){
	
   <%
    String reportName = request.getParameter("report_name");
    String typeReport  ="";
    String boxNo = "";
    String docNo ="";
    
	if("ControlReturnReport".equals(reportName)){
		typeReport = Utils.isNull(request.getParameter("typeReport"));
		%>
	
		document.tempForm.action = _path + "/jsp/confirmReturnWacoalAction.do?do=printControlReturnReport&typeReport=<%=typeReport%>";
		document.tempForm.submit();
		
  <%}else if("ReturnBoxReport".equals(reportName)){ 
	  typeReport = Utils.isNull(request.getParameter("typeReport"));
	  boxNo = Utils.isNull(request.getParameter("boxNo"));
  %>

      document.tempForm.action = _path + "/jsp/confirmReturnWacoalAction.do?do=printReturnBoxReport&typeReport=<%=typeReport%>&boxNo=<%=boxNo%>";
	  document.tempForm.submit();
		
  <%}else if("PayInReport".equals(reportName)){ 
		  typeReport = "PayInReport";
		  docNo = Utils.isNull(request.getParameter("docNo"));
		   if(request.getLocalAddr().equals("192.168.202.244") 
			|| request.getLocalName().equals("0.0.0.0")
				   ){
	  %>
	       document.tempForm.action = _path + "/jsp/payAction.do?do=printReport&typeReport=<%=typeReport%>&docNo=<%=docNo%>";
	       document.tempForm.submit();
	  <%}else{ %>
	    
	       document.tempForm.action = "http://<%=env.getProperty("host.payinreport")%>/printPayInReport?typeReport=<%=typeReport%>&docNo=<%=docNo%>&userName=<%=user.getUserName()%>";
		   document.tempForm.submit();
	  <%}  }%>
  
}


  //setTimeout(function(){window.close();},60000);


</script>
</head>
<body onload="loadMe('${pageContext.request.contextPath}');" topmargin="0" bottommargin="0" leftmargin="100" rightmargin="150" class="popbody">
<!-- BODY -->
<html:form action="/jsp/tempAction">

<input type="hidden" name="load" value="">
<input type="hidden" name="report_name" value="<%=reportName%>">
<input type="hidden" name="typeReport" value="<%=typeReport%>">
<input type="hidden" name="boxNo" value="<%=boxNo%>">

<!-- INCLUDE -->
<jsp:include page="../program.jsp">
	<jsp:param name="system" value="Transaction"/>
	<jsp:param name="function" value=""/>
</jsp:include>
<jsp:include page="../error.jsp"/>
<!-- BUTTON -->
<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
	<tr>
		<td align="right">
			
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