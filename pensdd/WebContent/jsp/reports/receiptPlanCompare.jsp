<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="receiptPlanCompareForm" class="com.isecinc.pens.web.report.receiptplancompare.ReceiptPlanCompareReportForm" scope="request" />
<%
List<References> paymentmethods = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentmethods", paymentmethods, PageContext.PAGE_SCOPE);
%>
<html>
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
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('shippingDateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('shippingDateTo'));
	new Epoch('epoch_popup','th',document.getElementById('confirmDateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('confirmDateTo'));
}

function gotoReport(path){
	if  ($('#shippingDateFrom').val()=='' && $('#shippingDateTo').val() == ''
	  && $('#confirmDateFrom').val()=='' && $('#confirmDateTo').val() == ''){
		alert("กรุณากรอกวันที่แผนรับเงิน หรือ วันที่ส่งสินค้าจริง อย่างใดอย่างหนึ่ง");
		return;
	}else{
		if( $('#shippingDateFrom').val()!='' ||  $('#shippingDateTo').val() != '' ){
			if($('#shippingDateFrom').val()=='' && $('#shippingDateTo').val() == ''){
				alert('กรุณาใส่ วันที่ตามแผนการรับเงิน ให้ครบ');
				$('#shippingDateFrom').focus();
				$('#shippingDateTo').focus();
				return;
			}
		}
		
		if( $('#confirmDateFrom').val()!='' ||  $('#confirmDateTo').val() != ''){
			if($('#confirmDateFrom').val()=='' && $('#confirmDateTo').val() == ''){
				alert('กรุณาใส่วันที่ส่งสินค้าจริให้ครบ');
				$('#confirmDateFrom').focus();
				$('#confirmDateTo').focus();
				return;
			}
		}
	}
	
	if(document.getElementById('msg')!=null){
		document.getElementById('msg').innerHTML = '';
	}
	document.receiptPlanCompareReportForm.action = path + "/jsp/receiptPlanCompareReport.do?do=searchReport&rf=Y";
	document.receiptPlanCompareReportForm.submit();
	return true;
}

function clearForm(path){
	document.receiptPlanCompareReportForm.action = path + "/jsp/receiptPlanCompareReport.do?do=clearForm";
	document.receiptPlanCompareReportForm.submit();
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
				<jsp:param name="function" value="ReceiptPlanCompareReport"/>
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
						<html:form action="/jsp/receiptPlanCompareReport">
						<jsp:include page="../error.jsp"/>
						<!-- CRITERIA -->
						<table style="text-align:center" align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="100%" colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td width="40%" align="right">วันที่ส่งสินค้าตามแผน   ตั้งแต่ :&nbsp;</td>
								<td align="left">
								<html:text property="receiptPlanCompareReport.shippingDateFrom" styleId="shippingDateFrom" readonly="true" size="15" /><font color="red">*</font>
								&nbsp;&nbsp;&nbsp;&nbsp;
								     ถึง :&nbsp;<html:text property="receiptPlanCompareReport.shippingDateTo" styleId="shippingDateTo" readonly="true" size="15"/><font color="red">*</font>
								     <br/>
								</td>
							</tr>
							<tr>
								<td width="40%" align="right">วันที่ส่งสินค้าจริง ตั้งแต่ :&nbsp;</td>
								<td align="left">
								<html:text property="receiptPlanCompareReport.confirmDateFrom" styleId="confirmDateFrom" readonly="true" size="15" /><font color="red">*</font>
								&nbsp;&nbsp;&nbsp;&nbsp;
								     ถึง :&nbsp;<html:text property="receiptPlanCompareReport.confirmDateTo" styleId="confirmDateTo" readonly="true" size="15"/><font color="red">*</font>
								     <br/>
								</td>
							</tr>
							
							<tr>
								<td width="40%" align="right">รหัสสมาชิก:&nbsp;</td>
								<td align="left">
									<html:text property="receiptPlanCompareReport.custCode" styleId="custCode" size="15" />
								</td>
							</tr>
							<tr>
								<td width="40%" align="right">ชื่อสมาชิก:&nbsp;</td>
								<td align="left">
									<html:text property="receiptPlanCompareReport.custName" styleId="custName" size="15" />
								</td>
							</tr>
							<tr>
								<td width="40%" align="right">วิธีการชำระเงิน :&nbsp;</td>
								<td align="left">
									<html:select property="receiptPlanCompareReport.param_PaymentMethod" styleId="paymentMethod">
										<html:option value=""></html:option>
										<html:options collection="paymentmethods" property="key" labelProperty="name" />
									</html:select>
								</td>
							</tr>
							<tr>
								<td  width="40%" align="right"><bean:message key="ReportFormat" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td  align="left">
									<html:radio property="criteria.fileType" value="XLS"/>&nbsp;<bean:message key="Excel" bundle="sysele"/>&nbsp;&nbsp;
									<html:radio property="criteria.fileType" value="PDF"/>&nbsp;<bean:message key="PDF" bundle="sysele"/>
								</td>
							</tr>
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="center">
									<a href="javascript:gotoReport('${pageContext.request.contextPath}')">
			<!--						<img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn">-->
									<input type="button" value="พิมพ์" class="newPosBtn"/>
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
			<!--						<img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn">-->
									<input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
			<!--						<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
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