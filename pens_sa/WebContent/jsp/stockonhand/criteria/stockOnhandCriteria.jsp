<%@page import="util.SessionUtils"%>
<%@page import="com.isecinc.pens.web.stockonhand.StockOnhandBean"%>
<%@page import="com.isecinc.pens.web.stockonhand.StockOnhandForm"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "stockOnhandForm");

StockOnhandBean bean = ((StockOnhandForm)session.getAttribute("stockOnhandForm")).getBean();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.stockOnhandForm;
	
	//set SaleChannel Salesrep,CustCateNo
	 document.getElementsByName('bean.salesChannelNo')[0].value = "<%=Utils.isNull(bean.getSalesChannelNo())%>";

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
	
	form.action = path + "/jsp/stockOnhandAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.stockOnhandForm;
	form.action = path + "/jsp/stockOnhandAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}

function getBrandNameKeypress(e,brandId){
	var form = document.stockOnhandForm;
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
	var form = document.stockOnhandForm;
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
	var form = document.stockOnhandForm;
	var param = "&pageName="+pageName+"&hideAll=true";
	if("SubInvOnhand" == pageName){
        param +="&selectone=false";;
	}else if("Brand" == pageName){
		param +="&brand="+form.brand.value+"&selectone=false";
	}else if("Item" == pageName){
		param +="&brand="+form.brand.value+"&selectone=false";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.stockOnhandForm;
	if("SubInvOnhand" == pageName){
		form.subInv.value = code;
	}else if("Brand" == pageName){
		form.brand.value = code;
	}else if("Item" == pageName){
		form.productCode.value = code;
	}
} 

</script>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
     <tr>
		<td colspan="3">
		 <html:radio property="bean.orgCode" styleId="orgCode" value="B00">  &nbsp;&nbsp;B00 คลัง B&W</html:radio>
		</td>
	</tr>	
	<tr>
        <td>SubInv</td>
		<td>
		 <html:text property="bean.subInv" styleId="subInv" size="20" styleClass="\" autoComplete=\"off"/>
		    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','SubInvOnhand')"/>   
		</td>
	   <td>&nbsp;&nbsp;&nbsp;&nbsp;
	           รูปแบบการแสดงผล  <font color="red">*</font>
		     <html:select property="bean.reportType" styleId="reportType">
			<html:options collection="REPORT_TYPE_LIST" property="reportValue" labelProperty="reportType"/>
			 </html:select> 
		</td> 
	</tr>	
	<tr>
        <td>แบรนด์   </td>
		<td>
		 <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off"/>
		    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
		</td>
	   <td>
	   &nbsp;&nbsp;&nbsp;&nbsp;
	      <html:checkbox property="bean.dispHaveQty" styleId="dispHaveQty"> </html:checkbox>&nbsp; แสดงเฉพาะที่มีสินค้า
		</td> 
	</tr>	
	<tr>
        <td>SKU   </td>
		<td>
		 <html:text property="bean.productCode" styleId="productCode" size="20" styleClass="\" autoComplete=\"off"/>
		    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Item')"/>   
		</td>
	   <td>
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