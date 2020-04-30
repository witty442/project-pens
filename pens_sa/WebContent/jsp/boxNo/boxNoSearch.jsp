<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id="boxNoForm" class="com.isecinc.pens.web.boxno.BoxNoForm" scope="session" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title>	ใบปะหน้ากล่อง ของเสีย(Nissin)</title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript">

function loadMe(){
}
function search(path){
	var form = document.boxNoForm;
	
	form.action = path + "/jsp/boxNoAction.do?do=search&action=newsearch";
	form.submit();
	
	return true;
}

function gotoPage(currPage){
	var path = document.getElementById("path").value;
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=search&currPage="+currPage;
	document.boxNoForm.submit();
	return true;
}
function addNew(path){
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=prepareDetail&action=new";
	document.boxNoForm.submit();
	return true;
}
function view(action,period,pdCode,salesrepCode){
	var path = document.getElementById("path").value;
	var param ="&action="+action+"&period="+period;
	    param +="&pdCode="+pdCode;
	    param +="&salesrepCode="+salesrepCode;
	    
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=prepareDetail"+param;
	document.boxNoForm.submit();
	return true;
}
function printBoxNoReport(period,pdCode,salesrepCode){
	var path = document.getElementById("path").value;
	var param ="&period="+period;
	    param +="&pdCode="+pdCode;
	    param +="&salesrepCode="+salesrepCode;
	    
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=printBoxNoReport"+param;
	document.boxNoForm.submit();
	return true;
}
function clearForm(path){
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=clear";
	document.boxNoForm.submit();
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
			        	 <jsp:include page="../menu_blank.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="BoxNo"/>
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
					<html:form action="/jsp/boxNoAction">
					<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right" width="40%">สินค้าชำรุด/เสียหาย ของเดือน<font color="red"></font></td>
								<td align="left" width="60%">
								     <html:select property="bean.period" styleId="period">
										<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
								    </html:select>
								 
								</td>
							</tr>
							<%-- <tr>
							  <td align="right" width="40%">PD <font color="red">*</font></td>
								<td align="left" width="60%">
								     <html:select property="bean.pdCode" styleId="pdCode">
										<html:options collection="PD_LIST" property="value" labelProperty="keyName"/>
								    </html:select>
								</td>
							</tr> --%>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">						
								  <input type="button" value="    ค้นหา      " class="newPosBtnLong" onclick="search('${pageContext.request.contextPath}')"> 
							      <input type="button" value="    เพิ่มรายการใหม่      " class="newPosBtnLong" onclick="addNew('${pageContext.request.contextPath}')"> 
								  <input type="button" value="     Clear     " class="newPosBtnLong" onclick="clearForm('${pageContext.request.contextPath}')">
							</td>
						</tr>
					</table>
					<!-- RESULT -->
			        <%
			        if(request.getAttribute("boxNoForm_RESULTS") != null){
			           out.println( ((StringBuffer)request.getAttribute("boxNoForm_RESULTS")).toString()); 
			        }
			        %>
			        <input type="hidden" name ="path" id="path" value="${pageContext.request.contextPath}"/>
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