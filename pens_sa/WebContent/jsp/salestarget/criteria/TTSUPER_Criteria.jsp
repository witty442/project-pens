<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetForm"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>
<%
SalesTargetBean bean = ((SalesTargetForm)session.getAttribute("salesTargetForm")).getBean();
User user = (User) request.getSession().getAttribute("user");
//System.out.println("TTSUPER Bean:"+bean);
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
	
	<%if( !"".equals(Utils.isNull(bean.getSalesChannelNo())) ) { %>
         document.getElementsByName('bean.salesChannelNo')[0].value = "<%=bean.getSalesChannelNo()%>";
         loadCustCatNoList();
	      document.getElementsByName('bean.custCatNo')[0].value = '<%=bean.getCustCatNo()%>';
	<% } %>
}

function disableF5(e) {
	if (e.which == 116) e.preventDefault(); 
}
//To re-enable f5
$(document).unbind("keydown", disableF5);

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

function openEdit(path,salesZone,brand,custCatNo,mode){
	var form = document.salesTargetForm;
	var param  = "&salesZone="+salesZone;
	    param += "&brand="+brand;
	    param += "&custCatNo="+custCatNo+"&mode="+mode;
	form.action = path + "/jsp/salesTargetAction.do?do=prepare"+param;
	form.submit();
	return true;
}

function rejectRow(path,salesZone,brand,custCatNo,period,rowId){
	var div_msg = document.getElementById("div_msg");
	
	var rejectReason = document.getElementsByName("rejectReason")[rowId-1];
	var status = document.getElementsByName("status")[rowId-1];
	var period = document.getElementsByName("bean.period")[0].value;
	var startDate = document.getElementsByName("bean.startDate")[0].value;
	//alert(rejectReason);
	var returnString = "";
	var param = "custCatNo="+custCatNo+"&salesZone="+salesZone+"&brand="+brand;
	    param += "&period="+period+"&startDate="+startDate
	var reasonPrompt = prompt("ระบุเหตุผลในการยกเลิก", "");
	//alert(reasonPrompt);
	if(reasonPrompt != null){
	
		rejectReason.value = reasonPrompt;
		param +="&rejectReason="+reasonPrompt;
		var getData = $.ajax({
				url: path+"/jsp/ajax/salesTTRejectRowAjax.jsp",
				data : encodeURI(param),
				async: true,
				cache: false,
				success: function(getData){
				  returnString = jQuery.trim(getData);
				}
			}).responseText;
		
		status.value ="Reject";
		//set hide action reject
		document.getElementById("span_reject_action_"+rowId).innerHTML = "";
		div_msg.style.display = 'block'; 
	}
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
function copyFromLastMonthByTTSUPER(path,e){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	var salesZone = document.getElementById("salesZone").value;
	
	if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		$('#periodDesc').focus();
		return false;
	 } 
	
	if( $('#salesZone').val()==""){
		alert("กรุณากรอก ภาคตามการดูแล");
		$('#salesZone').focus();
		return false;
	} 
	
	if(confirm('ยืนยัน Copy From Last Month ')){
	  //To disable f5
	  $(document).bind("keydown", disableF5);
	
	  /**Control Save Lock Screen **/
	  startControlSaveLockScreen();
	  
	  form.action = path + "/jsp/salesTargetAction.do?do=copyFromLastMonth&action=new&pageName="+pageName;
	  form.submit();
	  return true;
	}
	return false;
}
function copyRowByBrand(path,salesZone,brand,custCatNo,period,rowId){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	
	var div_msg = document.getElementById("div_msg");
	var period = document.getElementsByName("bean.period")[0].value;
	var startDate = document.getElementsByName("bean.startDate")[0].value;
	var returnString = "";
	var param = "&custCatNo="+custCatNo+"&salesZone="+salesZone+"&brand="+brand;
	    param += "&period="+period+"&startDate="+startDate
	
	if(confirm("ยืนยัน Copy แบรนด์นี้ จากเป้าเดือนที่แล้ว")){
		  /**Control Save Lock Screen **/
		  startControlSaveLockScreen();
		  
		  form.action = path + "/jsp/salesTargetAction.do?do=copyBrandFromLastMonth&action=new&pageName="+pageName+param;
		  form.submit();
	}
}
</script>

 <!-- Progress Bar -->
  <div id="div_msg" style="display:none">
   <b><font size="2" color="red">บันทึกข้อมูลเรียบร้อยแล้ว</font></b>
 </div>

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
                <td>แบรนด์ <font color="red"></font> </td>
				<td colspan="2">
	                <html:text property="bean.brand" styleId="brand" size="20" 
	                  onkeypress="getBrandNameKeypress(event,this)"
	                  styleClass="\" autoComplete=\"off" />	
				    <html:text property="bean.brandName" styleId="brandName" readonly="true" styleClass="disableText" size="50"/>
				</td>  
			</tr>	
			<tr>
                <td> ประเภทขาย <font color="red"></font></td>
				<td colspan="2">
				     <html:select property="bean.custCatNo" styleId="custCatNo">
				      <html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
				    </html:select> 
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    ภาคตามสายดูแล<font color="red">*</font>
				     <html:select property="bean.salesZone" styleId="salesZone">
						<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
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
					&nbsp;&nbsp;	
					<!-- Copy From Last Month -->
					 <a href="javascript:copyFromLastMonthByTTSUPER('${pageContext.request.contextPath}',event)">
					  <input type="button" value="Copy From Last Month" class="newPosBtnLong">
					</a> 
				</td>
			</tr>
		</table>