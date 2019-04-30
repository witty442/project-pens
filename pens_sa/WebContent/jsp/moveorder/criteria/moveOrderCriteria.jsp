
<%@page import="com.isecinc.pens.web.moveorder.MoveOrderForm"%>
<%@page import="com.isecinc.pens.web.moveorder.MoveOrderBean"%>
<%@page import="util.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%
MoveOrderBean bean = ((MoveOrderForm)session.getAttribute("moveOrderForm")).getBean();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png');
	var form = document.moveOrderForm;
	
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
	var form = document.moveOrderForm;
	var pageName = "";//document.getElementsByName("pageName")[0].value;
	form.action = path + "/jsp/moveOrderAction.do?do=prepareSearch&action=back&pageName="+pageName;
	form.submit();
	return true;
}
function search(path){
	var form = document.moveOrderForm;
	if( form.startDate.value =="" && form.endDate.value==""){
		alert("กรุณาระบุวันที่");
		form.startDate.focus();
		return false;
	}
	
	form.action = path + "/jsp/moveOrderAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportToExcel(path){
	var form = document.moveOrderForm;
	form.action = path + "/jsp/moveOrderAction.do?do=exportToExcel&action=newsearch";
	form.submit();
	return true;
}
function gotoPage(path,currPage){
	var form = document.moveOrderForm;
	form.action = path + "/jsp/moveOrderAction.do?do=searchHead&currPage="+currPage;
    form.submit();
    return true;
}

function setPeriodType(periodType){
	var form = document.moveOrderForm;
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
	var form = document.moveOrderForm;
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
			url: "${pageContext.request.contextPath}/jsp/moveorder/ajax/genSalesrepCodeListAjax.jsp",
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
	var form = document.moveOrderForm;
	var param = "&pageName="+pageName;
	if("CustomerVanProdShow" == pageName){
        param +="&salesChannelNo="+form.salesChannelNo.value;
        param +="&salesrepCode="+form.salesrepCode.value;
	}else if("Brand" == pageName){
		param +="&brand="+form.brand.value;
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}
function setDataPopupValue(code,desc,pageName){
	var form = document.moveOrderForm;
	if("Brand" == pageName){
		form.brand.value = code;
	}else if("CustomerVanProdShow" == pageName){
		form.customerCode.value = code;
	}
} 

</script>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
	       <tr>
                <td> รอบเวลา<font color="red">*</font></td>
				<td colspan="2">		
				    <html:select property="bean.periodType" styleId="periodType" onchange="setPeriodType(this)">
						<html:option value="month">เดือน</html:option>
						<html:option value="day">วัน</html:option>
				    </html:select>
				   &nbsp;
				     เดือน		
					 <html:select property="bean.periodDesc" styleId="periodDesc" onchange="setPeriodDate(this)">
						<html:options collection="PERIOD_LIST" property="value" labelProperty="keyName"/>
				    </html:select>
				     <html:hidden property="bean.period" styleId="period"/>
				 &nbsp; &nbsp; &nbsp;
				 <font color="red">*</font>	
				     <html:text property="bean.startDate" styleId="startDate" size="15" readonly="true" styleClass="disableText"/>
				       &nbsp;-&nbsp;
					<html:text property="bean.endDate" styleId="endDate" size="15" readonly="true" styleClass="disableText"/>
				</td>
				<td> 
				</td>
			</tr>
			<tr>
                <td> ประเภทขาย  </td>
				<td colspan="2">
				     <html:select property="bean.custCatNo" styleId="custCatNo">
						<html:options collection="CUSTOMER_CATEGORY_LIST" property="custCatNo" labelProperty="custCatDesc"/>
				    </html:select>
				      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    ภาคการขาย
				    <html:select property="bean.salesChannelNo" styleId="salesChannelNo"  onchange="loadSalesrepCodeList()">
						<html:options collection="SALES_CHANNEL_LIST" property="salesChannelNo" labelProperty="salesChannelDesc"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									    ภาคตามสายดูแล 
					<html:select property="bean.salesZone" styleId="salesZone" onchange="loadSalesrepCodeList()">
					    <html:options collection="SALES_ZONE_LIST" property="salesZone" labelProperty="salesZoneDesc"/>
					</html:select>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
				 พนักงานขาย 
				     <html:select property="bean.salesrepCode" styleId="salesrepCode">
						<html:options collection="SALESREP_LIST" property="salesrepId" labelProperty="salesrepCode"/>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    <html:checkbox property="bean.dispHaveReason"/> &nbsp;แสดงเฉพาะรายการที่มีเหตุผลหรือหมายเหตุ
				</td>
			</tr>	
			<tr>
                <td> ประเภทเอกสาร</td>
				<td colspan="2">
				    <html:select property="bean.docType" styleId="docType">
						<html:option value="ALL">ทั้งใบเบิก-ใบคืน</html:option>
						<html:option value="ใบเบิก">ใบเบิก</html:option>
						<html:option value="ใบคืน">ใบคืน</html:option>
				    </html:select>
				สถานะเอกสาร
				  <html:select property="bean.docStatus" styleId="docStatus">
						<html:option value="NO_RECEIVE">ภายในยังไม่ได้รับ</html:option>
						<html:option value="RECEIVE">ภายในรับแล้ว</html:option>
						<html:option value="ALL">แสดงทั้งหมด</html:option>
				    </html:select>
				    &nbsp;&nbsp;&nbsp;&nbsp;
				 <html:checkbox property="bean.dispCheckMoveDay"/> &nbsp;
				  จำนวนวันการเดินทางของเอกสาร มากกว่า <b><%=bean.getMoveDay() %></b>
				 <%--  <html:text property="bean.moveDay" styleId="moveDay" size="2" /> --%> วัน
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