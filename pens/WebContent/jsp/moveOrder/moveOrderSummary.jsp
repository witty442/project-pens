<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>
<%-- <%@taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%> --%>

<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="java.util.Locale"%>
<%@page import="com.isecinc.pens.SystemProperties"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="com.isecinc.core.bean.References"%>
<%@page import="com.isecinc.pens.init.InitialReferences"%>
<jsp:useBean id="moveOrderSummaryForm" class="com.isecinc.pens.web.moveordersummary.MoveOrderSummaryForm" scope="session" />
<%

List<References> types= new ArrayList<References>();
types.add(new References("DETAIL","   รายละเอียด   "));
types.add(new References("TOTAL","    ผลรวม     "));
pageContext.setAttribute("types",types,PageContext.PAGE_SCOPE);

List<References> statusTypes= new ArrayList<References>();
statusTypes.add(new References("","----ไม่เลือก----"));
statusTypes.add(new References("SV","ใช้งาน"));
statusTypes.add(new References("VO","ยกเลิก"));
pageContext.setAttribute("statusTypes",statusTypes,PageContext.PAGE_SCOPE);

List<References> moveOrderTypes= new ArrayList<References>();
moveOrderTypes.add(new References("MoveOrderRequisition","ใบเบิกสินค้า"));
moveOrderTypes.add(new References("MoveOrderReturn","ใบคืนสินค้า"));
pageContext.setAttribute("moveOrderTypes",moveOrderTypes,PageContext.PAGE_SCOPE);

List<References> exportedTypes= new ArrayList<References>();
exportedTypes.add(new References("","----ไม่เลือก----"));
exportedTypes.add(new References("Y","ส่งข้อมูลแล้ว"));
exportedTypes.add(new References("N","ยังไม่ส่งข้อมูล"));
pageContext.setAttribute("exportedTypes",exportedTypes,PageContext.PAGE_SCOPE);

%>
<%@page import="com.isecinc.pens.bean.Province"%>
<%@page import="com.isecinc.pens.model.MProvince"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=TIS-620;">
<title><bean:message bundle="sysprop" key="<%=SystemProperties.PROJECT_NAME %>"/></title>
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/webstyle.css" type="text/css" />
<link rel="StyleSheet" href="${pageContext.request.contextPath}/css/displaytag.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/epoch_styles.css" type="text/css"/>


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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/moveOrderSummary.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript">

function loadMe(){
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateFrom'));
	new Epoch('epoch_popup', 'th', document.getElementById('requestDateTo'));
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
				<jsp:param name="function" value="Summary"/>
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
						<html:form action="/jsp/moveOrderSummaryAction">
						<jsp:include page="../error.jsp"/>
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="40%">
						 <tr>
							<td align="left">ประเภทเอกสาร&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							    <html:select property="summary.moveOrderType">
								   <html:options collection="moveOrderTypes" property="key" labelProperty="name"/>
								</html:select></td>
						   </tr>
						<tr>
							<td align="left">
							  จากวันที่ทำรายการ&nbsp;&nbsp;<html:text property="summary.requestDateFrom" styleId="requestDateFrom" readonly="true" size="10"/>
							  ถึง วันที่รายการ&nbsp;&nbsp;<html:text property="summary.requestDateTo" styleId="requestDateTo" readonly="true" size="10"/>
							</td>
						</tr>
						<tr>
							<td align="left">
							   จาก รหัสสินค้า&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="summary.productCodeFrom" size="10"/>
							   ถึง รหัสสินค้า&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="summary.productCodeTo" size="10"/>
							</td>
						</tr>
						 <tr>
							<td align="left">สถานะเอกสาร&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							      <html:select property="summary.status">
									<html:options collection="statusTypes" property="key" labelProperty="name"/>
								  </html:select>
								  &nbsp;&nbsp;
								  สถานะการส่งข้อมูล&nbsp;&nbsp;
							     <html:select property="summary.exported">
									<html:options collection="exportedTypes" property="key" labelProperty="name"/>
								  </html:select>
							</td>
						</tr>
						 <tr>
							<td align="left">การแสดงผล&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							     <html:select property="summary.type">
									<html:options collection="types" property="key" labelProperty="name"/>
								  </html:select>
							</td>
						</tr>
					   </table>
					   
					<br>
					<!-- BUTTON -->
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
						<tr>
							<td align="center">
								<a href="javascript:search('${pageContext.request.contextPath}')">
								  <input type="button" value="ค้นหา" class="newPosBtn"> 
								</a>
								<a href="javascript:clearForm('${pageContext.request.contextPath}')">
								  <input type="button" value="Clear" class="newPosBtn">
								</a>
								<a href="javascript:exportExcel('${pageContext.request.contextPath}')">
								  <input type="button" value="Export" class="newPosBtn">
								</a>
							</td>
						</tr>
					</table>
					<!-- RESULT -->
				    
			        <c:if test="${moveOrderSummaryForm.results != null}">
			           <c:if test="${moveOrderSummaryForm.summary.type =='DETAIL'}">
							<display:table id="item" name="sessionScope.moveOrderSummaryForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="../jsp/moveOrderSummaryAction.do?do=search" sort="list" pagesize="30">	
							    <display:column  title="เลขที่เอกสาร" property="requestNumber"  sortable="false" class="ms_requestNumber"/>
							    <display:column  title="วันที่ทำรายการ" property="requestDate"  sortable="false" class="ms_requestDate"/>
							    
							    <c:if test="${moveOrderSummaryForm.summary.moveOrderType =='MoveOrderRequisition'}">
							        <display:column  title="เบิกจาก PD" property="pdCode"  sortable="false" class="ms_pdCode"/>
							    </c:if>
							    <c:if test="${moveOrderSummaryForm.summary.moveOrderType =='MoveOrderReturn'}">
							        <display:column  title="คืนสินค้าเข้า  PD" property="pdCode"  sortable="false" class="ms_pdCode"/>
							    </c:if>
							    
							    <display:column  title="รหัสสินค้า" property="productCode"  sortable="false" class="ms_productCode"/>	
							    <display:column  title="หน่วยนับ" property="uom1"  sortable="false" class="ms_uom1"/>
							    <display:column  title="จำนวนเต็ม" property="qty1"  sortable="false" class="ms_qty1"/>
							    <display:column  title="หน่วยนับเศษ" property="uom2"  sortable="false" class="ms_uom1"/>
							    <display:column  title="จำนวนเศษ" property="qty2"  sortable="false" class="ms_qty2"/>
							    <display:column  title="จำนวนเงิน" property="totalAmount"  sortable="false" class="ms_totalAmount"/>
							    <display:column  title="สถานะเอกสาร" property="statusLabel"  sortable="false" class="ms_statusLabel"/>
							    <display:column  title="สถานะการส่งข้อมูล" property="exportedLabel"  sortable="false" class="ms_exportedLabel"/>			
							</display:table>
						</c:if>
						<c:if test="${moveOrderSummaryForm.summary.type =='TOTAL'}">
							<display:table id="item" name="sessionScope.moveOrderSummaryForm.results" defaultsort="0" defaultorder="descending" class="resultDisp"
							    requestURI="../jsp/moveOrderSummaryAction.do?do=search" sort="list" pagesize="30">	
							  
							    <c:if test="${moveOrderSummaryForm.summary.moveOrderType =='MoveOrderRequisition'}">
							        <display:column  title="เบิกจาก PD" property="pdCode"  sortable="false" class="ms_pdCode"/>
							    </c:if>
							    <c:if test="${moveOrderSummaryForm.summary.moveOrderType =='MoveOrderReturn'}">
							        <display:column  title="คืนสินค้าเข้า  PD" property="pdCode"  sortable="false" class="ms_pdCode"/>
							    </c:if>
							    
							    <display:column  title="รหัสสินค้า" property="productCode"  sortable="false" class="ms_productCode"/>	
							    <display:column  title="หน่วยนับ" property="uom1"  sortable="false" class="ms_uom1"/>
							    <display:column  title="จำนวนเต็ม" property="qty1"  sortable="false" class="ms_qty1"/>
							    <display:column  title="หน่วยนับเศษ" property="uom2"  sortable="false" class="ms_uom1"/>
							    <display:column  title="จำนวนเศษ" property="qty2"  sortable="false" class="ms_qty2"/>
							    <display:column  title="จำนวนเงิน" property="totalAmount"  sortable="false" class="ms_totalAmount"/>
							    <display:column  title="สถานะเอกสาร" property="statusLabel"  sortable="false" class="ms_statusLabel"/>
							    <display:column  title="สถานะการส่งข้อมูล" property="exportedLabel"  sortable="false" class="ms_exportedLabel"/>			
							</display:table>
						</c:if>
                    </c:if>
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