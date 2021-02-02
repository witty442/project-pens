<%@page import="com.pens.util.GoogleMapJavaScriptAPI"%>
<%@page import="com.isecinc.pens.web.customer.CustomerHelper"%>
<%@page import="util.SessionGen"%>
<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="customerForm" class="com.isecinc.pens.web.customer.CustomerForm" scope="request" />
<%
	String tripFlag = request.getParameter("tf") != null ? (String)request.getParameter("tf") : "";
session.setAttribute("tf", tripFlag);

String action = (String)request.getParameter("action");
if(action==null)action="edit2";
String role = ((User)session.getAttribute("user")).getType();

List<References> territories = InitialReferences.getReferenes().get(InitialReferences.TERRITORY);
pageContext.setAttribute("territories",territories,PageContext.PAGE_SCOPE);

List<References> paymentTerm = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentTerm",paymentTerm,PageContext.PAGE_SCOPE);

List<References> vatCode = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatCode",vatCode,PageContext.PAGE_SCOPE);

List<References> paymentMethod = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentMethod",paymentMethod,PageContext.PAGE_SCOPE);

List<References> shippingMethod = InitialReferences.getReferenes().get(InitialReferences.SHIPMENT);
pageContext.setAttribute("shippingMethod",shippingMethod,PageContext.PAGE_SCOPE);

List<References> partyTypeList = InitialReferences.getReferenes().get(InitialReferences.PARTY_TYPE);
pageContext.setAttribute("partyTypeList",partyTypeList,PageContext.PAGE_SCOPE);


//init tripList
request.setAttribute("tripDayList", CustomerHelper.initTripList());

String readOnly = "false";
String styleClass = "";
if(role.equalsIgnoreCase(User.TT)){ 
	readOnly = "true";
	styleClass = "disableText";
}else{
	/** Van validate GPS is found no Edit Customer **/
	
}

System.out.println("readOnly["+readOnly+"]styleClass["+styleClass+"]");
/** Case TT Allow edit SAveImage and Location */
%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession() %>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customer.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/google_maps.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>

<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>
<script type="text/javascript">

window.onload =function(){
	var rowAddr = document.getElementsByName('addr.id').length;
	document.getElementsByName('addr_id')[0].value = rowAddr;
	
	/* if(document.getElementsByName('customer.parentCode')[0].value!=''){
		document.getElementById('parentCode').value = (document.getElementsByName('customer.parentCode')[0].value);
	} */

	new Epoch('epoch_popup','th',document.getElementById('birthDay'));
	switchPrintType();
	initForm(<%=readOnly%>,'<%=styleClass%>')
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

/*** Disable Text by Sale Type **/
function initForm(readonly,styleClass){
	$(document).ready(function(){
	    $('#parentCode').attr('readonly', readonly);
	    $('#parentCode').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#birthDay').attr('readonly', readonly);
	    $('#birthDay').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#businessType').attr('readonly', readonly);
	    $('#businessType').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#website').attr('readonly', readonly);
	    $('#website').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#name2').attr('readonly', readonly);
	    $('#name2').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#customerName').attr('readonly', readonly);
	    $('#customerName').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#storeType').attr('readonly', readonly);
	    $('#storeType').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#taxNo').attr('readonly', readonly);
	    $('#taxNo').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#printTax').attr('disabled', readonly);
	    $('#printTax').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#printTax').attr('disabled', readonly);
	    $('#printTax').addClass(styleClass);
	});

	$(document).ready(function(){
	    $('#printHeadBranchDesc').attr('disabled', readonly);
	    $('#printHeadBranchDesc').addClass(styleClass);
	});
	$(document).ready(function(){
	    $('#printType1').attr('disabled', readonly);
	    $('#printType1').addClass(styleClass);
	    $('#printType2').attr('disabled', readonly);
	    $('#printType2').addClass(styleClass);
	});
	
	$(document).ready(function(){
	    $('#airpayFlag').attr('disabled', readonly);
	    $('#airpayFlag').addClass(styleClass);
	});
}

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
function showMainCustomer(path,id,custId){
	window.open(path + "/jsp/pop/view/customerViewPopup.jsp?uId="+id+"&main='Y'&id="+custId, "Customer List", "width=500,height=350,location=No,resizable=No");
}
function setMainCustomer(code, name){
	$('#parentCode').val(code);
	$('#parentName').val(name);
	loadMainCustomer(null);
}
</script>
</head>
<body  topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
						<html:form action="/jsp/customerAction" enctype="multipart/form-data">
					
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
								  <html:radio property="customer.printType" styleId="printType1" value="H" onclick="switchPrintType()"></html:radio>สำนักงานใหญ่ 
								</td>
								<td align="left" colspan="3">
									 <html:radio property="customer.printType" styleId="printType2" value="B" onclick="switchPrintType()"></html:radio>สาขาที่
								     <html:text property="customer.printBranchDesc"  size="10"  styleId="printBranchDesc" maxlength="5" readonly="true" onkeydown="return inputNum(event);" styleClass="disableText"/>
								&nbsp;&nbsp;&nbsp;
								<%if("TT".equalsIgnoreCase(role) || customerForm.getCustomer().getExported().equalsIgnoreCase("Y")){ %>
									        กำหนดจุด #1
									    <font color="red"></font>
									    <html:text property="customer.tripDay" size="10" styleId="tripDay"  readonly="true" styleClass="disableText" />
										 <%-- <html:select property="customer.tripDay"  disabled="true">
												<html:options collection="tripDayList" property="key" labelProperty="name"/>
										  </html:select>
											&nbsp;จุด #2
										 <html:select property="customer.tripDay2"  disabled="true">
												<html:options collection="tripDayList" property="key" labelProperty="name"/>
										  </html:select>
											&nbsp;จุด #3
										 <html:select property="customer.tripDay3"  disabled="true">
												<html:options collection="tripDayList" property="key" labelProperty="name"/>
										 </html:select> --%>
									<%}else{ %>
									         กำหนดจุด #1
									    <font color="red">*</font>
									    <html:text property="customer.tripDay" size="10" styleId="tripDay"  readonly="true" styleClass="disableText" />
										 <%-- <html:select property="customer.tripDay" disabled="true">
												<html:options collection="tripDayList" property="key" labelProperty="name"/>
										 </html:select>
											&nbsp;จุด #2
										 <html:select property="customer.tripDay2" disabled="true">
												<html:options collection="tripDayList" property="key" labelProperty="name"/>
											</html:select>
											&nbsp;จุด #3
										 <html:select property="customer.tripDay3" disabled="true">
											<html:options collection="tripDayList" property="key" labelProperty="name"/>
										 </html:select> --%>
									<%} %>
								 </td>
							
							</tr>
							<tr>
								<td align="right" colspan="2"><b><bean:message key="Customer.Code" bundle="sysele"/></b>&nbsp;&nbsp;</td>
								<td align="left">
									<b><html:text property="customer.code" readonly="true" styleClass="disableTextBigSize" styleId="customerCode"/></b>
								</td>
								<td align="right"><bean:message key="Customer.PartyType" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.businessType" styleId="businessType" disabled="true">
									<%-- 	<html:option value=""></html:option>
										<html:option value="P"><bean:message key="PartyType.Personal" bundle="sysele"/></html:option>
										<html:option value="O"><bean:message key="PartyType.Org" bundle="sysele"/></html:option>
                                    --%>		
                                     <html:options collection="partyTypeList" property="key" labelProperty="name"/>							
                                  </html:select>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.name" size="25"  styleId="customerName"/>
								</td>
								<%if(action.equals("edit2")){ %>
								<td align="right"><bean:message key="Customer.SubName" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.name2" size="25" styleId="name2"/>
								</td>
								<%} %>
							</tr>
							<%if(action.equals("edit2")){ %>
							<tr>
								<td align="right" colspan="2"><bean:message key="TaxNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" nowrap>
									<html:text property="customer.taxNo" size="25"  maxlength="20" styleId="taxNo" onkeydown="return inputNum2(event,this)"/>
									<html:checkbox property="customer.printTax" value="Y" styleId="printTax">พิมพ์เลขประจำตัวผู้เสียภาษี</html:checkbox>
								 &nbsp;
								   <html:checkbox property="customer.printHeadBranchDesc" value="Y" >พิมพ์สนญ./สาขาที่</html:checkbox>
								</td>
								<td align="right"><bean:message key="Customer.Website" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.website" size="25" styleId="website"/>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Territory" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.territory" disabled="true" styleClass="disableText" >
										<html:options collection="territories" property="key" labelProperty="name" />
									</html:select>
								</td>
								<td align="right"><%-- <bean:message key="Customer.BusinessType" bundle="sysele"/>&nbsp;&nbsp; --%></td>
								<td align="left">
									<%-- <html:text property="customer.businessType" size="25" styleId="businessType"/> --%>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.MainCode" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<input id="parentCode" name="parentCode" size="22" onkeypress="loadMainCustomer(event);"/>
									<%if(readOnly.equals("false")){ %>
									<a href="#" onclick="showMainCustomer('${pageContext.request.contextPath}','${user.id}','${customerForm.customer.id}');">
									<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"/></a>
									<%} %>
								</td>
								<td align="right"><bean:message key="Customer.Birthday" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.birthDay" maxlength="10" size="15" styleId="birthDay"/>
								</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
								<td colspan="2">
									<html:text property="customer.parentName" size="77" styleId="parentName" readonly="true" styleClass="disableText"/>
									<html:hidden property="customer.parentID"/>
								</td>
								<td align="left">
									<html:checkbox property="customer.airpayFlag" value="Y" styleClass="normalText" styleId="airpayFlag"/>
									ให้ชำระผ่านระบบแอร์เพย์ (Air Pay)
								</td>
							</tr>
							<%} %>
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<!--*********** Addresss *********************** -->
							
							<tr>
								<td align="right" valign="top">
									
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
											<%if(readOnly.equals("false")){ %>
												<a href="javascript:open_address('${pageContext.request.contextPath}',${rows1.index+1});">
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
											<%} %>
											</td>
										
										</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<!-- ****************************Addresss************* -->
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<%if(action.equals("edit2")){ %>
							<tr>
								<td align="right" valign="top">
							    	<%if(readOnly.equals("false")){ %>
									<input type="button" value="เพิ่มผู้ติดต่อ" onclick="open_contact('${pageContext.request.contextPath}', 0);"/>
									<%} %>
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
											<%if(readOnly.equals("false")){ %>
												<a href="javascript:open_contact('${pageContext.request.contextPath}',${rows2.index+1});">
												<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
											<%} %>
											</td>
										</tr>
										</c:forEach>
									</table>
								</td>				
							</tr>
							<!-- *******************************Conntact****************************-->
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<%} %>
							<%if(role.equalsIgnoreCase(User.TT)){ %>
							<%if(action.equalsIgnoreCase("edit2")){ %>
							<tr>
								<td align="right" colspan="2"></td>
								<td align="left">
									<html:checkbox property="customer.creditCheck" value="Y" disabled="true"/>&nbsp;Credit Check
								</td>
							</tr>
							<%} %>
							<%} %>
							<%if(action.equalsIgnoreCase("edit2")){ %>
							<tr>
								<td align="right" colspan="2"><bean:message key="Profile.CreditTerm" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.paymentTerm" disabled="true" styleClass="disableText">
										<html:options collection="paymentTerm" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Profile.TaxRate" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.vatCode" disabled="true" styleClass="disableText">
										<html:options collection="vatCode" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Profile.PaymentMethod" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.paymentMethod" disabled="true" styleClass="disableText">
										<html:options collection="paymentMethod" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Order.ShipmentRule" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.shippingMethod" disabled="true" styleClass="disableText">
										<html:options collection="shippingMethod" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<%} %>
							<%if(role.equalsIgnoreCase(User.TT)){ %>
							<%if(action.equalsIgnoreCase("edit2")){ %>
							<tr>
								<td align="right" colspan="2"><bean:message key="Order.ShipmentRoute" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.shippingRoute" disabled="true" styleClass="disableText">
										<html:option value=""></html:option>
										<!--<html:options collection="shippingMethod" property="key" labelProperty="name"/>-->
									</html:select>
								</td>
								<td align="right"><bean:message key="Order.TransportName" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.transitName" size="25" readonly="true" styleClass="disableText"/>	
								</td>
							</tr>
							<%} %>
							<%} %>
							<%if(action.equalsIgnoreCase("edit2")){ %>
							<tr>
								<td align="right" colspan="2"><bean:message key="Order.SalesRepresent" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.salesRepresent.name" size="30" readonly="true" styleClass="disableText"/>
									<html:hidden property="customer.salesRepresent.id"/>
									<html:hidden property="customer.salesRepresent.code"/>
								</td>
								<td align="right"><bean:message key="Trip.No" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.trip" styleId="trip" size="15" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<%} %>
							<%if(role.equalsIgnoreCase(User.TT)){ %>
							<tr>
								<td align="right" colspan="2">Credit Limit&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="customer.creditLimitLabel" size="10" readonly="true" styleClass="disableText" style="text-align:right;"/>
									<html:hidden property="customer.creditLimit"/>
								</td>
							</tr>
							<%} %>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.TotalInvoice" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="customer.totalInvoiceLabel" size="10" readonly="true" styleClass="disableText" style="text-align:right;"/>
									<html:hidden property="customer.totalInvoice"/>
								</td>
							</tr>
							<%if(action.equalsIgnoreCase("edit2")){ %>
							<tr>
								<%if(role.equalsIgnoreCase(User.VAN)){ %>
								<td colspan="2"></td>
								<td align="left">
									<html:checkbox property="customer.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
								</td>
								<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:checkbox property="customer.isActive" value="Y" disabled="true"/>&nbsp;<bean:message key="Active" bundle="sysprop"/></td>
								<%}else{ %>
								<td align="right" colspan="2"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" colspan="3"><html:checkbox property="customer.isActive" value="Y" disabled="true"/>&nbsp;<bean:message key="Active" bundle="sysprop"/></td>
								<%} %>
							</tr>
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
									<html:file property="imageFile"  style="width:300px;height:21px" styleId="imageFile" onchange="readURL(this)"/>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"></td>
								<td align="left" colspan="3">
								     <img id="blah" /> 
								  
									   <%if( !Utils.isNull(customerForm.getCustomer().getImageFileName()).equals("")){ %>
									       <img id="imageDB" src="${pageContext.request.contextPath }/photoCustomerServlet?customerId=${customerForm.customer.id}" width="150" height="200" border="0"/>
									   <%} %>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"></td>
								<td align="left" colspan="3">
							    
								   <%if( !Utils.isNull(customerForm.getCustomer().getImageFileName()).equals("")){ %>
									               ไฟล์ที่บันทึกไว้:
									     <html:text property="customer.imageFileName" readonly="true" styleClass="disableText" size="100" styleId="imageFileName"></html:text>
																	
										<a href="#" onclick="return showImage('${pageContext.request.contextPath}','${customerForm.customer.id}');">
											<input type="button" value="แสดงรูปภาพเต็มจอ " class="newPosBtn">
										 </a>
									 <%} %>	
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2">บันทึกตำแหน่งที่ตั้งร้านค้า&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="customer.location" size="100" readonly="true" styleId="location" styleClass="disableText" /> 
								     <html:hidden property="customer.lat" styleId="lat"  /> 
								    <html:hidden property="customer.lng" styleId="lng"   /> 
								</td>
							</tr>
							<tr>
							    <td align="right" colspan="2"></td>
									<td align="left" colspan="3">
								      <span id="spnWait" style="display: none;"><img src="${pageContext.request.contextPath}/icons/waiting.gif" align="absmiddle" border="0"/></span>
									 <%--  <input type="button" value="ค้นหาตำแหน่ง" class="newPosBtn" onclick="return getLocation('${pageContext.request.contextPath}');"> --%>
									<input type="button" value="แสดงตำแหน่ง " class="newPosBtn" onclick="return gotoMap('${pageContext.request.contextPath}');">
								</td>
							</tr>
							<%} %>
						</table>
						<br />
						<!-- BUTTON -->
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								   <%if(role.equalsIgnoreCase(User.VAN)){ %>
										<a href="#" onclick="return saveEdit('${pageContext.request.contextPath}');">
										<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
										<input type="button" value="บันทึก" class="newPosBtn">
										</a>
									<%}else{ %>
										<a href="#" onclick="return saveEditCredit('${pageContext.request.contextPath}');">
										<!--<img src="${pageContext.request.contextPath}/images/b_save.gif" border="1" class="newPicBtn">-->
										<input type="button" value="บันทึก" class="newPosBtn">
										</a>
									<%} %>
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_cancel.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<jsp:include page="../trxhist.jsp">
							<jsp:param name="module" value="<%=TrxHistory.MOD_CUSTOMER %>"/>
							<jsp:param name="id" value="${customerForm.customer.id}"/>
						</jsp:include>
						<html:hidden property="customer.id"/>
						<html:hidden property="customer.exported"/>
						<div title="CustomerViewEdit">..</div>
						<input type="hidden" name="tf" value="<%=session.getAttribute("tf") %>">
						<div id="addressList" style="text-align: left;display: none;"></div>
						<div id="contactList" style="text-align: left;display: none;"></div>
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