<%@page import="com.isecinc.pens.web.stockmc.StockMCAction"%>
<%@page import="com.pens.util.UserUtils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockmc.StockMCBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="stockMCForm" class="com.isecinc.pens.web.stockmc.StockMCForm" scope="session" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockMCForm");
User user = (User)session.getAttribute("user");
String pageName = Utils.isNull(request.getParameter("pageName")); 

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SIdUtils.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	new Epoch('epoch_popup','th',document.getElementById('stockDate'));
}
function clearForm(path){
	var form = document.stockMCForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockMCAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function save(path){
	var form = document.stockMCForm;
 	if( form.customerCode.value ==""){
		alert("กรุณาระบุห้าง");
		form.customerCode.focus();
		return false;
	} 
 	if( form.productCode.value ==""){
		alert("กรุณาระบุรหัสสินค้า Pens");
		form.productCode.focus();
		return false;
	} 
 	if( form.barcode.value ==""){
		alert("กรุณาระบุบาร์โค้ด");
		form.barcode.focus();
		
		return false;
	} 
 	if( form.productPackSize.value ==""){
		alert("กรุณาระบุ บรรจุ");
		form.productPackSize.focus();
		return false;
	} 
	form.action = path + "/jsp/stockMCAction.do?do=save";
	form.submit();
	return true;
}
function backsearch(path) {
    document.stockMCForm.action = path + "/jsp/stockMCAction.do?do=searchHead&action=back";
	document.stockMCForm.submit();
}
function exportToExcel(path){
	var form = document.stockMCForm;
	form.action = path + "/jsp/stockMCAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function getCustNameKeypress(e,customerCode){
	var form = document.stockMCForm;
	if(e != null && e.keyCode == 13){
		//alert(customerCode.value);
		if(customerCode.value ==''){
			form.customerName.value = '';
		}else{
			getCustNameModel(customerCode);
		}
	}
}
function getCustNameModel(customerCode){
	var returnString = "";
	var form = document.stockMCForm;
	var path = document.getElementById("path").value;
	var getData = $.ajax({
			url: path+"/jsp/stockMC/ajax/getCustNameAjax.jsp",
			data : "customerCode=" + customerCode.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if(returnString !=''){
		//var retArr = returnString.split("|");
		form.customerName.value =returnString;	
	}else{
		alert("ไม่พบข้อมูล");
		form.customerCode.value = "";	
		form.customerName.value = "";
	}
}
function openPopup(path,pageName){
	var form = document.stockMCForm;
	var param = "&pageName="+pageName;
	 if("CustomerStockMC" == pageName){
		param +="&hideAll=true&customerCode="+form.customerCode.value;
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.stockMCForm;
	if("CustomerStockMC" == pageName){
		//alert(code);
		form.customerCode.value = code;
		form.customerName.value = desc;
	}
} 
</script>
</head>		
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"  style="height: 100%;">
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
				<jsp:param name="function" value="MasterItemStockMC"/>
			</jsp:include>
			<!-- Hidden Field -->
		 <%--    <html:hidden property="pageName" value="<%=pageName %>"/> --%>
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
						<html:form action="/jsp/stockMCAction">
						<jsp:include page="../error.jsp"/>
						<div align="center">
						   	<!--  Criteria -->
						   <table align="center" border="0" cellpadding="3" cellspacing="0" >
							<tr>
				                <td align="right"> ห้าง  <font color="red">*</font></td>
								<td colspan="3">
								  <%if(stockMCForm.getMode().equalsIgnoreCase("add")){ %>
									  <html:text property="bean.customerCode" styleId="customerCode" 
									    size="10"  styleClass="\" autoComplete=\"off"
										onkeypress="getCustNameKeypress(event,this)"/>
									    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStockMC')"/>   
								   <%}else{ %>
								      <html:text property="bean.customerCode" styleId="customerCode" readonly="true"
									    size="10"  styleClass="disableText\" autoComplete=\"off" />
								   <%} %>
								   <html:text property="bean.customerName" styleId="customerName" size="40" readonly="true" styleClass="disableText"/>
								</td>
							</tr>	
							<tr>
				                <td align="right"> รหัสสินค้า Pens<font color="red">*</font>  </td>
								<td >
								  <html:text property="bean.productCode" size="10"  styleId="productCode"  styleClass="\" autoComplete=\"off"/>						
								</td>
								<td align="right">&nbsp;&nbsp; รหัสสินค้าห้าง  </td>
								<td >
								  <html:text property="bean.itemCust" size="10" styleId="itemCust"  styleClass="\" autoComplete=\"off"/>						
								</td>
							</tr>	
						    <tr>
				                <td align="right"> รายละเอียด</td>
								<td colspan="3">
								  <html:text property="bean.productName" styleId="productName" size="60" styleClass="\" autoComplete=\"off"/>
								</td>
							</tr>	
							<tr>
				                <td align="right"> บาร์โค้ด  <font color="red">*</font></td>
								<td >
								 <%if(stockMCForm.getMode().equalsIgnoreCase("add")){ %>
								   <html:text property="bean.barcode" size="20"  styleId="barcode"  styleClass="\" autoComplete=\"off"/>						
								 <%}else{ %>
								   <html:text property="bean.barcode" size="20"  styleId="barcode" readonly="true" styleClass="disableText\" autoComplete=\"off"/>
								 <%} %>
								</td>
								<td align="right">&nbsp;&nbsp; ราคาค้าปลีก </td>
								<td >
								  <html:text property="bean.retailPriceBF" size="10" styleId="retailPriceBF"  onkeydown="return isNum(this,event);"
								    styleClass="enableNumber\" autoComplete=\"off"/>						
								</td>
							</tr>
							<tr>
				                <td align="right"> บรรจุ  <font color="red">*</font></td>
								<td >
								  <html:text property="bean.productPackSize" size="10"  styleId="productPackSize"  styleClass="\" autoComplete=\"off"/>						
								</td>
								<td align="right">&nbsp;&nbsp;อายุสินค้า <font color="red"></font></td>
								<td >
								  <html:text property="bean.productAge" size="10" styleId="productAge"  styleClass="\" autoComplete=\"off"/>						
								</td>
							</tr>	
							<tr>
				                <td align="right"> หน่วย  <font color="red">*</font></td>
								<td >
								  <html:text property="bean.uom" size="10"  styleId="uom"  styleClass="\" autoComplete=\"off"/>						
								</td>
								<td align="right">&nbsp;&nbsp;แบรนด์ <font color="red">*</font></td>
								<td >
								  <html:text property="bean.brand" size="10" styleId="brand"  styleClass="\" autoComplete=\"off"/>						
								
								</td>
							</tr>	
							<tr>
				                <td align="right"> สถานะ  <font color="red">*</font></td>
								<td>
								  <html:checkbox property="bean.statusFlag">
								      &nbsp;&nbsp;สถานะใช้งาน
								  </html:checkbox>
								</td>
								<td align="right">&nbsp;&nbsp;</td>
								<td></td>
							</tr>		
					   </table>
					   <table  border="0" cellpadding="3" cellspacing="0" >
							<tr>
								<td align="left">
									<a href="javascript:save('${pageContext.request.contextPath}')">
									  <input type="button" value="   บันทึกรายการ     " class="newPosBtnLong"> 
									</a>
									
									<%-- <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
									  <input type="button" value="Export To Excel" class="newPosBtnLong">
									</a> --%>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									  <input type="button" value="   Clear   " class="newPosBtnLong">
									</a>	
									<a href="javascript:backsearch('${pageContext.request.contextPath}','add','','')">
									  <input type="button" value=" ปิดหน้าจอ  " class="newPosBtnLong"> 
									</a>					
								</td>
							</tr>
						</table>
					    </div>
					 	<!-- INPUT HIDDEN -->
					 	<input type="hidden" name="pageName" value="<%=pageName %>"/>
					 	<input type="hidden" id="path" name="path" value="${pageContext.request.contextPath}"/>
					 	
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