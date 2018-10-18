<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.inf.helper.EnvProperties"%>
<%@page import="com.pens.util.*"%>
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
<jsp:useBean id="jojoForm" class="com.isecinc.pens.web.jojo.JojoForm" scope="request" />
 <%
 EnvProperties env = EnvProperties.getInstance();
 String startDate = Utils.isNull(request.getParameter("startDate"));
 String endDate = Utils.isNull(request.getParameter("endDate"));
 
 if(Utils.isNull(startDate).equals("")){
	 //default 01/01/+currentYear
	 Calendar c= Calendar.getInstance(Utils.local_th);
	 startDate = "01/01/"+c.get(Calendar.YEAR);
 }
 %>
<html>
<head>
<title>Jojo ล่าข้ามศตวรรษ</title>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<style type="text/css">
.text {
    color: blue;
    font-weight:bold;
    font-family: Tahoma;
    font-size: 25px;
}
</style>

<script>
function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	 new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
}
function submitBT(path){
	var reportType = document.getElementById("reportType").value;
	if("BME_SALEOUT" == reportType){
		var startDate = document.getElementById("startDate");
		var endDate = document.getElementById("endDate");
		if(startDate.value ==""){
			alert("กรุณาระบุวันที่ เริ่มต้น");
			startDate.focus();
			return false;
		}
		if(endDate.value ==""){
			alert("กรุณาระบุวันที่ สิ้นสุด");
			endDate.focus();
			return false;
		}
	}
	document.jojoForm.action = path + "/jsp/jojoAction.do?do=search&reportType="+reportType;
	document.jojoForm.submit();
}
function submitExport(path){
	var reportType = document.getElementById("reportType").value;
	if("BME_SALEOUT" == reportType){
		var startDate = document.getElementById("startDate");
		var endDate = document.getElementById("endDate");
		if(startDate.value ==""){
			alert("กรุณาระบุวันที่ เริ่มต้น");
			startDate.focus();
			return false;
		}
		if(endDate.value ==""){
			alert("กรุณาระบุวันที่ สิ้นสุด");
			endDate.focus();
			return false;
		}
	}
	document.jojoForm.action = path + "/jsp/jojoAction.do?do=export&reportType="+reportType;
	document.jojoForm.submit();
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
				<jsp:param name="function" value="JojoReport"/>
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
						<html:form action="/jsp/jojoAction">
						<jsp:include page="../error.jsp"/>

						   <table style="width:100%" border="0" align="center" class="text">
							
							  <tr>
							    <td width="30%" align="right"> เลือกรายงานที่ต้องการ:</td>
							    <td width="30%"><select id="reportType">
							        <option value="BME_SALEOUT">SaleOut BME</option>
							      </select>
							     </td> 
							     <td width="30%" align="right"></td>
							  </tr>
							  <tr>
							    <td colspan="3"  align="center">
							     <table style="width:100%">
							       <tr>
								     <td width="30%" align="right">วันที่เริ่มต้น</td>
								     <td width="70%">
								     <input type="text" name="startDate" id="startDate" value="<%=startDate%>"/><font color="red">*</font>-
								     <input type="text" name="endDate" id="endDate" value="<%=endDate%>"/><font color="red">*</font>
								     </td>
								    </tr>
								   </table>
							    </td>
							  </tr>
							  <tr>
							    <td width="30%" align="right">
							     <INPUT TYPE="button" class="newPosBtnLong" name ="B_QUERY" VALUE="Search" onclick="submitBT('<%=request.getContextPath()%>');">
							     <INPUT TYPE="button" class="newPosBtnLong" name ="B_QUERY" VALUE="Export" onclick="submitExport('<%=request.getContextPath()%>');">
							    </td>
							    <td></td>
							     <td></td>
							  </tr>
							</table>
					        
					        <%if( request.getAttribute("DATA_JOJO") != null) {
					           out.println((String)request.getAttribute("DATA_JOJO"));
					        }
					        %>
					<!-- hidden field -->
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
