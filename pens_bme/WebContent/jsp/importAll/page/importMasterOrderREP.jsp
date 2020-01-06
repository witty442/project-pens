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
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<jsp:useBean id="importAllForm" class="com.isecinc.pens.web.importall.ImportAllForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<%
	String storeType ="lotus";
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

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript">

function loadMe(){
}
function importExcel(){
	var form = document.importAllForm;
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
	if(form.dataFile.value != ''){
		if(  extension != 'xls' && extension != 'xlsx'){
			 alert("กรุณาเลือกไฟล์นามสกุล .xls ,xlsx");
			return false;
		}
	}
	form.action = form.path.value + "/jsp/importAllAction.do?do=importExcel";
	form.submit();
	return true;
}
function clearForm(){
	var form = document.importAllForm;
	form.action = form.path.value + "/jsp/importAllAction.do?do=prepare&action=new";
	form.submit();
	return true;
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
								<td align="right" width="40%">เลือกไฟล์(replenishment_minmax)&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="dataFile" styleClass="" style="width:300px;height:21px"/>
									[xls,xlsx]
								</td>
							</tr>
							 <tr>
								<td align="right" width="40%">เลือกไฟล์ (replenishment_priority)&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="dataFile2" styleClass="" style="width:300px;height:21px"/>
									[xls,xlsx]
								</td>
							</tr> 
							 <tr>
								<td align="right" width="40%">เลือกไฟล์ (replenishment_mat_config)&nbsp;&nbsp;</td>
								<td valign="top" align="left">
									<html:file property="dataFile3" styleClass="" style="width:300px;height:21px"/>
									[xls,xlsx] ลบข้อมูลเดิมออกทั้งหมด (PENSBI.BME_CONFIG_REP)
								</td>
							</tr> 
						</table>
						
						<!-- Button -->
						<div align="center">
						   <input type="button" value="  Upload  " class="newPosBtnLong" onclick="javascript:importExcel()">
						   <input type="button" value="  Clear  " class="newPosBtnLong" onclick="javascript:clearForm()">
						</div>
						
						<!-- hidden field -->
						<input type="hidden" name="pageName" value="<%=request.getParameter("pageName") %>"/>
						<input type="hidden" name="storeType" id="storeType" value="<%=storeType%>"/>
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