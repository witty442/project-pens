<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<jsp:useBean id="receiptForm" class="com.isecinc.pens.web.receipt.ReceiptForm" scope="request"/>
<%
User user = (User) request.getSession().getAttribute("user");
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);
%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>

<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/receipt.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('receiptDateFrom'));
	new Epoch('epoch_popup','th',document.getElementById('receiptDateTo'));
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
				<jsp:param name="function" value="Receipt"/>
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
						<html:form action="/jsp/receiptAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td colspan="4">
									<%if(!role.equalsIgnoreCase(User.DD)){ %>
									<a href="#" onclick="prepare('${pageContext.request.contextPath}','add')">
									<img border=0 src="${pageContext.request.contextPath}/icons/doc_add.gif" align="absmiddle">&nbsp;<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
									<%} %>
									<%if(role.equalsIgnoreCase(User.DD)){ %>
									<a href="#" onclick="prepareMR('${pageContext.request.contextPath}','add')">
									<img border=0 src="${pageContext.request.contextPath}/icons/doc_add.gif" align="absmiddle">&nbsp;<bean:message key="CreateNewRecord" bundle="sysprop"/></a>
									<%} %>
								</td>
							</tr>
							<tr>
								<td width="35%"></td>
								<td width="17%"></td>
								<td width="12%"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right">
									<%if(!((User)session.getAttribute("user")).getType().equalsIgnoreCase(User.DD)){ %>
									<bean:message key="Customer" bundle="sysprop"/>&nbsp;&nbsp;
									<%}else{ %>
									<bean:message key="Member" bundle="sysprop"/>&nbsp;&nbsp;
									<%} %>
								</td>
								<td align="left" colspan="3">
									<html:text property="receipt.customerName" size="65" readonly="true" styleClass="disableText"/>
									<html:hidden property="receipt.customerId"/>
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Receipt.No" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="receipt.receiptNo" size="20"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="TransactionDate" bundle="sysele"/><bean:message key="DateFrom" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="receipt.receiptDateFrom" maxlength="10" size="15" readonly="true" styleId="receiptDateFrom" />
								</td>
								<td align="right"><bean:message key="DateTo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="receipt.receiptDateTo" maxlength="10" size="15" readonly="true" styleId="receiptDateTo" />
								</td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Bill.No"  bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left"><html:text property="receipt.searchInvoiceNo" size="20"/></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="Status" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td valign="top">
									<html:select property="receipt.docStatus">
										<html:option value=""></html:option>
										<html:options collection="docstatus" property="key" labelProperty="name"/>
									</html:select>
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
						<c:if test="${receiptForm.results != null}">
						<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;<bean:message key="RecordsFound"  bundle="sysprop"/>&nbsp;
						<span class="searchResult">${receiptForm.criteria.searchResult}</span>&nbsp;
						<bean:message key="Records"  bundle="sysprop"/></div>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th class="order"><bean:message key="No" bundle="sysprop"/></th>
								<th class="name"><bean:message key="Receipt.No" bundle="sysele"/></th>
								<th class="code"><bean:message key="TransactionDate" bundle="sysele"/></th>
								<th class="code"><bean:message key="Order.Payment" bundle="sysele"/></th>
								<th class="name"><bean:message key="Description"  bundle="sysele"/></th>
								<%if(user.getRole().getKey().equalsIgnoreCase(User.VAN)){ %>
								<th class="status"><bean:message key="Order.Paid"  bundle="sysele"/></th>
								<%} %>
								<th class="status"><bean:message key="Exported"  bundle="sysele"/></th>
								<th class="status"><bean:message key="Interfaces"  bundle="sysele"/></th>
								<%if(!user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
								<th class="name"><bean:message key="Bill.No"  bundle="sysele"/></th>
								<%} %>
								<th class="status"><bean:message key="Status" bundle="sysele"/></th>
								<%if(role.equalsIgnoreCase(User.DD)){ %>
								<th class="status">แบ่งชำระเป็นรายการ</th>
								<th class="status"><bean:message key="Edit" bundle="sysprop"/></th>
								<%} %>
								<%if(!role.equalsIgnoreCase(User.DD)){ %>
								<th class="status"><bean:message key="Edit" bundle="sysprop"/></th>
								<%} %>
								<th class="status"><bean:message key="View" bundle="sysprop"/></th>
							</tr>	
						<c:forEach var="results" items="${receiptForm.results}" varStatus="rows">
						<c:choose>
							<c:when test="${rows.index %2 == 0}">
								<c:set var="tabclass" value="lineO"/>
							</c:when>
							<c:otherwise>
								<c:set var="tabclass" value="lineE"/>
							</c:otherwise>
						</c:choose>
						<tr class="<c:out value='${tabclass}'/>">
							<td width="36px;"><c:out value='${rows.index+1}'/></td>
							<td align="center" width="96px;">${results.receiptNo}</td>
							<td align="center" width="76px;">${results.receiptDate}</td>
							<td align="right" width="67px;">
								<fmt:formatNumber pattern="#,##0.00" value="${results.receiptAmount}"/>
							</td>
							<td align="left" width="108px;">${results.description}</td>
							<%if(user.getRole().getKey().equalsIgnoreCase(User.VAN)){ %>
							<td align="center" width="43px;">
								<c:if test="${results.prepaid=='Y'}">
								<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
							<%} %>
							<td align="center" width="47px;">
								<c:if test="${results.exported=='Y'}">
								<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
							<td align="center" width="47px;">
								<c:if test="${results.interfaces=='Y'}">
								<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
							<%if(!user.getRole().getKey().equalsIgnoreCase(User.DD)){ %>
							<td align="center" width="97px;">${results.invoiceNoLabel}</td>
							<%} %>
							<td align="center" width="48px;">${results.docStatusLabel}</td>
							
							<%if(role.equalsIgnoreCase(User.DD)){ %>
							<td align="center">
								<c:if test="${results.prepaid=='N'}">
								<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
								</c:if>
							</td>
							<td align="center">
								<c:if test="${results.exported=='N'}">
									<c:if test="${results.docStatus=='SV'}">
										<c:if test="${results.prepaid=='N'}">
										<!-- *** OLD CODE ********** -->
<!--										<a href="#" onclick="javascript:prepareMR('${pageContext.request.contextPath}','edit','${results.id}');">-->
<!--										<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>-->
										<!-- *** OLD CODE ********** -->
										<!-- WIT EDIT 03/08/2554*** -->
<!--										<a href="#" onclick="javascript:openCancelReceiptPopup('${pageContext.request.contextPath}','${results.receiptNo}','${results.id}');">-->
<!--											                                    ยกเลิกรายการ</a>-->
										<!-- WIT EDIT 03/08/2554*** -->
										</c:if>
									</c:if>
								</c:if>
							</td>
							<%} %>
							<%if(!role.equalsIgnoreCase(User.DD)){ %>
							<td align="center" width="47px;">
								<c:if test="${results.exported=='N'}">
									<c:if test="${results.docStatus=='SV'}">
										<c:if test="${results.prepaid=='N'}">
										   <!-- *** OLD CODE ********** -->
<!--											<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','edit','${results.id}');">-->
<!--											<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>-->

											<!-- *** OLD CODE ********** -->
											<!-- WIT EDIT 03/08/2554*** -->
<!--											<a href="#" onclick="javascript:openCancelReceiptPopup('${pageContext.request.contextPath}','${results.receiptNo}','${results.id}');">-->
<!--											                                    ยกเลิกรายการ</a>-->
										    <!-- WIT EDIT 03/08/2554*** -->
										</c:if>
									</c:if>
								</c:if>
							</td>
							<%} %>
							
							<td align="center">
								<%if(!role.equalsIgnoreCase(User.DD)){ %>
								<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','view','${results.id}');">
								<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								<%} %>
								<%if(role.equalsIgnoreCase(User.DD)){ %>
								<a href="#" onclick="javascript:prepareMR('${pageContext.request.contextPath}','view','${results.id}');">
								<img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif"></a>
								<%} %>
							</td>
						</tr>
						</c:forEach>
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td align="left" class="footer">&nbsp;</td>
							</tr>
						</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="backToCusotmer('${pageContext.request.contextPath}','${receiptForm.receipt.customerId}');">
									<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
									</a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						<html:hidden property="receipt.orderType"/>
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
</body>
</html>