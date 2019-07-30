<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="java.util.Date"%>
<%@page import="util.DateToolsUtil"%>
<%@page import="util.SessionGen"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="controlAllReportForm" class="com.isecinc.pens.web.report.controlall.ControlAllReportForm" scope="request" />
<%

//set default startDate-endDate in currDate
if(Utils.isNull(controlAllReportForm.getControlAllReport().getStartDate()).equals("")
	&& Utils.isNull(controlAllReportForm.getControlAllReport().getEndDate()).equals("")	){
	
	  controlAllReportForm.getControlAllReport().setStartDate(DateToolsUtil.getCurrentDate(DateToolsUtil.DD_MM_YYYY_WITH_SLASH, DateToolsUtil.local_th));
	  controlAllReportForm.getControlAllReport().setEndDate(DateToolsUtil.getCurrentDate(DateToolsUtil.DD_MM_YYYY_WITH_SLASH, DateToolsUtil.local_th));
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>

<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('startDate'));
	new Epoch('epoch_popup','th',document.getElementById('endDate'));
}

function gotoReport(path){
	if($('#startDate').val()==''){
		alert('กรุณาใส่เงื่อนไขในการออกรายงาน');
		$('#startDate').focus();
		return;
	}
	if($('#endDate').val()==''){
		alert('กรุณาใส่เงื่อนไขในการออกรายงาน');
		$('#endDate').focus();
		return;
	}
	if(!checkDateDiff($('#startDate').val(),$('#endDate').val())){
		return;
	}
	
	if(document.getElementById('msg')!=null){
		document.getElementById('msg').innerHTML = '';
	}
	document.controlAllReportForm.action = path + "/jsp/controlAllReportAction.do?do=searchReport&rf=Y";
	document.controlAllReportForm.submit();
	return true;
}

function clearForm(path){
	document.controlAllReportForm.action = path + "/jsp/controlAllReportAction.do?do=clearForm";
	document.controlAllReportForm.submit();
	return true;
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="ControlAllReport"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/controlAllReportAction">
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="38%">&nbsp;</td>
								<td colspan="3"></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="DateFrom" bundle="sysele"/><font color="red">*</font></td>
								<td align="left" width="10%">
								     <html:text property="controlAllReport.startDate" styleId="startDate" readonly="true" size="15"/>
								</td>
								<td align="right" width="5%"><bean:message key="DateTo" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
								     <html:text property="controlAllReport.endDate" styleId="endDate" readonly="true" size="15"/>
								</td>
							</tr>
							<tr>
								<td align="right">&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<%-- <html:hidden property="criteria.fileType" value="PDF"/> --%>
									<html:radio property="criteria.fileType" value="PRINTER"/>&nbsp;<bean:message key="Printer" bundle="sysele"/>
									&nbsp;&nbsp;&nbsp;&nbsp; 
									<%-- <html:radio property="criteria.fileType" value="PDF"/>&nbsp;<bean:message key="PDF" bundle="sysele"/> --%>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="center">
									<a href="javascript:gotoReport('${pageContext.request.contextPath}')">
									  <input type="button" value="พิมพ์" class="newPosBtn">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									  <input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						</html:form>
						<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>