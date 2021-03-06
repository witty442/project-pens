
<%@page import="com.isecinc.pens.web.promotion.PromotionForm"%>
<%@page import="com.isecinc.pens.web.promotion.PromotionBean"%>
<%@page import="com.pens.util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
PromotionBean bean = ((PromotionForm)session.getAttribute("promotionForm")).getBean();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.promotionForm;
	
	//Set PeriodType
	document.getElementsByName('bean.periodType')[0].value ="<%=bean.getPeriodType()%>" 
	setPeriodType(document.getElementsByName('bean.periodType')[0]);
	
	//Set Period Deswc startDate EndDate
	<%if("month".equalsIgnoreCase(bean.getPeriodType())){%>
	   //setDateMonth
	   <%if( !"".equalsIgnoreCase(Utils.isNull(bean.getPeriodDesc()))){%>
	      document.getElementsByName('bean.periodDesc')[0].value ="<%=bean.getPeriodDesc()%>" 
	      setPeriodDate(document.getElementsByName('bean.periodDesc')[0]);
	   <%}else{%>
	      setPeriodDate(document.getElementsByName('bean.periodDesc')[0]);
	   <%}%>
	<%}else{%>
	    document.getElementsByName('bean.startDate')[0].value ="<%=bean.getStartDate()%>" 
		document.getElementsByName('bean.endDate')[0].value ="<%=bean.getEndDate()%>" 
	<%}%>
	
	//set SaleChannel Salesrep,CustCateNo
	 document.getElementsByName('bean.salesChannelNo')[0].value = "<%=Utils.isNull(bean.getSalesChannelNo())%>";
	 document.getElementsByName('bean.custCatNo')[0].value = "<%=Utils.isNull(bean.getCustCatNo())%>";

	//load SalesrepList
	loadSalesrepCodeList();
	//set salesrepCode old value
	document.getElementsByName('bean.salesrepCode')[0].value = '<%=Utils.isNull(bean.getSalesrepCode())%>';
}
function clearForm(path){
	var form = document.promotionForm;
	var pageName = "";//document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/promotionAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.promotionForm;
	if( form.startDate.value ==""|| form.endDate.value==""){
		alert("��س��к��ѹ���");
		if(form.startDate.value ==""){
			form.startDate.focus();
		}else if( form.endDate.value ==""){
			form.endDate.focus();
		}
		return false;
	}
	
	form.action = path + "/jsp/promotionAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function popupPrint(path,requestNo){
	var param = "&requestNo="+requestNo;
        //window.open(path + "/jsp/requestPromotionAction.do?do=popupPrint"+param, "Print", "width=80,height=50,location=No,resizable=No");
	
	 document.promotionForm.action = path + "/jsp/promotionAction.do?do=popupPrint"+param;
	 document.promotionForm.submit();
	 return true;
}
function exportToExcel(path){
	var form = document.promotionForm;
	form.action = path + "/jsp/promotionAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.promotionForm;
	form.action = path + "/jsp/promotionAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}

function getBrandNameKeypress(e,brandId){
	var form = document.promotionForm;
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
	var form = document.promotionForm;
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
		alert("��辺������");
		form.brandId.value = "";	
		form.brandName.value = "";
	}
}
function setPeriodType(periodType){
	var form = document.promotionForm;
	if(periodType.value =="month"){
		form.periodDesc.disabled=false;
		form.periodDesc.className="normalText";
		
		//set startdate and endDate
		setPeriodDate(document.getElementsByName('bean.periodDesc')[0]);
	}else if(periodType.value =="day"){
		form.periodDesc.disabled=true;
		form.periodDesc.className="disableText";
		
		form.startDate.disabled = false;
		form.endDate.disabled = false;
		form.startDate.className="normalText";
		form.endDate.className="normalText";
		form.startDate.value="";
		form.endDate.value="";
		new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
		new Epoch('epoch_popup', 'th', document.getElementById('endDate'));
	}
}
function setPeriodDate(periodDesc){
	var form = document.promotionForm;
	//alert(periodDesc);
	form.period.value = periodDesc.value.split("|")[0];
	form.startDate.value = periodDesc.value.split("|")[1];
	form.endDate.value = periodDesc.value.split("|")[2]; 
}
function loadSalesrepCodeList(){
	var cboDistrict = document.getElementsByName('bean.salesrepCode')[0];
	var param  ="salesChannelNo=" + document.getElementsByName('bean.salesChannelNo')[0].value;
	    param +="&custCatNo="+ document.getElementsByName('bean.custCatNo')[0].value;
	    param +="&salesZone="+ document.getElementsByName('bean.salesZone')[0].value;
	$(function(){
		var getData = $.ajax({
			url: "${pageContext.request.contextPath}/jsp/promotion/ajax/genSalesrepCodeListAjax.jsp",
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
	var form = document.promotionForm;
	var param = "&pageName="+pageName;
	if("CustomerCreditPromotion" == pageName){
        param +="&salesChannelNo="+form.salesChannelNo.value;
        param +="&salesrepCode="+form.salesrepCode.value;
	}else if("Brand" == pageName){
		param +="&brand="+form.brand.value;
	}else if("ItemCreditPromotion" == pageName){
		param +="&brand="+form.brand.value;
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.promotionForm;
	if("Brand" == pageName){
		form.brand.value = code;
	}else if("CustomerCreditPromotion" == pageName){
		form.customerCode.value = code;
	}else if("ItemCreditPromotion" == pageName){
		form.itemCode.value = code;
	}
} 

</script>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
	       <tr>
                <td> �ͺ����<font color="red">*</font></td>
				<td>		
				    <html:select property="bean.periodType" styleId="periodType" onchange="setPeriodType(this)">
						<html:option value="month">��͹</html:option>
						<html:option value="day">�ѹ</html:option>
				    </html:select>
				   &nbsp;
				     ��͹		
					 <html:select property="bean.periodDesc" styleId="periodDesc" onchange="setPeriodDate(this)">
						<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
				    </html:select>
				     <html:hidden property="bean.period" styleId="period"/>
				</td>
				<td> 
				&nbsp;&nbsp;&nbsp; <font color="red">*</font>	
				     <html:text property="bean.startDate" styleId="startDate" size="15" readonly="true" styleClass="disableText"/>
				       &nbsp;-&nbsp;
					<html:text property="bean.endDate" styleId="endDate" size="15" readonly="true" styleClass="disableText"/>
				</td>
				<td> 
				</td>
			</tr>
			<tr>
                <td> ���������  </td>
				<td colspan="2">
				     <html:select property="bean.custCatNo" styleId="custCatNo">
						<html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
				    </html:select>
				      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    �Ҥ��â��
				    <html:select property="bean.salesChannelNo" styleId="salesChannelNo"  onchange="loadSalesrepCodeList()">
						<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				   
                                           �Ҥ�����´��� 
				      <html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
						<html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
				 ��ѡ�ҹ��� 
				     <html:select property="bean.salesrepCode" styleId="salesrepCode">
						<html:options collection="SALESREP_LIST" property="salesrepCode" labelProperty="salesrepCode"/>
				    </html:select>
				    
				</td>
			</tr>	
			<tr>
                <td> �ù�� </td>
				<td colspan="2">
				   <html:text property="bean.brand" styleId="brand" size="20"/>
				    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    ������ҹ���
				    <html:text property="bean.customerCode" styleId="customerCode" size="20"/>
				     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerCreditPromotion')"/>   
				  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				  SKU
				   <html:text property="bean.itemCode" styleId="itemCode" size="20"/>
				     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','ItemCreditPromotion')"/>   
				</td>
			</tr>	
			
	   </table>
	   <table  border="0" cellpadding="3" cellspacing="0" >
			<tr>
				<td align="left">
					<a href="javascript:search('${pageContext.request.contextPath}')">
					  <input type="button" value="   ����     " class="newPosBtnLong"> 
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