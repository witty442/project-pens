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
<jsp:useBean id="shipmentForm" class="com.isecinc.pens.web.shipment.ShipmentForm" scope="request" />
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

table.result th.confirm{
width:100%;
text-align: right;
padding-left: 10px;
padding-bottom: 3px;
padding-right: 42px;
padding-top: 3px;
color: blue;
font-weight:bold;
font-size: medium ;

}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/webstyle.js"></script>
<!-- Calendar -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/epoch_styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/epoch_classes.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/strfunc.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/input.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/javascript.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/shipconfirm.js"></script>
<script type="text/javascript">
	function loadMe() {
		new Epoch('epoch_popup', 'th', document.getElementById('shipDate'));
		<c:if test="${shipmentForm.results != null}">
		new Epoch('epoch_popup', 'th', document.getElementById('confirmDate'));
		for(var i = 0 ; i < ${shipmentForm.shipmentCriteria.searchResult} ; i++){
			new Epoch('epoch_popup', 'th', document.getElementById('postponeDate'+i));
			$("span#shipDateInput"+i).hide();
		}
		</c:if>
	}

	function viewComment(idx){
		window.open('${pageContext.request.contextPath}/jsp/shipment/commentPopup.jsp?idx='+idx, 'Comment', 'width=300,height=200,location=No,resizable=No');
	}

	function setComment(idx,comment){
		document.getElementsByName("confirms["+idx+"].comment")[0].value = comment;
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
				<jsp:param name="function" value="ShipConfirm" />
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
						<html:form action="/jsp/shipmentAction">
						<jsp:include page="../error.jsp" />
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="50%">
							<tr>
								<td align="center"> วันที่รายการส่งของ&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="shipment.shipDate" maxlength="10" size="10" readonly="true" styleId="shipDate" tabindex="1" onkeypress="nextKeypress(event)"/>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								รหัสสมาชิก&nbsp;&nbsp;&nbsp;&nbsp;<html:text property="shipment.memberCode" size="12" styleId="memberCode" tabindex="2" onkeypress="searchKeypress(event,'${pageContext.request.contextPath}')"/></td>
							</tr>													
						</table>
						<br>
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body" width="50%">
							<tr>
								<td align="center">
									<a href="javascript:search('${pageContext.request.contextPath}')">
									<input type="button" value="ค้นหา" class="newPosBtn">
									</a> 
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<input type="button" value="Clear" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>					
						<c:if test="${shipmentForm.results != null}">																
							<div align="left" class="recordfound">&nbsp;&nbsp;&nbsp;
							<bean:message key="RecordsFound" bundle="sysprop" /> &nbsp;
							<span class="searchResult">${shipmentForm.totalGroup}</span>&nbsp;<bean:message key="Records" bundle="sysprop" /></div>
							<html:hidden property="totalGroup"/>
							
							<input id="totalRows" type="hidden" value="${shipmentForm.shipmentCriteria.searchResult}">
							<br>
							<table align="center" border="0" cellpadding="3" cellspacing="1" class="result">
								<tr>
									<th class="confirm" colspan="16" align="right">วันที่ยืนยันการส่งสินค้า&nbsp;&nbsp;:&nbsp;&nbsp;<html:text property="confirmDate" maxlength="10" size="12" readonly="true" styleId="confirmDate" />&nbsp;&nbsp;&nbsp;&nbsp;</th>
								</tr>
								<tr>
									<th><bean:message key="No" bundle="sysprop"/></th>
									<th>รหัสสมาชิก</th>
									<th>ชื่อสมาชิก</th>
									<th>ชื่อสินค้า</th>
									<th><bean:message key="Member.Time" bundle="sysele"/></th>
									<th>เลขที่เอกสาร</th>
									<th>ชุดจัดส่ง</th>
									<th>จำนวนขวดที่แผน</th>
									<th>จำนวนขวดที่จัดส่งได้</th>
									<th>ส่งของ</th>
									<th>เลื่อนวันส่ง</th>
									<th>เลื่อนแผนส่ง</th>
									<th>ปรับแผนทั้งหมด</th>
									<th>วันที่ส่งของ</th>
									<th>รับชำระ</th>
									<th>หมายเหตุ</th>
								</tr>
								<c:set var="orageQty" value="0" />
								<c:set var="mixedQty" value="0" />
								<c:set var="stawQty" value="0" />
								<c:set var="totalAmount" value="0" />
							<c:forEach var="results" items="${shipmentForm.results}" varStatus="rows">
								<c:choose>
									<c:when test="${rows.index %2 == 0}">
										<c:set var="tabclass" value="lineO" />
									</c:when>
									<c:otherwise>
										<c:set var="tabclass" value="lineE" />
									</c:otherwise>
								</c:choose>
								<tr class="<c:out value='${results.classLine}'/>">
									<td><c:out value='${results.no}' /></td>
									<td align="center">${results.memberCode}</td>
									<td align="left">&nbsp;&nbsp;${results.memberName}</td>
									<td align="left">&nbsp;&nbsp;${results.orderLine.product.name}</td>
									<td align="center">${results.orderLine.tripNo}</td>
									<td align="center">${results.orderNo}</td>
									<td align="center">${results.deliveryGroup}</td>
									<td align="center">${results.orderLine.qty}</td>
									<td align="center">    
									<input name="confirms[${rows.index}].confirmQty"  onkeydown="return inputNum(event);" style="text-align: right; padding-right: 2px;" type="text" size="5" value="${results.confirmQty}" onblur="inputConfirmQty(this,'confirm','${rows.index}')" />
									<input name="orderLineQty" type="hidden" value="${results.orderLine.qty}" />
									<input name="productCode" type="hidden" value="${results.orderLine.product.code}" />
									</td>
									<td align="center">
									<input name="confirms[${rows.index}].memberName" type="hidden" value="${results.memberName}"/>
									<input name="confirms[${rows.index}].tripNo" type="hidden" value="${results.orderLine.tripNo}"/>
									<input name="confirms[${rows.index}].isConfirm" type="checkbox" checked value="Y" onchange="checkIsConfirm(${rows.index},this,'ISCONFIRM')" />
									<input name="confirms[${rows.index}].lineId" type="hidden" value="${results.orderLine.id}"/>
									<input name="confirms[${rows.index}].comment" type="hidden" value="${results.orderLine.shipComment}"/>
									<input name="confirms[${rows.index}].orderNo" type="hidden" value="${results.orderNo}"/>
									<input name="confirms[${rows.index}].orderLine.paymentMethod" type="hidden" value="${results.orderLine.paymentMethod}"/>
									</td>
									<td>
									<input name="confirms[${rows.index}].isPostponeShipDate" type="checkbox" value="Y" onchange="checkIsConfirm(${rows.index},this,'ISPOSTPONE')"/>
									</td>
									<td>
									<input name="confirms[${rows.index}].isPostponeReqDate" type="checkbox" value="Y" onchange="checkIsConfirm(${rows.index},this,'ISPOSTPONEREQDATE')"/>
									</td>
									<td>
									<input name="confirms[${rows.index}].isReSchedule" type="checkbox" value="Y" onchange="checkIsConfirm(${rows.index},this,'ISRESCHEDULE')"/>
									</td>
									<td align="center">
									<span id="shipDateTxt${rows.index}">${results.orderLine.requestDate}</span>
									<span id="shipDateInput${rows.index}">
									<input name="confirms[${rows.index}].postponeDate" id="postponeDate${rows.index}"  maxlength="10" size="10" readonly type="text" value="${results.orderLine.requestDate}"/>
									</span>
									</td>
									<td align="center">
										<c:if test="${results.orderLine.paymentMethod =='CS'}">
											<input name="confirms[${rows.index}].orderLine.needBill" style="text-align: right;" onblur="validateNeedBill(this,'${results.orderLine.needBill}')"  onkeydown="return inputNum(event);" maxlength="22" size="8" value="<fmt:formatNumber type="number" pattern="########0.00" value="${results.orderLine.needBill}" />" />
											<c:set var="totalAmount" value="${totalAmount + results.orderLine.needBill}" scope="page"/>
											<c:set var="needToCalculate" value="Y" scope="page"/>
										</c:if>
										<c:if test="${results.orderLine.paymentMethod =='CR'}">
											<input name="confirms[${rows.index}].orderLine.needBill" style="text-align: right;" onblur="validateNeedBill(this,'${results.orderLine.needBill}')"  onkeydown="return inputNum(event);" maxlength="22" size="8" value="<fmt:formatNumber type="number" pattern="########0.00" value="${results.orderLine.needBill}" />" />
											<c:set var="totalAmount" value="${totalAmount + results.orderLine.needBill}" scope="page"/>
											<c:set var="needToCalculate" value="Y" scope="page"/>
										</c:if>
										<c:if test="${results.orderLine.paymentMethod =='CH'}">
											<input name="confirms[${rows.index}].orderLine.needBill" type="hidden"  maxlength="22" size="8" value="<fmt:formatNumber pattern="########0.00" value="${results.orderLine.needBill}" />" />
											<c:set var="needToCalculate" value="N" scope="page"/>
										</c:if>
										<input name="confirms[${rows.index}].isNeedToCalculate" type="hidden" value="${needToCalculate}" />
									</td>
									<td align="center">
										<a href="#" onclick="viewComment(${rows.index})">
										<img src="${pageContext.request.contextPath}/icons/lookup.gif" align="absmiddle"/>
										</a>
									</td>
								</tr>
								<c:if test="${results.orderLine.product.code =='302009'}">
									<c:set var="orageQty" value="${orageQty + results.confirmQty}" scope="page" />
								</c:if>
								<c:if test="${results.orderLine.product.code =='302010'}">
									<c:set var="stawQty" value="${stawQty + results.confirmQty}" scope="page" />
								</c:if>
								<c:if test="${results.orderLine.product.code =='302011'}">
									<c:set var="mixedQty" value="${mixedQty + results.confirmQty}" scope="page" />
								</c:if>
							</c:forEach>
								<tr>
									<td align="right" class="footer" colspan="8">
										<table width="100%">
											<tr>
											<td style="text-align: right ; padding-right: 10px;">ฮาร์ทติเบเนคอลรสส้มรุ่นขวดเดียว</td>
											</tr>
											<tr>
											<td style="text-align: right ; padding-right: 10px;">ฮาร์ทติเบเนคอลรสสตรอเบอร์รี่รุ่นขวดเดียว</td>
											</tr>
											<tr>
											<td style="text-align: right ; padding-right: 10px;">ฮาร์ทติเบเนคอลรสมิกซ์เบอร์รี่รุ่นขวดเดียว</td>
											</tr>
											<tr>
											<td style="text-align: right ; padding-right: 10px;">รวมยืนยันนการจัดส่งสินค้า</td>
											</tr>
										</table>
									</td>
									<td align="left" class="footer" colspan="1">
									<table width="100%">
											<tr>
											<td style="text-align: center ; padding-right: 10px;">
												<input id="orageTotalQty" class="disableText" readonly type="text" style="text-align: right; padding-right: 2px;" value="${orageQty}" size="5" />
											</td>
											</tr>
											<tr>
											<td style="text-align: center ; padding-right: 10px;">
												<input id="stawberryTotalQty" class="disableText" type="text" style="text-align: right; padding-right: 2px;" readonly value="${stawQty}" size="5" />
											</td>
											</tr>
											<tr>
											<td style="text-align: center ; padding-right: 10px;">
												<input id="mixberryTotalQty" class="disableText" type="text" style="text-align: right; padding-right: 2px;" readonly value="${mixedQty}" size="5" />
											</td>
											</tr>
											<tr>
											<td style="text-align: center ; padding-right: 10px;">
												<input id="totalQty" class="disableText" type="text" style="text-align: right; padding-right: 2px;" readonly value="${mixedQty + stawQty +orageQty}" size="5" />
											</td>
											</tr>
										</table>
									</td>
									<td align="left" class="footer" colspan="5">
									<table width="100%">
											<tr>
											<td style="text-align: right ; padding-right: 10px;">รวมรับชำระ</td>
											</tr>
											<tr>
											<td>&nbsp;</td>
											</tr>
											<tr>
											<td>&nbsp;</td>
											</tr>
											<tr>
											<td>&nbsp;</td>
											</tr>
										</table>
									</td>
									<td align="left" class="footer">
									<table width="100%">
											<tr>
												<td style="text-align: right ; padding-right: 10px;">
													<input id="totalAmount" class="disableText" type="text" style="text-align: right; padding-right: 2px;" readonly 
													value="<fmt:formatNumber type="number" pattern="########0.00" value="${totalAmount}" />"  maxlength="22" size="10" />
												</td>
											</tr>
											<tr>
											<td>&nbsp;</td>
											</tr>
											<tr>
											<td>&nbsp;</td>
											</tr>
											<tr>
											<td>&nbsp;</td>
											</tr>
										</table>
									</td>
									<td class="footer" colspan="1">&nbsp;</td>
								</tr>
							</table>
						</c:if>
						<br>
						<!-- BUTTON -->
						<c:if test="${shipmentForm.results != null}">
						<table align="center" border="0" cellpadding="3" cellspacing="0" class="body">
							<tr>
								<td align="center">
									<a name="summary" href="#summary" onclick="calculateSummary();">
									<input type="button" value="คำนวณผลรวม" class="newPosBtn">
									</a>
									<a name="save" href="#" onclick="save('${pageContext.request.contextPath}');">
									<input type="button" value="บันทึก" class="newPosBtn">
									</a>
									<a href="javascript:clearForm('${pageContext.request.contextPath}')">
									<input type="button" value="ยกเลิก" class="newNegBtn">
									</a>
								</td>
							</tr>
						</table>
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
						    <tr>
								<td align="center" width ="100%">
								     <div id="progressbar"  style="width:600px;visibility: hidden;">
								       <img alt="" src="${pageContext.request.contextPath}/images/progress_bar.gif"">
								   </div>
								 </td>
							</tr>
						</table>
					</c:if>
						
						<!-- Hidden -->
						
						
						
						<table align="center" border="0" cellpadding="3" cellspacing="0" width="100%">
							<tr>
								<td align="right"><a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp'">
								
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
<div id="commentBlock">
<input id="comment" type="hidden">
<input id="idx" type="hidden" >
</div>

</body>
</html>