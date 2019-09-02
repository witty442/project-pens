<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>

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
function exportToExcel(path){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/salesTargetAction.do?do=exportToExcel&action=new&pageName="+pageName+"&subPageName=HEAD";
	form.submit();
	return true;
}
function save(path){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/salesTargetAction.do?do=save&action=new&pageName="+pageName;
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

function openView(path,ids,salesrepCode,customerId,customerCode,customerName,salesrepId,salesChannelNo,mode){
	var form = document.salesTargetForm;
	var param  = "&ids="+ids+"&salesrepCode="+salesrepCode;
	    param += "&customerCode="+customerCode+"&customerName="+customerName;
	    param += "&customerId="+customerId+"&salesrepId="+salesrepId+"&mode="+mode;
	    param += "&salesChannelNo="+salesChannelNo;
	   // alert(param);
	form.action = path + "/jsp/salesTargetAction.do?do=prepare"+encodeURI(param);
	form.submit();
	return true;
}

function getBrandNameKeypress(e,brandId){
	var form = document.salesTargetForm;
	if(e != null && e.keyCode == 13){
		if(brandId.value ==''){
			form.name.value = '';
		}else{
			getBrandNameModel(brandId);
		}
	}
}

//Return String :brandName
function getBrandNameModel(brandId){
	var returnString = "";
	var form = document.salesTargetForm;
	var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/getBrandNameAjax.jsp",
			data : "brandId=" + brandId.value,
			async: false,
			cache: false,
			success: function(getData){
			  returnString = jQuery.trim(getData);
			}
		}).responseText;
	if(returnString !=''){
		//var retArr = returnString.split("|");
		form.brandName.value =returnString;	
	}else{
		alert("ไม่พบข้อมูล");
		form.brandId.value = "";	
		form.brandName.value = "";
	}
}
function setPeriodDate(periodDesc){
	var form = document.salesTargetForm;
	//alert(periodDesc);
	form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}

function calcEstimateAmt(objNoUse,rowId){
	var price = document.getElementsByName("price")[rowId-1];
	var estimateQty = document.getElementsByName("estimateQty")[rowId-1];
	var estimateAmt = document.getElementsByName("estimateAmt")[rowId-1];
	//alert(price.value+"::"+estimateQty.value);
	if(price.value !='' && estimateQty.value !=''){
		//alert(convetTxtObjToFloat(price));
		var estimateAmtTemp = currenyToNum(estimateQty)*convetTxtObjToFloat(price);
		estimateAmt.value = estimateAmtTemp;
		//alert(estimateAmt.value +":"+estimateAmtTemp);
		toCurreny(estimateAmt);
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
			<td colspan="2"> 
			     <html:text property="bean.startDate" styleId="startDate" size="20" readonly="true" styleClass="disableText"/>
			        -
				<html:text property="bean.endDate" styleId="endDate" size="20" readonly="true" styleClass="disableText"/>
				
				รหัสพนักงานขาย  <html:text property="bean.salesrepCode" styleId="salesrepCode" size="50" readonly="true" styleClass="disableText"/>	
			     <html:hidden property="bean.salesrepId" styleId="salesrepId"/>
			</td>
		</tr>
		<tr>
               <td>แบรนด์ <font color="red"></font> </td>
			<td colspan="2">
                <html:text property="bean.brand" styleId="brand" size="20" 
                  onkeypress="getBrandNameKeypress(event,this)"
                  styleClass="\" autoComplete=\"off" />	
			    <html:text property="bean.brandName" styleId="brandName" readonly="true" styleClass="disableText" size="50"/>
			    
			    รหัสร้านค้า<font color="red"></font>
			    <html:select property="bean.customerCode" styleId="customerCode">
					<html:options collection="CUSTOMER_LIST" property="customerCode" labelProperty="customerName"/>
			    </html:select>
			</td>  
		</tr>	
   </table>
   <table  border="0" cellpadding="3" cellspacing="0" >
		<tr>
			<td align="left">
				<a href="javascript:search('${pageContext.request.contextPath}')">
				  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
				</a>
				<a href="javascript:clearForm('${pageContext.request.contextPath}')">
				  <input type="button" value="   Clear   " class="newPosBtnLong">
				</a>
				<a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
				  <input type="button" value="Export To Excel" class="newPosBtnLong">
				</a>						
			</td>
		</tr>
	</table>