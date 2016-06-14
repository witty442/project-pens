<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="customerForm" class="com.isecinc.pens.web.customer.CustomerForm" scope="request" />
<%
String action = (String)request.getParameter("action");

String role = ((User)session.getAttribute("user")).getType();

User user = (User)session.getAttribute("user");

List<References> territories = InitialReferences.getReferenes().get(InitialReferences.TERRITORY);
pageContext.setAttribute("territories",territories,PageContext.PAGE_SCOPE);

/** Edit **/
List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> vatCode = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatCode",vatCode,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes(InitialReferences.PAYMENT_METHOD,"CS");
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

List<References> shippingMethod = InitialReferences.getReferenes().get(InitialReferences.SHIPMENT);
pageContext.setAttribute("shippingMethod",shippingMethod,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.User"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="-1" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customer.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA1vZ7pnm-fm1dttRBhXwEpUO2iCqduTgg" type="text/javascript"></script>
<script type="text/javascript">
//API KEY :AIzaSyA1vZ7pnm-fm1dttRBhXwEpUO2iCqduTgg
/********************************************** Google Map ***************************************/
function getLocation(path){
	var customerName = $("#customerCode").val()+"-"+$("#customerName").val();
	//window.open(path+"/jsp/location/findLocation.jsp?customerName="+customerName);
	var width= window.innerWidth-50;
	var height= window.innerHeight-50;
	//alert(width+","+height);
	PopupCenter(path+"/jsp/location/findLocation.jsp?customerName="+customerName, "Print",width,height);
	//window.open(path+"/jsp/location/findLocation.jsp?customerName="+customerName, "Print", "width="+width+",height="+height+",location=No,resizable=No");
}

 function setLocationValue(location){
	 $("#location").val(location);
     //alert(lat+","+lng);
 }

 function gotoMap(path){
	 var location= $("#location").val();
		//alert(lat+","+lng);
		if(location != "" ){
			var locationArr = location.split(",");
			var lat = locationArr[0];
			var lng = locationArr[1];
		    var customerName = $("#customerCode").val()+"-"+$("#customerName").val();
		    var width= window.innerWidth-100;
			var height= window.innerHeight-100;
			
			PopupCenter(path+"/jsp/location/showMapDetail.jsp?lat="+lat+"&lng="+lng+"&customerName="+customerName, "แสดงแผนที่",width,height);
	        // window.open("https://www.google.co.th/maps/place/"+location);//version 1
	}else{
		alert("ยังไม่ได้ระบุตำแหน่งร้านค้านี้  กรุณา 'กดค้นหาตำแหน่ง'");
	}
}
 
 /********************************************** Google Map ***************************************/

  function showImage(path){
	 var location= $("#location").val();
		//alert(lat+","+lng);
	 if(location != "" ){
		var width= window.innerWidth-100;
		var height= window.innerHeight-100;
	
		PopupCenter(path+"/jsp/customer/dispImageLocal.jsp?", "แสดงรูปภาพ",width,height);
			
	}else{
		alert("ยังไม่ได้ระบุตำแหน่งร้านค้านี้  กรุณา 'กดค้นหาตำแหน่ง'");
	}
}
  /** Display image after upload **/
	function readURL(input) {
      if (input.files && input.files[0]) {
          var reader = new FileReader();

          reader.onload = function (e) {
              $('#blah')
                  .attr('src', e.target.result)
                  .width(150)
                  .height(200);
          };
          reader.readAsDataURL(input.files[0]);
      }
  }
  /*****************************************/
 
function editAddressRow(rowNo){
	editAddress('${pageContext.request.contextPath}', rowNo);
}

//call ajax
function loadMainCustomer(e){
	//alert($('#parentCode').val());
	if(e == null || (e != null && e.keyCode == 13)){
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/mainCustomerQuery.jsp",
				data : "custcode=" + $('#parentCode').val() + "&main='Y'&id="+$('#customerId').val(),
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					if(returnString.length > 0){
						document.getElementsByName('customer.parentID')[0].value=returnString.split('::')[0];
						document.getElementsByName('customer.parentCode')[0].value=returnString.split('::')[1];
						document.getElementsByName('customer.parentName')[0].value=returnString.split('::')[2];
					}else{
						document.getElementsByName('customer.parentID')[0].value='';
						document.getElementsByName('customer.parentName')[0].value='';
					}
				}
			}).responseText;
		});
	}
}

function loadMe(){
	// Aneak.t 24/01/2011
	var rowAddr = document.getElementsByName('addr.id').length;
	document.getElementsByName('addr_id')[0].value = rowAddr;
	
	if(document.getElementsByName('customer.parentCode')[0].value!=''){
		document.getElementById('parentCode').value = (document.getElementsByName('customer.parentCode')[0].value);
	}

	new Epoch('epoch_popup','th',document.getElementById('birthDay'));
	new Epoch('epoch_popup','th',document.getElementById('trip'));
	
	switchPrintType();
}
function showMainCustomer(path,id,custId){
	window.open(path + "/jsp/pop/view/customerViewPopup.jsp?uId="+id+"&main='Y'&id="+custId, "Customer List", "width=500,height=350,location=No,resizable=No");
}
function setMainCustomer(code, name){
	$('#parentCode').val(code);
	$('#parentName').val(name);
	loadMainCustomer(null);
}

function switchPrintType(){
	var printType = $('input[name=customer.printType]:checked').val();
	if("H"==printType){
		$('#printBranchDesc').attr('readonly', true);
		$("input#printBranchDesc").attr("class", "disableText");
	}else{
		$('#printBranchDesc').attr('readonly', false);
		$("input#printBranchDesc").attr("class", "normalText");
	}
}

</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="bottom: 0;height: 100%;" id="maintab">
  	<tr>
		<td colspan="3"><jsp:include page="../header.jsp"/></td>
	</tr>
  	<tr id="framerow">
  		<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
    		<div style="height: 60px;">
    		<!-- MENU -->
	    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
				<tr>
			        <td width="100%">
			        	<jsp:include page="../menu.jsp"/>
			       	</td>
				</tr>
	    	</table>
	    	</div>
	    	<!-- PROGRAM HEADER -->
	      	<jsp:include page="../program.jsp">
				<jsp:param name="function" value="CustomerInfo"/>
				<jsp:param name="code" value="${customerForm.customer.code}"/>
			</jsp:include>
	      	<!-- TABLE BODY -->
	      	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txt1">
	      		<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_1.gif"/></td>
		            <td width="832px;" background="${pageContext.request.contextPath}/images2/boxcont1_5.gif"/></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_2.gif"/></td>
	      		</tr>
	      		<tr>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
		            <td bgcolor="#f8f8f8">
						<!-- BODY -->
						<html:form action="/jsp/customerAction"  enctype="multipart/form-data">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="30%" colspan="2"></td>
								<td width="20%"></td>
								<td width="20%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right" colspan="2">
								  <html:radio property="customer.printType" styleId="printType" value="H" onclick="switchPrintType()"></html:radio>สำนักงานใหญ่ 
								</td>
								<td align="left">
									 <html:radio property="customer.printType" styleId="printType" value="B" onclick="switchPrintType()"></html:radio>สาขาที่
								     <html:text property="customer.printBranchDesc" size="10" styleId="printBranchDesc" maxlength="5" readonly="true" onkeydown="return inputNum(event);" styleClass="disableText" />
								</td>
								<td align="right">
								 <html:checkbox property="customer.printHeadBranchDesc" value="Y">พิมพ์สนญ./สาขาที่</html:checkbox>
								 </td>
								<td align="left">
									
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.code" readonly="true" styleClass="disableText" styleId="customerCode"/>
								</td>
								<td align="right"><bean:message key="Customer.PartyType" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:select property="customer.partyType">
										<html:option value="P"><bean:message key="PartyType.Personal" bundle="sysele"/></html:option>
										<html:option value="O"><bean:message key="PartyType.Org" bundle="sysele"/></html:option>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Name" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:text property="customer.name" size="25" styleId="customerName"/>
								</td>
								<td align="right"><bean:message key="Customer.SubName" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.name2" size="25" />
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="TaxNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" nowrap>
									<html:text property="customer.taxNo" size="25" maxlength="20"/>
									 <html:checkbox property="customer.printTax" value="Y">พิมพ์เลขประจำตัวผู้เสียภาษี</html:checkbox>
								</td>
								<td align="right"><bean:message key="Customer.Website" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.website" size="25" />
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Territory" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:select property="customer.territory" value="<%=user.getTerritory() %>">
										<html:options collection="territories" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Customer.BusinessType" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.businessType" size="25" />
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.MainCode" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input id="parentCode" name="parentCode" size="22" onkeypress="loadMainCustomer(event);"/>
									<a href="#" onclick="showMainCustomer('${pageContext.request.contextPath}','${user.id}','${customerForm.customer.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"/></a>
								</td>
								<td align="right"><bean:message key="Customer.Birthday" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.birthDay" readonly="true" size="15" styleId="birthDay"/>
								</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
								<td colspan="2">
									<html:hidden property="customer.parentID"/>
									<html:hidden property="customer.parentCode"/>
									<html:text property="customer.parentName" styleId="parentName" size="77" readonly="true" styleClass="disableText"/>
								</td>
								<td align="left">
									<html:checkbox property="customer.airpayFlag" value="Y" styleClass="normalText"/>
									ให้ชำระผ่านระบบแอร์เพย์ (Air Pay)
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<tr>
								<td align="right" valign="top">
									<input type="button" value="เพิ่มที่อยู่" onclick="open_address('${pageContext.request.contextPath}');"/>
								</td>
								<td align="right" valign="top">
									<bean:message key="Address" bundle="sysele"/><font color="red">*</font>
									<input type="hidden" name="addr_id" value="0"/>
								</td>
								<td colspan="3">
									<table id="tblAddress" align="left" border="0" cellpadding="3" cellspacing="1" width="100%" class="result">
										<c:forEach var="address1" items="${customerForm.addresses}" varStatus="rows1">
										<c:choose>
											<c:when test="${rows1.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										<tr style="cursor: pointer; cursor: hand;" class="<c:out value='${tabclass}'/>">
											<td align="left">
												${address1.lineString}
												<input type="hidden" name='addr.id' value='${address1.id}'/>
												<input type='hidden' name='addr.row' value='${rows1.index+1}'/>
												<input type='hidden' name='addr.line1' value='${address1.line1}'/>
												<input type='hidden' name='addr.line2' value='${address1.line2}'/>
												<input type='hidden' name='addr.line3' value='${address1.line3}'/>
												<input type='hidden' name='addr.district' value='${address1.district.id}'/>
												<input type='hidden' name='addr.districtLabel' value='${address1.district.name}'/>
												<input type='hidden' name='addr.province' value='${address1.province.id}'/>
												<input type='hidden' name='addr.provinceLabel' value='${address1.province.name}'/>
												<input type='hidden' name='addr.postcode' value='${address1.postalCode}'/>
												<input type='hidden' name='addr.purpose' value='${address1.purpose}'/>
												<input type='hidden' name='addr.purposeLabel' value='${address1.purposeLabel}'/>
												<input type='hidden' name='addr.status' value='${address1.isActive}'/>
												<input type='hidden' name='addr.statusLabel' value='${address1.activeLabel}'/>
											</td>
											<td align="center">${address1.purposeLabel}</td>
											<td align="center" width="80px;">${address1.activeLabel}</td>
											<td align="center" width="20px;">
												<a href="javascript:open_address('${pageContext.request.contextPath}',${rows1.index+1});">
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
											</td>
											<!-- Wit Edit:18/07/2555 hide copy address -->
											   <!--  
												<td align="center" width="20px;">
													<a href="javascript:open_address('${pageContext.request.contextPath}',${rows1.index+1},'copy');">Copy</a>
												</td>
											  -->
										</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<tr>
								<td align="right" valign="top">
									<input type="button" value="เพิ่มผู้ติดต่อ" onclick="open_contact('${pageContext.request.contextPath}', 0);"/>
								</td>
								<td align="right" valign="top">
									<bean:message key="Contact" bundle="sysele"/>&nbsp;&nbsp;
								</td>
								<td colspan="3">
									<table id="tblContact" align="left" border="0" cellpadding="3" cellspacing="1" width="100%" class="result">
										<c:forEach var="contact1" items="${customerForm.contacts}" varStatus="rows2">
										<c:choose>
											<c:when test="${rows2.index %2 == 0}">
												<c:set var="tabclass" value="lineO"/>
											</c:when>
											<c:otherwise>
												<c:set var="tabclass" value="lineE"/>
											</c:otherwise>
										</c:choose>
										<tr style="cursor: pointer; cursor: hand;" class="<c:out value='${tabclass}'/>">
											<td align="left" valign="top">
												${contact1.contactTo}<br>
												${contact1.relation}
												<input type="hidden" name='cont.id' value='${contact1.id}'/>
												<input type='hidden' name='cont.row' value='${rows2.index+1}'/>
												<input type='hidden' name='cont.contactTo' value='${contact1.contactTo}'/>
												<input type='hidden' name='cont.relation' value='${contact1.relation}'/>
												<input type='hidden' name='cont.phone' value='${contact1.phone}'/>
												<input type='hidden' name='cont.fax' value='${contact1.fax}'/>
												<input type='hidden' name='cont.status' value='${contact1.isActive}'/>
												<input type='hidden' name='cont.statusLabel' value='${contact1.activeLabel}'/>
												<input type='hidden' name='cont.phone2' value='${contact1.phone2}'/>
												<input type='hidden' name='cont.mobile' value='${contact1.mobile}'/>
												<input type='hidden' name='cont.mobile2' value='${contact1.mobile2}'/>
												<input type='hidden' name='cont.phoneSub1' value='${contact1.phoneSub1}'/>
												<input type='hidden' name='cont.phoneSub2' value='${contact1.phoneSub2}'/>
											</td>
											<td align="left">
												<bean:message key="Contact.Phone" bundle="sysele"/> ${contact1.phone}
												<c:if test="${contact1.phoneSub1!=''}">ต่อ ${contact1.phoneSub1}</c:if>
												<c:if test="${contact1.phone2!=''}">, ${contact1.phone2}
												<c:if test="${contact1.phoneSub2!=''}">ต่อ ${contact1.phoneSub2}</c:if>
												</c:if><br>
												<bean:message key="Contact.Mobile" bundle="sysele"/> ${contact1.mobile}
												<c:if test="${contact1.mobile2!=''}">, ${contact1.mobile2}</c:if><br>
												<bean:message key="Contact.Fax" bundle="sysele"/> ${contact1.fax}
											</td>
											<td align="center" width="80px;">${contact1.activeLabel}</td>
											<td align="center" width="20px;">
												<a href="javascript:open_contact('${pageContext.request.contextPath}',${rows2.index+1});">
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
											</td>
										</tr>
										</c:forEach>
									</table>
								</td>				
							</tr>
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Profile.CreditTerm" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:select property="customer.paymentTerm">
										<html:options collection="paymentTerm" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Profile.TaxRate" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:select property="customer.vatCode">
										<html:options collection="vatCode" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Profile.PaymentMethod" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:select property="customer.paymentMethod">
										<html:options collection="paymentMethod" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Order.ShipmentRule" bundle="sysele"/><font color="red">*</font></td>
								<td align="left">
									<html:select property="customer.shippingMethod">
										<html:options collection="shippingMethod" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Order.SalesRepresent" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
									<html:hidden property="customer.salesRepresent.id"/>
									<html:hidden property="customer.salesRepresent.code"/>
								</td>
								<td align="right"><bean:message key="Trip.No" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.trip" styleId="trip" size="15" readonly="true"/>
								</td>
							</tr>
							<tr>
								<%if(role.equalsIgnoreCase(User.VAN)){ %>
								<td colspan="2"></td>
								<td align="left">
									<html:checkbox property="customer.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
								</td>
								<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
								     <input type="checkbox" checked disabled/>&nbsp;<bean:message key="Active" bundle="sysprop"/>
								     <html:hidden property="customer.isActive" value="Y"/>
								</td>
								<%}else{ %>
								<td align="right" colspan="2"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3"><html:checkbox property="customer.isActive" value="Y"/>&nbsp;<bean:message key="Active" bundle="sysprop"/></td>
								<%} %>
							</tr>
							<%if(role.equalsIgnoreCase(User.VAN)){ %>
							<tr>
								<td colspan="2"></td>
								<td align="left">
									<html:checkbox property="customer.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<tr>
								<td align="right" colspan="2">เลือกไฟล์ รูปภาพร้านค้า&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:file property="imageFile" styleClass="" style="width:300px;height:21px" styleId="imageFile" onchange="readURL(this)"/>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"></td>
								<td align="left" colspan="3">
								   <%if( !Utils.isNull(customerForm.getCustomer().getImageFileName()).equals("")){ %>
									               ไฟล์ที่บันทึกไว้:
									     <html:text property="customer.imageFileName" readonly="true" styleClass="disableText" size="100" styleId="imageFileName"></html:text>
									<%} %>
									
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"></td>
								<td align="left" colspan="3">
								     <img id="blah" /> 
									 <%if( !Utils.isNull(customerForm.getCustomer().getImageFileName()).equals("")){ %>
									       <img src="${pageContext.request.contextPath }/photoServlet?customerId=${customerForm.customer.id}" width="150" height="200" border="0"/>
									<%} %>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2">บันทึกตำแหน่งที่ตั้งร้านค้า&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="customer.location" size="100" readonly="false" styleId="location" styleClass="" /> 
								</td>
							</tr>
							<tr>
							    <td align="right" colspan="2"></td>
									<td align="left" colspan="3">
								      <span id="spnWait" style="display: none;"><img src="${pageContext.request.contextPath}/icons/waiting.gif" align="absmiddle" border="0"/></span>
									 <input type="button" value="ค้นหาตำแหน่ง" class="newPosBtn" onclick="return getLocation('${pageContext.request.contextPath}');">
									
									 <input type="button" value="แสดงตำแหน่ง " class="newPosBtn" onclick="return gotoMap('${pageContext.request.contextPath}');">
									 
								</td>
							</tr>
							<%} %>
						</table>
						<br />
						
						customer
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="return save('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<input type="hidden" name="tf" value="N"/>
						<html:hidden property="customer.id" styleId="customerId"/>
						<html:hidden property="customer.exported" value="N"/>
						<div id="addressList" style="text-align: left;display: none;"></div>
						<div id="contactList" style="text-align: left;display: none;"></div>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						</html:form>
						<!-- BODY -->
					</td>
					<td width="6px;" background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
				</tr>
				<tr style="height: 9px;">
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_4.gif"/></td>
		            <td background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
		            <td width="5px;" background="${pageContext.request.contextPath}/images2/boxcont1_3.gif"/></td>
	          	</tr>
    		</table>
    	</td>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td width="25px;" background="${pageContext.request.contextPath}/images2/content_left.png"></td>
    	<td background="${pageContext.request.contextPath}/images2/content01.png" valign="top">
   			<jsp:include page="../contentbottom.jsp"/>
        </td>
        <td width="25px;" background="${pageContext.request.contextPath}/images2/content_right.png"></td>
    </tr>
    <tr>
    	<td colspan="3"><jsp:include page="../footer.jsp"/></td>
  	</tr>
</table>
</body>
</html>