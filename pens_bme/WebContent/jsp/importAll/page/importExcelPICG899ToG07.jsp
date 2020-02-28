<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.dao.ImportDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="importAllForm" class="com.isecinc.pens.web.importall.ImportAllForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<%
	User user = (User)session.getAttribute("user");
	String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
	String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));
	String pageName = Utils.isNull(request.getParameter("pageName"));
	String hideAll = "true";
%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

<script type="text/javascript">
function loadMe(){
	/** for popup BatchTask in page **/
	 <%if( !"".equals(Utils.isNull(request.getAttribute("BATCH_TASK_NAME")))){%>
	    //lockscreen
	    var path = document.getElementById("path").value;
	    /** Init progressbar **/
		$(function() {
			// update the block message 
	        $.blockUI({ message: "<h2>กำลังทำรายการ     กรุณารอสักครู่......</h2>" }); 
		}); 
		    
		//submitedGenStockOnhandTemp
		var url = path+'/jsp/batchTaskAction.do?do=prepare&pageAction=new&initBatchAction=initBatchFromPageByPopup&pageName=<%=Utils.isNull(request.getAttribute("BATCH_TASK_NAME"))%>';
		popupFull(url,'<%=Utils.isNull(request.getAttribute("BATCH_TASK_NAME"))%>');
    <%}%>
}
function searchBatch(){
	//unlockScreen
	setTimeout($.unblockUI, 100); 
	 
	var form = document.importAllForm;
	var path = form.path.value;
	form.action = path + "/jsp/importAllAction.do?do=searchBatch";
	form.submit();
	return true;
}
function searchBatchForm(){
	var path = document.getElementById("path").value;
	var form = document.importAllForm;
	form.action = path + "/jsp/importAllAction.do?do=searchBatchForm";
	form.submit();
	return true;
}
function clearBatchForm(){
	var path = document.getElementById("path").value;
	var form = document.importAllForm;
	form.action = path + "/jsp/importAllAction.do?do=clearBatchForm";
	form.submit();
	return true;
}
function importExcel(){
	var form = document.importAllForm;
	var extension = '';
	var startFileName = '';
	if(form.dataFile.value.indexOf(".") > 0){
		extension = form.dataFile.value.substring(form.dataFile.value.lastIndexOf(".") + 1).toLowerCase();
		//alert(extension);
	}
	if(form.dataFile.value != ''){
		if(  extension != 'xls' && extension != 'xlsx'){
			 alert("กรุณาเลือกไฟล์นามสกุล .xls ,xlsx");
			return false;
		}
	}else{
		alert("กรุณาเลือกไฟล์ เพื่อทำการ Upload");
		return false;
	}
	if(form.subInv.value ==""){
		alert("ไม่พบ Sub Inv หรือคลังที่จะโอน");
		form.storeCode.focus();
		return false;
	}
    if(confirm("ยืนยันทำรายการ")){
		form.action = form.path.value + "/jsp/importAllAction.do?do=importExcel";
		form.submit();
		return true;
    }
}
function clearForm(){
	var form = document.importAllForm;
	form.action = form.path.value + "/jsp/importAllAction.do?do=prepare&action=new";
	form.submit();
	return true;
}
function findStore(pageName){
	var form = document.importAllForm;
	var storeCode = document.getElementById("storeCode");
	if(storeCode.value != ""){
	    getAutoDetail(storeCode,pageName);
	}else{
		form.subInv.value = "";
	}
}
function getAutoDetail(obj,pageName){
	var returnString = "";
	var form = document.importAllForm;
	
	//prepare parameter
	var param = "";
	if("Store"==pageName){
		param  ="pageName="+pageName;
		param +="&storeCode="+obj.value;
	}
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getAutoKeypressAjax.jsp",
			data : param,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	 
	if("Store" == pageName){
		var retArr = returnString.split("|");
		if(retArr[0] !=-1){
			form.storeCode.value = retArr[1];
			//form.storeName.value = retArr[2];
			//form.storeNo.value = retArr[3];
			form.subInv.value = retArr[4];
		}else{
			alert("ไม่พบข้อมูล");
			form.storeCode.focus();
			form.storeCode.value = '';
			form.subInv.value = "";
		}
	}
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
			<jsp:include page="../../program.jsp">
				<jsp:param name="function" value="<%=pageName %>"/>
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
						<html:form action="/jsp/importAllAction" enctype="multipart/form-data">
						<jsp:include page="../../error.jsp"/>
						<!-- Criteria -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						    <tr>
								<td align="right" width="40%">รหัสร้านค้าใน B'me System ที่ทำการเบิก &nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:text property="pageBean.importExcelInvBillTBean.storeCode" styleId="storeCode" styleClass="\" autoComplete=\"off" size="10"/>
									 <input type="button" value="ค้นหา" class="newPosBtnLong" onclick="javascript:findStore('Store')">
									 &nbsp;&nbsp;
									SubInv
									<html:text property="pageBean.importExcelInvBillTBean.subInv" styleId="subInv" styleClass="disableText" readonly="true"  size="10"/>
									
								</td>
							</tr> 
							 <tr>
								<td align="right" width="40%">เลือกไฟล์ &nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="pageBean.importExcelInvBillTBean.dataFile" styleId="dataFile" styleClass="" style="width:300px;height:21px"/>
									[xls,xlsx] 
								</td>
							</tr> 
						</table>
						
						<!-- Button -->
						<div align="center">
						   <input type="button" value="  Upload  " class="newPosBtnLong" onclick="javascript:importExcel()">
						   <input type="button" value="  Clear  " class="newPosBtnLong" onclick="javascript:clearForm()">
						</div>
						<div align="center">
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
						<input type="hidden" name="pageName" value="<%=pageName %>"/>
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
   			<jsp:include page="../../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>