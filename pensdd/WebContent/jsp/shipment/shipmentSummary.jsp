<%@page import="java.sql.ResultSet"%>
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
<jsp:useBean id="shipmentForm" class="com.isecinc.pens.web.shipment.ShipmentForm" scope="request" />
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

table.result th.confirm{
width:100%;
text-align: right;
padding-left: 10px;
padding-bottom: 3px;
padding-right: 30px;
padding-top: 3px;
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/shipconfirm.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
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
	    	<%if(null == request.getParameter("page")){ %>
	      	    <jsp:include page="../program.jsp">
				<jsp:param name="function" value="ShipConfirm" />
				<jsp:param name="code" value=""/>
				</jsp:include>
			<%}else { %>
			    <jsp:include page="../program.jsp">
			    <jsp:param name="function" value="CancelShipmentConfirmation" />
			    <jsp:param name="code" value=""/>
			    </jsp:include>
			<%} %>
				
			
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
						<html:form action="/jsp/shipmentAction">
						<jsp:include page="../error.jsp" />
						
						<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<td colspan="4" class="confirm">จำนวนรายการทั้งหมด  ${shipmentForm.summary.no_of_confirm} รายการ</td>
							</tr>
						</table>
						
						<c:if test="${shipmentForm.summary.successSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6" align="left">จำนวนรายการที่สำเร็จ   ${shipmentForm.summary.successSize} รายการ</th>
							</tr>
							<tr>
								<th width="5%"><bean:message key="No" bundle="sysprop"/></th>
								<th width="15%">เลขที่ใบขาย</th>
								<th width="5%">ครั้งที่ส่ง</th>
								<th width="5%">วันที่ส่งของ</th>
								<th width="40%">ลูกค้า</th>
								<th width="35%">Message </th>
							</tr>
							<c:forEach var="results" items="${shipmentForm.summary.successList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td><c:out value='${rows.index+1}' /></td>
									<td align="center">${results.orderNo}</td>
									<td align="center">${results.tripNo}</td>
									<td align="center">${results.shippingDate}</td>
									<td align="left">${results.customerName}</td>
									<%if(null == request.getParameter("page")){ %>
									   <td align="center">ยืนยันรับสินค้าเรียบร้อย</td>
									<%}else{ %>
									 <td align="center"><font color="red">coยกเลิกรับสินค้าเรียบร้อย </font> </td>
									 <%} %>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<c:if test="${shipmentForm.summary.shipDateSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left">จำนวนรายการ เลื่อนวันส่ง   ${shipmentForm.summary.shipDateSize} รายการ</th>
							</tr>
							<tr>
								<th width="5%"><bean:message key="No" bundle="sysprop"/></th>
								<th width="15%">เลขที่ใบขาย</th>
								<th width="5%">ครั้งที่ส่ง</th>
								<th width="5%">วันที่ส่งของ</th>
								<th width="40%">ลูกค้า</th>
								<th width="35%">Message </th>
							</tr>
							<c:forEach var="results" items="${shipmentForm.summary.shipDateList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td><c:out value='${rows.index+1}' /></td>
									<td align="center">${results.orderNo}</td>
									<td align="center">${results.tripNo}</td>
									<td align="center">${results.shippingDate}</td>
									<td align="left">${results.customerName}</td>
									<td align="center">Success</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<c:if test="${shipmentForm.summary.rescheduleShipDateSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="6"  align="left">จำนวนรายการปรับแผน  ${shipmentForm.summary.rescheduleShipDateSize} รายการ</th>
							</tr>
							<tr>
								<th width="5%"><bean:message key="No" bundle="sysprop"/></th>
								<th width="15%">เลขที่ใบขาย</th>
								<th width="5%">ครั้งที่ส่ง</th>
								<th width="5%">วันที่ส่งของ</th>
								<th width="40%">ลูกค้า</th>
								<th width="35%">Message </th>
							</tr>
							<c:forEach var="results" items="${shipmentForm.summary.rescheduleShipDateList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${tabclass}'/>">
									<td><c:out value='${rows.index+1}' /></td>
									<td align="center">${results.orderNo}</td>
									<td align="center">${results.tripNo}</td>
									<td align="center">${results.shippingDate}</td>
									<td align="left">${results.customerName}</td>
									<td align="center">Success</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<c:if test="${shipmentForm.summary.failSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" style="border-top:10px;">
							<tr>
								<th colspan="6" align="left">จำนวนรายการที่ไม่สำเร็จ  ${shipmentForm.summary.failSize} รายการ</th>
							</tr>
							<tr>
								<th width="5%"><bean:message key="No" bundle="sysprop"/></th>
								<th width="15%">เลขที่ใบขาย</th>
								<th width="5%">ครั้งที่ส่ง</th>
								<th width="5%">วันที่ส่งของ</th>
								<th width="40%" align="left">ลูกค้า</th>
								<th width="35%">Message </th>
							</tr>
							<% int idx = 0 ; %>
							<c:forEach var="results2" items="${shipmentForm.summary.failOrderList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<% 
								String errorMsg = shipmentForm.getSummary().getErrorMessage(idx);
								idx++;
								%>
								<tr class="<c:out value='${tabclass}'/>">
									<td><c:out value='${rows.index+1}' /></td>
									<td align="center">${results2.orderNo}</td>
									<td align="center">${results2.tripNo}</td>
									<td align="center">${results2.shippingDate}</td>
									<td align="left">${results2.customerName}</td>
									<td align="center" style="color:red;"><%=errorMsg%></td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right">
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								</a></td>
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