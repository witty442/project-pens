<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.pens.util.SIdUtils"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockOnhandForm;
	new Epoch('epoch_popup','th',document.getElementById('transDate'));
}
function clearForm(path){
	var form = document.stockOnhandForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockOnhandAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.stockOnhandForm;
	if(form.transDate.value ==""){
		alert("กรุณาระบุ Transaction Date");
		form.transDate.focus();
		return false;
	}
	form.action = path + "/jsp/stockOnhandAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
//
function save(saveType){
	var msg ="";
	var form = document.stockOnhandForm;
	var path = document.getElementById("path").value;
	if(saveType=="saveDB"){
		msg = "ยืนยัน การเปลี่ยนแปลงข้อมูล";
	}else{
		msg = "เมื่อกดปุ่มนี้ แสดงว่าข้อมูลสมบูรณ์แล้ว จะไม่สามารถเปลี่ยนแปลงแก้ไขได้อีก";
	}
	if(confirm(msg)){
		/**  Control Save Lock Screen **/
		startControlSaveLockScreen();
		
		form.action = path + "/jsp/stockOnhandAction.do?do=save&saveType="+saveType;
		form.submit();
		return true;
	}
	return false;
}
function exportToExcel(path){
	var form = document.stockOnhandForm;
	if(form.transDate.value ==""){
		alert("กรุณาระบุ Transaction Date");
		form.transDate.focus();
		return false;
	}
	form.action = path + "/jsp/stockOnhandAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function openPopup(path,pageName){
	var form = document.stockOnhandForm;
	var url = path + "/jsp/stockonhand/popup/transDateNissinPreOrderPop.jsp?action=new";
	PopupCenter(url,"",400,300);
}
function setDataPopupValue(code){
	var form = document.stockOnhandForm;
		form.transDate.value = code;
} 
function calcEndMonthQty(index){
	var beginQty = convetTxtObjToFloat(document.getElementsByName("beginQty")[index]);
	var currentQty = convetTxtObjToFloat(document.getElementsByName("currentQty")[index]);
	
	endMonthQty = currentQty-beginQty;
	//var endMonthQty = foreCastQty - (salesQty+totalQty);
	document.getElementsByName("endMonthQty")[index].value = endMonthQty;
	toCurrenyNoDigit(document.getElementsByName("endMonthQty")[index]);//convert to 100,000
	
	calcBufferQty(index);
}
function calcBufferQty(index){
	//alert(document.getElementsByName("totalQty")[index].value);
	var currentQty = convetTxtObjToFloat(document.getElementsByName("currentQty")[index]);
	var bufferPercent = convetTxtObjToFloat(document.getElementsByName("bufferPercent")[index]);
	
	//cale bufferQty
	var bufferQty = currentQty + ((bufferPercent/100)*currentQty);
	document.getElementsByName("bufferQty")[index].value = bufferQty;
	toCurreny(document.getElementsByName("bufferQty")[index]);//convert to 100,000
	
	//cale preOrderQty
	var endMonthQty = convetTxtObjToFloat(document.getElementsByName("endMonthQty")[index]);
	var suggestedPoQty = bufferQty-endMonthQty;
	document.getElementsByName("suggestedPoQty")[index].value = suggestedPoQty;
	toCurreny(document.getElementsByName("suggestedPoQty")[index]);//convert to 100,000
}
</script>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
     <tr>
		<td align="right"> Transaction Date<font color="red">*</font>
		</td>
		  <td> 
		    <html:text property="bean.transDate" styleId="transDate" readonly="true"/>
		    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','TransDateNissinPreOrder')"/>   
		</td>
	</tr>	
  </table>
   <table  border="0" cellpadding="3" cellspacing="0" >
		<tr>
			<td align="left">
				<a href="javascript:search('${pageContext.request.contextPath}')">
				  <input type="button" value="แสดงข้อมูล Pre-Order" class="newPosBtnLong"> 
				</a>
				<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
				  <input type="button" value="  Export   " class="newPosBtnLong"> 
				</a>
				<a href="javascript:clearForm('${pageContext.request.contextPath}')">
				  <input type="button" value="   Clear   " class="newPosBtnLong">
				</a>						
			</td>
		</tr>
	</table>
