<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>

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

<jsp:useBean id="autoOrderForm" class="com.isecinc.pens.web.autoorder.AutoOrderForm" scope="session" />

<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "autoOrderForm");

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<style type="text/css">
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	 new Epoch('epoch_popup', 'th', document.getElementById('salesDateFrom'));
	 new Epoch('epoch_popup', 'th', document.getElementById('salesDateTo'));
}

function genAutoOrder(path){
	var form = document.autoOrderForm;
	var storeCode =$('#storeCode');
	if(storeCode.val() ==""){
		alert("กรุณาเลือก ร้านค้า");
		storeCode.focus();
		return false;
	}
	form.action = path + "/jsp/autoOrderAction.do?do=genAutoOrder";
	form.submit();
	return true;
}

function clearForm(path){
	var form = document.orderForm;
	form.action = path + "/jsp/autoOrderAction.do?do=prepareSearch&action=new";
	form.submit();
	return true;
}
function openPopupCustomer(path,types,storeType){
	var form = document.autoOrderForm;
	var storeGroup = form.custGroup.value;
	
    var param = "&types="+types;
        param += "&storeType="+storeType;
        param += "&storeGroup="+storeGroup;
    
	url = path + "/jsp/searchCustomerPopupAction.do?do=prepare3&action=new"+param;
	window.open(encodeURI(url),"",
			   "menubar=no,resizable=no,toolbar=no,scrollbars=yes,width=600px,height=540px,status=no,left="+ 50 + ",top=" + 0);
}

function setStoreMainValue(code,desc,storeNo,subInv,types){
	var form = document.autoOrderForm;
	//alert(form);
	form.storeCode.value = code;
	form.storeName.value = desc;
} 

function getCustNameKeypress(e,custCode,fieldName){
	var form = document.autoOrderForm;
	if(e != null && e.keyCode == 13){
		if(custCode.value ==''){
			if("storeCode" == fieldName){
				form.storeCode.value = '';
				form.storeName.value = "";
				form.storeNo.value = "";
				form.subInv.value = "";
			}
		}else{
		  getCustName(custCode,fieldName);
		}
	}
}

function getCustName(custCode,fieldName){
	var returnString = "";
	var form = document.autoOrderForm;
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
			form.storeNo.value = "";
			form.subInv.value = "";
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
				<jsp:param name="function" value="AutoOrder"/>
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
						<html:form action="/jsp/autoOrderAction">
						<jsp:include page="../error.jsp"/>
						
					    <div align="center">
					     <table align="center" border="0" cellpadding="3" cellspacing="0" >
					       <tr>
								<td align="right">
								    วันที่ Order <font color="red">*</font>
								 </td>
								  <td>
								  <html:text property="bean.orderDate" styleId="orderDate" size="20" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td  align="right">รหัสร้านค้า </td>
								<td>
								  <html:text property="bean.storeCode" styleId="storeCode" size="20" onkeypress="getCustNameKeypress(event,this,'storeCode')"/>-
								  <input type="button" name="x1" value="..." onclick="openPopupCustomer('${pageContext.request.contextPath}','from','')"/>
								  <html:text property="bean.storeName" styleId="storeName" readonly="true" styleClass="disableText" size="40"/>
								
								  <html:hidden property="bean.subInv" styleId="subInv"/>
								  <html:hidden property="bean.storeNo" styleId="storeNo"/>
								</td>
							</tr>
					   </table>
					   <br/>
					   <table  align="center" border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td >
									<a href="javascript:genAutoOrder('${pageContext.request.contextPath}')">
									  <input type="button" value="Generate Order เติมเต็ม " class="newPosBtnLong"> 
									</a>	
									<a href="javascript:save('${pageContext.request.contextPath}')">
									  <input type="button" value="   บันทึก   " class="newPosBtnLong">
									</a>				
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>		
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="ไปหน้าจอ Gen Stock Onhand" class="newPosBtnLong">
									</a>
								</td>
							</tr>
					  </table>
				     </div>
				     <br/>

					<!-- ************************Result ***************************************************-->
					
                    <!-- ************************Result ***************************************************-->

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