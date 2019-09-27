<%@page import="com.pens.util.Utils"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetForm"%>
<%@page import="com.isecinc.pens.web.salestarget.SalesTargetBean"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<jsp:useBean id="salesTargetForm" class="com.isecinc.pens.web.salestarget.SalesTargetForm" scope="session" />
<%
SalesTargetBean bean = ((SalesTargetForm)session.getAttribute("salesTargetForm")).getBean();
String subPageName= Utils.isNull(request.getParameter("subPageName"));
if("".equals(subPageName)){
	subPageName = salesTargetForm.getSubPageName();
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
	<%if( !"".equals(Utils.isNull(bean.getCustCatNo())) ) { %>
	    document.getElementsByName('bean.custCatNo')[0].value = "<%=bean.getCustCatNo()%>";
	    loadSalesrepCodeList();
	    document.getElementsByName('bean.salesrepCode')[0].value = '<%=bean.getSalesrepCode()%>';
<% } %>
}
function clearForm(path){
	var form = document.salesTargetForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/salesTargetAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function searchReport(path){
	var form = document.salesTargetForm;
	 if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		return false;
	} 
	 if( $('#reportType').val()==""){
		alert("กรุณากรอก รูปแบบ");
		$('#brand').focus();
		return false;
	 } 
	 
	form.action = path + "/jsp/salesTargetAction.do?do=searchHead&action=newsearch";
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

function loadSalesrepCodeList(){
	//alert("loadSalesrepCodeList");
	var cboDistrict = document.getElementsByName('bean.salesrepCode')[0];
	var param  ="custCatNo=" + document.getElementsByName('bean.custCatNo')[0].value;
	    param +="&salesChannelNo=" + document.getElementsByName('bean.salesChannelNo')[0].value;
	    param +="&salesZone=" + document.getElementsByName('bean.salesZone')[0].value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/ajax/genSalesrepCodeTTListAjax.jsp",
			data : param,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}

function openPopup(path,pageName){
    var param = "&pageName="+pageName;
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}

function setDataPopupValue(code,desc,pageName){
	var form = document.salesTargetForm;
	if("Brand" == pageName){
		form.brand.value = code;
	}else if("Customer" == pageName){
		form.customerCode.value = code;
	}
} 
function exportToExcel(path){
	var form = document.salesTargetForm;
	 if( $('#periodDesc').val()==""){
		alert("กรุณากรอก เดือน");
		return false;
	} 
	 if( $('#reportType').val()==""){
		alert("กรุณากรอก รูปแบบ");
		$('#brand').focus();
		return false;
	 } 
	 
	form.action = path + "/jsp/salesTargetAction.do?do=exportToExcel";
	form.submit();
	return true;
}

</script>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
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
				รูปแบบ <font color="red">*</font>
				   <html:select property="bean.reportType" styleId="reportType">
						<html:options collection="REPORT_TYPE_LIST" property="reportValue" labelProperty="reportType"/>
				    </html:select>
				</td>
			</tr>
			<tr>
                <td> ภาคการขาย </td>
				<td colspan="3">
				    <html:select property="bean.salesChannelNo" styleId="salesChannelNo" onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
						<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				         ภาคตามการดูแล
				    <html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
					    <html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
					 </html:select>
					 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				         ประเภทขาย 
				     <html:select property="bean.custCatNo" styleId="custCatNo" onchange="loadSalesrepCodeList('${pageContext.request.contextPath}')">
						<html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;
				       พนักงานขาย 
				      <html:select property="bean.salesrepCode" styleId="salesrepCode" >
						<html:options collection="SALESREP_LIST" property="salesrepCode" labelProperty="salesrepCode"/>
				    </html:select>
						
				</td>
			</tr>	
			<tr>
                <td> แบรนด์ </td>
				<td colspan="3">
				   <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off" />
				    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				   <%if( !"TT".equals(subPageName)){ %>
					    รหัสร้านค้า
					    <html:text property="bean.customerCode" styleId="customerCode" size="20" styleClass="\" autoComplete=\"off" />
					     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','Customer')"/>   
					  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				  <%} %>
				   สถานะ &nbsp;
				   <html:select property="bean.status" styleId="status" >
						<html:options collection="STATUS_LIST" property="status" labelProperty="status"/>
				    </html:select> 
				    
				</td>
			</tr>	
	   </table>
	   <table  border="0" cellpadding="3" cellspacing="0" >
			<tr>
				<td align="left">
				   <a href="javascript:searchReport('${pageContext.request.contextPath}')">
					  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
					</a>
					
				    <a href="javascript:exportToExcel('${pageContext.request.contextPath}')">
					  <input type="button" value="Export To Excel" class="newPosBtnLong"> 
					</a>
					
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					  <input type="button" value="   Clear   " class="newPosBtnLong">
					</a>						
				</td>
			</tr>
		</table>