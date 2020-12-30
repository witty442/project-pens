<%@page import="com.isecinc.pens.web.customer.CustomerHelper"%>
<%@page import="com.pens.util.*"%>
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
if(action==null)action="view";
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
%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<%@page import="com.isecinc.pens.bean.TrxHistory"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME%>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SIdUtils.getInstance().getIdSession() %>" type="text/css" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}
-->
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customer.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/customerTransaction.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/popup.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/google_maps.js?v=<%=SIdUtils.getInstance().getIdSession()%>"></script>
<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%=GoogleMapJavaScriptAPI.getInstance().getAPIKey() %>" type="text/javascript"></script>

<script type="text/javascript">

</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0" onload="MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')" style="height: 100%;">
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
								  <html:radio property="customer.printType" styleId="printType" value="H" onclick="switchPrintType()" disabled="true"></html:radio>�ӹѡ�ҹ�˭� 
								</td>
								<td align="left">
									 <html:radio property="customer.printType" styleId="printType" value="B" onclick="switchPrintType()" disabled="true"></html:radio>�Ңҷ��
								     <html:text property="customer.printBranchDesc" styleId="printBranchDesc" readonly="true" styleClass="disableText"/>
								&nbsp;&nbsp;&nbsp;
								��˹��ش #1
								<font color="red"></font>
								<html:text property="customer.tripDay" size="10" styleId="tripDay"  readonly="true" styleClass="disableText" />
								 <%-- <html:select property="customer.tripDay"  disabled="true">
										<html:options collection="tripDayList" property="key" labelProperty="name"/>
									</html:select>
									&nbsp;�ش #2
								 <html:select property="customer.tripDay2"  disabled="true">
										<html:options collection="tripDayList" property="key" labelProperty="name"/>
									</html:select>
									&nbsp;�ش #3
								 <html:select property="customer.tripDay3"  disabled="true">
										<html:options collection="tripDayList" property="key" labelProperty="name"/>
									</html:select> --%>
								 </td>
							</tr>
							<tr>
								<td align="right" colspan="2"><b><bean:message key="Customer.Code" bundle="sysele"/>&nbsp;&nbsp;</b></td>
								<td align="left">
									<html:text property="customer.code" readonly="true" styleClass="disableTextBigSize" styleId="customerCode"/>
								</td>
								<td align="right"><bean:message key="Customer.PartyType" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:select property="customer.businessType" disabled="true" styleClass="businessType">
										<%-- <html:option value=""></html:option>
										<html:option value="P"><bean:message key="PartyType.Personal" bundle="sysele"/></html:option>
										<html:option value="O"><bean:message key="PartyType.Org" bundle="sysele"/></html:option> --%>
									    <html:options collection="partyTypeList" property="key" labelProperty="name"/>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2"><bean:message key="Customer.Name" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.name" size="25" readonly="true" styleClass="disableText" styleId="customerName"/>
								</td>
								<%if(action.equals("view")){ %>
								<td align="right"><bean:message key="Customer.SubName" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="customer.name2" size="25" readonly="true" styleClass="disableText"/>
								</td>
								<%} %>
							</tr>
							<%if(action.equals("view")){ %>
							<tr>
								<td align="right" colspan="2">
								   <bean:message key="TaxNo" bundle="sysele"/>&nbsp;&nbsp;
								 
								</td>
								<td align="left" nowrap>
									<html:text property="customer.taxNo" size="25" readonly="true" styleClass="disableText"/>
									  <html:checkbox property="customer.printTax" value="Y" disabled="true">������Ţ��Шӵ�Ǽ����������</html:checkbox>
									  
									   &nbsp;
								   <html:checkbox property="customer.printHeadBranchDesc" value="Y" disabled="true">�����ʹ�./�Ңҷ��</html:checkbox>
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
								<td align="right"><%-- <bean:message key="Customer.BusinessType" bundle="sysele"/>&nbsp;&nbsp; --%></td>
								<td align="left">
								<%-- 	<html:text property="customer.businessType" size="25" readonly="true" styleClass="disableText"/> --%>
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
									<html:checkbox property="customer.airpayFlag" value="Y" disabled="true" styleClass="disableText"/>
									�����м�ҹ�к�������� (Air Pay)
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
							<%if(action.equals("view")){ %>
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
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<%} %>
							<%if(role.equalsIgnoreCase(User.TT)){ %>
							<%if(action.equalsIgnoreCase("view")){ %>
							<tr>
								<td align="right" colspan="2"></td>
								<td align="left">
									<html:checkbox property="customer.creditCheck" value="Y" disabled="true"/>&nbsp;Credit Check
								</td>
							</tr>
							<%} %>
							<%} %>
							<%if(action.equalsIgnoreCase("view")){ %>
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
							<%if(action.equalsIgnoreCase("view")){ %>
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
							<%if(action.equalsIgnoreCase("view")){ %>
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
							<tr>
								<td></td>
								<td colspan="4"><hr></td>
							</tr>
							<tr>
								<td align="right" colspan="2"></td>
								<td align="left" colspan="3">
								     <img id="blah" /> 
									 <%if( !Utils.isNull(customerForm.getCustomer().getImageFileName()).equals("")){ %>
									       <img src="${pageContext.request.contextPath }/photoCustomerServlet?customerId=${customerForm.customer.id}" width="150" height="200" border="0"/>
									<%} %>
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2">����ٻ��ҹ���</td>
								<td align="left" colspan="3">
								   <%if( !Utils.isNull(customerForm.getCustomer().getImageFileName()).equals("")){ %>
									     <html:text property="customer.imageFileName" readonly="true" styleClass="disableText" size="100" styleId="imageFileName"></html:text>
									<%}else{ %>
									     <html:hidden property="customer.imageFileName" ></html:hidden>
									 <%} %>
									
									<input type="button" value="�ʴ��ٻ�Ҿ����� " class="newPosBtn" onclick="return showImage('${pageContext.request.contextPath}','${customerForm.customer.id }');">
									 
								</td>
							</tr>
							<tr>
								<td align="right" colspan="2">�ѹ�֡���˹觷������ҹ���&nbsp;&nbsp;</td>
								<td align="left" colspan="3">
									<html:text property="customer.location" size="100" readonly="true" styleId="location" styleClass="disableText" /> 
								    <html:hidden property="customer.lat" styleId="lat"  /> 
								    <html:hidden property="customer.lng" styleId="lng"   /> 
								</td>
							</tr>
							<tr>
							    <td align="right" colspan="2"></td>
									<td align="left" colspan="3">
										<input type="button" value="�ʴ����˹� " class="newPosBtn" onclick="return gotoMap('${pageContext.request.contextPath}');">
								</td>
							</tr>
						
						</table>
						<br />
						<!-- BUTTON -->
					
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="center">
									<a href="#" onclick="toProduct('${pageContext.request.contextPath}','${customerForm.customer.id}','cus');">
									<input type="button" value="�������Թ��Ҿ�����������" class="newPosBtn"></a>
									<a href="#" onclick="toOrder('${pageContext.request.contextPath}','${customerForm.customer.id}','cus');">
									<input type="button" value="����¡�â��" class="newPosBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_salesorder.gif" border="1" class="newPicBtn"> --></a>
									<a href="#" onclick="toReceipt('${pageContext.request.contextPath}','${customerForm.customer.id}','cus');">
									<input type="button" value="����¡���Ѻ�Թ" class="newPosBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_receipt.gif" border="1" class="newPicBtn"> --></a>
									<a href="#" onclick="toVisit('${pageContext.request.contextPath}', '${customerForm.customer.id}', 'cus', '<%=action %>')">
									<input type="button" value="����������١���" class="newPosBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_visit.gif" border="1" class="newPicBtn"> --></a>
									<a href="#" onclick="open_invoiceOst('${pageContext.request.contextPath}','${customerForm.customer.id}');">
									<input type="button" value="�ʹ���˹���ҧ����" class="newPosBtn"></a>
									<a href="#" onclick="toCreateNewReqPromotion('${pageContext.request.contextPath}','${customerForm.customer.id}','customerView');">
									<input type="button" value="�͹��ѵԨѴ��¡��" class="newPosBtn"></a>
								</td>
							</tr>
						</table>
						<br/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="30%" align="right">
									<%if(role.equalsIgnoreCase(User.VAN) && action.equals("view")){ %>
									<a href="#" onclick="prepare('${pageContext.request.contextPath}','add');">
									<input type="button" value="���ҧ��¡������" class="newPosBtnLong">
									<!-- <img src="${pageContext.request.contextPath}/images/b_new.gif" border="1" class="newPicBtn"> --></a>	
									<%} %>
									<%if(role.equalsIgnoreCase(User.VAN) && action.equals("view")){ %>
									<c:if test="${customerForm.customer.orderAmount==0}">
									<c:if test="${customerForm.customer.exported!='Y'}">
									<a href="#" onclick="prepare('${pageContext.request.contextPath}','edit','${customerForm.customer.id}');">
									<input type="button" value="�����¡��" class="newPosBtnLong">
									<!-- <img src="${pageContext.request.contextPath}/images/b_edit.gif" border="1" class="newPicBtn"> --></a>
									</c:if>
									</c:if>	
									<%} %>
								</td>
								<td align="right">
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}');">
									<input type="button" value="�Դ˹�Ҩ�" class="newNegBtn">
									<!-- <img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"> --></a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<div align="left" style="width: 80%;" class="programlabel">
						 <bean:message key="TrxHist" bundle="sysprop"/>
						 <a href="javascript:gotoOrderSearch();">
						 <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"></a>
						</div>

						<jsp:include page="../searchCriteria.jsp"></jsp:include>
						<%-- <jsp:include page="../trxhist.jsp">
							<jsp:param name="module" value="<%=TrxHistory.MOD_CUSTOMER %>"/>
							<jsp:param name="id" value="${customerForm.customer.id}"/>
						</jsp:include> --%>
						<html:hidden property="customer.id" styleId="customerId"/>
						<html:hidden property="customer.exported"/>
						<input type="hidden" name ="path" id="path" value="${pageContext.request.contextPath}"/>
						
						<input type="hidden" name="tf" value="<%=session.getAttribute("tf") %>">
						<div id="addressList" style="text-align: left;display: none;"></div>
						<div id="contactList" style="text-align: left;display: none;"></div>
						<div title="CustomerView">...</div>
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