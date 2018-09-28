<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.pens.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="genVanScriptForm" class="com.isecinc.pens.web.other.GenVanScriptForm" scope="session" />
<%

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
<style type="text/css"></style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){

}
function clearForm(path){
	var form = document.genVanScriptForm;
	form.action = path + "/jsp/genVanScriptAction.do?do=prepare&action=new";
	form.submit();
	return true;
}
function uploadToFTP(path){
	var form = document.genVanScriptForm;
	
	if( $("#saleCode").val() == ""){
		alert("กรุณาระบุ SaleCode");
		$("#saleCode").focus(); 
		return false;
	}
	if( $("#prefix").val() == ""){
		alert("กรุณาระบุ Action");
		$("#prefix").focus(); 
		return false;
	}
	if( $("#scriptSQL").val() == ""){
		alert("กรุณาระบุร้านค้าอย่างน้อย 1 ร้านค้า");
		$("#customerCode").focus(); 
		return false;
	}
	if(confirm("ยืนยัน เพิ่มข้อมูลร้านค้า ขายเชื่อให้ Sale")){
	   form.action = path + "/jsp/genVanScriptAction.do?do=save";
	   form.submit();
	   return true;
	}
}

function addCustomer(path){
	var form = document.genVanScriptForm;
	if( $("#customerCode").val() != ""){
		getCustName($("#customerCode").val());
	}else{
		alert("กรุณาระบุรหัสร้านค้า");
		$("#customerCode").focus(); 
		return false;
	}
	return true;
}
function getCustName(custCode){
	var returnString = "";
	var form = document.genVanScriptForm;
		var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/other/getMstCustomerNameOracleAjax.jsp",
				data : "custCode=" + custCode,
				type: "GET",
				async: false,
				success: function(getData){
					returnString = jQuery.trim(getData);	 
				}
		}).responseText;
		
		// alert(returnString);
	  if( $("#scriptSQL").val() == ""){
		  $("#scriptSQL").val($("#customerCode").val()+"-"+returnString+", \n");
	  }else{
		  $("#scriptSQL").val($("#scriptSQL").val()+$("#customerCode").val()+"-"+returnString+",\n");
	  }
	  $("#customerCode").val("");//clear
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
				<jsp:param name="function" value="genVanScript"/>
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
						<html:form action="/jsp/genVanScriptAction">
						<jsp:include page="../error.jsp"/>

						   <div align="center">
						    <table align="center" border="0" cellpadding="3" cellspacing="0" >
							     <tr>
	                                 <td colspan="2" align="center"><font size="2"><b>เพิ่มร้านค้าที่ต้องการขายเชื่อ</b> </font> </td>		
								</tr>
								<tr>
                                    <td> SaleCode (Ex.V101)<font color="red">*</font></td>
									<td >
						               <html:text property="bean.saleCode" styleId="saleCode" size="6" maxlength="4"/>
									</td>
								</tr>	
								<tr>
                                    <td> Action<font color="red">*</font> (Ex.before import,after import)</td>
									<td >
						               <html:select property="bean.prefix" styleId="prefix">
						                <%--   <html:option value=""></html:option> --%>
						                  <html:option value="import_before">before import</html:option>
						                  <html:option value="import_after">after import</html:option>
						                  <html:option value="export_before">before export</html:option>
						                  <html:option value="export_after">after export</html:option>
						               </html:select>
									</td>
								</tr>	
								<tr>
                                    <td> รหัสร้านค้า<font color="red">*</font></td>
									<td >
						               <html:text property="bean.customerCode" styleId="customerCode" size="20"/>
						               <a href="javascript:addCustomer('${pageContext.request.contextPath}')">
										  <input type="button" value="เพิ่มร้านค้า" class="newPosBtnLong">
										</a>
										(เพิ่มได้หลายร้านค้า) 
									</td>
								</tr>	
								<tr>
                                   <td colspan="2">
                                       <html:textarea property="bean.scriptSQL" styleId="scriptSQL"
                                       rows="8" cols="100" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								
						   </table>
						   
						   <table  border="0" cellpadding="3" cellspacing="0" >
								<tr>
									<td align="left">
									   
										<a href="javascript:uploadToFTP('${pageContext.request.contextPath}')">
										  <input type="button" value="ส่งข้อมูลให้ Sales" class="newPosBtnLong"> 
										</a>
										
										<a href="javascript:clearForm('${pageContext.request.contextPath}')">
										  <input type="button" value="   Clear   " class="newPosBtnLong">
										</a>						
									</td>
								</tr>
							</table>
					  </div>
					 
						
		<!-- ************************Result ***************************************************-->	
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