<%@page import="java.util.Calendar"%>
<%@page import="com.pens.util.EnvProperties"%>
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
 String reportName = Utils.isNull(request.getParameter("reportName"));
 String startDate ="",endDate="",custGroup="";
 System.out.println("ReportName:"+reportName);
 if("BME_SALEOUT".equalsIgnoreCase(reportName)){
	 startDate = Utils.isNull(request.getParameter("startDate"));
	 endDate = Utils.isNull(request.getParameter("endDate"));
 }else  if("BME_CHECK_STOCK".equalsIgnoreCase(reportName)){
	 custGroup = Utils.isNull(request.getParameter("custGroup"));
 }
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
	document.getElementById("reportName").value ='<%=reportName%>'; 
	loadCritiria(document.getElementById("reportName"));
}
function submitBT(path){
	var reportName = document.getElementById("reportName").value;
	if(reportName ==""){
		alert("เลือกรายงานที่ต้องการ");
		reportName.focus();
		return false;
	}
	if("BME_SALEOUT" == reportName){
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
	}else if("BME_CHECK_STOCK" ==reportName){
		
	}
	document.jojoForm.action = path + "/jsp/jojoAction.do?do=search&reportName="+reportName;
	document.jojoForm.submit();
}
function submitExport(path){
	var reportName = document.getElementById("reportName").value;
	if("BME_SALEOUT" == reportName){
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
	}else if("BME_CHECK_STOCK" ==reportName){
		
	}
	document.jojoForm.action = path + "/jsp/jojoAction.do?do=export&reportName="+reportName;
	document.jojoForm.submit();
}
function loadCritiria(reportName){
	//hide all div
	document.getElementById("DIV_BME_SALEOUT").style.display  = "none";
	document.getElementById("DIV_BME_CHECK_STOCK").style.display  = "none";
	if("BME_SALEOUT"==reportName.value){
		document.getElementById("DIV_BME_SALEOUT").style.display  = "block";
		
		new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	    new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	}else if("BME_CHECK_STOCK"==reportName.value){
		document.getElementById("DIV_BME_CHECK_STOCK").style.display  = "block";
		document.getElementById("custGroup").value ='<%=custGroup%>'; 
	}
}
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();" style="height: 100%;">
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
                        <%--   <div align="center">
                          <img src="${pageContext.request.contextPath}/images2/jojo_manu.jpg" width="100" height="50"/>
                          </div> --%>
						   <table style="width:100%" border="0" align="center" class="text">
							
							  <tr>
							    <td width="30%" align="right"> เลือกรายงานที่ต้องการ:</td>
							    <td width="70%">
							       <select id="reportName" onchange="loadCritiria(this)">
							        <option value="">กรุณาเลือกรายงาน</option>
							        <option value="BME_SALEOUT">SaleOut BME</option>
							        <option value="BME_CHECK_STOCK">BME Check StockDate</option>
							      </select>
							     </td> 
							  </tr>
							  <tr>
							    <td colspan="2"  align="left">
							     <div id="DIV_BME_SALEOUT" style="display: none">
								     <table style="width:100%">
								       <tr>
									     <td width="30%" align="right">วันที่เริ่มต้น</td>
									     <td width="70%">
									     <input type="text" readonly name="startDate" id="startDate" value="<%=startDate%>"/><font color="red">*</font>-
									     <input type="text" readonly name="endDate" id="endDate" value="<%=endDate%>"/><font color="red">*</font>
									     </td>
									    </tr>
									   </table>
								   </div>
								   <div id="DIV_BME_CHECK_STOCK" style="display: none">
								     <table style="width:100%">
								       <tr>
									     <td width="30%" align="right">CustomerGroup</td>
									     <td width="70%">
									       <select name="custGroup" id="custGroup">
									        <option value="">กรุณากลุ่มร้านค้า</option>
									        <option value="020047">LOTUS</option>
									        <option value="020049">BIGC</option>
									      </select>
									     </td>
									    </tr>
									   </table>
								   </div>
							    </td>
							  </tr>
							  <tr>
							    <td width="100%" colspan="2" align="center">
							     <INPUT TYPE="button" class="newPosBtnLong" name ="B_QUERY" VALUE="Search" onclick="submitBT('<%=request.getContextPath()%>');">
							     <INPUT TYPE="button" class="newPosBtnLong" name ="B_QUERY" VALUE="Export" onclick="submitExport('<%=request.getContextPath()%>');">
							    </td>
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
