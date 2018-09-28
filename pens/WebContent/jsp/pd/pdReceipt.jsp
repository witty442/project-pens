<%@page import="util.SessionGen"%>
<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout"%>
<jsp:useBean id="pdReceiptForm"
	class="com.isecinc.pens.web.pd.PDReceiptForm" scope="request" />
<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

String today = DateToolsUtil.getCurrentDateTime("dd/MM/yyyy");
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="util.DateToolsUtil"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop"
		key="<%=SystemProperties.PROJECT_NAME %>" />
</title>
<link rel="StyleSheet"
	href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet"
	href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>"
	type="text/css" />
<style type="text/css">
<!--
body {
	background-image:
		url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}

.style1 {
	color: #004a80
}
-->
</style>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/webstyle.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/strfunc.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/input.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/pdreceipt.js?v=<%=SessionGen.getInstance().getIdSession()%>"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
	function loadMe() {
		new Epoch('epoch_popup', 'th', document
				.getElementById('receiptDateFrom'));
		new Epoch('epoch_popup', 'th', document.getElementById('receiptDateTo'));
	}
</script>
</head>
<body topmargin="0" rightmargin="0" leftmargin="0" bottommargin="0"
	onload="loadMe();MM_preloadImages('${pageContext.request.contextPath}/images2/button_logout2.png')"
	style="height: 100%;">
	<table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0" style="bottom: 0; height: 100%;" id="maintab">
		<tr>
			<td colspan="3"><jsp:include page="../header.jsp" /></td>
		</tr>
		<tr id="framerow">
			<td width="25px;"
				background="${pageContext.request.contextPath}/images2/content_left.png"></td>
			<td
				background="${pageContext.request.contextPath}/images2/content01.png"
				valign="top">
				<div style="height: 60px;">
					<!-- MENU -->
					<table width="100%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="txt1">
						<tr>
							<td width="100%"><jsp:include page="../menu.jsp" /></td>
						</tr>
					</table>
				</div> 
				
				<!-- PROGRAM HEADER --> 
				<jsp:include page="../program.jsp">
					<jsp:param name="function" value="PD" />
					<jsp:param name="code" value="" />
				</jsp:include> 
				
				<!-- TABLE BODY -->
				<table width="100%" border="0" align="center" cellpadding="0"
					cellspacing="0" class="txt1">
					<tr style="height: 9px;">
						<td width="5px;"
							background="${pageContext.request.contextPath}/images2/boxcont1_1.gif" /></td>
						<td width="832px;"
							background="${pageContext.request.contextPath}/images2/boxcont1_5.gif" /></td>
						<td width="5px;"
							background="${pageContext.request.contextPath}/images2/boxcont1_2.gif" /></td>
					</tr>
					<tr>
						<td width="5px;"
							background="${pageContext.request.contextPath}/images2/boxcont1_8.gif"></td>
						<td bgcolor="#f8f8f8">
							<!-- BODY --> <html:form action="/jsp/pdReceipt">
								<jsp:include page="../error.jsp" />
								<table align="center" border="0" cellpadding="3" cellspacing="0"
									width="60%">
									<tr>
										<td width="35%"></td>
										<td width="17%"></td>
										<td width="12%"></td>
										<td></td>
									</tr>
									<tr>
										<td align="right"><bean:message key="Order.No"
												bundle="sysele" />
											<bean:message key="From" bundle="sysele" />&nbsp;&nbsp;</td>
										<td align="left"><html:text
												property="receipt.orderNoFrom" maxlength="10" size="15"
												styleId="orderNoFrom" /></td>
										<td align="right"><bean:message key="To" bundle="sysele" />&nbsp;&nbsp;</td>
										<td align="left"><html:text property="receipt.orderNoTo"
												maxlength="10" size="15" styleId="orderNoTo" /></td>
									</tr>
									<tr>
										<td align="right"><bean:message key="TransactionDate"
												bundle="sysele" />
											<bean:message key="DateFrom" bundle="sysele" />&nbsp;&nbsp;</td>
										<td align="left"><html:text
												property="receipt.receiptDateFrom" maxlength="10" size="15"
												readonly="true" styleId="receiptDateFrom" /></td>
										<td align="right"><bean:message key="DateTo"
												bundle="sysele" />&nbsp;&nbsp;</td>
										<td align="left"><html:text
												property="receipt.receiptDateTo" maxlength="10" size="15"
												readonly="true" styleId="receiptDateTo" /></td>
									</tr>
								</table>
								<br>
								<table align="center" border="0" cellpadding="3" cellspacing="0"
									class="body">
									<tr>
										<td align="center"><a
											href="javascript:search('${pageContext.request.contextPath}')">
												<input type="button" value="ค้นหา" class="newPosBtn">
												<!-- <img src="${pageContext.request.contextPath}/images/b_search.gif" border="1" class="newPicBtn"> -->
										</a> <a
											href="javascript:clearForm('${pageContext.request.contextPath}')">
												<input type="button" value="Clear" class="newNegBtn">
												<!-- <img src="${pageContext.request.contextPath}/images/b_clear.gif" border="1" class="newPicBtn"> -->
										</a>
										<a href="#" onclick="window.close();">
										   <input type="button" value="ปิดหน้าจอ" class="newPosBtnLong">
										</a>
										</td>
									</tr>
								</table>
								<c:if test="${pdReceiptForm.pdReceipts != null}">
									<div align="left" class="recordfound">
										&nbsp;&nbsp;&nbsp;
										<bean:message key="RecordsFound" bundle="sysprop" />
										&nbsp; <span class="searchResult">${pdReceiptForm.criteria.searchResult}</span>&nbsp;
										<bean:message key="Records" bundle="sysprop" />
									</div>
									<table align="center" border="0" cellpadding="3"
										cellspacing="1" class="result" >
										<tr>
											<th class="order"><bean:message key="No"
													bundle="sysprop" />
											</th>
											<th class="code"><bean:message key="Order.No" bundle="sysele" />
											</th>
											<th class="code"><bean:message key="Order.Date" bundle="sysele" />
											</th>
											<th class="code"><bean:message key="Trip.CustomerName" bundle="sysele" />
											</th>
											<th class="code"><bean:message key="Receipt.Amount"
													bundle="sysele" />
											</th>
											<th class="code"><bean:message key="Profile.PaymentMethod"
													bundle="sysele" />
											</th>
											<th class="code"><bean:message key="PDReceipt.Date"
													bundle="sysele" />
											</th>
										</tr>
										<c:forEach var="results" items="${pdReceiptForm.pdReceipts}"
											varStatus="rows">
											<c:choose>
												<c:when test="${rows.index %2 == 0}">
													<c:set var="tabclass" value="lineO" />
												</c:when>
												<c:otherwise>
													<c:set var="tabclass" value="lineE" />
												</c:otherwise>
											</c:choose>
											<tr class="<c:out value='${tabclass}'/>">
												<td><c:out value='${rows.index+1}'/></td>
												<td>${results.receiptNo}</td>
												<td>${results.receiptDate}</td>
												<td align="left">${results.customerName}</td>
												<td><fmt:formatNumber pattern="#,##0.00" value="${results.receiptAmount}"/></td>
												<td>
													<input name="receiptId" type="hidden" value="${results.id}" />
													<select name="pd.paymentMethod" onchange="defaultDate(this,'${rows.index+1}','<%=today%>')" >
														<option value="">&nbsp;</option>
														<option value="CS"><bean:message key="PaymentMethod.Cash" bundle="sysele" /></option>
														<option value="CH"><bean:message key="PaymentMethod.Cheque" bundle="sysele" /></option>
													</select>
												</td>
												<td>
													<input type="text" name="pdReceiptDate" maxlength="10" size="15" value="" readonly="readonly" id="pdReceiptDate">
												</td>
											</tr>
										</c:forEach>
									</table>
									<table align="center" border="0" cellpadding="3"
										cellspacing="1" class="result">
										<tr>
											<td align="left" class="footer">&nbsp;</td>
										</tr>
									</table>
									<script>
										var pdReceiptDate = document.getElementsByName("pdReceiptDate");

										for(var i=0;i<pdReceiptDate.length ; i++){
											new Epoch('epoch_popup', 'th', pdReceiptDate[i]);
										}
									
									</script>
								<br>
								<!-- BUTTON -->
								<table align="center" border="0" cellpadding="3" cellspacing="0"
									width="50%">
									<tr>
										<td align="center">
											<!-- a href="#" onclick="backToCusotmer('${pageContext.request.contextPath}','${receiptForm.receipt.customerId}');" -->
											<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
											<a href="#" onclick="save('${pageContext.request.contextPath}')" >
											<input type="button" value="บันทึก" class="newNegBtn">
											</a>
											<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
											<input type="button" value="ยกเลิก" class="newNegBtn">
											</a></td>
										<td width="10%">&nbsp;</td>
									</tr>
								</table>
								</c:if>
								<!--html:hidden property="receipt.orderType"/-->
								<jsp:include page="../searchCriteria.jsp"></jsp:include>
							</html:form> <!-- BODY --></td>
						<td width="6px;"
							background="${pageContext.request.contextPath}/images2/boxcont1_6.gif"></td>
					</tr>
					<tr style="height: 9px;">
						<td width="5px;"
							background="${pageContext.request.contextPath}/images2/boxcont1_4.gif" /></td>
						<td
							background="${pageContext.request.contextPath}/images2/boxcont1_7.gif"></td>
						<td width="5px;"
							background="${pageContext.request.contextPath}/images2/boxcont1_3.gif" /></td>
					</tr>
				</table></td>
			<td width="25px;"
				background="${pageContext.request.contextPath}/images2/content_right.png"></td>
		</tr>
		<tr>
			<td width="25px;"
				background="${pageContext.request.contextPath}/images2/content_left.png"></td>
			<td
				background="${pageContext.request.contextPath}/images2/content01.png"
				valign="top"><jsp:include page="../contentbottom.jsp" /></td>
			<td width="25px;"
				background="${pageContext.request.contextPath}/images2/content_right.png"></td>
		</tr>
		<tr>
			<td colspan="3"><jsp:include page="../footer.jsp" /></td>
		</tr>
	</table>
</body>
</body>
</html>