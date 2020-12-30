<%@page import="com.pens.util.Utils"%>
<%@page import="com.pens.util.SessionUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.pens.util.SIdUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="orderForm" class="com.isecinc.pens.web.sales.OrderForm" scope="request" />
<%
/*clear session form other page */
SessionUtils.clearSessionUnusedForm(request, "orderForm");


User user = (User) request.getSession().getAttribute("user");
String userName = user.getUserName();
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";
//System.out.println(isAdd);

List<District> districtsAll = new ArrayList<District>();
District dBlank = new District();
dBlank.setId(0);
dBlank.setName("");
districtsAll.add(dBlank);

List<District> districts = new MDistrict().lookUp();
districtsAll.addAll(districts);
pageContext.setAttribute("districts", districtsAll, PageContext.PAGE_SCOPE);

List<References> territorys = InitialReferences.getReferenes().get(InitialReferences.TERRITORY);
pageContext.setAttribute("territorys", territorys, PageContext.PAGE_SCOPE);

%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>" /></title>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/salesOrder.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/number.js?v=<%=SIdUtils.getInstance().getIdSession() %>"></script>

<script type="text/javascript">
	function loadMe() {
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));
		
		loadProvince();
		
		document.getElementsByName('order.searchProvince')[0].value = ${orderForm.order.searchProvince};
		
		loadDistrict(); 
		<%if( !"".equals(orderForm.getOrder().getDistrict())){ %>
		  document.getElementsByName('order.district')[0].value = <%=orderForm.getOrder().getDistrict()%>;
		<% } %>
		
		sumTotalNetAmount();
	}
	function loadProvince(){
		var cboProvince = document.getElementsByName('order.searchProvince')[0];
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/ProvinceTerritory.jsp",
				data : "refId=" + document.getElementsByName('order.territory')[0].value,
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					cboProvince.innerHTML=returnString;
				}
			}).responseText;
		});
	}

	function loadDistrict(){
		var cboDistrict = document.getElementsByName('order.district')[0];
		$(function(){
			var getData = $.ajax({
				url: "${pageContext.request.contextPath}/jsp/ajax/DistrictAjax.jsp",
				data : "refId=" + document.getElementsByName('order.searchProvince')[0].value,
				async: false,
				success: function(getData){
					var returnString = jQuery.trim(getData);
					cboDistrict.innerHTML=returnString;
				}
			}).responseText;
		});
	}
	function sumTotalNetAmount(){
		var tempNetAmount = document.getElementsByName("tempNetAmount");
		var docStatusItem = document.getElementsByName("docStatusItem");
		var totalTempNetAmount = 0;
		if(tempNetAmount.length >0){
			for(var i=0;i<tempNetAmount.length;i++){
				if(docStatusItem[i].value !='CANCEL'){
				   totalTempNetAmount +=convetTxtObjToFloat(tempNetAmount[i]);
				}
			}
			document.getElementById("totalTempNetAmount").innerHTML =addCommas(Number(toFixed(totalTempNetAmount,2)).toFixed(2)); 
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
				<jsp:param name="function" value="SalesOrder" />
				<jsp:param name="code" value=""/>
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
						<html:form action="/jsp/saleOrderAction">
						<jsp:include page="../error.jsp" />
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<c:if test="${orderForm.order.priceListId!=0}">
								<td colspan="4" align="left">
									<a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
									<img border=0 src="${pageContext.request.contextPath}/icons/doc_add.gif" align="absmiddle">
									&nbsp;<bean:message key="CreateNewRecord" bundle="sysprop" /></a>
								</td>
								</c:if>
								<c:if test="${orderForm.order.priceListId==0}">
								<td colspan="4" align="center">
									<span class="errormsg">${orderForm.order.pricelistLabel}</span>
									<html:hidden property="order.pricelistLabel"/>
								</td>
								</c:if>
							</tr>
							<tr>
								<td width="35%"></td>
								<td width="17%"></td>
								<td width="12%"></td>
								<td></td>
							</tr>
							 <tr>
							<td width="35%" align="right"><bean:message key="Customer.Territory" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="2">
								<html:select property="order.territory" onchange="loadProvince();">
									<html:option value=""></html:option>
									<html:options collection="territorys" property="key" labelProperty="name"/>
								</html:select>
							</td>
							
						</tr>
						<tr>
						    <td align="right"><bean:message key="Address.Province" bundle="sysele"/>&nbsp;&nbsp;</td>
							<td align="left" colspan="3">
								<html:select property="order.searchProvince" onchange="loadDistrict();">
								</html:select>
							&nbsp;&nbsp;
							     เขต/อำเภอ
							     <html:select property="order.district" styleId="district">
									<%-- <html:options collection="districts" property="id" labelProperty="name"/> --%>
								</html:select>
							</td>
						</tr>
							<tr>
								<td align="right"><bean:message key="DocumentNo"
									bundle="sysele" />&nbsp;&nbsp;</td>
								<td align="left" colspan="3"><html:text property="order.orderNo" size="20" styleClass="\" autoComplete=\"off"/></td>
							</tr>
							<tr>
							  <td align="right"><bean:message key="Customer.Code" bundle="sysele"/>&nbsp;&nbsp;</td>
							  <td align="left" colspan="3">
							  <html:text property="order.customerCode"  styleClass="\" autoComplete=\"off" />
							  &nbsp;&nbsp;
							   <bean:message key="Customer.Name" bundle="sysele"/>
							   <html:text property="order.customerName"  styleClass="\" autoComplete=\"off" />
						       <html:hidden property="order.customerId" />
							  </td>
						    </tr>
						
							<tr>
								<td align="right"><bean:message key="TransactionDate"
									bundle="sysele" /> <bean:message key="DateFrom" bundle="sysele" />&nbsp;&nbsp;
								</td>
								<td align="left" colspan="3">
								    <html:text property="order.orderDateFrom"
									maxlength="10" size="15" readonly="true" styleId="orderDateFrom" />
									&nbsp;&nbsp;&nbsp;&nbsp;
								    <bean:message key="DateTo" bundle="sysele" />&nbsp;&nbsp;
								    <html:text property="order.orderDateTo"
									maxlength="10" size="15" readonly="true" styleId="orderDateTo" />
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Status" bundle="sysele" />&nbsp;&nbsp;</td>
								<td align="left" nowrap><html:select property="order.docStatus">
									<html:option value=""></html:option>
									<html:options collection="docstatus" property="key"
										labelProperty="name" />
								</html:select>
								&nbsp;&nbsp;PickingNo&nbsp;<html:text property="order.pickingNo" size="15" styleClass="\" autoComplete=\"off" ></html:text>
								</td>
							</tr>
						</table>
						<br>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								
									<input type="button" value="ค้นหา" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
									<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
									
								</td>
							</tr>
						</table>
						<c:if test="${orderForm.results != null}">
							<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
							<bean:message key="RecordsFound" bundle="sysprop" /> &nbsp;
							<span class="searchResult">${orderForm.criteria.searchResult}</span>&nbsp;<bean:message key="Records" bundle="sysprop" /></div>
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
								<tr>
									<th class="order"><bean:message key="No" bundle="sysprop" /></th>
									<th class="code"><bean:message key="DocumentNo" bundle="sysele" /></th>
									<th class="code"><bean:message key="TransactionDate" bundle="sysele" /></th>
									<th class="costprice"><bean:message key="TotalAmount" bundle="sysele" /></th>
									<th class="status">รหัสร้านค้า</th>
									<th class="status">ชื่อร้านค้า</th>
									<th class="status">ที่อยู่ส่ง</th>
									<th class="status"><bean:message key="Status" bundle="sysele" /></th>
									<th class="status">Picking No</th>
									<th class="status">ทำรายการ</th>
									<th class="status"><bean:message key="View" bundle="sysprop" /></th>
								</tr>
							<c:forEach var="results" items="${orderForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td width="2%"><c:out value='${rows.index+1}' /></td>
									<td align="center" width="7%">${results.orderNo}</td>
									<td align="center" width="7%">${results.orderDate}</td>
									<td align="right" width="7%"><fmt:formatNumber pattern="#,#00.00"
										value="${results.netAmount}"></fmt:formatNumber>
										<input type="hidden" name="tempNetAmount" value="${results.netAmount}"/>
										</td>
									<td align="center" width="5%">${results.customerCode}</td>
									<td align="left" width="15%">${results.customerName}</td>
									<td align="left" width="30%">${results.addressSummary}</td>
									<td align="center" width="8%">
									${results.docStatusLabel}
									<input type="hidden" name="docStatusItem" value="${results.docStatus}"/>
									</td>
									<td align="center" width="8%">${results.pickingNo}</td>
									<td align="center" width="8%">
									<%if( !userName.equalsIgnoreCase("ADMIN")){ %>
										<c:if test="${results.exported=='N'}">
											<c:if test="${results.docStatus =='OPEN' or results.docStatus =='REJECT'
											    or results.docStatus =='UNAVAILABLE' or results.docStatus =='RESERVE'}">
												<c:if test="${results.payment=='N'}">
												    <c:choose>
													<c:when test="${results.promotionSP == true}">
						                               <a href="#" onclick="javascript:prepareEditOrderSP('${pageContext.request.contextPath}','edit','${results.id}');">
												       <img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
													</c:when>
													<c:otherwise>
														<a href="#" onclick="javascript:prepareEditOrder('${pageContext.request.contextPath}','edit','${results.id}');">
												       <img border=0 src="${pageContext.request.contextPath}/icons/process.gif"></a>
													</c:otherwise>
												    </c:choose>
												</c:if>
											</c:if>
										</c:if>
									<%} %>
										<%if(role.equals(User.VAN)){ %>
											<c:if test="${results.exported=='Y'}">
												<c:if test="${results.docStatus=='SV'}">
													<c:if test="${results.payment=='N'}">
														<a href="#" onclick="javascript:prepareEditReceipt('${pageContext.request.contextPath}','edit','${results.id}');">
													      <img border=0 src="${pageContext.request.contextPath}/icons/process.gif" title="รายการนี้ยังไม่ทำรายการรับเงิน"></a>						   
													</c:if>
												</c:if>
											</c:if>
									    <% } %>
									</td>
									<td align="center">
								     <c:choose>
										<c:when test="${results.promotionSP == true}">
			                              	<a href="#" onclick="javascript:prepareSP('${pageContext.request.contextPath}','view','${results.id}');">
									         <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
										</c:when>
										<c:otherwise>
										     <a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','${results.id}');">
									         <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
										</c:otherwise>
									    </c:choose>
									</td>
								</tr>
							</c:forEach>
								<tr>
									<td align="right" colspan="3"><b>Total</b></td>
									<td align="right" ><b><span id="totalTempNetAmount"></span></b></td>
									<td align="left" colspan="9">&nbsp;</td>
								</tr>
							<!-- 	<tr>
									<td align="left" class="footer" colspan="13">&nbsp;</td>
								</tr> -->
							</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
								   <input type="button" value="ปิดหน้าจอ" class="newNegBtn" onclick="backToCusotmer('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<html:hidden property="order.roundTrip"/>
						<html:hidden property="order.priceListId"/>
						<html:hidden property="order.orderType" value="<%=role %>" />
						<input type="hidden" name="fromPage" id="fromPage" value ="<%=Utils.isNull(request.getParameter("fromPage"))%>"/>
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