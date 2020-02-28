<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.isecinc.pens.web.autocnhhtemp.AutoCNHHTempBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.List"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="autoCNHHTempForm" class="com.isecinc.pens.web.autocnhhtemp.AutoCNHHTempForm" scope="session" /> 
<%
User user = (User) request.getSession().getAttribute("user");
%>
<html>
<head>
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript">
function loadMe(){
	 /** for popup BatchTask in page **/
	 <%if( "submitedRunBatchTask".equals(request.getAttribute("action"))){%>
	    //lockscreen
	    var path = document.getElementById("path").value;
	    /** Init progressbar **/
		$(function() {
			// update the block message 
	        $.blockUI({ message: "<h2>กำลังทำรายการ     กรุณารอสักครู่......</h2>" }); 
		}); 
		    
		//submitedGenStockOnhandTemp
		var url = path+'/jsp/batchTaskAction.do?do=prepare&pageAction=new&initBatchAction=initBatchFromPageByPopup&pageName=<%=BatchTaskConstants.IMPORT_AUTOCNHHTEMP_FROM_EXCEL%>';
		popupFull(url,'submitedRunBatchTask');
  <%}%>
}

function importExcel(){
   var path = document.getElementById("path").value;
   var form = document.autoCNHHTempForm;
   var extension = '';
   var startFileName = '';
	if(form.dataFile.value.indexOf(".") > 0){
		extension = form.dataFile.value.substring(form.dataFile.value.lastIndexOf(".") + 1).toLowerCase();
		//alert(extension);
	}
	if(form.dataFile.value.indexOf("_") > 0){
		var pathFileName = form.dataFile.value;
		//alert(pathFileName +","+pathFileName.lastIndexOf("\\"));
		startFileName = pathFileName.substring(pathFileName.lastIndexOf("\\")+1,pathFileName.indexOf("_")).toLowerCase();
		//alert(startFileName);
	}
	
	if(form.dataFile.value != '' && (extension == "xls" || extension == "xlsx") ){
	}else{
		alert("กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ");
		return;
	}
	
   if(confirm("กรุณายืนยันการ Import")){
		form.action = path + "/jsp/autoCNHHTempAction.do?do=importExcel";
		form.submit();
		return true;
   }
}

function clearForm(){
	var path = document.getElementById("path").value;
	var form = document.autoCNHHTempForm;
	form.action = path + "/jsp/autoCNHHTempAction.do?do=prepare2&action=new";
	form.submit();
	return true;
}
function searchBatch(path){
	 var path = document.getElementById("path").value;
	//unlockScreen
	setTimeout($.unblockUI, 100); 
	 
	var form = document.autoCNHHTempForm;
	form.action = path + "/jsp/autoCNHHTempAction.do?do=searchBatch";
	form.submit();
	return true;
}
function searchBatchForm(){
	var path = document.getElementById("path").value;
	var form = document.autoCNHHTempForm;
	form.action = path + "/jsp/autoCNHHTempAction.do?do=searchBatchForm";
	form.submit();
	return true;
}
function clearBatchForm(){
	var path = document.getElementById("path").value;
	var form = document.autoCNHHTempForm;
	form.action = path + "/jsp/autoCNHHTempAction.do?do=clearBatchForm";
	form.submit();
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
				<jsp:param name="function" value="AutoCNHHTemp"/>
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
						<html:form action="/jsp/autoCNHHTempAction" enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
								<tr>
								<td align="right" width="40%">เลือกไฟล์(Excel)&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="dataFile" styleClass="" style="width:300px;height:21px"/>
								</td>
							</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										<a href="javascript:importExcel()">
										  <input type="button" value="      Import ข้อมูล        " class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearForm()">
										  <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
							<br/>
							 <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
										<a href="javascript:searchBatchForm()">
										  <input type="button" value="ตรวจสอบสถานะล่าสุด" class="newPosBtnLong"> 
										</a>
										<a href="javascript:clearBatchForm()">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>
					 
					 <!-- Disp BatchTask Lastest Run -->
		             <jsp:include page="/jsp/batchtask/batchTaskPopupResult.jsp"></jsp:include>
					<!-- hidden field -->

					<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
					<input type="hidden" name="path" id="path" value="${pageContext.request.contextPath}"/>
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