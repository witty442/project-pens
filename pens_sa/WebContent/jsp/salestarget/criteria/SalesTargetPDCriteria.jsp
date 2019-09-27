<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetForm"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%-- 
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script> 
--%>

<%
SalesTargetBean bean = ((SalesTargetForm)session.getAttribute("salesTargetForm")).getBean();
User user = (User) request.getSession().getAttribute("user");
//String role = user.getRoleSalesTarget();

%>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.salesTargetForm;
	// new Epoch('epoch_popup', 'th', document.getElementById('transactionDate'));
	
	//setDateMonth
	setPeriodDate(form.periodDesc);
}

function clearForm(path){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.salesTargetForm;
	 if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		return false;
	 } 
	form.action = path + "/jsp/salesTargetAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.salesTargetForm;
	 if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		return false;
	 } 
	form.action = path + "/jsp/salesTargetAction.do?do=exportToExcel";
	form.submit();
	return true;
}
function setPeriodDate(periodDesc){
	var form = document.salesTargetForm;
	//alert(periodDesc);
	form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}

function openPopup(path,pageName){
	var form = document.salesTargetForm;
	var param = "&pageName="+pageName+"&hideAll=true";
	if("PD" == pageName){
		param +="&selectone=false";
	}else if("Brand" == pageName){
		param +="&brand="+form.brand.value+"&selectone=false";
	}else if("Item" == pageName){
		param +="&brand="+form.brand.value+"&selectone=false";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.salesTargetForm;
	if("PD" == pageName){
		form.pdCode.value = code;
	}else if("Brand" == pageName){
		form.brand.value = code;
	}else if("Item" == pageName){
		form.itemCode.value = code;
	}
} 
</script>

<table align="center" border="0" cellpadding="3" cellspacing="0" >
	       <tr>
                <td> เดือน <font color="red">*</font></td>
				<td>					
					 <html:select property="bean.periodDesc" styleId="periodDesc" onchange="setPeriodDate(this)">
						<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
				    </html:select>
				     <html:hidden property="bean.period" styleId="period"/>
				</td>
				<td> 
				     <html:text property="bean.startDate" styleId="startDate" size="20" readonly="true" styleClass="disableText"/>
				        -
					<html:text property="bean.endDate" styleId="endDate" size="20" readonly="true" styleClass="disableText"/>
				</td>
				<td>							
				</td>
			</tr>
			<tr>
                <td>  ภาคตามสายดูแล <font color="red"></font></td>
				<td colspan="2">
				     <html:select property="bean.salesZone" styleId="salesZone">
						<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
				    </html:select> 
				    &nbsp;&nbsp;
				    PD:
				      <html:text property="bean.pdCode" styleId="pdCode" size="20" styleClass="\" autoComplete=\"off"/>
		              <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','PD')"/>   
				</td>
			</tr>
			<tr>
                <td> แบรนด์ <font color="red"></font></td>
				<td colspan="2">
				    <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off"/>
				    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				       รหัสสินค้า
				   <html:text property="bean.itemCode" styleId="itemCode" size="20" styleClass="\" autoComplete=\"off"/>
				     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','Item')"/>   
				    &nbsp;
				</td>
			</tr>		
	   </table>
	   <table  border="0" cellpadding="3" cellspacing="0" >
			<tr>
				<td align="left">
					<a href="javascript:search('${pageContext.request.contextPath}')">
					  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
					</a>
					<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
					  <input type="button" value="Export" class="newPosBtnLong"> 
					</a>
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					  <input type="button" value="   Clear   " class="newPosBtnLong">
					</a>		
				</td>
			</tr>
		</table>