<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="memberForm" class="com.isecinc.pens.web.member.MemberForm" scope="request" />
<%

String action = request.getParameter("action") != null ? (String)request.getParameter("action") : "";
pageContext.setAttribute("type", request.getParameter("type"), PageContext.PAGE_SCOPE);

if(request.getParameter("id")!=null){
	pageContext.setAttribute("memberId", request.getParameter("id"), PageContext.PAGE_SCOPE);
}else{
	pageContext.setAttribute("memberId", memberForm.getMember().getId(), PageContext.PAGE_SCOPE);
}

List<References> teritories = InitialReferences.getReferenes().get(InitialReferences.TERRITORY);
pageContext.setAttribute("teritories", teritories, PageContext.PAGE_SCOPE);

List<References> membertypes = InitialReferences.getReferenes().get(InitialReferences.MEMBER_TYPE);
pageContext.setAttribute("membertypes", membertypes, PageContext.PAGE_SCOPE);

List<References> memberlevels = InitialReferences.getReferenes().get(InitialReferences.MEMBER_STATUS);
pageContext.setAttribute("memberlevels", memberlevels, PageContext.PAGE_SCOPE);

List<References> paymentterms = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_TERM);
pageContext.setAttribute("paymentterms", paymentterms, PageContext.PAGE_SCOPE);
//System.out.println(paymentterms);

List<References> vatcodes = InitialReferences.getReferenes().get(InitialReferences.VAT_CODE);
pageContext.setAttribute("vatcodes", vatcodes, PageContext.PAGE_SCOPE);

List<References> paymentmethods = InitialReferences.getReferenes().get(InitialReferences.PAYMENT_METHOD);
pageContext.setAttribute("paymentmethods", paymentmethods, PageContext.PAGE_SCOPE);

List<References> qtydelivers = InitialReferences.getReferenes().get(InitialReferences.QTY_DELIVER);
pageContext.setAttribute("qtydelivers", qtydelivers, PageContext.PAGE_SCOPE);

List<References> roundDels = InitialReferences.getReferenes().get(InitialReferences.ROUND_DELIVER);
pageContext.setAttribute("roundDels", roundDels,PageContext.PAGE_SCOPE);

List<References> dg = InitialReferences.getReferenes().get(InitialReferences.DELIVERY_GROUP);
pageContext.setAttribute("dg", dg,PageContext.PAGE_SCOPE);

List<References> bankL = InitialReferences.getReferenes().get(InitialReferences.BANK);
pageContext.setAttribute("bankL", bankL,PageContext.PAGE_SCOPE);

// Check member expired date.
boolean isExpire = false;
if(DateToolsUtil.compareWithToday(memberForm.getMember().getExpiredDate()) >= 0){
	isExpire = true;
}

%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>


<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.ProductCategory"%>
<%@page import="com.isecinc.pens.model.MProductCategory"%>
<%@page import="com.isecinc.pens.bean.MemberProduct"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%>
<%@page import="util.DateToolsUtil"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>" /></title>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/member.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js"></script>

<script type="text/javascript" language="javascript"><!--
function loadMe(){
	//$('#pCategory').val(document.getElementsByName('member.productCategory.id')[0].value);
	changeVIP(document.getElementById('isvip').checked);
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
				<jsp:param name="function" value="MemberInfo" />
				<jsp:param name="code" value="${memberForm.member.code}"/>
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
						<html:form action="/jsp/memberAction">
						<jsp:include page="../error.jsp"/>
							<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
								<tr>
									<td width="25%" colspan="2"></td>
									<td width="20%"></td>
									<td width="20%"></td>
									<td></td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Member.Code" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.code" readonly="true" styleClass="disableText" size="15" /></td>
									<td align="right"><bean:message key="Customer.PartyType" bundle="sysele"/>&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.partyType" disabled="true" styleClass="disableText">
											<html:option value="P"><bean:message key="PartyType.Personal" bundle="sysele"/></html:option>
											<html:option value="O"><bean:message key="PartyType.Org" bundle="sysele"/></html:option>
										</html:select>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Member.Name" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.name" size="30" disabled="true" styleClass="disableText"/></td>
									<td align="right"><bean:message key="Member.Surname" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.name2" size="30" disabled="true" styleClass="disableText" /></td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Member.PIDNo" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.personIDNo" size="30" disabled="true" styleClass="disableText"/></td>
									<td align="right"><bean:message key="Contact.Email" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.email" size="30" disabled="true" styleClass="disableText"/></td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Member.DC" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.territory" disabled="true" styleClass="disableText">
											<html:options collection="teritories" property="key" labelProperty="name" />
										</html:select>
									</td>
									<td align="right"><bean:message key="Other.Cholesterol" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.chrolesterol" size="10" style="text-align: right;" disabled="true" styleClass="disableText"/></td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Other.Birthday" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:text property="member.birthDay" styleId="birthDay" readonly="true" size="15" styleClass="disableText"/>
									</td>
									<td align="right"><bean:message key="Other.Age" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.age" size="10" style="text-align: right;" disabled="true" styleClass="disableText"/></td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Other.Occupation" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.occupation" size="30" disabled="true" styleClass="disableText"/></td>
									<td align="right"><bean:message key="Other.MonthlyIncome" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left"><html:text property="member.monthlyIncome" size="10" style="text-align: right;" disabled="true" styleClass="disableText"/></td>
								</tr>
								<tr>
									<td colspan="2"></td>
									<td colspan="4"><hr></td>
								</tr>
								<tr>
									<td align="right" valign="top"></td>
									<td align="right" valign="top"><bean:message key="Address" bundle="sysele" />&nbsp;&nbsp;</td>
									<td colspan="3">
									<table id="tblAddress" align="left" border="0" cellpadding="3" cellspacing="0" width="100%" class="result">
										<tbody>
											<c:forEach var="address1" items="${memberForm.addresses}" varStatus="rows1">
												<c:choose>
													<c:when test="${rows1.index %2 == 0}">
														<c:set var="tabclass" value="lineO" />
													</c:when>
													<c:otherwise>
														<c:set var="tabclass" value="lineE" />
													</c:otherwise>
												</c:choose>
												<tr style="cursor: pointer; cursor: hand;" class="<c:out value='${tabclass}'/>">
													<td align="left">
														${address1.lineString} 
														<input type="hidden" name='addr.id' value='${address1.id}' /> 
														<input type='hidden' name='addr.row' value='${rows1.index+1}' /> 
														<input type='hidden' name='addr.line1' value='${address1.line1}' />
														<input type='hidden' name='addr.line2' value='${address1.line2}' /> 
														<input type='hidden' name='addr.line3' value='${address1.line3}' /> 
														<input type='hidden' name='addr.district' value='${address1.district.id}' /> 
														<input type='hidden' name='addr.districtLabel' value='${address1.district.name}' />
														<input type='hidden' name='addr.province' value='${address1.province.id}' /> 
														<input type='hidden' name='addr.provinceLabel' value='${address1.province.name}' />
														<input type='hidden' name='addr.postcode' value='${address1.postalCode}' /> 
														<input type='hidden' name='addr.purpose' value='${address1.purpose}' /> 
														<input type='hidden' name='addr.purposeLabel' value='${address1.purposeLabel}' /> 
														<input type='hidden' name='addr.status' value='${address1.isActive}' /> 
														<input type='hidden' name='addr.statusLabel' value='${address1.activeLabel}' />
													</td>
													<td align="center">${address1.purposeLabel}</td>
													<td align="center" width="80px;">${address1.activeLabel}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									</td>
								</tr>
								<tr>
									<td colspan="2"></td>
									<td colspan="4"><hr></td>
								</tr>
								<tr>
									<td align="right" valign="top"></td>
									<td align="right" valign="top"><bean:message key="Contact" bundle="sysele"/>&nbsp;&nbsp;</td>
									<td colspan="3">
										<table id="tblContact" align="left" border="0" cellpadding="3" cellspacing="1" width="100%" class="result">
											<c:forEach var="contact1" items="${memberForm.contacts}" varStatus="rows2">
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
												</td>
												<td align="left">
													<bean:message key="Contact.Phone" bundle="sysele"/> ${contact1.phone}
													<c:if test="${contact1.phoneSub1!=''}">��� ${contact1.phoneSub1}</c:if>
													<c:if test="${contact1.phone2!=''}">, ${contact1.phone2}
													<c:if test="${contact1.phoneSub2!=''}">��� ${contact1.phoneSub2}</c:if>
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
									<td colspan="2"></td>
									<td colspan="4"><hr></td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Condition.MemberType" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.memberType" disabled="true" styleClass="disableText">
											<html:options collection="membertypes" property="key" labelProperty="name" />
										</html:select>
									</td>
									<td align="right"><bean:message key="Member.DeliveryFirstDate" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:text property="member.firstDeliverlyDate" styleId="firstDeliverlyDate" readonly="true" size="15" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Profile.ApplyDate" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:text property="member.registerDate" styleId="registerDate" readonly="true" size="15" styleClass="disableText"/>
									</td>
									
									<td align="right"><bean:message key="Member.ExpireDate" bundle="sysele"/>&nbsp;&nbsp;</td>
									<td align="left">
										<html:text property="member.expiredDate" styleId="expiredDate" readonly="true" size="15" styleClass="disableText"/>
										
										&nbsp;&nbsp;&nbsp;&nbsp;  <html:checkbox property="member.oldPriceFlag" value="Y" disabled="true"/>&nbsp;�Ҥ����
									</td>
									
								</tr>
								
								<tr>
									<td align="right" colspan="2"><bean:message key="RecommendBy" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<table width="100%">
											<tr>
												<td align="left" width="10%">
													<html:select property="member.recommendedType" disabled="true" styleClass="disableText">
														<html:option value=""></html:option>
														<html:option value="M"><bean:message key="Member" bundle="sysele"/></html:option>
														<html:option value="O"><bean:message key="Other" bundle="sysele"/></html:option>
													</html:select>
												</td>
												<td align="left">
													<input type="text" id="mCode" name="mCode" value="${memberForm.member.recommendedCode}" readonly="readonly" class="disableText"/>
												</td>
											</tr>
										</table>
									</td>
									
								</tr>
								
								<tr>
									<td align="right" colspan="2"></td>
									<td align="left" colspan="3">
										<html:text property="member.recommendedBy" styleId="mName" size="95" readonly="true" styleClass="disableText"/>
										<html:hidden property="member.recommendedId"/>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Other.MemberStatus" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left" colspan="3">
										<html:select property="member.memberLevel" disabled="true" styleClass="disableText">
											<html:options collection="memberlevels" property="key" labelProperty="name" />
										</html:select>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Profile.CreditTerm" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.paymentTerm" disabled="true" styleClass="disableText">
											<html:options collection="paymentterms" property="key" labelProperty="name" />
										</html:select>
									</td>
									<td align="right"><bean:message key="Profile.TaxRate" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.vatCode" disabled="true" styleClass="disableText">
											<html:options collection="vatcodes" property="key" labelProperty="name" />
										</html:select>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Profile.PaymentMethod" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.paymentMethod" disabled="true" styleId="member.paymentMethod" styleClass="disableText">
											<html:options collection="paymentmethods" property="key" labelProperty="name" />
										</html:select>
									</td>
									<td align="right"><bean:message key="CreditCardBank" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.creditCardBank" disabled="true" styleId="member.creditCardBank" styleClass="disableText">
											<html:option value=""></html:option>
											<html:options collection="bankL" property="key" labelProperty="name" />
										</html:select>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="CreditCardNo" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:text property="member.creditCardNo" maxlength="20" size="20" readonly="true" styleClass="disableText"/>
									</td>
									<td align="right"><bean:message key="CreditCard.ExpiredDate" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:text property="member.creditcardExpired" maxlength="6" size="6" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="CreditCard.Name" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left" colspan="3">
										<html:text property="member.cardName" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="SysConf.RoundDelievery" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.roundTrip" disabled="true" styleClass="disableText">
											<html:options collection="roundDels" property="key" labelProperty="name" />
										</html:select>
									</td>
									<td align="right"><bean:message key="SysConf.QtyDelivery" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.orderAmountPeriod" disabled="true" styleClass="disableText">
											<html:options collection="qtydelivers" property="key" labelProperty="name" />
										</html:select>
									</td>
								</tr>
								<tr>
									
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Condition.ShipmentDay" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.shippingDate" disabled="true" styleClass="disableText">
											<html:option value="Sun"><bean:message key="Sunday" bundle="sysele" /></html:option>
											<html:option value="Mon"><bean:message key="Monday" bundle="sysele" /></html:option>
											<html:option value="Tue"><bean:message key="Tueday" bundle="sysele" /></html:option>
											<html:option value="Wed"><bean:message key="Wednesday" bundle="sysele" /></html:option>
											<html:option value="Thu"><bean:message key="Thursday" bundle="sysele" /></html:option>
											<html:option value="Fri"><bean:message key="Friday" bundle="sysele" /></html:option>
											<html:option value="Sat"><bean:message key="Saturday" bundle="sysele" /></html:option>
										</html:select>
									</td>
									<td align="right"><bean:message key="Condition.ShipmentTime" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:text property="member.shippingTime" size="5" maxlength="5" readonly="true" styleClass="disableText"/>
										<bean:message key="Member.shippingTimeTo" bundle="sysele" />
										<html:text property="member.shippingTimeTo" size="5" maxlength="5" readonly="true" styleClass="disableText"/>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Member.DeliveryGroup" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.deliveryGroup" styleId="deliveryGroup" disabled="true" styleClass="disableText">
											<html:option value=""></html:option>
											<html:options collection="dg" property="key" labelProperty="name"/>
										</html:select>	
									</td>
									<td></td>
									<td align="left">
										<html:checkbox property="member.isvip" styleId="isvip" value="Y" disabled="true" styleClass="disableText"/>Agent
										<html:hidden property="member.isvip"/>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2"><bean:message key="Member.PaymentType" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.paymentType" disabled="true"  styleId="paymentType">
											<html:option value="3">&nbsp;</html:option>
											<html:option value="1">�纤����á������</html:option>
											<html:option value="2">����¤���</html:option>
											
										</html:select>	
									</td>
									<td>�ӹǹ�Ǵ��� 㹡�èѴ�� &nbsp; <html:text property="member.totalOrderQty" styleClass="disableText" readonly="true" size="5" onkeydown="return inputNum(event);"/></td>
									<td align="left">
										<html:checkbox property="member.isFreeOfChart" disabled="true" styleId="isvip" value="Y" /><bean:message key="Member.FreeOfChart" bundle="sysele" />
									</td>
								</tr>
								<tr id="trLineProduct">
									<td colspan="2"></td>
									<td colspan="4"><hr></td>
								</tr>
								<tr id="trproduct">
									<td align="right" valign="top"></td>
									<td align="right" valign="top"><bean:message key="Product" bundle="sysele" />&nbsp;&nbsp;</td>
									<td colspan="3">
										<div id="div_product">
										<table id="tblProduct" align="left" border="0" cellpadding="3" cellspacing="1" width="100%" class="result">
											<c:forEach var="product1" items="${memberForm.memberProducts}" varStatus="rows3">
											<c:choose>
												<c:when test="${rows3.index %2 == 0}">
													<c:set var="tabclass" value="lineO" />
												</c:when>
												<c:otherwise>
													<c:set var="tabclass" value="lineE" />
												</c:otherwise>
											</c:choose>
											<tr style="cursor: pointer; cursor: hand;" class="<c:out value='${tabclass}'/>">
												<td align="left">
													<input type="hidden" name="prod.id" value="${product1.id}"/>
													<input type="hidden" name="prod.product.id" value="${product1.product.id}"/>
													<input type="hidden" name="prod.row" value="${rows3.index+1}"/>
													<input type="hidden" name="prod.product.code" value="${product1.product.code}"/>
													<input type="hidden" name="prod.product.name" value="${product1.product.name}"/>
													<input type='hidden' name='prod.uomId' value='${product1.uomId}'>
													<input type='hidden' name='prod.uomLabel' value='${product1.uomLabel}'>
													<input type='hidden' name='prod.orderQty' value='${product1.orderQty}'>
													${product1.product.code}&nbsp;${product1.product.name}
												</td>
												<td align="center" valign="middle" width="80px;">${product1.orderQty}&nbsp;&nbsp;${product1.uomLabel}</td>
											</tr>
											</c:forEach>
										</table>
										</div>
									</td>
								</tr>
								<tr>
									<td colspan="2"></td>
									<td colspan="4"><hr></td>
								</tr>
								<tr>
									<td colspan="2"></td>
									<td align="left">
										<html:checkbox property="member.exported" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Exported" bundle="sysele"/>
									</td>
									<td align="right"><bean:message key="Status" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left">
										<html:select property="member.isActive" disabled="true" styleClass="disableText">
											<html:option value="Y"><bean:message key="Active" bundle="sysprop" /></html:option>
											<html:option value="C"><bean:message key="Cancel" bundle="sysprop" /></html:option>
										</html:select>
									</td>
								</tr>
								<tr>
									<td colspan="2"></td>
									<td align="left">
										<html:checkbox property="member.interfaces" value="Y" disabled="true" styleClass="disableText"/><bean:message key="Interfaces" bundle="sysele"/>
									</td>
								</tr>
								<tr>
									<td align="right" colspan="2" valign="top"><bean:message key="CancelReason" bundle="sysele" />&nbsp;&nbsp;</td>
									<td align="left" colspan="4">
										<html:textarea property="member.cancelReason" cols="100" rows="3" styleClass="disableText" readonly="true"/>
									</td>
								</tr>
							</table>
							<!-- BUTTON -->
							<br/>
							<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
								<tr>
									<td align="center">
									<%if(isExpire){ %>
										<input type="button" value="�������" class="newTrxBtn" onclick="open_renew('${pageContext.request.contextPath}', '${memberForm.member.id}', 'search');"/>
									<%}else{ %>
										<%if(action.equals("renew")){ %>
											<input type="button" value="�������" class="newTrxBtn" onclick="open_renew('${pageContext.request.contextPath}', '${memberForm.member.id}', 'search');"/>
										<%}else{ %>
										<%} %>
									<%} %>
									<a href="#" onclick="toOrder('${pageContext.request.contextPath}','${memberForm.member.id}','mem')">
									<input type="button" value="����¡�â��" class="newTrxBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_salesorder.gif" border="1" class="newPicBtn"> --></a>
									<a href="#" onclick="toReceipt('${pageContext.request.contextPath}','${memberForm.member.id}','mem');">
									<input type="button" value="����¡���Ѻ�Թ" class="newTrxBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_receipt.gif" border="1" class="newPicBtn"> --></a>
									<a href="#" onclick="open_renewHist('${pageContext.request.contextPath}','${memberForm.member.id}');">
									<input type="button" value="����ѵԡ�õ������" class="newTrxBtn"/></a>
									<a href="#" onclick="recommented_member('${pageContext.request.contextPath}','${memberForm.member.id}');">
									<input type="button" value="<bean:message key="Recommend" bundle="sysprop"/>"  class="newTrxBtn"></a>
									</td>
								</tr>
							</table>
							<br />
							<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
								<tr>
									<td width="35%" align="right">
										<%if(!action.equals("renew")){ %>
											<%if(!isExpire){ %>
												<a href="#" onclick="prepare('${pageContext.request.contextPath}','add');">
												<!--<img src="${pageContext.request.contextPath}/images/b_new.gif" border="1" class="newPicBtn">-->
												<input type="button" value="���ҧ��¡������" class="newPosBtnLong">
												</a>
												<c:if test="${memberForm.member.exported=='N'}">	
												<a href="#" onclick="prepare('${pageContext.request.contextPath}','edit','${memberId}');">
												<!--<img src="${pageContext.request.contextPath}/images/b_edit.gif" border="1" class="newPicBtn">-->
												<input type="button" value="�����¡��" class="newPosBtnLong">
												</a>
												</c:if>
											<%} %>	
										<%} %>
									</td>
									<td align="right">
									<%if(action.equals("renew")){ %>
										<a href="#" onclick="renewsearch('${pageContext.request.contextPath}');">
										<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
										<input type="button" value="�Դ˹�Ҩ�" class="newNegBtn">
										</a>
									<%}else{ %>
										<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
										<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
										<input type="button" value="�Դ˹�Ҩ�" class="newNegBtn">
										</a>
									<%} %>
										
									</td>
									<td width="10%">&nbsp;</td>
								</tr>
							</table>
							<jsp:include page="../searchCriteria.jsp"></jsp:include>
							<jsp:include page="../trxhist.jsp">
								<jsp:param name="module" value="<%=TrxHistory.MOD_MEMBER %>"/>
								<jsp:param name="id" value="${memberForm.member.id}"/>
							</jsp:include>
							<html:hidden property="member.expiredDate"/>
							<html:hidden property="member.id"/>
							<html:hidden property="member.exported"/>
							<input type="hidden" name="type" id="type" value="${type}"/>
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