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
<%@page import="com.isecinc.pens.bean.OrderLine"%>


<%

User user = (User) request.getSession().getAttribute("user");
String role = user.getType();
List<References> docstatus= InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS);
pageContext.setAttribute("docstatus",docstatus,PageContext.PAGE_SCOPE);

String isAdd = request.getSession(true).getAttribute("isAdd") != null ? (String)request.getSession(true).getAttribute("isAdd") : "Y";


%>

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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/shipconfirm.js"></script>
<script type="text/javascript">
	function loadMe() {
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateFrom'));
		new Epoch('epoch_popup', 'th', document.getElementById('orderDateTo'));
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
						
					
						<html:form action="/jsp/shipmentResultAction">
						<jsp:include page="../error.jsp" />
					
												
						<c:if test="${shipmentForm.results != null}">
																		
							<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
							<bean:message key="RecordsFound" bundle="sysprop" /> &nbsp;
							<span class="searchResult">${shipmentForm.shipmentCriteria.searchResult}</span>&nbsp;<bean:message key="Records" bundle="sysprop" /></div>
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
								
								<tr>
								<td align="center" width="88px;">
								 <input type="checkbox" name="selectAll"  /> Select All
								 </td>
								</tr>
								
								<tr>
									<th class="order"><bean:message key="No" bundle="sysprop"/></th>
									<th class="order">รหัสสมาชิก</th>
									<th class="order">ชื่อสมาชิก</th>
									<th class="order">ชื่อสินค้า</th>
									<th><bean:message key="Member.Time" bundle="sysele"/></th>
									<th class="order">เลขที่เอกสาร</th>
									<th class="order">ชุดจัดส่ง</th>
									<th class="order">จำนวนขวดที่แผน</th>
									<th class="order">จำนวนขวดที่จัดส่งได้</th>
									<th class="order">ส่งของ</th>
									<th class="order">วันที่ส่งของ</th>
									<th class="order">หมายเหตุ</th>
								</tr>
								
								
							<c:forEach var="results" items="${shipmentForm.results}" varStatus="rows">

							
								<input type="hidden" name='lines.id' value='${results.id}'>
								<tr>
									<td width="36px;"><c:out value='${rows.index+1}' /></td>
									<td align="center" width="88px;">${results.customerCode}</td>
									<td align="center" width="88px;">${results.customerName}</td>
									<td align="center" width="88px;">${results.product.name}</td>
									<td align="center" width="88px;">${results.tripNo}</td>
									<td align="center" width="88px;">${results.orderNo}</td>
									<td align="center" width="88px;">${results.deliveryGroup}</td>
									<td align="center" width="88px;">${results.qty}</td>
									<td align="center" width="88px;">
									${results.id}
									<input type="text"  name='qty' size="5" value="1"/>
									</td>
									<td align="center" width="88px;">
									<input type="checkbox" name="shipment"  />
									</td>
									<td align="center" width="88px;">
									${results.requestDate}
									
									</td>
									
									<td align="center" width="88px;">
									<input type="text"  size="5" name="comment"  />
									</td>
								</tr>
							</c:forEach>
								<tr>
									<td align="left" class="footer" colspan="13">&nbsp;</td>
								</tr>
							</table>
						</c:if>					

					
					<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									
									
									<a href="#" onclick="save('${pageContext.request.contextPath}');">
									
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
									
									<a href="#" onclick="backsearch('${pageContext.request.contextPath}','${orderForm.order.customerId}');">
									
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
					
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right"><a href="#" onclick="backToCusotmer('${pageContext.request.contextPath}','${shipmentForm.order.customerId}');">
								<!--<img src="${pageContext.request.contextPath}/images/b_close.gif" border="1" class="newPicBtn">-->
								<input type="button" value="ปิดหน้าจอ" class="newNegBtn">
								</a></td>
								<td width="10%">&nbsp;</td>
							</tr>
						</table>
						
					<div id="orderLineList" style="display: none;"></div>
						<jsp:include page="../searchCriteria.jsp"></jsp:include>
			</html:form> 
					<!-- BODY 		/html:form>-->
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