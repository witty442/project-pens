<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String notFound = InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc();
String saveSuccess = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
String saveFail = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="com.isecinc.pens.init.InitialMessages"%>
<%@page import="com.isecinc.core.bean.Messages"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">

function goSearch(path){
	$('#spnWait').show();
	search(path);
}

function search(path){
	//Ajax Search
	$(function(){
		var getData = $.ajax({
			url: path+"/jsp/dataimports/memberImportSearchResult.jsp",
			data : "code=" + $('#membercode').val(),
			async: true,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				$('#searchResult').html(returnString);
				$('#spnWait').hide();
			}
		}).responseText;
	});
}

function autoReceipt(orderId,memberId,amt){
	if(!confirm("ยืนยันการรับชำระเงิน จำนวน "+amt))return false;
	var divId = 'order_'+orderId;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/mior",
			data : "orderId=" + orderId + "&memberId="+memberId,
			async: false,
			success: function(getData){
				var returnString =  jQuery.trim(getData);
				if(returnString.substring(0,1)=='S'){
					alert('<%=saveSuccess%>');
					var k = returnString.split("_"); 
					$('#'+divId).html('&nbsp;เลขที่รับชำระ '+k[2]);
				}else{
					alert('<%=saveFail%>');
				}
			}
		}).responseText;
	});	
}

function viewOrder(id){
	window.open("${pageContext.request.contextPath}/jsp/saleOrderAction.do?do=preparePopup&id="+id, "ViewOrder", "width=939,height=430,location=No,resizable=No");
}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
				<jsp:param name="function" value="MemberInfo"/>
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
						<form name="memberImportSearch" onsubmit="return false;">
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td width="45%" align="right"><bean:message key="Member.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input type="text" id="membercode" name="membercode">
									<span id="spnWait" style="display: none;"><img src="${pageContext.request.contextPath}/icons/waiting.gif" align="absmiddle" border="0"/></span>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<input type="button" value="ค้นหา" class="newPosBtn" onclick="javascript:goSearch('${pageContext.request.contextPath}')">
								</td>
							</tr>
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<div id="searchResult" style="width: 800px;"></div>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn"></a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						</form>
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