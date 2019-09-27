<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.isecinc.pens.dao.GeneralDAO"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.web.stock.StockForm"%>
<%@page import="com.isecinc.pens.web.stock.StockBean"%>
<%@page import="com.pens.util.*"%>
<%@page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%
StockBean bean = ((StockForm)session.getAttribute("stockForm")).getBean();
User user = (User)session.getAttribute("user");

//check user login is map cust sales TT to fillter search customer popup 
boolean isUserMapCustSalesTT = GeneralDAO.isUserMapCustSalesTT(user);

String screenWidth = "";
if(session.getAttribute("screenWidth") != null){ 
	screenWidth = (String)session.getAttribute("screenWidth");
}
%>
<script type="text/javascript">
window.onload = function(){
	loadMe();
}
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('startDate'));
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
	//alert(form.startDate.value);
   
	 if( $('#customerCode').val()==""){
		alert("กรุณาระบุ รหัสร้านค้า");
		$('#customerCode').focus();
		return false;
	 } 
	 if( $('#brand').val()==""){
		alert("กรุณาระบุ แบรนด์");
		$('#brand').focus();
		return false;
	 } 
	 if( $('#reportType').val()==""){
		alert("กรุณาระบุ รูปแบบ");
		$('#reportType').focus();
		return false;
	 } 
	form.action = path + "/jsp/stockAction.do?do=searchHead&action=newsearch";
	form.submit();
	return true;
}
function exportReport(path){
	var form = document.stockForm;
	 if( $('#customerCode').val()==""){
		alert("กรุณาระบุ รหัสร้านค้า");
		$('#customerCode').focus();
		return false;
	 } 
	 if( $('#brand').val()==""){
		alert("กรุณาระบุ แบรนด์");
		$('#brand').focus();
		return false;
	 } 
	 if( $('#reportType').val()==""){
		alert("กรุณาระบุ รูปแบบ");
		$('#reportType').focus();
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

function openPopup(path,pageName){
	var form = document.stockForm;
	var param = "&hideAll=true&pageName="+pageName;
	if("CustomerCreditSales" == pageName){
		param += "&selectone=true";
		param += "&salesrepCode="+form.salesrepCode.value;
		param += "&startDate="+form.startDate.value;
		<%if(isUserMapCustSalesTT){%>
		    param +="&userName=<%=user.getUserName()%>";
		<%}%>
	}else if("SalesrepCreditSales" == pageName){
		param += "&selectone=false";
		<%if(isUserMapCustSalesTT){%>
		    param +="&userName=<%=user.getUserName()%>";
		<%}%>
	}else if("Brand" == pageName){
		param += "&selectone=true";
	}
	url = path + "/jsp/popupAction.do?do=prepare&action=new"+param;
	PopupCenterFullHeight(url,"",600);
}

function setDataPopupValue(code,desc,pageName){
	var form = document.stockForm;
	if("Brand" == pageName){
		form.brand.value = code;
	}else if("CustomerCreditSales" == pageName){
		form.customerCode.value = code;
		form.customerName.value = name;
	}else if('SalesrepCreditSales' == pageName){
		form.salesrepCode.value = code;
	}
} 
</script>
<style>
.td_bg_lineH{
	background-color: #03A4B6;
	text-align: center;
	height: 30px;
}
.td_bg_lineS{
	background-color: #AED6F1;
	color:red; 
	text-align: center;
}
.td_bg_lineS_num{
	background-color: #AED6F1;
	color:red; 
	text-align: right;
}
.td_bg_lineA{
	background-color: #F2D7D5;
	text-align: center;
}
.td_bg_lineA_num{
	background-color: #F2D7D5;
	text-align: right;
}
 #scroll {
<%if(!"0".equals(screenWidth)){%>
    width:<%=screenWidth%>px; 
    height:400px; 
    /* background:#A3CBE0; */
	/* border:1px solid #000; */
	overflow:auto;
	white-space:nowrap;
	/* box-shadow:0 0 25px #000; */
<%}%>
</style>
<table align="center" border="0" cellpadding="3" cellspacing="0" >
       <tr>
            <td colspan="2">
             ใช้ข้อมูลตั้งแต่ &nbsp;&nbsp;
              <html:text property="bean.startDate" styleId="startDate" size="10" readonly="true" styleClass="\" autoComplete=\"off"/> 
            &nbsp; ถึงปัจจุบัน</td>
		</tr>
		<tr>
            <td align="right">พนักงานขาย</td>
			<td>
			    <html:text property="bean.salesrepCode" styleId="salesrepCode" size="20" styleClass="\" autoComplete=\"off" />
			     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','SalesrepCreditSales')"/>   
			&nbsp;&nbsp;&nbsp;&nbsp;
			รหัสร้านค้า <font color="red">*</font> &nbsp;
			  <html:text property="bean.customerCode" styleId="customerCode" size="20" styleClass="\" autoComplete=\"off" />
			     <input type="button" name="x2" value="..." onclick="openPopup('${pageContext.request.contextPath}','CustomerCreditSales')"/>   
			  <html:hidden property="bean.customerName" styleId="customerName"/>
			</td>
		</tr>	
		<tr>
            <td align="right"> แบรนด์ <font color="red">*</font></td>
			<td>
			   <html:text property="bean.brand" styleId="brand" size="20" styleClass="\" autoComplete=\"off" />
			    <input type="button" name="x1" value="..." onclick="openPopup('${pageContext.request.contextPath}','Brand')"/>   
			&nbsp;&nbsp;&nbsp;&nbsp;
			รูปแบบการแสดงผล&nbsp;
			 <html:select property="bean.reportType">
			     <html:option value="2">แสดง SKU แนวนอน</html:option>
			     <html:option value="1">แสดง SKU แนวตั้ง</html:option>
			  </html:select>
			</td>
		</tr>	
		<tr>
            <td align="right"> หมายเหตุ</td>
			<td>
			   <table  border="0" cellpadding="3" cellspacing="0" >
		         <tr> <td align="left" class="td_bg_lineS">บรรทัดสีฟ้า คือ ข้อมูลที่ได้นับสต๊อก</td> </tr>
			      <tr> <td align="left" class="td_bg_lineA">บรรทัดสีส้ม คือ ข้อมูลการเปิดบิลขายและคืน (รวมของแถม)</td></tr>
			    </table>
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