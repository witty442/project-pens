<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.web.billplan.BillPlanForm"%>
<%@page import="com.isecinc.pens.web.billplan.BillPlanBean"%>
<%@page import="util.Utils"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.billPlanForm;
	
	//load SalesrepList
	loadSalesrepCodeList();

}
function clearForm(path){
	var form = document.billPlanForm;
	var pageName = "";//document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/billPlanAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.billPlanForm;
	
	form.action = path + "/jsp/billPlanAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.billPlanForm;
	form.action = path + "/jsp/billPlanAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.billPlanForm;
	form.action = path + "/jsp/billPlanAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}


function loadSalesrepCodeList(){
	var cboDistrict = document.getElementsByName('bean.salesrepCode')[0];
	var param  ="salesZone="+ document.getElementsByName('bean.salesZone')[0].value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/billPlan/ajax/genSalesrepCodeListAjax.jsp",
			data : param,
			async: false,
			success: function(getData){
				var returnString = jQuery.trim(getData);
				cboDistrict.innerHTML=returnString;
			}
		}).responseText;
	});
}

</script>

<table align="center" border="0" cellpadding="3" cellspacing="0" >
	 	<tr>
            <td>  ภาคตามสายดูแล   </td>
			<td colspan="2">	   
				<html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
				    <html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
				</html:select>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
			 พนักงานขาย 
			     <html:select property="bean.salesrepCode" styleId="salesrepCode">
					<html:options collection="SALESREP_LIST" property="salesrepId" labelProperty="salesrepCode"/>
			    </html:select>
			  
			</td>
		</tr>	 
   </table>
	   <table  border="0" cellpadding="3" cellspacing="0" >
			<tr>
				<td align="left">
					<a href="javascript:search('${pageContext.request.contextPath}')">
					  <input type="button" value="   ค้นหา     " class="newPosBtnLong"> 
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