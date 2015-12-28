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

%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js"></script>

<script language="javascript">

window.onload =function(){
	switchPrintType();
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
						<html:form action="/jsp/customerAction">
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
								     <html:text property="customer.printBranchDesc"  size="10"  styleId="printBranchDesc" maxlength="5" readonly="true" onkeydown="return inputNum(event);" styleClass="disableText"/>
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
									<html:text property="customer.code" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="Customer.PartyType" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.partyType" disabled="true" styleClass="disableText">
										<html:option value=""></html:option>
										<html:option value="P"><bean:message key="PartyType.Personal" bundle="sysele"/></html:option>
										<html:option value="O"><bean:message key="PartyType.Org" bundle="sysele"/></html:option>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.name" size="25" readonly="true" styleClass="disableText"/>
								</td>
								<%if(action.equals("edit2")){ %>
								<td align="right"><bean:message key="Customer.SubName" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.name2" size="25" readonly="true" styleClass="disableText"/>
								</td>
								<%} %>
							</tr>
							<%if(action.equals("edit2")){ %>
							<tr>
								<td align="right" colspan="2"><bean:message key="TaxNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left" nowrap>
									<html:text property="customer.taxNo" size="25" readonly="false" maxlength="20"/>
									<html:checkbox property="customer.printTax" value="Y">พิมพ์เลขประจำตัวผู้เสียภาษี</html:checkbox>
								</td>
								<td align="right"><bean:message key="Customer.Website" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.website" size="25" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Territory" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.territory" disabled="true" styleClass="disableText">
										<html:options collection="territories" property="key" labelProperty="name"/>
									</html:select>
								</td>
								<td align="right"><bean:message key="Customer.BusinessType" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.businessType" size="25" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.MainCode" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.parentCode" size="25" readonly="true" styleClass="disableText"/>
								</td>
								<td align="right"><bean:message key="Customer.Birthday" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.birthDay" maxlength="10" size="15" readonly="true" styleClass="disableText"/>
								</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
								<td colspan="2">
									<html:text property="customer.parentName" size="77" readonly="true" styleClass="disableText"/>
									<html:hidden property="customer.parentID"/>
								</td>
								<td align="left">
									<html:checkbox property="customer.airpayFlag" value="Y" styleClass="normalText"/>
									ให้ชำระผ่านระบบแอร์เพย์ (Air Pay)
								</td>
							</tr>
							<%} %>
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<tr>
								<td align="center" valign="top">&nbsp;</td>
								<td align="right" valign="top">
									<bean:message key="Address" bundle="sysele"/>&nbsp;&nbsp;
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
										<tr class="<c:out value='${tabclass}'/>">
											<td align="left">
												${address1.lineString}
											</td>
											<td align="center">${address1.purposeLabel}</td>
											<td align="center" width="80px;">${address1.activeLabel}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
							</tr>
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<%if(action.equals("edit2")){ %>
							<tr>
								<td align="center" valign="top">&nbsp;</td>
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
										<tr class="<c:out value='${tabclass}'/>">
											<td align="left" valign="top">
												${contact1.contactTo}<br>
												${contact1.relation}
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
										</tr>
										</c:forEach>
									</table>
								</td>				
							</tr>
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
							<%if(role.equalsIgnoreCase(User.VAN)){ %>
							<tr>
								<td colspan="2"></td>
								<td align="left">
									<html:checkbox property="customer.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
								</td>
							</tr>
							<%} %>
							<%} %>
						</table>
						<br />
						<!-- BUTTON -->
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="#" onclick="return saveEdit('${pageContext.request.contextPath}');">
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
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<jsp:include page="../trxhist.jsp">
							<jsp:param name="module" value="<%=TrxHistory.MOD_CUSTOMER %>"/>
							<jsp:param name="id" value="${customerForm.customer.id}"/>
						</jsp:include>
						<html:hidden property="customer.id"/>
						<html:hidden property="customer.exported"/>
						<input type="hidden" name="tf" value="<%=session.getAttribute("tf") %>">
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