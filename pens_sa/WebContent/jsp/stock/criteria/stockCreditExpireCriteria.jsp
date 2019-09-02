<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.web.stock.StockForm"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="util.*"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
StockBean bean = ((StockForm)session.getAttribute("stockForm")).getBean();
%>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}

function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockForm;
	
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
	new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	
	<%if( !"".equals(Utils.isNull(bean.getSalesChannelNo())) || !"".equals(Utils.isNull(bean.getCustCatNo())) ) { %>
         document.getElementsByName('bean.salesChannelNo')[0].value = "<%=bean.getSalesChannelNo()%>";
         document.getElementsByName('bean.custCatNo')[0].value = "<%=bean.getCustCatNo()%>";

	     document.getElementsByName('bean.salesrepCode')[0].value = '<%=bean.getSalesrepCode()%>';
	<% } %>
}

function clearForm(path){
	var form = document.stockForm;
	var pageName = document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockAction.do?do=prepareSearch&action=new&pageName="+pageName;
	form.submit();
	return true;
}
function searchReport(path){
	var form = document.stockForm;
	 if( $('#startDate').val()==""){
		alert("กรุณาระบุ วันที่เริ่ม");
		$('#startDate').focus();
		return false;
	} 
	if( $('#endDate').val()==""){
		alert("กรุณาระบุ วันที่สิ้นสุด");
		$('#endDate').focus();
		return false;
	} 
	form.action = path + "/jsp/stockAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}

function sort(columnNameSort,orderSortType){
  var form = document.stockForm;
  var path = document.getElementById("path").value;
  var param ="&columnNameSort="+columnNameSort+"&orderSortType="+orderSortType;
  form.action = path + "/jsp/stockAction.do?do=searchHead&action=sort"+param;
  form.submit();
  return true;
}
function exportReport(path){
	var form = document.stockForm;
	 if( $('#startDate').val()==""){
		alert("กรุณาระบุ วันที่เริ่ม");
		$('#startDate').focus();
		return false;
	} 
	if( $('#endDate').val()==""){
		alert("กรุณาระบุ วันที่สิ้นสุด");
		$('#endDate').focus();
		return false;
	} 
	 
	form.action = path + "/jsp/stockAction.do?do=exportReport&action=newsearch";
	form.submit();
	return true;
}
function getBrandNameKeypress(e,brandId){
	var form = document.stockForm;
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
	var form = document.stockForm;
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
function loadSalesrepCodeList(){
	var cboDistrict = document.getElementsByName('bean.salesrepCode')[0];
	var param  ="salesChannelNo=" + document.getElementsByName('bean.salesChannelNo')[0].value;
	    param +="&custCatNo="+ document.getElementsByName('bean.custCatNo')[0].value;
	    param +="&salesZone="+ document.getElementsByName('bean.salesZone')[0].value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/stock/ajax/genSalesrepCodeListAjax.jsp",
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
	var form = document.stockForm;
	var param = "&pageName="+pageName;
	if("CustomerStock" == pageName){
        param +="&salesChannelNo="+form.salesChannelNo.value;
        param +="&salesrepCode="+form.salesrepCode.value;
	}else if("ItemStock" == pageName){
		param +="&brand="+form.brand.value;
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.stockForm;
	if("BrandStock" == pageName){
		form.brand.value = code;
	}else if("CustomerStock" == pageName){
		form.customerCode.value = code;
	}else if("ItemStock" == pageName){
		form.itemCode.value = code;
	}
} 
</script>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
           
	       <tr>
                <td> วันที่<font color="red">*</font></td>
				<td >					
					 <html:text property="bean.startDate" styleId="startDate" size="20" readonly="true" styleClass="disableText"/>
				       -
					 <html:text property="bean.endDate" styleId="endDate" size="20" readonly="true" styleClass="disableText"/>
				</td>
				<td colspan="2">
				   รูปแบบการแสดงผล  <font color="red">*</font>
				   <html:select property="bean.reportType" styleId="reportType">
						<html:options collection="REPORT_TYPE_LIST" property="reportValue" labelProperty="reportType"/>
				    </html:select> 
				</td>
			</tr>
			<tr>
                <td> ประเภทขาย </td>
				<td colspan="2">
				 <html:select property="bean.custCatNo" styleId="custCatNo">
						<html:options collection="CUST_CAT_LIST" property="custCatNo" labelProperty="custCatDesc"/>
				    </html:select>
				      &nbsp;&nbsp;&nbsp;&nbsp;
				ภาคการขาย 
				    <html:select property="bean.salesChannelNo" styleId="salesChannelNo" onchange="loadSalesrepCodeList()">
						<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
				    </html:select>
				      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
				       ภาคตามสายดูแล 
				      <html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
						<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
				       พนักงานขาย 
				      <html:select property="bean.salesrepCode" styleId="salesrepCode" >
						<html:options collection="SALESREP_LIST" property="salesrepCode" labelProperty="salesrepCode"/>
				    </html:select>
				</td>
			</tr>	
			<tr>
                <td> แบรนด์ </td>
				<td colspan="2">
				   <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off" />
				    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','BrandStock')"/>   
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    รหัสร้านค้า
				    <html:text property="bean.customerCode" styleId="customerCode" size="20" styleClass="\" autoComplete=\"off" />
				     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerStock')"/>   
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				  SKU
				   <html:text property="bean.itemCode" styleId="itemCode" size="20" styleClass="\" autoComplete=\"off" />
				     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','ItemStock')"/>   
				</td>
			</tr>	
	   </table>
	   <table  border="0" cellpadding="3" cellspacing="0" >
			<tr>
				<td align="left">
					<a href="javascript:searchReport('${pageContext.request.contextPath}')">
					  <input type="button" value="    ค้นหา      " class="newPosBtnLong"> 
					</a>&nbsp;
					 <a href="javascript:exportReport('${pageContext.request.contextPath}')">
					  <input type="button" value="  Export  " class="newPosBtnLong"> 
					</a>
					&nbsp;
					<a href="javascript:clearForm('${pageContext.request.contextPath}')">
					  <input type="button" value="   Clear   " class="newPosBtnLong">
					</a>			
				</td>
			</tr>
		</table>
<script>
 loadSalesrepCodeList();
</script>