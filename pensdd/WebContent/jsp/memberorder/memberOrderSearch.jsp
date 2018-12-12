<%@ page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
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
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
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
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/memberOrder.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
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
				<jsp:param name="function" value="MemberOrderAdjust" />
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
						<html:form action="/jsp/memberOrderAction">
						<jsp:include page="../error.jsp" />
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td width="30%">&nbsp;</td>
								<td width="20%"></td>
								<td width="10%"></td>
							</tr>
							<tr>
								<td align="right"><bean:message key="DocumentNo" bundle="sysele"/>&nbsp;&nbsp;</td>
								<td align="left">
									<html:text property="order.orderNo" styleId="orderNo" size="20" maxlength="20" tabindex="1" onkeypress="nextKeypress(event)"/>
								</td>
								<td align="right"><bean:message key="Member.Code" bundle="sysele" /></td>
								<td align="left">
									<html:text property="order.memberCode" styleId="memberCode" size="20" maxlength="20" tabindex="2" onkeypress="searchKeypress(event,'${pageContext.request.contextPath}')"/>
								</td>
							</tr>
						</table>
						<br>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									<input type="button" value="ค้นหา" class="newPosBtn"></a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<input type="button" value="Clear" class="newNegBtn"></a>
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
									<th><bean:message key="Member" bundle="sysele"/></th>
									<th class="costprice"><bean:message key="TotalAmount" bundle="sysele" /></th>
									<th class="costprice">ยอดรวมแผนการจัดส่ง</th>
									<th class="status"><bean:message key="Exported" bundle="sysele"/></th>
									<th class="status"><bean:message key="Interfaces" bundle="sysele"/></th>
									<th class="status"><bean:message key="Order.Paid" bundle="sysele"/></th>
									<th class="status"><bean:message key="Status" bundle="sysele" /></th>
									<th class="status"><bean:message key="Edit" bundle="sysprop" /></th>
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
									<td width="36px;"><c:out value='${rows.index+1}' /></td>
									<td align="center">${results.orderNo}</td>
									<td align="center">${results.orderDate}</td>
									<td align="left">${results.customerName}</td>
									<td align="right">
									   <fmt:formatNumber pattern="#,#00.00"
										value="${results.totalNeedBill}"></fmt:formatNumber>
									</td>
									<td align="right">
									   <fmt:formatNumber pattern="#,#00.00"
										value="${results.netAmount}"></fmt:formatNumber>
									</td>
									<td align="center">
										<c:if test="${results.exported=='Y'}">
											<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
										</c:if>
									</td>
									<td align="center">
										<c:if test="${results.interfaces=='Y'}">
											<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
										</c:if>
									</td>
									<td align="center">
										<c:if test="${results.payment=='Y'}">
										<img border=0 src="${pageContext.request.contextPath}/icons/check.gif">
										</c:if>
									</td>
									<td align="center">${results.docStatusLabel}</td>
									<td align="center">
										<a href="#" onclick="javascript:prepare('${pageContext.request.contextPath}','${results.id}');">
										<img border=0 src="${pageContext.request.contextPath}/icons/doc_edit.gif"></a>
									</td>
								</tr>
							</c:forEach>
								<tr>
									<td align="left" class="footer" colspan="12">&nbsp;</td>
								</tr>
							</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
									<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
									<input type="button" value="ปิดหน้าจอ" class="newNegBtn"></a>
								</td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
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