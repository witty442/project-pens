<%@page import="com.isecinc.pens.inf.helper.Utils"%>
<%@page import="com.isecinc.pens.bean.PD"%>
<%@page import="com.isecinc.pens.model.MPD"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<jsp:useBean id="billPlanForm" class="com.isecinc.pens.web.billplan.BillPlanForm" scope="request" />
<%
User user = ((User)session.getAttribute("user"));
String role = user.getType();
String userName = user.getUserName();

%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Pragma" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link type="text/css" href="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.7.3.custom.css" rel="stylesheet" />
<style type="text/css">
<!--
body {
	background-image: url(${pageContext.request.contextPath}/images2/bggrid.jpg);
	/**background-repeat: repeat;**/
}
.style1 {color: #004a80}

-->
 
.currPage{ border:1px solid #000000; padding-left:4px;padding-right:4px;padding-top:2px; }
.pageLink{padding-left:4px;padding-right:4px;padding-top:2px; }
.paging{height:18px;width:100%;}
.catalog{text-align:center;/*background-color:#FFCC99;*/}
.brandName{width:120px;vertical-align:top;} 

table#productList thead{background:#FFE4CA;}
.qtyInput{width:35px; text-align:right;}
table#productList tbody td{vertical-align:top;padding-left:2px;padding-right:4px;}
table#productList tbody td.number{text-align:right;}


</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tablesorter.css" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/billPlan.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/DateUtils.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui-1.7.3.custom.min.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup','th',document.getElementById('billPlanRequestDate'));
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
				<jsp:param name="function" value="BillPlan"/>
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
						<html:form action="/jsp/billPlanAction">
						<jsp:include page="../error.jsp"/>
		
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							
							<tr>
							<td colspan="4" align="center">
								    <table align="center" border="0" cellpadding="3" cellspacing="0" width="80%">
								  
									<tr>
										<td align="right"  width="10%">
											   เลขที่เอกสาร
										</td>
										<td align="left"  width="15%">
										    <html:text property="billPlan.billPlanNo" maxlength="10" size="15" readonly="true" styleId="billPlanNo" styleClass="disableText" />
										</td>
										<td align="right"  width="23%" >
										   	วันที่ปัจจุบัน
										</td>
										<td align="left"  width="27%" >
									        <html:text property="billPlan.currentDate" maxlength="10" size="15" readonly="true" styleId="currentDate" styleClass="disableText"/>
										</td>	
									</tr>
									<tr>
										<td align="right"  width="10%">
											   วันที่เอกสารบิล T
										</td>
										<td align="left"  width="15%">
										   <html:text property="billPlan.billPlanDate" maxlength="10" size="15" readonly="true" styleId="billPlanDate" styleClass="disableText"/>
										</td>
										<td align="right"  width="23%" >
										       วันที่รับสินค้า <font color="red">*</font> 
										</td>
										<td align="left"  width="27%" >
									       <html:text property="billPlan.billPlanRequestDate" maxlength="10" size="15" readonly="true" styleId="billPlanRequestDate" />
										</td>	
									</tr>
									<tr>
										<td align="right"  width="15%">เข้า PD</td>
										<td align="left"  width="10%"> 
										   <html:text property="billPlan.pdDesc" size="50" readonly="true"  styleId="pdDesc" styleClass="disableText"/>
										</td>
										<td align="right"  width="22%" >พนักงานขาย </td>
										<td align="left"  width="28%" >
										   <html:text property="billPlan.salesDesc" size="30" readonly="true"  styleId="salesDesc" styleClass="disableText"/>
										</td>		
									</tr>
									<tr>
										<td align="right"  width="15%">สถานะการรับสินค้า</td>
										<td align="left"  width="10%"> 
										   <html:text property="billPlan.statusLabel" size="18" readonly="true"  styleId="statusLabel" styleClass="disableText"/>
										</td>
										<td align="right"  width="22%" >สถานะการส่งข้อมูล </td>
										<td align="left"  width="28%" >
										   <html:text property="billPlan.exportedLabel" size="30" readonly="true"  styleId="exportedLabel" styleClass="disableText"/>
										</td>		
									</tr>
									</table>
									
							     </td>
							  </tr>
							  
							<tr>
								<td colspan="4" align="center">	
								<!--  Results  -->
								<table id="tblProduct" align="center" border="0" cellpadding="3" cellspacing="1" class="moveOrder">
									<tr>
										<th class="moveOrder.no">No.</th>
										<th class="moveOrder.no">Line No</th>
										<th class="moveOrder.productName">รหัสสินค้า</th>
										<th class="moveOrder.uom">ชื่อสินค้า</th>
										<th class="moveOrder.qty">จำนวน</th>
										<th class="moveOrder.uom">หน่วยนับ</th>
									</tr>
									<c:forEach var="lines1" items="${billPlanForm.lines}" varStatus="rows1">
									<c:choose>
										<c:when test="${rows1.index %2 == 0}">
											<c:set var="tabclass" value="lineO"/>
										</c:when>
										<c:otherwise>
											<c:set var="tabclass" value="lineE"/>
										</c:otherwise>
									</c:choose>
									<tr class="${tabclass}">
										<td align="center" class = "moveOrder.no">${rows1.index + 1}</td>	
										<td align="center" class ="moveOrder.uom">
										   ${lines1.lineNo}
										</td>			
										<td align="left" class ="moveOrder.uom">
										   ${lines1.productCode}
										</td>
										<td align="left" class ="moveOrder.productName">
										   ${lines1.productName}
										</td>
										<td align="right" class="moveOrder.qty">
										    ${lines1.fullQTY}	
										</td>
										<td align="right" class="moveOrder.uom">
										    ${lines1.fullUOM}	
										</td>
									</tr>
								   </c:forEach>
								  
								</table>
								<!--  Results -->		
								
								</td>
							</tr>
				
						</table>
						<br>
						<!-- BUTTON -->
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
								   <c:if test="${billPlanForm.billPlan.canEdit =='true'}">
									<a href="#" onclick="return confirmBillPlan('${pageContext.request.contextPath}');">
									  <input type="button" value="ยืนยันการรับสินค้าเข้า PD" class="newPosBtnLong">
									</a>	
								   </c:if>		
								   
								    <c:if test="${billPlanForm.billPlan.canCancel =='true'}">
									<a href="#" onclick="return cancelBillPlan('${pageContext.request.contextPath}');">
									  <input type="button" value="ยกเลิกการรับสินค้าเข้า PD" class="newCancelBtnLong">
									</a>	
								   </c:if>										
								
								    <a href="#" onclick="backsearch('${pageContext.request.contextPath}','${moveOrderForm.moveOrder.moveOrderType}');">
										<input type="button" value="ปิดหน้าจอ"  class="newPosBtnLong">
									</a>
								</td>
							</tr>
						</table>
						
						<!-- Hidden Field -->
						
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
<Script>
	<%if("true".equals(Utils.isNull(request.getAttribute("popupPrint")))){ %>
	   popupPrint('${pageContext.request.contextPath}','${moveOrderForm.moveOrder.moveOrderType}','${moveOrderForm.moveOrder.requestNumber}');
	<%} %>
</Script>
<div id="brand-dialog">No Product Catalog To Display!</div>
<div id="selectProduct">No Product To Display!</div>