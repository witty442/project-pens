<%@page import="java.util.Date"%>
<%@page import="com.pens.util.DateToolsUtil"%>
<%@page import="com.pens.util.SIdUtils"%>
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
<jsp:useBean id="salesTargetSummaryForm" class="com.isecinc.pens.web.report.salestargetsummary.SalesTargetSummaryForm" scope="request" />
<%
/*  String[] dateArr = DateToolsUtil.getStartEndDateInMonth(new Date());
 salesTargetSummaryForm.getSalesTargetSummaryReport().setDateFrom(dateArr[0]);
 salesTargetSummaryForm.getSalesTargetSummaryReport().setDateTo(dateArr[1]);
 
 System.out.println("dateFrom:"+dateArr[0]+","+salesTargetSummaryForm.getSalesTargetSummaryReport().getDateFrom()); */
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('dateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('dateTo'));
}

function gotoReport(path){
	// Check Date Is Require Parameter
	var errMsg = '';
	var objectFocus = '' ;
	
	if($('#dateFrom').val()=='' && $('#dateTo').val()==''){
		errMsg = 'กรุณาระบุวันที่เริ่มต้นและวันที่สิ้นสุด'; 
		objectFocus = '#dateFrom';
	}
	else if($('#dateFrom').val()==''){
		errMsg = 'กรุณาระบุวันที่เริ่มต้น'; 
		objectFocus = '#dateFrom';
	}
	else if($('#dateTo').val()==''){
		errMsg = 'กรุณาระบุวันที่สิ้นสุด'; 
		objectFocus = '#dateTo';
	}
	else{
		// Check Date Should Be In The Same Month
		var yyyymmFrom = $('#dateFrom').val().substring(6,10)+$('#dateFrom').val().substring(3,5);
		var yyyymmTo = $('#dateTo').val().substring(6,10)+$('#dateTo').val().substring(3,5);

		var ddFrom =$('#dateFrom').val().substring(0,2);
		var ddTo =$('#dateTo').val().substring(0,2);
		
		if(yyyymmFrom != yyyymmTo){
			errMsg = 'กรุณาระบุช่วงวันที่ภายในเดือนเดียวกัน';
			objectFocus = '#dateFrom';
		}
		else if(ddFrom > ddTo){
			errMsg = 'กรุณาระบุวันที่เริ่มต้นก่อนวันที่สิ้นสุด';
			objectFocus = '#dateFrom';
		}
	}

	// Check If Have Error Then Print Out
	if(errMsg.length > 0){
		alert(errMsg);
		$(objectFocus).focus();
		return;		
	}

	// No Error
	if(document.getElementById('msg')!=null){
		document.getElementById('msg').innerHTML = '';
	}
	
	document.salesTargetSummaryForm.action = path + "/jsp/salesTargetSummaryAction.do?do=searchReport&rf=Y";
	document.salesTargetSummaryForm.submit();
	return true;
}

function clearForm(path){
	document.salesTargetSummaryForm.action = path + "/jsp/salesTargetSummaryAction.do?do=clearForm";
	document.salesTargetSummaryForm.submit();
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
				<jsp:param name="function" value="SalesTargetSummary"/>
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
						<html:form action="/jsp/salesTargetSummary">
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="40%">&nbsp;</td>
								<td></td>
							</tr>
							<tr>
								<td align="right">ตั้งแต่วันที่ :<html:text property="salesTargetSummaryReport.dateFrom" styleId="dateFrom" readonly="true" size="15"/></td>
								<td align="left">
								     ถึงวันที่ :<html:text property="salesTargetSummaryReport.dateTo" styleId="dateTo" readonly="true" size="15"/>
								</td>
							</tr>
							<tr>
								<td align="right">ตั้งแต่รหัสสินค้า : <html:text property="salesTargetSummaryReport.productCodeFrom" styleId="productCodeFrom"  size="15"/></td>
								<td align="left">
								ถึงรหัสสินค้า : 
								     <html:text property="salesTargetSummaryReport.productCodeTo" styleId="productCodeTo"  size="15"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="ReportFormat" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<!--<html:radio property="criteria.fileType" value="XLS"/>&nbsp;<bean:message key="Excel" bundle="sysele"/>&nbsp;&nbsp;-->
									<html:radio property="criteria.fileType" value="PDF"/>&nbsp;<bean:message key="PDF" bundle="sysele"/>&nbsp;&nbsp;
									<%-- <html:radio property="criteria.fileType" value="PRINTER"/>&nbsp;<bean:message key="Printer" bundle="sysele"/> --%>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="center">
								
									<input type="button" value="พิมพ์" class="newPosBtn" onclick="gotoReport('${pageContext.request.contextPath}')">
							
									<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
									
								</td>
							</tr>
						</table>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
								
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
