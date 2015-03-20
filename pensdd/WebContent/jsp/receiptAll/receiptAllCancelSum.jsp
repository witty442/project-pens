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
<jsp:useBean id="manageOrderReceiptForm" class="com.isecinc.pens.web.admin.ManageOrderReceiptForm" scope="request" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/receiptAll.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>

<script>
function clearForm(){
	window.location = '${pageContext.request.contextPath}/jsp/manageOrderReceipt.do';
}

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
				<jsp:param name="function" value="ManageReceipt" />
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
						<html:form action="/jsp/receiptAllAction">
						<jsp:include page="../error.jsp" />
						<c:if test="${manageOrderReceiptForm.summary.receiptSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
							<tr>
								<th colspan="5" class="confirm">จำนวนรายการที่ยกเลิกทั้งหมด  ${manageOrderReceiptForm.summary.receiptSize} รายการ</th>
							</tr>
							<tr>
								<th><bean:message key="No" bundle="sysprop"/></th>
								<th>เลขที่ใบเสร็จ</th>
								<th>ลูกค้า</th>
								<th>รับชำระ</th>
								<th>Message</th>
							</tr>
							<c:forEach var="results" items="${manageOrderReceiptForm.summary.successReceiptList}" varStatus="rows">
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
									<td align="center">${results.receiptNo}</td>
									<td align="center">${results.customerName}</td>
									<td align="center">${results.receiptAmount}</td>
									<td align="center">ยกเลิกเรียบร้อย</td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						
						<c:if test="${manageOrderReceiptForm.summary.failCancelReceiptSize > 0}">
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result" style="border-top:10px;">
							<tr>
								<th colspan="5" class="confirm">จำนวนรายการที่ไม่สามารถยกเลิกได้ทั้งหมด  ${receiptAllForm.summary.failCancelReceiptSize} รายการ</th>
							</tr>
							<tr>
								<th><bean:message key="No" bundle="sysprop"/></th>
								<th>เลขที่ใบเสร็จ</th>
								<th>ลูกค้า</th>
								<th>รับชำระ</th>
								<th>Message</th>
							</tr>
							<% int idx = 0 ; %>
							<c:forEach var="results2" items="${manageOrderReceiptForm.summary.failCancelReceiptList}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<% 
								String errorMsg = manageOrderReceiptForm.getSummary().getErrorMessage(idx);
								idx++;
								%>
								<tr class="<c:out value='${tabclass}'/>">
									<td><c:out value='${rows.index+1}' /></td>
									<td align="center">${results2.receiptNo}</td>
									<td align="center">${results2.customerName}</td>
									<td align="center" style="color:red;"><%=errorMsg%></td>
								</tr>
							</c:forEach>
							</table>
						</c:if>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right"><a href="#" onclick="clearForm();">
								<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
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