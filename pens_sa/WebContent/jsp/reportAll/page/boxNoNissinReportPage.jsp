<%@page import="com.isecinc.pens.web.boxno.BoxNoBean"%>
<%@page import="com.isecinc.pens.web.reportall.ReportAllForm"%>
<%@page import="com.isecinc.pens.web.reportall.ReportAllBean"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%> 
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="reportAllForm" class="com.isecinc.pens.web.reportall.ReportAllForm" scope="session" />
<%
BoxNoBean bean = ((ReportAllForm)session.getAttribute("reportAllForm")).getBean().getBoxNoBean();
System.out.println("bean SalesZone:"+bean.getSalesZone());
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
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
<script type="text/javascript">

function loadMe(){
	var form = document.reportAllForm;
}

function setPeriodDate(periodDesc){
	var form = document.reportAllForm;
	//alert(periodDesc);
	form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}
function search(path){
	var form = document.reportAllForm;
    var period = form.period;  
	if(period.value ==""){ 
		alert("กรุณากรอกข้อมูล เดือน");
		period.focus();
		return false;
	}
	   
	form.action = path + "/jsp/reportAllAction.do?do=search&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(curPage){
	var form = document.reportAllForm;
	var path = document.getElementById("path").value;
	form.action = path + "/jsp/reportAllAction.do?do=search&curPage="+curPage;
	form.submit();
	return true;
}
function exportExcel(path){
	var form = document.reportAllForm;
	var period = form.period;  
	if(period.value ==""){ 
		alert("กรุณากรอกข้อมูล เดือน");
		period.focus();
		return false;
	}
	form.action = path + "/jsp/reportAllAction.do?do=export";
	form.submit();
	return true;
}
function printControlBoxNoReport(period,pdCode){
	var form = document.reportAllForm;
	var path = document.getElementById("path").value;
	var param = "&period="+period;
	    param +="&pdCode="+pdCode;
	    param +="&reportName=controlBoxNoReport";
	form.action = path + "/jsp/reportAllAction.do?do=printReport"+param;
	form.submit();
	return true;
}
function printControlBoxNoByZoneReport(){
	var form = document.reportAllForm;
	var path = document.getElementById("path").value;
	var period = document.getElementById("period");
	var salesZone = document.getElementById("salesZone");
	if(period.value ==""){ 
		alert("กรุณากรอกข้อมูล เดือน");
		period.focus();
		return false;
	}
	if(salesZone.value ==""){ 
		alert("กรุณากรอกข้อมูล ภาคการดูแล");
		salesZone.focus();
		return false;
	}
	
	var param = "&period="+period.value;
	    param +="&salesZone="+salesZone.value;
	    param +="&reportName=controlBoxNoByZoneReport";
	form.action = path + "/jsp/reportAllAction.do?do=printReport"+param;
	form.submit();
	return true;
}
function clearForm(path){
	var form = document.reportAllForm;
	form.action = path + "/jsp/reportAllAction.do?do=prepare&action=new";
	form.submit();
	return true;
}

function openPopup(path,pageName){
	var form = document.reportAllForm;
	var param = "";
	if("PDBoxNo" == pageName){
		param = "&pageName="+pageName+"&hideAll=true&selectone=true";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.reportAllForm;
	if("PDBoxNo" == pageName){
		form.pdCode.value = code;
	}
} 
/** disable enter key **/
$(document).keypress(
  function(event){
    if (event.which == '13') {
      event.preventDefault();
    }
});
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
						<html:form action="/jsp/reportAllAction">
						<jsp:include page="../../error.jsp"/>
						
						<div id="div_message" style="font-size:15px;color:green" align="center"></div> 
						<div id="div_error_message" style="font-size:15px;color:red" align="center"></div> 
						
					<div id="div_m" align="center">	
				    	<!-- ***** Criteria ******* -->
				    	<table align="center" border="0" cellpadding="3" cellspacing="0" >
					       <tr>
				                <td>  เดือน <font color="red">*</font></td>
								<td>		
									<html:select property="bean.boxNoBean.period" styleId="period" >
									   <html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
									</html:select>
								</td>
							</tr>
						   	 <tr>
				                <td>  ภาคการขาย  <font color="red"></font></td>
								<td>		
									 <html:select property="bean.boxNoBean.salesChannelNo" styleId="salesChannelNo">
										<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
								    </html:select>
								    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								       ภาคตามสายดูแล 
								      <html:select property="bean.boxNoBean.salesZone" styleId="salesZone">
										<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
								    </html:select>
								    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
								    PD
								    <html:text property="bean.boxNoBean.pdCode" styleId="pdCode" size="10" styleClass="\" autoComplete=\"off"/>
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','PDBoxNo')"/>   
								</td>
							</tr>
						</table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center" width="60%">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtnLong"> 
								</a>&nbsp;
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtnLong">
								</a>&nbsp;
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtnLong">
								</a>
								<a href="javascript:printControlBoxNoByZoneReport()">
								  <input type="button" value="พิมพ์ใบคุมรายภาค" class="newPosBtnLong">
								</a>
							</td>
							 <td align="right" width="40%" nowrap></td>
						</tr>
					</table>
			  </div>
			
		     <!-- ****** RESULT ***************************************************************** -->
		     <%
		      if(bean!= null  && bean.getDataStrBuffer() != null ){
		    	 out.println(bean.getDataStrBuffer().toString());
		      }
		     %>
		     <!-- ******************************************************************************* -->
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