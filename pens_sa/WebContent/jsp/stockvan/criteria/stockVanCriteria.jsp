<%@page import="util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockvan.StockVanBean"%>
<%@page import="com.isecinc.pens.web.stockvan.StockVanForm"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockVanForm");

StockVanBean bean = ((StockVanForm)session.getAttribute("stockVanForm")).getBean();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockVanForm;
	
	//set SaleChannel Salesrep,CustCateNo
	 document.getElementsByName('bean.salesChannelNo')[0].value = "<%=Utils.isNull(bean.getSalesChannelNo())%>";

}
function clearForm(path){
	var form = document.stockVanForm;
	var pageName = "";//document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/stockVanAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.stockVanForm;
	if( form.pdType.value ==""){
		alert("กรุณาระบุ ประเภท");
		form.pdType.focus();
		return false;
	}
	if( form.dispType.value ==""){
		alert("กรุณาระบุ รูปแบบการแสดงผล");
		form.pdType.focus();
		return false;
	}
	if( form.dispType.value =="1"){
		if( form.brand.value =="" && form.productCode.value ==""){
			alert("กรุณาระบุแบรนด์");
			form.brand.focus();
			return false;
		}
	}
	
	if( form.pdType.value =="V"){
		if( form.dispPlan.checked){
			alert("ยอดแพลนระหว่างทางที่ยังไม่ได้รับ มีเฉพาะกรณี สต๊อก PD เท่านั้น");
			form.dispPlan.focus();
			return false;
		}
	}
	
	form.action = path + "/jsp/stockVanAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.stockVanForm;
	form.action = path + "/jsp/stockVanAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.stockVanForm;
	form.action = path + "/jsp/stockVanAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}
function getBrandNameKeypress(e,brandId){
	var form = document.stockVanForm;
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
	var form = document.stockVanForm;
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
function openPopup(path,pageName){
	var form = document.stockVanForm;
	var param = "&pageName="+pageName+"&hideAll=true";
	if("PDStockVan" == pageName){
        param +="&salesChannelNo="+form.salesChannelNo.value+"&pdType="+form.pdType.value+"&selectone=false";;
	}else if("BrandStockVan" == pageName){
		param +="&brand="+form.brand.value+"&selectone=true";
	}else if("ItemStockVan" == pageName){
		param +="&brand="+form.brand.value+"&selectone=false";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.stockVanForm;
	if("PDStockVan" == pageName){
		form.pdCode.value = code;
	}else if("BrandStockVan" == pageName){
		form.brand.value = code;
	}else if("ItemStockVan" == pageName){
		form.productCode.value = code;
	}
} 

</script>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
	<tr>
        <td>  ภาคการขาย </td>
		<td colspan="2">	 
		    <html:select property="bean.salesChannelNo" styleId="salesChannelNo"  onchange="loadSalesrepCodeList()">
				<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
		    </html:select>
		    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    ภาคตามสายดูแล 
			<html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
			    <html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
			</html:select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		    ประเภท <font color="red">*</font>
		  <html:select property="bean.pdType" styleId="pdType">
		        <html:option value=""> </html:option>
				<html:option value="P">สต๊อก PD</html:option>
				<html:option value="V">สต๊อกบนรถ</html:option>
		    </html:select>
		     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		     PD/หน่วยรถ
		    <html:text property="bean.pdCode" styleId="pdCode" size="10" styleClass="\" autoComplete=\"off"/>
		    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','PDStockVan')"/>   
		</td>
	</tr>	
	<tr>
        <td>แบรนด์   </td>
		<td colspan="2">
		 <html:text property="bean.brand" styleId="brand" size="10" styleClass="\" autoComplete=\"off"/>
		    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','BrandStockVan')"/>   
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		       รหัสสินค้า
		   <html:text property="bean.productCode" styleId="productCode" size="10" styleClass="\" autoComplete=\"off"/>
		     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','ItemStockVan')"/>   
		    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    รูปแบบการแสดงผล <font color="red">*</font>
		     <html:select property="bean.dispType" styleId="dispType">
		        <html:option value=""> </html:option>
				<html:option value="1">แนวตั้ง : PD/หน่วยรถ  | แนวนอน :  รหัสสินค้า  </html:option>
				<html:option value="2">แนวตั้ง : รหัสสินค้า   |  แนวนอน :  PD/หน่วยรถ</html:option>
		    </html:select>
		</td> 
	</tr>	
	<tr>
		<td colspan="3">
		   <html:checkbox property="bean.dispPlan" styleId="dispPlan"> </html:checkbox>&nbsp; แสดงยอดแพลน ระหว่างทางที่ยังไม่ได้รับ
		   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		    <html:checkbox property="bean.dispPrice" styleId="dispPrice"> </html:checkbox>&nbsp; แสดงยอดเงิน
		     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		   <html:checkbox property="bean.dispHaveQty" styleId="dispHaveQty"> </html:checkbox>&nbsp; แสดงเฉพาะที่มีสินค้า
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