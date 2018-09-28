<%@page import="com.isecinc.pens.dao.RTDAO"%>
<%@page import="com.isecinc.pens.bean.RTBean"%>
<%@page import="com.isecinc.pens.web.rt.RTForm"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.isecinc.pens.web.popup.PopupForm"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>

<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="rtForm" class="com.isecinc.pens.web.rt.RTForm" scope="session" />

<%
User user = (User) request.getSession().getAttribute("user");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
span.pagebanner {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	margin-top: 10px;
	display: block;
	border-bottom: none;
	font-size: 15px;
}

span.pagelinks {
	background-color: #eee;
	border: 1px dotted #999;
	padding: 4px 6px 4px 6px;
	width: 99%;
	display: block;
	border-top: none;
	margin-bottom: -1px;
	font-size: 15px;
}

.day {
  width: 14%;
}
.holiday {
  width: 14%;
  background-color: #F78181;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('picRcvDate'));
}
function clearForm(path){
	var form = document.rtForm;
	form.action = path + "/jsp/rtAction.do?do=clear";
	form.submit();
	return true;
}

function back(path){
	var form = document.rtForm;
	form.action = path + "/jsp/rtAction.do?do=prepare2&action=back&page=pic";
	form.submit();
	return true;
}

function savePic(path){
	var form = document.rtForm;
	/* if( $('#custGroup').val()==""){
		alert("กรุณาระบุ กลุ่มร้านค้า");
		return false;
	}
	if( $('#storeCode').val()==""){
		alert("กรุณาระบุ ร้านค้า");
		return false;
	} */
	if(confirm("กรุณายืนยันการบันทึกยอดรับคืน")){
		form.action = path + "/jsp/rtAction.do?do=savePic&action=newsearch";
		form.submit();
		return true;
	}
	return false;
}
function cancelAction(path){
	var form = document.rtForm;
	if(confirm("กรุณายืนยันการ ยกเลิก Authorize Return no นี้")){
		form.action = path + "/jsp/rtAction.do?do=cancelAction";
		form.submit();
		return true;
	}
	return false;
}
function completeAction(path){
	var form = document.rtForm;
	if(confirm("กรุณายืนยันการ Complete ข้อมูล  จะแก้ไขไม่ได้อีกแล้ว")){
		form.action = path + "/jsp/rtAction.do?do=completeAction";
		form.submit();
		return true;
	}
	return false;
}

function openPopupCustomer(path,types,storeType){
	var form = document.rtForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.rtForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.rtForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
			}
		}else{
		  getCustName(custCode,fieldName);
		}
	}
}

function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.rtForm;
	var storeGroup = form.custGroup.value;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/getCustNameWithSubInvAjax.jsp",
				data : "custCode=" + custCode.value+"&storeGroup="+storeGroup,
				async: false,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		
		if("storeCode" == fieldName){
			if(returnString !=''){
				var retArr = returnString.split("|");
				form.storeName.value = retArr[0];

			}else{
				alert("ไม่พบข้อมูล");
				form.storeCode.focus();
				form.storeCode.value ="";
				form.storeName.value = "";
				
			}
		}
}

function resetStore(){
	var form = document.rtForm;
	var storeGrouptext = $("#custGroup option:selected").text();
	
	if(storeGrouptext != ''){
		form.storeCode.value = "";
		form.storeName.value = "";
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
				<jsp:param name="function" value="picRT"/>
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
						<html:form action="/jsp/rtAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
						      <tr>
                                    <td> วันที่</td>
									<td>		
										 <html:text property="bean.docDate" styleClass="disableText" readonly="true" styleId="docDate"></html:text>
									</td>
									<td>
										 <font size="3"><b>Authorize Return No</b>
										 <html:text property="bean.docNo" styleClass="disableTextBigBold" readonly="true" styleId="docNo"></html:text>
										 </font>
									</td>
								</tr>
								<tr>
                                    <td> กลุ่มร้านค้า<font color="red">*</font> </td>
									<td>		
										 <html:select property="bean.custGroup" styleId="custGroup" styleClass="disableText" disabled="true">
											<html:options collection="custGroupList" property="code" labelProperty="desc"/>
									    </html:select>
									</td>
									<td></td>
								</tr>
								<tr>
									<td >รหัสร้านค้า<font color="red">*</font></td>
									<td align="left" colspan="2"> 
									  <html:text property="bean.storeCode" styleClass="disableText" readonly="true"  styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
									<%--   <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/> --%>
									  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="30"/>
									</td>
								</tr>
								<tr>
                                    <td> เล่ม/เลขที่<font color="red"></font></td>
									<td>		
										<html:text property="bean.refDoc" styleId="refDoc" styleClass="disableText" readonly="true" size="30"></html:text>
									</td>
									 <td> RTN No ของห้าง 
										<html:text property="bean.rtnNo" styleId="rtnNo" styleClass="disableText" readonly="true"  size="30"></html:text>
									</td>
								</tr>
								<tr>
                                    <td> จำนวนหีบตาม RTN<font color="red"></font></td>
									<td>		
										<html:text property="bean.rtnQtyCTN" styleId="rtnQtyCTN" styleClass="disableText" readonly="true" size="30"></html:text>
									</td>
									<td>จำนวนชิ้นตาม RTN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<html:text property="bean.rtnQtyEA" styleId="rtnQtyEA" styleClass="disableText" readonly="true" size="30"></html:text>
									</td>
								</tr>
								<tr>
                                    <td> ชื่อขนส่งที่ไปรับจากห้าง<font color="red"></font></td>
									<td  colspan="2">		
										<html:text property="bean.deliveryBy" styleId="deliveryBy" styleClass="disableText" readonly="true" size="50"/>
										
										วันที่นัดมาส่งของที่ PD
										<html:text property="bean.deliveryDate" styleId="deliveryDate" styleClass="disableText" readonly="true" size="10" />
									       จำนวนหีบสินค้าที่จัดส่ง
									    <html:text property="bean.deliveryQty" styleId="deliveryQty" styleClass="disableText" readonly="true"size="20" />
									</td>
								</tr>
					
								<tr>
                                    <td> สิ่งอื่นที่ส่งมาเพิ่มเติม 1<font color="red"></font></td>
									<td  colspan="2">		
										<html:text property="bean.attach1" styleId="attach1" styleClass="disableText" readonly="true" size="100" />
									</td>
								</tr>
								<tr>
                                    <td>สิ่งอื่นที่ส่งมาเพิ่มเติม 2<font color="red"></font></td>
									<td  colspan="2">		
										<html:text property="bean.attach2" styleId="attach2" styleClass="disableText" readonly="true" size="100" />
									</td>
								</tr>
								<tr>
                                    <td> สิ่งอื่นที่ส่งมาเพิ่มเติม 3<font color="red"></font></td>
									<td  colspan="2">		
										<html:text property="bean.attach3" styleId="attach3" styleClass="disableText" readonly="true" size="100"/>
									</td>
								</tr>
								<tr>
                                    <td> สิ่งอื่นที่ส่งมาเพิ่มเติม 4<font color="red"></font></td>
									<td  colspan="2">		
										<html:text property="bean.attach4" styleId="attach4" styleClass="disableText" readonly="true"size="100" />
									</td>
								</tr>
								<tr>
                                    <td>วันที่ PIC รับสินค้า<font color="red"></font></td>
									<td>		
										<html:text property="bean.picRcvDate" styleId="picRcvDate" styleClass="normalText" size="30" readonly="true"></html:text>
									</td>
								</tr>
								<tr>
                                    <td> จำนวนหีบ<font color="red"></font></td>
									<td  colspan="2">		
										<html:text property="bean.picRcvQtyCTN" styleId="picRcvQtyCTN" styleClass="normalText" size="30" onkeydown="return inputNum(event);"></html:text>
									 จำนวนชิ้น <font color="red"></font>
								
										<html:text property="bean.picRcvQtyEA" styleId="picRcvQtyEA" styleClass="normalText" size="30"  onkeydown="return inputNum(event);"></html:text>
									</td>
								</tr>
								<tr>
                                    <td> หมายเหตุของทีม Pic<font color="red"></font></td>
									<td  colspan="2">		
										<html:text property="bean.remarkTeamPic" styleId="remarkTeamPic" styleClass="normalText" size="100" maxlength="200"/>
									</td>
								</tr>
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
								    	
										<c:if test="${rtForm.bean.canPicSave == true}">
											<a href="javascript:savePic('${pageContext.request.contextPath}')">
											  <input type="button" value="    บันทึก รับสินค้า  " class="newPosBtnLong"> 
											</a>
										</c:if>
										
										<a href="javascript:back('${pageContext.request.contextPath}','','add')">
										  <input type="button" value="   ปิดหน้าจอ   " class="newPosBtnLong">
										</a>		
											
									</td>
								</tr>
							</table>
					  </div>
					<%-- <jsp:include page="../searchCriteria.jsp"></jsp:include> --%>
					
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