<%@page import="com.isecinc.pens.report.salesanalyst.helper.Utils"%>
<%@page import="com.isecinc.pens.web.location.LocationInitial"%>
<%@page import="util.SIdUtils"%>
<%@page import="com.isecinc.pens.bean.PopupBean"%>
<%@page import="util.GoogleMapJavaScriptAPI"%>
<%@page import="java.util.ArrayList"%>
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
<jsp:useBean id="locationForm" class="com.isecinc.pens.web.location.LocationForm" scope="session" />
<%
if(request.getParameter("action") != null){
    LocationInitial.getInstance().initSession(request);
 }

String typeSearch = Utils.isNull(request.getAttribute("DATA"));
java.util.List yearList = null;
if(session.getAttribute("yearList") != null) 
	yearList = (java.util.List)session.getAttribute("yearList");

java.util.List yearListASC = null;
if(session.getAttribute("yearListASC") != null) 
	yearListASC = (java.util.List)session.getAttribute("yearListASC");

String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/page/location.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('day'));
	new Epoch('epoch_popup','th',document.getElementById('dayTo'));
	chkSearch();
	chkYear();
}
function chkSearch(){
	   var typeSearch = document.getElementsByName("bean.typeSearch")[0];
	   //alert(typeSearch.value);
		disabledObj(document.getElementsByName("bean.day")[0] ,false);
		disabledObj(document.getElementsByName("bean.dayTo")[0] ,false);
		
		var monthList = document.getElementsByName("bean.chkMonth");
		for(i=0;i<monthList.length;i++){
	       disabledObj(document.getElementsByName("bean.chkMonth")[i],false);
	    }
		
	   if(typeSearch.value == 'DAY'){
		   for(i=0;i<monthList.length;i++){
		      disabledObj(document.getElementsByName("bean.chkMonth")[i],true);
		   }
		   
	    }else  if(typeSearch.value == 'MONTH'){
	       disabledObj(document.getElementsByName("bean.day")[0] ,true);
	       disabledObj(document.getElementsByName("bean.dayTo")[0] ,true);
	    }
	}

function chkYear(){
	var year = $('select#yearList').val();
	var yearList = $('select#yearList option');

	//Month
	for(var i=0; i<yearList.size();i++){
		if(yearList[i].value == year){
			$('tr#'+yearList[i].value).show();
		}
		else{
			$('tr#'+yearList[i].value).hide();
		}
	}		
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
				<jsp:param name="function" value="Location"/>
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
						<html:form action="/jsp/locationAction">
						<jsp:include page="../error.jsp"/>
						<table width="80%" border="0" align="center" cellpadding="3" cellspacing="1">
							<tr>
							    <td colspan=8 align="left"><b>ข้อมูล ณ วันที่ &nbsp;:&nbsp;<%=(String)session.getAttribute("maxOrderedDate")%>&nbsp;&nbsp;เวลา&nbsp;:<%=(String)session.getAttribute("maxOrderedTime")%></b></td>
							 </tr>
							  <tr>
								  <td colspan="8" align="right">
								    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="1">
									<tr class="txt_style" >
								  	<td width="13%" align="left">รอบเวลา &nbsp;&nbsp;<html:select property="bean.typeSearch" onchange="chkSearch()" styleClass="txt_style">
								         <html:options collection="typeSearchList" property="key" labelProperty="name"/>
							           </html:select>
							           </td>
								     <td width="6%" align="right">วันที่</td>
								     <td width="27%" align="left">
								      <html:text property="bean.day" readonly="true" styleId="day" size="15"/>
								      &nbsp;&nbsp;-&nbsp;&nbsp;
								      <html:text property="bean.dayTo" readonly="true" styleId="dayTo" size="15"/>
                                     </td> 
									<td width="2%" align="right">ปี</td>
									<td width="6%" align="left">
									  <html:select property="bean.year" styleId="yearList" onchange="chkYear()" styleClass="txt_style">
								         <html:options collection="yearList" property="key" labelProperty="name"/>
						              </html:select>
									</td>
								    <td width="8%" align="right"></td>
								    <td width="20%" align="left"></td>
								     </tr>
								   </table>
								</td></tr>
								<tr><td colspan="8" align="left">
								     <fieldset>
								       <legend>เดือน</legend>
									    <table width="100%" border="0">
									    <c:forEach var="item" items="${yearList}" >
                                         <tr id="${item.key}" class="txt_style" >
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}01</html:multibox>ม.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}02</html:multibox>ก.พ.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}03</html:multibox>มี.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}04</html:multibox>เม.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}05</html:multibox>พ.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}06</html:multibox>มิ.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}07</html:multibox>ก.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}08</html:multibox>ส.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}09</html:multibox>ก.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}10</html:multibox>ต.ค.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}11</html:multibox>พ.ย.</td>
                                          <td width="5%"><html:multibox  property="bean.chkMonth">${item.key}12</html:multibox>ธ.ค.</td>
                                         </tr>
                                         </c:forEach>
                                       </table>
                                       </fieldset>
                                 </td></tr>
							 </table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<input type="button" value="ค้นหา" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
								<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
								
								  <a href="#" onclick="return MarkLocationMap('${pageContext.request.contextPath}');">
									<input type="button" value="แสดงร้านค้าทั้งหมดบน แผนที่" class="newPosBtn">
								</a> 
								
							</td>
						</tr>
					</table>				
                    
										
				<!-- Result -->	
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						<tr>
							<td align="right">
								<a href="#" onclick="window.location='./mainpage.jsp'">
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								<!-- <img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"> --></a>
							</td>
							<td width="10%">&nbsp;</td>
						</tr>
					</table>
					<!-- Hidden Field -->
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
