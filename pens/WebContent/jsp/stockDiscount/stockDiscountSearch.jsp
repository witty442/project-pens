<%@page import="util.SessionGen"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.District"%>
<%@page import="com.isecinc.pens.model.MDistrict"%>
<%@page import="util.AppversionVerify"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="stockDiscountForm" class="com.isecinc.pens.web.stockdiscount.StockDiscountForm" scope="request" />
<%
String role = ((User)session.getAttribute("user")).getType();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/icons/favicon.ico">
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/table_style.css?v=<%=SessionGen.getInstance().getIdSession()%>" type="text/css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">
function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateTo'));
}
function clearForm(path) {
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=prepare"+"&action=new";//clearForm
	document.stockDiscountForm.submit();
}

function search(path){
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=search&action=new";
	document.stockDiscountForm.submit();
	return true;
}

function viewStockDiscount(path,requestNumber){
	var param ="&requestNumber="+requestNumber;
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=viewStockDiscount&action=view"+param;
	document.stockDiscountForm.submit();
	return true;
}

function createNewStockDiscount(path){
	var param = "";
	    //param +="&backAvgMonth="+document.getElementsByName('bean.backAvgMonth')[0].value;
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=createNewStock"+param;
	document.stockDiscountForm.submit();
	return true;
}

function gotoPage(path,page){
	document.stockDiscountForm.action = path + "/jsp/stockDiscountAction.do?do=search&rf=Y";
	document.getElementsByName('curPage')[0].value = page;
	document.stockDiscountForm.submit();
	return true;
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
				<jsp:param name="function" value="StockDiscount"/>
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
						<html:form action="/jsp/stockDiscountAction">
						<jsp:include page="../error.jsp"/>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="100%">
						 <tr>
							<td colspan="4" align="center">
							      <font color="black" size="5"> <b> ค้นหาข้อมูล ใบอนุมัติให้ส่วนลดร้านค้า</b> </font>
							 </td>
						 </tr>
						 <tr>
							<td align="right" width="40%">จาก วันที่รายการ&nbsp;&nbsp;</td>
							<td align="left" width="10%" ><html:text property="bean.requestDateFrom" styleId="requestDateFrom" readonly="true" styleClass="\" autoComplete=\"off"/></td>
							<td align="right" width="10%">ถึง วันที่รายการ&nbsp;&nbsp;</td>
							<td align="left" width="40%" ><html:text property="bean.requestDateTo" styleId="requestDateTo" readonly="true" styleClass="\" autoComplete=\"off"/></td>
						 </tr>
						 <tr>
						 </tr>
					</table>
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<input type="button" value="ค้นหา" class="newPosBtn" onclick="search('${pageContext.request.contextPath}')">
								<input type="button" value="เพิ่มรายการใหม่" class="newPosBtn" onclick="createNewStockDiscount('${pageContext.request.contextPath}')">
								<input type="button" value="Clear" class="newNegBtn" onclick="clearForm('${pageContext.request.contextPath}')">
							</td>
						</tr>
					</table>	
					<!-- RESULT -->
					<!-- Paging -->
					 <c:if test="${stockDiscountForm.results != null }">
						<span class="pagebanner">พบรายการ  ${stockDiscountForm.totalRow} รายการ  ,แสดงรายการทั้งหมด </span>
						 <span class="pagelinks">หน้า
							<c:forEach var="i" begin="1" end="${stockDiscountForm.totalPage}">
							     <c:if test="${stockDiscountForm.curPage != i}">
								    <a href="javascript:gotoPage('${pageContext.request.contextPath}','${i}')"><c:out value="${i}"/></a>&nbsp;,
								 </c:if> 
								 <c:if test="${stockDiscountForm.curPage == i}">
								    &nbsp;<b><c:out value="${i}"/>&nbsp;,</b>
								 </c:if> 
						    </c:forEach>
					    </span>
					    <table align="center" border="0" cellpadding="3" cellspacing="2" class="tableSearchNoWidth" width="100%">
							<tr >
								<th>No.</th>
								<th>Request Number</th>
								<th>วันที่ทำรายการ</th>
								<th>หมายเลขลูกค้า</th>
								<th>ชื่อ</th>
								<th>แก้ไข/ดู</th>
							</tr>	
							<c:forEach var="item" items="${stockDiscountForm.results}" varStatus="rows">
							<c:choose>
								<c:when test="${item.no %2 == 0}">
									<c:set var="tabclass" value="lineO"/>
								</c:when>
								<c:otherwise>
									<c:set var="tabclass" value="lineE"/>
								</c:otherwise>
							</c:choose>
							<tr class="<c:out value='${tabclass}'/>">
							    <td class="td_text_center" width="5%"><c:out value='${item.no}'/></td>
							    <td class="td_text_center" width="10%"><c:out value='${item.requestNumber}'/></td>
							    <td class="td_text_center" width="5%"><c:out value='${item.requestDate}'/></td>
								<td class="td_text_center" width="10%"><c:out value='${item.customerCode}'/></td>
								<td class="td_text" width="20%"><c:out value='${item.customerName}'/></td>
								<td class="td_text_center" width="10%">
								   <c:choose>
										<c:when test="${item.exported == 'Y'}">
											 <a href="#" onclick="javascript:viewStockDiscount('${pageContext.request.contextPath}','${item.requestNumber}');">
									           <img border=0 src="${pageContext.request.contextPath}/icons/lookup.gif">
								             </a>
										</c:when> 
										<c:otherwise>
											 <a href="#" onclick="javascript:viewStockDiscount('${pageContext.request.contextPath}','${item.requestNumber}');">
											  <img border=0 src="${pageContext.request.contextPath}/icons/process.gif">
										   </a>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
							</c:forEach>
						</table>
					</c:if>	
					
				<!-- Result -->	
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						<tr>
							<td align="right">
								<a href="#" onclick="window.location='./mainpage.jsp'">
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								<!-- <img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn"> --></a>
							</td>
							<td width="10%">&nbsp;</td>
						</tr>
					</table>
					<!-- Hidden Field -->
					<html:hidden property="curPage"/>
					<html:hidden property="totalPage"/>
					<html:hidden property="totalRow"/>
					
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
