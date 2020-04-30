<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="boxNoForm" class="com.isecinc.pens.web.boxno.BoxNoForm" scope="session" />
<%
User user = ((User)session.getAttribute("user"));
String userName = user.getUserName();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title>	ใบปะหน้ากล่อง ของเสีย(Nissin)</title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.0.js"></script>
<script type="text/javascript">
function loadMe(){
	//set Period Desc
	setPeriodDesc(document.getElementById("period"));
	
	var period = document.getElementById("period");
	var pdCode = document.getElementById("pdCode");
	var totalBox = document.getElementById("totalBox");
	//disable Input By Mode
	<%if("edit".equalsIgnoreCase(boxNoForm.getMode())){%>
	    period.disabled = true;
	   // pdCode.disabled = true;
	<%}else if("view".equalsIgnoreCase(boxNoForm.getMode())){%>
	    period.disabled = true;
	    pdCode.disabled = true;
	    totalBox.disabled = true;
	<%}%>
}
function save(path) {
	var period = document.getElementById("period");
	var pdCode = document.getElementById("pdCode");
	var totalBox = document.getElementById("totalBox");
	if(period.value ==""){
		alert("กรุณาระบุ เดือน");
		period.focus();
		return false;
	}
	if(pdCode.value ==""){
		alert("กรุณาระบุ PD");
		pdCode.focus();
		return false;
	}
	if(totalBox.value ==""){
		alert("กรุณาระบุ จำนวนกล่องของเสีย");
		totalBox.focus();
		return false;
	}
	/**  Control Save Lock Screen **/
	startControlSaveLockScreen();
	
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=save";
	document.boxNoForm.submit();
}
function backsearch(path) {
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=prepare"+"&action=back";
	document.boxNoForm.submit();
}
function setPeriodDesc(sel){
	document.getElementById("periodDesc").value = sel.options[sel.selectedIndex].text;
}
function printBoxNoReport(){
	var path = document.getElementById("path").value;
	
	var period= document.getElementById("period").value;
	var pdCode= document.getElementById("pdCode");
	var salesrepCode= document.getElementById("salesrepCode").value;
	var param ="&actionDB=saveDB";
	    param +="&period="+period;
	    param +="&pdCode="+pdCode.value;
	    param +="&salesrepCode="+salesrepCode;
	    
	var totalBox = document.getElementById("totalBox");
	if(pdCode.value ==""){
		alert("กรุณาระบุ PD");
		pdCode.focus();
		return false;
	}
	if(totalBox.value ==""){
		alert("กรุณาระบุ จำนวนกล่องของเสีย");
		totalBox.focus();
		return false;
	}
	
	document.boxNoForm.action = path + "/jsp/boxNoAction.do?do=printBoxNoReport"+param;
	document.boxNoForm.submit();
	return true;
}
/** disable enter key **/
$(document).keypress(
  function(event){
    if (event.which == '13') {
      event.preventDefault();
    }
});
function openPopup(path,pageName){
	var form = document.boxNoForm;
	var param = "";
	if("PDBoxNo" == pageName){
		param = "&pageName="+pageName+"&hideAll=true&selectone=true";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.boxNoForm;
	if("PDBoxNo" == pageName){
		form.pdCode.value = code;
		form.pdDesc.value = desc;
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
			        	<jsp:include page="../menu_blank.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="BoxNo"/>
				<jsp:param name="code" value=""/>
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
		
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
					
						     <tr>
								<td align="center" colspan="2">
								   <font size="3"><b>
									   <%if("edit".equalsIgnoreCase(boxNoForm.getMode())){%>
									                แก้ไข ใบปะหน้ากล่อง ของเสีย(Nissin)
									    <%}else if("add".equalsIgnoreCase(boxNoForm.getMode())){%>
									                 เพิ่ม ใบปะหน้ากล่อง ของเสีย(Nissin)
									    <%}else if("view".equalsIgnoreCase(boxNoForm.getMode())){%>
									                 แสดง ใบปะหน้ากล่อง ของเสีย(Nissin)
									    <%} %>
									    </b>
								    </font>
								</td>
								
							</tr>
						    <tr>
								<td align="right" width="40%">สินค้าชำรุด/เสียหาย ของเดือน<font color="red">*</font></td>
								<td align="left" width="60%">
								     <html:select property="bean.period" styleId="period" onchange="setPeriodDesc(this)">
										<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
								    </html:select>
								    <html:hidden property="bean.periodDesc" styleId="periodDesc"/>
								    <html:hidden property="bean.salesrepCode" styleId="salesrepCode"/>
								</td>
							</tr>
							<tr>
							  <td align="right" width="40%">ระบุ PD ที่วางของเสีย(เพื่อให้ขนส่งมารับ)<font color="red">*</font></td>
								<td align="left" width="60%">
								<%if(user.getUserGroupName().equalsIgnoreCase("Credit Sales")) {%>
								    <html:text property="bean.pdCode" styleId="pdCode" size="10" styleClass="\" autoComplete=\"off" readonly="true"/>
								    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','PDBoxNo')"/>   
								    <html:text property="bean.pdDesc" styleId="pdDesc" size="40" styleClass="disableText" readonly="true"/>
								<%}else{ %>
								     <html:select property="bean.pdCode" styleId="pdCode">
										<html:options collection="PD_LIST" property="value" labelProperty="keyName"/>
								    </html:select>
								<%} %>
								
								<html:hidden property="bean.pdCodeKey"/>
								</td>
							</tr>
							<tr>
							  <td align="right" width="40%">จำนวนกล่องของเสียที่จะให้ขนส่งมารับ<font color="red">*</font></td>
								<td align="left" width="60%">
								    <html:text property="bean.totalBox" styleClass="enableNumber\" autoComplete=\"off" 
								    styleId="totalBox" onblur="isNum(this)"></html:text> &nbsp;กล่อง
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								   <input type="button" value="พิมพ์ใบปะหน้ากล่อง" class="newPosBtnLong" onclick="printBoxNoReport();">	
								   &nbsp;
								   <c:if test="${boxNoForm.bean.canEdit =='true'}">
									  <input type="button" value="บันทึกข้อมูล" class="newPosBtnLong" onclick="save('${pageContext.request.contextPath}');">	
								      &nbsp;
								   </c:if>		
								  <input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong" onclick="backsearch('${pageContext.request.contextPath}');">

								</td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
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

 <!-- Control Save Lock Screen -->
<jsp:include page="../controlSaveLockScreen.jsp"/>
